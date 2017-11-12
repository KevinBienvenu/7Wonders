package inputActions;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Vector;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import Effects.EffectType;
import gameSystem.Building;
import gameSystem.Card;
import ia.GameLog;
import ia.IASystem.IAHandler;
import main.Game;
import main.Main;
import ressources.Data;

public class Communications {

	public static String state = "";

	public static Server server;

	public static boolean receiving = false;

	public static HashMap<Integer, Vector<Action>> actions;
	public static HashMap<Integer, Integer> keys;

	public static void send(HashMap<Integer, State> hashmap){
		actions = new HashMap<Integer, Vector<Action>>();
		keys = new HashMap<Integer, Integer>();
		String url, resp;
		for(Integer i : hashmap.keySet()){
			if(hashmap.keySet().contains(i)){
				try {
					keys.put(i, (int)(Math.random()*1000000));
					hashmap.get(i).setKey(keys.get(i));
					GameLog.state.add(hashmap.get(i).toString());
					if(Main.nbIAPlayer==0){
						// Normal user route
						url = "http://gameserver-kevinbienvenu.c9users.io/users/pushstate";
						resp = sendPost(url, hashmap.get(i).toString());
						actions.put(i, null);
					} else {
						// IA route
						receiving = true;
						url = "http://127.0.0.1:5000/";
						resp = sendPost(url, hashmap.get(i).toString());
						System.out.println("response : "+resp);
						HashMap<String, String> temp_hashmap = Data.gson.fromJson(resp, new TypeToken<HashMap<String, String>>(){}.getType());
						actions.put(i, null);
						receive(i, temp_hashmap);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		receiving = true;
	}

	public static void send(HashMap<Integer, State> hashmap, int i){
	}

	public static void receive(int idJoueur, HashMap<String, String> temp_hashmap){
		if(receiving){
			System.out.println("recieving "+idJoueur);
			if(temp_hashmap.containsKey("action") && temp_hashmap.containsKey("card") && actions.containsKey(idJoueur) && actions.get(idJoueur)==null){

				System.out.print("receiving response for player "+idJoueur);
				GameLog.decisions.add(Data.gson.toJson(temp_hashmap, new TypeToken<HashMap<String, String>>(){}.getType()));
				Communications.actions.put(idJoueur, new Vector<Action>());
				// Trouver la carte en question
				Card c = null;
				Building buriedBuilding = null;
				boolean fromDiscard = false;
				switch(temp_hashmap.get("state")){
				case "StateBuryLeaderChoice":
					for(Building b : Game.gameSystem.board.players.get(idJoueur).buildings){
						if(b.name.equals(temp_hashmap.get("card"))){
							buriedBuilding = b;
							break;
						}
					}
					break;
				case "StateLeaderChoice":
				case "StateCardChoice":
					for(Card c1 : Game.gameSystem.board.players.get(idJoueur).leaderToChoose){
						if(c1.building.name.equals(temp_hashmap.get("card"))){
							c = c1;
							break;
						}
					}
					for(Card c1 : Game.gameSystem.cards.get(idJoueur)){
						if(c1.building.name.equals(temp_hashmap.get("card"))){
							c = c1;
							break;
						}
					}
					break;
				case "StatePlayFromDiscard":
					for(Card c1 : Game.gameSystem.discardedCards){
						if(c1.building.name.equals(temp_hashmap.get("card"))){
							c = c1;
							fromDiscard = true;
						}
					}
					break;
				}
				if(c==null && buriedBuilding==null){
					System.out.println();
					System.out.println("probl�me d'identification - "+temp_hashmap.get("card"));
					for(Card c1 : Game.gameSystem.cards.get(idJoueur)){
						System.out.print(c1.building.name+" - ");
					}
					System.out.println();
					return;
				}
				// Diff�rencier les cas possible
				switch(temp_hashmap.get("action")){
				case "build":
					actions.get(idJoueur).add(new ActionBuilding(c, fromDiscard));
					System.out.println("   building - "+c.building.name);
					break;

				case "freebuild":
					actions.get(idJoueur).add(new ActionBuilding(c, fromDiscard));
					((ActionBuilding)(actions.get(idJoueur).lastElement())).freeBuild = true;
					System.out.println("   free building - "+c.building.name);
					break;

				case "discard":
					actions.get(idJoueur).add(new ActionDiscard(c, fromDiscard));
					System.out.println("   discarding - "+c.building.name);
					break;

				case "wonder":
					actions.get(idJoueur).add(new ActionWonder(Integer.parseInt(temp_hashmap.get("wonderFloor")), c));
					System.out.println("   wondering - "+temp_hashmap.get("wonderFloor")+" - with : "+c.building.name);
					break;
				case "bury":	
					actions.get(idJoueur).add(new ActionBuryLeader(buriedBuilding));
					System.out.println("   burying : "+buriedBuilding.idname);
					break;
				case "leader":
					actions.get(idJoueur).add(new ActionLeaderChoice(c));
					System.out.println("   leadering : "+c.building.name);
					break;

				default:
					break;
				}
				// Checker le commerce
				if(Integer.parseInt(temp_hashmap.get("trade_right"))>0){
					actions.get(idJoueur).add(new ActionBuyRessource(Integer.parseInt(temp_hashmap.get("trade_right")), 
							(idJoueur+Game.gameSystem.nbPlayer-1)%Game.gameSystem.nbPlayer));
					
				}
				if(Integer.parseInt(temp_hashmap.get("trade_left"))>0){
					actions.get(idJoueur).add(new ActionBuyRessource(Integer.parseInt(temp_hashmap.get("trade_left")), 
							(idJoueur+1)%Game.gameSystem.nbPlayer));
				}
				// Checker bilkis
				if(temp_hashmap.get("bilkis").length()>0){
					Game.gameSystem.board.players.get(idJoueur).coins-=1;
				}
			}
		}
	}

	public static boolean isDone(){
		if(actions != null){
			for(Integer i : actions.keySet()){
				if(actions.get(i) == null){
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}
	public static Vector<String> getJoueurEnAttente(){
		Vector<String> v = new Vector<String>();
		if(actions != null){
			for(Integer i : actions.keySet()){
				if(actions.get(i) == null){
					v.add(Game.gameSystem.board.players.get(i).nickName);
				}
			}
		}
		return v;
	}

	private static class Server extends Thread{

		String url; 
		String response;

		public Server(){
			url = "http://gameserver-kevinbienvenu.c9users.io/users/userlist";
		}

		@Override
		public void run(){
			HashMap<String, String> temp_hashmap = new HashMap<String, String>();
			while(true){
				try {
					if(receiving){
						response = Communications.sendGet(url);
						if(response.length()>2){
							temp_hashmap.clear();
							for(String s : response.substring(2, response.length()-2).split("\\}\\,\\{")){
								try {
									temp_hashmap = Data.gson.fromJson("{"+s+"}", new TypeToken<HashMap<String, String>>(){}.getType());
									int idJoueur = Integer.parseInt(temp_hashmap.get("idJoueur"));
									if(temp_hashmap.containsKey("key") && temp_hashmap.containsKey("idJoueur")
											&& temp_hashmap.containsKey("done") && temp_hashmap.get("done").equals("true")
											&& actions.containsKey(idJoueur) && actions.get(idJoueur)==null
											&& keys.containsKey(idJoueur) && keys.get(idJoueur) == Integer.parseInt(temp_hashmap.get("key"))){
										receive(Integer.parseInt(temp_hashmap.get("idJoueur")), temp_hashmap);
									}
								} catch (JsonIOException | JsonSyntaxException e) {
								}
							}
						}
					}
					Thread.sleep(20);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

	}





	private static final String USER_AGENT = "Mozilla/5.0";

	// HTTP GET request
	public static String sendGet(String url) throws Exception {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Cookie", "c9.live.user.click-through=ok");

		//		int responseCode = con.getResponseCode();
		//		System.out.println("\nSending 'GET' request to URL : " + url);
		//		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream(), "UTF-8"));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		return response.toString();

	}

	// HTTP POST request
	public static String sendPost(String url, String urlParameters) throws Exception {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

		//		 charset=UTF-8

		// Send post request
		System.out.println("POST !");
		System.out.println(url);
		System.out.println(urlParameters);
		System.out.println("");
		con.setDoOutput(true);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
		bw.write(urlParameters);
		bw.flush();
		bw.close();
		//		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		//		wr.writeBytes(urlParameters);
		//		wr.flush();
		//		wr.close();
		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream(), "UTF-8"));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		return ""+response.toString();
		//		System.out.println("\nSending 'POST' request to URL : " + url);
		//		System.out.println("Post parameters : " + urlParameters);
		//		System.out.println("Response Code : " + responseCode);


	}

	public static void init() {
		String url = "http://gameserver-kevinbienvenu.c9users.io/users/initgame";
		try {
			System.out.println(sendPost(url, "{\"cmd\":\"init\"}"));
		} catch (Exception e) {}
		if(server == null){
			server = new Server();
			server.start();
		}
	}

}