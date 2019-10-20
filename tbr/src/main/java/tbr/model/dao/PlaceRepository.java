package tbr.model.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tbr.model.dto.PlaceDTO;

@Repository
public interface PlaceRepository extends CrudRepository<PlaceDTO, BigDecimal>{
	final String DISTANCE = "select id, distance, type_id\r\n" + 
			"from (\r\n" + 
			"	select id, type_id, ( 6371 * acos( cos( radians((select lat from place where id = :id)) )\r\n" + 
			"		* cos( radians( lat ) ) * cos( radians(lon) - radians((select lon from place where id = :id)) )\r\n" + 
			"		+ sin( radians((select lat from place where id = :id)) ) * sin( radians( lat ) ) ) ) as distance\r\n" + 
			"		from place\r\n" + 
			"		where type_id = :typeId\r\n" + 
			"	  )dt\r\n" + 
			"where distance < :distance and distance > 0\r\n" + 
			"order by distance";
	final String DISTANCEALL ="select id, distance, type_id\r\n" + 
			"from (\r\n" + 
			"	select id, type_id, ( 6371 * acos( cos( radians((select lat from place where id = :id)) )\r\n" + 
			"		* cos( radians( lat ) ) * cos( radians(lon) - radians((select lon from place where id = :id)) )\r\n" + 
			"		+ sin( radians((select lat from place where id = :id)) ) * sin( radians( lat ) ) ) ) as distance\r\n" + 
			"		from place\r\n" + 
			"	  )dt\r\n" + 
			"where distance < :distance and distance > 0\r\n" + 
			"order by distance";
	
	// type id 검색
	List<PlaceDTO> findPlaceByTypeId(BigDecimal typeId);
	
	// 키워드 검색 (이름, 주소, 설명)
	List<PlaceDTO> findPlaceByNameContainingOrAddressContainingOrDescriptionContaining(String kwd1, String kwd2, String kwd3);
	
	// JPA를 통해 특정 컬럼 조회 : http://blog.naver.com/idrukawa/220940108211
	// SPRING DATA + JPQL, named parmeter : https://www.baeldung.com/spring-data-jpa-query
	// 거리 검색 (type 구분)
	@Query(value = DISTANCE, nativeQuery = true)
	List<Object[]> findPlaceByDistance(
			@Param("id") BigDecimal id,
			@Param("typeId") BigDecimal typeId,
			@Param("distance") double distance);

	// 거리 검색 (type 구분 없이)
	@Query(value = DISTANCEALL, nativeQuery = true)
	List<Object[]> findPlaceByDistance(
			@Param("id") BigDecimal id,
			@Param("distance") double distance);

}
