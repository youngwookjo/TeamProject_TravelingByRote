package tbr.util.crawling;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Selenium {
	private static Selenium instance = new Selenium();
	private static WebDriver driver = null;
	static {
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
	}

	private Selenium () {};
	
	public static Selenium start() {
		// https://beomi.github.io/gb-crawling/posts/2017-09-28-HowToMakeWebCrawler-Headless-Chrome.html
		// https://marobiana.tistory.com/150
		ChromeOptions options = new ChromeOptions();
		options.addArguments("headless");
		options.addArguments("window-size=1920x1080");
		options.addArguments("disable-gpu");
		driver = new ChromeDriver(options);
		System.out.println("== Headless 크롬 가동 ==");
		return instance;
	}
	
	public void close() {
		driver.close();
	}
	
	public void quit() {
		driver.quit();
		System.out.println("== Headless 크롬 종료 ==");
	}

	public void access(String url) {
		driver.get(url);
	}
	
	public WebElement find(String xpath) {
		return driver.findElement(By.xpath(xpath));
	}
	
	public List<WebElement> findAll(String xpath) {
		return driver.findElements(By.xpath(xpath));
	}
	
}
