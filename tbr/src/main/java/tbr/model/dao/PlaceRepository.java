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
	final String DISTANCEQUERY =
			"select id, distance, type_id\r\n" + 
			"from (\r\n" + 
			"	select id, type_id, ( 6371 * acos( cos( radians((select lat from place where id = :id)) )\r\n" + 
			"		* cos( radians( lat ) ) * cos( radians(lon) - radians((select lon from place where id = :id)) )\r\n" + 
			"		+ sin( radians((select lat from place where id = :id)) ) * sin( radians( lat ) ) ) ) as distance\r\n" + 
			"		from place\r\n" + 
			"		where type_id = :typeId\r\n" + 
			"	  )dt\r\n" + 
			"where distance < :distance and distance > 0\r\n" + 
			"order by distance";

	List<PlaceDTO> findPlaceByTypeId(BigDecimal typeId);
	// type id 검색
	
	List<PlaceDTO> findPlaceByNameContainingOrAddressContainingOrDescriptionContaining(String kwd1, String kwd2, String kwd3);
	// 키워드 검색 (이름, 주소, 설명)
	
	// JPA를 통해 특정 컬럼 조회 : http://blog.naver.com/idrukawa/220940108211
	// SPRING DATA + JPQL, named parmeter : https://www.baeldung.com/spring-data-jpa-query
	@Query(value=DISTANCEQUERY, nativeQuery=true)
	List<Object[]> findPlaceByDistance(
			@Param("id") BigDecimal id,
			@Param("typeId") String typeId,
			@Param("distance") double distance);
	// 거리 검색
	
	/*
		@Query("SELECT u FROM User u WHERE u.status = :status and u.name = :name")
		User findUserByStatusAndNameNamedParams(
		@Param("status") Integer status, 
		@Param("name") String name);
	 */
	
	/* 
	 * select id, distance
		from (select id, ( 6371 * acos( cos( radians((select lat from place where id = 319571)) ) * cos( radians( lat ) )
          * cos( radians(lon) - radians((select lon from place where id = 319571)) )
          + sin( radians((select lat from place where id = 319571)) ) * sin( radians( lat ) ) ) ) as distance
          from place)dt
		where distance < 10 and distance > 0
		order by distance;
	 */
	
/*
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
*/

	
//	Optional<PlaceDTO> findPlaceById(BigDecimal id);
//	// id 검색
//	
//	List<PlaceDTO> findPlaceByAddressContaining(String shigu);
//	// 시, 구 정보로 장소 검색

}
