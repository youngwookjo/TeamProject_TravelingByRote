package tbr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class PlaceNotFoundAdvice {

	@ResponseBody  //생략 불가
	@ExceptionHandler(PlaceNotFoundException.class) //생략 불가
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String PlaceNotFoundHandler(PlaceNotFoundException e) {
		System.out.println("global 예외 처리");
		return "redirect:/fail.html";
	}
}
