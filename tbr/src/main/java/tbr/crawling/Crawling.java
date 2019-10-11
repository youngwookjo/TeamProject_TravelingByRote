package tbr.crawling;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import junit.framework.Test;

public class Crawling {
	@SuppressWarnings("unchecked")
	public static void getUrl() throws IOException {
		String url = "https://api.visitkorea.or.kr/guide/inforArea.do?langtype=KOR&arrange=A&mode=listOk&pageNo=";
		String regex = "contentId=(\\d*)&langtype=KOR&typeid=(\\d*)";
		JSONArray array = new JSONArray();
		BufferedWriter bWriter = null;
		try {
			for (int i = 1; i < 200; i++) {
				System.out.println(url+i);
				sleep(4);
//				Jsoup.connect(url + i).get().select(".galleryList > li > a").stream()
//				.map(v -> getRawData(findGroup(v.attr("href"), regex))).filter(v -> v.size() != 0)
//				.forEach(v -> array.add(v));		
				Jsoup.connect(url + i).get().select(".galleryList > li > a").stream()
						.map(v -> result(findGroup(v.attr("href"), regex))).forEach(v -> array.add(v));
				System.out.println(array);
//				bWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("corpus_관광정보.txt"), "UTF-8"),
//						1024);
//				bWriter.write(array.toJSONString());
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
//			bWriter.flush();
//			bWriter.close();
		}
	}

//
	public static JSONObject result(List<String> list) {
		String value1 = list.get(0);
		String value2 = list.get(1);
		JSONObject resultObj = new JSONObject();
		resultObj.put("contentId", value1);
		resultObj.put("typeid", value2);
		return resultObj;

	}

//	static void makeFile(JSONArray contentArray) throws IOException {
//		BufferedWriter bWriter = null;
//		try {
//			bWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("corpus_관광정보.txt"), "UTF-8"),
//					1024);
//			bWriter.write(contentArray.toJSONString());
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			bWriter.flush();
//			bWriter.close();
//		}
//	}

	public static List<String> findGroup(String text, String regex) {
		Matcher m = Pattern.compile(regex).matcher(text);
		m.find();
		List<String> list = new ArrayList<>();
		for (int i = 1; i <= m.groupCount(); i++) {
			list.add(m.group(i));
		}
		return list;
	}

	public static JSONObject getRawData(List<String> list) {
		JSONObject result = new JSONObject();
		if (!list.get(1).equals("25")) {
			Document doc = null;
			try {
				System.out.println(list.get(0));
				sleep(0.5);
				doc = Jsoup.connect("http://data.visitkorea.or.kr/page/" + list.get(0)).userAgent("Mozilla").get();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Map<String, String> info = new HashMap<String, String>();
			int i = 0;
			while (i < 17) {
				i++;
				try {
					String keyRoot = "#wrap > table > tbody > tr:nth-child(" + i + ") > td.tit > a";
					Element keyElement = doc.select(keyRoot).get(0);
					String key = keyElement.text();

					String valueRoot = "#wrap > table > tbody > tr:nth-child(" + i + ") > td:nth-child(2)";
					Element valueElement = doc.select(valueRoot).get(0);
					String value = valueElement.text();

					info.put(key, value);

				} catch (IndexOutOfBoundsException e) {
					continue;
				}
			}

			result.putAll(info);
		}
		return result;
	}

	static void sleep(double i) {
		try {
			Thread.sleep((long) (i * 1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		try {
			getUrl();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}