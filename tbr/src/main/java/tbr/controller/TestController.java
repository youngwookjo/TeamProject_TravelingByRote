package tbr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import tbr.service.PlaceService;
import tbr.service.UserService;

@Controller
public class TestController {
	@Autowired
	UserService userService;
	@Autowired
	PlaceService placeService;
}
