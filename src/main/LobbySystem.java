package main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import com.google.gson.reflect.TypeToken;

import inputActions.Communications;
import ressources.Data;
import ressources.Images;
import ressources.Musics;
import ressources.WonderName;

public class LobbySystem extends ClassSystem{

	public Vector<LobbyPlayer> players = new Vector<LobbyPlayer>();
	public int time = 120;
	public int timeToUpdate = 120;
	public HashSet<WonderName> wondersSelected = new HashSet<WonderName>();
	
	public boolean loadMusics = false;

	public Vector<WonderName> necessaryWonders = new Vector<WonderName>();

	public WonderName selectNewWonder(){
		WonderName wonder;
		if(necessaryWonders.size()>0){
			wonder = necessaryWonders.remove(0);
		} else {
			do{
				wonder = WonderName.values()[(int)(Math.random()*WonderName.values().length)];
			} while(wondersSelected.contains(wonder));
		}
		wondersSelected.add(wonder);
		return wonder;
//		return WonderName.babylone;
	}

	public LobbySystem(){
		Game.app.setMinimumLogicUpdateInterval(1000/Main.framerate);
		Game.app.setMaximumLogicUpdateInterval(1000/Main.framerate);
		Game.app.setTargetFrameRate(Main.framerate);

	}

	public class LobbyPlayer{
		public WonderName wonder;
		public String face = "";
		public String name;
		public LobbyPlayer(String name){
			this.name = name;
			this.wonder = selectNewWonder();
			String url = "http://gameserver-kevinbienvenu.c9users.io/users/launchgame";
			String data;
			data="{\"name\":\""+name+"\",\"wonder\":\""+wonder.name()+"\"}";
			try {
				Communications.sendPost(url, data);
			} catch (Exception e) {e.printStackTrace();}
		}
		public boolean isReady(){
			return !face.equals("");
		}
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		g.drawImage(Images.get("wonder0"),0,0);
		g.drawImage(Images.get("title-background"),Game.resX/2-Images.get("title-background").getWidth()/2,0);
		String s;
		g.setColor(Color.black);
		g.fillRect(Game.resX/6f, Game.resY*3f/10f, Game.resX*4f/6f, Game.resY*6f/10f);
		g.setColor(Color.white);
		g.drawRect(Game.resX/6f, Game.resY*3f/10f, Game.resX*4f/6f, Game.resY*6f/10f);
		g.drawRect(Game.resX/6f, Game.resY*3f/10f, Game.resX*4f/6f, Game.resY*1f/15f);
		g.drawRect(Game.resX/3f, Game.resY*3f/10f, Game.resX*2f/6f, Game.resY*6f/10f);
		s = "Joueur";
		Data.font_big.drawString(Game.resX*3f/12f-Data.font_big.getWidth(s)/2, Game.resY*3f/10f+3f, s);
		s = "Merveille";
		Data.font_big.drawString(Game.resX*6f/12f-Data.font_big.getWidth(s)/2, Game.resY*3f/10f+3f, s);
		s = "Statut";
		Data.font_big.drawString(Game.resX*9f/12f-Data.font_big.getWidth(s)/2, Game.resY*3f/10f+3f, s);

		int i = 0;
		for(LobbyPlayer player : players){

			Data.font_mid.drawString(Game.resX*3f/12f-Data.font_mid.getWidth(player.name)/2, Game.resY*5.5f/15f+3f+Game.resY/20f*i, player.name);
			Data.font_mid.drawString(Game.resX*6f/12f-Data.font_mid.getWidth(player.wonder.name())/2, Game.resY*5.5f/15f+3f+Game.resY/20f*i, player.wonder.name());
			if(player.face.length()>0)
				Data.font_mid.drawString(Game.resX*9f/12f-Data.font_mid.getWidth("PR�T")/2, Game.resY*5.5f/15f+3f+Game.resY/20f*i, "PR�T");
			i+=1;
		}
	}

	@Override
	public void update(GameContainer gc, int arg1) throws SlickException {
		time+=1;
		boolean b = false;
		if(time>timeToUpdate){
			time = 0;
			b = updatePlayerList();
		}
		Input in = gc.getInput();
		if(in.isKeyPressed(Input.KEY_SPACE) || b){
			launchGame();
		}
	}

	public boolean updatePlayerList(){
		String url = "http://gameserver-kevinbienvenu.c9users.io/users/namelist";
		HashMap<String, String> temp_hashmap;
		boolean b = false, b2 = true;
		Vector<String> vs = new Vector<String>();
		try {
			boolean flagNewPlayer;
			String response = Communications.sendGet(url);
			if(response.length()>4){
				for(String player:response.substring(2,response.length()-2).split("\\}\\,\\{")){
					flagNewPlayer = true;
					temp_hashmap = Data.gson.fromJson("{"+player+"}", new TypeToken<HashMap<String, String>>(){}.getType());
					for(LobbyPlayer lp : players){
						if(lp.name.equals(temp_hashmap.get("name"))){
							flagNewPlayer = false;
							if(temp_hashmap.containsKey("face")){
								lp.face = temp_hashmap.get("face");
							} else {
								b2 = false;
							}
							if(temp_hashmap.get("launch")!=null && temp_hashmap.get("launch").equals("true")) {
								b = true;
								vs.add("{"+player+"}");
							}
							break;
						}
					}
					if(flagNewPlayer){
						players.add(new LobbyPlayer(temp_hashmap.get("name")));
					}
				}
			}
		} catch (Exception e) {e.printStackTrace();}
		if(!b2 || players.size()<3) {
			url = "http://gameserver-kevinbienvenu.c9users.io/users/resetstartgame";
			for(String s : vs) {
				try {
					Communications.sendPost(url, s);
				} catch (Exception e) {}
			}
		}
		return b && b2 && players.size()>2;
	}

	public void launchGame(){
		String url = "http://gameserver-kevinbienvenu.c9users.io/users/launchgame";
		String data;
		int i=0;
		Game.gameSystem = new GameSystem(players);
		for(LobbyPlayer player : players){
			Game.gameSystem.board.players.get(i).nickName = player.name;
			data="{\"name\":\""+player.name+"\",\"idJoueur\":"+i+"}";
			try {
				Communications.sendPost(url, data);
			} catch (Exception e) {e.printStackTrace();}
			i+=1;
		}
		Game.system = Game.gameSystem;
	}

	public void removePlayer(int i){
		this.wondersSelected.remove(this.players.get(i).wonder);
		this.players.remove(i);
	}

}
