package render;

import java.util.HashMap;
import java.util.Vector;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import Effects.EffectType;
import gameSystem.Building;
import gameSystem.Player;
import inputActions.StateCardChoice;
import main.Game;
import main.Main;
import ressources.CategoryName;
import ressources.Data;
import ressources.Images;
import ressources.TokenName;
import ressources.WonderName;


public class PlayerRender {

	public static float ratioSizeXName = 0.2f;
	public static float ratioSizeXRessource = 0.08f;
	public static float ratioSizeYName = 3f/16f;
	
	public static float ratioStartXBuildings = 0.25f;
	public static float ratioStartYBuildings = 0.22f;
	public static float ratioSizeXBuildings = 0.1f;
	public static float ratioSizeYBuildings = 0.12f;
	public static float ratioSizeXScience = 0.06f;
	public static float ratioSizeYScience = 0.18f;
	
	
	public static float ratioSizeX = 2f/5f;
	public static float ratioSizeY = 0.25f;
	
	public static TypeRender typeRender = TypeRender.RenderCards;
	public static int remainingTime = 0;
	public static int totalTime = 7 * Main.framerate;
	public static int nbPeriodCosinus = 8;
	
	public enum TypeRender{
		RenderLeader,
		RenderScience,
		RenderCards
	}
	
	
	public static void render(Graphics g, Player p){
		Vector<Integer> dims = getRenderDimensionForPlayer(p.id);
		int x = dims.get(0), y = dims.get(1), sizeX = (int) (Game.resX*ratioSizeX), sizeY = (int) (Game.resY*ratioSizeY);
		// Drawing Background
		g.setColor(Color.black);
		g.fillRect(x, y, sizeX, sizeY);
		g.drawImage(Images.get(p.wonderName.name()+"_background"),x,y);
		// Drawing Name
		g.drawImage(Images.get(p.wonderName.name()+"_foreground"),x,y);
		
		Data.font_main.drawString(x+ratioSizeXName*sizeX/2-Data.font_main.getWidth(p.nickName.toUpperCase())/2,
				y+ratioSizeYName*sizeY/2-Data.font_main.getHeight(p.nickName.toUpperCase())/2, p.nickName.toUpperCase());
		g.setColor(Color.white);
		g.drawLine(x, y+ratioSizeYName*sizeY, x+ratioSizeXName*sizeX, y+ratioSizeYName*sizeY);
		g.drawLine(x+ratioSizeXName*sizeX, y, x+ratioSizeXName*sizeX, y+ratioSizeYName*sizeY);
		// Drawing Wonder Name
		String s = Data.wonders.get(p.wonderName).name.toUpperCase();
		Data.font_main.drawString(x+sizeX-2-Data.font_main.getWidth(s), y+2,s);
//		for(int i=0; i<this.buildings.size(); i++){
//			this.buildings.get(i).renderOnBoard(g, x+7, y+sizeY/7*(i+1), sizeX/4, sizeY/7);
//		}
		// Drawing Coins
		g.drawImage(Images.get(p.wonderName.name()+"_foreground"),
				x,y+sizeY-sizeY*ratioSizeYName);
		g.drawLine(x, y+sizeY-ratioSizeYName*sizeY, x+ratioSizeXName*sizeX, y+sizeY-ratioSizeYName*sizeY);
		g.drawLine(x+ratioSizeXName*sizeX, y+sizeY, x+ratioSizeXName*sizeX, y+sizeY-ratioSizeYName*sizeY);
		g.drawImage(Images.get("piececornerbasgauche"),
				x+sizeX*ratioSizeXName/2-(int)(ratioSizeYName*sizeY*0.8f)-4,
				y+sizeY-ratioSizeYName*sizeY/2-ratioSizeYName*sizeY*0.4f);
		Data.font_number.drawString(x+sizeX*ratioSizeXName/2+4, y+sizeY-ratioSizeYName*sizeY/2-Data.font_number.getHeight(""+p.coins)/2, ""+p.coins);
		// Drawing Ressources
		HashMap<String, Integer> dic = StateCardChoice.getDicRessources(p.ressources, false);
		int idPos = 0;
		float x1, y1, dec;
		float sizeImage = Math.min(sizeX*ratioSizeXRessource/2, sizeY*(1f-2f*ratioSizeYName)/4f);
		for(String s1 : new String[]{"bois","pierre","argile","minerai",
				"verre","papyrus","tissu",
				"bois-argile","pierre-argile","argile-minerai","pierre-bois","bois-minerai","minerai-pierre"}){
			if(dic.containsKey(s1)){
				x1 = 10+x+(idPos/4)*sizeX*ratioSizeXRessource;
				y1 = y+sizeY*ratioSizeYName+sizeY*(1f-2f*ratioSizeYName)/4f*(idPos%4);
				
				dec = 0f;
				for(String temp_string : s1.split("-")){
					g.drawImage(Images.get(temp_string+"panneaugauche"), 
							x1+dec, y1+sizeY*(1f-2f*ratioSizeYName)/8f-sizeImage/2);
					dec += 10f;
				}
				Data.font_number.drawString(x1+dec+sizeImage+2, y1+sizeY*(1f-2f*ratioSizeYName)/8f-Data.font_number.getHeight(""+dic.get(s1))/2, ""+dic.get(s1));
				
				idPos += 1;
			}
		}
		// Drawing Token
		idPos = 0;
		for(TokenName token : new TokenName[]{TokenName.VictoryToken1, TokenName.VictoryToken3, TokenName.VictoryToken5, TokenName.DefeatToken1}){
			if(p.tokens.get(token)>=0){
				x1 = x+sizeX*ratioSizeXName+1.3f*idPos*sizeX*(1f-ratioSizeXName*2.5f)/8f;
				y1 = y+sizeY-sizeY*(1f-2f*ratioSizeYName)/4f;
				g.drawImage(Images.get(token.name().toLowerCase()+"panneaubas"), x1+2, y1+sizeY*(1f-2f*ratioSizeYName)/8f-sizeImage/2f);
				Data.font_number.drawString(x1+sizeImage+4, y1+sizeY*(1f-2f*ratioSizeYName)/8f-Data.font_number.getHeight(""+p.tokens.get(token))/2f, ""+p.tokens.get(token));
				idPos+=1;
			}
		}
		// Drawing Science
		x1 = x+sizeX*(1-1.7f*ratioSizeXName);
		idPos = 0;
		Image im;
		for(TokenName token : new TokenName[]{TokenName.Compas, TokenName.Engrenage, TokenName.Tablette, TokenName.Military}){
			y1 = y+sizeY*ratioSizeYName+sizeY*(1f-2f*ratioSizeYName)/4f*(idPos);
			im = Images.get(token.name().toLowerCase()+"panneaubas").getScaledCopy(1.5f);
			g.drawImage(im, x1, y1);
			Data.font_number.drawString(x1+im.getWidth()+10, y1, ""+p.tokens.get(token));
			idPos+=1;
			if(idPos==3){
				idPos += 1;
			}
		}
		
		// Drawing Wonder Construction
		g.setColor(Color.black);
		g.fillRect(x+sizeX*(1-ratioSizeXName), y+ratioSizeYName*sizeY, sizeX*ratioSizeXName, (1f-ratioSizeYName)*sizeY);
		int nbFloor = Data.wonders.get(p.wonderName).floors.get(p.wonderFace).size();
		for(int i=0; i<nbFloor; i++){
			g.setColor(Color.white);
			g.drawLine(x+sizeX*(1-ratioSizeXName), y+ratioSizeYName*sizeY+(1f-ratioSizeYName)*sizeY*(i+1)/(nbFloor+1), 
					x+sizeX, y+ratioSizeYName*sizeY+(1f-ratioSizeYName)*sizeY*(i+1)/(nbFloor+1));
			g.setColor(Color.darkGray);
			g.drawLine(x+sizeX*(1-ratioSizeXName), y+ratioSizeYName*sizeY+(1f-ratioSizeYName)*sizeY*(i+1)/(nbFloor+1)-1, 
					x+sizeX, y+ratioSizeYName*sizeY+(1f-ratioSizeYName)*sizeY*(i+1)/(nbFloor+1)-1);
			g.drawLine(x+sizeX*(1-ratioSizeXName), y+ratioSizeYName*sizeY+(1f-ratioSizeYName)*sizeY*(i+1)/(nbFloor+1)+1, 
					x+sizeX, y+ratioSizeYName*sizeY+(1f-ratioSizeYName)*sizeY*(i+1)/(nbFloor+1)+1);
			g.drawImage(Images.get("floor"+(nbFloor-i)+""+nbFloor), 
					x+sizeX*(1-ratioSizeXName/2)-Images.get("floor"+(nbFloor-i)+""+nbFloor).getWidth()/2-p.offsetXWonder, 
					y+ratioSizeYName*sizeY+(1f-ratioSizeYName)*sizeY*(i+0.5f)/(nbFloor+1)+1-Images.get("floor"+(nbFloor-i)+""+nbFloor).getHeight()/2);
			
		}
		g.drawImage(Images.get(p.wonderName.name()+"_wonder"), 
				x+sizeX*(1-ratioSizeXName) + p.offsetXWonder, 
				y+ratioSizeYName*sizeY+(1f-ratioSizeYName)*sizeY*(nbFloor-p.wonderFloorBuilt.size())/(nbFloor+1)+1+p.offsetYWonder);
		g.setColor(Color.white);
		g.drawLine(x+sizeX*(1-ratioSizeXName)-2, y+ratioSizeYName*sizeY-2, x+sizeX, y+ratioSizeYName*sizeY-2);
		g.drawLine(x+sizeX*(1-ratioSizeXName)-2, y+ratioSizeYName*sizeY-2, x+sizeX*(1-ratioSizeXName)-2, y+sizeY);
		g.setColor(p.wonderName.light);
		g.drawLine(x+sizeX*(1-ratioSizeXName)-1, y+ratioSizeYName*sizeY-1, x+sizeX, y+ratioSizeYName*sizeY-1);
		g.drawLine(x+sizeX*(1-ratioSizeXName)-1, y+ratioSizeYName*sizeY-1, x+sizeX*(1-ratioSizeXName)-1, y+sizeY);
		g.setColor(Color.white);
		g.drawLine(x+sizeX*(1-ratioSizeXName), y+ratioSizeYName*sizeY, x+sizeX, y+ratioSizeYName*sizeY);
		g.drawLine(x+sizeX*(1-ratioSizeXName), y+ratioSizeYName*sizeY, x+sizeX*(1-ratioSizeXName), y+sizeY);
		
		switch(typeRender){
		case RenderCards:
		default:
			renderCards(g, p, dims);
			break;
//		case RenderLeader:
//			renderLeaders(g, p, dims);
//			break;
//		case RenderScience:
//			renderScience(g, p, dims);
//			break;
		}
		

		// Rendering utility leader
		if(p.leaderToShow != null){
			im = Images.get(p.leaderToShow.building.idname).getScaledCopy(0.7f);
			g.drawImage(im, x+sizeX*2/3-im.getWidth()/2, y+sizeY/2-im.getHeight()/2);
		}
//		Image im = Images.get(p.wonderName.name()+"_background").getSubImage((int)(ratioSizeXName*sizeX), (int)(ratioSizeYName*sizeY), (int)((1-2*ratioSizeXName)*sizeX)-1, (int)((1-2*ratioSizeYName)*sizeY)-1);
//		float alpha = 0f;
//		if(remainingTime<totalTime/nbPeriodCosinus || remainingTime>(nbPeriodCosinus-1)*totalTime/nbPeriodCosinus){
//			alpha = (float) (0.5f*Math.cos(6*Math.PI*remainingTime/totalTime)+0.5f);
//		}
//		im.setAlpha(alpha);
//		g.drawImage(im,(int)(x+ratioSizeXName*sizeX), (int)(y+ratioSizeYName*sizeY));
		
	}
	
	// Several render functions
	
	// Drawing Bâtiment
	public static void renderCards(Graphics g, Player p, Vector<Integer> dims){
		int idPos = 0;
		int temp_nb = 0;
		int x = dims.get(0), y = dims.get(1), sizeX = (int) (Game.resX*ratioSizeX), sizeY = (int) (Game.resY*ratioSizeY);
		float x1, y1;
		for(CategoryName cn : CategoryName.values()){
			temp_nb = 0;
			for(Building b : p.buildings){
				if(b.categoryName==cn){
					temp_nb += 1;
				}
			}
			if(temp_nb>0){
				x1 = x + sizeX*ratioStartXBuildings + (idPos/4)*sizeX*ratioSizeXBuildings;
				y1 = y + sizeY*ratioStartYBuildings + (idPos%4)*sizeY*ratioSizeYBuildings+2;
				g.setColor(Color.white);
				g.fillRoundRect(x1, y1, sizeY*ratioSizeYBuildings*2/3, sizeY*ratioSizeYBuildings-4, 3);
				g.setColor(cn.color);
				g.fillRoundRect(x1+2, y1+2, sizeY*ratioSizeYBuildings*2/3-4, sizeY*ratioSizeYBuildings-8, 3);
				idPos += 1;

				Data.font_number.drawString(x1+sizeX*ratioSizeXBuildings/2, y1, ""+temp_nb );
			}
		}
	}
	// Drawing leaders
	public static void renderLeaders(Graphics g, Player p, Vector<Integer> dims){
		int temp_nb = 0;
		int x = dims.get(0), y = dims.get(1), sizeX = (int) (Game.resX*ratioSizeX), sizeY = (int) (Game.resY*ratioSizeY);
		float x1, y1;
		Image image;
		for(Building building : p.buildings){
			if(building.categoryName == CategoryName.Leader){
				y1 = y + sizeY*ratioStartYBuildings;
				image = Images.get(building.idname).getScaledCopy(4f*sizeY*ratioSizeYBuildings/Images.get(building.idname).getHeight());
				x1 = x + sizeX*ratioStartXBuildings + 1.1f*image.getWidth()*temp_nb;
				g.drawImage(image, x1, y1);
				temp_nb += 1;
			}
		}
	}
	// Drawing Science
	public static void renderScience(Graphics g, Player p, Vector<Integer> dims){
		int x = dims.get(0), y = dims.get(1), sizeX = (int) (Game.resX*ratioSizeX), sizeY = (int) (Game.resY*ratioSizeY);
		float x1, y1;
		int temp_i = 0, temp_j = 0;
		Image im;
		for(String[] t : new String[][]{new String[]{"officine","atelier","scriptorium","","","euclide","guilde_des_scientifiques"},
			new String[]{"dispensaire","laboratoire","bibliotheque","ecole","","pythagore","merveille"},
			new String[]{"loge","observatoire","universite","academie","etude","ptolemee",""}}){
			temp_j = 0;
			for(String s : t){
				if(temp_i==1 && temp_j==3){
					x1 = x + sizeX*ratioSizeYName + (0.65f+temp_j)*sizeX*ratioSizeXScience*1.3f;
				} else {
					x1 = x + sizeX*ratioSizeXName + temp_j*sizeX*ratioSizeXScience*1.3f;
				}
				y1 = y + sizeY*ratioStartYBuildings + temp_i*sizeY*ratioSizeYScience*1.1f;
				if(!s.equals("")){
					g.setColor(Color.white);
					g.fillRoundRect(x1, y1, sizeX*ratioSizeXScience, sizeY*ratioSizeYScience, 3);
					im = null;
					g.setColor(Color.darkGray);
					if(s.equals("merveille")){
						if(p.wonderName==WonderName.babylone){
							for(Integer i : p.wonderFloorBuilt){
								if(Data.wonders.get(p.wonderName).floors.get(p.wonderFace).get(i).effects.contains(EffectType.ProductionScience)){
									g.setColor(CategoryName.Wonder.color);
								}
							}
						}
					} else {
						for(Building b : p.buildings){
							if(b.idname.equals(s)){
								g.setColor(b.categoryName.color);
								break;
							}
						}
						im = Images.get(Data.getBuildingByName(s).effects.get(0).name()+"panneaubas");
					} 
					g.fillRoundRect(x1+2, y1+2, sizeX*ratioSizeXScience-4, sizeY*ratioSizeYScience-4, 3);
					if(im!=null){
						g.drawImage(im, x1+sizeX*ratioSizeXScience/2-im.getWidth()/2, y1+sizeY*ratioSizeYScience/2-im.getHeight()/2);
					}
				}
				temp_j += 1;
			}
			temp_i += 1;
		}
		
	}
	
	public static void initTypeRender(){
		typeRender = TypeRender.RenderCards;
		remainingTime = totalTime/nbPeriodCosinus;
	}
	
	public static void updateTypeRender(){
		remainingTime += 1;
		if(remainingTime>totalTime){
			remainingTime = 0;
			boolean next = false;
			for(TypeRender tr : TypeRender.values()){
				if(next){
					typeRender = tr;
					next = false;
					break;
				}
				if(tr==typeRender){
					next = true;
				}
			}
			if(next){
				typeRender = TypeRender.values()[0];
			}
		}
	}
	
	public static void renderNull(Graphics g, int p){
		Vector<Integer> dims = getRenderDimensionForPlayer(p);
		int x = dims.get(0), y = dims.get(1), sizeX = (int) (Game.resX*ratioSizeX), sizeY = (int) (Game.resY*ratioSizeY);
		// Drawing Background
		g.setColor(Color.black);
		g.fillRect(x, y, sizeX, sizeY);
	}
	
	public static Vector<Integer> getRenderDimensionForPlayer(int i){
		Vector<Integer> v = new Vector<Integer>();
		// getting x
		if(i/4==0){
			v.add(0);
		} else {
			v.add(3*Game.resX/5);
		}
		// getting y
		if(i<4){			
			v.add(Game.resY*i/4);
		} else {
			v.add(Game.resY-Game.resY*(1+i-4)/4);
		}
		// getting sizeX
		v.addElement(2*Game.resX/5);
		// getting sizeY
		v.addElement(Game.resY/4);
		return v;
	}
}
