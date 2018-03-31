
package main;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;

import ia.IASystem;
import systems.ClassSystem;
import systems.EndGameSystem;
import systems.GameSystem;
import systems.IntroSystem;
import systems.LobbySystem;
import systems.TestSystem;




public class Game extends BasicGame {

	public static GameSystem gameSystem;
	public static LobbySystem lobbySystem;
	public static TestSystem testSystem;
	public static IntroSystem introSystem;
	public static EndGameSystem endGameSystem;

	public static boolean skipIntro = false;

	public static int resX, resY;

	public static AppGameContainer app; 

	public static ClassSystem system;

	public static Music music;
	



	public Game(int resX, int resY) {
		super("7 Wonders");
		Game.resX = resX;
		Game.resY = resY;
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		if(system!=null){
			system.render(gc, g);
		} else {
			throw new SlickException("Game - System missing error");
		}
	}

	@Override
	public void init(GameContainer arg0) throws SlickException {
		System.out.println("intro");
		introSystem = new IntroSystem();
		System.out.println("lobby");
		lobbySystem = new LobbySystem();
		System.out.println("done");
		gameSystem = new GameSystem();
		system = introSystem;
	}

	@Override
	public void update(GameContainer arg0, int arg1) throws SlickException {
		if(system!=null){
			system.update(arg0, arg1);
		} else {
			throw new SlickException("Game - System missing error");
		}
	}

	@Override
	public boolean closeRequested()
	{
		IASystem.close();
		return true;
	}


}