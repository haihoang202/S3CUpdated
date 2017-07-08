import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Thesaurus {
	private String api_key = "api_key=a2a73e7b926c924fad7001ca3111acd55af2ffabf50eb4ae5";
	private String url = "http://api.wordnik.com:80/v4/word.json/";
	private String option = "/relatedWords?useCanonical=false&";
	private ArrayList<String> synonyms;
	
	public ArrayList<String> getSynonym(String term) {
		// TODO Auto-generated method stub
		synonyms = new ArrayList<>();
		if (term == "" | term == null | term==" ")
			return new ArrayList<>();
		
		try{
			URL request_url = new URL(this.url+term.replace(" ", "%20")+this.option+this.api_key);
			System.out.println("Term is: " + term);
			System.out.println(request_url.toExternalForm());
			HttpURLConnection conn = (HttpURLConnection) request_url.openConnection();
			conn.setRequestMethod("GET");
			int rc = conn.getResponseCode();
			System.out.println("Sending request to "+request_url);
			System.out.println("Response code is: "+rc);
			
			
			if(rc == 200){
				String line = null;
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuilder sb = new StringBuilder();
				
				while ((line = br.readLine()) != null){
					sb.append(line + '\n');
				}
				
				System.out.println(sb);

				JSONArray array = (JSONArray) JSONValue.parse(sb.toString());
				for(int i = 0; i < array.size(); i++){
					JSONObject thisobj = (JSONObject) array.get(i);

					String rela = (String) thisobj.get("relationshipType");
					if( !rela.equals("rhyme")) {
						JSONArray arraysyn = (JSONArray) thisobj.get("words");
						for (int j = 0; j < arraysyn.size(); j++)
							if (!synonyms.contains(arraysyn.get(j)))
								synonyms.add(arraysyn.get(j).toString().replaceAll("[-+.^:,]", ""));
					}

				}					
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return synonyms;
	}	
}