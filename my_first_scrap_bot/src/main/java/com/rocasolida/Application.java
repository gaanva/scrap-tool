package com.rocasolida;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.rocasolida.entities.Credential;
import com.rocasolida.scrap.FacebookScrap;


/*
 * NOTA: Si estás Loggeado, no te muestra por default las historias (o tal vez mi profile tenga algo).
 * Si no estás loggeado, te carga una banda de publicaciones.
 */
@SpringBootApplication
public class Application {
	
	public static void main(String[] args) {
		//Configuración del webdriver
		
		/*
		DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
		capabilities.setCapability("phantomjs.binary.path","C:\\Users\\gvaldez\\drivers\\phantomjs.exe");
		capabilities.setCapability("phantomjs.page.settings.userAgent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36");
		//Creo el webdriver
		WebDriver driver = new PhantomJSDriver(capabilities);
		//Accedo a la página que quiero scrapear
		*/
		Credential access = new Credential("estelaquilmes2018@gmail.com","qsocialnow2018",0L,"");
		FacebookScrap fs = new FacebookScrap();
		System.out.println("[APP]Por hacer login");
		if(fs.login(access)) {			
			//fs.obtainPublicationsAndComments();
			fs.printPublications(fs.obtainPublicationsLoggedIn());
		}
		//SIEMPRE cerrar el navegador. Sino te queda un proceso corriendo for ever "phantomjs.exe".
		fs.quit();
		System.out.println("[APP] FIN");
		
		
	}
}

/*
//Busca el email...
System.out.println("buscando mail");
WebElement email_phone = driver.findElement(By.xpath("//input[@id='email']"));
System.out.println("Encontró mail y lo cargó");
//Carga el campo
email_phone.sendKeys("gaanva@gmail.com");
//Busca el botón y le da Siguiente,
//driver.findElement(By.id("identifierNext")).click();
//Busca el elemento password
WebElement password = driver.findElement(By.xpath("//input[@name='pass']"));
//carga el campo password
password.sendKeys("Antonio12");
//Hace una espera, porque este campo está en la siguiente pantall, luego de cargado el usuario..
//WebDriverWait wait = new WebDriverWait(driver, 20);
//usa la espera hasta que el elemento está clickeable
//wait.until(ExpectedConditions.elementToBeClickable(password));

//Hace el click en el botón siguiente.
driver.findElement(By.id("loginbutton")).click(); 
*/
/*

// Find the Denvycom search input element by its name
WebElement element = driver.findElement(By.id("s"));

// Enter something to search for
element.sendKeys("research");

// Now submit the form. WebDriver will find the form for us from the element
element.submit();

// Check the title of the page
System.out.println("****************************************Page title is: " + driver.getTitle());
// Should see: "All Articles on Denvycom related to the Keyword "Research""
//Get the title of all posts
List<WebElement> titles = driver.findElements(By.cssSelector("h2.page-header"));
List<WebElement> dates = driver.findElements(By.cssSelector("span.entry-date"));
System.out.println(" =============== Denvycom Articles on Research ================= ");
for (int j = 0; j < titles.size(); j++) {
    System.out.println( dates.get(j).getText() + "\t - " + titles.get(j).getText() ) ;
}

//Close the browser
*/

//driver.quit();

/////////////////HELPER//////////////////////
// Now you can do whatever you need to do with it, for example copy somewhere
/*
* File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);

try {
FileUtils.copyFile(scrFile, new File("c:\\tmp\\screenshot1.png"));
} catch (IOException e) {
// TODO Auto-generated catch block
e.printStackTrace();
}
*/