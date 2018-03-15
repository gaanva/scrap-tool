package com.rocasolida.scrap;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.rocasolida.FacebookConfig;
import com.rocasolida.entities.Credential;

import lombok.Data;

public @Data class Scrap {
	/**
	 * An IMPLICIT wait is to tell WebDriver to poll the DOM for a certain amount of time 
	 * when trying to find an element or elements if they are not immediately available.
	 */
	private static Integer IMPLICIT_WAIT = 60; 
	/**
	 * An EXPLICIT wait is code you define to wait for a certain condition to occur 
	 * before proceeding further in the code.
	 */
	private static Integer EXPLICIT_WAIT = 60; 
	
	private WebDriver driver;
	private Credential access;
	private WebDriverWait waitDriver;
	private Actions actions;
	public Scrap() {
		
		
		DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
		capabilities.setCapability("phantomjs.binary.path","C:\\Users\\gvaldez\\drivers\\phantomjs.exe");
		capabilities.setCapability("phantomjs.page.settings.userAgent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36");
		capabilities.setCapability("phantomjs.page.settings.loadImages", "false");
		
		//Creo el webdriver
		this.driver = new PhantomJSDriver(capabilities);
		
		this.driver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT, TimeUnit.SECONDS);
		this.waitDriver = new WebDriverWait(this.driver, EXPLICIT_WAIT);
		
		this.setScreenDimension();
		this.driver.manage().window().maximize();
		this.actions = new Actions(this.driver);
		
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
