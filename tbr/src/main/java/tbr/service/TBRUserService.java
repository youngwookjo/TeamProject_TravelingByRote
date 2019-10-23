package tbr.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tbr.model.dao.MemberRepository;
import tbr.model.dao.PlaceRepository;
import tbr.model.dto.MemberDTO;
import tbr.model.dto.PlaceDTO;

@Service
public class TBRUserService {
	@Autowired
	MemberRepository memberRepo;
	@Autowired
	PlaceRepository placeRepo;

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
			m.setCreateDate(new Date());
			memberRepo.save(m);
			return true;
		}
		return false;
	}

	public boolean updateMember(MemberDTO m) {
		if (checkMember(m.getId())) {
			m.setCreateDate(memberRepo.findById(m.getId()).get().getCreateDate());
			m.setLastModifiedDate(new Date());
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

	public boolean poking(String memberId, BigDecimal placeId) {
		MemberDTO m = memberRepo.findById(memberId).get();
		boolean b = m.addPlace(placeRepo.findById(placeId).get());
		memberRepo.save(m);
		return b;
	}
	
	public boolean unPoking(String memberId, BigDecimal placeId) {
		MemberDTO m = memberRepo.findById(memberId).get();
		boolean b = m.removePlace(placeRepo.findById(placeId).get());
		memberRepo.save(m);
		return b;
	}
	
	public List<PlaceDTO> getPokingList(String memberId) {
		return memberRepo.findById(memberId).get().getPlaces();
	}

	public Optional<MemberDTO> getMember(String id) {
		return memberRepo.findById(id);
	}
}