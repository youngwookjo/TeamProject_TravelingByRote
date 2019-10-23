package tbr.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tbr.model.dao.MemberRepository;
import tbr.model.dto.MemberDTO;

@Service
public class TBRUserService {
	@Autowired
	MemberRepository memberRepo;

	// Application Properties 활용법
	// http://progtrend.blogspot.com/2017/07/spring-boot-applicationproperties.html
	@Value("${admin.id}")
	String adminId;
	@Value("${admin.pw}")
	String adminPw;
	
	// * Member
	public boolean checkMember(String id) {
		return memberRepo.existsById(id);
	}

	public boolean loginMember(MemberDTO m) {
		return checkMember(m.getId()) ? memberRepo.findById(m.getId()).get().getPw().equals(m.getPw()) : false;
	}

	public boolean addMember(MemberDTO m) {
		if (!checkMember(m.getId())) {
			memberRepo.save(m);
			return true;
		}
		return false;
	}

	public boolean updateMember(MemberDTO m) {
		if (checkMember(m.getId())) {
			memberRepo.findById(m.getId()).get().setPw(m.getPw());
			memberRepo.save(m);
			return true;
		} else {
			return false;
		}
	}

	// * Admin
	public boolean loginAdmin(String id, String pw) {
		return id.equals(adminId) && pw.equals(adminPw) ? true : false;
	}
	
	public Iterable<MemberDTO> getAllMember() {
		return memberRepo.findAll();
	}
	
	public List<MemberDTO> searchMember(String id) {
		return memberRepo.findMemberByIdContaining(id);
	}
	
	public boolean deleteMember(String id) {
		memberRepo.deleteById(id);
		return true;
	}

}