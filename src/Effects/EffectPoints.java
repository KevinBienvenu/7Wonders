package effects;

import java.util.Vector;

import enums.CategoryName;
import enums.TokenName;
import main.Game;
import model.Building;
import model.Player;

public class EffectPoints {

	public static int points(EffectType type, Player player){
		int nbpoints = 0;
		int temp;
		switch(type){
		case CoinsAndVictoryPointsForCommercial:
			nbpoints += player.getNumberOfCardFromCategoryForPlayer(CategoryName.Commercial);
			break;
		case CoinsAndVictoryPointsForCommonProd:
			nbpoints += player.getNumberOfCardFromCategoryForPlayer( CategoryName.CommonRessources);
			break;
		case CoinsAndVictoryPointsForRareProd:
			nbpoints += 2*player.getNumberOfCardFromCategoryForPlayer( CategoryName.RareRessources);
			break;
		case CoinsAndVictoryPointsForWonderFloors:
			nbpoints += player.wonderFloorBuilt.size()*1;
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
			nbpoints += player.getRightNeighbour().getNumberOfCardFromCategoryForPlayer(CategoryName.Civilian);
			nbpoints += player.getLeftNeighbour().getNumberOfCardFromCategoryForPlayer(CategoryName.Civilian);
			break;
		case VictoryPointsForCommercialNeighbours:
			nbpoints += player.getRightNeighbour().getNumberOfCardFromCategoryForPlayer(CategoryName.Commercial);
			nbpoints += player.getLeftNeighbour().getNumberOfCardFromCategoryForPlayer(CategoryName.Commercial);
			break;
		case VictoryPointsForCommonNeighbours:
			nbpoints += player.getRightNeighbour().getNumberOfCardFromCategoryForPlayer(CategoryName.CommonRessources);
			nbpoints += player.getLeftNeighbour().getNumberOfCardFromCategoryForPlayer(CategoryName.CommonRessources);
			break;
		case VictoryPointsForDefeatedNeighbours:
			nbpoints += player.getRightNeighbour().tokens.get(TokenName.DefeatToken1);
			nbpoints += player.getLeftNeighbour().tokens.get(TokenName.DefeatToken1);
			break;
		case VictoryPointsForMilitaryNeighbours:
			nbpoints += player.getRightNeighbour().getNumberOfCardFromCategoryForPlayer(CategoryName.Military);
			nbpoints += player.getLeftNeighbour().getNumberOfCardFromCategoryForPlayer(CategoryName.Military);
			break;
		case VictoryPointsForRareCommonGuildSet:
			nbpoints += player.getNumberOfCardFromCategoryForPlayer(CategoryName.CommonRessources);
			nbpoints += player.getNumberOfCardFromCategoryForPlayer(CategoryName.RareRessources);
			nbpoints += player.getNumberOfCardFromCategoryForPlayer(CategoryName.Guilds);
			break;
		case VictoryPointsForRareNeighbours:
			nbpoints += player.getRightNeighbour().getNumberOfCardFromCategoryForPlayer(CategoryName.RareRessources)*2;
			nbpoints += player.getLeftNeighbour().getNumberOfCardFromCategoryForPlayer(CategoryName.RareRessources)*2;
			break;
		case VictoryPointsForLeaderNeighbours:
			nbpoints += player.getRightNeighbour().getNumberOfCardFromCategoryForPlayer(CategoryName.Leader)*2;
			nbpoints += player.getLeftNeighbour().getNumberOfCardFromCategoryForPlayer(CategoryName.Leader)*2;
			break;
		case VictoryPointsForGuildNeighbours:
			nbpoints += player.getRightNeighbour().getNumberOfCardFromCategoryForPlayer(CategoryName.Guilds)*3;
			nbpoints += player.getLeftNeighbour().getNumberOfCardFromCategoryForPlayer(CategoryName.Guilds)*3;
			break;
		case VictoryPointsForScientificNeighbours:
			nbpoints += player.getRightNeighbour().getNumberOfCardFromCategoryForPlayer(CategoryName.Scientific);
			nbpoints += player.getLeftNeighbour().getNumberOfCardFromCategoryForPlayer(CategoryName.Scientific);
			break;
		case VictoryPointsForWonderFloors:
			nbpoints += player.getRightNeighbour().wonderFloorBuilt.size();
			nbpoints += player.getLeftNeighbour().wonderFloorBuilt.size();
			nbpoints += player.wonderFloorBuilt.size();
			break;
		case VictoryPointsForCivilian:
		case VictoryPointsForCommercial:
		case VictoryPointsForCommonRessources:
		case VictoryPointsForScientific:
			nbpoints += player.getNumberOfCardFromCategoryForPlayer(CategoryName.valueOf(type.name().substring(16)));
			break;
		case VictoryPointsForRareRessources:
		case VictoryPointsForMilitary:
		case VictoryPointsForGuilds:
			nbpoints += player.getNumberOfCardFromCategoryForPlayer(CategoryName.valueOf(type.name().substring(16)))*2;
			break;
		case PointsPlaton:
			temp = player.getNumberOfCardFromCategoryForPlayer(CategoryName.CommonRessources);
			temp = Math.min(player.getNumberOfCardFromCategoryForPlayer(CategoryName.RareRessources), temp);
			temp = Math.min(player.getNumberOfCardFromCategoryForPlayer(CategoryName.Guilds), temp);
			temp = Math.min(player.getNumberOfCardFromCategoryForPlayer(CategoryName.Civilian), temp);
			temp = Math.min(player.getNumberOfCardFromCategoryForPlayer(CategoryName.Commercial), temp);
			temp = Math.min(player.getNumberOfCardFromCategoryForPlayer(CategoryName.Military), temp);
			temp = Math.min(player.getNumberOfCardFromCategoryForPlayer(CategoryName.Scientific), temp);
			nbpoints += temp*7;
			break;
		case PointsJustinien:
			temp = player.getNumberOfCardFromCategoryForPlayer(CategoryName.Civilian);
			temp = Math.min(player.getNumberOfCardFromCategoryForPlayer(CategoryName.Military), temp);
			temp = Math.min(player.getNumberOfCardFromCategoryForPlayer(CategoryName.Scientific), temp);
			nbpoints += temp*3;
			break;
		case PointsForMilitaryVictory:
			nbpoints += player.tokens.get(TokenName.VictoryToken1);
			nbpoints += player.tokens.get(TokenName.VictoryToken3);
			nbpoints += player.tokens.get(TokenName.VictoryToken5);
			break;
		case PointsMidas:
			nbpoints += player.coins/3;
			break;
		case PointsAmytis:
			nbpoints += player.wonderFloorBuilt.size()*2;
			break;
		case SpecialCopyGuild:
			Vector<Building> guilds = new Vector<Building>();
			nbpoints = 0;
			for(Building b : player.getRightNeighbour().buildings){
				if(b.categoryName==CategoryName.Guilds){
					temp = 0;
					for(EffectType et : b.effects){
						temp += points(et, player);
					}
					if(temp>nbpoints){
						nbpoints = temp;
					}
				} 
			}
			for(Building b :player.getLeftNeighbour().buildings){
				if(b.categoryName==CategoryName.Guilds){
					temp = 0;
					for(EffectType et : b.effects){
						temp += points(et, player);
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
