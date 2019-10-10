package tbr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import tbr.model.dao.UserRepository;
import tbr.model.dto.User;
//미완성
@RestController
public class Controller {

	@Autowired
	private UserRepository UserRepo;

	// insert/update
	@GetMapping("/create")
	public String createUser(String id, String pw, String wishlist) {
		UserRepo.save(new User(id, pw, wishlist));
		return "추가 완료";
	}

	// =======================================================

	// select
	@GetMapping("/search")
	public User searchUser(String id) {
		return UserRepo.findById(id).get();
	}

	// selectAll
	@GetMapping("/getAll")
	public Iterable<User> searchAllUser() {
		return UserRepo.findAll();
	}

	// selectWishlist
	@GetMapping("/wishlist")
	public Iterable<User> searchContainUser(String wishlist) {
		return UserRepo.findUserByWishlistContaining(wishlist);
	}
	
	// selectCount
	@GetMapping("/countwishlist")
	public long CountWishlist(Integer wishlist) {
		return UserRepo.countByWishlist(wishlist);
	}

	// =======================================================

	// delete
	@GetMapping("/delete")
	public String deleteUser(String name) {
		UserRepo.delete(UserRepo.findById(id).get());
		return "삭제 완료";
	}

	// deleteAll
	@GetMapping("/removeAll")
	public String deleteAllFruits(String name) {
		UserRepo.deleteAll();
		return "전체 삭제 완료";
	}
//***********************************user
}

