package inputActions;

import java.util.Vector;

import Effects.EffectType;
import gameSystem.Building;
import gameSystem.Card;
import main.Game;
import ressources.CategoryName;

public class StateBuryLeaderChoice extends State{

	public Vector<Card> cards;
	public int coins;
	public Vector<EffectType> ressources;

	public StateBuryLeaderChoice(int idJoueur) {
		super("StateBuryLeaderChoice", idJoueur);
		this.cards = new Vector<Card>();
		for(Building b : Game.gameSystem.board.players.get(idJoueur).buildings){
			if(b.categoryName==CategoryName.Leader){
				this.cards.add(new Card(b));
			}
		}
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
		s += "\"name\":\""+Game.gameSystem.board.players.get(idJoueur).nickName+"\",";
		s += handleAdditionalInfos(Game.gameSystem.board.players.get(idJoueur));
		s += "\"state\":\""+id+"\",";
		s += "\"time\":\""+Game.gameSystem.t+"\",";
		s += "\"done\":\"false\"}";
		System.out.println(s);
		return s;
	}



}
