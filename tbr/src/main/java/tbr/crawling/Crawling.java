package tbr.crawling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Crawling {
	
	public static void main(String[] args) {
			
			String locNum = "128771";
			Selenium s = Selenium.start();
			ArrayList<String> key = new ArrayList<>();
			ArrayList<String> value = new ArrayList<>();
			Map<String, String> info = new HashMap<>();

			try {
				s.access("http://data.visitkorea.or.kr/page/@".replace("@", locNum));
//				sleep(1);
				System.out.println("== key ==");
				s.findAll("//*[@id=\"wrap\"]/table/tbody/tr/td[1]/a")
				.stream().forEach(v -> key.add(v.getText()));			
				System.out.println("== value ==");
				s.findAll("//*[@id=\"wrap\"]/table/tbody/tr/td[2]")
				.stream().forEach(v -> value.add(v.getText()));
				
				for(int i=0; i<value.size(); i++) {
					info.put(key.get(i), value.get(i));
				}
				
			} finally {
				s.quit();
			}
			System.out.println(info.toString());
		
		
	}
}
