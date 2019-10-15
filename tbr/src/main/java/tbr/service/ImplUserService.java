package tbr.service;

import java.util.Optional;

import tbr.model.dto.TbrUser;

public interface ImplUserService{
	
	public boolean login(TbrUser user); //회원확인(로그인)
	public boolean add(TbrUser user); //회원가입, 비번 변경
	public TbrUser get(String id); //회원조회
	public Iterable<TbrUser> getAll(); //전체회원 조회
	public void update(TbrUser user); //비번 변경
	public void delete(TbrUser user); //회원탈퇴

}
