package tbr.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.message.callback.PrivateKeyCallback.Request;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import tbr.model.dto.Place;
import tbr.model.dto.TbrUser;
import tbr.service.PlaceService;
import tbr.service.UserService;

@Controller
public class TestController {
	@Autowired
	UserService userService;
	@Autowired
	PlaceService placeService;
	
	
//	@RequestMapping(value="/", method=RequestMethod.GET)
//	public String testCookie(HttpServletResponse response){
//	Cookie setCookie = new Cookie("placeName", value); // 쿠키 이름을 name으로 생성
//	setCookie.setMaxAge(60*60*24); // 기간을 하루로 지정
//	response.addCookie(setCookie);
//	}
	
//	@RequestMapping(value="/", method=RequestMethod.GET)
//	public String testCookie(HttpServletRequest request){
//	Cookie[] getCookie = request.getCookies();
//	if(getCookie != null){
//	for(int i=0; i<getCookie.length; i++){
//	Cookie c = getCookie[i];
//	String name = c.getName(); // 쿠키 이름 가져오기
//	String value = c.getValue(); // 쿠키 값 가져오기
//			}
//		}
//	}

	//여행지 정확한 이름으로 하나 검색
	@RequestMapping("/placeSearchOne")
	public Place placeSearchOne(@RequestParam("name") String name, HttpServletResponse response){
		Cookie setCookie = new Cookie("place", name);
		setCookie.setMaxAge(60*60*24); // 기간을 하루로 지정
		Place place = placeService.placeSearchOne(name);
		response.addCookie(setCookie);
		return place;
	}
	
	//여행지 이름과 관련된 리스트 검색
	@RequestMapping("/placeSearchByName")
	public List<Place> placeSearchByName(@RequestParam("name") String name){
		List<Place> place = placeService.placeSearchByName(name);
		return place;
	}
	
	
	//타입 ID로 검색하기
	@RequestMapping("/placeSearchTypeId")
	public List<Place> placeSearchByName(@RequestParam("typeid") BigDecimal typeid){
		List<Place> place = placeService.placeSearchByTypeId(typeid);
		return place;
	}
	
	
	
	//여행지 이름으로 이름찾고 여행지있을경우 삭제
	@RequestMapping("/placeDeleteByName")
	public String placeDeleteByName(@RequestParam("name") String name) {
		Place place = placeService.placeSearchOne(name);
		try {
			placeService.placeDelete(place);
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("삭제 완료");
        return "여행지 삭제 완료";
	}
}
