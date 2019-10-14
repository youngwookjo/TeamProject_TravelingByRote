package tbr.util.crawling;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tbr.model.dao.PlaceRepository;
import tbr.model.dto.Place;
import tbr.util.Common;

@Component
public class PlaceCrawler {
	@Autowired
	PlaceRepository placeRepo;

	public int crawling() {
		// http://api.visitkorea.or.kr/guide/inforArea.do?langtype=KOR&pageNo=1&mode=listOk
		HashMap<String, String> data = new HashMap<>();
		data.put("langtype", "KOR");
		data.put("mode", "listOk");
		int w = 841;
		int count = 0;
		while (true) {
			Common.sleep(0.1);
			System.out.printf("page %d\n", w);
			data.put("pageNo", Integer.toString(w));
			try {
				List<Element> list = Jsoup.connect("https://api.visitkorea.or.kr/guide/inforArea.do").data(data).get()
						.select(".galleryList > li > a");
				if (list.size() == 0) {
					System.out.println("크롤링 종료");
					break;
				}
				saveAll(
						list.stream()
						.map(v -> Common.findAll(v.attr("href"), "d=(\\d*)")) // 정규표현식으로 id, typeId 받아서
						.filter(v -> !v.get(1).equals("25")) // resource에서 받을 수 없는 코스 정보 제외
						.filter(v -> !exist(new BigDecimal(v.get(0)))) // 이미 저장되어 있는 내용들 제외
						.map(v -> crawlingResource(v.get(0), v.get(1))) // 리소스에서 받아옴
						.collect(Collectors.toList())
						);
//				w += 1000; // 테스트 용
				w++; // 다음 페이지로
			} catch (IOException e) {
				e.printStackTrace();
				Common.sleep(1);
			}
		}
		return count;
	}
	
	private boolean saveAll(List<HashMap<String, String>> list) {
		placeRepo.saveAll(
				list.stream()
				.map(v -> new Place(
					new BigDecimal(v.get("id")),
					new BigDecimal(v.get("typeId")),
					v.get("name").replace("(@ko)", "")
						.replace("[한국관광 품질인증/Korea Quality]", "")
						.replace("[한국관광품질인증/KoreaQuality]", "").trim(),
					new BigDecimal(v.getOrDefault("lat", "0").replace("(xsd:double)", "")),
					new BigDecimal(v.getOrDefault("long", "0").replace("(xsd:double)", ""))))
				.collect(Collectors.toList()));
		return true;
	}

	private HashMap<String, String> crawlingResource(String id, String typeId) {
		String url = "http://data.visitkorea.or.kr/page/" + id;
		Common.sleep(0.1);
		System.out.println(url);
		HashMap<String, String> map = new HashMap<>();
		Document doc;
		while (true) {
			try {
				doc = Jsoup.connect(url).get();
				break;
			} catch (IOException e) {
				e.printStackTrace();
				Common.sleep(1);
			}
		}
		map.put("id", id);
		map.put("typeId", typeId);
		for (Element el : doc.select("#wrap > table > tbody > tr")) {
			map.put(Common.find(el.select("td.tit > a").text(), ":(\\S*)"),
					el.select("td:nth-child(2)").text());
		}
		return map;
	}

	private boolean exist(BigDecimal id) {
		return placeRepo.findById(id).isPresent();
	}

}
