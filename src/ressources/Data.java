package ressources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Vector;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import enums.WonderName;
import model.Building;
import model.Wonder;
import network.Communications;
import network.Routes;

public class Data {

	public static HashMap<String, Building> buildings;
	public static HashMap<String, Building> leaders;
	public static HashMap<WonderName, Wonder> wonders;
	public static Vector<String> buildingNames;
	public static Vector<String> guildNames;
	public static Vector<String> leaderNames;
	
	public static Gson gson = new Gson();
	public static HashMap<String, HashMap<String, String>> loadRepertoire(String nameRepertoire, Vector<String> extensionsFichier){
		HashMap<String, HashMap<String, String>> toReturn = new HashMap<String, HashMap<String, String>>();
		File repertoire = new File(nameRepertoire);
		File[] files=repertoire.listFiles();
		String s;
		for(int i=0; i<files.length; i++){
			s = files[i].getName();
			for(String ext : extensionsFichier){
				if(s.contains("."+ext)){
					// on load la donnee
					try {
						toReturn.put(s.substring(0, s.length()-ext.length()-1).toLowerCase(), 
									gson.fromJson(new JsonReader(new BufferedReader(new InputStreamReader(new FileInputStream(nameRepertoire+s), "UTF-8"))), 
											new TypeToken<HashMap<String, String>>(){}.getType()));
					} catch (JsonIOException | JsonSyntaxException | FileNotFoundException | UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						System.err.println("ERREUR : "+s);
						e.printStackTrace();
					}
					continue;
				} 
			}
			if (!s.contains(".")){
				// nouveau répertoire
				toReturn.putAll(loadRepertoire(nameRepertoire+s+"/", extensionsFichier));
			}
		} 
		return toReturn;
	}
	
	public static HashMap<String, HashMap<String, String>> loadRepertoire(String nameRepertoire, String extension){
		Vector<String> extensions = new Vector<String>();
		extensions.add(extension);
		return loadRepertoire(nameRepertoire, extensions);
	}
	
	public static void init(){
		buildings = new HashMap<String, Building>();
		leaders = new HashMap<String, Building>();
		wonders = new HashMap<WonderName, Wonder>();
		buildingNames = new Vector<String>();
		leaderNames = new Vector<String>();
		guildNames = new Vector<String>();
		// init buildings
		HashMap<String, HashMap<String, String>> h = loadRepertoire("ressources/buildings/", "json");
		for(String key : h.keySet()){
			buildings.put(key, new Building(h.get(key),key));
			if(buildings.get(key).numberPlayerRequired.size()==1 && buildings.get(key).numberPlayerRequired.get(0)==0){
				guildNames.add(key);
			} else {
				buildingNames.add(key);
			}
		}
		// init wonders
		h = loadRepertoire("ressources/wonders/", "json");
		for(String key : h.keySet()){
			wonders.put(WonderName.valueOf(key.replace(" ", "")), new Wonder(h.get(key), key));
		}
		// init leaders
		h = loadRepertoire("ressources/leaders/", "json");
		for(String key : h.keySet()){
			leaders.put(key, new Building(h.get(key),key));
			leaderNames.add(key);
		}
	}
	
	public static void pushBuildingList(){
		for(Building b: buildings.values()){
			try {
				Communications.sendPost(Routes.DATA, b.getJsonForm());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for(Building b: leaders.values()){
			try {
				Communications.sendPost(Routes.DATA, b.getJsonForm());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for(Wonder w : wonders.values()){
			try {
				Communications.sendPost(Routes.DATA, w.getJsonForm());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Building getBuildingByName(String idname){
		for(Building b : Data.buildings.values()){
			if(b.idname.equals(idname)){
				return b;
			}
		}
		for(Building b : Data.leaders.values()){
			if(b.idname.equals(idname)){
				return b;
			}
		}
		return null;
	}
}
