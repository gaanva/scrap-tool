package com.rocasolida.scrap;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.rocasolida.entities.Credential;

import lombok.Data;

public @Data class Scrap {
	private WebDriver driver;
	private Credential access;
	
	public Scrap() {
		
		
		DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
		capabilities.setCapability("phantomjs.binary.path","C:\\Users\\gvaldez\\drivers\\phantomjs.exe");
		capabilities.setCapability("phantomjs.page.settings.userAgent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36");
		//Creo el webdriver
		this.driver = new PhantomJSDriver(capabilities);
		
		this.setScreenDimension();
		
	}
	
	public void quit() {
		this.driver.quit();
	}
	
	private void setScreenDimension(){
		/*
		 * [SCREEN SIZE]Debería ser una property de la aplicación. Si no le pones esto, 
		 * cuando queres clickear un elemento, te dice que el elemento no está visible y por ende no lo podés manipular.
		 * Selenium se basa en todo lo que sea Visual en pantalla.
		 */
		this.driver.manage().window().setSize(new Dimension(1920, 1080)); 
	}
	
}
