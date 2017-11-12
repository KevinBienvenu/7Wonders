package inputActions;

import java.util.Vector;

import Effects.EffectType;
import gameSystem.Card;
import gameSystem.Wonder.Floor;
import main.Game;
import ressources.Data;

public class StatePlayFromDiscard extends State{

	public Vector<Card> cards;
	public int coins;
	public Vector<EffectType> ressources;
	
	public StatePlayFromDiscard(int idJoueur) {
		super("StatePlayFromDiscard", idJoueur);
		cards = Game.gameSystem.discardedCards;
		ressources = Game.gameSystem.board.players.get(idJoueur).ressources;
		coins = Game.gameSystem.board.players.get(idJoueur).coins;
	}

	@Override
	public String toString() {
		String s = "{\"idJoueur\":"+this.idJoueur+",";
		String state;
		// buildings
		s += "\"cards\":[";
		for(Card c : this.cards){
			s+="\""+c.building.name+"\",";
		}
		s = s.substring(0, s.length()-1)+"],";
		s += "\"cardoptions\":{";
		boolean removeComa = false;
		for(Card c : this.cards){
			state = Game.gameSystem.board.players.get(this.idJoueur).getCardBuildingPossibilites(c.building);
			if(!state.equals("alreadybuilt")){
				state = "free";
			}
			if(!state.equals("ok")){
				s+="\""+c.building.name+"\":\""+state+"\",";
				removeComa = true;
			}
		}
		if(removeComa){
			s = s.substring(0, s.length()-1);
		}
		s += "},";
		// wonders
		s += "\"wonderIdName\":\""+this.player.wonderName+"\",";
		s += "\"wonderFace\":\""+this.player.wonderFace+"\",";
		s += "\"wonderFloors\":[],";
		s += "\"ressources\":{"+handleRessources(ressources, true)+"},";
		s += "\"ressourcesleft\":{"+handleRessources(new Vector<EffectType>(), false)+"},";
		s += "\"ressourcesright\":{"+handleRessources(new Vector<EffectType>(), false)+"},";
		s += "\"coins\":"+coins+",";
		s += "\"key\":"+key+",";
		s += "\"time\":\""+Game.gameSystem.t+"\",";
		s += "\"name\":\""+Game.gameSystem.board.players.get(idJoueur).nickName+"\",";
		s += "\"state\":\""+id+"\",";
		s += handleAdditionalInfos(Game.gameSystem.board.players.get(idJoueur));
		s += "\"done\":\"false\"}";
		System.out.println(s);
		return s;
	}

}
