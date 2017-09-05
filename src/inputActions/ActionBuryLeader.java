package inputActions;

import java.util.Vector;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import Effects.EffectAction;
import Effects.EffectType;
import gameSystem.Building;
import gameSystem.Card;
import main.Game;
import main.GameSystem;
import render.PlayerRender;
import ressources.ActionNames;
import ressources.Images;

public class ActionBuryLeader extends ActionCard {

	float x, y, toX, toY;
	int time;
	public ActionBuryLeader(Building b) {
		super(ActionNames.ACTIONBURYLEADER, new Card(b));
	}

	@Override
	public void play(GameSystem gs, int idPlayer) {
		Vector<Integer> v = PlayerRender.getRenderDimensionForPlayer(idPlayer);
		x = v.get(0)+v.get(2)/2;
		y = v.get(1)+v.get(3)/2;
		toX = v.get(0)+v.get(2)*(1f-PlayerRender.ratioSizeXName/2);
		toY = v.get(1)+v.get(3)*(1f-(1f-PlayerRender.ratioSizeYName)/2);
		Game.gameSystem.board.players.get(idPlayer).buildings.remove(card.building);
		for(EffectType et : card.building.effects){
			EffectAction.undoaction(et, idPlayer);
		}
		Game.gameSystem.board.players.get(idPlayer).buriedCards.add(card.building);
	}

	@Override
	public boolean update() {
		time++;
		if(time>70 && time%2==0){
			x = (4f*x+toX)/5f;
			y = (3f*y+2f*toY)/5f;
		}
		return true;
	}
	
	@Override
	public void render(Graphics g){
		Image im;
		if(time>70){
			im = Images.get("carteage0");
		} else {
			im = Images.get(card.building.idname);
		}
		if(time<=30){
			im = im.getScaledCopy((time+30f)/30f);
			im.setAlpha(time/30f);
		} else if(time<=70){
			im = im.getScaledCopy((int)(im.getWidth()*((70-x)/40)), im.getHeight());
		} else {
			im = Images.get(card.building.idname);
			if(time<=110){
				im = im.getScaledCopy((int)(im.getWidth()*((x-70)/40)), im.getHeight());
			}
			im = im.getScaledCopy((370f-time)/170f);
			im.setAlpha(Math.min(1f, (370-time)/170f));
		}
		g.drawImage(im, x-im.getWidth()/2, y-im.getHeight()/2);
	}
}
