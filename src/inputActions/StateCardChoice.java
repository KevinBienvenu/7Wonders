package inputActions;

import java.util.Vector;

import Effects.EffectType;
import gameSystem.Card;
import gameSystem.Wonder.Floor;
import main.Game;
import ressources.Data;

public class StateCardChoice extends State{

	public Vector<Card> cards;
	public Vector<Card> leaders = new Vector<Card>();
	public int coins;
	public Vector<EffectType> ressources, ressourcesLeft, ressourcesRight;
	public Vector<EffectType> tradeBuildings;
	public Vector<EffectType> specialEffects;
	public Vector<Integer> wonderFloors;

	public StateCardChoice(int idJoueur, boolean leader) {
		super("StateCardChoice", idJoueur);
		if(leader){
			this.cards = Game.gameSystem.board.players.get(idJoueur).leaderToChoose;
		} else {
			this.cards = Game.gameSystem.cards.get(idJoueur);
			this.leaders = Game.gameSystem.board.players.get(idJoueur).leaderToChoose;
		}
		this.ressources = Game.gameSystem.board.players.get(idJoueur).ressources;
		this.ressourcesLeft = Game.gameSystem.board.players.get((idJoueur+1)%Game.gameSystem.nbPlayer).ressources;
		this.ressourcesRight = Game.gameSystem.board.players.get((idJoueur-1+Game.gameSystem.nbPlayer)%Game.gameSystem.nbPlayer).ressources;
		this.tradeBuildings = Game.gameSystem.board.players.get(idJoueur).tradeBuildings;
		this.coins = Game.gameSystem.board.players.get(idJoueur).coins;
		this.wonderFloors = Game.gameSystem.board.players.get(idJoueur).getBuildableWonderFloors();
		this.specialEffects = new Vector<EffectType>();
		for(EffectType et : Game.gameSystem.board.players.get(idJoueur).specialEffects){
			switch(et){
			case SpecialBuildFreeBuilding:
				if(Game.gameSystem.board.players.get(idJoueur).hasFreeBuildingLeft){
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
	public String toString() {
		String s = "{\"idJoueur\":"+this.idJoueur+",";
		String state;
		// buildings
		s += "\"cards\":[";
		for(Card c : this.cards){
			s+="\""+c.building.name+"\",";
		}
		s = s.substring(0, s.length()-1)+"],";
		// leaders
		s += "\"leaders\":[";
		for(Card c : this.leaders){
			s+="\""+c.building.name+"\",";
		}
		if(this.leaders.size()>0){
			s = s.substring(0, s.length()-1);
		}
		s+="],";
		s += "\"cardoptions\":{";
		boolean removeComa = false;
		for(Card c : this.cards){
			state = Game.gameSystem.board.players.get(this.idJoueur).getCardBuildingPossibilites(c.building);
			if(!state.equals("ok")){
				s+="\""+c.building.name+"\":\""+state+"\",";
				removeComa = true;
			}
		}
		if(removeComa){
			s = s.substring(0, s.length()-1);
		}
		s += "},";
		// wonders
		s += "\"wonderIdName\":\""+this.player.wonderName+"\",";
		s += "\"wonderFace\":\""+this.player.wonderFace+"\",";
		s += "\"wonderFloors\":[";
		int i = 0;
		for(Floor f : Data.wonders.get(this.player.wonderName).floors.get(this.player.wonderFace)){
			if(this.player.wonderFloorBuilt.contains(i)){
				s+="\"alreadybuilt\",";	
			} else if(i==0 || this.player.wonderFloorBuilt.contains(i-1) || Data.wonders.get(this.player.wonderName).canBuildInAnyOrder){
				s+="\"ok\",";				
			} else {
				s+="\"pasok\",";
			}
			i+=1;
		}
		s = s.substring(0, s.length()-1)+"],";
		s += "\"ressources\":{"+handleRessources(ressources, true)+"},";
		s += "\"tradebuildings\":["+handleEffectTypes(tradeBuildings)+"],";
		s += "\"ressourcesleft\":{"+handleRessources(ressourcesLeft, false)+"},";
		s += "\"ressourcesright\":{"+handleRessources(ressourcesRight, false)+"},";
		s += "\"specialeffects\":["+handleEffectTypes(specialEffects)+"],";
		s += "\"coins\":"+coins+",";
		s += "\"key\":"+key+",";
		s += "\"name\":\""+Game.gameSystem.board.players.get(idJoueur).nickName+"\",";
		s += "\"state\":\""+id+"\",";
		s += "\"time\":\""+Game.gameSystem.t+"\",";
		s += handleAdditionalInfos(Game.gameSystem.board.players.get(idJoueur));
		s += "\"done\":\"false\"}";
		System.out.println(s);
		return s;
	}



}
