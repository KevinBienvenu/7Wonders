package systems;

import java.util.Vector;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import com.google.gson.JsonObject;

import enums.CategoryName;
import main.Game;
import main.Main;
import model.Player;
import network.Communications;
import network.Routes;
import ressources.Fonts;
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
			this.players.lastElement().position = this.players.size();
		}
		Game.gameSystem.board.players = this.players;
		for(Player p2 : this.players){
			p2.nbJoueur = this.players.size();
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
		g.drawImage(Images.get("title-background").getScaledCopy(0.5f), Game.resX/2-Images.get("title-background").getWidth()/4, 0);
		String s;
		for(int i=0; i<nbCat+1; i++){
			s = "JOUEUR";
			Fonts.font_big.drawString(Game.resX*2f/nbCol-Fonts.font_big.getWidth(s)/2, Game.resY*3f/10f+3f, s);
			if(i<nbCat){
				g.setColor(cat.get(i).color);
				g.fillRect(Game.resX*(3f+i)/nbCol, Game.resY*3f/10f, Game.resX*1/nbCol, Game.resY*1f/15f);
				s = cat.get(i).name().substring(0,Math.min(cat.get(i).name().length(), 6));
				Fonts.font_mid.drawString(Game.resX*(3f+i+0.5f)/nbCol-Fonts.font_mid.getWidth(s)/2f, Game.resY*3f/10f+6f, s);
			}
			s = "TOTAL";
			Fonts.font_big.drawString(Game.resX*(nbCol-1.5f)/nbCol-Fonts.font_big.getWidth(s)/2, Game.resY*3f/10f+3f, s);
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
				Fonts.font_mid.drawString(Game.resX*(3f+i+0.5f)/nbCol-Fonts.font_mid.getWidth(s)/2f,
						Game.resY*5.5f/15f+3f+Game.resY/20f*j+3f, s);
			}
			if(idPoints>=nbCat || j > idJoueur){
				s = players.get(j).nickName;
				Fonts.font_big.drawString(Game.resX*2f/nbCol-Fonts.font_big.getWidth(s)/2, 
						Game.resY*5.5f/15f+3f+Game.resY/20f*j+3f, s);
				s = "" +players.get(j).getScore();
				Fonts.font_big.drawString(Game.resX*(nbCol-1.5f)/nbCol-Fonts.font_big.getWidth(s)/2,
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
					if(Main.nbIAPlayer==0){
						sendScores(this.players.get(idJoueur+1));
					}
				}
			} else {
				time = 0;
				idPoints+=1;
			}
		} else {
			if(!Game.gameSystem.board.hasSentScore){
				Game.gameSystem.board.hasSentScore = true;
			}
			if(Main.replay){
				Game.lobbySystem = new LobbySystem();
				Communications.init();
				Game.system = Game.lobbySystem;
			}
		}
	}
	
	public void sendScores(Player p){
		JsonObject data = new JsonObject();
//		data.addProperty("idJoueur", p.id);
		data.addProperty("username", p.nickName);
//		data.addProperty("state", "scores");
//		p.pointsToDisplay.keySet().stream().forEach(category -> {
//			data.addProperty(category.name(), p.pointsToDisplay.get(category));
//		});
		data.addProperty("position", p.position);
		data.addProperty("nbPlayers", p.nbJoueur);
		data.addProperty("gameId", Communications.gameId);
		data.addProperty("score", p.pointsToDisplay.values().stream().mapToInt(i -> i.intValue()).sum());
		try {
			Communications.sendPost(Routes.SCORES, data);
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}
