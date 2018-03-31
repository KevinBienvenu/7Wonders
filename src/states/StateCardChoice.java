package states;

import java.util.Vector;
import java.util.stream.IntStream;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import effects.EffectType;
import enums.StateNames;
import main.Game;
import model.Card;
import model.Player;
import model.Wonder.Floor;
import ressources.Data;

public class StateCardChoice extends State{

	public Vector<Card> leaders = new Vector<Card>();
	public Vector<EffectType> ressources, ressourcesLeft, ressourcesRight;
	public Vector<EffectType> tradeBuildings;
	public Vector<EffectType> specialEffects;
	public Vector<Integer> wonderFloors;

	public StateCardChoice(Player player, boolean leader) {
		super(StateNames.CARD_CHOICE, player);
		if(leader){
			this.cards = player.leaderToChoose;
		} else {
			this.cards = player.getCards();
			this.leaders = player.leaderToChoose;
		}
		this.ressources = player.ressources;
		this.ressourcesLeft = player.getLeftNeighbour().ressources;
		this.ressourcesRight = player.getRightNeighbour().ressources;
		this.tradeBuildings = player.tradeBuildings;
		this.wonderFloors = player.getBuildableWonderFloors();
		this.specialEffects = new Vector<EffectType>();
		for(EffectType et : player.specialEffects){
			switch(et){
			case SpecialBuildFreeBuilding:
				if(player.hasFreeBuildingLeft){
					this.specialEffects.add(EffectType.SpecialBuildFreeBuilding);
				}
				break;
			case CheaperWonder:
			case SpecialBilkis:
				this.specialEffects.add(et);
				break;
			default:
			}
		}
	}

	@Override
	public void handleBody(JsonObject body) {
		// card options
		this.cards.stream().forEach(card -> {
			body.getAsJsonObject("cardoptions").addProperty(card.building.name, player.getCardBuildingPossibilites(card.building));
		});
		// wonders
		Vector<Floor> floors = Data.wonders.get(this.player.wonderName).floors.get(this.player.wonderFace);
		IntStream.range(0, floors.size())
			.mapToObj(i -> getWonderFloorState(player, i))
			.forEach(state -> {
				body.getAsJsonArray("wonderFloors").add(new JsonPrimitive(state));
			});
		// ressources
		body.add("ressources", handleRessources(ressources, true));
		body.add("tradebuildings", handleEffectTypes(tradeBuildings));
		body.add("ressourcesleft", handleRessources(ressourcesLeft, false));
		body.add("ressourcesright", handleRessources(ressourcesRight, false));
		body.add("specialeffects", handleEffectTypes(specialEffects));
	}
	
	public String getWonderFloorState(Player player, int i){
		if(this.player.wonderFloorBuilt.contains(i)){
			return "alreadybuilt";	
		} else if(i==0 || this.player.wonderFloorBuilt.contains(i-1) || Data.wonders.get(this.player.wonderName).canBuildInAnyOrder){
			return "ok";				
		} else {
			return "pasok";
		}
	}

}
