package tbr.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
	public static String find(String text, String regex) {
		Matcher m = Pattern.compile(regex).matcher(text);
		m.find();
		return m.group(1);
	}

	public static List<String> findAll(String text, String regex) {
		Matcher m = Pattern.compile(regex).matcher(text);
		List<String> list = new ArrayList<>();
		while (m.find()) {
			if (m.group(1).length() != 0) {
				list.add(m.group(1));
			}
		}
		return list;
	}

	public static void sleep(double sec) {
		try {
			Thread.sleep((long) (sec * 1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
