package com.rocasolida.scrap;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.rocasolida.FacebookConfig;
import com.rocasolida.entities.Comment;
import com.rocasolida.entities.Credential;
import com.rocasolida.entities.Publication;

import lombok.Data;


public @Data class FacebookScrap extends Scrap{
	
	private Long timeStampCorte; //Un mínimo de fecha en la que tiene que correr. Por default, sería la fecha de Ejecución.
	
	
	public FacebookScrap() {
		super();
	}
	
	public boolean login(Credential access) {
		this.getDriver().navigate().to(FacebookConfig.URL_PROFILE);
		
		if(this.existElement(null, FacebookConfig.XPATH_BUTTON_LOGIN)) {
			WebElement formLogin = this.getDriver().findElement(By.xpath(FacebookConfig.XPATH_FORM_LOGIN));
			formLogin.findElement(By.xpath(FacebookConfig.XPATH_INPUT_MAIL_LOGIN)).sendKeys(access.getUser());
			formLogin.findElement(By.xpath(FacebookConfig.XPATH_INPUT_PASS_LOGIN)).sendKeys(access.getPass());
			formLogin.findElement(By.xpath(FacebookConfig.XPATH_BUTTON_LOGIN)).click();
			if(loggedIn()){
				super.setAccess(access);
				System.out.println("[SUCCESS]Login Successfull! "+"usr: "+this.getAccess().getUser());
				return true;
			}else {
				System.out.println("[ERROR]Check Login Credentials! "+"usr: "+access.getUser());
				return false;
			}
		}
		System.out.println("[ERROR] No se cargó el botón de Login. Expression: " + FacebookConfig.XPATH_FORM_LOGIN);
		return false;
	}
	
	public List<Publication> obtainPublicationsLoggedIn() {
		List<WebElement> publicationsElements = this.inicializePublicationsToBeLoad(/*utimeIni, utimeFin*/);
		List<Publication> publicationsImpl= new ArrayList<Publication>();
		
		for (int i = 0; i < publicationsElements.size(); i++) {
			//Si la publicación cumple los límites del timestamp...
			//if(publicationsElements.get(i).findElements(By.xpath(FacebookConfig.XPATH_PUBLICATION_TIMESTAMP_CONDITION)).size()>0) {
				this.moveTo(publicationsElements.get(i)); //Posiciono el cursor para hacer visible el elemento.
	        	System.out.println("[INFO] EXTRAYENDO DATOS DE LA PUBLICACION NRO#"+i);
	        	//Extraigo los datos de las publicaciones.
	        	publicationsImpl.add(this.extractPublicationData(publicationsElements.get(i)));
		}   	
	        	
		//for (int i = 0; i < publicationsImpl.size(); i++) {
		for (int i = 0; i < 1; i++) {
			System.out.println("[INFO] RELOAD PHANTOMJS. REININICIALIZAR CONEXIÓN...");
			this.refresh();
			System.out.println("[INFO] {fin} RELOAD PHANTOMJS.");
			System.out.println("[INFO] ME DIRIJO A: " + FacebookConfig.URL_POST+publicationsImpl.get(i).getId());
			this.getDriver().navigate().to(FacebookConfig.URL_POST + publicationsImpl.get(i).getId());
			
			try {
				this.getDriver().findElement(By.xpath(FacebookConfig.XPATH_CLOSE_BUTTON)).click();
			}catch(Exception e){
				System.out.println("[INFO] NO SE PUDO HACER CLICK EN CERRAR (X).");
			}
			
        	List<WebElement> pubsNew = this.getDriver().findElements(By.xpath(FacebookConfig.XPATH_PUBLICATIONS_CONTAINER));
        	System.out.println("[INFO] PUBLICATION TITLE: " + publicationsImpl.get(i).getTitulo());
    		//if(this.existElement(publicationsElements.get(i), FacebookConfig.XPATH_COMMENTS_CONTAINER)) {
        	
        	System.out.println("[INFO] EXTRAYENDO COMENTARIOS DE LA PUBLICACIÓN");
    		if(this.existElement(pubsNew.get(0), FacebookConfig.XPATH_COMMENTS_CONTAINER)) {
				publicationsImpl.get(i).setComments(this.obtainAllPublicationComments(pubsNew.get(0).findElement(By.xpath(FacebookConfig.XPATH_COMMENTS_CONTAINER)), FacebookConfig.XPATH_PUBLICATION_VER_MAS_MSJS));
			}
	    }	
		
		return publicationsImpl;
	}
	
	/**
	 * Si existe el botón de show more, entonces lo clickea, hasta que se cargaron todos los mensajes
	 * para luego obtenerlos con un XPATH query y extraerle los datos.
	 * Me servirá para las replies y para los comentarios.
	 */
	public List<Comment> obtainAllPublicationComments(WebElement container, String xPathExpression) {
		WebElement showMoreLink;
		//Variable para cargar los comentarios de la página
		List<WebElement> comentarios= new ArrayList<WebElement>();
		//Variable para guardar lista de comentarios instanciados.
		List<Comment> comments = new ArrayList<Comment>();
		
		int cantIniComentarios = container.findElements(By.xpath(FacebookConfig.XPATH_COMMENTS)).size();
		System.out.println("[INFO] CANTIDAD DE COMENTARIOS INICIAL = " + cantIniComentarios);
		
		showMoreLink = container.findElement(By.xpath(xPathExpression));
		this.moveTo(showMoreLink);
		showMoreLink.click();
		
		int veces = 0;
		while(this.existElement(container, xPathExpression)) {
			try {
				//this.clickViewMoreTextContent(container, xPathExpression);
				showMoreLink = container.findElement(By.xpath(xPathExpression));
				this.moveTo(showMoreLink);
				//this.getWaitDriver().until(ExpectedConditions.invisibility_of_element_located((By.CSS_SELECTOR, '.archive_loading_bar')))
				try {
						
					//Si la cantidad de comentarios en la página es mayor a la última registrada...
					if(container.findElements(By.xpath(FacebookConfig.XPATH_COMMENTS)).size()>cantIniComentarios) {
						//Entonces, se actualizó la llamada.
						System.out.println("[INFO] NUEVA CANT COMENTARIOS: " + container.findElements(By.xpath(FacebookConfig.XPATH_COMMENTS)).size());
						cantIniComentarios=container.findElements(By.xpath(FacebookConfig.XPATH_COMMENTS)).size();
						showMoreLink = container.findElement(By.xpath(xPathExpression));
						this.moveTo(showMoreLink);
						//
						showMoreLink.click();
					}else {
						if(veces<30) {
							veces++;
							this.getWaitDriver().until(ExpectedConditions.invisibilityOfElementLocated((By.xpath("//span[@role='progressbar']"))));
						}else {
							veces=0;
							System.out.println("No se recibe respuesta... se vuelve a clickear");
							//this.saveScreenShot("SINRESPUESTA_SHOWMORE"+String.valueOf(System.currentTimeMillis()));
							showMoreLink = container.findElement(By.xpath(xPathExpression));
							this.moveTo(showMoreLink);
						}
					}
					
					if(cantIniComentarios>1990) {
						System.out.println("[INFO] SE SUPERÓ EL MAX DE COMENTARIOS A PROCESAR.");
						//this.saveScreenShot("APUNTO_CRASHEAR_"+String.valueOf(System.currentTimeMillis()));
						break;
					}					
	    		}catch (Exception e){
	    			System.out.println("[WARN] FIN: No se pudo hacer click en 'Ver Más'. ");
	    			break;
	    		}
    		    
			}catch (Exception e) {
				System.out.println("[ERROR] No se encontró el LINK Ver más.");
				break;
			}
			
		}
		/*
		List<WebElement> commentsAndReplies = container.findElements(By.xpath(FacebookConfig.XPATH_COMMENTS_AND_REPLIES_DIVS));
		for(int k=0; k<commentsAndReplies.size();k++) {
			try {
				commentsAndReplies.get(k).getAttribute("id");
				Comment c = this.extractCommentData(commentsAndReplies.get(k));
				comments.add(c);
			}catch(Exception e){
				System.out.println("[INFO] ES UNA RESPUESTA.");
				//Busco el último comentario...
				this.extractCommentData(commentsAndReplies.get(k));
				commentsAndReplies.get(k).findElement(By.xpath(FacebookConfig.XPATH_PUBLICATION_VER_RESPUESTAS)).click();
				
				comments.get(comments.size()-1);
				
				
				continue;
			}
			if() {
				
			}
		}
		*/
		
		
		
		//this.saveScreenShot("SCREEN_SCRAWLED_"+String.valueOf(System.currentTimeMillis()));
		
		//comentarios = container.findElements(By.xpath(FacebookConfig.XPATH_COMMENTS_BLOCK));
		comentarios = container.findElements(By.xpath(FacebookConfig.XPATH_COMMENT_ROOT_DIV));
		for(int j=0; j<comentarios.size(); j++) {
			comments.add(this.extractCommentData(comentarios.get(j)));
    	}
		
		System.out.println("[INFO] CANTIDAD TOTAL DE COMENTARIOS: " + comments.size());
		return comments;
	}
	
	/**
	 * Se cargan todas las publicaciones del timestamp definido en las variables del CONFIG.
	 * En un futuro llegarían como parámetro.
	 */
	public List<WebElement> inicializePublicationsToBeLoad(/*uTimeIni, uTimeFin*/) {
			this.getDriver().navigate().to(FacebookConfig.URL_PROFILE);
			System.out.println("[INFO] PROFILE PAGE LOADED. "+ FacebookConfig.URL_PROFILE);
			
			//cargo publicaciones hasta que encuentro al menos 1 publicación, que tiene fecha de inicio menor a la uTimeIni.
			while(!((this.getDriver().findElements(By.xpath(FacebookConfig.XPATH_PUBLICATION_TIMESTAMP_CONDITION_SATISFIED)).size())>0)){
			//while(this.continueScroll(pubsLoaded, posIni)){
			//while(!(this.getDriver().findElements(By.xpath(FacebookConfig.XPATH_PUBLICATIONS_CONTAINER)).size()>FacebookConfig.CANT_PUBLICATIONS_TO_BE_LOAD)) {
				//System.out.println("[INFO] WHILE DE ENCONTRAR PUBLICACIONES CON FECHA ANTERIOR A LA INICIAL INGRESADA");
				if((this.existElement(null, FacebookConfig.XPATH_PPAL_BUTTON_SHOW_MORE))) {
					JavascriptExecutor jse = (JavascriptExecutor)this.getDriver();
					/**
					 * TODO Buscar una manera de que espere a que refresque la página
					 * luego de hacer el primer scroll. Sino se ejecuta el scroll unas cuantas veces
					 * hasta que muestra las publicaciones.
					 */
					jse.executeScript("window.scrollTo(0, document.body.scrollHeight)");
					this.waitForJStoLoad();
					System.out.println("[INFO] Scroll down.");
				}else {
					System.out.println("[ERROR] Se esperaba encontrar el botón de Show More. Expression: " + FacebookConfig.XPATH_PPAL_BUTTON_SHOW_MORE);
					break;
				}
			}
			//Trtatando de usar XPATH:
			//public static String XPATH_PUBLICATION_TIMESTAMP_CONDITION = ".//abbr[@data-utime>="+FacebookConfig.uTIME_INI+" and @data-utime=<"+FacebookConfig.uTIME_FIN+"]";
			//public static String XPATH_PUBLICATIONS_CONTAINER = "//div[contains(@class,'userContentWrapper')]";
			
			
			int match = this.getDriver().findElements(By.xpath(FacebookConfig.XPATH_PUBLICATIONS_CONTAINER+FacebookConfig.XPATH_PUBLICATION_TIMESTAMP_CONDITION+"//ancestor::div[contains(@class,'userContentWrapper')]")).size();
			if(match>0){
				System.out.println("[INFO] SE ENCONTRARON "+ String.valueOf(match) + " PUBLICACIONES ENTRE LAS FECHAS > a "+FacebookConfig.uTIME_INI+" y < a "+FacebookConfig.uTIME_FIN);
				return this.getDriver().findElements(By.xpath(FacebookConfig.XPATH_PUBLICATIONS_CONTAINER+FacebookConfig.XPATH_PUBLICATION_TIMESTAMP_CONDITION+"//ancestor::div[contains(@class,'userContentWrapper')]"));
			}else {
				return null;
			}
			
	}
	

	public boolean continueScroll(List<WebElement> pubsLoaded, int posIni) {
		for(int i = posIni; i<pubsLoaded.size(); i++) {
			if(pubsLoaded.get(i).findElements(By.xpath(FacebookConfig.XPATH_PUBLICATION_TIMESTAMP_CONDITION_SATISFIED)).size()>0) {
				return false;
			}
		}
		return true;
	}
	
		
	public Comment extractCommentData(WebElement comentario) {
		Comment auxComment = new Comment();
		//Mensaje
		if(comentario.findElements(By.xpath(FacebookConfig.XPATH_USER_COMMENT)).size()>0) {
			auxComment.setMensaje(comentario.findElement(By.xpath(FacebookConfig.XPATH_USER_COMMENT)).getText());
		}else {
			//Puede ser porque postea solo una imagen...
			auxComment.setMensaje("");
		}
		//Usuario
		String ini="id=";
		String fin="&";
		String pathUserID = comentario.findElement(By.xpath(FacebookConfig.XPATH_USER_ID_COMMENT)).getAttribute("data-hovercard");
		//System.out.println("USERID CORTADO: " + pathUserID.substring(pathUserID.indexOf(ini)+(ini.length()+1),pathUserID.indexOf(fin)));
		auxComment.setUserId(pathUserID.substring(pathUserID.indexOf(ini)+(ini.length()+1),pathUserID.indexOf(fin)));
		
		//Utime
		//System.out.println("USTIME: " + comentario.findElement(By.xpath(FacebookConfig.XPATH_COMMENT_UTIME)).getAttribute("data-utime"));
		auxComment.setUTime(comentario.findElement(By.xpath(FacebookConfig.XPATH_COMMENT_UTIME)).getAttribute("data-utime"));
		return auxComment;
		
		
		
	}
	
	
	/**
	 * Click en el link de "Ver Más".
	 * @param container
	 * @param xPathExpression
	 */
	public void ShowMoreClick(WebElement container, String xPathExpression) {
		WebElement showMoreLink;
		if(this.existElement(container, xPathExpression)) {
			showMoreLink = container.findElement(By.xpath(xPathExpression));
			this.moveTo(showMoreLink);
			try {
    			showMoreLink.click();
    			if(this.waitForJStoLoad()) {
    				System.out.println("[INFO] se hizo click en ver mas!");
    			}else {
    				System.out.println("[INFO] Se superó el tiempo de espera click en ver más...");
    				this.waitForJStoLoad();
    			}
    		}catch (Exception e){
    			System.out.println("[ERROR] No se pudo hacer click en mostrar mas.");
    		}
		}	
	}
	
	
	public boolean waitForJStoLoad() {
		ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
		      @Override
		      public Boolean apply(WebDriver driver) {
		    	  if(((JavascriptExecutor)driver).executeScript("return document.readyState").toString().equals("complete")) {
		    		  return true;
		    	  }else {
		    		  return false;
		    	  }
		    	  
		      }
		 };
	
		 return this.getWaitDriver().until(jsLoad);
	}
	
	public void moveTo(WebElement element) {
		this.getActions().moveToElement(element);
		this.getActions().perform();
	}
	
	public Publication extractPublicationData(WebElement publication){
		Publication aux = new Publication();
		/**
		 * Extraigo ID del post
		 */
		String anchor = publication.findElement(By.xpath(FacebookConfig.XPATH_PUBLICATION_ID_1)).getAttribute("href");
		//POST ID: https://www.facebook.com/mauriciomacri/videos/10156385274043478/
		String[] stringArray = anchor.split("/");
		//System.out.println("POST ID: " + stringArray[stringArray.length-1]);
		aux.setId(stringArray[stringArray.length-1]);
		/**
    	 * TIMESTAMP
    	 * El timestamp viene en GMT.
    	 */
    	aux.setTimeStamp(Long.parseLong(publication.findElement(By.xpath(FacebookConfig.XPATH_PUBLICATION_TIMESTAMP)).getAttribute("data-utime")));
    	
    	/**
    	 * TITULO
    	 * TODO HAY QUE VER QUE PASA CUANDO EL TEXTO DEL TITULO ES MUY LARGO... SI RECARGA LA PAGINA O LA MANTIENE EN LA MISMA.
    	 */
    	if(this.existElement(publication, FacebookConfig.XPATH_PUBLICATION_TITLE)) {
    		//puede ser que una publicación no tenga título y puede ser que tenga un link de "ver más", al cual hacerle click.
    		this.clickViewMoreTextContent(publication, FacebookConfig.XPATH_PUBLICATION_TITLE_VER_MAS);
    		aux.setTitulo(publication.findElement(By.xpath(FacebookConfig.XPATH_PUBLICATION_TITLE)).getText());
    	}else {
    		aux.setTitulo(null);
    	}
    	
    	/**
    	 * OWNER
    	 * La pubicación siempre tiene un OWNER.
    	 */
    	aux.setOwner(publication.findElement(By.xpath(FacebookConfig.XPATH_PUBLICATION_OWNER)).getText());//.getAttribute("aria-label"));
    	/**
    	 * DATETIME
    	 * Tener en cuenta que es GMT+4, porque es el del usuario. (controlar cuando la cuenta a scrapear sea de otro país, qué muestra?
    	 * la del usuario que consulta o la del owner de la cuenta?.)
    	 * TODO Si son posts, anteriores al día de la fecha, el formato del String cambia a: martes, 6 de marzo de 2018 a las 6:59
    	 */
    	String d = (publication.findElement(By.xpath(FacebookConfig.XPATH_PUBLICATION_TIMESTAMP))).getAttribute("title");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        try
        {        	
            Date date = simpleDateFormat.parse(d);
            aux.setDateTime(date);
        }
        catch (ParseException ex)
        {
            System.out.println("Exception "+ex);
        }	
    	
        /**
         * CANTIDAD DE REPRODUCCIONES
         */
    	if(this.existElement(publication, FacebookConfig.XPATH_PUBLICATION_CANT_REPRO)) {
    		aux.setCantReproducciones(Integer.parseInt(publication.findElement(By.xpath(FacebookConfig.XPATH_PUBLICATION_CANT_REPRO)).getText().replaceAll("\\D+","")));
    	}else {
    		aux.setCantReproducciones(null);
    	}
    	/**
         * CANTIDAD DE SHARES
         */
    	if(this.existElement(publication, FacebookConfig.XPATH_PUBLICATION_CANT_SHARE)) {
    		aux.setCantShare(Integer.parseInt(publication.findElement(By.xpath(FacebookConfig.XPATH_PUBLICATION_CANT_SHARE)).getText().replaceAll("\\D+","")));
    	}else {
    		aux.setCantShare(0);
    	}
		
    	return aux;
	}
	
	public void obtainPublicationsAndCommentsNotLoggedIn() {
		this.getDriver().navigate().to(FacebookConfig.URL_PROFILE);
		
		List<WebElement> publicationsElements;
		//Busco todas las publicaciones que se cargaron. (Si entras sin usuario logueado, te carga 16 publicaciones de una vez).
		if(this.existElement(null, FacebookConfig.XPATH_PUBLICATIONS_CONTAINER)) {
			publicationsElements = this.getDriver().findElements(By.xpath(FacebookConfig.XPATH_PUBLICATIONS_CONTAINER));
			List<Publication> publicationsImpl= new ArrayList<Publication>();
	        
	        for (int i = 0; i < publicationsElements.size(); i++) {
	        	System.out.println(" =============== "+ i +" DATOS PUBLICACIÓN ================= ");
	        	Publication aux = new Publication();
	        	
	        	/**
	        	 * TIMESTAMP
	        	 * El timestamp viene en GMT.
	        	 */
	        	aux.setTimeStamp(Long.parseLong(publicationsElements.get(i).findElement(By.xpath(FacebookConfig.XPATH_PUBLICATION_TIMESTAMP)).getAttribute("data-utime")));
	        	
	        	/**
	        	 * TITULO
	        	 * TODO HAY QUE VER QUE PASA CUANDO EL TEXTO DEL TITULO ES MUY LARGO... SI RECARGA LA PAGINA O LA MANTIENE EN LA MISMA.
	        	 */
	        	if(this.existElement(publicationsElements.get(i), FacebookConfig.XPATH_PUBLICATION_TITLE)) {
	        		//puede ser que una publicación no tenga título y puede ser que tenga un link de "ver más", al cual hacerle click.
	        		this.clickViewMoreTextContent(publicationsElements.get(i), FacebookConfig.XPATH_PUBLICATION_TITLE_VER_MAS);
	        		aux.setTitulo(publicationsElements.get(i).findElement(By.xpath(FacebookConfig.XPATH_PUBLICATION_TITLE)).getText());
	        	}else {
	        		aux.setTitulo(null);
	        	}
	        	
	        	/**
	        	 * OWNER
	        	 * La pubicación siempre tiene un OWNER.
	        	 */
	        	aux.setOwner(publicationsElements.get(i).findElement(By.xpath(FacebookConfig.XPATH_PUBLICATION_OWNER)).getText());//.getAttribute("aria-label"));
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
	        	}else {
	        		aux.setCantReproducciones(null);
	        	}
	        	/**
		         * CANTIDAD DE SHARES
		         */
	        	if(this.existElement(publicationsElements.get(i), FacebookConfig.XPATH_PUBLICATION_CANT_SHARE)) {
	        		aux.setCantShare(Integer.parseInt(publicationsElements.get(i).findElement(By.xpath(FacebookConfig.XPATH_PUBLICATION_CANT_SHARE)).getText().replaceAll("\\D+","")));
	        	}else {
	        		aux.setCantShare(0);
	        	}
	        	
	        	//Lo almaceno en un array.
	        	System.out.println("CAPTURADOS_ " + aux.toString());
	        	publicationsImpl.add(aux);
	        
	        }
	        this.printPublications(publicationsImpl);
		}else {
			System.out.println("[ERROR] No se encontraron las publicaciones.");
		}
		
		//Si momento 0 al cargar la página no hay publicaciones, entonces busco el botón más:
		/*
		while(publicationsElements.size()==0) {
			
			if(this.getDriver().findElements(By.xpath(FacebookConfig.XPATH_PPAL_BUTTON_SHOW_MORE)).size()==1) {
				System.out.println("Show more");
				WebDriverWait wait = new WebDriverWait(this.getDriver(), 10); 
				WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(FacebookConfig.XPATH_PPAL_BUTTON_SHOW_MORE)));
				System.out.println("CLICK!");
				element.click();
			}
			if(this.getDriver().findElements(By.xpath(FacebookConfig.XPATH_PUBLICATIONS_CONTAINER)).size()>0) {
				publicationsElements = this.getDriver().findElements(By.xpath(FacebookConfig.XPATH_PUBLICATIONS_CONTAINER));
			}
			
        }
        */
		/*
		publicationsElements = this.getDriver().findElements(By.xpath(FacebookConfig.XPATH_PUBLICATIONS_CONTAINER));
		
		File scrFile2 = ((TakesScreenshot)this.getDriver()).getScreenshotAs(OutputType.FILE);

		try {
		FileUtils.copyFile(scrFile2, new File("c:\\tmp\\screenshot8887.png"));
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		 */
		
        	
        	
        /*	
        	System.out.println(publications.get(i).getText());
        	//La publicacion tiene para ver más comentarios?
            //this.loadAllPublicationComments(publications.get(i));
            
        	
        	//Por ahora solo me fijo 1 vez si tiene el boton de VER MAS COMENTARIOS
            try {
            	publications.get(i).findElement(By.xpath("//a[@class='UFIPagerLink']")).click();
	    	    this.getDriver().manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
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
	private boolean loggedIn() {
		try {
			this.getDriver().findElement(By.xpath(FacebookConfig.XPATH_FORM_LOGIN));
			System.out.println("[ERROR]Login error! check credentials provided");
			return false;
    	} catch (NoSuchElementException e) {
    	    return true;
    	}
	}
	
	private boolean existElement(WebElement element, String xpathExpression) {
		if(element==null)
			return ((this.getDriver().findElements(By.xpath(xpathExpression))).size() > 0);
		else
			return ((element.findElements(By.xpath(xpathExpression))).size() > 0);
		
	}
	
	/**
	 * Es el 'more text' que puede aparecer en el titulo de una publicación cuando es muy larga...
	 * @param element
	 * @param xpathExpression
	 */
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
	
	public void printPublications(List<Publication> lista) {
		System.out.println("SE ENCONTRARON UN TOTAL DE " + lista.size() + "PUBLICACIONES");
		for (int j = 0; j < lista.size(); j++) {
			System.out.println("============== POS " + j + "===============");
			System.out.println(lista.get(j).toString());
		}
	}
	
	private void saveScreenShot(String name) {
		File scrFile = ((TakesScreenshot)this.getDriver()).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(scrFile, new File("c:\\tmp\\"+name+".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
