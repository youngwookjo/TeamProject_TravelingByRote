package tbr.service;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import tbr.model.dao.TbrUserRepository;
import tbr.model.dto.TbrUser;

@Service
public class UserService implements ImplUserService {
	@Autowired
	TbrUserRepository userRepository;

	@Override
	public boolean add(TbrUser user) {
		System.out.println("====add in service====");
		boolean flag = false;
		List<TbrUser> list = userRepository.findTbrUserByIdEquals(user.getId()); 	
        if (list.size() == 0) {
        	userRepository.save(user);
        	flag = true;
        }
        return flag;
	}
	
	@Override
	public TbrUser get(String id) {
		System.out.println("====get in service====");
		return userRepository.findTbrUserByIdEquals(id).get(0);
	}
	
	@Override
	public Iterable<TbrUser> getAll(){
		System.out.println("====getAll in service====");
		return userRepository.findAll();
	}
	
	@Override
	public void update(TbrUser user) {
		System.out.println("====update in service====");
		userRepository.findTbrUserByIdEquals(user.getId()).forEach(v -> {
			v.setPw(user.getPw());
			userRepository.save(v);
		});
	}
	
	
	@Override
	public void delete(TbrUser user) {
		System.out.println("====delete in service====");
		userRepository.delete(user);
	}

}