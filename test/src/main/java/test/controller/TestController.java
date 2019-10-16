package test.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import test.model.dao.PlaceRepository;
import test.model.dto.PlaceDTO;

@RestController
public class TestController {
	@Autowired
	PlaceRepository placeRepo;
	
	@GetMapping("/make_db")
	public String makeDB() {
		long start = System.currentTimeMillis();
		// Resource Id 추출
		HashMap<String, String> data = new HashMap<>();
		data.put("langtype", "KOR");
		data.put("mode", "listOk");
		int w = 1;
		ArrayList<PlaceDTO> list = new ArrayList<>();
		Elements els = null;
		do {
			sleep(0.1);
			System.out.printf("// page %d\n", w);
			data.put("pageNo", Integer.toString(w));
			try {
				els = Jsoup.connect("https://api.visitkorea.or.kr/guide/inforArea.do").data(data).get()
						.select(".galleryList > li > a");
				els.stream()
					.filter(v -> !findAll(v.attr("href"), "d=(\\d*)").get(1).contains("25"))
					.forEach(v -> list.add(
						new PlaceDTO(findAll(v.attr("href"), "d=(\\d*)"),
						v.getElementsByTag("p").text().replace("[한국관광 품질인증/Korea Quality]", ""),
						v.getElementsByTag("img").attr("src")
						)));
//				break;
				w += 1000;
//				w++;
			} catch (Exception e) {
				System.out.println("// 에러 발생 => 1초 wait");
				sleep(1);
				continue;
			}
		} while(els.size() != 0); // 더 이상 긁을 게 없으면 Stop
		list.stream().forEach(System.out::println);
		System.out.println(list.size());
		placeRepo.saveAll(list.stream().map(this::putInfo).collect(Collectors.toList()));
		// 세부 info 추출
		return "실행 시간 : " + (System.currentTimeMillis() - start);
		}
	
	public PlaceDTO putInfo(PlaceDTO p) {
		HashMap<String, String> map = new HashMap<>();
		while(true) {
			sleep(0.1);
			System.out.println("// ID : " + p.getId());
			map.clear();
			try {
				for (Element el : Jsoup.connect("http://data.visitkorea.or.kr/page/" + p.getId()).get()
						.select("#wrap > table > tbody > tr")) {
					map.put(find(el.select("td.tit > a").text(), ":(\\S*)"), el.select("td:nth-child(2)").text());
				}
				break;
			} catch (Exception e) {
				System.out.println("// 에러 발생 => 1초 wait");
				sleep(1);
				continue;
			}
		}
		p.setDescription(map.getOrDefault("description", "NULL").replace("(@ko)", ""));
		p.setLat(new BigDecimal(map.getOrDefault("lat", "0").replace("(xsd:double)", "")));
		p.setLon(new BigDecimal(map.getOrDefault("long", "0").replace("(xsd:double)", "")));
		p.setAddress(map.getOrDefault("address", "NULL").replace("(@ko)", ""));
		System.out.println(p);
		return p;
	}
	
	public String find(String text, String regex) {
		Matcher m = Pattern.compile(regex).matcher(text);
		m.find();
		return m.group(1);
	}
	
	public List<String> findAll(String text, String regex) {
		Matcher m = Pattern.compile(regex).matcher(text);
		List<String> list = new ArrayList<>();
		while (m.find()) {
			if (m.group(1).length() != 0) {
				list.add(m.group(1));
			}
		}
		return list;
	}

	public void sleep(double sec) {
		try {
			Thread.sleep((long) (sec * 1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}