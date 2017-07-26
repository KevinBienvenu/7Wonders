package inputActions;

import java.util.Vector;

import Effects.EffectAction;
import Effects.EffectType;
import gameSystem.Card;
import gameSystem.Player;
import main.Game;
import main.GameSystem;
import render.PlayerRender;
import ressources.ActionNames;
import ressources.CategoryName;
import ressources.Data;
import ressources.Sounds;

public class ActionWonder extends ActionCard{
	
	int idFloor;
	int time = 0;
	int idPlayer;
	
	public ActionWonder(int idFloor, Card card){
		super(ActionNames.ACTIONWONDER, card);
		this.idFloor = idFloor;
	}
	
	@Override
	public void play(GameSystem gs, int idPlayer) {
		Sounds.get("wonder").play();
		Sounds.get("quake").play(1f,3f);
		this.idPlayer = idPlayer;
	}

	@Override
	public boolean update() {
		time++;
		Player p = Game.gameSystem.board.players.get(idPlayer);
		int nbFloor = Data.wonders.get(p.wonderName).floors.get(p.wonderFace).size();
		int sizeY = (int) (Game.resY*PlayerRender.ratioSizeY);
		p.offsetXWonder = (int)(Math.random()*5-2);
		p.offsetYWonder = (int)(-(1f-PlayerRender.ratioSizeYName)*sizeY/(nbFloor+1)*time/220+Math.random()*3-1);
		
		if(time>240){
			p.offsetXWonder = 0;
			p.offsetYWonder = 0;
			p.wonderFloorBuilt.add(idFloor);
			if(Data.wonders.get(p.wonderName).floors.get(p.wonderFace).get(idFloor).coincost>0){
				p.coins-=Data.wonders.get(p.wonderName).floors.get(p.wonderFace).get(idFloor).coincost;
			}
			Game.gameSystem.board.players.get(idPlayer).majRessources();
			for(EffectType type : Data.wonders.get(p.wonderName).floors.get(p.wonderFace).get(idFloor).effects){
				EffectAction.action(type, idPlayer);
			}
			return true;
		}
		return false;
	}
	
	

}
