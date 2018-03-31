package network;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Vector;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import actions.Action;
import main.Game;
import main.Main;
import model.Player;
import states.State;

public class Communications {

	public static String state = "";

	public static Server server;

	public static String gamekey, gameowner;

	public static String baseUrl = Main.hostname + ":3001";

	public static String gameId = "sevenWonders";
	
	public static boolean listening = true;


	public static void updatePlayerState(Player player, State newState){
		player.setState(newState);
		try {
			String resp = sendPost(Routes.USERSTATES, player.getState());
			if(resp.contains("err")){
				throw new Exception(resp);
			}
			listening = true;
		} catch (Exception e) {e.printStackTrace();}
	}


	public static Vector<Player> checkIfDone(){
		Vector<Player> playersNotDone = new Vector<>();
		for(Player player : Game.gameSystem.board.players){
			if(player.actions.size()==0 || player.actions.get(0).key != player.getState().key){
				playersNotDone.add(player);
			}
		}
		if(playersNotDone.size()==0){
			listening = false;
		}
		return playersNotDone;
	}

	public static boolean isDone(){
		return false;
	}

	private static class Server extends Thread{

		String response;

		public Server(){
		}

		@Override
		public void run(){
			JsonObject jsonObject;
			while(true){
				try {
					if(listening){
						response = Communications.sendGet(Routes.USERACTIONS);
						if(response.length()>2){
							JsonElement jelement = new JsonParser().parse(response);
							for(JsonElement s : jelement.getAsJsonArray()){
								try {
									jsonObject = s.getAsJsonObject();
									for(Player player : Game.gameSystem.board.players){
										if(player.nickName.equals(jsonObject.get("username").getAsString()) 
												&& player.getState().key == jsonObject.get("key").getAsInt()
												&& (player.actions.size()==0 || player.getState().key != player.actions.get(0).key)
												&& listening){
											player.actions = Action.buildFromJson(jsonObject, player);
											break;
										}
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
	public static String sendGet(Routes route) throws Exception {
		return sendGet(route, new HashMap<String, String>());
	}
	public static String sendGet(Routes route, HashMap<String, String> params) throws Exception {
		params.put("gamekey", gamekey);
		params.put("gameId", gameId);
		String url = baseUrl + route.route;
		if(params.size()>0){
			url += "?" + params.keySet().stream().map(key -> key+"="+params.get(key)).collect(Collectors.joining("&"));
		}
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
	public static String sendPost(Routes route, State state) throws Exception{
		return sendPost(route, state.getBody());
	}
	public static String sendPost(Routes route, JsonObject body) throws Exception {

		URL obj = new URL(baseUrl + route.route);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

		// handle body
		body.addProperty("gamekey", gamekey);
		body.addProperty("gameId", gameId);
		String urlParameters = body.toString();

		// Send post request
		//		System.out.println("Sending 'POST' request to URL : " + obj);
		//		System.out.println("Post parameters : " + urlParameters);
		con.setDoOutput(true);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
		bw.write(urlParameters);
		bw.flush();
		bw.close();
		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream(), "UTF-8"));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		//		System.out.println("Response Code : " + response);
		//		System.out.println();
		return ""+response.toString();


	}

	public static String readFile(String filename){
		String content = "", line;
		try {
			// FileReader reads text files in the default encoding.
			FileReader fileReader = 
					new FileReader(filename);

			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = 
					new BufferedReader(fileReader);

			while((line = bufferedReader.readLine()) != null) {
				content += line;
			}   

			bufferedReader.close();         
		} catch(Exception e) { }
		return content;
	}

	public static void init() {
		gamekey = readFile("gamekey.bmfile");
		gameowner = readFile("gameowner.bmfile");
		JsonObject body = new JsonObject();
		body.addProperty("gameowner", gameowner);
		body.addProperty("maxPlayers", ""+Main.maxPlayers);
		try {
			System.out.println(sendPost(Routes.GAMESLIST, body));
		} catch (Exception e) {}
		if(server == null){
			server = new Server();
			server.start();
		}
	}


	public static void getPlayerStats(Player newPlayer) {
		HashMap<String, String> params = new HashMap<>();
		params.put("username", newPlayer.nickName);
		params.put("gameId", gameId);
		String response;
		try {
			response = Communications.sendGet(Routes.USERSTATS, params);
			JsonElement jelement = new JsonParser().parse(response);
			JsonObject jsonObject = jelement.getAsJsonObject();
			newPlayer.nbGame = jsonObject.get("nbGame").getAsInt();
			newPlayer.bestScore = jsonObject.get("bestScore").getAsInt();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}