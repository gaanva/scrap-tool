package com.rocasolida.scrap;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

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
	
	
	public void obtainPublicationsLoggedIn() {
		this.getDriver().navigate().to(FacebookConfig.URL_PROFILE);
		//este while presiona el botón show more pubilcations hasta que la cantidad de publicaciones es mayor a la estática.
		
		while(!(this.getDriver().findElements(By.xpath(FacebookConfig.XPATH_PUBLICATIONS_CONTAINER)).size()>FacebookConfig.CANT_PUBLICATIONS_TO_BE_LOAD)) {
			if((this.existElement(null, FacebookConfig.XPATH_PPAL_BUTTON_SHOW_MORE))) {
				JavascriptExecutor jse = (JavascriptExecutor)this.getDriver();
				/**
				 * TODO Buscar una manera de que espere a que refresque la página
				 * luego de hacer el primer scroll. Sino se ejecuta el scroll unas cuantas veces
				 * hasta que muestra las publicaciones.
				 */
				jse.executeScript("window.scrollTo(0, document.body.scrollHeight)");
				this.waitForJStoLoad();
				System.out.println("[SCROLL] Executed.");
			}else {
				System.out.println("[ERROR] Se esperaba encontrar el botón de Show More. Expression: " + FacebookConfig.XPATH_PPAL_BUTTON_SHOW_MORE);
				break;
			}
		}
		//Extraer las publicaciones.
		List<WebElement> publicationsElements = this.getDriver().findElements(By.xpath(FacebookConfig.XPATH_PUBLICATIONS_CONTAINER));
		System.out.println("[INFO] Cant. Publicaciones presentes en la página: " + publicationsElements.size() );
		
		//Creo la lista que contendrá las publicaciones instanciadas en clases de mi dominio.
		List<Publication> publicationsImpl= new ArrayList<Publication>();
		
		for (int i = 0; i < publicationsElements.size(); i++) {
        	System.out.println("[INFO] SCRAPPING: PUBLICATION NRO: "+ i);
        	
        	Publication aux = new Publication();
        	
        	//Posiciona el cursor en la publicación para que esté visible.
    		this.moveTo(publicationsElements.get(i));
        	
    		//Extraigo los atributs de la publicación en mi instancia.
    		aux = this.extractPublicationData(publicationsElements.get(i));
    		System.out.println("[INFO] PUBLICATION TITLE: "+aux.getTitulo());
    		
    		//Variable para cargar los comentarios de la página
    		List<WebElement> comentarios= new ArrayList<WebElement>();
    		//Variable para guardar lista de comentarios instanciados.
    		List<Comment> comments = new ArrayList<Comment>();
    		
    		//WebElement publicationCommentSection = publicationsElements.get(i).findElement(By.xpath(".//div[@class='_3b-9 _j6a']"));
    		//Tiene comentarios la publicación?
    		//Si la publicación tiene comentarios...
			if(this.existElement(publicationsElements.get(i), FacebookConfig.XPATH_COMMENTS_CONTAINER)) {
				WebElement publicationCommentSection = publicationsElements.get(i).findElement(By.xpath(FacebookConfig.XPATH_COMMENTS_CONTAINER));
				this.ShowMoreClick(publicationCommentSection, FacebookConfig.XPATH_PUBLICATION_VER_MAS_MSJS);
				//Extraigo los comentarios...
        		comentarios = publicationCommentSection.findElements(By.xpath(FacebookConfig.XPATH_COMMENTS));
        		for(int j=0; j<comentarios.size(); j++) {
        			comments.add(this.extractCommentData(comentarios.get(j)));
    	    	}
        		System.out.println("[INFO] CAntidad comentarios: " + comments.size());
			}
    		
    		
    		
    		
    		
    		/*
    		//Si la publicación tiene comentarios...
			if(this.existElement(publicationsElements.get(i), FacebookConfig.XPATH_COMMENTS_CONTAINER)) {
				int cantComentariosIni = 0;
				//Obtengo el elemnto con todos los comentarios...
				WebElement publicationCommentSection = publicationsElements.get(i).findElement(By.xpath(FacebookConfig.XPATH_COMMENTS_CONTAINER));
        		//Extraigo los comentarios...
        		comentarios = publicationCommentSection.findElements(By.xpath(FacebookConfig.XPATH_COMMENTS));
				do {
	    			for(int j=0; j<comentarios.size(); j++) {
	        			comments.add(this.extractCommentData(comentarios.get(j)));
	    	    	}
	        		cantComentariosIni = comentarios.size();
	        		//Hago click en el "Ver Más" comentarios. Si no existe, capturo el error y sigo con la lógica.
	        		this.ShowMoreClickPro(publicationCommentSection, FacebookConfig.XPATH_PUBLICATION_VER_MAS_MSJS);
	        		//SI HAY MAS COMENTARIOS: Se supone que la cantidad de comentarios totales va a ser mayor que la inicial.
	        		comentarios = publicationCommentSection.findElements(By.xpath(FacebookConfig.XPATH_COMMENTS));
	    		}while(cantComentariosIni < comentarios.size());
			}else {
    			aux.setComments(null);
    			System.out.println("[INFO] Publicación sin comentarios!");
    		}
			
    		System.out.println("[INFO] CANTIDAD DE COMENTARIOS: "+ comments.size());
			*/
			aux.setComments(comments);
			publicationsImpl.add(aux);
			
			
		}    		
/*    		
    		- paso 0) hacer click en Ver más mensajes. (VER MAS MSJ LiNK)
			- Buscar contenedor de los comentarios y replies: div[@class='_3b-9 _j6a']
			- Ver mas comentarios?

    		- Recorrer de 1 en 1. child.findElement(By.xpath("//following-sibling::*"));
    		  SI:
    		    - Es comment? exist? (.//span[contains(@class,' UFICommentActorAndBody')])
    		o
    		    - Es Reply? exist? (.//div[contains(@class,' UFIReplyList')])

    		PAra ambos es el mismo tratamiento luego!

*/  		
		this.printPublications(publicationsImpl);
	}
	
	/**
	 * Si existe el botón de show more, entonces lo clickea, hasta que se cargaron todos los mensajes
	 * para luego obtenerlos con un XPATH query y extraerle los datos.
	 * Me servirá para las replies y para los comentarios.
	 */
	public void ShowMoreClick(WebElement container, String xPathExpression) {
		WebElement showMoreLink;
		int i=0;
		while(this.existElement(container, xPathExpression)) {
			i=i+1;
			try {
				showMoreLink = container.findElement(By.xpath(xPathExpression));
				this.moveTo(showMoreLink);
				
	    		try {
	    			showMoreLink.click();
	    			this.waitForJStoLoad();
	    			
	    			
	    			//Esperar a que desaparezca el elemento:
	    			
	    			/* dentro del Anchor. 
	    			 *span[@role='progressbar']
	    			 */
	    		}catch (Exception e){
	    			System.out.println("[ERROR] No se pudo hacer click.");
	    		}
    		//ExpectedConditions.elementToBeClickable(By.id(>someid>))
    		//ESpero a que desaparezca el spinner
    		
			//}   		
    		
    		//Por click carga cerca de 50 mensajes.
			}catch (Exception e) {
				
			}
			
		}
		System.out.println("Click en showMore messgaes button: " + i + " veces");
	}
	
	
	public Comment extractCommentData(WebElement comentario) {
		Comment auxComment = new Comment();
		auxComment.setMensaje(comentario.findElement(By.xpath(FacebookConfig.XPATH_USER_COMMENT)).getText());
		//System.out.println("Comentario "+"nro "+ j +": "+comentarios.get(j).findElement(By.xpath(FacebookConfig.XPATH_USER_COMMENT)).getText());
		return auxComment;
		//Me falta cortar el id, y pasarlo a long.
		//System.out.println("USER ID: "+comentarios.get(j).findElement(By.xpath(FacebookConfig.XPATH_USER_ID_COMMENT)).getAttribute("data-hovercard"));
	}
	
	
	/**
	 * VERSION PRO.
	 * @param container
	 * @param xPathExpression
	 */
	public void ShowMoreClickPro(WebElement container, String xPathExpression) {
		WebElement showMoreLink;
		if(this.existElement(container, xPathExpression)) {
			showMoreLink = container.findElement(By.xpath(xPathExpression));
			this.moveTo(showMoreLink);
			this.waitForJStoLoad();
			try {
    			showMoreLink.click();
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
	private void getPublicationComments(List<WebElement> comments) {
		//Comentarios de una publicación
        for (int i = 0; i < comments.size(); i++) {
        	System.out.println(" =============== "+ i +" DATOS COMENTARIO ================= ");
        	System.out.println(comments.get(i).getText());
        	System.out.println(" ==============="+i+" FIN================= ");
        	
        }
     
        System.out.println("SE ENCONTRARON UN TOTAL DE " + comments.size() + " COMENTARIOS");       
	}
	
	private void loadAllPublicationComments(WebElement publication) {
		boolean isVisibleMoreMsgLnk = true;
		while(isVisibleMoreMsgLnk) {
			try {
				publication.findElement(By.xpath(FacebookConfig.XPATH_PUBLICATION_VER_MAS_MSJS)).click();
	    	} catch (NoSuchElementException e) {
	    	    System.out.println("NO EXISTE BOTON VER MAS MENSAJES EN LA PUBLICACION");
	    	    isVisibleMoreMsgLnk = false;
	    	}
		}
	}	
	
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
	
	private void printPublications(List<Publication> lista) {
		System.out.println("SE ENCONTRARON UN TOTAL DE " + lista.size() + "PUBLICACIONES");
		for (int j = 0; j < lista.size(); j++) {
			System.out.println("============== POS " + j + "===============");
			System.out.println(lista.get(j).toString());
		}
	}
	
}
