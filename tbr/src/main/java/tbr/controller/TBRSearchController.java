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
import tbr.service.TBRUserService;
import tbr.util.Util;

@CrossOrigin(origins = { "http://127.0.0.1:8000", "http://localhost:8000" })
@RestController
public class TBRSearchController {
	@Autowired
	TBRSearchService searchService;
	@Autowired
	TBRUserService userService;
	

	// * SET
	// http://127.0.0.1:8000/pokingsearch?id=t1
	@GetMapping("/pokingList")
	public Object[] pokingList(@RequestParam("id") String memberId) throws AsyncException {
		if(memberId == null || memberId.length() == 0) {
			throw new AsyncException("NULL");
		}
		Util.sleep(0.1);
		System.out.println("/pokingList : " + memberId);
		System.out.println("// DB, INSTA, TAG");
		Object[] result = new Object[3];
		System.out.println("// STEP 1 : DB");
		try {
			result[0] = userService.getPokingList(memberId);
		} catch (Exception e) {
			throw new AsyncException("RDBMS(mySQL) 관련 오류");
		}
		return result;
	}

	
	// http://127.0.0.1:8000/kwdsearch?kwd=여름
	@GetMapping("/kwdsearch")
	public Object[] kwdSearch(@RequestParam String kwd) {
		if(kwd == null || kwd.length() == 0) {
			throw new AsyncException("NULL");
		}
		Util.sleep(0.1);
		System.out.println("/kwdsearch : " + kwd);
		System.out.println("// DB, INSTA, TAG");
		Object[] result = new Object[3];
		System.out.println("// STEP 1 : DB");
		try {
			result[0] = searchService.findPlaceByKwd(kwd);
		} catch (Exception e) {
			throw new AsyncException("RDBMS(mySQL) 관련 오류");
		}
		System.out.println("// STEP 2 : INSTA");
		try {
			result[1] = searchService.searchInstaByKwd(kwd);
		} catch (Exception e) {
			throw new AsyncException("Elastic Search (Insta) 관련 오류");
		}
		System.out.println("// STEP 3 : TAG");
		try {
			result[2] = searchService.getTagListByKwd(kwd);
		} catch (Exception e) {
			throw new AsyncException("Elastic Search (Tag) 관련 오류");
		}
		return result;
	}

	// http://127.0.0.1:8000/locsearch?loc=서울
	@GetMapping("/locsearch")
	public Object[] locSearch(@RequestParam String loc) {
		if(loc == null || loc.length() == 0) {
			throw new AsyncException("NULL");
		}
		Util.sleep(0.1);
		System.out.println("/locsearch : " + loc);
		System.out.println("// DB, INSTA, TAG");
		Object[] result = new Object[3];
		System.out.println("// STEP 1 : DB");
		try {
			result[0] = searchService.findPlaceByKwd(loc);
		} catch (Exception e) {
			throw new AsyncException("RDBMS(mySQL) 관련 오류");
		}
		System.out.println("// STEP 2 : INSTA");
		try {
			result[1] = searchService.searchInstaByLoc(loc);
		} catch (Exception e) {
			throw new AsyncException("Elastic Search (Insta) 관련 오류");
		}
		System.out.println("// STEP 3 : TAG");
		try {
			result[2] = searchService.getTagListByLoc(loc);
		} catch (Exception e) {
			throw new AsyncException("Elastic Search (Tag) 관련 오류");
		}
		return result;
	}

	// http://127.0.0.1:8000/kwdandlocsearch?kwd=데이트&loc=서울
	@GetMapping("/kwdandlocsearch")
	public Object[] kwdAndLocsearch(@RequestParam String kwd, @RequestParam String loc) {
		if(loc == null || loc.length() == 0 || kwd == null || kwd.length() == 0) {
			throw new AsyncException("NULL");
		}
		Util.sleep(0.1);
		System.out.println("/kwdandlocsearch : " + kwd + " " + loc);
		System.out.println("// DB, INSTA, TAG");
		Object[] result = new Object[3];
		System.out.println("// STEP 1 : DB");
		try {
			result[0] = searchService.findPlaceByKwd(kwd);
		} catch (Exception e) {
			throw new AsyncException("RDBMS(mySQL) 관련 오류");
		}
		System.out.println("// STEP 2 : INSTA");
		try {
			result[1] = searchService.searchInstaByLocAndKwd(loc, kwd);
		} catch (Exception e) {
			throw new AsyncException("Elastic Search (Insta) 관련 오류");
		}
		System.out.println("// STEP 3 : TAG");
		try {
			result[2] = searchService.getTagListByLocAndKwd(loc, kwd);
		} catch (Exception e) {
			throw new AsyncException("Elastic Search (Tag) 관련 오류");
		}
		return result;
	}

	// http://127.0.0.1:8000/idsearch?id=1012988
	@GetMapping("/idsearch")
	public Object[] idSearch(@RequestParam BigDecimal id) {
		if(id == null) {
			throw new AsyncException("NULL");
		}
		System.out.println("/idsearch : " + id);
		System.out.println("// HIT++");
		try {
			searchService.addPlaceHit(id);
		} catch (Exception e) {
			throw new AsyncException("RDBMS(mySQL) Place Hit 관련 오류");
		}
		System.out.println("// DB, INSTA, TAG");
		Object[] result = new Object[3];
		System.out.println("// STEP 1 : DB");
		try {
			result[0] = searchService.findPlaceById(id);
		} catch (Exception e) {
			throw new AsyncException("RDBMS(mySQL) 관련 오류");
		}
		// ID 별 위치 정보 처리
		String loc = searchService.findPlaceById(id).get().getAddress().split(" ")[0].replace("특별시", "").replace("광역시", "")
				.replace("특별자치도", "").replaceAll("\\S북도", "북").replaceAll("\\S남도", "남").replace("세종특별자치시", "충남");
		System.out.println(loc);
		System.out.println("// STEP 2 : INSTA");
		try {
			result[1] = searchService.searchInstaByLoc(loc);
		} catch (Exception e) {
			throw new AsyncException("Elastic Search (Insta) 관련 오류");
		}
		System.out.println("// STEP 3 : TAG");
		try {
			result[2] = searchService.getTagListByLoc(loc);
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
			result[0] = searchService.findPlaceByDistance(id, km);
		} catch (Exception e) {
			throw new AsyncException("RDBMS(mySQL) 관련 오류");
		}
		// ID 별 위치 정보 처리
		String loc = searchService.findPlaceById(id).get().getAddress().split(" ")[0].replace("특별시", "").replace("광역시", "")
				.replace("특별자치도", "").replaceAll("\\S북도", "북").replaceAll("\\S남도", "남").replace("세종특별자치시", "충남");
		System.out.println(loc);
		System.out.println("// STEP 2 : INSTA");
		try {
			result[1] = searchService.searchInstaByLoc(loc);
		} catch (Exception e) {
			throw new AsyncException("Elastic Search (Insta) 관련 오류");
		}
		System.out.println("// STEP 3 : TAG");
		try {
			result[2] = searchService.getTagListByLoc(loc);
		} catch (Exception e) {
			throw new AsyncException("Elastic Search (Tag) 관련 오류");
		}
		return result;
	}

	// http://127.0.0.1:8000/distandtypesearch?id=1012988&typeId=39&km=10
	@GetMapping("/distandtypesearch")
	public Object[] distAndTypeSearch(@RequestParam BigDecimal id, @RequestParam BigDecimal typeId,
			@RequestParam double km) {
		System.out.println("/distanceandtypesearch : " + id + " km : " + km);
		System.out.println("// DB, INSTA, TAG");
		Object[] result = new Object[3];
		System.out.println("// STEP 1 : DB");
		try {
			result[0] = searchService.findPlaceByDistance(id, typeId, km);
		} catch (Exception e) {
			throw new AsyncException("RDBMS(mySQL) 관련 오류");
		}
		// ID 별 위치 정보 처리
		String loc = searchService.findPlaceById(id).get().getAddress().split(" ")[0].replace("특별시", "").replace("광역시", "")
				.replace("특별자치도", "").replaceAll("\\S북도", "북").replaceAll("\\S남도", "남").replace("세종특별자치시", "충남");
		System.out.println(loc);
		System.out.println("// STEP 2 : INSTA");
		try {
			result[1] = searchService.searchInstaByLoc(loc);
		} catch (Exception e) {
			throw new AsyncException("Elastic Search (Insta) 관련 오류");
		}
		System.out.println("// STEP 3 : TAG");
		try {
			result[2] = searchService.getTagListByLoc(loc);
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
			return searchService.findPlaceByTypeId(typeId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AsyncException("ERROR");
		}
	}

	// http://127.0.0.1:8000/searchByKeyword?kwd=산
	@GetMapping("/searchByKeyword")
	public List<PlaceDTO> searchByKeyword(@RequestParam String kwd) throws AsyncException {
		System.out.println("/searchByKeyword : " + kwd);
		if (kwd.length() == 0) {
			throw new AsyncException("no search keyword");
		}
		try {
			return searchService.findPlaceByKwd(kwd);
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
			return searchService.findPlaceByDistance(id, distance);
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
			return searchService.findPlaceByDistance(id, typeId, distance);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AsyncException("ERROR");
		}
	}

	// http://127.0.0.1:8000/searchById?id=2360786
	@GetMapping("/searchById")
	public Optional<PlaceDTO> searchById(@RequestParam BigDecimal id) {
		System.out.println("/searchById");
		return searchService.findPlaceById(id);
	}

	// http://127.0.0.1:8000/hit?placeId=1012988
	@GetMapping("/hit")
	public String hit(@RequestParam BigDecimal placeId) {
		System.out.println("/hit");
		try {
			searchService.addPlaceHit(placeId);
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
			return "실행 시간 : " + searchService.getIds() + "ms";
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
			return "실행 시간 : " + searchService.getSocialData() + "ms";
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
			return searchService.searchInstaByKwd(kwd);
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
			return searchService.searchInstaByLoc(loc);
		} catch (AsyncException e) {
			throw new AsyncException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new AsyncException("ERROR");
		}
	}

	// http://127.0.0.1:8000/instaLocAndKwdSearch?loc=경남&kwd=핑크
	@GetMapping("/instaLocAndKwdSearch")
	public List<InstaPostDTO> instaLocAndKwdSearch(@RequestParam String loc, @RequestParam String kwd)
			throws AsyncException {
		System.out.println("/instaLocAndKwdSearch");
		try {
			return searchService.searchInstaByLocAndKwd(loc, kwd);
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
			return searchService.getTagListFromAll();
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
			return searchService.getTagListByKwd(kwd);
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
			return searchService.getTagListByLoc(loc);
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
			return searchService.getTagListByLocAndKwd(loc, kwd);
		} catch (AsyncException e) {
			throw new AsyncException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new AsyncException("ERROR");
		}
	}

	// * DB: DB내 존재하는 모든 typeId와 카운팅 횟수 return 로직
	@GetMapping("/countTypeId")
	public List<List<Object>> countTypeId() throws AsyncException {
		System.out.println("/countTypeId");
		try {
			return searchService.typeId();
		} catch (AsyncException e) {
			throw new AsyncException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new AsyncException("ERROR");
		}
	}

	// * DB: 키워드 입력 후 결과값 내 typeId와 카운팅 횟수 return 로직
	@GetMapping("/countTypeIdKwd")
	public List<List<Object>> countTypeIdKwd(@RequestParam("kwd") String kwd) throws AsyncException {
		System.out.println("/countTypeIdKwd:" + kwd);
		try {
			return searchService.typeIdKwd(kwd);
		} catch (AsyncException e) {
			throw new AsyncException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new AsyncException("ERROR");
		}
	}

	// * DB: 키워드&typeId 값 입력 후 결과값 return 로직
	// http://127.0.0.1:8000/kwdTypeIdIn?kwd=서울&typeId=12
	@GetMapping("/kwdTypeIdIn")
	public List<PlaceDTO> kwdTypeIdIn(@RequestParam BigDecimal typeId, @RequestParam String kwd) throws AsyncException {
		try {
			return searchService.kwdTypeIdIn(typeId, kwd);
		} catch (AsyncException e) {
			throw new AsyncException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new AsyncException("ERROR");
		}
	}

}