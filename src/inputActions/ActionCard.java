package inputActions;

import gameSystem.Card;
import ressources.ActionNames;

public abstract class ActionCard extends Action{
	
	public Card card;
	public boolean removeCardFromHand = true;
	public boolean removeCardFromDiscard = false;

	public ActionCard(ActionNames action, Card card, boolean discard) {
		super(action);
		this.card = card;
		this.removeCardFromDiscard = discard;
		this.removeCardFromHand = !discard;
	}
	
	public ActionCard(ActionNames action, Card card) {
		super(action);
		this.card = card;
	}




}
