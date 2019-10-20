package tbr.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import tbr.exception.AsyncException;
import tbr.exception.SyncException;
import tbr.model.dto.MemberDTO;
import tbr.model.dto.PlaceDTO;
import tbr.service.TBRService;

@CrossOrigin(origins = { "http://127.0.0.1:8000", "http://localhost:8000" })
@RestController
public class TBRController {
	@Autowired
	TBRService service;

	// * LOGIN & OUT
	@PostMapping("/login")
	public RedirectView loginMember(HttpServletResponse response, @RequestParam("id") String id,
			@RequestParam("pw") String pw) throws SyncException {
		if (id.length() == 0 || pw.length() == 0) {
			throw new SyncException("empty_space");
		} else if (service.loginMember(new MemberDTO(id, pw))) {
			System.out.println("로그인 성공");
			response.addCookie(new Cookie("id", id));
		} else {
			throw new SyncException("not_exist_member");
		}
		return new RedirectView("menu.html");
	}

	@GetMapping("/logout")
	public RedirectView logoutMember(HttpServletResponse response, HttpServletRequest request) {
		Cookie c = null;
		String id = Arrays.stream(
						request.getCookies())
							.filter(v -> v.getName().equals("id"))
							.map(v -> v.getValue())
							.reduce("", ((x, y) -> x + y));
		if (service.checkMember(id)) {
			System.out.println("로그아웃 성공");
			response.addCookie(new Cookie("msg", "logout_complete"));
			c = new Cookie("id", "");
			c.setMaxAge(0);
			response.addCookie(c);
		} else {
			System.out.println("로그인 기록 없음");
		}
		return new RedirectView("login.html");
	}

	// * USER
	@PostMapping("/addUser")
	public RedirectView addUser(
			@RequestParam("id") String id, @RequestParam("pw") String pw) throws SyncException {
		System.out.println("/addUser");
		if (id.length() == 0 || pw.length() == 0) {
			throw new SyncException("empty_space");
		}
		if (service.addMember(new MemberDTO(id, pw))) {
			System.out.println("회원 가입 성공");
		} else {
			System.out.println("회원 가입 실패");
			throw new SyncException("already_exist_member");
		}
		return new RedirectView("login.html");
	}

	@PostMapping("/updateUser")
	public RedirectView updateUser(HttpServletRequest request,
			@RequestParam("oldpw") String oldPw, @RequestParam("newpw") String newPw) throws SyncException {
		System.out.println("/updateUser");
		String id = Arrays.stream(
				request.getCookies())
					.filter(v -> v.getName().equals("id"))
					.map(v -> v.getValue())
					.reduce("", ((x, y) -> x + y));
			if (id.length() == 0 || oldPw.length() == 0 || newPw.length() == 0) {
				throw new SyncException("empty_space");
			} else if (service.updateMember(new MemberDTO(id, newPw))) {
				System.out.println("업데이트 성공");
			} else {
				System.out.println("업데이트 실패");
				throw new SyncException("update_failed");
			}
		return new RedirectView("privacy.html");
	}

	// * SEARCH
	// http://127.0.0.1:8000/searchByType/typeId=38
	@GetMapping("/searchByType")
	public List<PlaceDTO> searchByType(@RequestParam BigDecimal typeId) throws AsyncException {
		System.out.println("/searchByType");
		try {
			return service.findPlaceByTypeId(typeId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AsyncException("ERROR");
		}
	}

	// http://127.0.0.1:8000/searchByKeyword?kwd=산
	@GetMapping("/searchByKeyword")
	public List<PlaceDTO> searchByKeyword(@RequestParam String kwd) throws AsyncException {
		System.out.println("/searchByKeyword : " + kwd);
		if(kwd.length() == 0) {
			throw new AsyncException("no search keyword");
		}
		try {
			return service.findPlaceByKwd(kwd);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AsyncException("ERROR");
		}
	}

	// http://127.0.0.1:8000/searchByDistanceAndType?id=319571&distance=10
	@GetMapping("/searchByDistance")
	public List<List<Object>> searchByDistance(@RequestParam BigDecimal id, @RequestParam double distance)
			throws AsyncException {
		System.out.println("/searchByDistance");
		try {
			return service.findPlaceByDistance(id, distance);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AsyncException("ERROR");
		}
	}
	
	// http://127.0.0.1:8000/searchByDistanceAndType?id=319571&typeId=12&distance=10
	@GetMapping("/searchByDistanceAndType")
	public List<List<Object>> searchByDistance(@RequestParam BigDecimal id, @RequestParam BigDecimal typeId,
			@RequestParam double distance) throws AsyncException {
		System.out.println("/searchByDistanceAndType");
		try {
			return service.findPlaceByDistance(id, typeId, distance);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AsyncException("ERROR");
		}
	}

	// * DB
	// http://127.0.0.1:8000/dataCollect
	// 최초 구동 시 spring.jpa.hibernate.ddl-auto=create 설정 확인
	// + mySQL UTF-8 관련 인코딩 문제 해결해야함
	@GetMapping("/dataCollect")
	public String dataCollect() throws AsyncException {
		System.out.println("/dataCollect");
		try {
			return "실행 시간 : " + service.getIds() + "ms";			
		} catch (Exception e) {
			e.printStackTrace();
			throw new AsyncException("ERROR");
		}
	}
	
	// * Handler (동기 기술로 구현되는 것들을 SyncException으로 받음)
	@ExceptionHandler(SyncException.class)
	public RedirectView handling(SyncException e, HttpServletResponse response) {
		System.out.println("ERROR 발생");
		response.addCookie(new Cookie("msg", e.getMessage()));
		return new RedirectView("error.html");
	}
	
	// * Admin
	@PostMapping("/loginAdmin")
	public ModelAndView loginAdmin(HttpServletResponse response,
			@RequestParam("id") String id,
			@RequestParam("pw") String pw) {
		String vn = "redirect:/admin.html";
		Cookie c, c2 = null;
		try {
			if(id.length() == 0 || pw.length() == 0) {
				c = new Cookie("msg", "empty_space");
				throw new Exception("입력하지 않은 영역"); // 활용하면 로그인/가입 시 길이 검증도 가능
			}
			else if (service.loginAdmin(new MemberDTO(id, pw))) {
				c = new Cookie("msg", "login_complete");
				c2 = new Cookie("id", id);
				System.out.println("로그인 성공");
				vn = "redirect:/admin_main.html";
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
	
	// 회원정보 조회(전체)
	@GetMapping("/getAllUser")
	public Iterable<MemberDTO> getAllUser() {
		return service.getAllMembers();
	}

	// 회원정보 조회(Containing)
	@GetMapping("/searchAccount")
	public List<MemberDTO> searchAccount(@RequestParam("id") String id) {
		return service.findMemberById(id);
	}

	// 회원 삭제
	@GetMapping("/deleteAccount")
	public String deleteAccount(@RequestParam("id") String id) {
		String result = "오류 발생";
		try {
			service.deleteId(id);
			result = "회원 삭제 성공";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}