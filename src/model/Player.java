package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import actions.Action;
import effects.EffectType;
import enums.CategoryName;
import enums.TokenName;
import enums.WonderName;
import main.Game;
import ressources.Data;
import states.State;

public class Player {

	public int id;
	public int points;
	public String nickName;
	public WonderName wonderName;
	public String wonderFace;
	public HashSet<Integer> wonderFloorBuilt;
	
	public double angle = 0d;
	public float opacity = 0f;
	
	public int offsetXWonder = 0;
	public int offsetYWonder = 0;

	public int coins;

	public Vector<EffectType> ressources;
	public Vector<EffectType> tradeBuildings;
	public Vector<EffectType> specialEffects;

	public boolean hasFreeBuildingLeft = false;
	public boolean hasToPlayFromDiscard = false;

	public Vector<Card> cards;
	public Vector<Card> leaderToChoose;
	public Vector<Building> buildings;
	public HashMap<TokenName, Integer> tokens;

	public HashMap<CategoryName, Integer> pointsToDisplay;
	public Vector<Building> buriedCards;
	
	public Building leaderToShow = null;
	
	public int position;
	public int nbJoueur;
	
	private State state;
	public Vector<Action> actions = new Vector<Action>();
	public Vector<State> specialState = new Vector<State>();
	public int nbGame;
	public int bestScore;

	public Player(String nickName, WonderName wonder){
		this.nickName = nickName;
		this.wonderName = wonder;
		this.wonderFloorBuilt = new HashSet<Integer>();

		this.cards = new Vector<Card>();
		this.leaderToChoose = new Vector<Card>();
		this.buildings = new Vector<Building>();
		this.buriedCards = new Vector<Building>();
		this.tradeBuildings = new Vector<EffectType>();
		this.ressources = new Vector<EffectType>();
		this.specialEffects = new Vector<EffectType>();
		this.tokens = new HashMap<TokenName, Integer>();
		for(TokenName token : TokenName.values()){
			this.tokens.put(token, 0);
		}
		this.coins = 6;
		
	}


	public void majRessources(){
		this.ressources.clear();
		this.tradeBuildings.clear();
		Vector<EffectType> effects = new Vector<EffectType>();
		// Checking buildings
		for(Building b : this.buildings){
			for(EffectType effect : b.effects){
				effects.add(effect);
			}
		}
		// Checking wonders
		for(Integer i : this.wonderFloorBuilt){
			for(EffectType effect : Data.wonders.get(this.wonderName).floors.get(this.wonderFace).get(i).effects){
				effects.add(effect);
			}
		}
		// Adding basic wonder effect
		effects.addElement(Data.wonders.get(this.wonderName).effectTypes.get(this.wonderFace));
		for(EffectType effect : effects){
			switch(effect){
			// production double
			case ProductionArgileDouble:
			case ProductionPierreDouble:
			case ProductionBoisDouble:
			case ProductionMineraiDouble:
				// production simple
			case ProductionArgileSimple:
			case ProductionBoisSimple:
			case ProductionMineraiSimple:
			case ProductionPierreSimple:
				// production choix
			case ProductionArgileMinerai:
			case ProductionBoisArgile:
			case ProductionBoisMinerai:
			case ProductionMineraiPierre:
			case ProductionPierreArgile:
			case ProductionPierreBois:
			case ProductionRare:
			case ProductionCommon:
				// production rare
			case ProductionPapyrus:
			case ProductionTissu:
			case ProductionVerre:
				this.ressources.add(effect);
				break;
			case TradeCommonEastEqual1:
			case TradeCommonWestEqual1:
			case TradeRareBothEqual1:
				this.tradeBuildings.add(effect);
				break;
			default:
				break;

			}
		}
	}

	public Vector<Card> getCards(){
		return cards;
	}

	public Vector<Integer> getBuildableWonderFloors() {
		Vector<Integer> v = new Vector<Integer>();
		//TODO: check buildable wonder floor
		return v;
	}

	public String getCardBuildingPossibilites(Building b){
		String s = "ok";
		if(b.categoryName==CategoryName.Guilds && specialEffects.contains(EffectType.SpecialGuildsFree)){
			s = "free";
		}
		if(b.categoryName==CategoryName.Leader){
			if(specialEffects.contains(EffectType.SpecialLeaderFree)){
				return "free";
			} else if(specialEffects.contains(EffectType.SpecialLeaderCheaper2)){
				return "cheaper_gold_2";
			} else if(specialEffects.contains(EffectType.SpecialLeaderCheaper1)){
				return "cheaper_gold_1";
			}
		} else {
			if(b.categoryName==CategoryName.Civilian && specialEffects.contains(EffectType.CheaperCivilian)){
				s = "cheaper_ressource";
			} else if(b.categoryName==CategoryName.Military && specialEffects.contains(EffectType.CheaperMilitary)){
				s = "cheaper_ressource";
			}else if(b.categoryName==CategoryName.Scientific && specialEffects.contains(EffectType.CheaperScientific)){
				s = "cheaper_ressource";
			}
			for(Building b1 : this.buildings){
				if(b1.name.equals(b.name) || 
						(b.name.endsWith("2") && b.name.substring(0, b.name.length()-1).equals(b1.name.substring(0, b1.name.length()-1)))){
					return "alreadybuilt";
				} else {
					for(String b2 : b.chainDown){
						if(b2.equals(b1.idname)){
							s = "chain";
						}
					}
				}
			}
		}
		return s;
	}

	public void addToken(TokenName token, int nb){
		this.tokens.put(token, this.tokens.get(token)+nb);
	}



	public int getScore() {
		int p = 0;
		for(CategoryName cn : pointsToDisplay.keySet()){
			p+=pointsToDisplay.get(cn);
		}
		return p;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Player getLeftNeighbour() {
		if(Game.gameSystem.nbPlayer>0){
			return Game.gameSystem.board.players.get((id+1)%Game.gameSystem.nbPlayer);
		}
		return this;
	}
	
	public Player getRightNeighbour() {
		if(Game.gameSystem.nbPlayer>0){
			return Game.gameSystem.board.players.get((id-1+Game.gameSystem.nbPlayer)%Game.gameSystem.nbPlayer);
		}
		return this;
	}
	
	public int getNumberOfCardFromCategoryForPlayer(CategoryName cat){
		int temp_nb = 0;
		for(Building b : buildings){
			if(b.categoryName==cat){
				temp_nb+=1;
			}
		}
		return temp_nb;
	}

}
