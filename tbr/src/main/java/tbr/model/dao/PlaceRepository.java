package tbr.model.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tbr.model.dto.PlaceDTO;

@Repository
public interface PlaceRepository extends CrudRepository<PlaceDTO, BigDecimal>{

	List<PlaceDTO> findPlaceByTypeId(BigDecimal typeId); // type id 검색
	
	List<PlaceDTO> findPlaceByAddressContaining(String location); // 주소 검색 (앞에 시.도)
	
	List<PlaceDTO> findPlaceByNameContaining(String name);
	
	List<PlaceDTO> findPlaceByNameContainingOrAddressContainingOrDescriptionContaining(String kwd1, String kwd2, String kwd3);
}
