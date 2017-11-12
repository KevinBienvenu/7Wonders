package inputActions;

import java.util.Vector;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import Effects.EffectType;
import gameSystem.Card;
import main.Game;
import main.GameSystem;
import main.Main;
import render.PlayerRender;
import ressources.ActionNames;
import ressources.Data;
import ressources.Images;
import ressources.Sounds;

public class ActionBuyRessource extends Action{
	
	public int idPlayerToPay;
	public int idPlayer;
	public int amount;
	

	public float x, y;
	public float toX, toY;
	public int time = 0;
	int maxTime = 60;
	public Image im;
	
	public ActionBuyRessource(int amount, int idPlayerToPay){
		super(ActionNames.ACTIONBUYRESSOURCE);
		this.amount = amount;
		this.idPlayerToPay = idPlayerToPay;
		im = Images.get("piececornerbasgauche");
	}

	@Override
	public void play(GameSystem gs, int idPlayer) {
		this.idPlayer = idPlayer;

		Vector<Integer> v = PlayerRender.getRenderDimensionForPlayer(idPlayer);
		x = v.get(0)+v.get(2)/2;
		y = v.get(1)+v.get(3)/2;
		v = PlayerRender.getRenderDimensionForPlayer(idPlayerToPay);
		toX = v.get(0)+v.get(2)/2;
		toY = v.get(1)+v.get(3)/2;
		gs.board.players.get(idPlayer).coins -= this.amount;
		if(Game.gameSystem.board.players.get(idPlayer).specialEffects.contains(EffectType.Coins1Commerce)){
			Game.gameSystem.board.players.get(idPlayer).leaderToShow = new Card(Data.getBuildingByName("hatshepsout"));
		}
	}

	@Override
	public boolean update() {
		time ++;
		x = (15f*x + toX)/16f;
		y = (15f*y + toY)/16f;
		if(time==maxTime/2) {
			if(Sounds.isInit())
				Sounds.get("coins").play();
		}
		if(time>=maxTime || Main.quickGame){
			Game.gameSystem.board.players.get(idPlayerToPay).coins += this.amount;
			if(Game.gameSystem.board.players.get(idPlayer).specialEffects.contains(EffectType.Coins1Commerce)){
				Game.gameSystem.board.players.get(idPlayer).coins+=1;
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void render(Graphics g) {
		g.drawImage(im,
				x-im.getWidth()-5,
				y-im.getHeight()/2);
		Data.font_number.drawString(x, y, "+"+this.amount);
		
	}

}
