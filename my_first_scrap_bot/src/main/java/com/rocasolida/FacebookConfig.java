package com.rocasolida;

import lombok.Data;

public @Data final class FacebookConfig {
	public static String URL = "https://www.facebook.com/";
	public static String URL_PROFILE =  "https://www.facebook.com/mauriciomacri";
    public static String XPATH_PUBLICATIONS_CONTAINER = "//div[contains(@class,'userContentWrapper')]";
    
    /*
     * COMENTARIOS
     */
    public static String XPATH_COMMENTS_CONTAINER = "//div[contains(@class,'UFIContainer')]";
    public static String XPATH_COMMENTS = "//span[contains(@class,' UFICommentActorAndBody')]";
    
    
    
    
    
    
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

