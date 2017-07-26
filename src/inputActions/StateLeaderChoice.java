package inputActions;

import java.util.Vector;

import Effects.EffectType;
import gameSystem.Card;
import gameSystem.Wonder.Floor;
import main.Game;
import ressources.Data;

public class StateLeaderChoice extends State{

	public Vector<Card> cards;
	public int coins;
	public Vector<EffectType> ressources;

	public StateLeaderChoice(int idJoueur) {
		super("StateLeaderChoice", idJoueur);
		this.cards = Game.gameSystem.cards.get(idJoueur);
		this.ressources = Game.gameSystem.board.players.get(idJoueur).ressources;
		this.coins = Game.gameSystem.board.players.get(idJoueur).coins;
	}

	@Override
	public String toString() {
		String s = "{\"idJoueur\":"+this.idJoueur+",";
		// buildings
		s += "\"cards\":[";
		for(Card c : this.cards){
			s+="\""+c.building.name+"\",";
		}
		s = s.substring(0, s.length()-1)+"],";
		s += "\"cardoptions\":{},";
		// wonders
		s += "\"wonderIdName\":\""+this.player.wonderName+"\",";
		s += "\"wonderFace\":\""+this.player.wonderFace+"\",";
		s += "\"wonderFloors\":[],";
		s += "\"ressources\":{},";
		s += "\"tradebuildings\":[],";
		s += "\"ressourcesleft\":{},";
		s += "\"ressourcesright\":{},";
		s += "\"specialeffects\":[],";
		s += "\"coins\":"+coins+",";
		s += "\"key\":"+key+",";
		s += "\"state\":\""+id+"\",";
		s += "\"done\":\"false\"}";
		System.out.println(s);
		return s;
	}



}
