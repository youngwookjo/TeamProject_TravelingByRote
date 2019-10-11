package tbr.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import tbr.model.dto.TbrUser;
import tbr.service.PlaceService;
import tbr.service.UserService;

@Controller
public class TestController {
	@Autowired
	UserService userService;
	@Autowired
	PlaceService placeService;
	
	//1. 회원가입
	public String addUser(String id, String pw){
		TbrUser info = new TbrUser(id, pw);
		String result = "가입 실패";
		boolean flag = userService.add(info);
        if (flag == true) {
        	result = "가입 성공";
        }
        return result;
	}
	
	//2. 회원정보 조회(개별)
	public String getUser(String id){
		TbrUser info = userService.get(id);
		String result = info.toString();
		return result;
	}

	//3. 회원정보 조회(전체)
	public String getAllUser() {
		ModelAndView model = new ModelAndView();
		Iterable<TbrUser> list = userService.getAll();
		model.addObject("AllUser", list);
		return "회원명부 넘길 주소값";
	}
	//4. 회원 pw 수정(addUser와 동일)
	public String updateUser(String id, String pw) {
		TbrUser info = new TbrUser(id, pw);
		try {
			userService.update(info);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return "비밀번호 수정 완료";
	
	}
	
	//5. 회원 탈퇴
	public String deleteUser(String id, String pw) {
		TbrUser info = new TbrUser(id, pw);
		try {
			userService.delete(info);
		}catch(Exception e) {
			e.printStackTrace();
		}
        return "회원 탈퇴 완료";
	}
}
