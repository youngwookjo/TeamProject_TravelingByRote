package tbr.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	
	public long getIds() {
		long start = System.currentTimeMillis(); // 실행 시간 측정
		// (1) Resource Id 추출
		HashMap<String, String> data = new HashMap<>(); // Jsoup param 조정을 위한 map
		data.put("langtype", "KOR");
		data.put("mode", "listOk");
		int w = 1; // 반복 기준이 되는 변수
		ArrayList<PlaceDTO> list = new ArrayList<>();
		Elements els = null;
		do {
			Util.sleep(0.1);
//			System.out.printf("// page %d\n", w);
			data.put("pageNo", Integer.toString(w));
			try {
				els = Jsoup.connect("https://api.visitkorea.or.kr/guide/inforArea.do")
						.data(data).get()
						.select(".galleryList > li > a");
				els.stream()
					.filter(v -> !Util.findAll(v.attr("href"), "d=(\\d*)").get(1).contains("25")) // type id가 25가 아닌 장소
					.forEach(v -> list.add(
							new PlaceDTO( // Place DTO 1차 추가
								Util.findAll(v.attr("href"), "d=(\\d*)"), // id, type id
								v.getElementsByTag("p").text().replace("[한국관광 품질인증/Korea Quality]", "").trim(), // 제목
								v.getElementsByTag("img").attr("src")))); // 이미지 주소
				w += 100; // 테스트용 (100건식)
//				w++; // 실제 DB 적재용
			} catch (Exception e) {
//				System.out.println("// 에러 발생 => 1초 wait");
				Util.sleep(1);
				continue;
			}
		} while (els.size() != 0); // 더 이상 긁을 게 없으면 Stop
//		list.stream().forEach(System.out::println);
//		System.out.println(list.size());
		// (2) 세부 info 추출 -> 자세한 크롤링 내용 getInfos 참고
		placeRepo
			.saveAll(list.stream()
			.map(this::getInfos)
			.collect(Collectors.toList()));
		return System.currentTimeMillis() - start;
	}

	public PlaceDTO getInfos(PlaceDTO p) {
		HashMap<String, String> map = new HashMap<>();
		while (true) {
			Util.sleep(0.1);
//			System.out.println("// ID : " + p.getId());
			map.clear();
			try {
				for (Element el : Jsoup.connect("http://data.visitkorea.or.kr/page/" + p.getId()).get()
						.select("#wrap > table > tbody > tr"))
				{
					map.put(
						Util.find(el.select("td.tit > a").text(), ":(\\S*)"), // key 값
						el.select("td:nth-child(2)").text()); // value 값
				}
				break;
			} catch (Exception e) {
//				System.out.println("// 에러 발생 => 1초 wait");
				Util.sleep(1);
				continue;
			}
		}
		// 1차에서 받아온 정보 외에 전처리 등을 통해 데이터 획득
		p.setDescription(map.getOrDefault("description", "NULL").replace("(@ko)", ""));
		p.setLat(new BigDecimal(map.getOrDefault("lat", "0").replace("(xsd:double)", "")));
		p.setLon(new BigDecimal(map.getOrDefault("long", "0").replace("(xsd:double)", "")));
		p.setAddress(map.getOrDefault("address", "NULL").replace("(@ko)", ""));
//		System.out.println(p);
		return p;
	}


	public List<PlaceDTO> findPlaceByTypeId(String typeId) {
		return placeRepo.findPlaceByTypeId(new BigDecimal(typeId));
	}
	public List<PlaceDTO> findPlaceByKwd(String kwd) {
		return placeRepo.findPlaceByNameContainingOrAddressContainingOrDescriptionContaining(kwd, kwd, kwd);
	}

	public boolean loginMember(MemberDTO m) {
		if(memberRepo.existsById(m.getId())) {
			return memberRepo.findById(m.getId()).get().getPw().equals(m.getPw());
		}
		return false;
	}
	
	public boolean addMember(MemberDTO m) {
		return false;
	}
	
	//이하 남세영 추가
	public List<PlaceDTO> findPlaceByDistance(BigDecimal id, String typeId, int distance){
		
		List<PlaceDTO> result = new ArrayList<PlaceDTO>();
		//기준장소의 경도, 위도, 주소값 구하기
		Optional<PlaceDTO> place = placeRepo.findById(id);
		double lat1 = place.get().getLat().doubleValue();
		double lon1 = place.get().getLon().doubleValue();
		String address1 = place.get().getAddress();
		//주소값 string을 띄어쓰기 기준으로 잘라서 시, 구 단위 구하기
		String[] array = address1.split(" ");
		String shi = array[0];
		String gu = array[1];
		String shigu = shi + " " + gu;
		//기준지역의 주소값내 시,구, 그리고 주어진 typeId와 일치하는 targetList 구하기
		List<PlaceDTO> targetList = placeRepo.findPlaceByAddressContaining(shigu);
		//targetList내 PlaceDTO 중 주어진 typeId와 일치하는 애들만 모아서 narrowedList 만들기
		List<PlaceDTO> narrowedList = new ArrayList<PlaceDTO>();
		for(int i=0; i<targetList.size(); i++) {
			
			String typeId2 = targetList.get(i).getTypeId().toString();
			if(typeId.equals(typeId2)) {
				narrowedList.add(targetList.get(i));
			}
		}
		//narrowedList내 PlaceDTO 중 주어진 거리를 만족하는 애들만 솎아내기
		for(int i=0; i<narrowedList.size(); i++) {
			double lat2 = narrowedList.get(i).getLat().doubleValue();
			double lon2 = narrowedList.get(i).getLon().doubleValue();
			double distanceKilo = distance(lat1, lon1, lat2, lon2, "kilometer");
			System.out.println(distanceKilo);
			if(distanceKilo<distance) {
				result.add(narrowedList.get(i));
			}
		}
		return result;
	}
	
	//distance 구하는 메소드
    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
    	if ((lat1 == lat2) && (lon1 == lon2)) {
			return 0;
		}
		else {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
         
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
         
        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if(unit == "meter"){
            dist = dist * 1609.344;
        }
 
        return (dist);
		}
    }
    
    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
     
    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
	
}
