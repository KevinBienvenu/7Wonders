package Effects;

import java.util.HashMap;
import java.util.Vector;

import gameSystem.Building;
import gameSystem.Card;
import inputActions.State;
import inputActions.StateBuryLeaderChoice;
import inputActions.StateCardChoice;
import inputActions.StateLeaderChoice;
import inputActions.StatePlayFromDiscard;
import main.Game;
import ressources.CategoryName;
import ressources.TokenName;

public class EffectAction {
	
	/// UTILITY FONCTIONS
	public static int getNumberOfCardFromCategoryForPlayer(int idJoueur, CategoryName cat){
		int temp_nb = 0;
		for(Building b : Game.gameSystem.board.players.get(idJoueur).buildings){
			if(b.categoryName==cat){
				temp_nb+=1;
			}
		}
		return temp_nb;
	}

	public static void action(EffectType type, int idJoueur){
		int temp_nb = 0;
		int temp_travail = 0;
		HashMap<Integer, State> hashmap = new HashMap<Integer, State>();
		switch(type){
		case Coins3:
			Game.gameSystem.board.players.get(idJoueur).coins+=3;
			break;
		case Coins4:
			Game.gameSystem.board.players.get(idJoueur).coins+=4;
			break;
		case Coins5:
			Game.gameSystem.board.players.get(idJoueur).coins+=5;
			break;
		case Coins6:
			Game.gameSystem.board.players.get(idJoueur).coins+=6;
			break;
		case Coins9:
			Game.gameSystem.board.players.get(idJoueur).coins+=9;
			break;
		case CoinsForAllCommonProd:
			temp_travail = Game.gameSystem.nbPlayer;
			for(Integer i : new Integer[]{idJoueur, (idJoueur+1)%temp_travail, (idJoueur-1+temp_travail)%temp_travail}){
				temp_nb+=getNumberOfCardFromCategoryForPlayer(i, CategoryName.CommonRessources);
			}
			Game.gameSystem.board.players.get(idJoueur).coins+=temp_nb;
			break;
		case CoinsForAllRareProd:
			temp_travail = Game.gameSystem.nbPlayer;
			for(Integer i : new Integer[]{idJoueur, (idJoueur+1)%temp_travail, (idJoueur-1+temp_travail)%temp_travail}){
				temp_nb+=2*getNumberOfCardFromCategoryForPlayer(i, CategoryName.RareRessources);
			}
			Game.gameSystem.board.players.get(idJoueur).coins+=temp_nb;
			break;
		case CoinsAndVictoryPointsForCommercial:
			Game.gameSystem.board.players.get(idJoueur).coins+=1*getNumberOfCardFromCategoryForPlayer(idJoueur, CategoryName.Commercial);
			break;
		case CoinsAndVictoryPointsForCommonProd:
			Game.gameSystem.board.players.get(idJoueur).coins+=1*getNumberOfCardFromCategoryForPlayer(idJoueur, CategoryName.CommonRessources);
			break;
		case CoinsAndVictoryPointsForRareProd:
			Game.gameSystem.board.players.get(idJoueur).coins+=2*getNumberOfCardFromCategoryForPlayer(idJoueur, CategoryName.RareRessources);
			break;
		case CoinsAndVictoryPointsForWonderFloors:
			Game.gameSystem.board.players.get(idJoueur).coins+=Game.gameSystem.board.players.get(idJoueur).wonderFloorBuilt.size()*3;
			break;
		case Compas:
			Game.gameSystem.board.players.get(idJoueur).tokens.put(TokenName.Compas,
					Game.gameSystem.board.players.get(idJoueur).tokens.get(TokenName.Compas)+1);
			break;
		case Engrenage:
			Game.gameSystem.board.players.get(idJoueur).tokens.put(TokenName.Engrenage,
					Game.gameSystem.board.players.get(idJoueur).tokens.get(TokenName.Engrenage)+1);
			break;
		case Tablette:
			Game.gameSystem.board.players.get(idJoueur).tokens.put(TokenName.Tablette,
					Game.gameSystem.board.players.get(idJoueur).tokens.get(TokenName.Tablette)+1);
			break;
		case Military1:
			Game.gameSystem.board.players.get(idJoueur).tokens.put(TokenName.Military,
					Game.gameSystem.board.players.get(idJoueur).tokens.get(TokenName.Military)+1);
			break;
		case Military2:
			Game.gameSystem.board.players.get(idJoueur).tokens.put(TokenName.Military,
					Game.gameSystem.board.players.get(idJoueur).tokens.get(TokenName.Military)+2);
			break;
		case Military3:
			Game.gameSystem.board.players.get(idJoueur).tokens.put(TokenName.Military,
					Game.gameSystem.board.players.get(idJoueur).tokens.get(TokenName.Military)+3);
			break;
		case Military5:
			Game.gameSystem.board.players.get(idJoueur).tokens.put(TokenName.Military,
					Game.gameSystem.board.players.get(idJoueur).tokens.get(TokenName.Military)+5);
			break;
		case Military7:
			Game.gameSystem.board.players.get(idJoueur).tokens.put(TokenName.Military,
					Game.gameSystem.board.players.get(idJoueur).tokens.get(TokenName.Military)+7);
			break;
		case SpecialBuildFreeBuilding:
			Game.gameSystem.board.players.get(idJoueur).specialEffects.add(EffectType.SpecialBuildFreeBuilding);
			Game.gameSystem.board.players.get(idJoueur).hasFreeBuildingLeft = true;
			break;
		case SpecialBuildFromDiscard:
			if(Game.gameSystem.discardedCards.size()>0){
				hashmap.put(idJoueur, new StatePlayFromDiscard(idJoueur));
				Game.gameSystem.actionToPlay.add(hashmap);
			}
			break;
		case SpecialBuryLeader:
			boolean todo = false;
			for(Building b : Game.gameSystem.board.players.get(idJoueur).buildings){
				if(b.categoryName == CategoryName.Leader){
					todo = true;
					break;
				}
			}
			if(todo){
				hashmap.put(idJoueur, new StateBuryLeaderChoice(idJoueur));
				Game.gameSystem.actionToPlay.add(hashmap);
			}
			break;
		case SpecialPlayLeader:
			hashmap.put(idJoueur, new StateCardChoice(idJoueur, true));
			Game.gameSystem.actionToPlay.add(hashmap);
			break;
		case SpecialPlayLastCard:
			Game.gameSystem.board.players.get(idJoueur).specialEffects.add(EffectType.SpecialPlayLastCard);
			break;
		case SpecialDraw4Leaders:
			for(int i = 0; i<4; i++){
				if(Game.gameSystem.remainingLeaders.size()>0){
					Game.gameSystem.board.players.get(idJoueur).leaderToChoose.add(Game.gameSystem.remainingLeaders.get(0));
					Game.gameSystem.remainingLeaders.remove(0);
				} else {
					System.out.println("No leader remaining => bug !!!");
				}
			}
			break;
		case Coins2Chainage:
		case Coins2MilitaryVictory:
		case Coins1Commerce:
		case Coins2Commercial:
		case SendBackDefeat:
		case SpecialLeaderFree:
		case CheaperScientific:			
		case CheaperMilitary:
		case CheaperCivilian:
		case SpecialBilkis:
		case CheaperWonder:
		case ProductionScience:
		case SpecialGuildsFree:
			Game.gameSystem.board.players.get(idJoueur).specialEffects.add(type);
			break;
		case SpecialLeaderCheaper:
			Game.gameSystem.board.players.get(idJoueur).specialEffects.add(EffectType.SpecialLeaderCheaper2);
			temp_travail = Game.gameSystem.nbPlayer;
			Game.gameSystem.board.players.get((idJoueur+1)%temp_travail).specialEffects.add(EffectType.SpecialLeaderCheaper1);
			Game.gameSystem.board.players.get((idJoueur-1+temp_travail)%temp_travail).specialEffects.add(EffectType.SpecialLeaderCheaper1);
			break;
		case None:
			break;
		default:
			break;
		}
	}
	
	public static void undoaction(EffectType type, int idJoueur){
		switch(type){
		case Compas:
			Game.gameSystem.board.players.get(idJoueur).tokens.put(TokenName.Compas,
					Game.gameSystem.board.players.get(idJoueur).tokens.get(TokenName.Compas)-1);
			break;
		case Engrenage:
			Game.gameSystem.board.players.get(idJoueur).tokens.put(TokenName.Engrenage,
					Game.gameSystem.board.players.get(idJoueur).tokens.get(TokenName.Engrenage)-1);
			break;
		case Tablette:
			Game.gameSystem.board.players.get(idJoueur).tokens.put(TokenName.Tablette,
					Game.gameSystem.board.players.get(idJoueur).tokens.get(TokenName.Tablette)-1);
			break;
		case Military1:
			Game.gameSystem.board.players.get(idJoueur).tokens.put(TokenName.Military,
					Game.gameSystem.board.players.get(idJoueur).tokens.get(TokenName.Military)-1);
			break;
		case Military2:
			Game.gameSystem.board.players.get(idJoueur).tokens.put(TokenName.Military,
					Game.gameSystem.board.players.get(idJoueur).tokens.get(TokenName.Military)-2);
			break;
		case Military3:
			Game.gameSystem.board.players.get(idJoueur).tokens.put(TokenName.Military,
					Game.gameSystem.board.players.get(idJoueur).tokens.get(TokenName.Military)-3);
			break;
		case Military5:
			Game.gameSystem.board.players.get(idJoueur).tokens.put(TokenName.Military,
					Game.gameSystem.board.players.get(idJoueur).tokens.get(TokenName.Military)-5);
			break;
		case Military7:
			Game.gameSystem.board.players.get(idJoueur).tokens.put(TokenName.Military,
					Game.gameSystem.board.players.get(idJoueur).tokens.get(TokenName.Military)-7);
			break;
		case SpecialBuildFreeBuilding:
			Game.gameSystem.board.players.get(idJoueur).specialEffects.remove(EffectType.SpecialBuildFreeBuilding);
			Game.gameSystem.board.players.get(idJoueur).hasFreeBuildingLeft = false;
			break;
		case SpecialPlayLastCard:
			Game.gameSystem.board.players.get(idJoueur).specialEffects.remove(EffectType.SpecialPlayLastCard);
			break;
		case Coins2Chainage:
		case Coins2MilitaryVictory:
		case Coins1Commerce:
		case Coins2Commercial:
		case SendBackDefeat:
		case SpecialLeaderFree:
		case CheaperScientific:			
		case CheaperMilitary:
		case SpecialBilkis:
		case CheaperCivilian:
		case CheaperWonder:
		case ProductionScience:
		case SpecialGuildsFree:
			Game.gameSystem.board.players.get(idJoueur).specialEffects.remove(type);
			break;
		case None:
			break;
		default:
			break;
		}
	}
	

}
