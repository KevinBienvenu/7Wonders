package main;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.DeferredResource;
import org.newdawn.slick.loading.LoadingList;

import inputActions.Communications;
import ressources.Data;
import ressources.Images;
import ressources.Musics;
import ressources.Sounds;

public class IntroSystem extends ClassSystem{

	public Image background;
	public int nbLoadedThing;
	public boolean waitingLoading;
	public String lastThing;
	public DeferredResource nextResource;

	public boolean hasPressKey = false;

	public int cooldownTotal = 920;
	public int cooldownMid = 820;
	public int cooldownMusic = 880;
	public int cooldownIntro = cooldownTotal;
	
	public Vector<IntroWonder> wonders;



	public IntroSystem(){
		try {
			background = new Image("ressources/images/wonder0.jpg");
			if(Game.resX<1920){
				background = background.getScaledCopy(Game.resX, Game.resY);
			}
		} catch (SlickException e) {}
		Musics.init();
		Sounds.init();
		LoadingList.setDeferredLoading(true);
		Data.init();
		Data.pushBuildingList();
		Images.init();
		nbLoadedThing = LoadingList.get().getRemainingResources();
		Communications.init();
		wonders = new Vector<IntroWonder>();
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		g.setColor(Color.black);
		g.fillRect(0, 0, Game.resX, Game.resY);
		g.drawImage(background,0,0);
		if(!hasPressKey && cooldownIntro<=200){
			g.setColor(Color.white);
			String s = "Appuyez sur Entrée";
			Data.font_main.drawString(Game.resX/2-Data.font_main.getWidth(s)/2, 3*Game.resY/4,s);
		}
		if(LoadingList.get().getRemainingResources() > 0){
			if(lastThing!=null){
				int startBarX = (int) (Game.resX/10);
				int startBarY = (int) (18*Game.resY/20);
				int sizeBarX = (int) (Game.resX - 2*startBarX);
				int sizeBarY = (int)(Game.resY/40);
				g.setColor(Color.white);
				g.fillRect(startBarX-2, startBarY-2,sizeBarX+4, sizeBarY+4);
				g.setColor(Color.black);
				g.fillRect(startBarX, startBarY,sizeBarX, sizeBarY);
				float x = 1f*(nbLoadedThing-LoadingList.get().getRemainingResources())/nbLoadedThing;
				g.setColor(new Color(1f-x,0.75f-0.5f*x,x));
				g.fillRect(startBarX, startBarY,sizeBarX*(nbLoadedThing-LoadingList.get().getRemainingResources())/nbLoadedThing, sizeBarY);
				if(LoadingList.get().getRemainingResources() > 0){
					g.setColor(Color.white);
					g.drawString(""+lastThing, startBarX+20f, startBarY+sizeBarY/2-Data.font_main.getHeight("Hg")/2);
				}
			}
		} else if (cooldownIntro<cooldownMusic) {
			for(IntroWonder iw : this.wonders){
				iw.render(g);
			}
			g.drawImage(Images.get("title-background"),Game.resX/2-Images.get("title-background").getWidth()/2,0);
		}

	}

	@Override
	public void update(GameContainer gc, int arg1) throws SlickException {
		
		if (LoadingList.get().getRemainingResources() > 0) { 
			if(waitingLoading == false){
				waitingLoading = true;
				try {
					Thread.sleep((long) 0.1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return;
			}
			nextResource = LoadingList.get().getNext(); 
			try {
				//				System.out.println(nextResource.getDescription());
				nextResource.load();
				lastThing = nextResource.getDescription();
			} catch (IOException e) {
				e.printStackTrace();
			}
			waitingLoading = false;
			Game.app.setTargetFrameRate(Main.framerate);
			return;
			
		
		} else if(cooldownIntro>cooldownMid){
			if(Game.music == null){
				Game.music = Musics.get("intro");
				Game.music.loop(1f,3f);
			}
			cooldownIntro--;
		} else if(cooldownIntro>0){
			if(cooldownIntro%90==0 && this.wonders.size()<(new File("ressources/images/intro").listFiles().length)){
				this.wonders.add(new IntroWonder(wonders.size()+1));
			}
			cooldownIntro--;
			if(cooldownIntro%2==0){
				Images.images.put("title-background",Images.get("title-background").getScaledCopy(0.999f));
			}
		} else if(cooldownIntro>-200){
			cooldownIntro--;
		} else {
			launchGame();
		}
		
		Input in = gc.getInput();
		if(in.isKeyPressed(Input.KEY_SPACE) || in.isKeyPressed(Input.KEY_RETURN)){
			launchGame();
		}
		for(IntroWonder iw : this.wonders){
			iw.update();
		}
	}
	
	public void launchGame(){
		if(Game.skipIntro){
			Game.gameSystem = new GameSystem(7);
			Game.system = Game.gameSystem;
		} else {
			Game.system = Game.lobbySystem;
		}
		
	}
	
	public class IntroWonder{
		public int x, y;
		public int sens;
		public int i;
		public IntroWonder(int i){
			this.i = i;
			y = (int)(Math.random()*Game.resY*(0.75)-Game.resY/8);
			if(Math.random()>0.5){
				sens = Game.resX/160;
				x = -Game.resX/2;
			} else {
				sens = -Game.resX/160;
				x = Game.resX;
			}
		}
		public void update(){
			x+=sens;
		}
		public void render(Graphics g){
			g.drawImage(Images.get("wonder"+i), x,y);
		}
	}

}
