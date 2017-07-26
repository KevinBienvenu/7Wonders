package inputActions;

import java.util.HashMap;
import java.util.Vector;

import Effects.EffectType;
import gameSystem.Player;
import main.Game;

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
