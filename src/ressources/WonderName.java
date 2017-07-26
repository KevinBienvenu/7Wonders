package ressources;

import org.newdawn.slick.Color;

public enum WonderName {
	
	ephese(new Color(185,142,27),new Color(252,198,51)),
	alexandrie(new Color(95,95,95),new Color(176,176,176)),
	olympie(new Color(5,133,156),new Color(69,216,243)),
	halicarnasse(new Color(94,39,112),new Color(167,29,212)),
	rhodes(new Color(165,9,9),new Color(255,55,55)),
	gizeh(new Color(140,72,5),new Color(199,118,39)),
	babylone(new Color(0,88,2),new Color(41,156,44)),
	rome(new Color(150,150,160), new Color(230,230,240)),
	petra(new Color(190,120,0), new Color(255,144,0));
	
	public Color dark;
	public Color light;
	
	private WonderName(Color dark, Color light){
		this.dark = dark;
		this.light = light;
	}

}
