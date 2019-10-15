package tbr.model.dao;

import java.math.BigDecimal;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tbr.model.dto.Place;

@Repository
public interface PlaceRepository extends CrudRepository<Place, BigDecimal>{
	
}
