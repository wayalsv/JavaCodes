package all.org.vyomlibrary;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Program to handle selenium element.
 *
 */
public class VyomSeleniumHandler 
{
	public static WebDriver driver;
	public static WebDriverWait wait;


	public void clearDriverInstances() throws Exception //delete all active IEDriverServer.exe processes
	{	
		String browserName = getProperties("BrowserName");
		Thread.sleep(Integer.parseInt(getProperties(("ClearDriverWait"))));

		if(browserName.equalsIgnoreCase("Ie"))
		{
			Runtime.getRuntime().exec("taskkill /f /t /im IEDriverServer.exe");
		}
		else if(browserName.equalsIgnoreCase("Chrome"))
		{
			Runtime.getRuntime().exec("taskkill /f /t /im chromedriver.exe");
		}

		Thread.sleep(Integer.parseInt(getProperties(("ClearDriverWait"))));

	}// End of function 

	public static String getProperties(String proName)throws Exception//get properties
	{
		String proValue = "";
		try{
			// Load properties file
			File file = new File("Tools\\Credentials.properties");//get file source
			FileInputStream fileInput;
			fileInput = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fileInput);
			proValue = properties.getProperty(proName).trim();
		}catch(Exception e)
		{
			System.out.println("Unable to access properties: "+proName+" value from propeties file.");
			throw e;
		}
		return proValue;
	}//End of ReadPropertiesFile() 

	public static boolean checkPath(String path, Logger log)
	{
		boolean flag = false;

		File file = new File(path);
		log.info(path);
		if ((file.isDirectory()) && (file.exists())) {
			flag = true;
		}
		return flag;
	}
	public boolean getDriver() throws Exception 
	{
		try
		{
			String browserName = getProperties("BrowserName");
			if(browserName.equalsIgnoreCase("Ie"))
			{
				System.setProperty("webdriver.ie.driver",getProperties("IeDriverPath"));// Set desired capabilities to Ignore IEDriver zoom level settings and disable native events.		

				DesiredCapabilities caps = DesiredCapabilities.internetExplorer();
				caps.setCapability("EnableNativeEvents", false);
				caps.setCapability("ignoreZoomSetting", true);// Set desired capabilities to Ignore IEDriver zoom level settings and disable native events.
				caps.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);// Protected mode
				caps.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);//SSL setting

				driver = new InternetExplorerDriver(caps);//create Internet explorer driver Instance
				driver.manage().window().maximize();
				try
				{
					driver.manage().deleteAllCookies();
				}catch(Exception e){}
				driver.findElement(By.tagName("html")).sendKeys(Keys.chord(Keys.CONTROL, "0"));				
				return true;			
			}
			else if(browserName.equalsIgnoreCase("Chrome"))
			{
				//Chrome Browser use
				System. setProperty("webdriver.chrome.driver", getProperties("ChromeDriverPath"));
				driver = new ChromeDriver();
				driver.manage().window().maximize();
				try
				{
					driver.manage().deleteAllCookies();
				}catch(Exception e){}
				return true;
			}
			else if(browserName.equalsIgnoreCase("ChromeBinary"))
			{
				ChromeOptions chromeOptions = new ChromeOptions();
				chromeOptions.setBinary("D:\\GoogleChromePortable.exe");
				System. setProperty("webdriver.chrome.driver", getProperties("ChromeDriverPath"));         
				driver = new ChromeDriver(chromeOptions);
			}
		}catch(Exception e)
		{
			throw e;
		}
		return false;
	}//End of setURL()

	public void loadUrl(String URL) throws Exception 
	{
		driver.get(URL);
	}//End of loadUrl()

	public void waitForPageLoad(int waitSec, Logger log) 
	{
		log.info("---------------------------Inside wait for page---------------------------------");
		ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() 
		{
			public Boolean apply(WebDriver driver) 
			{
				return ((JavascriptExecutor)driver).executeScript("return document.readyState").equals("complete");
			}
		};
		WebDriverWait wait = new WebDriverWait(VyomSeleniumHandler.driver, waitSec);
		wait.until(pageLoadCondition);

	}//End of waitForPageLoad() 

	public void switchToFrame(String NewFrameName)throws Exception
	{	
		driver.switchTo().defaultContent();
		driver.switchTo().frame(NewFrameName);

	}//End of switchToFrame()

	public void acceptAlert()
	{	
		try{
			new WebDriverWait(driver,10).ignoring(NoAlertPresentException.class).until(ExpectedConditions.alertIsPresent());
			Alert alertOK = driver.switchTo().alert();
			alertOK.accept();
		}catch(Exception e){}
	}//End of acceptAlert()

	public void actionTab() throws InterruptedException
	{
		Actions action = new Actions(driver);
		action.sendKeys(Keys.TAB).perform();
		Thread.sleep(500);
	}//End of actionTab()

	public boolean checkName(String name) 
	{
		if(driver.findElements(By.name(name)).size()!= 0)//check xpath
		{
			return true;
		}else
		{
			return false;
		}

	}//End of  checkLinkText();


	public boolean checkPartLinkText(String partLink) 
	{
		if(driver.findElements(By.partialLinkText(partLink)).size() != 0 )//check partial  link text
		{
			return true;
		}
		else
		{
			return false;
		}
	}//End of checkPartLinkText();


	public boolean setValueByXpath(String xpath,String value,int  locatedWaitInSec) throws Exception
	{	
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		WebElement wb = VyomSeleniumHandler.driver.findElement(By.xpath(xpath));
		JavascriptExecutor jse = (JavascriptExecutor)VyomSeleniumHandler.driver;
		jse.executeScript("arguments[0].value ='"+value+ "';", wb);
		Thread.sleep(200);

		String enterValue = getAttributeValueByXpath(xpath, 30);
		Thread.sleep(200);
		if(enterValue.equals(value)) return true;
		return false;

	}//End of setTextByValue_ID()

	public boolean setByJavaExecutorById_Date(String xpath, String value, int locatedWaitInSec) throws Exception
	{	
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		JavascriptExecutor jse = (JavascriptExecutor)VyomSeleniumHandler.driver;
		jse.executeScript("document.getElementByXpath('"+xpath+"').removeAttribute('readonly'),0;");
		Thread.sleep(200);

		sendById(xpath, value, 10);
		return false;

	}//End of setTextByValue_ID()


	public String getAttributeValueByXpath(String xpath,int locatedWaitInSec)throws Exception
	{
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
		String value = driver.findElement(By.xpath(xpath)).getAttribute("value");
		return value;
	}//End of getAttributeValueByXpath()

	public void clickByPartLinkText(String partLinkText,int locatedWaitInSec)throws Exception
	{	
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText(partLinkText)));
		driver.findElement(By.partialLinkText(partLinkText)).click();

	}//End of clickByPartLinkText()

	public void clickByLinkText(String linkText,int locatedWaitInSec)throws Exception
	{	
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText(linkText)));
		driver.findElement(By.linkText(linkText)).click();

	}//End of clickBYLinkText()

	public void waitForLinkText(String linkText, int locatedWaitInSec) 
	{
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText(linkText)));

	}//End of  waitForLinkText();

	public void waitForID(String ID, int locatedWaitInSec) 
	{
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id(ID)));
	}//End of  waitID();


	public boolean checkLinkText(String link) 
	{
		if(driver.findElements(By.linkText(link)).size() != 0 )//check link text
		{
			return true;
		}else
		{
			return false;
		}
	}//End of  checkLinkText();

	public boolean checkId(String id) 
	{
		if(driver.findElements(By.id(id)).size() != 0 )//check link text
		{
			return true;
		}else
		{
			return false;
		}
	}//End of  checkLinkText();

	public boolean checkXpath(String xpath) 
	{
		if(driver.findElements(By.xpath(xpath)).size()!= 0)//check xpath
		{
			return true;
		}else
		{
			return false;
		}

	}//End of  checkLinkText();

	public boolean checkcssSelector(String path) 
	{
		if(driver.findElements(By.cssSelector(path)).size()!= 0)//check xpath
		{
			return true;
		}else
		{
			return false;
		}

	}//End of  checkcssSelector();

	public void getFrame(String getFrameName)throws Exception
	{	
		driver.switchTo().defaultContent();
		driver.switchTo().frame(getFrameName);
	}//End of getFrame()

	public void sendByClassName(String className,String value,int  locatedWaitInSec) throws Exception
	{	
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.className(className)));
		driver.findElement(By.className(className)).sendKeys(value);

	}//End of sendByClassName()

	public void sendByName(String name,String value,int  locatedWaitInSec) throws Exception
	{	
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.name(name)));
		driver.findElement(By.name(name)).sendKeys(value);;
	}//End of sendByName()

	public void sendById(String id, String value, int locatedWaitInSec) throws Exception
	{	
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
		driver.findElement(By.id(id)).sendKeys(value);
	}//End of sendById()

	public void enterBySelectedId(String id, int  locatedWaitInSec) throws Exception
	{	
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
		driver.findElement(By.id(id)).sendKeys(Keys.ENTER);
	}//End of sendById()

	public void sendByxpath(String xpath,String value,int  locatedWaitInSec) throws Exception
	{	
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
		driver.findElement(By.xpath(xpath)).sendKeys(value);
	}//End of sendById()

	public void clickById(String id,int locatedWaitInSec)throws Exception
	{	
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
		driver.findElement(By.id(id)).click();
	}//End of clickById()

	public void clickByClassName(String className,int locatedWaitInSec)throws Exception
	{	
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.className(className)));
		driver.findElement(By.className(className)).click();
	}//End of clickById()

	public void doubleclickByID(String id,int locatedWaitInSec)throws Exception
	{	
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
		Actions action = new Actions(driver);
		action.moveToElement(driver.findElement(By.id(id))).doubleClick().perform(); // Double Click on Selected Record
	}//End of doubleclickByID()

	public void clickByName(String name,int locatedWaitInSec)throws Exception
	{	
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.name(name)));
		driver.findElement(By.name(name)).click();
	}//End of clickByName()

	public void doubleclickByName(String name,int locatedWaitInSec)throws Exception
	{	
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.name(name)));
		Actions action = new Actions(driver);
		action.moveToElement(driver.findElement(By.xpath(name))).doubleClick().perform(); // Double Click on Selected Record
	}//End of doubleclickByName()

	public void clickByXpath(String xpath,int locatedWaitInSec)throws Exception
	{	
		wait = new WebDriverWait(driver,locatedWaitInSec);    
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
		driver.findElement(By.xpath(xpath)).click();
	}//End of clickByXpath()

	public void clickByEleVisibleByXpath(String xpath,int locatedWaitInSec)throws Exception
	{	
		wait = new WebDriverWait(driver,locatedWaitInSec);    
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
		driver.findElement(By.xpath(xpath)).isDisplayed();
	}//End of clickByXpath()

	public void doubleclickByXpath(String xpath,int locatedWaitInSec)throws Exception
	{	
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
		Actions action = new Actions(driver);
		action.moveToElement(driver.findElement(By.xpath(xpath))).doubleClick().perform(); // Double Click on Selected Record
	}//End of doubleclickByXpath()

	public List<WebElement> getByTagName(String tableID,String tagName,int locatedWaitInSec) throws Exception
	{	
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id(tableID)));
		WebElement myTable = driver.findElement(By.id(tableID));//Get table.
		List<WebElement> cnt = myTable.findElements(By.tagName(tagName));//Get number of rows in table.
		return cnt;
	}//End of getByTagName()

	public List<WebElement> getTableValuesByXpath(String xpath,String tagName,int locatedWaitInSec) throws Exception
	{	
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
		WebElement located = driver.findElement(By.xpath(xpath));//Get table.
		List<WebElement> cnt = located.findElements(By.xpath(tagName));//Get number of rows in table.
		return cnt;
	}//End of getByXpath()

	public String getTextValueByXpath(String xpath,int locatedWaitInSec)throws Exception
	{
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
		String value = driver.findElement(By.xpath(xpath)).getText().trim();
		return value;
	}//End of getAttributeValueByXpath()

	public String getByXpath(String xpath,int locatedWaitInSec)throws Exception
	{
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
		String value = driver.findElement(By.xpath(xpath)).getText().trim();
		return value;
	}//End of getByXpath()

	public void waitForXpath(String xpath,int locatedWaitInSec)throws Exception
	{
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
	}//End of waitForXpath()

	public void waitForName(String name,int locatedWaitInSec)throws Exception
	{
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.name(name)));
	}//End of waitForXpath()

	public Set<String> getWindows()
	{ 
		Set<String>  window = driver.getWindowHandles(); //keep window	
		return window;
	}//End of getWindow()

	public void setWindow(String window)//
	{
		driver.switchTo().window(window);
	}//End of setWindow()

	public String getWindowTitle()
	{
		String winTitle = driver.getTitle();
		return winTitle;
	}//End of getWindowTitle()

	public String getWindow()
	{
		String window = driver.getWindowHandle(); // Store parent window
		return window;
	}//End of getWindow()

	public void maximizeWindow()throws Exception
	{
		driver.manage().window().maximize();
	}//End of maximizeWindow()

	public static boolean checkDispEleByXpath(String xpath, Logger log) throws InterruptedException
	{
		boolean elementCheck = false;
		try
		{
			log.info("---------------------------------Inside CheckDispEleByXpath--------------------------------------");

			Thread.sleep(Integer.parseInt(VyomSeleniumHandler.getProperties("ThreadMSecForDispElemnt")));
			elementCheck = VyomSeleniumHandler.driver.findElement(By.xpath(xpath)).isDisplayed();
			log.info("Xpath display flag : "+elementCheck);
		}catch(Exception e)
		{
			log.info("Error in checkDispEleByXpath :"+e);
		}
		return elementCheck;

	}//End of checkDisplayElementByXpath()
	public void clearByName(String name,int locatedWaitInSec)throws Exception
	{
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.name(name)));
		driver.findElement(By.name(name)).clear();
	}//End of clearByName()

	public void clearById(String id,int locatedWaitInSec)throws Exception
	{
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
		driver.findElement(By.id(id)).clear();
	}//End of clearById()

	public void clearByXpath(String xpath,int locatedWaitInSec)throws Exception
	{
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
		driver.findElement(By.xpath(xpath)).clear();
	}//End of clearByXpath()

	public void clearByClassName(String className,int locatedWaitInSec)throws Exception
	{
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.className(className)));
		driver.findElement(By.className(className)).clear();
	}//End of clearByClassName()

	public void clearByClassName(String className)throws Exception
	{
		driver.findElement(By.className(className)).clear();
	}//End of clearByClassName()

	public void sendKeysByClassName(String eleClassName,String text) throws Exception
	{	
		driver.findElement(By.className(eleClassName)).sendKeys(text);
	}//End of sendKeysByClassName()

	public void clickById(String tagId)throws Exception
	{	
		driver.findElement(By.id(tagId)).click();
	}//End of clickByiD()

	public String getByxPath(String xPath)throws Exception
	{
		String value = driver.findElement(By.xpath(xPath)).getText().trim();
		return value;
	}//End of getByxPath()

	public void selectDropDownById(String id,String text,int locatedWaitInSec)throws Exception
	{
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
		Select dropdown = new Select(driver.findElement(By.id(id)));
		dropdown.selectByVisibleText(text);
	}//End of selectDropDownById()

	public void selectDropDownByXpath(String xPath,String text,int locatedWaitInSec)throws Exception
	{
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xPath)));
		Select dropdown = new Select(driver.findElement(By.xpath(xPath)));
		dropdown.selectByVisibleText(text);
	}//End of selectDropDownById()


	public void selectDropDownByName(String name,String text,int locatedWaitInSec)throws Exception
	{
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.name(name)));
		Select dropdown = new Select(driver.findElement(By.name(name)));
		dropdown.selectByVisibleText(text);
	}//End of selectDropDownByName()

	public String getByName(String name,int locatedWaitInSec)throws Exception
	{
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.name(name)));
		String value = driver.findElement(By.name(name)).getAttribute("value").trim();
		return value;
	}//End of getByName()

	public String getTextById( String id ,int locatedWaitInSec)throws Exception
	{
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
		String text = driver.findElement(By.id(id)).getText().trim();
		return text;
	}//End of getTextById()

	public String getValueById( String id ,int locatedWaitInSec)throws Exception
	{
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
		String value = driver.findElement(By.id(id)).getAttribute("value").trim();
		return value;
	}//End of getValueById()

	public void uploadFileByTagName(String filePath,String tagName,int locatedWaitInSec) 
	{
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.name(tagName)));
		WebElement element = driver.findElement(By.name(tagName));
		element.sendKeys(filePath);

	}//End of uploadFile

	public void uploadFileByTagId(String tagId, String filePath, int locatedWaitInSec) 
	{
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id(tagId)));
		WebElement element = driver.findElement(By.id(tagId));
		element.sendKeys(filePath);

	}//End of uploadFile

	public void uploadFileByName(String nameIs, String filePath, int locatedWaitInSec) 
	{
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.name(nameIs)));
		WebElement element = driver.findElement(By.name(nameIs));
		element.sendKeys(filePath);

	}//End of uploadFile

	public void clickByJavaExecutorName(String name, int locatedWaitInSec) 
	{
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.name(name)));
		WebElement element = driver.findElement(By.name(name));
		JavascriptExecutor js = (JavascriptExecutor)driver;
		js.executeScript("arguments[0].click();", element);

	}//End of clickByJavaExecutorName()

	public void clickByJavaExecutorXpath(String xpath, int locatedWaitInSec) 
	{
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
		WebElement element = driver.findElement(By.xpath(xpath));
		JavascriptExecutor js = (JavascriptExecutor)driver;
		js.executeScript("arguments[0].click();", element);

	}//End of clickByJavaExecutorXpath()

	public void clickByJavaExecutorLinkText(String linkText, int locatedWaitInSec) 
	{
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText(linkText)));
		WebElement element = driver.findElement(By.linkText(linkText));
		JavascriptExecutor js = (JavascriptExecutor)driver;
		js.executeScript("arguments[0].click();", element);

	}//End of clickByJavaExecutorLinkText()

	public void clickByJavaExecutorId(String id, int locatedWaitInSec) 
	{
		wait = new WebDriverWait(driver,locatedWaitInSec);  
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
		WebElement element = driver.findElement(By.id(id));
		JavascriptExecutor js = (JavascriptExecutor)driver;
		js.executeScript("arguments[0].click();", element);

	}//End of clickByJavaExecutorId()

	public List<WebElement> getTableRowCount(String tableId, String tableBodyId, String tableRow)
	{
		WebElement assignTable = VyomSeleniumHandler.driver.findElement(By.id(tableId));
		WebElement tableBody = assignTable.findElement(By.tagName(tableBodyId));
		List<WebElement>rows = tableBody.findElements(By.tagName(tableRow));
		return rows;
	}// getRowCount Value

	public void setThreadWait(String propertiesName) throws Exception
	{	
		Thread.sleep(Integer.parseInt(getProperties(propertiesName)));
	}//End of setThreadWait()

	public void closeDriver()
	{
		driver.close();
	}//End of closeDriver()

	public String getDropDownBoxValueByName(String dropDownByName, String optionName, Logger log) throws Exception 
	{
		String remark = "";
		waitForName(dropDownByName, 30);
		WebElement dropdown = VyomSeleniumHandler.driver.findElement(By.name(dropDownByName));
		Select select = new Select(dropdown);
		List<WebElement> options = select.getOptions();
		int n = 1;

		for (WebElement we : options)
		{
			String getOption = we.getText().trim();

			if(getOption.equalsIgnoreCase(optionName))
			{
				remark = "Option Name Found";
				log.info("Option Name Found : " + n + ": " + getOption);//Status Type
				break;
			}else
			{
				remark = "Option Name Not Found :"+optionName;
			}
			n++;
		}
		return remark;
	}//End of getDropDownBoxValue()

	public void clearDownloads(String downloadPath, Logger log) throws Exception 
	{
		try
		{
			File file = new File(downloadPath);

			String[] myFiles;
			if (file.isDirectory())
			{
				myFiles = file.list();
				for (int i = 0; i < myFiles.length; i++) 
				{
					File myFile = new File(file, myFiles[i]);
					log.info("Deleted Files :"+myFile);
					myFile.delete();
				}
			}
		}catch(Exception e)
		{
			log.info("Error in clearDownloads :"+e);
			throw e;
		}
	}//End of clearDownloads

	public static boolean downloadedFile_Check(String reportName, int loopCount, Logger log) throws Exception
	{
		boolean fileDownload = false;
		try
		{
			Thread.sleep(2000);
			log.info("------------------------------------Inside Download Files----------------------------------");
			for(int i=0; i<=loopCount; i++)
			{
				File file = new File(reportName);
				if(file.exists())
				{
					log.info("File Download successfully :"+reportName);
					fileDownload = true;
					break;
				}else
				{
					log.info("Waiting for file download");
					fileDownload = false;
					Thread.sleep(1000);
				}

				if(i==loopCount)
				{
					log.info("Time exceed file not found :"+reportName);
					fileDownload = false;
				}
			}
		}catch(Exception e)
		{
			log.info("Error in downloadedFile_Check :"+e);
			throw e;
		}

		return fileDownload;
	}//End of fileDownloadCheck()
	
	public void textFileRemarkWrite(String txtFilePath, String processRemark, Logger log) throws Exception
	{
		if(!processRemark.equals(""))
		{
			File fileIs = new File(txtFilePath);
			if(fileIs.createNewFile())
			{
				log.info("TXT File is created");
				Thread.sleep(1000);
			}

			FileOutputStream out = new FileOutputStream(txtFilePath); 
			try
			{
				out.flush();
				out.write(processRemark.getBytes());//out.write(remark.getBytes(),0,remark.length());
				out.close();
				log.info("TXT File Writting done :"+processRemark);

			}catch(Exception e)
			{
				log.info("Error in TXT file writting :"+e);
			}
		}else
		{
			log.info("While file writting remark was Blank, File not created");
		}
	}//End of textFileRemarkWrite()
	
	public void moveFilesFromDownload(String fileFrom_Path, String fileTo_Path, String fileNameWithExtension, Logger log) throws Exception
	{
		File dtfFileMoveFrom = new File(fileFrom_Path);
		String destFile = fileTo_Path;
		File toDest=new File(destFile);
		if(!toDest.isDirectory())
		{
			toDest.mkdirs();
		}
		destFile=destFile+"\\"+""+fileNameWithExtension;
		toDest=new File(destFile);

		try
		{
			Files.move(Paths.get(""+dtfFileMoveFrom), Paths.get(""+toDest),StandardCopyOption.REPLACE_EXISTING);
			log.info("File moved successfully to :"+toDest);
			Thread.sleep(2000);
		}catch(Exception e)
		{
			log.info("Processed file not Move :" +e);
		}

	}//End of moveInputFile()

}//End of class
