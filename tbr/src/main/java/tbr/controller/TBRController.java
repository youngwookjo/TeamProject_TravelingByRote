package tbr.controller;

import java.util.List;

import javax.servlet.http.Cookie;
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

	@Autowired
	TBRService service;

	// * LOGIN
	// https://yeon27.tistory.com/66 (redirect <-> forward)
	@PostMapping("/login")
	public ModelAndView loginMember(HttpSession session, HttpServletResponse response,
			@RequestParam("id") String id, @RequestParam("pw") String pw){
		String vn = "redirect:/login.html";
		if(service.loginMember(new MemberDTO(id, pw))) {
			Cookie c = new Cookie("id", id);
			response.addCookie(c);
			session.setAttribute("login", true);
			System.out.println("로그인 성공");
			vn = "redirect:/menu.html";
		} else {
			Cookie c = new Cookie("msg", "not_exist_member");
			response.addCookie(c);
			System.out.println("로그인 실패");
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
}
