package tbr.model.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tbr.model.dto.PlaceDTO;

@Repository
public interface PlaceRepository extends CrudRepository<PlaceDTO, BigDecimal>{

	List<PlaceDTO> findPlaceByTypeId(BigDecimal typeId);
	// type id 검색
	
	List<PlaceDTO> findPlaceByNameContainingOrAddressContainingOrDescriptionContaining(String kwd1, String kwd2, String kwd3);
	// 키워드 검색 (이름, 주소, 설명)
}
