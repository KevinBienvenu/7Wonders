package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class MainCalculDegueu {

	public static void main(String[] args) throws Exception {
		
		Vector<String> vaneau = new Vector<String>();
		vaneau.add("roger");
		vaneau.remove("roger");
//		JsonObject j = new JsonObject();
//		j.addProperty("mythe", "vaneau");
//		j.addProperty("roger", 1);
//		j.add("array", new JsonArray());
//		j.getAsJsonArray("array").add(new JsonPrimitive("36"));
//		
//		System.out.println(j.toString());
//		URL obj = new URL("http://localhost:3000/test");
//		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//		//add reuqest header
//		con.setRequestMethod("POST");
//		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
//		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//
//		// handle body
//		j.addProperty("gamekey", "123456789");
//		j.addProperty("gameId", "sevenWonders");
//		String urlParameters = j.toString();
//
//		// Send post request
//		System.out.println("POST !");
//		System.out.println(obj);
//		System.out.println(urlParameters);
//		System.out.println("");
//		con.setDoOutput(true);
//		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
//		bw.write(urlParameters);
//		bw.flush();
//		bw.close();
//		BufferedReader in = new BufferedReader(
//				new InputStreamReader(con.getInputStream(), "UTF-8"));
//		String inputLine;
//		StringBuffer response = new StringBuffer();
//
//		while ((inputLine = in.readLine()) != null) {
//			response.append(inputLine);
//		}
//		System.out.println("\nSending 'POST' request to URL : " + obj);
//		System.out.println("Post parameters : " + urlParameters);
//		System.out.println("Response Code : " + response);
	}

}
