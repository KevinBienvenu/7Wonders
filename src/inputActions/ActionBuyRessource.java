package inputActions;

import main.GameSystem;
import ressources.ActionNames;

public class ActionBuyRessource extends Action{
	
	public int idPlayerToPay;
	public int amount;
	
	public ActionBuyRessource(int amount, int idPlayerToPay){
		super(ActionNames.ACTIONBUYRESSOURCE);
		this.amount = amount;
		this.idPlayerToPay = idPlayerToPay;
	}

	@Override
	public void play(GameSystem gs, int idPlayer) {
		gs.board.players.get(idPlayer).coins -= this.amount;
		gs.board.players.get(idPlayerToPay).coins += this.amount;
	}

	@Override
	public boolean update() {
		// TODO Auto-generated method stub
		return true;
	}

}
