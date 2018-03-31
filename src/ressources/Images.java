package ressources;

import java.io.File;
import java.util.HashMap;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import main.Game;
import render.PlayerRender;


public class Images {

	public static HashMap<String, Image> images;


	public static void init(){
		images = new HashMap<String, Image>();
		loadRepertoire("ressources/images/");
		float sizeImage = Math.min(Game.resX*PlayerRender.ratioSizeX*PlayerRender.ratioSizeXRessource/2, 
				Game.resY*PlayerRender.ratioSizeY*(1f-2f*PlayerRender.ratioSizeYName)/4f);
		HashMap<String, Image> toAdd = new HashMap<String, Image>();
		if(Game.resX!=1920 || Game.resY!=1080){
			for(String imageName : images.keySet()){
				images.put(imageName, images.get(imageName).getScaledCopy(Game.resX/1920f));
			}
		}
		images.putAll(toAdd);
		for(int i=1; i<9; i++){
			images.put("wonder"+i,images.get("wonder"+i).getScaledCopy(Game.resX/2, Game.resY/2));
		}
	}


	private static void loadRepertoire(String name){
		File repertoire = new File(name);
		File[] files=repertoire.listFiles();
		String s;
		Image im;
		try {
			for(int i=0; i<files.length; i++){
				s = files[i].getName();
				if(s.contains(".png")){
					// on load l'image
					s = s.substring(0, s.length()-4);
					im = new Image(name+s+".png");
					images.put(s.toLowerCase(),im);
					//					f = Images.class.getField(s);
					//					f.set(this, im);
					//this.images.put(s, new Image(name+s+".png"));
				} else if(s.contains(".jpg")){
					// on load l'image
					s = s.substring(0, s.length()-4);
					im = new Image(name+s+".jpg");
					images.put(s,im);
					//this.images.put(s, new Image(name+s+".jpg"));
				} else if(s.contains(".svg")){
					// on load l'image
					s = s.substring(0, s.length()-4);
					im = new Image(name+s+".svg");
					images.put(s,im);
					//this.images.put(s, new Image(name+s+".svg"));
				} else if (!s.contains(".") && !s.equals("unit")){
					// nouveau répertoire
					loadRepertoire(name+s+"/");

				}
			} 
		} catch (SlickException | SecurityException | IllegalArgumentException  e) {
			e.printStackTrace();
		}
	}

	public static Image get(String name){
		if(images.containsKey(name.toLowerCase())){
			return images.get(name.toLowerCase());
		} else {
			//			System.out.println("Error : trying to load an non-existing image : "+name);
			//			return this.images.get("image_manquante");
			return null;
		}
	}

}
