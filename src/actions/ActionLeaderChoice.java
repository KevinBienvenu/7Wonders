package actions;

import com.google.gson.JsonObject;

import model.Player;
import systems.GameSystem;

public class ActionLeaderChoice extends ActionCard {

	public ActionLeaderChoice(JsonObject input, Player player) {
		super(input, player);
	}

	@Override
	public void playInitialEffect() {
		player.leaderToChoose.add(card);
	}

	@Override
	public boolean update() {
		// TODO Auto-generated method stub
		return true;
	}

}
