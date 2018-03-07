package com.rocasolida.scrap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.boot.actuate.autoconfigure.ShellProperties.SpringAuthenticationProperties;

import com.rocasolida.FacebookConfig;
import com.rocasolida.entities.Credential;
import com.rocasolida.entities.Publication;

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
		if(this.access!=null) {
			driver.navigate().to(FacebookConfig.URL);
			driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
			//try login
        	this.login();
        }
		
		/*
		 * [SCREEN SIZE]Debería ser una property de la aplicación. Si no le pones esto, 
		 * cuando queres clickear un elemento, te dice que el elemento no está visible y por ende no lo podés manipular.
		 * Selenium se basa en todo lo que sea Visual en pantalla.
		 */
		driver.manage().window().setSize(new Dimension(1920, 1080)); 
		
		/*
		 *Si accesdes con un usuario de estos que te da brunolidewilde, puede ser que te redireccione a una página de configuracion de la página.
		 *Por esto el acceso al perfil a scrapear lo hago luego de que se loguea. 
		 */
		driver.navigate().to(FacebookConfig.URL_PROFILE);
		driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);	
		
		//Busco todas las publicaciones que se cargaron. (Si entras sin usuario logueado, te carga 16 publicaciones de una vez).
		List<WebElement> publicationsElements = driver.findElements(By.xpath(FacebookConfig.XPATH_PUBLICATIONS_CONTAINER));
		System.out.println("SE ENCONTRARON UN TOTAL DE " + publicationsElements.size() + "PUBLICACIONES");
        
		List<Publication> publicationsImpl= new ArrayList<Publication>();
        
        for (int i = 0; i < publicationsElements.size(); i++) {
        	System.out.println(" =============== "+ i +" DATOS PUBLICACIÓN ================= ");
        	Publication aux = new Publication();
        	
        	/**
        	 * TIMESTAMP
        	 * El timestamp viene en GMT.
        	 */
        	aux.setTimeStamp(Long.parseLong(publicationsElements.get(i).findElement(By.xpath(FacebookConfig.XPATH_PUBLICATION_TIMESTAMP)).getAttribute("data-utime")));
        	System.out.println("TIMESTAMP" + publicationsElements.get(i).findElement(By.xpath(FacebookConfig.XPATH_PUBLICATION_TIMESTAMP)).getAttribute("data-utime"));
        	
        	/**
        	 * TITULO
        	 * TODO HAY QUE VER QUE PASA CUANDO EL TEXTO DEL TITULO ES MUY LARGO... SI RECARGA LA PAGINA O LA MANTIENE EN LA MISMA.
        	 */
        	if(this.existElement(publicationsElements.get(i), FacebookConfig.XPATH_PUBLICATION_TITLE)) {
        		//puede ser que una publicación no tenga título y puede ser que tenga un link de "ver más", al cual hacerle click.
        		this.clickViewMoreTextContent(publicationsElements.get(i), FacebookConfig.XPATH_PUBLICATION_TITLE_VER_MAS);
        		aux.setTitulo(publicationsElements.get(i).findElement(By.xpath(FacebookConfig.XPATH_PUBLICATION_TITLE)).getText());
        		System.out.println("TITULO: "+publicationsElements.get(i).findElement(By.xpath(FacebookConfig.XPATH_PUBLICATION_TITLE)).getText());
        	}else {
        		aux.setTitulo(null);
        		System.out.println("SIN TITULO");
        	}
        	
        	/**
        	 * OWNER
        	 * La pubicación siempre tiene un OWNER.
        	 */
        	aux.setOwner(publicationsElements.get(i).findElement(By.xpath(FacebookConfig.XPATH_PUBLICATION_OWNER)).getText());//.getAttribute("aria-label"));
        	System.out.println("OWNER: " + publicationsElements.get(i).findElement(By.xpath(FacebookConfig.XPATH_PUBLICATION_OWNER)).getText());
        	
        	/**
        	 * DATETIME
        	 * Tener en cuenta que es GMT+4, porque es el del usuario. (controlar cuando la cuenta a scrapear sea de otro país, qué muestra?
        	 * la del usuario que consulta o la del owner de la cuenta?.)
        	 * TODO Si son posts, anteriores al día de la fecha, el formato del String cambia a: martes, 6 de marzo de 2018 a las 6:59
        	 */
        	String d = (publicationsElements.get(i).findElement(By.xpath(FacebookConfig.XPATH_PUBLICATION_TIMESTAMP))).getAttribute("title");
    		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
	        try
	        {        	
	            Date date = simpleDateFormat.parse(d);
	            aux.setDateTime(date);
	            System.out.println("DATETIME : "+simpleDateFormat.format(date));
	        }
	        catch (ParseException ex)
	        {
	            System.out.println("Exception "+ex);
	        }	
        	
	        /**
	         * CANTIDAD DE REPRODUCCIONES
	         */
        	if(this.existElement(publicationsElements.get(i), FacebookConfig.XPATH_PUBLICATION_CANT_REPRO)) {
        		aux.setCantReproducciones(Integer.parseInt(publicationsElements.get(i).findElement(By.xpath(FacebookConfig.XPATH_PUBLICATION_CANT_REPRO)).getText().replaceAll("\\D+","")));
        		System.out.println("CANT REPROS: " + publicationsElements.get(i).findElement(By.xpath(FacebookConfig.XPATH_PUBLICATION_CANT_REPRO)).getText().replaceAll("\\D+",""));
        	}else {
        		aux.setCantReproducciones(null);
        		System.out.println("SIN CANT REPROS");
        	}
        	
        	/**
	         * CANTIDAD DE SHARES
	         */
        	if(this.existElement(publicationsElements.get(i), FacebookConfig.XPATH_PUBLICATION_CANT_SHARE)) {
        		aux.setCantShare(Integer.parseInt(publicationsElements.get(i).findElement(By.xpath(FacebookConfig.XPATH_PUBLICATION_CANT_SHARE)).getText().replaceAll("\\D+","")));
        		System.out.println("Cant SHARE: " + publicationsElements.get(i).findElement(By.xpath(FacebookConfig.XPATH_PUBLICATION_CANT_SHARE)).getText().replaceAll("\\D+",""));
        	}else {
        		aux.setCantShare(0);
        		System.out.println("SIN SHARE!");
        	}
        	
        	aux.toString();
        	publicationsImpl.add(aux);
		}
		
		/*for (int j = 0; j < publicationsImpl.size(); j++) {
			System.out.println("pos " + j);
			((Publication)publicationsImpl.get(j)).toString();
		}*/
        	
        	
        /*	
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
        */	
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
       /* 	
        }
       */
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
			System.out.println("[ERROR]Login error! check credentials provided");
			return false;
    	} catch (NoSuchElementException e) {
    	    System.out.println("[SUCCESS]Login Successfull! "+"usr: "+this.access.getUser());
    	    return true;
    	}
	}
	
	private boolean existElement(WebElement element, String xpathExpression) {
		if((element.findElements(By.xpath(xpathExpression))).size() > 0) {
			return true;
		}else {
			return false;
		}
	}
	
	private void clickViewMoreTextContent(WebElement element, String xpathExpression) {
		boolean verMasClicked = false;
		while(this.existElement(element, xpathExpression) && (!verMasClicked)) {
			WebElement we = element.findElement(By.xpath(xpathExpression));
			if(we.isDisplayed()) {
				we.click();
				verMasClicked = true;
			}else {
				System.out.println("[ERROR] VER MAS TITLE NOT DISPLAYED");
				
			}
		}
				
	}
	
}
