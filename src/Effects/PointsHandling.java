package effects;


import java.util.HashMap;
import java.util.Vector;

import enums.CategoryName;
import enums.TokenName;
import enums.WonderName;
import main.Game;
import model.Building;
import model.Player;
import ressources.Data;

public class PointsHandling {

	public static void computePoints(Player p){
		// init
		p.pointsToDisplay = new HashMap<CategoryName, Integer>();
		for(CategoryName cn : CategoryName.values()){
			if(cn.score){
				p.pointsToDisplay.put(cn, 0);
			}
		}
		// buildings
		int temp;
		for(Building b: p.buildings){
			for(EffectType et : b.effects){
				temp = EffectPoints.points(et, p);
				if(temp>0){
					p.pointsToDisplay.put(b.categoryName, p.pointsToDisplay.get(b.categoryName)+temp);
				}
			}
		}
		// wonder floors
		for(Integer i : p.wonderFloorBuilt){
			for(EffectType et : Data.wonders.get(p.wonderName).floors.get(p.wonderFace).get(i).effects){
				p.pointsToDisplay.put(CategoryName.Wonder, p.pointsToDisplay.get(CategoryName.Wonder)+EffectPoints.points(et, p));
			}
		}
		// special case : buried cards
		for(Building b : p.buriedCards){
			p.pointsToDisplay.put(CategoryName.Wonder, p.pointsToDisplay.get(CategoryName.Wonder)+b.coinCost*2);
		}
		// coins
		p.pointsToDisplay.put(CategoryName.Coins, (int)(p.coins/3f));
		// tokens
		int nbpoints = 0;
		for(TokenName tn : p.tokens.keySet()){
			nbpoints = 0;
			switch(tn){
			case DefeatToken1:
				nbpoints -= p.tokens.get(tn);
				p.pointsToDisplay.put(CategoryName.Military, p.pointsToDisplay.get(CategoryName.Military)+nbpoints);
				break;
			case VictoryToken1:
			case VictoryToken3:
			case VictoryToken5:
				nbpoints += p.tokens.get(tn)*Integer.parseInt(tn.name().substring(12));
				p.pointsToDisplay.put(CategoryName.Military, p.pointsToDisplay.get(CategoryName.Military)+nbpoints);
				break;
			default:
				break;
			}
		}
		// science
		int nbSpecial = 0;
		for(EffectType et : p.specialEffects){
			if(et == EffectType.ProductionScience){
				nbSpecial++;
			}
		}
		p.pointsToDisplay.put(CategoryName.Scientific, pointsScience(p.tokens, nbSpecial, p.specialEffects.contains(EffectType.PointsAristote)));
	}

	public static int pointsScience(HashMap<TokenName, Integer> tokens, int nbSpecial, boolean aristote){
		int n = 0;
		int minScience =-1;
		int nbpoints = 0;
		if(nbSpecial==0){
			for(TokenName tn : tokens.keySet()){
				switch(tn){
				case Compas:
				case Engrenage:
				case Tablette:
					nbpoints += tokens.get(tn)*tokens.get(tn);
					if(minScience == -1){
						minScience = tokens.get(tn);
					} else {
						minScience = Math.min(minScience, tokens.get(tn));
					}
					break;
				default:
					break;
				}
			}
			if(minScience>0){
				nbpoints+=minScience*7;
				if(aristote){
					nbpoints+=minScience*3;				
				}
			}
			n = nbpoints;
		} else if(nbSpecial==1){
			for(TokenName tn2 : new TokenName[] {TokenName.Compas,TokenName.Engrenage,TokenName.Tablette}){
				tokens.put(tn2, tokens.get(tn2)+1);
				nbpoints = 0;
				minScience = -1;
				for(TokenName tn : tokens.keySet()){
					switch(tn){
					case Compas:
					case Engrenage:
					case Tablette:
						nbpoints += tokens.get(tn)*tokens.get(tn);
						if(minScience == -1){
							minScience = tokens.get(tn);
						} else {
							minScience = Math.min(minScience, tokens.get(tn));
						}
						break;
					default:
						break;
					}
				}
				if(minScience>0){
					nbpoints+=minScience*7;
					if(aristote){
						nbpoints+=minScience*3;				
					}
				}
				n = Math.max(n,nbpoints);				
				tokens.put(tn2, tokens.get(tn2)-1);
			}
		} else if(nbSpecial==2){
			for(TokenName tn2 : new TokenName[] {TokenName.Compas,TokenName.Engrenage,TokenName.Tablette}){
				tokens.put(tn2, tokens.get(tn2)+1);
				for(TokenName tn3 : new TokenName[] {TokenName.Compas,TokenName.Engrenage,TokenName.Tablette}){
					tokens.put(tn3, tokens.get(tn3)+1);
					nbpoints = 0;
					minScience = -1;
					for(TokenName tn : tokens.keySet()){
						switch(tn){
						case Compas:
						case Engrenage:
						case Tablette:
							nbpoints += tokens.get(tn)*tokens.get(tn);
							if(minScience == -1){
								minScience = tokens.get(tn);
							} else {
								minScience = Math.min(minScience, tokens.get(tn));
							}
							break;
						default:
							break;
						}
					}
					if(minScience>0){
						nbpoints+=minScience*7;
						if(aristote){
							nbpoints+=minScience*3;				
						}
					}
					n = Math.max(n,nbpoints);				
					tokens.put(tn3, tokens.get(tn3)-1);
				}
				tokens.put(tn2, tokens.get(tn2)-1);
			}
		}
		return n;
	}

	public static void computePoints(){
		for(Player p : Game.gameSystem.board.players){
			computePoints(p);
		}
	}
	
	public static void computeRandomPoints(Player p){
		// init
		p.pointsToDisplay = new HashMap<CategoryName, Integer>();
		for(CategoryName cn : CategoryName.values()){
			if(cn.score){
				p.pointsToDisplay.put(cn, (int)(Math.random()*25));
			}
		}
	}
	
	
}
