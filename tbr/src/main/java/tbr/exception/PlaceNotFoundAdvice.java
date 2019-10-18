package tbr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
public class PlaceNotFoundAdvice {

//	@ResponseBody  //사용 안 함
	@ExceptionHandler(RuntimeException.class) //생략 불가
//	@ResponseStatus(HttpStatus.NOT_FOUND) //사용 안 함
	public RedirectView PlaceNotFoundHandler(RuntimeException e) {
		System.out.println("global 예외 처리");
		return new RedirectView("http://127.0.0.1:8000/fail.html");
	}
}
