package tbr.controller;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import tbr.model.dto.Place;
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
	public ModelAndView placeSearchOne(@RequestParam("name") String name, HttpServletResponse response){
		Cookie setCookie = new Cookie("place", name);
		setCookie.setMaxAge(60*60*24); // 기간을 하루로 지정
		Place place = placeService.placeSearchOne(name);
		response.addCookie(setCookie);
		ModelAndView model = new ModelAndView();
		model.addObject("place", place);
		model.setViewName("test.html"); 
		return model;
	}
	
	//여행지 이름과 관련된 리스트 검색
	@RequestMapping("/placeSearchByName")
	public ModelAndView placeSearchByName(@RequestParam("name") String name){
		List<Place> place = placeService.placeSearchByName(name);
		ModelAndView model = new ModelAndView();
		model.addObject("place", place);
		model.setViewName("test.html"); 
		return model;
	}
	
	
	//타입 ID로 검색하기
	@RequestMapping("/placeSearchTypeId")
	public ModelAndView placeSearchByName(@RequestParam("typeid") BigDecimal typeid){
		List<Place> place = placeService.placeSearchByTypeId(typeid);
		ModelAndView model = new ModelAndView();
		model.addObject("place", place);
		model.setViewName("test.html"); 
		return model;
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
