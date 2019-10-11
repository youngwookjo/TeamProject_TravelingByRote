package tbr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tbr.model.dao.TbrUserRepository;

@Service
public class UserService {
	@Autowired
	TbrUserRepository userRepository;
}
