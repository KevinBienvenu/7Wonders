package inputActions;

import org.newdawn.slick.Graphics;

import main.GameSystem;
import ressources.ActionNames;

public abstract class Action {
	
	public ActionNames action;
	
	public Action(ActionNames action){
		this.action = action;
	}
	
	public abstract void play(GameSystem gs, int idPlayer);
	
	public abstract boolean update();
	
	public void render(Graphics g){
		
	}
}
