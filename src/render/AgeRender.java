package render;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import main.Game;
import ressources.Images;
import ressources.Sounds;

public class AgeRender {
	
	static boolean hasPlayedTrompette = false;
	static int idAge;
	public static int cooldown;
	
	public static void init(int idAge){
		hasPlayedTrompette = false;
		AgeRender.idAge = idAge;
		cooldown = 400;
		Images.images.put("age"+idAge, Images.get("age"+idAge).getScaledCopy(3f));
	}
	
	public static boolean update(){
		if(!hasPlayedTrompette){
			Game.music.fade(1000, 0, true);
			hasPlayedTrompette = true;
			Sounds.get("trompette").play(1f,0.6f);
		}
		cooldown--;
		if(cooldown>250 && cooldown%2==0){
			Images.images.put("age"+idAge, Images.get("age"+idAge).getScaledCopy(0.98f));
		}
		if(cooldown<30){
			Images.images.get("age"+idAge).setAlpha(1f*cooldown/30f);
		}
		return cooldown<=0;
	}
	
	public static void render(Graphics g){
		Image im = Images.get("age"+idAge);
		g.setColor(new Color(0f,0f,0f,0.6f));
		g.fillRect(0, 0, Game.resX, Game.resY);
		g.drawImage(im,Game.resX/2-im.getWidth()/2, Game.resY/2-im.getHeight()/2-Math.max(200,cooldown)+200);
	}
}
