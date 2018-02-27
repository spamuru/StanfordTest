package com.test.stanford.my_test;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;

public class LandingPagesTest extends HelperTest {
	//Declaration of variables
	public WebDriver driver;
	HelperTest helper = new HelperTest();
	String filePath=System.getProperty("user.dir") + "//DataSheet//TuscanyTrip.xls";
	String sheetName="Trip";
	String tableName="Testxxx";

	@Test(dataProvider = "data-provider")
	public void LinkCheck(String name ,String pageurl, String title) throws InterruptedException {

		System.out.println("Test Name: " + name);

		//Load the url
		driver.get(pageurl);

		//Verify the title and url
		Assert.assertEquals(title, driver.getTitle());
		Assert.assertEquals(pageurl, driver.getCurrentUrl());

		System.out.println(driver.getCurrentUrl());
		System.out.println(driver.getTitle());

		//wait for 5 seconds - if any lag in the page this will handle it .
		int wait = 10;
		WebDriverWait WAIT = new WebDriverWait(driver,wait);

		try{
			WAIT.pollingEvery(5, TimeUnit.SECONDS);

			Set<String> UrlList = new HashSet<String>();

			//Fetch the link tags
			List<WebElement> anchorTagsList = driver.findElements(By.tagName("a"));

			//Extract Href and add to the set to eliminate duplicate urls
			for(WebElement anchorTagElement:anchorTagsList) {
				String url=anchorTagElement.getAttribute("href");
				if(url.contains("#") || url.contains("javascript") ||url.contains("mailto:") ) {
					//These are not Landing pages to check
				}else {
					UrlList.add(url);  
				}
			}

			//Check status code of all the links on the page
			helper.StatusCodeCheck(UrlList);
		}
		catch (NoSuchElementException exception)
		{
			System.out.println(exception.getMessage());
		}


	}

	//Dataprovider
	@DataProvider(name = "data-provider")
	public Object[][] dataProviderMethod() {
		Object[][] testData = HelperTest.readExcelData(filePath, sheetName, tableName);
		return testData;
	}

	//Initialize the driver
	@BeforeClass
	public void beforeClass() {  
		System.setProperty("webdriver.gecko.driver",System.getProperty("user.dir") + "//geckodriver");
		driver=new FirefoxDriver();  
	}

	//Kill the driver
	@AfterClass
	public void afterClass() {
		driver.quit();
	}

}
