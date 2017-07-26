package ressources;

import java.io.File;
import java.util.HashMap;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;


public class Musics {
	
	private static HashMap<String, Music> musics;
	
	public static void init(){
		// loading musics
		musics = new HashMap<String, Music>();
		loadRepertoire("ressources/musics/");
	}


	public static Music get(String name) {
		if(musics.containsKey(name.toLowerCase())){
			return musics.get(name.toLowerCase());
		} else {
			//System.out.println("Error : trying to load an non-existing sound : "+name);
			return null;
		}
	}

	public static void loadRepertoire(String name){
		File repertoire = new File(name);
		File[] files=repertoire.listFiles();
		String s;
		Music music;
		try {
			for(int i=0; i<files.length; i++){
				s = files[i].getName();
				if(s.contains(".ogg")){
					// on charge la musique
					s = s.substring(0, s.length()-4);
					music = new Music(name+s+".ogg");
					musics.put(s.toLowerCase(),music);
				} else if (!s.contains(".")){
					// nouveau répertoire à charger
					loadRepertoire(name+s+"/");
				}
			} 
		} catch (SlickException | SecurityException | IllegalArgumentException  e) {
			e.printStackTrace();
		}
	}

}
