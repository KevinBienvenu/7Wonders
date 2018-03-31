package actions;

import java.util.Vector;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import effects.EffectType;
import enums.ActionNames;
import main.Game;
import main.Main;
import model.Building;
import model.Card;
import model.Player;
import render.PlayerRender;
import ressources.Fonts;
import ressources.Images;
import ressources.Sounds;
import systems.GameSystem;

public class ActionGoldFromBank extends Action {
	
	Building leader;
	
	public int amount;
	
	public float x, y;
	public float toX, toY;
	public int time = 0;
	int maxTime = 60;
	public Image im;

	public ActionGoldFromBank(Player player, int amount, Building leader) {
		super(ActionNames.GOLD_FROM_BANK, player);
		this.leader = leader;
		this.amount = amount;
		im = Images.get("piececornerbasgauche");
	}

	@Override
	public void playInitialEffect() {
		Vector<Integer> v = PlayerRender.getRenderDimensionForPlayer(player.id);
		if(this.amount>0){
			x = Game.resX/2;
			y = Game.resY/2;
			toX = v.get(0)+v.get(2)/2;
			toY = v.get(1)+v.get(3)/2;
		} else {
			toX = Game.resX/2;
			toY = Game.resY/2;
			x = v.get(0)+v.get(2)/2;
			y = v.get(1)+v.get(3)/2;
		}
		if(leader!=null){
			player.leaderToShow = leader;
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
			player.coins += this.amount;
			return true;
		}
		return false;
	}
	
	@Override
	public void render(Graphics g) {
		g.drawImage(im,
				x-im.getWidth()-5,
				y-im.getHeight()/2);
		Fonts.font_number.drawString(x, y, "+"+Math.abs(this.amount));
		
	}
}
