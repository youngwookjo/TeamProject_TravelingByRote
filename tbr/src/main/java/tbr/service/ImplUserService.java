package tbr.service;

import java.util.Optional;

import tbr.model.dto.TbrUser;

public interface ImplUserService{
	
	public boolean add(TbrUser user); //회원가입, 비번 변경
	public TbrUser get(String id); //회원조회
	public Iterable<TbrUser> getAll(); //전체회원 조회
	public void update(TbrUser user); //비번 변경
	public void delete(TbrUser user); //회원탈퇴

}
