package tbr.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tbr.model.dto.TbrUser;
import tbr.service.PlaceService;
import tbr.service.UserService;

@RestController
public class TestController {
	@Autowired
	UserService userService;
	@Autowired
	PlaceService placeService;
	
	//1. 회원가입
	//http://localhost:8000/addUser?id=tester1&pw=secretkey1
	//http://localhost:8000/addUser?id=tester2&pw=secretkey2
	@GetMapping("/addUser")
	public String addUser(@RequestParam("id") String id, @RequestParam("pw") String pw){
		TbrUser info = new TbrUser(id, pw);
		String result = "가입 실패";
		boolean flag = userService.add(info);
        if (flag == true) {
        	result = "가입 성공";
        }
        System.out.println(result);
        return result;
	}
	
	//2. 회원정보 조회(개별)
	//http://localhost:8000/getUser/tester1
	//http://localhost:8000/getUser/tester3
	@GetMapping("/getUser/{id}")
	public String getUser(@PathVariable("id") String id){
		TbrUser info = userService.get(id);
		String result = info.toString();
		System.out.println(result);
		return result;
	}

	//3. 회원정보 조회(전체)
	//http://localhost:8000/getAllUser
	@GetMapping("/getAllUser")
	public String getAllUser() {
		Iterable<TbrUser> list = userService.getAll();
		System.out.println(list.toString());
		return "회원명부 넘길 주소값";
	}
	//4. 회원 pw 수정(addUser와 동일)
	//http://localhost:8000/updateUser?id=tester2&pw=secretrevised
	@GetMapping("/updateUser")
	public String updateUser(@RequestParam("id") String id, @RequestParam("pw") String pw) {
		try {
			userService.update(new TbrUser(id, pw));
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("비밀번호 수정 완료");
		return "비밀번호 수정 완료";
	
	}
	
	//5. 회원 탈퇴
	@GetMapping("/deleteUser")
	//http://localhost:8000/deleteUser?id=tester1&pw=secretkey1
	public String deleteUser(@RequestParam("id") String id, @RequestParam("pw") String pw) {
		TbrUser info = new TbrUser(id, pw);
		try {
			userService.delete(info);
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("회원 탈퇴 완료");
        return "회원 탈퇴 완료";
	}
}
