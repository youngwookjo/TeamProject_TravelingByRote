package tbr.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tbr.exception.AsyncException;
import tbr.model.dao.MemberRepository;
import tbr.model.dao.PlaceRepository;
import tbr.model.dto.MemberDTO;
import tbr.model.dto.PlaceDTO;
import tbr.util.Util;

@Service
public class TBRService {
	@Autowired
	PlaceRepository placeRepo;
	@Autowired
	MemberRepository memberRepo;

	// * DB
	public long getIds() throws AsyncException {
		long start = System.currentTimeMillis(); // 실행 시간 측정
		System.out.println("// 크롤링 시작");
		try {
		// (1) Resource Id 추출
		HashMap<String, String> data = new HashMap<>(); // Jsoup param 조정을 위한 map
		data.put("langtype", "KOR");
		data.put("mode", "listOk");
		int w = 1; // 반복 기준이 되는 변수
		ArrayList<PlaceDTO> list = new ArrayList<>();
		Elements els = null;
		do {
			Util.sleep(0.1);
			data.put("pageNo", Integer.toString(w));
			try {
				els = Jsoup.connect("https://api.visitkorea.or.kr/guide/inforArea.do").data(data).get()
						.select(".galleryList > li > a");
				els.stream().filter(v -> !Util.findAll(v.attr("href"), "d=(\\d*)").get(1).contains("25")) // type id가
																											// 25가 아닌 장소
						.forEach(v -> list.add(new PlaceDTO( // Place DTO 1차 추가
								Util.findAll(v.attr("href"), "d=(\\d*)"), // id, type id
								v.getElementsByTag("p").text().replace("[한국관광 품질인증/Korea Quality]", "").trim(), // 제목
								v.getElementsByTag("img").attr("src")))); // 이미지 주소
				w += 100; // 테스트용 (100건식)
//				w++; // 실제 DB 적재용
			} catch (Exception e) {
				Util.sleep(1);
				continue;
			}
		} while (els.size() != 0); // 더 이상 긁을 게 없으면 Stop
		System.out.println("// 크롤링 종료");

		System.out.println("// DB 저장 시작");
		// (2) 세부 info 추출 -> 자세한 크롤링 내용 getInfos 참고
		placeRepo.saveAll(list.stream().map(this::getInfos).collect(Collectors.toList()));
		} catch(Exception e) {
			e.printStackTrace();
			throw new AsyncException("Error While Making DB");
		}
		System.out.println("// DB 저장 종료");
		return System.currentTimeMillis() - start;
	}

	public PlaceDTO getInfos(PlaceDTO p) {
		HashMap<String, String> map = new HashMap<>();

		while (true) {
			Util.sleep(0.1);
			map.clear();
			try {
				for (Element el : Jsoup.connect("http://data.visitkorea.or.kr/page/" + p.getId()).get()
						.select("#wrap > table > tbody > tr")) {
					map.put(Util.find(el.select("td.tit > a").text(), ":(\\S*)"), // key 값
							el.select("td:nth-child(2)").text()); // value 값
				}
				break;
			} catch (Exception e) {
				Util.sleep(1);
				continue;
			}
		}

		// 1차에서 받아온 정보 외에 전처리 등을 통해 데이터 획득
		p.setDescription(map.getOrDefault("description", "NULL").replace("(@ko)", ""));
		p.setLat(new BigDecimal(map.getOrDefault("lat", "0").replace("(xsd:double)", "")));
		p.setLon(new BigDecimal(map.getOrDefault("long", "0").replace("(xsd:double)", "")));
		p.setAddress(map.getOrDefault("address", "NULL").replace("(@ko)", ""));
		return p;
	}

	// * Place
	public List<PlaceDTO> findPlaceByTypeId(BigDecimal typeId) {
		return placeRepo.findPlaceByTypeId(typeId);
	}

	public List<PlaceDTO> findPlaceByKwd(String kwd) {
		return placeRepo.findPlaceByNameContainingOrAddressContainingOrDescriptionContaining(kwd, kwd, kwd);
	}

	public Optional<PlaceDTO> findPlaceById(BigDecimal id) {
		return placeRepo.findById(id);
	}

	public List<List<Object>> findPlaceByDistance(BigDecimal id, double distance) {
		return placeRepo.findPlaceByDistance(id, distance).stream()
					.map(v -> Arrays.asList(placeRepo.findById(new BigDecimal(v[0].toString())), v[1]))
					.collect(Collectors.toList());
	}
	
	public List<List<Object>> findPlaceByDistance(BigDecimal id, BigDecimal typeId, double distance) {
		return placeRepo.findPlaceByDistance(id, typeId, distance).stream()
					.map(v -> Arrays.asList(placeRepo.findById(new BigDecimal(v[0].toString())), v[1]))
					.collect(Collectors.toList());
	}

	// * Member
	public boolean checkMember(String id) {
		return memberRepo.existsById(id);
	}

	public boolean loginMember(MemberDTO m) {
		return checkMember(m.getId()) ? memberRepo.findById(m.getId()).get().getPw().equals(m.getPw()) : false;
	}

	public boolean addMember(MemberDTO m) {
		if (!checkMember(m.getId())) {
			memberRepo.save(m);
			return true;
		}
		return false;
	}

	public boolean updateMember(MemberDTO m) {
		if (checkMember(m.getId())) {
			memberRepo.findById(m.getId()).get().setPw(m.getPw());
			memberRepo.save(m);
			return true;
		} else {
			return false;
		}
	}

	// Admin
	public boolean loginAdmin(MemberDTO m) throws Exception {
		return checkMember(m.getId()) ? memberRepo.findById(m.getId()).get().getPw().equals(m.getPw()) : false;
	}
	
	public Iterable<MemberDTO> getAllMembers(){
		return memberRepo.findAll();
	}
	
	public List<MemberDTO> findMemberById(String id) {
		return memberRepo.findMemberByIdContaining(id);
	}
	
	public void deleteId(String id) {
		memberRepo.deleteById(id);
	}

}