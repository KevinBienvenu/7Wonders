package actions;

import java.util.Vector;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import com.google.gson.JsonObject;

import effects.EffectAction;
import effects.EffectType;
import enums.ActionNames;
import enums.CategoryName;
import main.Game;
import main.Main;
import model.Card;
import model.Player;
import render.PlayerRender;
import ressources.Data;
import ressources.Images;
import ressources.Sounds;
import systems.GameSystem;

public class ActionBuilding extends ActionCard{

	public boolean freeBuild = false;	
	public int idPlayer;

	public ActionBuilding(JsonObject input, Player player){
		super(input, player);
		freeBuild = input.get("action").getAsString().equals(ActionNames.FREE_BUILDING.state);
	}	

	public float x, y;
	public float toX, toY;
	public int time = 0;
	public Image im;

	@Override
	public void playInitialEffect() {
		this.idPlayer = player.id;
		GameSystem gs = Game.gameSystem;
		Vector<Integer> v = PlayerRender.getRenderDimensionForPlayer(idPlayer);
		x = Game.resX/2;
		y = Game.resY/2;
		toX = v.get(0)+v.get(2)/2;
		toY = v.get(1)+v.get(3)/2;
		im = Images.get(card.building.idname);
		if(card.building.coinCost>0 && 
				!gs.board.players.get(idPlayer).getCardBuildingPossibilites(card.building).equals("chain")
				&& !freeBuild 
				&& !(gs.board.players.get(idPlayer).specialEffects.contains(EffectType.SpecialGuildsFree) 
						&& card.building.categoryName==CategoryName.Guilds)
				&& !(gs.board.players.get(idPlayer).specialEffects.contains(EffectType.SpecialLeaderFree) 
						&& card.building.categoryName==CategoryName.Leader)){
			if(card.building.categoryName==CategoryName.Leader 
					&& gs.board.players.get(idPlayer).specialEffects.contains(EffectType.SpecialLeaderCheaper2)){
				gs.board.players.get(idPlayer).coins-=Math.max(0,(card.building.coinCost-2));
			} else if (card.building.categoryName==CategoryName.Leader 
					&& gs.board.players.get(idPlayer).specialEffects.contains(EffectType.SpecialLeaderCheaper1)){
				gs.board.players.get(idPlayer).coins-=Math.max(0,card.building.coinCost-1);
			} else {
				gs.board.players.get(idPlayer).coins-=card.building.coinCost;
			}
		}
		if(gs.board.players.get(idPlayer).getCardBuildingPossibilites(card.building).equals("chain")
				&& gs.board.players.get(idPlayer).specialEffects.contains(EffectType.Coins2Chainage)){
			player.actions.add(new ActionGoldFromBank(player, 2, Data.getBuildingByName("vitruve")));
		}
		if(this.card.building.categoryName==CategoryName.Commercial
				&& gs.board.players.get(idPlayer).specialEffects.contains(EffectType.Coins2Commercial)){
			player.actions.add(new ActionGoldFromBank(player, 2, Data.getBuildingByName("xenophon")));
		}
		if(freeBuild){
			gs.board.players.get(idPlayer).hasFreeBuildingLeft = false;
		}

	}
	@Override
	public boolean update() {
		int maxTime = 200;
		if(time==0 && Sounds.isInit()){
			Sounds.get("hammer"+(int)(Math.random()*3+1)).play();
		}
		time++;
		if(time>70 && time%2==0){
			x = (4f*x+toX)/5f;
			y = (3f*y+2f*toY)/5f;
		}
		if(time>maxTime || Main.quickGame){
			this.player.buildings.add(card.building);
			for(EffectType type : card.building.effects){
				EffectAction.action(type, player);
			}
			this.player.majRessources();
			return true;
		}
		return false;
	}

	public void render(Graphics g){
		try{
			if(time<=30){
				im = Images.get(card.building.idname).getScaledCopy((time+30f)/30f);
				im.setAlpha(time/30f);
			} else {
				im = Images.get(card.building.idname).getScaledCopy((370f-time)/170f);
				im.setAlpha(Math.min(1f, (370-time)/170f));
			}
		} catch(NullPointerException e){
			System.err.println(card.building.idname);
		}
		g.drawImage(im, x-im.getWidth()/2, y-im.getHeight()/2);
	}


}
