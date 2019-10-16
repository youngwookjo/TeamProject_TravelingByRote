package tbr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import tbr.model.dto.TbrUser;
import tbr.service.PlaceService;
import tbr.service.UserService;

@CrossOrigin(origins = { "http://127.0.0.1:8000", "http://localhost:8000" })
@RestController
public class TestController {
	@Autowired
	UserService userService;
	@Autowired
	PlaceService placeService;

	// 1. User : 로그인
	@PostMapping("/loginUser")
	public ModelAndView loginUser(@RequestParam("id") String id, @RequestParam("pw") String pw, ModelAndView mv) {
		TbrUser userInfo = new TbrUser(id, pw);
		String result = "로그인 실패";
		boolean flag = userService.login(userInfo);
		if (flag == true) {
			result = "로그인 성공";
		}
		System.out.println(result);
		mv.setViewName("../static/index.html");
		mv.addObject("userInfo", userInfo);

		return mv;
	}

	// 2. User : 회원가입
	@PostMapping("/addUser")
	public ModelAndView addUser(@RequestParam("id") String id, @RequestParam("pw") String pw, ModelAndView mv) {
		TbrUser userInfo = new TbrUser(id, pw);
		String result = "가입 실패";
		boolean flag = userService.add(userInfo);
		if (flag == true) {
			result = "가입 성공";
		}
		System.out.println(result);
		mv.setViewName("../static/login.html");
		mv.addObject("userInfo", userInfo);
		return mv;
	}

	// 3. User : 개인정보 조회(아직 미구현)
	@PostMapping("/getUser")
	public TbrUser getUser(@RequestParam("id") String id) {
		TbrUser result = userService.get(id);
		System.out.println(result.toString());
		return result;
	}

	// 4. User : 회원 비밀번호 수정
	@PostMapping("/updateUser")
	public ModelAndView updateUser(@RequestParam("id") String id, @RequestParam("pw") String pw, ModelAndView mv) {
		try {
			TbrUser userInfo = new TbrUser(id, pw);
			userService.update(userInfo);
			mv.setViewName("../static/about.html");
			mv.addObject("userInfo", userInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;

	}

	// 5. User : 회원 탈퇴 (아직 미구현)
	@PostMapping("/deleteUser")
	// http://localhost:8000/deleteUser?id=tester1&pw=secretkey1
	public ModelAndView deleteUser(@RequestParam("id") String id, @RequestParam("pw") String pw, ModelAndView mv) {
		TbrUser userInfo = new TbrUser(id, pw);
		try {
			userService.delete(userInfo);
			mv.setViewName("../static/login.html");
			mv.addObject("userInfo", userInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("회원 탈퇴 완료");
		return mv;
	}

	// 6. Admin : 회원정보 조회(전체)
	@GetMapping("/getAllUser")
	public Iterable<TbrUser> getAllUser() {
		Iterable<TbrUser> list = userService.getAll();
		System.out.println(list.toString());
		return list;
	}

	// 7. Admin : 회원정보 조회(Containing)
	@GetMapping("/searchAccount")
	public List<TbrUser> searchAccount(@RequestParam("id") String id) {
		List<TbrUser> list = userService.searchId(id);
		System.out.println(list.toString());
		return list;
	}

	// 8. admin page : 회원 삭제
	@GetMapping("/deleteAccount")
	// http://localhost:8000/deleteAccount?id=tester1
	public String deleteAccount(@RequestParam("id") String id) {
		try {
			userService.deleteId(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("회원 삭제 완료");
		return "회원 삭제 완료";
	}

	@PostMapping("/loginAdmin")
	public ModelAndView loginAdmin(@RequestParam("id") String id, @RequestParam("pw") String pw, ModelAndView mv) {
		TbrUser userInfo = new TbrUser(id, pw);
		String result = "어드민 로그인 실패";
		boolean flag = userService.adlogin(userInfo);
		if (flag == true) {
			result = "어드민 로그인 성공";
		}
		System.out.println(result);
		mv.setViewName("../static/admain.html");
		mv.addObject("userInfo", userInfo);

		return mv;
	}
}
