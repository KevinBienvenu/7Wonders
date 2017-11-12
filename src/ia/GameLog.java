package ia;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Vector;

import gameSystem.Player;
import main.Game;

public class GameLog {
	
	public static Vector<String> state = new Vector<String>();
	public static Vector<String> decisions = new Vector<String>();
	
	public static boolean logwritten = false;
	
	
	
	public static void computeFinalFiles() {
		if(logwritten)
			return;
		Date d = new Date();
		String fileNameDate = "" +d.getTime();
		int idBest=-1, maxScore=0;
		for(Player p : Game.gameSystem.board.players) {
			if(p.getScore()>maxScore) {
				idBest = p.id;
				maxScore = p.getScore();
			}
		}
		// Saving decisions
		boolean haswon;
		String dic = "[";
		int idPlayer;
		for(String s : decisions) {
			idPlayer = Integer.parseInt(s.split("idJoueur\":")[1].split(",")[0].substring(1, 2));
			haswon = idPlayer==idBest;
			dic += s.substring(0, s.length()-1)+",\"winning\":"+haswon+",\"score\":"+Game.gameSystem.board.players.get(idPlayer).getScore()+"},";
		}
		dic = dic.substring(0, dic.length()-1)+"]";
		try {
			OutputStreamWriter fos = new OutputStreamWriter(new FileOutputStream("logs/testDecision_"+fileNameDate+".json"), StandardCharsets.UTF_8);
			fos.write(dic);
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Saving States
		try {
			OutputStreamWriter fos = new OutputStreamWriter(new FileOutputStream("logs/testState_"+fileNameDate+".json"), StandardCharsets.UTF_8);
			fos.write("[");
			for(int i=0; i<state.size()-1; i++) {
				fos.write(state.get(i)+",");
			}
			fos.write(state.lastElement()+"]");
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Process p = Runtime.getRuntime().exec("python src/ia/IA.py learn "+fileNameDate);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logwritten = true;
	}
	
}
