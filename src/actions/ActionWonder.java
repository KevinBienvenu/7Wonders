package actions;

import com.google.gson.JsonObject;

import effects.EffectAction;
import effects.EffectType;
import main.Game;
import main.Main;
import model.Card;
import model.Player;
import render.PlayerRender;
import ressources.Data;
import ressources.Sounds;
import systems.GameSystem;

public class ActionWonder extends ActionCard{
	
	int idFloor;
	int time = 0;
	
	public ActionWonder(JsonObject input, Player player){
		super(input, player);
		this.idFloor = input.get("wonderFloor").getAsInt();
	}
	
	@Override
	public void playInitialEffect() {
	}

	@Override
	public boolean update() {
		if(time==0 && Sounds.isInit()){
			Sounds.get("wonder").play();
			Sounds.get("quake").play(1f,3f);
		}
		time++;
		int nbFloor = Data.wonders.get(player.wonderName).floors.get(player.wonderFace).size();
		int sizeY = (int) (Game.resY*PlayerRender.ratioSizeY);
		player.offsetXWonder = (int)(Math.random()*5-2);
		player.offsetYWonder = (int)(-(1f-PlayerRender.ratioSizeYName)*sizeY/(nbFloor+1)*time/220+Math.random()*3-1);
		
		if(time>240 || Main.quickGame){
			player.offsetXWonder = 0;
			player.offsetYWonder = 0;
			player.wonderFloorBuilt.add(idFloor);
			if(Data.wonders.get(player.wonderName).floors.get(player.wonderFace).get(idFloor).coincost>0){
				player.coins-=Data.wonders.get(player.wonderName).floors.get(player.wonderFace).get(idFloor).coincost;
			}
			player.majRessources();
			for(EffectType type : Data.wonders.get(player.wonderName).floors.get(player.wonderFace).get(idFloor).effects){
				EffectAction.action(type, player);
			}
			return true;
		}
		return false;
	}
	
	

}
