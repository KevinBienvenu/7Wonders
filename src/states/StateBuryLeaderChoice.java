package states;

import java.util.Vector;

import effects.EffectType;
import enums.CategoryName;
import enums.StateNames;
import main.Game;
import model.Building;
import model.Card;
import model.Player;

public class StateBuryLeaderChoice extends State{

	public int coins;
	public Vector<EffectType> ressources;

	public StateBuryLeaderChoice(Player player) {
		super(StateNames.BURY_LEADER, player);
		this.cards = new Vector<Card>();
		for(Building b : player.buildings){
			if(b.categoryName==CategoryName.Leader){
				this.cards.add(new Card(b));
			}
		}
		this.ressources = player.ressources;
		this.coins = player.coins;
	}


}
