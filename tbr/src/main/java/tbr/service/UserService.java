package tbr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tbr.model.dao.MemberRepository;

@Service
public class UserService {
	@Autowired
	MemberRepository userRepository;
}
