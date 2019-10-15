package tbr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import tbr.model.dto.TbrUser;
import tbr.service.PlaceService;
import tbr.service.UserService;

//@CrossOrigin(origins= {"http://127.0.0.1:8000", "http://localhost:8000"})
@Controller
public class TestController {
	@Autowired
	UserService userService;
	@Autowired
	PlaceService placeService;
	
	//0. 로그인
		//http://localhost:8000/addUser?id=tester1&pw=secretkey1
		//http://localhost:8000/addUser?id=tester2&pw=secretkey2
/*		@GetMapping("/login/loginUser")
		public String loginUser(@RequestParam("id") String id, @RequestParam("pw") String pw, Model model) {
	
			TbrUser userInfo = new TbrUser(id, pw);
			String result = "로그인 실패";
			boolean flag = userService.login(userInfo);
			if (flag == true) {
				result = "로그인 성공";
			}
			System.out.println(result);
			model.addAttribute("object", "userInfo");
			return "redirect:/andrea/index.html";
		}*/
	
//		@GetMapping("/login/loginUser")
//		public String loginUser(@RequestParam("id") String id, @RequestParam("pw") String pw, Model model){
//			
//			TbrUser userInfo = new TbrUser(id, pw);
//			String result = "로그인 실패";
//			boolean flag = userService.login(userInfo);
//	        if (flag == true) {
//	        	result = "로그인 성공";
//	        }
//	        System.out.println(result);
//	        model.addAttribute("userInfo", userInfo);
//	        System.out.println(model.toString());
//	        return "tester.html";
//		}
		
		@GetMapping("/login/loginUser")
		public ModelAndView loginUser(@RequestParam("id") String id, @RequestParam("pw") String pw, ModelAndView mv){
			TbrUser userInfo = new TbrUser(id, pw);
			String result = "로그인 실패";
			boolean flag = userService.login(userInfo);
	        if (flag == true) {
	        	result = "로그인 성공";
	        }
	        System.out.println(result);
	        mv.setViewName("../static/andrea/index.html");
	        mv.addObject("userInfo", userInfo);
	     
	        return mv;
		}
		
/*		@GetMapping("/login/loginUser")
		public ModelAndView loginUser(@RequestParam("id") String id, @RequestParam("pw") String pw){
			TbrUser userInfo = new TbrUser(id, pw);
			String result = "로그인 실패";
			boolean flag = userService.login(userInfo);
	        if (flag == true) {
	        	result = "로그인 성공";
	        }
	        System.out.println(result);
	        return new ModelAndView("redirect:/andrea/index.html", "userInfo", userInfo);
		}*/
	
	//1. 회원가입
	//http://localhost:8000/login/addUser?id=tester1&pw=secretkey1
	//http://localhost:8000/login/addUser?id=tester2&pw=secretkey2
	@GetMapping("/login/addUser")
	public String addUser(@RequestParam("id") String id, @RequestParam("pw") String pw){
		TbrUser info = new TbrUser(id, pw);
		String result = "가입 실패";
		boolean flag = userService.add(info);
        if (flag == true) {
        	result = "가입 성공";
        }
        System.out.println(result);
        return "redirect:/andrea/index.html";
	}
	
	//2. 회원정보 조회(개별)
	//http://localhost:8000/getUser/tester1
	//http://localhost:8000/getUser/tester3
	@RequestMapping("/getUser/{id}")
	public String getUser(@PathVariable("id") String id){
		TbrUser info = userService.get(id);
		String result = info.toString();
		System.out.println(result);
		return result;
	}

	//3. 회원정보 조회(전체)
	//http://localhost:8000/getAllUser
	@RequestMapping("/getAllUser")
	public String getAllUser() {
		Iterable<TbrUser> list = userService.getAll();
		System.out.println(list.toString());
		return "회원명부 넘길 주소값";
	}   
	//4. 회원 pw 수정
	//http://localhost:8000/updateUser?id=tester1&pw=revised
	@GetMapping("/andrea/updateUser")
	public String updateUser(@RequestParam("id") String id, @RequestParam("pw") String pw, Model model) {
		try {
			TbrUser userInfo = new TbrUser(id, pw);
			userService.update(userInfo);
			model.addAttribute("userInfo", userInfo);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return "redirect:/andrea/contact.html";
	
	}
	
	//5. 회원 탈퇴
	@RequestMapping("/deleteUser")
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
