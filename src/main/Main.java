package main;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import org.lwjgl.LWJGLUtil;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

import ia.IASystem.IAHandler;
import network.Communications;


public class Main {
	// A REGLER \\
	public static boolean pleinEcran = false;
	public static String hostname;
	public static int maxPlayers = 7;
	public static int nbIAPlayer = 0;
	public static int framerate = nbIAPlayer>0 ? 500 : 60;
	public static int nbRandPlayer = 0;
	public static boolean launchGame = false;
	public static boolean quickGame = false;
	public static boolean replay = false;
	public static long time;

	public static void main(String[] args) {


		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		System.setProperty("org.lwjgl.librarypath", new File(new File(System.getProperty("user.dir"), "native"), LWJGLUtil.getPlatformName()).getAbsolutePath());
		int resolutionX;
		int resolutionY;
		hostname = "http://games.battlemythe.net";
		//hostname = "http://localhost";
		if(pleinEcran){
			resolutionX = (int)screenSize.getWidth();		
			resolutionY = (int)screenSize.getHeight();
		}  else {
			resolutionX = (int)screenSize.getWidth()*2/3;		
			resolutionY = (int)screenSize.getHeight()*2/3;
//						resolutionX = 1920;		
//						resolutionY = 1080;
			//			resolutionX = 1680;		
			//			resolutionY = 1050;
		}
		try {
			Game game = new Game(resolutionX,resolutionY);
			AppGameContainer app = new AppGameContainer(game);
			Game.app = app;
			app.setTargetFrameRate(framerate);
			app.setShowFPS(false);
			app.setDisplayMode(resolutionX, resolutionY,pleinEcran);
			app.setAlwaysRender(true);
			app.setUpdateOnlyWhenVisible(false);
			app.setClearEachFrame(true);
			app.setVSync(true);
			//app.setSmoothDeltas(true);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}



}
