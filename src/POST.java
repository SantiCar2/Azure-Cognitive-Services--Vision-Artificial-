import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Locale.Category;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

import okhttp3.*;


public class POST {
	
	private final static OkHttpClient httpClient = new OkHttpClient(); //Se inicializa la libreria OKHTTP para poder hacer los pedidos POST a Azure

	public static final MediaType MEDIA_TYPE_MARKDOWN
    = MediaType.parse("application/json; charset=utf-8"); //Se define el tipo de datos que se enviaran a Azure

	public boolean keyTest(String key) throws Exception{ //Este es el metodo que verifica la key de la clase splash

		String json = "{'url' : 'https://raw.githubusercontent.com/MicrosoftDocs/mslearn-process-images-with-the-computer-vision-service/master/images/mountains.jpg'}"; //Imagen de referencia para hacer la validacion

		//LA LIBRERIA OKHTTP NO MUESTRA EL RESULTADO JSON ESPERADO DE AZURE, POR ESO SOLO SE USAR PARA VALIDAR LA KEY
		
		Request request = new Request.Builder() //Estructura de como son los pedidos a Azure
				.url("https://westus2.api.cognitive.microsoft.com/vision/v2.0/analyze?visualFeatures=Brands") //1. Link
				.addHeader("Ocp-Apim-Subscription-Key", key) //2. Key
				.addHeader("Content-Type", "application/json") //3. Tipo de datos, en este caso un link
				.post(RequestBody.create(MEDIA_TYPE_MARKDOWN, json)) //Tipo de datos, mas direcciona los datos
				.build(); //Inicializa la variable request
		try (Response response = httpClient.newCall(request).execute()){ //Utiliza la libreria OKHTTP para hacer el request a Azure y espera la respuesta
			
			System.out.println(response.toString());
			
			
			boolean isBadKey = response.toString().indexOf("PermissionDenied") !=-1? true: false; //Si en la respuesta de Azure contiene "PermissionDenied", Retorna un valor falso, ya que no se logro autenticar la key
			if (isBadKey) {
				//return true;
				return false;
				
			}else { //Si se logra autenticar la key, retorna un valor verdadero
				return true;
			}
		}

	}
	

	public void POST(String path, String mode){ //Este es el metodo que se utiliza en la ventana principal "mainwin", y tiene el parametro del modo seleccionado en los radiobutton, y el path de la imagen
		String command;
		main m = new main(); //Obtiene la key que esta previamente guardada y verificada
		String key = m.key;
		
		command = "curl.exe \"https://westus2.api.cognitive.microsoft.com/vision/v2.0/analyze?visualFeatures=" + mode  + "\" -H \"Ocp-Apim-Subscription-Key:"
				+ key + "\" -H \"Content-Type:application/octet-stream\" --data-binary \"@" + path + "\""; //Estructura del comando que se va a ejecutar en PowerShell
		String cmds[] = {"powershell.exe",command}; //Estructura de variable para utilizar en "runtime"
		
		
		Runtime run = Runtime.getRuntime(); //Si inicializa Runitme para ejecutar powershell (en segundo plano)
		Process pr;
		String output = null;
		try {
			pr = run.exec(cmds); //Se ejecuta el comando en PowerShell a travez de Runtime
			pr.waitFor(); //Se espera una respuesta de Azure
			BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream())); //Lee el resultado que devuelve Azure a travez de PowerShell
			String line = ""; //Variable para pasar el resultado
			while ((line=buf.readLine())!=null) { //La primera linea que arroja PowerShell es null, por eso estre proceso se ejecuta 2 veces
				output = line;
			}
			while ((line=buf.readLine())!=null) {
				output = line;
				}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
		
		
		//Inicializa las variables necesarias para empezar a decodificar el JSON retornado por Azure
		JSONParser parser = new JSONParser();
	    String s = output;
	    System.out.println(output);
	    JSONObject jsonObject = null;

	    Object text = null;
	    int cont = 0;
	    String[] tags = null;

	    String brand = null;
	    
	    int confidence = 0;
	      try{
	    	 jsonObject = (JSONObject) parser.parse(s); //Convierte el output de Azure a un objeto json usando la libreria "json-simple"
	         
	         if(mode == "Description") { //Discrimina lo que se va a buscar en json dependiendo de lo seleccionado, ya que Azure devuelve diferentes propiedades
	        	String jsonst1 = jsonObject.get("description").toString(); //Inicia la decodificacion del JSON que Azure retorna
	        	JSONObject jsonObject2  = (JSONObject) parser.parse(jsonst1);
	        	JSONArray tagsJSON = (JSONArray) jsonObject2.get("tags"); //Las tags es una categoria del JSON que varios valores, por eso se usa JSONArray
	        	Iterator<String> iterator = tagsJSON.iterator();
	        	tags = new String[100]; //Segun la documentacion de Azure puede haber un maximo de 100 tags
	        	while (iterator.hasNext()) {
	        		tags[cont] = iterator.next();
	        		cont++;
				}
	        	
	        	
	        	
	        	
	        	JSONArray JSONcaptions = (JSONArray) jsonObject2.get("captions"); //Decodifica otra categoria
	        	Iterator<JSONObject> iterator2 = JSONcaptions.iterator();
	        	int cont1 = 0;
	        	JSONObject t = iterator2.next();
	        	String out = null;
	        	for(int i =0;i<2;i++) { //En esta parte como hay una categoria con dos valores, se ejecuta esto dos veces guardando todo lo necesario de esta categoria
	        		
	        		
	        		if(cont1 == 0) {
	        			text = t.get("text");
	        		}else {
	        			confidence = (int) Math.round((double) t.get("confidence") * 100);
	        		}
	        		cont1++;
	        		System.out.println("Se detecto: " + text);
	        		out = "Se detecto: " + text + "\n";
	        		System.out.println("Con una seguridad del: " + confidence + "%");
	        		out = out + "Con una seguridad del: " + confidence + "%" + "\n" + "Con estos tags:" + "\n";
	        		System.out.println("Con estos tags:");
	        	    for(int j = 0; j <= cont-1;j++) {
	              		System.out.println(tags[j]);
	              		out = out + "    " + tags[j] + "\n";
	              	} //Finalmente se organiza la informacion para mostrarla al usuario
	        	    
	        	}
	        	JOptionPane.showMessageDialog(null, out ,"Resultado", 1); //Por ultimo muestra en un optionpane toda la informacion que Azure retorno
	        	
	         }else if(mode == "Brands") { //El proceso para las otras dos categorias es muy similar, pero con menos categorias.
	        	 confidence = 0;
	        	 System.out.println("Brands");
	        	 String jsonst1 = jsonObject.get("brands").toString();
	        	 JSONArray jsonBrands = (JSONArray) parser.parse(jsonst1);
				 Iterator<JSONObject> iterator = jsonBrands.iterator(); 
				 JSONObject b = iterator.next();
				 brand = b.get("name").toString();
				 confidence = (int) Math.round((double) b.get("confidence") * 100);
	        	 
				 String out = null;
	        	 System.out.println("La marca es: " + brand);
	        	 out = "La marca detectada es: " + brand + "\n";
	        	 System.out.println("Con una seguridad del: " + confidence + "%");
	        	 out = out + "Con una seguridad del: " + confidence + "%" + "\n";
	        	 
	        	 JOptionPane.showMessageDialog(null, out ,"Resultado", 1);
	        	 
	         }else if(mode == "Objects") { //Esta opcion es muy similar a Brands...
	        	 System.out.println("Objects");
	        	 String jsonObj = jsonObject.get("objects").toString();
	        	 JSONArray jsonArr = (JSONArray) parser.parse(jsonObj);
	        	 Iterator<JSONObject> iterator = jsonArr.iterator();
	        	 JSONObject o = iterator.next();
	        	 
	        	 confidence = (int) Math.round((double) o.get("confidence") * 100);
	        	 String out = null;
	        	 System.out.println("Se detecto este objeto: " + o.get("object"));
	        	 out = "Se detecto este objeto: " + o.get("object") + "\n";
	        	 System.out.println("Con una seguridad del: " + confidence + "%");
	        	 out = out + "Con una seguridad del: " + confidence + "%";
	        	 
	        	 JOptionPane.showMessageDialog(null, out ,"Resultado", 1);
	        	 
	         }

	      }catch (Exception e) {
	    	  if(e.toString() != "java.util.NoSuchElementException") {
	    		  System.out.println(e);
	    		  try {
	    			  JOptionPane.showMessageDialog(null, jsonObject.get("code") + "\n" + jsonObject.get("message") + "\n" + "Es posible tu key no tenga suficientes privilegios para analizar esta imagen.", "Error del servidor", 0);
	    		  }catch (Exception error){ //Y si por alguna razon hay otro error desconocido, para evitar que el programa deje de funcionar se muestra en modod de Catch
	    			  JOptionPane.showMessageDialog(null, "Ha ocurrido un error interno en el programa\n" + "Debug info:\n    " + error, "Error del programa", 0);
	    		  }
	    	  }else { //Este error significa que el servidor no retorno nada, entonces se muestra el mensaje al usuario avisando que no se detecto nada.
	    		  System.out.println("No se detectaron " + mode);
	    		  JOptionPane.showMessageDialog(null, "No se detectaron " + mode ,"Resultado", 2);
	    	  }
		}
	      
	      
		
	    
	    
	}
}
	

