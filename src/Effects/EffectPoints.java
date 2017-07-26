package Effects;

import java.util.Vector;

import gameSystem.Building;
import main.Game;
import ressources.CategoryName;
import ressources.TokenName;

public class EffectPoints {

	public static int points(EffectType type, int idJoueur){
		int nbpoints = 0;
		int temp;
		switch(type){
		case CoinsAndVictoryPointsForCommercial:
			nbpoints += EffectAction.getNumberOfCardFromCategoryForPlayer(idJoueur, CategoryName.Commercial);
			break;
		case CoinsAndVictoryPointsForCommonProd:
			nbpoints += EffectAction.getNumberOfCardFromCategoryForPlayer(idJoueur, CategoryName.CommonRessources);
			break;
		case CoinsAndVictoryPointsForRareProd:
			nbpoints += 2*EffectAction.getNumberOfCardFromCategoryForPlayer(idJoueur, CategoryName.RareRessources);
			break;
		case CoinsAndVictoryPointsForWonderFloors:
			nbpoints += Game.gameSystem.board.players.get(idJoueur).wonderFloorBuilt.size()*1;
			break;
		case VictoryPoints1:
		case VictoryPoints10:
		case VictoryPoints2:
		case VictoryPoints3:
		case VictoryPoints4:
		case VictoryPoints5:
		case VictoryPoints6:
		case VictoryPoints7:
		case VictoryPoints8:
		case VictoryPoints9:
		case VictoryPoints14:
			nbpoints += Integer.parseInt(type.name().substring(13));
			break;
		case VictoryPointsForCivilianNeighbours:
			nbpoints += EffectAction.getNumberOfCardFromCategoryForPlayer(Game.gameSystem.getLeftPlayer(idJoueur).id,CategoryName.Civilian);
			nbpoints += EffectAction.getNumberOfCardFromCategoryForPlayer(Game.gameSystem.getRightPlayer(idJoueur).id,CategoryName.Civilian);
			break;
		case VictoryPointsForCommercialNeighbours:
			nbpoints += EffectAction.getNumberOfCardFromCategoryForPlayer(Game.gameSystem.getLeftPlayer(idJoueur).id,CategoryName.Commercial);
			nbpoints += EffectAction.getNumberOfCardFromCategoryForPlayer(Game.gameSystem.getRightPlayer(idJoueur).id,CategoryName.Commercial);
			break;
		case VictoryPointsForCommonNeighbours:
			nbpoints += EffectAction.getNumberOfCardFromCategoryForPlayer(Game.gameSystem.getLeftPlayer(idJoueur).id,CategoryName.CommonRessources);
			nbpoints += EffectAction.getNumberOfCardFromCategoryForPlayer(Game.gameSystem.getRightPlayer(idJoueur).id,CategoryName.CommonRessources);
			break;
		case VictoryPointsForDefeatedNeighbours:
			nbpoints += Game.gameSystem.getRightPlayer(idJoueur).tokens.get(TokenName.DefeatToken1);
			nbpoints += Game.gameSystem.getLeftPlayer(idJoueur).tokens.get(TokenName.DefeatToken1);
			break;
		case VictoryPointsForMilitaryNeighbours:
			nbpoints += EffectAction.getNumberOfCardFromCategoryForPlayer(Game.gameSystem.getLeftPlayer(idJoueur).id,CategoryName.Military);
			nbpoints += EffectAction.getNumberOfCardFromCategoryForPlayer(Game.gameSystem.getRightPlayer(idJoueur).id,CategoryName.Military);
			break;
		case VictoryPointsForRareCommonGuildSet:
			nbpoints += EffectAction.getNumberOfCardFromCategoryForPlayer(idJoueur, CategoryName.CommonRessources);
			nbpoints += EffectAction.getNumberOfCardFromCategoryForPlayer(idJoueur, CategoryName.RareRessources);
			nbpoints += EffectAction.getNumberOfCardFromCategoryForPlayer(idJoueur, CategoryName.Guilds);
			break;
		case VictoryPointsForRareNeighbours:
			nbpoints += EffectAction.getNumberOfCardFromCategoryForPlayer(Game.gameSystem.getLeftPlayer(idJoueur).id,CategoryName.RareRessources)*2;
			nbpoints += EffectAction.getNumberOfCardFromCategoryForPlayer(Game.gameSystem.getRightPlayer(idJoueur).id,CategoryName.RareRessources)*2;
			break;
		case VictoryPointsForLeaderNeighbours:
			nbpoints += EffectAction.getNumberOfCardFromCategoryForPlayer(Game.gameSystem.getLeftPlayer(idJoueur).id,CategoryName.Leader)*2;
			nbpoints += EffectAction.getNumberOfCardFromCategoryForPlayer(Game.gameSystem.getRightPlayer(idJoueur).id,CategoryName.Leader)*2;
			break;
		case VictoryPointsForGuildNeighbours:
			nbpoints += EffectAction.getNumberOfCardFromCategoryForPlayer(Game.gameSystem.getLeftPlayer(idJoueur).id,CategoryName.Guilds)*3;
			nbpoints += EffectAction.getNumberOfCardFromCategoryForPlayer(Game.gameSystem.getRightPlayer(idJoueur).id,CategoryName.Guilds)*3;
			break;
		case VictoryPointsForScientificNeighbours:
			nbpoints += EffectAction.getNumberOfCardFromCategoryForPlayer(Game.gameSystem.getLeftPlayer(idJoueur).id,CategoryName.Scientific);
			nbpoints += EffectAction.getNumberOfCardFromCategoryForPlayer(Game.gameSystem.getRightPlayer(idJoueur).id,CategoryName.Scientific);
			break;
		case VictoryPointsForWonderFloors:
			nbpoints += Game.gameSystem.board.players.get(Game.gameSystem.getLeftPlayer(idJoueur).id).wonderFloorBuilt.size();
			nbpoints += Game.gameSystem.board.players.get(Game.gameSystem.getRightPlayer(idJoueur).id).wonderFloorBuilt.size();
			nbpoints += Game.gameSystem.board.players.get(idJoueur).wonderFloorBuilt.size();
			break;
		case VictoryPointsForCivilian:
		case VictoryPointsForCommercial:
		case VictoryPointsForCommonRessources:
		case VictoryPointsForScientific:
			nbpoints += EffectAction.getNumberOfCardFromCategoryForPlayer(idJoueur, CategoryName.valueOf(type.name().substring(16)));
			break;
		case VictoryPointsForRareRessources:
		case VictoryPointsForMilitary:
		case VictoryPointsForGuilds:
			nbpoints += EffectAction.getNumberOfCardFromCategoryForPlayer(idJoueur, CategoryName.valueOf(type.name().substring(16)))*2;
			break;
		case PointsPlaton:
			temp = EffectAction.getNumberOfCardFromCategoryForPlayer(idJoueur, CategoryName.CommonRessources);
			temp = Math.min(EffectAction.getNumberOfCardFromCategoryForPlayer(idJoueur, CategoryName.RareRessources), temp);
			temp = Math.min(EffectAction.getNumberOfCardFromCategoryForPlayer(idJoueur, CategoryName.Guilds), temp);
			temp = Math.min(EffectAction.getNumberOfCardFromCategoryForPlayer(idJoueur, CategoryName.Civilian), temp);
			temp = Math.min(EffectAction.getNumberOfCardFromCategoryForPlayer(idJoueur, CategoryName.Commercial), temp);
			temp = Math.min(EffectAction.getNumberOfCardFromCategoryForPlayer(idJoueur, CategoryName.Military), temp);
			temp = Math.min(EffectAction.getNumberOfCardFromCategoryForPlayer(idJoueur, CategoryName.Scientific), temp);
			nbpoints += temp*7;
			break;
		case PointsJustinien:
			temp = EffectAction.getNumberOfCardFromCategoryForPlayer(idJoueur, CategoryName.Civilian);
			temp = Math.min(EffectAction.getNumberOfCardFromCategoryForPlayer(idJoueur, CategoryName.Military), temp);
			temp = Math.min(EffectAction.getNumberOfCardFromCategoryForPlayer(idJoueur, CategoryName.Scientific), temp);
			nbpoints += temp*3;
			break;
		case PointsForMilitaryVictory:
			nbpoints += Game.gameSystem.board.players.get(idJoueur).tokens.get(TokenName.VictoryToken1);
			nbpoints += Game.gameSystem.board.players.get(idJoueur).tokens.get(TokenName.VictoryToken3);
			nbpoints += Game.gameSystem.board.players.get(idJoueur).tokens.get(TokenName.VictoryToken5);
			break;
		case PointsMidas:
			nbpoints += Game.gameSystem.board.players.get(idJoueur).coins/3;
			break;
		case PointsAmytis:
			nbpoints += Game.gameSystem.board.players.get(idJoueur).wonderFloorBuilt.size()*2;
			break;
		case SpecialCopyGuild:
			Vector<Building> guilds = new Vector<Building>();
			nbpoints = 0;
			for(Building b : Game.gameSystem.getRightPlayer(idJoueur).buildings){
				if(b.categoryName==CategoryName.Guilds){
					temp = 0;
					for(EffectType et : b.effects){
						temp += points(et, idJoueur);
					}
					if(temp>nbpoints){
						nbpoints = temp;
					}
				} 
			}
			for(Building b : Game.gameSystem.getLeftPlayer(idJoueur).buildings){
				if(b.categoryName==CategoryName.Guilds){
					temp = 0;
					for(EffectType et : b.effects){
						temp += points(et, idJoueur);
					}
					if(temp>nbpoints){
						nbpoints = temp;
					}
				} 
			}
			break;
		default:
			break;
		}
		return nbpoints;
	}
}
