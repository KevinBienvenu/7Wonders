package actions;

import com.google.gson.JsonObject;

import main.Game;
import model.Building;
import model.Card;
import model.Player;

public abstract class ActionCard extends Action{
	
	public Card card;
	public boolean removeCardFromHand = true;
	public boolean removeCardFromDiscard = false;

	public ActionCard(JsonObject input, Player player) {
		super(input, player);
		switch(player.getState().id){
		case BURY_LEADER:
			for(Building b : player.buildings){
				if(b.name.equals(input.get("card").getAsString())){
					this.card = new Card(b);
					break;
				}
			}
			break;
		case LEADER_CHOICE:
			for(Card c1 :player.getCards()){
				if(c1.building.name.equals(input.get("card").getAsString())){
					this.card = c1;
					break;
				}
			}
			break;
		case CARD_CHOICE:
			for(Card c1 : player.leaderToChoose){
				if(c1.building.name.equals(input.get("card").getAsString())){
					this.card = c1;
					break;
				}
			}
			for(Card c1 :player.getCards()){
				if(c1.building.name.equals(input.get("card").getAsString())){
					this.card = c1;
					break;
				}
			}
			break;
		case PLAY_FROM_DISCARD:
			for(Card c1 : Game.gameSystem.discardedCards){
				if(c1.building.name.equals(input.get("card").getAsString())){
					this.card = c1;
					this.removeCardFromDiscard = true;
					this.removeCardFromHand = false;
				}
			}
			break;
		default:
			break;
		}
		if(this.card==null){
			System.out.println("ERREUR : card null");
			System.out.println(input.getAsString());
		}
	}
	




}
