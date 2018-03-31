package systems;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import enums.WonderName;
import ia.GameLog;
import main.Game;
import main.Main;
import model.Player;
import network.Communications;
import network.Routes;
import render.LobbyPlayerRender;
import ressources.Data;
import ressources.Fonts;
import ressources.Images;
import ressources.Sounds;
import states.StateWonderFaceChoice;

public class LobbySystem extends ClassSystem{

	public int time = 120;
	public int timeToUpdate = 120;
	public HashSet<WonderName> wondersSelected = new HashSet<WonderName>();
		    
	public boolean loadMusics = false;
	
	public boolean b = false;

	public Vector<WonderName> necessaryWonders = new Vector<WonderName>();

	public LobbySystem(){
		if(Main.time>0){
			float time = (System.currentTimeMillis() - Main.time)/1000f;
			System.out.println("time: "+time);
			System.out.println();
		}
		Main.time = System.currentTimeMillis();
		Game.app.setMinimumLogicUpdateInterval(1000/Main.framerate);
		Game.app.setMaximumLogicUpdateInterval(1000/Main.framerate);
		Game.app.setTargetFrameRate(Main.framerate);
//		IASystem.close();
//		IASystem.startIA();
//		IASystem.startIA(Main.nbIAPlayer, Main.nbRandPlayer);
//		for(int i=0; i<Main.nbIAPlayer + Main.nbRandPlayer; i++){
//			players.add(new LobbyPlayer("IA_"+i));
//			players.lastElement().face = "A";
//		}
//		necessaryWonders.add(WonderName.olympie);
//		necessaryWonders.add(WonderName.abousimbel);
//		necessaryWonders.add(WonderName.halicarnasse);
	}

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


	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		g.drawImage(Images.get("wonder0"),0,0);
		render(gc, g, true);
	}
	
	public void render(GameContainer gc, Graphics g, boolean logo) throws SlickException{
		if(logo){
			Image image = Images.get("title-background").getScaledCopy(0.5f);
			g.drawImage(image,Game.resX/2-image.getWidth()/2,Game.resY/2-image.getHeight()/2);
		}
		Player player;
		for(int i = Game.gameSystem.board.players.size() -1; i>=0; i--){
			player = Game.gameSystem.board.players.get(i);
			LobbyPlayerRender.render(g, player);
		}
	}

	@Override
	public void update(GameContainer gc, int arg1) throws SlickException {
		time+=1;
		if(time>timeToUpdate){
			time = 0;
			b = updatePlayerList();
		}
		Input in = gc.getInput();
		if(in.isKeyPressed(Input.KEY_SPACE) && b){
			launchGame();
		}
	}

	public boolean updatePlayerList(){
		HashMap<String, String> temp_player;
		boolean everyoneIsReady = Communications.checkIfDone().size()==0;
		try {
			boolean flagNewPlayer;
			String response = Communications.sendGet(Routes.CONNECTED_PLAYERS);
			if(response.length()>4){
				for(String player:response.substring(2,response.length()-2).split("\\}\\,\\{")){
					flagNewPlayer = true;
					temp_player = Data.gson.fromJson("{"+player+"}", new TypeToken<HashMap<String, String>>(){}.getType());
					for(Player lp : Game.gameSystem.board.players){
						if(lp.nickName.equals(temp_player.get("username"))){
							flagNewPlayer = false;
						}
					}
					if(flagNewPlayer && Game.gameSystem.board.players.size()<Main.maxPlayers){
						Player newPlayer = new Player(temp_player.get("username"),selectNewWonder());
						Game.gameSystem.board.players.add(newPlayer);
						Communications.updatePlayerState(newPlayer, new StateWonderFaceChoice(newPlayer));
						Communications.getPlayerStats(newPlayer);
						updatePlayerIds();
						if(Sounds.isInit() && Game.system == Game.lobbySystem){
							Sounds.get("newplayer").play(1f, 1f);;
						}
					}
				}
			}
		} catch (Exception e) {e.printStackTrace();}
		return everyoneIsReady && Game.gameSystem.board.players.size()>2;
	}

	public void updatePlayerIds(){
		int i=0;
		for(Player player : Game.gameSystem.board.players){
			player.id = i;
			i+=1;
		}
	}
	
	public void launchGame(){
		for(Player player : Game.gameSystem.board.players){
			player.actions.get(0).playInitialEffect();
		}
		Game.gameSystem.nbPlayer = Game.gameSystem.board.players.size();
		Game.system = Game.gameSystem;
		GameLog.logwritten = false;
		GameLog.decisions.clear();
		GameLog.state.clear();
	}

	public void removePlayer(int i){
		this.wondersSelected.remove(Game.gameSystem.board.players.get(i).wonderName);
		Game.gameSystem.board.players.remove(i);
		updatePlayerIds();
	}

}
