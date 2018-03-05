package com.rocasolida.scrap;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.rocasolida.FacebookConfig;
import com.rocasolida.entities.Credential;

import lombok.Data;

public @Data class FacebookScrap {
	
	private WebDriver driver;
	private Credential access;
	
	public FacebookScrap(WebDriver driver) {
		super();
		this.driver = driver;
	}
	
	public FacebookScrap(WebDriver driver, Credential access) {
		super();
		this.driver = driver;
		this.access = access;
	}

	public void obtainPublicationsAndComments() {
		driver.navigate().to(FacebookConfig.URL_PROFILE);
        if(this.access!=null) {
        	//try login
        	this.login();
        }
		
		//Espero 5 segundos que cargue la página. (Por lo general tarda el contenido, pero el maquetado HTML en teoría debería estar...)
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);	
		//System.out.println(driver.getPageSource());
		
		//Content General
		//WebElement contentContainer = driver.findElement(By.xpath("//div[@id='content_container']"));
		
		//Busco todas las publicaciones que se cargaron. (Si entras sin usuario logueado, te carga 16 publicaciones de una vez).
		//List<WebElement> publications = contentContainer.findElements(By.xpath(FacebookConfig.XPATH_PUBLICATIONS_CONTAINER));
		List<WebElement> publications = driver.findElements(By.xpath(FacebookConfig.XPATH_PUBLICATIONS_CONTAINER));
        
        
        for (int i = 0; i < publications.size(); i++) {
        	System.out.println("SE ENCONTRARON UN TOTAL DE " + publications.size() + "PUBLICACIONES");
        	System.out.println(" =============== "+ i +" DATOS PUBLICACIÓN ================= ");
        	System.out.println(publications.get(i).getText());
        	//La publicacion tiene para ver más comentarios?
            //this.loadAllPublicationComments(publications.get(i));
            
        	
        	//Por ahora solo me fijo 1 vez si tiene el boton de VER MAS COMENTARIOS
            try {
            	publications.get(i).findElement(By.xpath("//a[@class='UFIPagerLink']")).click();
	    	    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	    	} catch (NoSuchElementException e) {
	    	    System.out.println("Element Not Found");
	    	    
	    	}
            
            List<WebElement> comments = publications.get(i).findElements(By.xpath(FacebookConfig.XPATH_COMMENTS));
            this.obtainPublicationComments(comments);
        	System.out.println(" ==============="+i+" FIN================= ");
        	
        	//this.obtainPublicationComments(publications.get(i));
        	/*
        	 * ESTE ES EL FORMATO DE EXTRACCIÓN:
	        	Mauricio Macri
	        	17 h ·
	        	CON BOLSAS DE COMIDA PARA PERRO FABRICA MOCHILAS
	        	También usa carteles de la vía pública para fabricar bolsos, cartucheras y fundas de skate y surf, mientras les enseña un oficio a vecinos de Melchor Romero. Hoy recibí a Iván en Olivos.
	        	Swahili fundas
	        	320.202 reproducciones
        	*/
        	
        }
        
	}
	
	/*
	 * Pasarle el nodo que contiene los comentarios de una publicación. Me debería devolver la clase Comentario.
	 */
	private void obtainPublicationComments(List<WebElement> comments) {
		//Comentarios de una publicación
        for (int i = 0; i < comments.size(); i++) {
        	System.out.println(" =============== "+ i +" DATOS COMENTARIO ================= ");
        	System.out.println(comments.get(i).getText());
        	System.out.println(" ==============="+i+" FIN================= ");
        	
        }
     
        System.out.println("SE ENCONTRARON UN TOTAL DE " + comments.size() + " COMENTARIOS");       
	}
	
	private void loadAllPublicationComments(WebElement publication) {
		while(true) {
			try {
	    	    publication.findElement(By.xpath("//a[@class='UFIPagerLink']")).click();
	    	    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	    	} catch (NoSuchElementException e) {
	    	    System.out.println("Element Not Found");
	    	    break;
	    	}
		}
	}
	
	
	private boolean login() {
		WebElement formLogin = this.driver.findElement(By.xpath(FacebookConfig.XPATH_FORM_LOGIN));
		formLogin.findElement(By.xpath(FacebookConfig.XPATH_INPUT_MAIL_LOGIN)).sendKeys(this.access.getUser());
		formLogin.findElement(By.xpath(FacebookConfig.XPATH_INPUT_PASS_LOGIN)).sendKeys(this.access.getPass());
		formLogin.findElement(By.xpath(FacebookConfig.XPATH_BUTTON_LOGIN)).click();
		driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
	    return (loggedIn())?true:false;
		
	}
	
	private boolean loggedIn() {
		try {
			this.driver.findElement(By.xpath(FacebookConfig.XPATH_FORM_LOGIN));
			return false;
    	} catch (NoSuchElementException e) {
    	    System.out.println("Login Successfull! "+"usr: "+this.access.getUser());
    	    return true;
    	}
	}
}
