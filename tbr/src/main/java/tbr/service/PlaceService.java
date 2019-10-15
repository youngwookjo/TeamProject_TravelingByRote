package tbr.service;

import java.math.BigDecimal;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import tbr.model.dao.PlaceRepository;
import tbr.model.dto.Place;


@Service
public class PlaceService {
	@Autowired
	PlaceRepository placeRepo;

	
	//쓸 경우는 안보임 테스트용? 모든 여행지 검색
//	@GetMapping("/placeSearchAll")
//	public Iterable<Place> stockSearchAll() {
//		return placeRepo.findAll();
//	}
	//이름값에 해당하는 장소만 반환
	public Place placeSearchOne(String name) {
		return placeRepo.findPlaceByNameEquals(name);
	}
	
	//이름으로 비슷한 장소 검색
	public List<Place> placeSearchByName(String name) {
		return placeRepo.findPlaceByNameContainingOrderByTotalDesc(name);
	}
	
	//타입 ID 검색
	public List<Place> placeSearchByTypeId(BigDecimal typeid) {
		return placeRepo.findPlaceByTypeIdEqualsOrderByTotalDesc(typeid);
	}
	
	//이름으로 검색후 지우기
	public String placeDelete(Place place) {
		placeRepo.delete(place);
		return "place delete";
	}
	
	
}
