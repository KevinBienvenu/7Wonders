package systems;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import actions.Action;
import actions.ActionCard;
import effects.EffectAction;
import effects.EffectType;
import effects.PointsHandling;
import enums.AgeNames;
import enums.PhaseNames;
import ia.GameLog;
import main.Game;
import main.Main;
import model.Board;
import model.Building;
import model.Card;
import model.Player;
import model.PointsHandling2;
import network.Communications;
import render.AgeRender;
import render.PlayerRender;
import render.WarRender;
import ressources.Data;
import ressources.Images;
import ressources.Musics;
import states.State;
import states.StateCardChoice;
import states.StateLeaderChoice;
import states.StatePlayFromDiscard;

public class GameSystem extends ClassSystem {


	public int nbPlayer;

	public int t = -1;

	private boolean debugLeader = false;
	private boolean debugAgeI = false;
	private boolean debugAgeII = false;
	private boolean debugAgeIII = false;

	public Board board;

	public boolean leader;

	public AgeNames currentAge = AgeNames.INIT;

	public PhaseNames currentPhase = PhaseNames.DEBUTAGE;

	public int nbRoundRestant = 0;

	public Vector<Card> discardedCards;
	public Vector<Card> remainingLeaders;
	public Vector<Player> playersNotDone;


	public GameSystem(){
		this.board = new Board();
		this.discardedCards = new Vector<Card>();
		this.t = 0;
	}
	
	public GameSystem(int nbPlayers){
		this.nbPlayer = nbPlayers;
		this.board = new Board(nbPlayers);
		this.discardedCards = new Vector<Card>();
		this.t = 0;
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
			Player player;
			if(this.currentAge.idAge>0){
				for(Vector<Card> v : this.handleDeckCreation(this.nbPlayer, this.currentAge.idAge)){
					player = board.players.get(idPlayer);
					player.cards =  v;
					player.majRessources();
					if(player.specialEffects.contains(EffectType.SpecialBuildFreeBuilding)){
						player.hasFreeBuildingLeft = true;
					}
					idPlayer++;
					nbRoundRestant = v.size();
				}
				this.leader = true;
			} else {
				for(Vector<Card> v : this.handleLeaderDistribution(this.nbPlayer)){
					player = board.players.get(idPlayer);
					player.cards = v;
					idPlayer++;
					player.majRessources();
					EffectAction.action(Data.wonders.get(player.wonderName).effectTypes.get(player.wonderFace), player);
					nbRoundRestant = v.size();
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
//		necessaryLeaders.addElement("bilkis");
//		necessaryLeaders.addElement("salomon");
//		necessaryLeaders.addElement("archimede");
//		necessaryLeaders.addElement("hammurabi");
//		necessaryLeaders.addElement("imhotep");
//		necessaryLeaders.addElement("leonidas");
//		necessaryLeaders.addElement("mecene");
//		necessaryLeaders.addElement("ramses");
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
		// connection ï¿½ la db
		//Sending to all player the state
		this.t += 1;
		if(this.currentAge.idAge>0){
			for(Player player : board.players){
				Communications.updatePlayerState(player, new StateCardChoice(player, leader));
				board.players.get(player.id).leaderToShow = null;
			}
		} else {
			for(Player player : board.players){
				Communications.updatePlayerState(player, new StateLeaderChoice(player));
			}
		}
		if(debugLeader && this.currentAge==AgeNames.LEADERCHOICE){
//			Communications.actions = new HashMap<Integer, Vector<Action>>();
//			for(int i=0; i<board.players.size(); i++){
//				Communications.actions.put(i, new Vector<Action>());
//				Communications.actions.get(i).add(new ActionLeaderChoice(cards.get(i).get(0)));
//			}
//			this.currentPhase = PhaseNames.ANIMATIONPHASEI;
		} else if((debugAgeI && this.currentAge==AgeNames.AGEI) 
				|| (debugAgeII && this.currentAge==AgeNames.AGEII)
				|| (debugAgeIII && this.currentAge==AgeNames.AGEIII)){
//			Communications.actions = new HashMap<Integer, Vector<Action>>();
//			for(int i=0; i<board.players.size(); i++){
//				Communications.actions.put(i, new Vector<Action>());
//				if(leader){
//					Communications.actions.get(i).add(new ActionBuilding(board.players.get(i).leaderToChoose.get(0)));
//				} else {
//					Communications.actions.get(i).add(new ActionBuilding(cards.get(i).get(0)));
//				}
//			}
//			this.currentPhase = PhaseNames.ANIMATIONPHASEI;
		} else {
			// TODO
//			Communications.send(hashmap);
			System.out.println("debut tour");
			Communications.state = this.currentAge.name();
			this.currentPhase = PhaseNames.MAINTOUR;			
		}
	}

	public void mainAge(){
		//Check if all players have answered
		if(playersNotDone.size()==0){
			PlayerRender.initTypeRender();
			this.currentPhase = PhaseNames.ANIMATIONPHASEI;
			for(Player player : this.board.players){
				// Checking if we're not in special state
				if(specialStatePlaying!=null && player != specialStatePlaying){
					continue;
				}
				//Performing the players actions
				Card cardToRemove;
				boolean fromDiscard = false;
				cardToRemove = null;
				Action action;
				int indexAction = 0;
				while(indexAction < player.actions.size()){
					action = player.actions.get(indexAction);
					actionsToPlay.add(action);
					if(action instanceof ActionCard){
						cardToRemove = ((ActionCard)action).card;
						fromDiscard = ((ActionCard)action).removeCardFromDiscard;
						if(fromDiscard){
							discardedCards.remove(cardToRemove);
						} else {
							if(player.leaderToChoose.contains(cardToRemove)){
								player.leaderToChoose.remove(cardToRemove);
							} else if(player.cards.contains(cardToRemove)){
								player.cards.remove(cardToRemove);
							} else {
								try{
									throw new Exception("no card to remove");
								} catch(Exception e){
									e.printStackTrace();
								}
							}
						}
					} 
					action.playInitialEffect();
					indexAction += 1;
				}
			}
		} else {
			PlayerRender.updateTypeRender();
		}
	}

	public Vector<Action> actionsToPlay = new Vector<Action>();
	public Player specialStatePlaying = null;

	public void animationPhaseI(){
		// animating the actions
		if(actionsToPlay.size()>0){
			if(actionsToPlay.firstElement().update()){
				actionsToPlay.removeElementAt(0);
			} 
			return;
		}
		if(this.nbRoundRestant==2 && this.currentAge.idAge>0){
			// on dïéfausse les cartes restantes sauf si un joueur possï¿½de l'effet play last card
			for(Player player : board.players){
				if(player.getCards().size()>0){
					if(player.specialEffects.contains(EffectType.SpecialPlayLastCard)){
						player.specialState.add(new StateCardChoice(player, false));
					} else {
						this.discardedCards.add(player.getCards().remove(0));
					}
				}
			}
			for(Player player : board.players){
				if(player.hasToPlayFromDiscard){
					if(Game.gameSystem.discardedCards.size()>0){
						boolean canDo = false;
						for(Card card : Game.gameSystem.discardedCards){
							if(!player.getCardBuildingPossibilites(card.building).equals("alreadybuilt")){
								canDo = true;
							}
						}
						if(canDo){
							player.specialState.add(new StatePlayFromDiscard(player));
						}
					}
					player.hasToPlayFromDiscard = false;
				}
			}
		}
		for(Player player : board.players){
			if(player.specialState.size()>0){
				Communications.updatePlayerState(player, player.specialState.remove(0));
				specialStatePlaying = player;
				break;
			}
		}
		if(Communications.checkIfDone().size()==0){
			this.currentPhase = PhaseNames.ENDTOUR;
			specialStatePlaying = null;
		} else {
			this.currentPhase = PhaseNames.MAINTOUR;
		}
	}


	public void endTour(){
		for(Player p : board.players){
			p.leaderToShow = null;
		}
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
				v = board.players.get(nbPlayer-1).cards;
				for(int i=nbPlayer-1; i>-1; i--){
					if(i==0){
						board.players.get(i).cards = v;
					} else {
						board.players.get(i).cards = board.players.get(i+toAdd).cards;
					}
				}
			} else {
				v = board.players.get(0).cards;
				for(int i=0; i<nbPlayer; i++){
					if(i==nbPlayer-1){
						board.players.get(i).cards = v;
					} else {
						board.players.get(i).cards = board.players.get(i+toAdd).cards;
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
		if(Main.nbIAPlayer==0){
			Game.endGameSystem = new EndGameSystem(this.board.players);
			Game.system = Game.endGameSystem;
		} else {
			GameLog.computeFinalFiles();
			Game.lobbySystem = new LobbySystem();
			Communications.init();
			Game.system = Game.lobbySystem;
		}
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
		case MAINTOUR:
			if(Musics.isInit() && (music.length()==0 ||!music.startsWith("ambiance") || !Game.music.playing())){
				launchMusic();
			}
			break;
		case ENDAGE:
			if(Musics.isInit() && !music.equals("guerre") && this.currentAge.idAge>0){
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
		Game.music = Musics.get(music);
		if(Game.music!=null)
			Game.music.play(1f,0.6f);
	}


	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		this.board.render(g);
		if(actionsToPlay.size()>0){
			actionsToPlay.firstElement().render(g);
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
		playersNotDone = Communications.checkIfDone();
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
