package com.rocasolida;


public final class FacebookConfig {
	public static String URL = "https://www.facebook.com/";
	public static String URL_PROFILE =  "https://www.facebook.com/mauriciomacri";
	public static String URL_POST = URL_PROFILE+"/posts/";
	////title[@lang='en']
	public static Integer CANT_PUBLICATIONS_TO_BE_LOAD = 10;
    
	public static String uTIME_INI="1520985600"; //03/14/2018 @ 12:00am (UTC) - Desde las 0hs del 14/03
	public static String uTIME_FIN="1521072000"; //03/14/2018 @ 12:59:59pm (UTC) - Hasta las 0hs dle 15/03
	
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
    
    public static String XPATH_CLOSE_BUTTON = "//a[@class='_xlt _418x']";
    
    
    /**DATOS DE LA PUBLICACIÓN**/
    public static String XPATH_PUBLICATIONS_CONTAINER = "//div[contains(@class,'userContentWrapper')]";
    
    public static String XPATH_PUBLICATION_ID = ".//a[contains(@ajaxify,'ft_id')]";//getAttribute("ajaxify")
    public static String XPATH_PUBLICATION_ID_1 = ".//span[contains(@class,'fsm fwn fcg')]//a";
    
    public static String XPATH_PUBLICATION_OWNER = ".//span[contains(@class,'fwn fcg')]//span[contains(@class,'fwb')]"; //getAttribute("aria-label")
    //TIEMSTAMP: HUSO HORARIO GMT (sumarle 4 horas para saber fecha de post en Arg.)
    public static String XPATH_PUBLICATION_TIMESTAMP = ".//abbr[contains(@class,'livetimestamp')]"; //getAttribute("data-utime")
    
    
    //Condición por timeStamp
    public static String XPATH_PUBLICATION_TIMESTAMP_CONDITION = "//abbr[@data-utime>="+FacebookConfig.uTIME_INI+" and @data-utime<="+FacebookConfig.uTIME_FIN+"]";
    //public static String XPATH_PUBLICATION_TIMESTAMP_CONDITION = ".//abbr[@data-utime=(min('"+FacebookConfig.uTIME_INI+"'), max('"+FacebookConfig.uTIME_FIN+"'))]";
    //Esto me sirve para saber cuando debo seguir cargando más publicaciones.
    public static String XPATH_PUBLICATION_TIMESTAMP_CONDITION_SATISFIED = ".//abbr[@data-utime<"+FacebookConfig.uTIME_INI+"]";
    
    
    //DATE_TIME: PONE HUSO HORARIO ARGENTINA (GMT+4). Diff de 4hs.
    public static String XPATH_PUBLICATION_DATE_TIME = ".//abbr[contains(@class,'livetimestamp')]"; //getAttribute("title")
    public static String XPATH_PUBLICATION_TITLE = ".//div[contains(@class,'_5pbx userContent')]";
    public static String XPATH_PUBLICATION_TITLE_VER_MAS = ".//div[contains(@class,'_5pbx userContent')]//a[contains(@class,'see_more_link')]";
    
    public static String XPATH_PUBLICATION_CANT_REPRO = ".//div[contains(@class,'_1t6k')]";
    public static String XPATH_PUBLICATION_CANT_SHARE = ".//a[contains(@class,'UFIShareLink')]";
    
    public static String XPATH_PUBLICATION_VER_MAS_MSJS = ".//a[contains(@class,'UFIPagerLink')]";
    
    public static String XPATH_PUBLICATION_VER_RESPUESTAS = ".//a[contains(@class,'UFICommentLink')]";
    /**DATOS DE LOS MENSAJES**/   
    
    
    /*
     * COMENTARIOS
     */
    public static String XPATH_COMMENTS_CONTAINER = ".//div[contains(@class,'UFIContainer')]"; //Esto agrupa a TODOS los comentarios/Replies
    
    public static String XPATH_COMMENT_ROOT_DIV = ".//div[starts-with(@id,'comment_js')]"; //Esto agrupa el Comentario. Es el RAIZ del comentario
    public static String XPATH_REPLY_ROOT_DIV = ".//div[@class=' UFIReplyList']";
    public static String XPATH_COMMENTS_AND_REPLIES_DIVS = "//*[starts-with(@class,'UFIRow UFIComment') or contains(@class,'UFIReplyList')]";
    
    public static String XPATH_COMMENTS_BLOCK = ".//div[contains(@class,'UFICommentContentBlock')]";
    public static String XPATH_COMMENTS = ".//span[contains(@class,' UFICommentActorAndBody')]";
    //public static String XPATH_COMMENTS = ".//div//*"; //-->Toma como base el CONTAINER.
    public static String XPATH_USER_ID_COMMENT = ".//a[contains(@class,' UFICommentActorName')]"; //getAttribute("data-hovercard") 
    public static String XPATH_USER_COMMENT = ".//span[contains(@class,'UFICommentBody')]//*";
    public static String XPATH_COMMENT_UTIME = ".//abbr[contains(@class,'UFISutroCommentTimestamp livetimestamp')]";
    public static String XPATH_USER_COMMENT_ACTIONS = ".//div[contains(@class,'UFICommentActions')].//abbr";////getAttribute("data-utime")
    
    //div[contains(@class,' UFIReplyList')]
    
    
    
	public FacebookConfig() {
		
	}
	
	
	
	//Lista comentarios
	//"div class=UFIList" --> VEO el TOTAL de COMMMENTS // "Comentarios Relevantes" // "Ver Más Comentarios" // Veces compartidas //
	// Si son muchos comentarios, ir a "Ver más comentarios":
	//Antes hay un "Ver más comentarios!" ("UFIPagerLink")
	//Luego un: '<a class="UFIPagerLink" href="#" role="button">Ver 4 comentarios más</a>'
	//El patrón es que desaparezca el "UFIPagerLink".
	
	
		
}

