package actions;

import java.util.Vector;

import org.newdawn.slick.Graphics;

import com.google.gson.JsonObject;

import enums.ActionNames;
import main.Game;
import model.Player;
import ressources.Data;
import systems.GameSystem;

public abstract class Action {
	
	public ActionNames action;
	public Player player;
	public int key;
	
	public Action(JsonObject input, Player player){
		for(ActionNames an : ActionNames.values()){
			if(an.state.equals(input.get("action").getAsString())){
				this.action = an;
				break;
			}
		}
		this.player = player;
		this.key = input.get("key").getAsInt();
	}
	
	public Action(ActionNames action, Player player){
		this.action = action;
		this.player = player;
	}
	
	public abstract void playInitialEffect();
	
	public abstract boolean update();
	
	public void render(Graphics g){
		
	}
	
	public static Vector<Action> buildFromJson(JsonObject input, Player player){
		// init
		Vector<Action> actions = new Vector<Action>();
		Action action = null;
		ActionNames actionName = null;
		for(ActionNames an : ActionNames.values()){
			if(an.state.equals(input.get("action").getAsString())){
				actionName = an;
				break;
			}
		}
		// Switch sur le type d'actions
		System.out.println("new action for "+player.nickName+" : "+actionName);
		switch(actionName){
		case BUILDING:
		case FREE_BUILDING:
			action = new ActionBuilding(input, player);
			break;
		case DISCARD:
			action = new ActionDiscard(input, player);
			break;
		case WONDER:
			action = new ActionWonder(input, player);
			break;
		case BURY_LEADER:	
			action = new ActionBuryLeader(input, player);
			break;
		case LEADER_CHOICE:
			action = new ActionLeaderChoice(input, player);
			break;
		case WONDER_FACE_CHOICE:
			action = new ActionWonderFaceChoice(input, player);
			break;
		default:
			System.err.println("Action non reconnue: actions/Action.java l.78");
			break;
		}
		actions.add(action);
		// Checker le commerce
		if(input.get("trade_right")!=null && input.get("trade_right").getAsInt()>0){
			actions.add(new ActionBuyRessource(input.get("trade_right").getAsInt(), 
					(player.id+Game.gameSystem.nbPlayer-1)%Game.gameSystem.nbPlayer,
					player));
		}
		if(input.get("trade_left")!=null && input.get("trade_left").getAsInt()>0){
			actions.add(new ActionBuyRessource(input.get("trade_left").getAsInt(), 
					(player.id+1)%Game.gameSystem.nbPlayer,
					player));
		}
		// Checker bilkis
		if(input.get("bilkis")!=null && input.get("bilkis").getAsString().length()>0){
			actions.add(new ActionGoldFromBank(player, -1, Data.getBuildingByName("bilkis")));
		}
		return actions;
	}
}
