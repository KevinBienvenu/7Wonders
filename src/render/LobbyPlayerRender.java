package render;


import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import main.Game;
import model.Player;
import network.Communications;
import ressources.Fonts;
import ressources.Images;

public class LobbyPlayerRender {
	
	
	public static void render(Graphics g, Player p){
		double angle = (float) (2*Math.PI*(Game.gameSystem.board.players.size()-p.id-1)/(Game.gameSystem.board.players.size()));
		if(p.angle<angle){
			p.angle=Math.min(angle, p.angle+0.09d);
		}
		if(p.opacity<=1f){
			p.opacity+=0.02f;
		}
		angle = Math.PI/2f - p.angle;
		double a = Game.resX/3, b = Game.resY/3;
		int centerX = (int) (Game.resX/2 + a * Math.cos(angle));
		int centerY = (int) (Game.resY/2 - b * Math.sin(angle));
		int sizeX = (int) (Game.resX/4.5f);
		int sizeY = (int) (Game.resY/4.5f);
		Image image = Images.get(p.wonderName.name()+"_background").getScaledCopy(sizeX, sizeY);
		image.setAlpha(p.opacity);
		g.drawImage(image, centerX-sizeX/2, centerY-sizeY/2);
		boolean isReady = !Communications.checkIfDone().contains(p);
		g.setColor(isReady ? new Color(200f/255f,160f/255f,80f/255f, p.opacity) : new Color(0.1f,0.1f,0.1f,p.opacity));
		g.drawRect(centerX-sizeX/2, centerY-sizeY/2, sizeX, sizeY);
		g.setColor(new Color(0f,0f,0f,p.opacity));
		g.drawRect(centerX-sizeX/2+1, centerY-sizeY/2+1, sizeX-2, sizeY-2);
		int size = Fonts.font_big.getHeight("Hj")+10;
		image = Images.get(isReady ? "check" : "tocheck").getScaledCopy(size, size);
		image.setAlpha(p.opacity);
		g.drawImage(image, centerX - sizeX/2 + 5, centerY - sizeY/2 +5);
		Fonts.font_big.drawString(centerX-sizeX/2+10 + size, centerY-sizeY/2+10, p.nickName);
		size = Fonts.font_main.getHeight("Hj")+10;
		String s = "GAMES : ", s1 = ""+p.nbGame;
		Fonts.font_main.drawString(centerX+sizeX/2-Fonts.font_number.getWidth(s1)-Fonts.font_main.getWidth(s)-15f, centerY+sizeY/2 - 1.5f*size - Fonts.font_main.getHeight(s), s);
		Fonts.font_number.drawString(centerX+sizeX/2-Fonts.font_number.getWidth(s1)-15f, centerY+sizeY/2 - 1.5f*size - Fonts.font_number.getHeight(s1), s1);
		s = "BEST SCORE : ";
		s1 = "" + p.bestScore;
		Fonts.font_main.drawString(centerX+sizeX/2-Fonts.font_number.getWidth(s1)-Fonts.font_main.getWidth(s)-15f, centerY+sizeY/2 - 0.5f*size - Fonts.font_main.getHeight(s), s);
		Fonts.font_number.drawString(centerX+sizeX/2-Fonts.font_number.getWidth(s1)-15f, centerY+sizeY/2 - 0.5f*size - Fonts.font_number.getHeight(s1), s1);
	}
}
