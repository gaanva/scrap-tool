package com.rocasolida;


public final class FacebookConfig {
	public static String URL = "https://www.facebook.com/";
	public static String URL_PROFILE =  "https://www.facebook.com/mauriciomacri";
	////title[@lang='en']
	public static Integer CANT_PUBLICATIONS_TO_BE_LOAD = 10;
    
	public static String uTIME_INI="1520985600"; //03/14/2018 @ 12:00am (UTC) - Desde las 0hs del 14/03
	public static String uTIME_FIN="1521072000"; //03/15/2018 @ 12:00am (UTC) - Hasta las 0hs dle 15/03
	
    /**FORM_LOGIN**/
	public static String XPATH_FORM_LOGIN = "//form[contains(@id,'login_form')]";
    public static String XPATH_INPUT_MAIL_LOGIN = ".//input[contains(@id,'email')]"; 
    public static String XPATH_INPUT_PASS_LOGIN = ".//input[contains(@id,'pass')]";
    public static String XPATH_BUTTON_LOGIN = ".//label[contains(@id,'loginbutton')]//input";
    
    /**CARGAR PUBLICACIONES**/
    public static String XPATH_TIMELINE_MAINCOLUMN = "//div[@id='pagelet_timeline_main_column']";
    public static String XPATH_SHOW_ALL_PHOTOS_LINK = "//div[@id='page_photos']//div[@class='_4z-w']//a";
    public static String XPATH_SHOW_ALL_VIDEOS_LINK = "//div[@id='videos']//div[@class='_4z-w']//a";
    public static String XPATH_SHOW_ALL_PUB_DETACADAS_LINK = "//div[@class='_4-u2 _3xaf _3-95 _4-u8']//div[@class='_4z-w']//a";
    
    public static String XPATH_START_PUBLICATIONS_TITLE = "//div[@class='_3-95']//span[text()='Publicaciones']"; //Busqueda de control para saber si ya están cargadas las publicaciones
    //Para cargar publicaciones tengo que contar los hermanos siguientes /following-sibling::div[@class='_4-u2 _4-u8']
    //public static String XPATH_PUBLICATIONS = "//div[@class='_3-95']//div[@class=_4-u2 _4-u8 and not(@id)]"; //Busqueda de control para saber si ya están cargadas las publicaciones
    //Buscar los siguientes a partir del start publications
    
    
    //public static String XPATH_PPAL_BUTTON_SHOW_MORE = "//div[@id='pagelet_timeline_main_column']//a[contains(@class,'uiMorePagerPrimary')]";
    public static String XPATH_PPAL_BUTTON_SHOW_MORE = "//a[contains(@class,'uiMorePagerPrimary')]";
     
    
    /**DATOS DE LA PUBLICACIÓN**/
    
    
    
    public static String XPATH_PUBLICATIONS_CONTAINER = "//div[contains(@class,'userContentWrapper')]";
    public static String XPATH_PUBLICATION_OWNER = ".//span[contains(@class,'fwn fcg')]//span[contains(@class,'fwb')]"; //getAttribute("aria-label")
    //TIEMSTAMP: HUSO HORARIO GMT (sumarle 4 horas para saber fecha de post en Arg.)
    public static String XPATH_PUBLICATION_TIMESTAMP = ".//abbr[contains(@class,'livetimestamp')]"; //getAttribute("data-utime")
    
    
    //Condición por timeStamp
    public static String XPATH_PUBLICATION_TIMESTAMP_CONDITION = ".//abbr[@data-utime>"+FacebookConfig.uTIME_INI+" AND "+"@data-utime<"+FacebookConfig.uTIME_FIN+"]";
    //Esto me sirve para saber cuando debo seguir cargando más publicaciones.
    public static String XPATH_PUBLICATION_TIMESTAMP_CONDITION_SATISFIED = ".//abbr[@data-utime<"+FacebookConfig.uTIME_INI+"]";
    
    
    //DATE_TIME: PONE HUSO HORARIO ARGENTINA (GMT+4). Diff de 4hs.
    public static String XPATH_PUBLICATION_DATE_TIME = ".//abbr[contains(@class,'livetimestamp')]"; //getAttribute("title")
    public static String XPATH_PUBLICATION_TITLE = ".//div[contains(@class,'_5pbx userContent')]";
    public static String XPATH_PUBLICATION_TITLE_VER_MAS = ".//div[contains(@class,'_5pbx userContent')]//a[contains(@class,'see_more_link')]";
    
    public static String XPATH_PUBLICATION_CANT_REPRO = ".//div[contains(@class,'_1t6k')]";
    public static String XPATH_PUBLICATION_CANT_SHARE = ".//a[contains(@class,'UFIShareLink')]";
    
    public static String XPATH_PUBLICATION_VER_MAS_MSJS = ".//a[contains(@class,'UFIPagerLink')]";
    /**DATOS DE LOS MENSAJES**/   
    
    
    /*
     * COMENTARIOS
     */
    public static String XPATH_COMMENTS_CONTAINER = ".//div[contains(@class,'UFIContainer')]";
    public static String XPATH_COMMENTS = ".//span[contains(@class,' UFICommentActorAndBody')]";
    //public static String XPATH_COMMENTS = ".//div//*"; //-->Toma como base el CONTAINER.
    public static String XPATH_USER_ID_COMMENT = ".//a[contains(@class,' UFICommentActorName')]"; //getAttribute("data-hovercard") 
    //RECORTAR desde 'id' hasta '&': /ajax/hovercard/hovercard.php?id=100000706798375&extragetparams=%7B%22is_public%22%3Atrue%2C%22hc_location%22%3A%22ufi%22%7D 
    public static String XPATH_USER_COMMENT = ".//span[contains(@class,'UFICommentBody')]//*";
    
    public static String XPATH_USER_COMMENT_ACTIONS = ".//div[contains(@class,'UFICommentActions')].//abbr";////getAttribute("data-utime")
    
    
    //.findElements(By.xpath("//*[self::a|self::span][@id='foobar']"));
    
    
    //Texto de la publicación
    public static String XPATH_CONTENT_PUBLICATION = "//*[contains(@class,'userContent')]";
	
    
    
    
    //PUBLICACION ENTERA
	private static String PUB_DATA="_5jmm _5pat _3lb4 r_1jzqrqxdnp";//Datos de la publicación ID=hyperfeed_story_id_5a99eaa110be89e22935492 // data-timestamp
	
	//Publicación entera (Publicación + Likes)
	public static String PUB_HEADER_DIV_CLASS = "//div[contains(@class,'userContentWrapper')]";
	//Publication Tag (usuario/Texto/contenido de la publicación)
	private static String PUB_CLASS = "_1dwg _1w_m _q7o";
	
	//#u_k_6 > div._5pcr.userContentWrapper
	////*[@id="u_k_6"]/div[2]
	//Texto de la publicación: //*[@id="js_2b"]/p
	//Hace cuantas horas: //*[@id="js_2a"]
	//Cantidad de reproducciones: //*[@id="u_h_10"]
	//CAntidad de reacciones: //*[@id='js_4xb']
	//CAntidad de compartidos: //*[@id="js_4y4"]
	
	
	
	//Cantidad de comentarios:
	//Cantidad de comentarios: //*[@id="u_h_1c"]/div/div[4]/div/div/div/div[2]/a
	//TimeStamp Tag - Darme cuenta si ya la ví a esta publicacion
	private static Long PUB_TIMESTAMP_TAG; //lo busco por data-utime de una???
	/*
	 Dentro de esta clase: fsm fwn fcg
	 Buscar: <abbr data-utime>
	*/
	//Texto de la publicación
	private static String PUB_TEXT_TAG = "_5pbx userContent _3576";
	
	/*
	 * COMENTARIOS DE LA PUBLICACION
	 */
	private static String COMMENT_CLASS = " UFICommentActorAndBody";
	private static String COMMENT_ACTORNAME_CLASS = " UFICommentActorAndBody";
	private static String COMMENT_BODY_CLASS = " UFICommentBody";
	
			
	//Recorro todos los comentarios:
	private static String PUB_MORE_COMMENTS = "UFIPagerLink";
	
	
	private static String PUBLICATIONS_LOAD_MORE = "_5usd";
	//Pido más historias:<a class="_5usd" href="#" role="button"><span class="fsxl fcg">Más historias</span></a>


	public FacebookConfig() {
		
	}
	
	
	
	//Lista comentarios
	//"div class=UFIList" --> VEO el TOTAL de COMMMENTS // "Comentarios Relevantes" // "Ver Más Comentarios" // Veces compartidas //
	// Si son muchos comentarios, ir a "Ver más comentarios":
	//Antes hay un "Ver más comentarios!" ("UFIPagerLink")
	//Luego un: '<a class="UFIPagerLink" href="#" role="button">Ver 4 comentarios más</a>'
	//El patrón es que desaparezca el "UFIPagerLink".
	
	
	
	
	
	//
	/*
	 * private static String PUB_TIMESTAMP_TAG = "";
	private static String PUB_TIMESTAMP_TAG = "";
	private static String PUB_TIMESTAMP_TAG = "";
	private static String PUB_TIMESTAMP_TAG = "";
	private static String PUB_TIMESTAMP_TAG = "";
	private static String PUB_TIMESTAMP_TAG = "";
	private static String PUB_TIMESTAMP_TAG = "";
	*/
	
	
	
}

/*
 * 
 * - identificar las publicaciones <TAG De publicacion>
- Obtener todas las publicaciones
- recorrer las publicaciones y obtener su TIMESTAMP.<tag de timestamp de publicacion>
- Obtener likes de las publicaciones
- Obtener todos los comentarios de la publicación. <Tag lista de comentarios>
- obtener el usuario que hizo el comentario <tag de usuario q comentó>
- obtener el timestamp del comentario.<tag de timestamp de comentario>
- obtener el texto de los comentarios.<tag texto del comentario>
- Obtener Likes de los comentarios

 * 
 * 
 * */


//**************************TIME STAMP ************************************************* Video/
/*
<span class="fsm fwn fcg">
	<a href="/mauriciomacri/videos/10156342868513478/" aria-label="Vídeo, Story Congreso,, duración: 2 minutos, 25 segundos" data-video-channel-id="55432788477" data-channel-caller="channel_view_from_page_timeline" ajaxify="#" rel="async" class="async_saving _400z _2-40 _5pcq" target="" data-onclick="[[&quot;TahoeController&quot;,&quot;openFromVideoLinkHelper&quot;,{&quot;__elem&quot;:1},&quot;page_timeline&quot;,&quot;unknown&quot;]]">
	  <abbr title="02/03/2018 0:20" data-utime="1519960801" data-shorten="1" class="_5ptz timestamp livetimestamp">
	    <span class="timestampContent" id="js_5z">17 horas</span>
	  </abbr>
	</a>
</span>
*/

