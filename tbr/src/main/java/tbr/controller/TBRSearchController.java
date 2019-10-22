package tbr.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tbr.exception.AsyncException;
import tbr.model.dto.InstaPostDTO;
import tbr.model.dto.PlaceDTO;
import tbr.model.dto.TagDTO;
import tbr.service.TBRSearchService;
import tbr.util.Util;

@CrossOrigin(origins = { "http://127.0.0.1:8000", "http://localhost:8000" })
@RestController
public class TBRSearchController {
	@Autowired
	TBRSearchService service;

	// * SET
	// http://127.0.0.1:8000/kwdsearch?kwd=여름
	@GetMapping("/kwdsearch")
	public Object[] kwdSearch(@RequestParam String kwd) {
		Util.sleep(0.1);
		System.out.println("/kwdsearch : " + kwd);
		System.out.println("// DB, INSTA, TAG");
		Object[] result = new Object[3];
		System.out.println("// STEP 1 : DB");
		try {
			result[0] = service.findPlaceByKwd(kwd);			
		} catch (Exception e) {
			throw new AsyncException("RDBMS(mySQL) 관련 오류");
		}
		System.out.println("// STEP 2 : INSTA");
		try {
			result[1] = service.searchInstaByKwd(kwd);			
		} catch (Exception e) {
			throw new AsyncException("Elastic Search (Insta) 관련 오류");
		}
		System.out.println("// STEP 3 : TAG");
		try {
			result[2] = service.getTagListByKwd(kwd);			
		} catch (Exception e) {
			throw new AsyncException("Elastic Search (Tag) 관련 오류");
		}
		return result;
	}
	
	// http://127.0.0.1:8000/locsearch?loc=서울
	@GetMapping("/locsearch")
	public Object[] locSearch(@RequestParam String loc) {
		Util.sleep(0.1);
		System.out.println("/locsearch : " + loc);
		System.out.println("// DB, INSTA, TAG");
		Object[] result = new Object[3];
		System.out.println("// STEP 1 : DB");
		try {
			result[0] = service.findPlaceByKwd(loc);			
		} catch (Exception e) {
			throw new AsyncException("RDBMS(mySQL) 관련 오류");
		}
		System.out.println("// STEP 2 : INSTA");
		try {
			result[1] = service.searchInstaByLoc(loc);			
		} catch (Exception e) {
			throw new AsyncException("Elastic Search (Insta) 관련 오류");
		}
		System.out.println("// STEP 3 : TAG");
		try {
			result[2] = service.getTagListByLoc(loc);			
		} catch (Exception e) {
			throw new AsyncException("Elastic Search (Tag) 관련 오류");
		}
		return result;
	}
	
	// http://127.0.0.1:8000/kwdandlocsearch?kwd=데이트&loc=서울
	@GetMapping("/kwdandlocsearch")
	public Object[] kwdAndLocsearch(@RequestParam String kwd, @RequestParam String loc) {
		Util.sleep(0.1);
		System.out.println("/kwdandlocsearch : " + kwd + " " + loc);
		System.out.println("// DB, INSTA, TAG");
		Object[] result = new Object[3];
		System.out.println("// STEP 1 : DB");
		try {
			result[0] = service.findPlaceByKwd(kwd);			
		} catch (Exception e) {
			throw new AsyncException("RDBMS(mySQL) 관련 오류");
		}
		System.out.println("// STEP 2 : INSTA");
		try {
			result[1] = service.searchInstaByLocAndKwd(loc, kwd);			
		} catch (Exception e) {
			throw new AsyncException("Elastic Search (Insta) 관련 오류");
		}
		System.out.println("// STEP 3 : TAG");
		try {
			result[2] = service.getTagListByLocAndKwd(loc, kwd);			
		} catch (Exception e) {
			throw new AsyncException("Elastic Search (Tag) 관련 오류");
		}
		return result;
	}
	
	// http://127.0.0.1:8000/idsearch?id=1012988
	@GetMapping("/idsearch")
	public Object[] idSearch(@RequestParam BigDecimal id) {
		System.out.println("/idsearch : " + id);
		System.out.println("// DB, INSTA, TAG");
		Object[] result = new Object[3];
		System.out.println("// STEP 1 : DB");
		try {
			result[0] = service.findPlaceById(id);			
		} catch (Exception e) {
			throw new AsyncException("RDBMS(mySQL) 관련 오류");
		}
		// ID 별 위치 정보 처리
		String loc = service.findPlaceById(id).get().getAddress().split(" ")[0]
				.replace("특별시", "").replace("광역시", "").replace("특별자치도", "")
				.replaceAll("\\S북도", "북").replaceAll("\\S남도", "남")
				.replace("세종특별자치시", "충남");
		System.out.println(loc);
		System.out.println("// STEP 2 : INSTA");
		try {
			result[1] = service.searchInstaByLoc(loc);			
		} catch (Exception e) {
			throw new AsyncException("Elastic Search (Insta) 관련 오류");
		}
		System.out.println("// STEP 3 : TAG");
		try {
			result[2] = service.getTagListByLoc(loc);			
		} catch (Exception e) {
			throw new AsyncException("Elastic Search (Tag) 관련 오류");
		}
		return result;
	}
	
	// http://127.0.0.1:8000/distsearch?id=1012988&km=4
	@GetMapping("/distsearch")
	public Object[] distSearch(@RequestParam BigDecimal id, @RequestParam double km) {
		System.out.println("/distanceSearch : " + id + " km : " + km);
		System.out.println("// DB, INSTA, TAG");
		Object[] result = new Object[3];
		System.out.println("// STEP 1 : DB");
		try {
			result[0] = service.findPlaceByDistance(id, km);			
		} catch (Exception e) {
			throw new AsyncException("RDBMS(mySQL) 관련 오류");
		}
		// ID 별 위치 정보 처리
		String loc = service.findPlaceById(id).get().getAddress().split(" ")[0]
				.replace("특별시", "").replace("광역시", "").replace("특별자치도", "")
				.replaceAll("\\S북도", "북").replaceAll("\\S남도", "남")
				.replace("세종특별자치시", "충남");
		System.out.println(loc);
		System.out.println("// STEP 2 : INSTA");
		try {
			result[1] = service.searchInstaByLoc(loc);			
		} catch (Exception e) {
			throw new AsyncException("Elastic Search (Insta) 관련 오류");
		}
		System.out.println("// STEP 3 : TAG");
		try {
			result[2] = service.getTagListByLoc(loc);			
		} catch (Exception e) {
			throw new AsyncException("Elastic Search (Tag) 관련 오류");
		}
		return result;
	}
	
	// http://127.0.0.1:8000/distandtypesearch?id=1012988&typeId=39&km=10
	@GetMapping("/distandtypesearch")
	public Object[] distAndTypeSearch(@RequestParam BigDecimal id, @RequestParam BigDecimal typeId, @RequestParam double km) {
		System.out.println("/distanceandtypesearch : " + id + " km : " + km);
		System.out.println("// DB, INSTA, TAG");
		Object[] result = new Object[3];
		System.out.println("// STEP 1 : DB");
		try {
			result[0] = service.findPlaceByDistance(id, typeId, km);			
		} catch (Exception e) {
			throw new AsyncException("RDBMS(mySQL) 관련 오류");
		}
		// ID 별 위치 정보 처리
		String loc = service.findPlaceById(id).get().getAddress().split(" ")[0]
				.replace("특별시", "").replace("광역시", "").replace("특별자치도", "")
				.replaceAll("\\S북도", "북").replaceAll("\\S남도", "남")
				.replace("세종특별자치시", "충남");
		System.out.println(loc);
		System.out.println("// STEP 2 : INSTA");
		try {
			result[1] = service.searchInstaByLoc(loc);			
		} catch (Exception e) {
			throw new AsyncException("Elastic Search (Insta) 관련 오류");
		}
		System.out.println("// STEP 3 : TAG");
		try {
			result[2] = service.getTagListByLoc(loc);			
		} catch (Exception e) {
			throw new AsyncException("Elastic Search (Tag) 관련 오류");
		}
		return result;
	}
	
	
	// * SEARCH
	// http://127.0.0.1:8000/searchByType/typeId=38
	@GetMapping("/searchByType")
	public List<PlaceDTO> searchByType(@RequestParam BigDecimal typeId) throws AsyncException {
		System.out.println("/searchByType");
		try {
			return service.findPlaceByTypeId(typeId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AsyncException("ERROR");
		}
	}

	// http://127.0.0.1:8000/searchByKeyword?kwd=산
	@GetMapping("/searchByKeyword")
	public List<PlaceDTO> searchByKeyword(@RequestParam String kwd) throws AsyncException {
		System.out.println("/searchByKeyword : " + kwd);
		if(kwd.length() == 0) {
			throw new AsyncException("no search keyword");
		}
		try {
			return service.findPlaceByKwd(kwd);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AsyncException("ERROR");
		}
	}

	// http://127.0.0.1:8000/searchByDistanceAndType?id=319571&distance=10
	@GetMapping("/searchByDistance")
	public List<List<Object>> searchByDistance(@RequestParam BigDecimal id, @RequestParam double distance)
			throws AsyncException {
		System.out.println("/searchByDistance");
		try {
			return service.findPlaceByDistance(id, distance);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AsyncException("ERROR");
		}
	}
	
	// http://127.0.0.1:8000/searchByDistanceAndType?id=319571&typeId=12&distance=10
	@GetMapping("/searchByDistanceAndType")
	public List<List<Object>> searchByDistance(@RequestParam BigDecimal id, @RequestParam BigDecimal typeId,
			@RequestParam double distance) throws AsyncException {
		System.out.println("/searchByDistanceAndType");
		try {
			return service.findPlaceByDistance(id, typeId, distance);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AsyncException("ERROR");
		}
	}
	
	//http://127.0.0.1:8000/searchById?id=2360786
	@GetMapping("/searchById")
	public Optional<PlaceDTO> searchById(@RequestParam BigDecimal id) {
		System.out.println("/searchById");
		return service.findPlaceById(id);
	}
   
	//http://127.0.0.1:8000/hit?placeId=1012988
	@GetMapping("/hit")
	public String hit(@RequestParam BigDecimal placeId) {
		System.out.println("/hit");
		try {
			service.addPlaceHit(placeId);
			return "OK";
		} catch (Exception e) {
			e.printStackTrace();
			throw new AsyncException("ERROR");
		}
	}

	// * DB
	// http://127.0.0.1:8000/dataCollect
	// 최초 구동 시 spring.jpa.hibernate.ddl-auto=create 설정 확인
	// + mySQL UTF-8 관련 인코딩 문제 해결해야함
	@GetMapping("/dataCollect")
	public String dataCollect() throws AsyncException {
		System.out.println("/dataCollect");
		try {
			return "실행 시간 : " + service.getIds() + "ms";			
		} catch (Exception e) {
			e.printStackTrace();
			throw new AsyncException("ERROR");
		}
	}
	
	// http://127.0.0.1:8000/instaCollect
	// 최초 구동시 nori plugin 및 userdict 설치 여부 확인
	@GetMapping("/instaCollect")
	public String instaCollect() throws AsyncException {
		System.out.println("/instaCollect");
		try {
			return "실행 시간 : " + service.getSocialData() + "ms";
		} catch (AsyncException e) {
			throw new AsyncException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new AsyncException("ERROR");
		}
	}
	
	// * Insta
	// http://127.0.0.1:8000/instaKwdSearch?kwd=대한민국
	@GetMapping("/instaKwdSearch")
	public List<InstaPostDTO> instaKwdSearch(@RequestParam String kwd) throws AsyncException {
		System.out.println("/instaKwdSearch");
		try {
			return service.searchInstaByKwd(kwd);
		} catch (AsyncException e) {
			throw new AsyncException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new AsyncException("ERROR");
		}
	}
	
	// http://127.0.0.1:8000/instaLocSearch?loc=경남
	@GetMapping("/instaLocSearch")
	public List<InstaPostDTO> instaLocSearch(@RequestParam String loc) throws AsyncException {
		System.out.println("/instaLocSearch");
		try {
			return service.searchInstaByLoc(loc);
		} catch (AsyncException e) {
			throw new AsyncException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new AsyncException("ERROR");
		}
	}
	
	// http://127.0.0.1:8000/instaLocAndKwdSearch?loc=경남&kwd=핑크
	@GetMapping("/instaLocAndKwdSearch")
	public List<InstaPostDTO> instaLocAndKwdSearch(@RequestParam String loc, @RequestParam String kwd) throws AsyncException {
		System.out.println("/instaLocAndKwdSearch");
		try {
			return service.searchInstaByLocAndKwd(loc, kwd);
		} catch (AsyncException e) {
			throw new AsyncException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new AsyncException("ERROR");
		}
	}
	
	// http://127.0.0.1:8000/instaTagListFromAll
	@GetMapping("/instaTagListFromAll")
	public List<TagDTO> instaTagListFromAll() throws AsyncException {
		System.out.println("/instaTagListByKwd");
		try {
			return service.getTagListFromAll();
		} catch (AsyncException e) {
			throw new AsyncException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new AsyncException("ERROR");
		}
	}
	
	// http://127.0.0.1:8000/instaTagListByKwd?kwd=가을
	@GetMapping("/instaTagListByKwd")
	public List<TagDTO> instaTagListByKwd(@RequestParam String kwd) throws AsyncException {
		System.out.println("/instaTagListByKwd");
		try {
			return service.getTagListByKwd(kwd);
		} catch (AsyncException e) {
			throw new AsyncException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new AsyncException("ERROR");
		}
	}
	
	// http://127.0.0.1:8000/instaTagListByLoc?loc=서울
	@GetMapping("/instaTagListByLoc")
	public List<TagDTO> instaTagListByLoc(@RequestParam String loc) throws AsyncException {
		System.out.println("/instaTagListByLoc");
		try {
			return service.getTagListByLoc(loc);
		} catch (AsyncException e) {
			throw new AsyncException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new AsyncException("ERROR");
		}
	}
	
	// http://127.0.0.1:8000/instaTagListByLocAndKwd?loc=서울&kwd=가을
	@GetMapping("/instaTagListByLocAndKwd")
	public List<TagDTO> instaTagListByLoc(@RequestParam String loc, @RequestParam String kwd) throws AsyncException {
		System.out.println("/instaTagListByLocAndKwd");
		try {
			return service.getTagListByLocAndKwd(loc, kwd);
		} catch (AsyncException e) {
			throw new AsyncException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new AsyncException("ERROR");
		}
	}

}