package actions;

import com.google.gson.JsonObject;

import model.Player;
import systems.GameSystem;

public class ActionWonderFaceChoice extends Action {

	String face;
	
	public ActionWonderFaceChoice(JsonObject input, Player player){
		super(input, player);
		this.face = input.get("face").getAsString();
	}

	@Override
	public void playInitialEffect() {
		player.wonderFace = face;
	}

	@Override
	public boolean update() {
		// TODO Auto-generated method stub
		return true;
	}

}
