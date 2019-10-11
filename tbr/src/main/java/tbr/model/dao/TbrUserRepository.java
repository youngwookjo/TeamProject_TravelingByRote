package tbr.model.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tbr.model.dto.TbrUser;

@Repository
public interface TbrUserRepository extends CrudRepository<TbrUser, String> {
	List<TbrUser> findTbrUserByIdEquals(String id);
	List<TbrUser> findTbrUserByIdContaining(String id);
}
