package states;

import java.util.Vector;

import effects.EffectType;
import enums.StateNames;
import model.Card;
import model.Player;

public class StateLeaderChoice extends State{

	public Vector<EffectType> ressources;

	public StateLeaderChoice(Player player) {
		super(StateNames.LEADER_CHOICE, player);
		this.cards = player.getCards();
		this.ressources = player.ressources;
	}

}
