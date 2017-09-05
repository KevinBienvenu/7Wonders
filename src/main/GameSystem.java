package main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import Effects.EffectAction;
import Effects.EffectType;
import Effects.PointsHandling;
import gameSystem.Board;
import gameSystem.Building;
import gameSystem.Card;
import gameSystem.Player;
import gameSystem.PointsHandling2;
import inputActions.Action;
import inputActions.ActionBuilding;
import inputActions.ActionCard;
import inputActions.ActionLeaderChoice;
import inputActions.Communications;
import inputActions.State;
import inputActions.StateCardChoice;
import inputActions.StateLeaderChoice;
import main.LobbySystem.LobbyPlayer;
import render.AgeRender;
import render.PlayerRender;
import render.WarRender;
import ressources.AgeNames;
import ressources.Data;
import ressources.Images;
import ressources.Musics;
import ressources.PhaseNames;
import ressources.TokenName;

public class GameSystem extends ClassSystem {


	public int nbPlayer;

	private boolean debugLeader = false;
	private boolean debugAgeI = false;
	private boolean debugAgeII = false;
	private boolean debugAgeIII = false;

	public Board board;

	public boolean leader;

	public AgeNames currentAge = AgeNames.INIT;

	public PhaseNames currentPhase = PhaseNames.DEBUTAGE;

	public int nbRoundRestant = 0;

	public HashMap<Integer, Vector<Card>> cards;
	public Vector<Card> discardedCards;
	public Vector<Card> remainingLeaders;
	public Vector<HashMap<Integer,State>> actionToPlay;


	public GameSystem(Vector<LobbyPlayer> players){
		this.nbPlayer = players.size();
		this.board = new Board(players);
		this.discardedCards = new Vector<Card>();
	}
	public GameSystem(int nbPlayers){
		this.nbPlayer = nbPlayers;
		this.board = new Board(nbPlayers);
		this.discardedCards = new Vector<Card>();
	}

	public Player getLeftPlayer(int idJoueur){
		return Game.gameSystem.board.players.get((idJoueur+1)%Game.gameSystem.nbPlayer);
	}

	public Player getRightPlayer(int idJoueur){
		return Game.gameSystem.board.players.get((idJoueur-1+Game.gameSystem.nbPlayer)%Game.gameSystem.nbPlayer);
	}

	/**
	 *   FUNCTIONS DE DEBUT DE PARTIE
	 */

	public void handleInitGame(){
		AgeRender.init(0);
		this.currentAge = AgeNames.LEADERCHOICE;
	}


	/**
	 *   FUNCTIONS DES AGES I-III
	 */
	public void initAge(){
		if(AgeRender.update()){
			int idPlayer = 0;
			cards = new HashMap<Integer, Vector<Card>>();
			if(this.currentAge.idAge>0){
				for(Vector<Card> v : this.handleDeckCreation(this.nbPlayer, this.currentAge.idAge)){
					cards.put(idPlayer, v);
					this.board.players.get(idPlayer).majRessources();
					if(board.players.get(idPlayer).specialEffects.contains(EffectType.SpecialBuildFreeBuilding)){
						board.players.get(idPlayer).hasFreeBuildingLeft = true;
					}
					idPlayer++;
					nbRoundRestant = v.size();
				}
				this.leader = true;
			} else {
				Game.gameSystem.board.players.get(idPlayer).majRessources();
				for(Vector<Card> v : this.handleLeaderDistribution(this.nbPlayer)){
					cards.put(idPlayer, v);
					idPlayer++;
					nbRoundRestant = v.size();
				}
				// also initializing wonder basic effects
				for(Player p : board.players){
					EffectAction.action(Data.wonders.get(p.wonderName).effectTypes.get(p.wonderFace), p.id);
				}
			}
			this.currentPhase = PhaseNames.DEBUTTOUR;
		}
	}


	public Vector<Vector<Card>> handleLeaderDistribution(int nbPlayer){
		Vector<Vector<Card>> deck = new Vector<Vector<Card>>();
		// import des leaders
		this.remainingLeaders = new Vector<Card>();
		// debug : necessary leaders:
		Vector<String> necessaryLeaders = new Vector<String>();
		necessaryLeaders.add("bilkis");
		Card c;
		for(String l : Data.leaderNames){
			c = new Card(Data.leaders.get(l));
			if(!necessaryLeaders.contains(l)){
				try{
					this.remainingLeaders.insertElementAt(c,(int)(Math.random()*remainingLeaders.size()));
				} catch(Exception e){
					this.remainingLeaders.add(c);
				}
			}
		}
		// ajout des leaders
		for(int i=0; i<nbPlayer; i++){
			deck.add(new Vector<Card>());
			for(int nCard=0; nCard<4; nCard++){
				if(necessaryLeaders.size()>0){
					deck.lastElement().add(new Card(Data.leaders.get(necessaryLeaders.remove(0))));
				} else {
					deck.lastElement().add(this.remainingLeaders.remove((int)(Math.random()*this.remainingLeaders.size())));
				}
				System.out.println(deck.lastElement().lastElement().building.idname);
			}
		}
		return deck;
	}


	public Vector<Vector<Card>> handleDeckCreation(int nbPlayer, int currentAge){
		Vector<Vector<Card>> deck = new Vector<Vector<Card>>();
		Vector<Card> deckTotal = new Vector<Card>();
		Building b;
		// ajout des batiments
		for(String buildingName : Data.buildingNames){
			b = Data.buildings.get(buildingName);
			if(b.age==currentAge){
				for(Integer i : b.numberPlayerRequired){
					if(i<=nbPlayer){
						deckTotal.add(new Card(b));
					}
				}
			}
		}
		// ajout des guildes
		if(currentAge==3){
			HashSet<String> guildNames = new HashSet<String>();
			String temp_name;
			for(int i = 0; i<nbPlayer+2; i++){
				do{
					temp_name = Data.guildNames.get((int)(Math.random()*Data.guildNames.size()));
				}while(guildNames.contains(temp_name));
				deckTotal.add(new Card(Data.buildings.get(temp_name)));
				guildNames.add(temp_name);
			}
		}
		int nbCardPerPlayer = deckTotal.size()/nbPlayer;
		for(int i=0; i<nbPlayer; i++){
			deck.add(new Vector<Card>());
			for(int nCard=0; nCard<nbCardPerPlayer; nCard++){
				deck.lastElement().add(deckTotal.remove((int)(Math.random()*deckTotal.size())));
			}
		}
		return deck;
	}

	public void debutTour(){
		// connection à la db
		//Sending to all player the state
		HashMap<Integer, State> hashmap = new HashMap<Integer, State>();
		if(this.currentAge.idAge>0){
			StateCardChoice state;
			for(int i=0; i<this.nbPlayer; i++){
				state = new StateCardChoice(i, leader);
				hashmap.put(i, state);
			}
		} else {
			StateLeaderChoice state;
			for(int i=0; i<this.nbPlayer; i++){
				state = new StateLeaderChoice(i);
				hashmap.put(i, state);
			}
		}
		this.actionToPlay = new Vector<HashMap<Integer, State>>();
		if(debugLeader && this.currentAge==AgeNames.LEADERCHOICE){
			Communications.actions = new HashMap<Integer, Vector<Action>>();
			for(int i=0; i<board.players.size(); i++){
				Communications.actions.put(i, new Vector<Action>());
				Communications.actions.get(i).add(new ActionLeaderChoice(cards.get(i).get(0)));
			}
			this.currentPhase = PhaseNames.ANIMATIONPHASEI;
		} else if((debugAgeI && this.currentAge==AgeNames.AGEI) 
				|| (debugAgeII && this.currentAge==AgeNames.AGEII)
				|| (debugAgeIII && this.currentAge==AgeNames.AGEIII)){
			Communications.actions = new HashMap<Integer, Vector<Action>>();
			for(int i=0; i<board.players.size(); i++){
				Communications.actions.put(i, new Vector<Action>());
				if(leader){
					Communications.actions.get(i).add(new ActionBuilding(board.players.get(i).leaderToChoose.get(0)));
				} else {
					Communications.actions.get(i).add(new ActionBuilding(cards.get(i).get(0)));
				}
			}
			this.currentPhase = PhaseNames.ANIMATIONPHASEI;
		} else {
			Communications.send(hashmap);
			Communications.state = this.currentAge.name();
			this.currentPhase = PhaseNames.MAINTOUR;			
		}
	}

	public void mainAge(){
		//Check if all players have answered
		if(Communications.isDone()){
			System.out.println("communication is done!");
			Communications.receiving = false;
			PlayerRender.initTypeRender();
			this.currentPhase = PhaseNames.ANIMATIONPHASEI;
		} else {
			PlayerRender.updateTypeRender();
		}
	}

	public Action action = null;

	public void animationPhaseI(){
		// animating the actions
		if(action!=null){
			if(action.update()){
				action = null;
			} 
			return;
		}
		//Performing the players actions
		Card cardToRemove;
		boolean fromDiscard = false;
		for(Integer i : Communications.actions.keySet()){
			cardToRemove = null;
			if(Communications.actions.get(i).size()>0){
				Action a = Communications.actions.get(i).remove(0);
				if(a instanceof ActionCard){
					cardToRemove = ((ActionCard) a).card;
					fromDiscard = ((ActionCard)a).removeCardFromDiscard;
					if(fromDiscard){
						discardedCards.remove(cardToRemove);
					} else {
						if(board.players.get(i).leaderToChoose.contains(cardToRemove)){
							board.players.get(i).leaderToChoose.remove(cardToRemove);
						} else {
							cards.get(i).remove(cardToRemove);
						}
					}
				} 
				a.play(this, i);
				action = a;
				return;
			}
		}
		if(this.nbRoundRestant==2 && this.currentAge.idAge>0){
			// on défausse les cartes restantes sauf si un joueur possède l'effet play last card
			for(Integer i : cards.keySet()){
				if(cards.get(i).size()>0){
					if(board.players.get(i).specialEffects.contains(EffectType.SpecialPlayLastCard)){
						HashMap<Integer, State> hashmap = new HashMap<Integer, State>();
						hashmap.put(i, new StateCardChoice(i, false));
						actionToPlay.add(hashmap);
					} else {
						this.discardedCards.add(cards.get(i).remove(0));
					}
				}
			}
		}
		this.currentPhase = PhaseNames.MAINSPECIALTOUR;
	}

	public void mainAgeSpecial(GameContainer gc){
		PlayerRender.updateTypeRender();
		if(this.actionToPlay.size()>0 || Communications.receiving){
			if(Communications.receiving){
				if(Communications.isDone() || gc.getInput().isKeyPressed(Input.KEY_SPACE)){
					Communications.receiving = false;
					PlayerRender.initTypeRender();
					System.out.println("done!");
					this.currentPhase = PhaseNames.ANIMATIONPHASEI;
				}
			} else {
				System.out.println("==> special actions");
				HashMap<Integer, State> ss = this.actionToPlay.remove(0);
				Communications.send(ss);
				Communications.state = this.currentAge.name();
			}
		} else {
			this.currentPhase = PhaseNames.ENDTOUR;
		}

	}

	public void endTour(){
		if(leader){
			this.currentPhase = PhaseNames.DEBUTTOUR;
			leader = false;
			return;
		}
		this.nbRoundRestant-=1;
		if((this.nbRoundRestant==1 && this.currentAge.idAge>0) || this.nbRoundRestant==0){
			if(this.currentAge.idAge>0)
				WarRender.init(this.currentAge.idAge);
			this.currentPhase = PhaseNames.ENDAGE;
		} else {
			// on distribue les cartes restantes
			int toAdd = +1;
			Vector<Card> v;
			if(this.currentAge==AgeNames.AGEII || this.currentAge==AgeNames.LEADERCHOICE){
				toAdd-=2;
				v = cards.get(cards.size()-1);
				for(int i=nbPlayer-1; i>-1; i--){
					if(i==0){
						cards.put(i, v);
					} else {
						cards.put(i, cards.get(i+toAdd));
					}
				}
			} else {
				v = cards.get(0);
				for(int i=0; i<nbPlayer; i++){
					if(i==nbPlayer-1){
						cards.put(i, v);
					} else {
						cards.put(i, cards.get(i+toAdd));
					}
				}
			}
			this.currentPhase = PhaseNames.DEBUTTOUR;
		}
	}

	public void endAge(){
		//Victoire militaires
		if(this.currentAge.idAge>0 && !WarRender.update()){
			return;
		}
		Images.images.put("fleche_circulaire",Images.get("fleche_circulaire").getFlippedCopy(true,false));
		switch(this.currentAge){
		case LEADERCHOICE:
			this.currentAge = AgeNames.AGEI;
			AgeRender.init(this.currentAge.idAge);
			break;
		case AGEI:
			this.currentAge = AgeNames.AGEII;
			AgeRender.init(this.currentAge.idAge);
			break;
		case AGEII:
			this.currentAge = AgeNames.AGEIII;
			AgeRender.init(this.currentAge.idAge);
			break;
		case AGEIII:
			this.currentAge = AgeNames.END;
			break;
		default:
		}

		this.currentPhase = PhaseNames.DEBUTAGE;
	}

	/**
	 *   FUNCTIONS DE FIN DE PARTIE
	 */

	public void handleEndGame(GameContainer arg0){
		PointsHandling.computePoints();
		Game.endGameSystem = new EndGameSystem(this.board.players);
		Game.system = Game.endGameSystem;
	}

	/**
	 *  Utility functions
	 */

	public String music = "";
	public int previous = -1;
	public int dejavu = -1;

	public void updateMusicAmbiance(){
		switch(this.currentPhase){
		case DEBUTAGE:
			break;
		case DEBUTTOUR:
		case ANIMATIONPHASEI:
		case ENDTOUR:
		case LEADERCHOICE:
		case MAINSPECIALTOUR:
		case MAINTOUR:
			if(music.length()==0 ||!music.startsWith("ambiance") || !Game.music.playing()){
				launchMusic();
			}
			break;
		case ENDAGE:
			if(!music.equals("guerre") && this.currentAge.idAge>0){
				music = "guerre";
				Game.music.fade(1000, 0, true);
				Game.music = Musics.get("guerre");
				Game.music.play(1f,0.6f);
			}
			break;
		default:
			break;
		}
	}

	public void launchMusic(){
		if(music.startsWith("ambiance")){
			previous = dejavu;
			dejavu = Integer.parseInt(music.substring(8));
		}
		int i;
		do{
			i = (int)(Math.random()*10);
		} while(i==dejavu || i==previous);
		music = "ambiance"+i;
		System.out.println(music+" "+dejavu+" "+previous);
		Game.music = Musics.get(music);
		Game.music.play(1f,0.6f);
	}


	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		this.board.render(g);
		if(action!=null){
			action.render(g);
		}
		if(WarRender.rendering){
			WarRender.render(g);
		}
		if(AgeRender.cooldown>0){
			AgeRender.render(g);
		}
		if(this.currentAge==AgeNames.END){
			PointsHandling2.render(g);
		}
	}


	@Override
	public void update(GameContainer arg0, int arg1) throws SlickException {
		updateMusicAmbiance();
		switch(this.currentAge){
		case INIT:
			this.handleInitGame();
			break;
		case LEADERCHOICE:
		case AGEI:
		case AGEII:
		case AGEIII:
			switch(this.currentPhase){
			case DEBUTAGE:
				this.initAge();
				break;
			case LEADERCHOICE:
				break;
			case DEBUTTOUR:
				this.debutTour();
				break;
			case MAINTOUR:
				this.mainAge();
				break;
			case MAINSPECIALTOUR:
				this.mainAgeSpecial(arg0);
				break;
			case ANIMATIONPHASEI:
				this.animationPhaseI();
				break;
			case ENDTOUR:
				this.endTour();
				break;
			case ENDAGE:
				this.endAge();
				break;
			default:
				break;
			}
			break;
		case END:
			this.handleEndGame(arg0);
			break;

		}
	}



}
