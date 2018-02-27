package com.rockasolida;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		 // Create a new instance of the Firefox driver
		        // Notice that the remainder of the code relies on the interface,
		        // not the implementation.
		        WebDriver driver = new FirefoxDriver();
		 
		        // And now use this to visit Google
		        driver.get("http://denvycom.com/blog/");
		        // Alternatively the same thing can be done like this
		        // driver.navigate().to("http://www.google.com");
		 
		        
		        //Buscar los elementos para loguearme....
		        /*
				String url = "https://accounts.google.com/signin";
			    driver.get(url);
			    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); 
			    //Busca el email...
			    WebElement email_phone = driver.findElement(By.xpath("//input[@id='identifierId']"));
			    //Carga el campo
			    email_phone.sendKeys("your_email_phone");
			    //Busca el botón y le da Siguiente,
			    driver.findElement(By.id("identifierNext")).click();
			    //Busca el elemento password
			    WebElement password = driver.findElement(By.xpath("//input[@name='password']"));
			    //Hace una espera, porque este campo está en la siguiente pantall, luego de cargado el usuario..
			    WebDriverWait wait = new WebDriverWait(driver, 20);
			    //usa la espera hasta que el elemento está clickeable
			    wait.until(ExpectedConditions.elementToBeClickable(password));
			    //carga el campo password
			    password.sendKeys("your_password");
			    //Hace el click en el botón siguiente.
			    driver.findElement(By.id("passwordNext")).click();; 
		         */
		        
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
		        driver.quit();
	}
}
