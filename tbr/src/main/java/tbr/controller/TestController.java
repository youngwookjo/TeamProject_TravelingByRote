package tbr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import tbr.model.dto.TbrUser;
import tbr.service.PlaceService;
import tbr.service.UserService;

@CrossOrigin(origins= {"http://127.0.0.1:8000", "http://localhost:8000"})
@RestController
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
	
//		@RequestMapping(value="/login/loginUser", method=RequestMethod.POST)
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
//	        return "andrea/index.html";
//		}
		
		@PostMapping("/loginUser")
		public ModelAndView loginUser(@RequestParam("id") String id, @RequestParam("pw") String pw, ModelAndView mv){
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
	//http://localhost:8000/addUser?id=tester1&pw=secretkey1
	//http://localhost:8000/addUser?id=admin&pw=admin
	@GetMapping("/addUser")
	public ModelAndView addUser(@RequestParam("id") String id, @RequestParam("pw") String pw, ModelAndView mv){
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
	

	
	//3. 회원정보 조회(Equal)
	@RequestMapping("/getUser")
	public TbrUser getUser(@RequestParam("id") String id){
		TbrUser result = userService.get(id);
		System.out.println(result.toString());
		return result;
	}


	//4. 회원 pw 수정
	//http://localhost:8000/updateUser?id=tester1&pw=revised
	@GetMapping("/updateUser")
	public String updateUser(@RequestParam("id") String id, @RequestParam("pw") String pw, Model model) {
		try {
			TbrUser userInfo = new TbrUser(id, pw);
			userService.update(userInfo);
			model.addAttribute("userInfo", userInfo);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return "redirect:/contact.html";
	
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
	
	//1. admin page : 회원정보 조회(전체)
	//http://localhost:8000/getAllUser
	@GetMapping("/getAllUser")
	public Iterable<TbrUser> getAllUser() {
		Iterable<TbrUser> list = userService.getAll();
		System.out.println(list.toString());
		return list;
	}   
	
	//2. Admin page : 회원정보 조회(Containing)
	//http://localhost:8000/getUser/tester1
	//http://localhost:8000/getUser/tester3
	@GetMapping("/searchAccount")
	public List<TbrUser> searchAccount(@RequestParam("id") String id){
		List<TbrUser> list = userService.searchId(id);
		System.out.println(list.toString());
		return list;
	}
	
	//3. admin page : 회원 삭제
	@GetMapping("/deleteAccount")
	//http://localhost:8000/deleteAccount?id=tester1
	public String deleteAccount(@RequestParam("id") String id) {
		try {
			userService.deleteId(id);
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("회원 삭제 완료");
        return "회원 삭제 완료";
	}
	
	@PostMapping("/loginAdmin")
	public ModelAndView loginAdmin(@RequestParam("id") String id, @RequestParam("pw") String pw, ModelAndView mv){
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
