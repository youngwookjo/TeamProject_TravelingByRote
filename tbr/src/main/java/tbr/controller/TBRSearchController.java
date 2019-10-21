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
import tbr.service.TBRSearchService;

@CrossOrigin(origins = { "http://127.0.0.1:8000", "http://localhost:8000" })
@RestController
public class TBRSearchController {
	@Autowired
	TBRSearchService service;

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
			return service.getSearchHit(kwd);
		} catch (AsyncException e) {
			throw new AsyncException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new AsyncException("ERROR");
		}
	}

}