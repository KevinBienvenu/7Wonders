package model;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import enums.AgeNames;
import enums.WonderName;
import main.Game;
import network.Communications;
import render.PlayerRender;
import render.WarRender;
import ressources.Data;
import ressources.Fonts;
import ressources.Images;


public class Board {

	public static int idplayer = 0;

	public Vector<Player> players;

	public HashSet<WonderName> wondersSelected;

	public boolean hasSentScore;
	
	public Board(){
		this.players = new Vector<Player>();
	}

	public Board(int nbPayers){
		idplayer = 0;
		this.players = new Vector<Player>();
		for(int lp=0; lp<nbPayers; lp++){
			this.players.add(new Player("player_"+lp, (WonderName) Data.wonders.keySet().toArray()[lp]));
			this.players.lastElement().wonderFace = "A";
		}
	}

	public void update(){

	}

	public void render(Graphics g){
		g.drawImage(Images.get("wonder0"),0,0);
		g.setColor(Color.white);
		g.drawRect(Game.resX*2/5+1, 1, Game.resX/5-2, Game.resY-2);
		g.setColor(Color.yellow);
		g.drawRect(Game.resX*2/5+3, 3, Game.resX/5-6, Game.resY-6);
		g.setColor(Color.white);
		g.drawRect(Game.resX*2/5+5, 5, Game.resX/5-10, Game.resY-10);
		Image im = Images.get("fleche_circulaire");
		int sens = 1;
		if(Game.gameSystem.currentAge==AgeNames.AGEII || Game.gameSystem.currentAge==AgeNames.LEADERCHOICE){
			sens = -1;
		}
		im.setRotation(im.getRotation()+0.1f*sens);
		g.drawImage(im, Game.resX/2-im.getWidth()/2, Game.resY/2-im.getHeight()/2);
		String s = Game.gameSystem.currentAge.text;
		Fonts.font_mid.drawString(Game.resX/2-Fonts.font_mid.getWidth(s)/2,20, s);
		String bconditionternaire = Game.gameSystem.nbRoundRestant > 2 ? "S" : "";
		s = Game.gameSystem.nbRoundRestant+ " REMAINING TURN"+bconditionternaire;
		Fonts.font_main.drawString(Game.resX*2/5+15, Game.resY*1.3f/10, s);
		bconditionternaire = Game.gameSystem.discardedCards.size() > 1 ? "S" : "";
		s = Game.gameSystem.discardedCards.size() + " CARD"+bconditionternaire+" DISCARDED";
		Fonts.font_main.drawString(Game.resX*2/5+15, Game.resY*1.7f/10, s);
		Vector<Integer> v_temp = new Vector<Integer>();
		for(int i=0; i<4; i++){
			if(i<Game.gameSystem.board.players.size()){
				v_temp.addElement(i);
			}
		}
		for(int i=8; i>3; i--){
			if(i<Game.gameSystem.board.players.size()){
				v_temp.addElement(i);
			}
		}
		for(Integer i : v_temp){
			Player p = this.players.get(i);
			if(WarRender.rendering){
				if(p.id == WarRender.idPlayer || p.id == WarRender.idOther){
					PlayerRender.render(g, p);
				} else {
					PlayerRender.renderNull(g, p.id);
				}
			} else {
				PlayerRender.render(g, p);
			}
		}
		for(int i=this.players.size(); i<9;i++){
			PlayerRender.renderNull(g,i);
		}
		List<String> v = Communications.checkIfDone().stream().map(player -> player.nickName).collect(Collectors.toList());
		String spluriel = "";
		if(v.size()>1){
			spluriel = "S";
		}
		s = "WAITING FOR "+v.size() + " PLAYER"+spluriel;
		Fonts.font_main.drawString(Game.resX*2/5+15, Game.resY*7.5f/10, s);
		int i=0;
		for(String s1 : v){
			i+=1;
			Fonts.font_main.drawString(Game.resX*2/5+35, Game.resY*(7.5f+i/4f)/10, s1);
		}
	}




}
