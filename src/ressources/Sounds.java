package ressources;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;


public class Sounds {
	// STORE ALL THE SOUNDS

	private static HashMap<String, Sound> sounds;
	private static boolean isInit;

	public static void init(){
		// loading sounds
		sounds = new HashMap<String, Sound>();
		loadRepertoire("ressources/sounds/");
		isInit = true;
	}

	public static boolean isInit(){
		return isInit;
	}
	
	public static Sound get(String name) {
		if(isInit && sounds.containsKey(name.toLowerCase())){
			return sounds.get(name.toLowerCase());
		} else {
			//System.out.println("Error : trying to load an non-existing sound : "+name);
			return null;
		}
	}



	private static void loadRepertoire(String name){
		File repertoire = new File(name);
		File[] files=repertoire.listFiles();
		String s;
		Sound sound;
		try {
			for(int i=0; i<files.length; i++){
				s = files[i].getName();
				if(s.contains(".ogg")){
					// on load le son
					s = s.substring(0, s.length()-4);
					sound = new Sound(name+s+".ogg");
					sounds.put(s.toLowerCase(),sound);
					//					f = Images.class.getField(s);
					//					f.set(this, im);
					//this.images.put(s, new Image(name+s+".png"));
				} else if (!s.contains(".") ){
					// nouveau répertoire
					loadRepertoire(name+s+"/");

				}
			} 
		} catch (SlickException | SecurityException | IllegalArgumentException  e) {
			e.printStackTrace();
		}
	}
}
