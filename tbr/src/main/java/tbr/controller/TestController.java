package tbr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import tbr.service.PlaceService;
import tbr.service.UserService;
import tbr.util.crawling.PlaceCrawler;

@Controller
public class TestController {
	@Autowired
	UserService userService;
	@Autowired
	PlaceService placeService;
	@Autowired
	PlaceCrawler placeCrawler;
	
	@RequestMapping("crawling.do")
	public String crawling() {
		placeCrawler.crawling();
		return "redirect:crawling.html";
	}
}
