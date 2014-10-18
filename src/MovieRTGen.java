import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MovieRTGen {
	
	public void GenerateStopWords(String movieTitle) throws Exception{
		String sURL="http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=vycqgr7g8v9qwbtacpcaf7wf&q=Spiderman&page_limit=1";
		String description="";
		
		URL url=new URL(sURL);
		HttpURLConnection request=(HttpURLConnection) url.openConnection();
		request.connect();
		
		JsonParser jp=new JsonParser();
		JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
		JsonObject rootobj = root.getAsJsonObject(); 
		JsonArray movie = rootobj.get("movies").getAsJsonArray();
		if (movie.size()<1)
			System.exit(0);
		JsonObject movie_details=movie.get(0).getAsJsonObject();
		
		description=description+movie_details.get("synopsis").getAsString();
		
		JsonArray abridged_cast = movie_details.get("abridged_cast").getAsJsonArray();
		
		for (JsonElement cast_obj : abridged_cast){
			
			JsonObject cast_obj_json=cast_obj.getAsJsonObject();
			description=description+ cast_obj_json.get("name").getAsString()+" ";
			
			JsonArray characters=cast_obj_json.get("characters").getAsJsonArray();
			
			for (int i=0;i<characters.size();i++)
			{
				String[] roles=characters.get(i).getAsString().replace("\"","").split("/");
				for(String role:roles){
					description=description+role+" ";
				}
			}
				
		}
		System.out.println(description);		
			
	}
	


}
