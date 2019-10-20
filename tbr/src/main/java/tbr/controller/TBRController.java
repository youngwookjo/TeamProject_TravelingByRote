package tbr.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
				throw new Exception("입력하지 않은 영역");
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

   @PostMapping("/updateUser")
   public ModelAndView updateUser(HttpServletRequest request, HttpServletResponse response,
         @RequestParam("id") String id,
         @RequestParam("pw") String pw) {
      System.out.println("실행중");
      Cookie u = null;
      String vn = "redirect:/privacy.html";
      try {
         if (id.length() == 0 || pw.length() == 0) {
            u = new Cookie("msg", "empty_space");
            throw new Exception("입력하지 않은 영역");
         } else if (service.updateMember(new MemberDTO(id, pw))) {
            u = new Cookie("msg", "update_complete");
            System.out.println("업데이트 성공");
         }
      } catch (Exception e) {
         if (u == null) {
            u = new Cookie("msg", "unexpected_error");
         }
         e.printStackTrace();
      }if(u != null) {
         response.addCookie(u);
      }
      return new ModelAndView(vn);
   }
	
	// * SEARCH
	// http://127.0.0.1:8000/searchByType/typeId=38
	@GetMapping("/searchByType")
	public List<PlaceDTO> searchByType(@RequestParam BigDecimal typeId) {
		System.out.println("/searchByType");
		return service.findPlaceByTypeId(typeId);
	}

	// http://127.0.0.1:8000/searchByKeyword?kwd=산
	@GetMapping("/searchByKeyword")
	public List<PlaceDTO> searchByKeyword(@RequestParam String kwd) {
		System.out.println("/searchByKeyword");
		return service.findPlaceByKwd(kwd);
	}
	
	// http://127.0.0.1:8000/searchByDistance?id=319571&typeId=12&distance=10
	@GetMapping("/searchByDistance")
	public List<List<Object>> searchByDistance(@RequestParam BigDecimal id, @RequestParam BigDecimal typeId, @RequestParam double distance){
		System.out.println("/searchByDistance");
		return service.findPlaceByDistance(id, typeId, distance);
	}
	
	// * DB
	// http://127.0.0.1:8000/data_collect
	@GetMapping("/dataCollect")
	public String dataCollect() {
		return "실행 시간 : " + service.getIds() + "ms";
	}
	
	
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
	
	// Admin : 회원정보 조회(전체)
	@GetMapping("/getAllUser")
	public Iterable<MemberDTO> getAllUser() {
		return service.getAllMember();
	}

	// Admin : 회원정보 조회(Containing)
	@GetMapping("/searchAccount")
	public List<MemberDTO> searchAccount(@RequestParam("id") String id) {
		return service.searchId(id);
	}

	// 8. admin page : 회원 삭제
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