package tbr.model.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tbr.model.dto.Place;

@Repository
public interface PlaceRepository extends CrudRepository<Place, BigDecimal>{
	
	public Place findPlaceByNameEquals(String name);
	public List<Place> findPlaceByNameContainingOrderByTotalDesc(String name);
	public List<Place> findPlaceByTypeIdEqualsOrderByTotalDesc(BigDecimal typeid);
	
}
