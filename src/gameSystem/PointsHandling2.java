package gameSystem;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

import main.Game;
import render.PlayerRender;
import ressources.CategoryName;
import ressources.Data;
import ressources.Images;
import ressources.TokenName;

public class PointsHandling2 {

	public static int idJoueur;

	public static boolean update(GameContainer gc){
		Input in = gc.getInput();
		if(in.isKeyPressed(Input.KEY_RETURN)){
			idJoueur += 1;
			if(idJoueur==Game.gameSystem.board.players.size()){
				return true;
			}
		}
		return false;
	}

	public static void render(Graphics g){
		g.setColor(Color.black);
		g.fillRect(0, 0, Game.resX, Game.resY);
		int i=0;
		int x,y;
		int sizeX = Images.get("marche").getWidth();
		int sizeY = Images.get("marche").getHeight();
		for(CategoryName cn : CategoryName.values()){
			for(Building b : Game.gameSystem.board.players.get(idJoueur).buildings){
				if(b.categoryName==cn){
					x = (int) ((i%8)*(sizeX*1.05f));
					y = (int) ((i/8)*(sizeY*1.05f));
					g.drawImage(Images.get(b.idname),x,y);
					i+=1;
				}
			}
		}
		for(Integer j : Game.gameSystem.board.players.get(idJoueur).wonderFloorBuilt){
			x = (int) ((8)*(sizeX*1.05f));
			y = (int) ((j)*(100*1.05f));
			g.drawImage(Images.get(""+Game.gameSystem.board.players.get(idJoueur).wonderName.name()+
					Game.gameSystem.board.players.get(idJoueur).wonderFace+(j+1)),x,y);
		}
		// Drawing Coins
		g.drawImage(Images.get("piececornerbasgauche"),10,Game.resY-80);
		Data.font_main.drawString(60, Game.resY-60-Data.font_main.getHeight(""+Game.gameSystem.board.players.get(idJoueur).coins)/2, ""+Game.gameSystem.board.players.get(idJoueur).coins);
		// Drawing Token
		int idPos = 0;
		float x1, y1;
		x = 100;
		y = Game.resY-30;
		int sizeImage;
		for(TokenName token : TokenName.values()){
			if(Game.gameSystem.board.players.get(idJoueur).tokens.get(token)>=0){
				x1 = x+idPos*sizeX/4f;
				y1 = Game.resY-50;
				sizeImage = Images.get(token.name().toLowerCase()+"panneaubas").getWidth();
				g.drawImage(Images.get(token.name().toLowerCase()+"panneaubas"), x1+2, y1-sizeImage/2);
				Data.font_main.drawString(x1+sizeImage+4, y1-Data.font_main.getHeight(""+Game.gameSystem.board.players.get(idJoueur).tokens.get(token))/2f, ""+Game.gameSystem.board.players.get(idJoueur).tokens.get(token));
				idPos+=1;
			}
		}
	}
}
