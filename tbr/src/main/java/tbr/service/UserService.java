package tbr.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tbr.model.dao.TbrUserRepository;
import tbr.model.dto.TbrUser;

@Service
public class UserService implements ImplUserService {
	@Autowired
	TbrUserRepository userRepository;

	@Override
	public boolean login(TbrUser user) {
		System.out.println("====login in service====");
		boolean flag = false;
		TbrUser userInfo = userRepository.findTbrUserByIdEquals(user.getId()).get(0); 	
        if (user.getPw().equals(userInfo.getPw())) {
        	flag = true;
        }
        return flag;
	}
	
	@Override
	public boolean adlogin(TbrUser user) {
		System.out.println("====admin login in service====");
		boolean flag = false;
		TbrUser userInfo = userRepository.findTbrUserByIdEquals(user.getId()).get(0); 	
        if (user.getPw().equals(userInfo.getPw())) {
        	flag = true;
        }
        return flag;
	}
	
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
	public List<TbrUser> searchId(String id) {
		System.out.println("====searchId in service====");
		return userRepository.findTbrUserByIdContaining(id);
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
			v.setId(user.getId());
			v.setPw(user.getPw());
			userRepository.save(v);
		});
	}
	
	
	@Override
	public void delete(TbrUser user) {
		System.out.println("====delete in service====");
		userRepository.delete(user);
	}
	
	@Override
	public void deleteId(String id) {
		System.out.println("====deleteById in service====");
		userRepository.deleteById(id);
	}

}