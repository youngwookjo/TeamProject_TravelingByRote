package tbr.crawling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Apply {
	public static JSONObject getLocationID() {
		JSONObject result = new JSONObject();
		Document doc = null;
		try {
			doc = Jsoup.connect("https://api.visitkorea.or.kr/guide/inforArea.do?langtype=KOR&arrange=A&mode=listOk&pageNo=1").userAgent("Mozilla").get();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		Map<String, String> info = new HashMap<String, String>();
		
		int i=0;
		while(i<12) {
			i++;
			try {
				String root = "#content > div.listWrap > ul > li:nth-child("+i+") > a";
				Element keyElement = doc.select(root).get(0);
				String keyRoot = keyElement.attr("abs:href");
				System.out.println(keyRoot);
				
				info.put("ID", keyRoot);
			
				
//				Pattern log = Pattern.compile("/guide/tourDetail.do?contentId=(\\d+)&langtype=KOR&typeid=(\\d+)&oper=area&burl-");
//				Matcher m = log.matcher(keyRoot);
//				
//				while(m.find()) {
//					info.put("contentId", m.group(0));
//					info.put("typeid", m.group(1));
//				}
//				
				
			}catch(IndexOutOfBoundsException e) {
				continue;
			}
		}
		result.putAll(info);
		return result;
	}
	
	public static void main(String[] args) {
		System.out.println(getLocationID());

	}

}
