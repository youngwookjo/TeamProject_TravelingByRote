package tbr.model.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tbr.model.dto.PlaceDTO;

@Repository
public interface PlaceRepository extends CrudRepository<PlaceDTO, BigDecimal>{

	List<PlaceDTO> findPlaceByTypeId(BigDecimal typeId);
	// type id 검색
	
	List<PlaceDTO> findPlaceByNameContainingOrAddressContainingOrDescriptionContaining(String kwd1, String kwd2, String kwd3);
	// 키워드 검색 (이름, 주소, 설명)
	@Query(value = "SELECT name,"
	+"( 6371 * acos( cos( radians((select lat from place where name = :name)) ) * cos( radians( lat ) )"
	+"* cos( radians(lon) - radians((select lon from place where name = :name)) )"
	+"+ sin( radians((select lat from place where name = :name)) ) * sin( radians( lat ) ) ) ) AS distance"
	+"FROM place "
	+"HAVING distance <= 50.0 "
	+"ORDER BY distance"
	+"LIMIT 0,300;", nativeQuery = true)
		List<PlaceDTO> findPlaceByNameParamsNative(
	  @Param("name") String name);
}
