package tbr.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import tbr.model.dto.MemberDTO;
import tbr.model.dto.PlaceDTO;
import tbr.service.TBRService;

@CrossOrigin(origins= {"http://127.0.0.1:8000", "http://localhost:8000"})
@RestController
public class TBRController {
	// 에러 핸들러 & 에러 페이지 개발 필요
	// 유지 관리의 효율성을 위해 User와 Search 컨트롤러 및 서비스의 분리 필요성 고려해봐야...

	@Autowired
	TBRService service;

	// * LOGIN & OUT
	// https://yeon27.tistory.com/66 (redirect <-> forward)
	@PostMapping("/login")
	public ModelAndView loginMember(HttpServletResponse response,
			@RequestParam("id") String id,
			@RequestParam("pw") String pw) {
		String vn = "redirect:/login.html";
		Cookie c, c2 = null;
		try {
			if(id.length() == 0 || pw.length() == 0) {
				c = new Cookie("msg", "empty_space");
				throw new Exception("입력하지 않은 영역"); // 활용하면 로그인/가입 시 길이 검증도 가능
			}
			else if (service.loginMember(new MemberDTO(id, pw))) {
				c = new Cookie("msg", "login_complete");
				c2 = new Cookie("id", id);
// 				HttpSession session 를 argument에 추가
//				session.setAttribute("login", true);
				System.out.println("로그인 성공");
				vn = "redirect:/menu.html";
			} else {
				c = new Cookie("msg", "not_exist_member");
				System.out.println("로그인 실패 : 존재하지 않는 멤버");
			}
		} catch (Exception e) {
			c = new Cookie("msg", "unexpected_error");
			System.out.println("에러 발생");
			e.printStackTrace();
		}
		if(c != null) {
			response.addCookie(c);			
		}
		if(c2 != null) {			
			response.addCookie(c2);
		}
		return new ModelAndView(vn);
	}
	
	@GetMapping("/logout")
	public ModelAndView logoutMember(HttpServletResponse response, HttpServletRequest request) {
		String vn = "redirect:/login.html";
		Cookie c, c2 = null;
		try {
			// reduce 사용법 : http://iloveulhj.github.io/posts/java/java-stream-api.html
			if (service.checkMember(
					Arrays.stream(request.getCookies())
						.filter(v -> v.getName().equals("id"))
						.map(v -> v.getValue())
						.reduce("", ((x, y) -> x + y)) // id라는 key를 가진 cookie값을 return 받기 위해 (신문법으로 만)
					)) {
//			if ((boolean) session.getAttribute("login")) {
//				status.setComplete(); (세션 검증해서 세션 초기화 하는 방식)
				// 로그인할 때마다 key 생성해주는 방식으로 비동기 하에서 로그인 검증?
				c = new Cookie("msg", "logout_complete");
				c2 = new Cookie("id", "");
				System.out.println("로그아웃 성공");
			} else {
				c = new Cookie("msg", "not_login_record");
				System.out.println("로그인 기록 없음");
			}
		} catch (Exception e) {
			c = new Cookie("msg", "unexpected_error");
			System.out.println("에러 발생");
			e.printStackTrace();
		}
		if(c != null) {
			response.addCookie(c);			
		}
		if(c2 != null) {			
			response.addCookie(c2);
		}
		return new ModelAndView(vn);
	}
	
	// * USER
	@PostMapping("/addUser")
	public ModelAndView addUser(HttpSession session, HttpServletResponse response,
			@RequestParam("id") String id, @RequestParam("pw") String pw) {
		Cookie c = null;
		String vn = "redirect:/join.html";
		try {
			if(id.length() == 0 || pw.length() == 0) {
				c = new Cookie("msg", "empty_space");
				throw new Exception("입력하지 않은 영역"); // 활용하면 로그인/가입 시 길이 검증도 가능
			}
			if(service.addMember(new MemberDTO(id, pw))) {
				c = new Cookie("msg", "join_complete");
				System.out.println("회원 가입 성공");
				vn = "redirect:/login.html";
			} else {
				c = new Cookie("msg", "already_exist_member");				
				System.out.println("회원 가입 실패");
			}
		} catch (Exception e) {
			if (c == null) {
				c = new Cookie("msg", "unexpected_error");					
			}
			e.printStackTrace();
		} 
		if(c != null) {
			response.addCookie(c);
		}
		return new ModelAndView(vn);
	}
	
	// * SEARCH
	// http://127.0.0.1:8000/type_search/38
	@GetMapping("/searchByType")
	public List<PlaceDTO> searchByType(@PathVariable String typeId) {
		return service.findPlaceByTypeId(typeId);
	}

	// http://127.0.0.1:8000/kwd_search?kwd=산
	@GetMapping("/searchByKeyword")
	public List<PlaceDTO> searchByKeyword(@RequestParam String kwd) {
		return service.findPlaceByKwd(kwd);
	}

	// http://127.0.0.1:8000/data_collect
	@GetMapping("/dataCollect")
	public String dataCollect() {
		return "실행 시간 : " + service.getIds() + "ms";
	}
	// http://127.0.0.1:8000/searchDistance?name=㈜강원심층수
	@GetMapping("/searchDistance")
	public List<PlaceDTO> searchDistance(@RequestParam String name){
		System.out.println(name);
		return service.findPlaceByPlaceNameParams(name);
	}
}
