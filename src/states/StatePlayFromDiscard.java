package states;

import java.util.Vector;

import com.google.gson.JsonObject;

import effects.EffectType;
import enums.StateNames;
import main.Game;
import model.Card;
import model.Player;

public class StatePlayFromDiscard extends State{

	public Vector<EffectType> ressources;
	
	public StatePlayFromDiscard(Player player) {
		super(StateNames.PLAY_FROM_DISCARD, player);
		cards = Game.gameSystem.discardedCards;
		ressources = player.ressources;
	}

	@Override
	public void handleBody(JsonObject body) {
		// card options
		this.cards.stream().forEach(card -> {
			body.getAsJsonObject("cardoptions").addProperty(card.building.name, 
					(player.getCardBuildingPossibilites(card.building).equals("alreadybuilt") ? "alreadybuilt" : "free"));
		});
		body.add("ressources", handleRessources(ressources, true));
		
	}
	
}
