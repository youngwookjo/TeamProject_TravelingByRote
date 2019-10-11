package tbr.crawling;


import java.io.BufferedWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Apply2 {
   public static void main(String[] args) {
      try {
         makeFile();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   static JSONArray makeCorpus() {
      System.out.println("== 말뭉치 생성 시작 ==");
      Selenium s = Selenium.start();
      ArrayList<String> contents = new ArrayList<>();
      JSONArray contentArray = new JSONArray();

      try {
         for (int i = 1; i < 3; i++) {
            s.access("https://api.visitkorea.or.kr/guide/inforArea.do?langtype=KOR&arrange=A&mode=listOk&pageNo="
                  + i);
            sleep(4);
            System.out.println("== 주소값 받아옴 ==");
            s.findAll("//*[@id=\"content\"]/div/ul/li/a").stream()
                  .forEach(v -> contents.add(v.getAttribute("href")));
//         s.findAll("//*[@id=\"content\"]/div > ul > li > a")
//         .stream().forEach(v -> contents.add(v.getAttribute("href")));
            System.out.println("== 말뭉치 생성 시작 ==");
            for (String url : contents) {
               String value1 = url.split("=")[1].split("&")[0];
               String value2 = url.split("=")[3].split("&")[0];
               JSONObject resultObj = new JSONObject();
               resultObj.put("contentId", value1);
               resultObj.put("typeid", value2);
               contentArray.add(resultObj);

            }
         }
      } finally {
         s.quit();
      }
      System.out.println("== 말뭉치 생성 완료 ==");
      return contentArray;
   }

   static void makeFile() throws IOException {
      // https://csw7432.tistory.com/entry/Java-Input-Output-Stream
      // https://negafix.tistory.com/entry/Java로-UTF-8-파일-쓰기
      BufferedWriter bWriter = null;
      try {
         bWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("corpus_관광정보.txt"), "UTF-8"),
               1024);
         bWriter.write(makeCorpus().toJSONString());
      } finally {
         bWriter.close();
      }
   }

   static void sleep(double i) {
      try {
         Thread.sleep((long) (i * 1000));
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }

}