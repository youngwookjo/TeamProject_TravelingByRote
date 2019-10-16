package tbr.service;

import java.util.List;

import tbr.model.dto.TbrUser;

public interface ImplUserService{
	
	public boolean login(TbrUser user); //회원확인(로그인)
	public boolean adlogin(TbrUser user); //어드민로그인
	public boolean add(TbrUser user); //회원가입, 비번 변경
	public List<TbrUser> searchId(String id); //회원검색(Admin용)
	public TbrUser get(String id); //회원조회
	public Iterable<TbrUser> getAll(); //전체회원 조회
	public void update(TbrUser user); //비번 변경
	public void delete(TbrUser user); //회원탈퇴
	public void deleteId(String id); //회원삭제(Admin용)

}
