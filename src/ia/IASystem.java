package ia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

import inputActions.Action;
import inputActions.State;

public class IASystem {
	
	private static Vector<Process> vp;
	private static boolean isInit;
	
	
	private static void init(){
		vp = new Vector<Process>();
		isInit = true;
	}
	
	public static void startIA(int nbJoueurIA, int nbJoueurRand){
		if(!isInit){
			init();
		}
		Process p;
		try {
			p = Runtime.getRuntime().exec("python src/ia/IA.py "+nbJoueurIA+" "+nbJoueurRand);
			vp.add(p);
			(new IAHandler(p, "IA_")).start();
		} catch (IOException e) {}
	}
	
	public static void startIA(){
		Process p;
		try {
			p = Runtime.getRuntime().exec("set FLASK_APP=src/ia/IA.py& python -m flask run");
			vp.add(p);
			(new IAHandler(p, "IA_")).start();
		} catch (IOException e) {}
	}
	
	public static void close(){
		if(!isInit){
			return;
		}
		for(Process p : vp){
			p.destroy();
		}
	}
	
	public static class IAHandler extends Thread {
		private Process p;
		private String name;
		
		public IAHandler(Process p, String name){
			this.p = p;
			this.name = name;
		}
		
		@Override
		public void run(){
			InputStream stdout = p.getInputStream ();
			BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));
			String line;
			try {
				OutputStreamWriter fos = new OutputStreamWriter(new FileOutputStream("logs/debug_"+name+".json"), StandardCharsets.UTF_8);
				while ((line = reader.readLine ()) != null) {
					fos.write(line+"\n");
				}
				fos.close();
			} catch (Exception e) {}
		}
	}

}
