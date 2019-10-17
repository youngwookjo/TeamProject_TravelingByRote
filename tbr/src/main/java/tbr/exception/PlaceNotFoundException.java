package tbr.exception;

import java.math.BigDecimal;

public class PlaceNotFoundException extends RuntimeException {

	public PlaceNotFoundException(BigDecimal id) {
		super("Could not find place" + id);
	}
}