package tbr.crawling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

public class Crawling {
	
	public static void main(String[] args) {
			
			String locNum = "128780";
			Selenium s = Selenium.start();
			ArrayList<String> key = new ArrayList<>();
			ArrayList<Object> value = new ArrayList<>();
			Map<String, Object> info = new HashMap<String, Object>();

			try {
				s.access("http://data.visitkorea.or.kr/page/@".replace("@", locNum));
//				sleep(1);
				s.findAll("//*[@id=\"wrap\"]/table/tbody/tr/td[1]/a")
				.stream().forEach(v -> key.add(v.getText()));			
				s.findAll("//*[@id=\"wrap\"]/table/tbody/tr/td[2]")
				.stream().forEach(v -> value.add(v.getText()));
				
				for(int i=0; i<value.size(); i++) {
					info.put(key.get(i), value.get(i));
				}
				
			} finally {
				s.quit();
			}
			
			JSONObject json = new JSONObject();
			json.putAll(info);
			System.out.println(json.toString());
	}
}
