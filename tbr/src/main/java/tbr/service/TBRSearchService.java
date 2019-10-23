package tbr.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tbr.exception.AsyncException;
import tbr.model.dao.PlaceRepository;
import tbr.model.dto.InstaPostDTO;
import tbr.model.dto.PlaceDTO;
import tbr.model.dto.TagDTO;
import tbr.model.es.ESHighLevelClient;
import tbr.util.Util;

@Service
public class TBRSearchService {
	@Autowired
	PlaceRepository placeRepo;
	@Autowired
	ESHighLevelClient esClient;

	String indexName = "insta_post";
	String[] locList = {"국내여행","서울여행","인천여행","경기도여행","충북여행","충남여행",
						"강원도여행","전북여행","전남여행","대전여행","광주여행", "제주여행",
						"부산여행","울산여행","경북여행","경남여행","대구여행"};
	
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
	
	public long getSocialData() throws IOException {
		long start = System.currentTimeMillis(); // 실행 시간 측정

		System.out.println("// 인스타 크롤링 시작");
		List<InstaPostDTO> list = new ArrayList<>();
		for(String loc : locList) {
			System.out.println(loc);
			list.addAll(getInstaPosts(loc));
		}
		System.out.println("// 인스타 크롤링 종료");
		System.out.println("// 엘라스틱 서치 저장 시작");
		
		esClient.connect();
		try {
			System.out.println("// 인덱스 생성");
			try {
				System.out.println(esClient.deleteIndex(indexName) ? "// 초기화를 위한 삭제 진행" : false);			
			} catch (Exception e) {
				System.out.println("// 존재하지 않는 인덱스");
			}
			// https://www.elastic.co/guide/en/elasticsearch/plugins/7.1/analysis-nori-tokenizer.html
			// mapping
			Map<String, Object> locType = new HashMap<>();
			Map<String, Object> img = new HashMap<>();
			Map<String, Object> text = new HashMap<>();
			Map<String, Object> likes = new HashMap<>();
			Map<String, Object> comments = new HashMap<>();
			locType.put("type", "keyword");
			img.put("type", "keyword");
			text.put("type", "text");
			text.put("analyzer", "korean");
			likes.put("type", "integer");
			comments.put("type", "integer");
			Map<String, Object> properties = new HashMap<>();
			properties.put("loc_type", locType);
			properties.put("img", img);
			properties.put("text", text);
			properties.put("likes", likes);
			properties.put("comments", comments);
			Map<String, Object> mappings = new HashMap<>();
			mappings.put("properties", properties);
			
			// settings		
			Map<String, Object> nori_user_dict = new HashMap<>();
			nori_user_dict.put("type", "nori_tokenizer");
			nori_user_dict.put("decompound_mode", "mixed");
			// $ES_HOME/config/userdict_ko.txt
			nori_user_dict.put("user_dictionary", "userdict_ko.txt");
			Map<String, Object> tokenizer = new HashMap<>();
			tokenizer.put("nori_user_dict", nori_user_dict);
			
			Map<String, Object> korean = new HashMap<>();
			korean.put("type", "custom");
			korean.put("tokenizer", "nori_user_dict");
			korean.put("filter", Arrays.asList("nori_readingform", "lowercase", "nori_part_of_speech_basic"));
			Map<String, Object> analyzer = new HashMap<>();
			analyzer.put("korean", korean);
			
			Map<String, Object> nori_part_of_speech_basic = new HashMap<>();
			nori_part_of_speech_basic.put("type", "nori_part_of_speech");
			nori_part_of_speech_basic.put("stoptags", Arrays.asList("E","IC","J","MAG","MAJ","MM","SP","SSC","SSO","SC","SE","XPN","XSA","XSN","XSV","UNA","NA","VSV"));
			Map<String, Object> filter = new HashMap<>();
			filter.put("nori_part_of_speech_basic", nori_part_of_speech_basic);

			Map<String, Object> analysis = new HashMap<>();
			analysis.put("tokenizer", tokenizer);
			analysis.put("analyzer", analyzer);
			analysis.put("filter", filter);

			Map<String, Object> indexMap = new HashMap<>();
			indexMap.put("analysis", analysis);

			Map<String, Object> settings = new HashMap<>();
			settings.put("number_of_shards", 2);
			settings.put("number_of_replicas", 0);
			settings.put("index", indexMap);
			
			Map<String, Object> source = new HashMap<>();
			source.put("settings", settings);
			source.put("mappings", mappings);
			
			esClient.createIndex(indexName, source);
			
			esClient.bulk(indexName, list);
		} catch (Exception e) {
			e.printStackTrace();
			new AsyncException("Elastic_Search_Error");
		} finally {			
			esClient.close();
		}
		System.out.println("// 엘라스틱 서치 저장 종료");
		return System.currentTimeMillis() - start;
	}
	
	public List<InstaPostDTO> getInstaPosts(String tag) throws IOException {
		ArrayList<InstaPostDTO> list = new ArrayList<>();
		Jsoup.connect("https://www.instazu.com/tag/" + tag).get()
			.select(".box-photo").forEach(v -> list.add(
					new InstaPostDTO(tag,
							v.selectFirst("img").attr("src"),
							v.selectFirst(".photo-description").text(),
							Integer.parseInt(v.selectFirst(".likes_photo").text().replace("k", "000")),
							Integer.parseInt(v.selectFirst(".comments_photo").text())
							)));
		System.out.println(list.size());
		return list;
	}
	
	// * Place
	public boolean addPlaceHit(BigDecimal placeId) {
		PlaceDTO m = placeRepo.findById(placeId).get();
		m.addHit();
		placeRepo.save(m);
		return true;
	}

	public List<PlaceDTO> findPlaceByTypeId(BigDecimal typeId) {
		return placeRepo.findPlaceByTypeId(typeId).stream()
				.sorted((v1, v2) -> Long.compare(v2.getHits(), v1.getHits()))
				.collect(Collectors.toList());
	}

	public List<PlaceDTO> findPlaceByKwd(String kwd) {
//		List<PlaceDTO> result = new ArrayList<>();
		Set<PlaceDTO> resultSet = new HashSet<>();
		placeRepo.findPlaceByNameContainingOrAddressContaining(kwd, kwd).stream().forEach( v -> {resultSet.add(v);});
		placeRepo.findPlaceByDescriptionContaining(kwd).stream().forEach( v -> {resultSet.add(v);});
		return resultSet.stream()
			.sorted((v1, v2) -> Long.compare(v2.getHits(), v1.getHits()))
			.collect(Collectors.toList());
//		return result;
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
	
	// * InstaPost
	public List<InstaPostDTO> searchInstaByKwd(String kwd) throws IOException {
		List<InstaPostDTO> list = new ArrayList<>();
		Map<String, Object> map = null;
		esClient.connect();
		try {
			for(SearchHit hit : esClient.searchByKwd(indexName, kwd)) {
				map = hit.getSourceAsMap();
				list.add(
						new InstaPostDTO(
								map.get("loc_type").toString(), map.get("img").toString(), map.get("text").toString(),
								Integer.parseInt(map.get("likes").toString()), Integer.parseInt(map.get("comments").toString())));
			};
		} catch (Exception e) {
			e.printStackTrace();
			new AsyncException("ES_ERROR");
		} finally {			
			esClient.close();
		}
		return list;
	}
	
	public List<InstaPostDTO> searchInstaByLoc(String loc) throws IOException {
		List<InstaPostDTO> list = new ArrayList<>();
		Map<String, Object> map = null;
		esClient.connect();
		try {
			for(SearchHit hit : esClient.searchByLoc(indexName, loc)) {
				map = hit.getSourceAsMap();
				list.add(
						new InstaPostDTO(
								map.get("loc_type").toString(), map.get("img").toString(), map.get("text").toString(),
								Integer.parseInt(map.get("likes").toString()), Integer.parseInt(map.get("comments").toString())));
			};
		} catch (Exception e) {
			e.printStackTrace();
			new AsyncException("ES_ERROR");
		} finally {			
			esClient.close();
		}
		return list;
	}
	
	public List<InstaPostDTO> searchInstaByLocAndKwd(String loc, String kwd) throws IOException {
		List<InstaPostDTO> list = new ArrayList<>();
		Map<String, Object> map = null;
		esClient.connect();
		try {
			for(SearchHit hit : esClient.searchByLocAndKwd(indexName, loc, kwd)) {
				map = hit.getSourceAsMap();
				list.add(
						new InstaPostDTO(
								map.get("loc_type").toString(), map.get("img").toString(), map.get("text").toString(),
								Integer.parseInt(map.get("likes").toString()), Integer.parseInt(map.get("comments").toString())));
			};
		} catch (Exception e) {
			e.printStackTrace();
			new AsyncException("ES_ERROR");
		} finally {			
			esClient.close();
		}
		return list;
	}
	
	public List<TagDTO> getTagListFromAll() throws IOException {
		esClient.connect();
		try {
			return esClient.getAllFrequencyList(indexName);
		} catch (Exception e) {
			e.printStackTrace();
			new AsyncException("ES_ERROR");
		} finally {			
			esClient.close();
		}
		return null;
	}
	
	public List<TagDTO> getTagListByKwd(String kwd) throws IOException {
		esClient.connect();
		try {
			return esClient.getFrequencyList(indexName, StreamSupport
						.stream(esClient.searchByKwd(indexName, kwd).spliterator(), false)
						.map(v -> v.getId()).toArray(String[]::new), Arrays.asList(kwd).stream().toArray(String[]::new));
		} catch (Exception e) {
			e.printStackTrace();
			new AsyncException("ES_ERROR");
		} finally {			
			esClient.close();
		}
		return null;
	}
	
	public List<TagDTO> getTagListByLoc(String loc) throws IOException {
		esClient.connect();
		try {
			return esClient.getFrequencyList(indexName, StreamSupport
					.stream(esClient.searchByLoc(indexName, loc).spliterator(), false)
					.map(v -> v.getId()).toArray(String[]::new), Arrays.asList(loc, loc.replace("도", "")).stream()
					.collect(Collectors.toSet()).stream().toArray(String[]::new));
		} catch (Exception e) {
			e.printStackTrace();
			new AsyncException("ES_ERROR");
		} finally {			
			esClient.close();
		}
		return null;
	}
	
	public List<TagDTO> getTagListByLocAndKwd(String loc, String kwd) throws IOException {
		esClient.connect();
		try {
			return esClient.getFrequencyList(indexName, StreamSupport
					.stream(esClient.searchByLocAndKwd(indexName, loc, kwd).spliterator(), false)
					.map(v -> v.getId()).toArray(String[]::new), Arrays.asList(kwd, loc, loc.replace("도", "")).stream()
					.collect(Collectors.toSet()).stream().toArray(String[]::new));
		} catch (Exception e) {
			e.printStackTrace();
			new AsyncException("ES_ERROR");
		} finally {			
			esClient.close();
		}
		return null;
	}

	// * DB: DB내 존재하는 모든 typeId와 카운팅 횟수 return 로직
	public List<List<Object>> typeId(){
		return placeRepo.findCountByTypeId().stream()
				.map(v -> Arrays.asList(v[0], v[1]))
				.collect(Collectors.toList());
	}

	// * DB: 키워드 입력 후 결과값 내 typeId와 카운팅 횟수 return 로직
	public List<List<Object>> typeIdKwd(String kwd){
		System.out.println(kwd+ " in service");
		return placeRepo.findCountByTypeIdKwd(kwd).stream()
				.map(v -> Arrays.asList(v[0], v[1]))
				.collect(Collectors.toList());
	}

	// * DB: 키워드 입력 후 결과값 내 typeId와 카운팅 횟수 return 로직
	public List<PlaceDTO> kwdTypeIdIn(BigDecimal typeId, String kwd){
		System.out.println(kwd + typeId + " in service");
		List<PlaceDTO> result = new ArrayList<>();
		placeRepo.findPlaceByTypeIdAndAddressContaining(typeId, kwd).stream().forEach( v -> {result.add(v);});
		placeRepo.findPlaceByTypeIdAndDescriptionContaining(typeId, kwd).stream().forEach( v -> {result.add(v);});
		placeRepo.findPlaceByTypeIdAndNameContaining(typeId, kwd).stream().forEach( v -> {result.add(v);});
		return result;
	}

	
}