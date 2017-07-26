package gameSystem;

import java.util.HashMap;
import java.util.Vector;


import Effects.EffectType;
import ressources.RessourceName;

public class Wonder {

	public String name;
	public String idname;
	public HashMap<String, EffectType> effectTypes = new HashMap<String, EffectType>();
	public HashMap<String, Vector<Floor>> floors = new HashMap<String, Vector<Floor>>();
	public boolean canBuildInAnyOrder = false;
	public String atext = "", btext = "";


	public Wonder(HashMap<String, String> hashmap, String idname){
		this.idname = idname;
		String[] faces = new String[]{"A","B"};
		if(hashmap.containsKey("Name")){
			this.name = hashmap.get("Name");
		} else {
			System.out.println("Probleme pas de name");
		}
		for(String face : faces){
			if(hashmap.containsKey(face+"EffectType")){
				effectTypes.put(face, EffectType.valueOf(hashmap.get(face+"EffectType")));
			} else {
				System.out.println("Probl�me : "+name+" - pas d'effet face "+face);
				effectTypes.put(face, EffectType.None);
			}
			this.floors.put(face, new Vector<Floor>());
		}
		if(hashmap.containsKey("AText")){
			this.atext = hashmap.get("AText");
		}
		if(hashmap.containsKey("BText")){
			this.btext = hashmap.get("BText");
		}
		HashMap<RessourceName,Integer> ressourceCost = new HashMap<RessourceName,Integer>();
		Vector<EffectType> effects = new Vector<EffectType>();
		int coincost = 0;
		for(String face : faces){
			int nFloor = 1;
			int nEffect = 1;
			while(true){
				if(!hashmap.containsKey(face+"Floor"+nFloor+"EffectType0")){
					break;
				}
				effects = new Vector<EffectType>();
				ressourceCost = new HashMap<RessourceName,Integer>();
				for(RessourceName res : RessourceName.values()){
					if(hashmap.containsKey(face+"Floor"+nFloor+res.name()+"Cost")){
						ressourceCost.put(res, Integer.valueOf(hashmap.get(face+"Floor"+nFloor+res.name()+"Cost")));
					}
				}
				if(hashmap.containsKey(face+"Floor"+nFloor+"GoldCost")){
					coincost = Integer.valueOf(hashmap.get(face+"Floor"+nFloor+"GoldCost"));
				} else {
					coincost = 0;
				}
				nEffect = 0;
				while(true){
					if(!hashmap.containsKey(face+"Floor"+nFloor+"EffectType"+nEffect)){
						break;
					} else {
						effects.add(EffectType.valueOf(hashmap.get(face+"Floor"+nFloor+"EffectType"+nEffect)));
					}
					nEffect+=1;
				}
				this.floors.get(face).add(new Floor(ressourceCost,effects,coincost));
				nFloor+=1;
			}
		}
	}


	public String getJsonForm(){
		String s = "";
		s = "{";
		s += "\"idname\":\""+this.idname+"\",";
		s += "\"name\":\""+this.name+"\",";
		s += "\"AText\":\""+this.atext+"\",";
		s += "\"BText\":\""+this.btext+"\",";
		s += "\"floors\":{";
		for(String face : this.floors.keySet()){
			s+="\""+face+"\":[";
			for(Floor f : this.floors.get(face)){
				s += "{\"gold\":"+f.coincost+",";
				for(RessourceName rn : f.ressourceCost.keySet()){
					s += "\""+rn.name().toLowerCase()+"\":"+f.ressourceCost.get(rn)+",";
				}
				s = s.substring(0, s.length()-1);
				s += "},";
			}
			s = s.substring(0, s.length()-1);
			s += "],";
		}
		s = s.substring(0, s.length()-1);
		s += "}}";
		System.out.println(s);
		return s;
	}

	public class Floor {
		public HashMap<RessourceName,Integer> ressourceCost;
		public Vector<EffectType> effects;
		public int coincost = 0;

		private Floor(HashMap<RessourceName,Integer> ressourceCost, Vector<EffectType> effects, int coincost){
			this.ressourceCost = ressourceCost;
			this.effects = effects;
			this.coincost = coincost;
		}

	}


}
