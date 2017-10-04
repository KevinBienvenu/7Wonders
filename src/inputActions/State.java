package inputActions;

import java.util.HashMap;
import java.util.Vector;

import Effects.EffectType;
import gameSystem.Building;
import gameSystem.Player;
import main.Game;
import ressources.CategoryName;
import ressources.TokenName;

public abstract class State {
	
	public String id;
	public int idJoueur;
	public Player player;
	public int key = -1;
	
	public State(String id, int idJoueur) {
		this.id = id;
		this.idJoueur = idJoueur;
		this.player = Game.gameSystem.board.players.get(idJoueur);
	}
	
	public void setKey(int key){
		this.key = key;
	}
	
	public abstract String toString();
	
	
	public static String handleEffectTypes(Vector<EffectType> vector){
		String s = "";
		for(EffectType et : vector){
			s+="\""+et.name()+"\",";
		}
		if(s.length()>0){
			s = s.substring(0, s.length()-1);
		}
		return s;
	}
	
	public static HashMap<String, Integer> getDicRessources(Vector<EffectType> ressources, boolean complete){
		HashMap<String, Integer> dic = new HashMap<String, Integer>();
		String toAdd;
		int nbToAdd = 1;
		int iChar;
		for(EffectType r : ressources){
			nbToAdd = 1;
			toAdd = r.name();
			iChar = 1;
			switch(r){
			case ProductionRare:
				if(!complete)
					continue;
				toAdd = "tissu-verre-papyrus";
				break;
			case ProductionCommon:
				if(!complete)
					continue;
				toAdd = "minerai-argile-bois-pierre";
				break;
			case ProductionArgileDouble:
			case ProductionBoisDouble:
			case ProductionMineraiDouble:
			case ProductionPierreDouble:
				nbToAdd += 1;
			case ProductionArgileSimple:
			case ProductionBoisSimple:
			case ProductionMineraiSimple:
			case ProductionPierreSimple:
				toAdd = toAdd.substring(0, toAdd.length()-6);
			case ProductionPapyrus:
			case ProductionTissu:
			case ProductionVerre:
				toAdd = toAdd.substring(10).toLowerCase();
				break;
			case ProductionArgileMinerai:
			case ProductionBoisArgile:
			case ProductionBoisMinerai:
			case ProductionMineraiPierre:
			case ProductionPierreArgile:
			case ProductionPierreBois:
				toAdd = toAdd.substring(10);
				while((int)toAdd.charAt(iChar)>96){
					iChar+=1;
				}
				toAdd = (toAdd.substring(0,iChar)+"-"+toAdd.substring(iChar)).toLowerCase();
				break;
			default:
				if(!complete){
					continue;
				}
				break;
			}
			if(!dic.containsKey(toAdd)){
				dic.put(toAdd, 0);			
			}
			dic.put(toAdd, dic.get(toAdd)+nbToAdd);
		}
		return dic;
	}
	
	public static String handleCardNumber(Player p){
		String s = "{";
		int temp_nb;
		for(CategoryName cn : CategoryName.values()){
			temp_nb = 0;
			for(Building b : p.buildings){
				if(b.categoryName==cn){
					temp_nb += 1;
				}
			}
			if(temp_nb>0){
				s+="\""+cn.name()+"\":"+temp_nb+",";
			}
		}
		if(s.length()>1){
			s = s.substring(0, s.length()-1);
		}
		return s+"}";
	}
	
	public static String handleTokenNumber(Player p){
		String s = "{";
		for(TokenName token : TokenName.values()){
			if(p.tokens.get(token)>=0){
				s+="\""+token.name()+"\":"+p.tokens.get(token)+",";
			}
		}
		if(s.length()>1){
			s = s.substring(0, s.length()-1);
		}
		return s+"}";
	}
	
	public static String handleAdditionalInfos(Player p){
		String s = "";
		s += "\"cardNumber\":"+handleCardNumber(p)+",";
		s += "\"cardNumberLeft\":"+handleCardNumber(Game.gameSystem.getLeftPlayer(p.id))+",";
		s += "\"cardNumberRight\":"+handleCardNumber(Game.gameSystem.getRightPlayer(p.id))+",";
		s += "\"tokenNumber\":"+handleTokenNumber(p)+",";
		s += "\"tokenNumberLeft\":"+handleTokenNumber(Game.gameSystem.getLeftPlayer(p.id))+",";
		s += "\"tokenNumberRight\":"+handleTokenNumber(Game.gameSystem.getRightPlayer(p.id))+",";
		return s;
	}

	public static String handleRessources(Vector<EffectType> ressources, boolean complete){
		String s = "";
		HashMap<String, Integer> dic = getDicRessources(ressources, complete);
		for(String so : dic.keySet()){
			s += "\""+so+"\":"+dic.get(so)+",";
		}
		if(s.length()>0){
			s = s.substring(0, s.length()-1);
		}
		return s;
	}
	
}
