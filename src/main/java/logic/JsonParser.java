package logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonParser {

	public JsonParser() {
		// TODO Auto-generated constructor stub
	}
	
	public static Questions[] parseQuestions(String url){
		Questions[] questions;
		
		String jsonString = getJSonText(url);
		JSONObject rootObject = new JSONObject(jsonString);
		JSONArray questionJsonArray = rootObject.getJSONArray("questions");
		
		questions = new Questions[questionJsonArray.length()];;
		for(int i=0; i < questionJsonArray.length(); i++) 
		{
			JSONObject question = questionJsonArray.getJSONObject(i);
			questions[i] = new Questions(question.getString("Question"), question.getString("Answer"));
		}
		
		return questions;
	}
	
	public static String getJSonText(String url)
	{
		String result = "";
		try
		{
			File f = new File(url);
			result = "";
			FileReader fr = new FileReader(f);
			org.json.JSONTokener j = new JSONTokener(fr);
			while(j.more())
			{
				result += j.next();
			}
			fr.close();
		} catch (FileNotFoundException e){} catch (JSONException e){} catch (IOException e){}
		
		return result.replace("\\\"", "\"");
	}
}
