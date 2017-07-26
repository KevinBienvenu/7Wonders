package inputActions;

import gameSystem.Card;
import main.GameSystem;
import ressources.ActionNames;

public class ActionLeaderChoice extends ActionCard {

	public ActionLeaderChoice(Card c) {
		super(ActionNames.ACTIONLEADERCHOICE, c);
	}

	@Override
	public void play(GameSystem gs, int idPlayer) {
		gs.board.players.get(idPlayer).leaderToChoose.add(card);
	}

	@Override
	public boolean update() {
		// TODO Auto-generated method stub
		return true;
	}

}
