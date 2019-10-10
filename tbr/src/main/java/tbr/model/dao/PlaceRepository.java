package tbr.model.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import tbr.model.dto.Place;
import tbr.model.dto.User;

public interface PlaceRepository {

	public interface StockRepository extends CrudRepository<Place, String>{
		
		public List<User> findUserById(String id);
		public List<Place> findPlaceByNameContaining(String nameData);
		public List<Place> findPlaceByPhonenumber(String phonenumberData);
		public List<Place> findPlaceByHomepage(String homepageData);
		public List<Place> findPlaceByMapx(BigDecimal mapxData);
		public List<Place> findPlaceByMapy(BigDecimal mapyData);
		public List<Place> findPlaceByAddress(String adressData);
		public List<Place> findPlaceByImage(String imageData);
		public List<Place> findPlaceByTypeid(BigDecimal typeidData);
		
	}
}
