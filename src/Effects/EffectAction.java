package effects;

import java.util.HashMap;

import enums.CategoryName;
import enums.TokenName;
import main.Game;
import model.Building;
import model.Card;
import model.Player;
import states.State;
import states.StateBuryLeaderChoice;
import states.StateCardChoice;
import states.StatePlayFromDiscard;

public class EffectAction {
	
	/// UTILITY FONCTIONS
	

	public static void action(EffectType type, Player player){
		int temp_nb = 0;
		int temp_travail = 0;
		HashMap<Integer, State> hashmap = new HashMap<Integer, State>();
		switch(type){
		case Coins3:
			player.coins+=3;
			break;
		case Coins4:
			player.coins+=4;
			break;
		case Coins5:
			player.coins+=5;
			break;
		case Coins6:
			player.coins+=6;
			break;
		case Coins9:
			player.coins+=9;
			break;
		case CoinsForAllCommonProd:
			player.coins+=player.getNumberOfCardFromCategoryForPlayer(CategoryName.CommonRessources);
			player.coins+=player.getLeftNeighbour().getNumberOfCardFromCategoryForPlayer(CategoryName.CommonRessources);
			player.coins+=player.getRightNeighbour().getNumberOfCardFromCategoryForPlayer(CategoryName.CommonRessources);
			break;
		case CoinsForAllRareProd:
			player.coins+=2*player.getNumberOfCardFromCategoryForPlayer(CategoryName.RareRessources);
			player.coins+=2*player.getLeftNeighbour().getNumberOfCardFromCategoryForPlayer(CategoryName.RareRessources);
			player.coins+=2*player.getRightNeighbour().getNumberOfCardFromCategoryForPlayer(CategoryName.RareRessources);
			break;
		case CoinsAndVictoryPointsForCommercial:
			player.coins+=player.getNumberOfCardFromCategoryForPlayer(CategoryName.Commercial);
			break;
		case CoinsAndVictoryPointsForCommonProd:
			player.coins+=player.getNumberOfCardFromCategoryForPlayer(CategoryName.CommonRessources);
			break;
		case CoinsAndVictoryPointsForRareProd:
			player.coins+=2*player.getNumberOfCardFromCategoryForPlayer(CategoryName.RareRessources);
			break;
		case CoinsAndVictoryPointsForWonderFloors:
			player.coins+=player.wonderFloorBuilt.size()*3;
			break;
		case Compas:
			player.tokens.put(TokenName.Compas,
					player.tokens.get(TokenName.Compas)+1);
			break;
		case Engrenage:
			player.tokens.put(TokenName.Engrenage,
					player.tokens.get(TokenName.Engrenage)+1);
			break;
		case Tablette:
			player.tokens.put(TokenName.Tablette,
					player.tokens.get(TokenName.Tablette)+1);
			break;
		case Military1:
			player.tokens.put(TokenName.Military,
					player.tokens.get(TokenName.Military)+1);
			break;
		case Military2:
			player.tokens.put(TokenName.Military,
					player.tokens.get(TokenName.Military)+2);
			break;
		case Military3:
			player.tokens.put(TokenName.Military,
					player.tokens.get(TokenName.Military)+3);
			break;
		case Military5:
			player.tokens.put(TokenName.Military,
					player.tokens.get(TokenName.Military)+5);
			break;
		case Military7:
			player.tokens.put(TokenName.Military,
					player.tokens.get(TokenName.Military)+7);
			break;
		case SpecialBuildFreeBuilding:
			player.specialEffects.add(EffectType.SpecialBuildFreeBuilding);
			player.hasFreeBuildingLeft = true;
			break;
		case SpecialBuildFromDiscard:
			player.hasToPlayFromDiscard = true;
			break;
		case SpecialBuryLeader:
			boolean todo = false;
			for(Building b : player.buildings){
				if(b.categoryName == CategoryName.Leader){
					todo = true;
					break;
				}
			}
			if(todo){
				player.specialState.add(new StateBuryLeaderChoice(player));
			}
			break;
		case SpecialPlayLeader:
			player.specialState.add(new StateCardChoice(player, true));
			break;
		case SpecialPlayLastCard:
			player.specialEffects.add(EffectType.SpecialPlayLastCard);
			break;
		case SpecialDraw4Leaders:
			for(int i = 0; i<4; i++){
				if(Game.gameSystem.remainingLeaders.size()>0){
					player.leaderToChoose.add(Game.gameSystem.remainingLeaders.get(0));
					Game.gameSystem.remainingLeaders.remove(0);
				} else {
					System.err.println("No leader remaining => bug !!!");
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
			player.specialEffects.add(type);
			break;
		case SpecialLeaderCheaper:
			player.specialEffects.add(EffectType.SpecialLeaderCheaper2);
			player.getLeftNeighbour().specialEffects.add(EffectType.SpecialLeaderCheaper1);
			player.getRightNeighbour().specialEffects.add(EffectType.SpecialLeaderCheaper1);
			break;
		case None:
			break;
		default:
			break;
		}
	}
	
	public static void undoaction(EffectType type, Player player){
		switch(type){
		case Compas:
			player.tokens.put(TokenName.Compas,
					player.tokens.get(TokenName.Compas)-1);
			break;
		case Engrenage:
			player.tokens.put(TokenName.Engrenage,
					player.tokens.get(TokenName.Engrenage)-1);
			break;
		case Tablette:
			player.tokens.put(TokenName.Tablette,
					player.tokens.get(TokenName.Tablette)-1);
			break;
		case Military1:
			player.tokens.put(TokenName.Military,
					player.tokens.get(TokenName.Military)-1);
			break;
		case Military2:
			player.tokens.put(TokenName.Military,
					player.tokens.get(TokenName.Military)-2);
			break;
		case Military3:
			player.tokens.put(TokenName.Military,
					player.tokens.get(TokenName.Military)-3);
			break;
		case Military5:
			player.tokens.put(TokenName.Military,
					player.tokens.get(TokenName.Military)-5);
			break;
		case Military7:
			player.tokens.put(TokenName.Military,
					player.tokens.get(TokenName.Military)-7);
			break;
		case SpecialBuildFreeBuilding:
			player.specialEffects.remove(EffectType.SpecialBuildFreeBuilding);
			player.hasFreeBuildingLeft = false;
			break;
		case SpecialPlayLastCard:
			player.specialEffects.remove(EffectType.SpecialPlayLastCard);
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
			player.specialEffects.remove(type);
			break;
		case None:
			break;
		default:
			break;
		}
	}
	

}
