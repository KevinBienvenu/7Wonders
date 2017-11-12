package gameSystem;

import java.util.HashMap;
import java.util.Vector;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import Effects.EffectType;
import ressources.CategoryName;
import ressources.RessourceName;

public class Building {

	public String name;
	public String idname;
	public int coinCost;
	public HashMap<RessourceName,Integer> ressourcesCost;
	public Vector<EffectType> effects;
	public int age;
	public CategoryName categoryName;
	public String[] chainDown;
	public String[] chainUps;
	public Vector<Integer> numberPlayerRequired;
	public String description;


	public Building(HashMap<String, String> hashmap, String key){
		this.idname = key;
		String s;
		this.ressourcesCost = new HashMap<RessourceName, Integer>();
		this.effects = new Vector<EffectType>();
		this.numberPlayerRequired = new Vector<Integer>();
		for(String element : hashmap.keySet()){
			s = hashmap.get(element);
			switch(element){
			case "Name" : this.name = s; break;
			case "CoinCost" : this.coinCost = Integer.parseInt(s); break;
			case "ArgileCost" : 
			case "PierreCost" :
			case "BoisCost" :
			case "MineraiCost" :
			case "VerreCost" :
			case "TissuCost" :
			case "PapyrusCost" :
				if(Integer.parseInt(s)>0){
					this.ressourcesCost.put(RessourceName.valueOf(element.substring(0, element.length()-4)), Integer.parseInt(s));
				}
				break;
			case "Effects" : 
				for(String s1 : s.split(",")){
					this.effects.add(EffectType.valueOf(s1));
				}
				break;
			case "NumberPlayerRequired" : 
				for(String s1 : s.split(",")){
					this.numberPlayerRequired.add(Integer.parseInt(s1));
				}
				break;
			case "Age": this.age = Integer.parseInt(s); break;
			case "CategoryName" : this.categoryName = CategoryName.valueOf(s);break;
			case "ChainDown" : this.chainDown = s.split(","); break;
			case "ChainUps" : this.chainUps = s.split(","); break;
			case "Description" : this.description = s;break;
			}
		}
	}

	public void renderOnBoard(Graphics g, int x, int y, int sizeX, int sizeY){
		g.setColor(this.categoryName.color);
		g.fillRect(x, y, sizeX, sizeY);
		g.setColor(Color.white);
		g.drawString(this.name, x+2, y+2);
		for(int i = 0; i<this.effects.size(); i++){
			this.effects.get(i).render(g, (int)(x+2f*sizeX/3f), (int)(y+sizeY/2f), (int)(2f*sizeY/3f));
		}
	}

	public String getJsonForm(){
		String s = "";
		s = "{";
		s += "\"name\":\""+this.name+"\",";
		s += "\"description\":\""+this.description+"\",";
		s += "\"coinCost\":"+this.coinCost+",";
		s += "\"ressourcesCost\":{";
		for(RessourceName rn : this.ressourcesCost.keySet()){
			s += "\""+rn.name().toLowerCase()+"\":"+this.ressourcesCost.get(rn)+",";
		}
		if(this.ressourcesCost.size()>0){
			s = s.substring(0, s.length()-1);
		}
		s += "}}";
		return s;
	}



}
