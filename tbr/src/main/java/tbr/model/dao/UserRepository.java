package tbr.model.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import tbr.model.dto.User;

public interface UserRepository {
public interface StockRepository extends CrudRepository<User, String>{
		
		public List<User> findUserById(String id);
		public List<User> findUserByWishlistContaining(String wishlist);
	}

}
