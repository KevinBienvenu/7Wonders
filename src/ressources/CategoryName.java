package ressources;

import org.newdawn.slick.Color;

public enum CategoryName {
	
	Commercial(Color.yellow, true),
	Scientific(new Color(0.0f, 0.6f, 0.0f), true),
	Military(Color.red, true),
	Civilian(Color.blue, true),
	Guilds(new Color(0.5f, 0f, 0.5f), true),
	CommonRessources(new Color(0.6f, 0.4f, 0.1f), false),
	RareRessources(Color.lightGray, false), 
	Coins(Color.white, true),
	Leader(Color.white, true),
	Wonder(Color.orange, true);
	
	private CategoryName(Color color, boolean score){
		this.color = color;
		this.score = score;
	}
	
	public Color color;
	public boolean score;

}
