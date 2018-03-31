package actions;

import java.util.Vector;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import effects.EffectType;
import enums.ActionNames;
import main.Game;
import main.Main;
import model.Card;
import model.Player;
import render.PlayerRender;
import ressources.Data;
import ressources.Fonts;
import ressources.Images;
import ressources.Sounds;
import systems.GameSystem;

public class ActionBuyRessource extends Action{
	
	public int idPlayerToPay;
	public int idPlayer;
	public int amount;
	

	public float x, y;
	public float toX, toY;
	public int time = 0;
	int maxTime = 60;
	public Image im;
	
	public ActionBuyRessource(int amount, int idPlayerToPay, Player player){
		super(ActionNames.BUY_RESSOURCE, player);
		this.amount = amount;
		this.idPlayerToPay = idPlayerToPay;
		im = Images.get("piececornerbasgauche");
	}

	@Override
	public void playInitialEffect() {

		Vector<Integer> v = PlayerRender.getRenderDimensionForPlayer(player.id);
		x = v.get(0)+v.get(2)/2;
		y = v.get(1)+v.get(3)/2;
		v = PlayerRender.getRenderDimensionForPlayer(idPlayerToPay);
		toX = v.get(0)+v.get(2)/2;
		toY = v.get(1)+v.get(3)/2;
		player.coins -= this.amount;
		if(player.specialEffects.contains(EffectType.Coins1Commerce)){
			player.actions.add(new ActionGoldFromBank(player, 1, Data.getBuildingByName("hatshepsout")));
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
			return true;
		}
		return false;
	}
	
	@Override
	public void render(Graphics g) {
		g.drawImage(im,
				x-im.getWidth()-5,
				y-im.getHeight()/2);
		Fonts.font_number.drawString(x, y, "+"+this.amount);
		
	}

}
