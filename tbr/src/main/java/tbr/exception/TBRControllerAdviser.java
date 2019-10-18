package tbr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class TBRControllerAdviser {
	@ResponseBody
	@ExceptionHandler(AsyncException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String asyncHandler(AsyncException e) {
		System.out.println("비동기 예외 처리");
		return e.getMessage();
	}

}
