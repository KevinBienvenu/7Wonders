package inputActions;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import Effects.EffectAction;
import Effects.EffectType;
import gameSystem.Card;
import main.Game;
import main.GameSystem;
import ressources.ActionNames;
import ressources.Images;

public class ActionDiscard  extends ActionCard{
	
	public float x, y;
	public float toX, toY;
	public int time = 0;
	public Image im;
	public int idPlayer;
	
	public ActionDiscard(Card card){
		super(ActionNames.ACTIONDISCARD, card);
	}
	public ActionDiscard(Card card, boolean fromDiscard){
		super(ActionNames.ACTIONDISCARD, card, fromDiscard);
	}

	@Override
	public void play(GameSystem gs, int idPlayer) {
		this.idPlayer = idPlayer;
		x = Game.resX/2;
		y = Game.resY/2;
		toX = x;
		toY = Game.resY/5;
		Game.gameSystem.action = this;
	}
	@Override
	public boolean update() {
		time++;
		if(time>70 && time%2==0){
			x = (4f*x+toX)/5f;
			y = (3f*y+2f*toY)/5f;
		}
		if(time>200){
			Game.gameSystem.board.players.get(idPlayer).coins+=3;
			if(!Game.gameSystem.leader){
				Game.gameSystem.discardedCards.add(card);
			}
		}
		return time>200;
	}
	
	public void render(Graphics g){
		if(Game.gameSystem.leader){
			im = Images.get("carteage0");
		} else {
			im = Images.get("carteage"+Game.gameSystem.currentAge.idAge);
		}
		if(time<=30){
			im = im.getScaledCopy((time+30f)/30f);
			im.setAlpha(time/30f);
		} else {
			im = im.getScaledCopy((370f-time)/170f);
			im.setAlpha(Math.min(1f, (370-time)/170f));
		}
		g.drawImage(im, x-im.getWidth()/2, y-im.getHeight()/2);
	}

}
