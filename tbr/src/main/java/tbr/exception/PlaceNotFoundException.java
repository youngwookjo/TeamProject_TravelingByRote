package tbr.exception;

import java.math.BigDecimal;

public class PlaceNotFoundException extends RuntimeException {

	public PlaceNotFoundException(BigDecimal id, String kwd, double distance) {
		super("Could not find place" + id + kwd + distance);
	}
}