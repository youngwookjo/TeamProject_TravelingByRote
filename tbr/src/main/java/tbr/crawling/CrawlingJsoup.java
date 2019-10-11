package tbr.crawling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class CrawlingJsoup {
	public static JSONObject getRawData(String location) {
		JSONObject result = new JSONObject();
		Document doc = null;
		try {
			doc = Jsoup.connect("http://data.visitkorea.or.kr/page/" + location).userAgent("Mozilla").get();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		Map<String, String> info = new HashMap<String, String>();
		
		int i=0;
		while(i<17) {
			i++;
			try {
				String keyRoot = "#wrap > table > tbody > tr:nth-child("+i+") > td.tit > a";
				Element keyElement = doc.select(keyRoot).get(0);
				String key = keyElement.text();
				
				String valueRoot = "#wrap > table > tbody > tr:nth-child("+i+") > td:nth-child(2)";
				Element valueElement = doc.select(valueRoot).get(0);
				String value = valueElement.text();
				
				info.put(key, value);
				
			}catch(IndexOutOfBoundsException e) {
				continue;
			}
		}
		result.putAll(info);
		return result;
	}

/*	public static void main(String[] args) {
		String location = "128780";
		System.out.println(getRawData(location));

	}*/

}
