package tbr.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
import org.springframework.web.servlet.view.RedirectView;

import tbr.exception.AsyncException;
import tbr.exception.SyncException;
import tbr.model.dto.MemberDTO;
import tbr.model.dto.PlaceDTO;

import tbr.service.TBRUserService;

@CrossOrigin(origins = { "http://127.0.0.1:8000", "http://localhost:8000" })
@RestController
public class TBRUserController {
	@Autowired
	TBRUserService service;

	// * Poking	
	@GetMapping("/poking")
	public String poking(@RequestParam("id") String memberId, @RequestParam("place") BigDecimal placeId) throws AsyncException {
		try {
			return service.poking(memberId, placeId) ? "찜하기 추가 성공" : "이미 있는 찜하기";
		} catch (Exception e) {
			e.printStackTrace();
			new AsyncException("ERROR");
		}
		return "에러 발생";
	}
	@GetMapping("/unpoking")
	public String unPoking(@RequestParam("id") String memberId, @RequestParam("place") BigDecimal placeId) throws AsyncException {
		try {
			return service.unPoking(memberId, placeId) ? "찜하기 삭제 성공" : "찜하기 기록 없음";
		} catch (Exception e) {
			e.printStackTrace();
			new AsyncException("ERROR");
		}
		return "에러 발생";
	}

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
			} else if (service.loginMember(new MemberDTO(id, oldPw)) && service.updateMember(new MemberDTO(id, newPw))) {
				System.out.println("업데이트 성공");
			} else {
				System.out.println("업데이트 실패");
				throw new SyncException("update_failed");
			}
		return new RedirectView("privacy.html");
	}
	
	@PostMapping("/dropOutUser")
	public RedirectView dropOutUser(HttpServletRequest request,
			@RequestParam("pw") String pw) throws SyncException {
		System.out.println("/dropOutUser");
		String id = Arrays.stream(
				request.getCookies())
					.filter(v -> v.getName().equals("id"))
					.map(v -> v.getValue())
					.reduce("", ((x, y) -> x + y));
			if (id.length() == 0 || pw.length() == 0) {
				throw new SyncException("empty_space");
			} else if (service.loginMember(new MemberDTO(id, pw)) && service.deleteMember(id)) {
				System.out.println("탈퇴 성공");
				return new RedirectView("login.html");
			} else {
				System.out.println("탈퇴 실패");
				throw new SyncException("dropout_failed");
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
	public RedirectView loginAdmin(HttpServletResponse response, @RequestParam("id") String id,
			@RequestParam("pw") String pw) throws SyncException, Exception {
		if (id.length() == 0 || pw.length() == 0) {
			throw new SyncException("empty_space");
		} else if (service.loginAdmin(id, pw)) {
			System.out.println("로그인 성공");
			response.addCookie(new Cookie("id", id));
		} else {
			throw new SyncException("not_exist_member");
		}
		return new RedirectView("admin_main.html");
	}
	
	@GetMapping("/logoutAdmin")
	public RedirectView logoutAdmin(HttpServletResponse response, HttpServletRequest request) {
		Cookie c = null;
		String id = Arrays.stream(
						request.getCookies())
							.filter(v -> v.getName().equals("id"))
							.map(v -> v.getValue())
							.reduce("", ((x, y) -> x + y));
		if (id.equals("admin")) {
			System.out.println("관리자 로그아웃 성공");
			response.addCookie(new Cookie("msg", "logout_complete"));
			c = new Cookie("id", "");
			c.setMaxAge(0);
			response.addCookie(c);
		} else {
			System.out.println("관리자 로그인 기록 없음");
		}
		return new RedirectView("admin.html");
	}
	
	// 회원정보 조회(전체)
	@GetMapping("/getAllUser")
	public Iterable<MemberDTO> getAllUser() {
		try {
			return service.getAllMember();
		} catch (Exception e) {
			e.printStackTrace();
			new AsyncException("ERROR");
		}
		return null;
	}

	// http://127.0.0.1:8000/getAccount?id=ts1
	// 회원정보 조회(PK)
	@GetMapping("/getAccount")
	public Optional<MemberDTO> getAccount(@RequestParam("id") String id) {
		try {
			return service.getMember(id);
		} catch (Exception e) {
			e.printStackTrace();
			new AsyncException("ERROR");
		}
		return null;
	}

	// 회원정보 조회(Containing)
	@GetMapping("/searchAccount")
	public List<MemberDTO> searchAccount(@RequestParam("id") String id) {
		try {
			return service.searchMember(id);
		} catch (Exception e) {
			e.printStackTrace();
			new AsyncException("ERROR");
		}
		return null;
	}

	// 회원 삭제
	@GetMapping("/deleteAccount")
	public String deleteAccount(@RequestParam("id") String id) throws AsyncException {
		try {
			service.deleteMember(id);
		} catch (Exception e) {
			e.printStackTrace();
			new AsyncException("ERROR");
		}
		return "회원 삭제 성공";
	}

}