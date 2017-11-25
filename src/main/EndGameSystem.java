package main;

import java.util.Vector;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import Effects.PointsHandling;
import gameSystem.Player;
import ia.GameLog;
import inputActions.Communications;
import ressources.CategoryName;
import ressources.Data;
import ressources.Images;
public class EndGameSystem  extends ClassSystem{

	public Vector<Player> players;

	public int idJoueur;
	public int idPoints;
	public int nbCat, nbCol, nbLine;
	public Vector<CategoryName> cat;
	public int time = 0;
	public int timeToWaitCat = 10;
	public int timeToWaitJoueur = 40;

	public EndGameSystem(Vector<Player> players){
		this.players = players;
		init();
	}
	
	public EndGameSystem(int nbJoueur){
		this.players = PointsHandling.computeRandomPoints(nbJoueur);
		init();
	}

	public void init(){
		// sorting players according to their scores
		int maxScore, indPlayer, tempScore;
		Player p;
		Vector<Player> playersTemp = this.players;
		this.players = new Vector<Player>();
		while(playersTemp.size()>0){
			maxScore = 0;
			indPlayer = 0;
			for(int i=0; i<playersTemp.size(); i++){
				p = playersTemp.get(i);
				tempScore = p.getScore();
				if(tempScore>maxScore){
					maxScore = tempScore;
					indPlayer = i;
				}
			}
			this.players.add(playersTemp.remove(indPlayer));
		}
		cat = new Vector<CategoryName>();
		for(CategoryName cn : players.get(0).pointsToDisplay.keySet()){
			cat.add(cn);
		}
		nbCat = cat.size();
		nbCol = nbCat+2+2+1;
		nbLine = 1+ players.size();
		idJoueur = this.players.size()-1;
	}


	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		g.drawImage(Images.get("title-background"), Game.resX/2-Images.get("title-background").getWidth()/2, 0);
		String s;
		for(int i=0; i<nbCat+1; i++){
			s = "JOUEUR";
			Data.font_big.drawString(Game.resX*2f/nbCol-Data.font_big.getWidth(s)/2, Game.resY*3f/10f+3f, s);
			if(i<nbCat){
				g.setColor(cat.get(i).color);
				g.fillRect(Game.resX*(3f+i)/nbCol, Game.resY*3f/10f, Game.resX*1/nbCol, Game.resY*1f/15f);
				s = cat.get(i).name().substring(0,Math.min(cat.get(i).name().length(), 6));
				Data.font_mid.drawString(Game.resX*(3f+i+0.5f)/nbCol-Data.font_mid.getWidth(s)/2f, Game.resY*3f/10f+6f, s);
			}
			s = "TOTAL";
			Data.font_big.drawString(Game.resX*(nbCol-1.5f)/nbCol-Data.font_big.getWidth(s)/2, Game.resY*3f/10f+3f, s);
			g.setColor(Color.white);
			g.drawLine(Game.resX*(3f+i)/nbCol, Game.resY*3f/10f, Game.resX*(3f+i)/nbCol, Game.resY*9f/10f);
		}
		g.setColor(Color.white);
		g.drawRect(Game.resX*1f/nbCol, Game.resY*3f/10f, Game.resX*(nbCol-2)/nbCol, Game.resY*6f/10f);
		g.drawRect(Game.resX*1f/nbCol, Game.resY*3f/10f, Game.resX*(nbCol-2)/nbCol, Game.resY*1f/15f);
		for(int j=0; j<players.size(); j++){
			if(j < idJoueur){
				continue;
			}
			for(int i=0; i<nbCat; i++){
				if(i>idPoints && idJoueur == j){
					break;
				}
				s = ""+players.get(j).pointsToDisplay.get(cat.get(i));
				Data.font_mid.drawString(Game.resX*(3f+i+0.5f)/nbCol-Data.font_mid.getWidth(s)/2f,
						Game.resY*5.5f/15f+3f+Game.resY/20f*j+3f, s);
			}
			if(idPoints>=nbCat || j > idJoueur){
				s = players.get(j).nickName;
				Data.font_big.drawString(Game.resX*2f/nbCol-Data.font_big.getWidth(s)/2, 
						Game.resY*5.5f/15f+3f+Game.resY/20f*j+3f, s);
				s = "" +players.get(j).getScore();
				Data.font_big.drawString(Game.resX*(nbCol-1.5f)/nbCol-Data.font_big.getWidth(s)/2,
						Game.resY*5.5f/15f+3f+Game.resY/20f*j+3f, s);
			}
		}
	}

	@Override
	public void update(GameContainer arg0, int arg1) throws SlickException {
		if(time<timeToWaitCat){
			time++;
		} else if(idJoueur>=0){
			if(idPoints>=nbCat){
				if(time<timeToWaitJoueur){
					time++;
				} else {
					idPoints = 0;
					idJoueur -= 1;
				}
			} else {
				time = 0;
				idPoints+=1;
			}
		} else {
		//	GameLog.computeFinalFiles();
			if(!Game.gameSystem.board.hasSentScore){
				Game.gameSystem.board.hasSentScore = true;
				Communications.sendScores();
			}
			if(Main.replay){
				Game.lobbySystem = new LobbySystem();
				Communications.init();
				Game.system = Game.lobbySystem;
			}
		}
	}
}
