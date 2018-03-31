package states;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import effects.EffectType;
import enums.CategoryName;
import enums.StateNames;
import enums.TokenName;
import main.Game;
import model.Card;
import model.Player;

public abstract class State {
	
	public StateNames id;
	public Vector<Card> cards = new Vector<Card>();
	public Vector<Card> leaders = new Vector<Card>();
	public Player player;
	public int key;
	
	public State(StateNames id, Player player) {
		this.id = id;
		this.player = player;
		this.key = (int) (Math.random()*Integer.MAX_VALUE);
	}
	
	
	public JsonObject getBody() {
		JsonObject body = new JsonObject();
		body.addProperty("idJoueur",""+this.player.id);
		body.add("cards", new JsonArray());
		this.cards.stream().forEach(card -> {
			body.getAsJsonArray("cards").add(new JsonPrimitive(card.building.name));
		});
		body.add("leaders", new JsonArray());
		this.player.leaderToChoose.stream().forEach(card -> {
			body.getAsJsonArray("leaders").add(new JsonPrimitive(card.building.name));
		});
		body.add("cardoptions", new JsonObject());
		body.addProperty("wonderIdName", this.player.wonderName.toString());
		body.addProperty("wonderFace", this.player.wonderFace!=null ? this.player.wonderFace.toString() : "");
		body.add("wonderFloors", new JsonArray());
		body.add("ressources", new JsonObject());
		body.add("tradebuildings", new JsonArray());
		body.add("ressourcesleft", new JsonObject());
		body.add("ressourcesright", new JsonObject());
		body.add("specialeffects", new JsonArray());
		body.addProperty("coins", player.coins);
		body.addProperty("key", key);
		body.addProperty("username", player.nickName);
		body.addProperty("state", id.state);
		body.addProperty("time", Game.gameSystem.t);
		handleAdditionalInfos(player, body);
		handleBody(body);
		return body;
	}
	
	public void handleBody(JsonObject body){}
	
	
	public static JsonArray handleEffectTypes(Vector<EffectType> vector){
		JsonArray s = new JsonArray();
		vector.stream()
			.forEach(effect -> {
				s.add(new JsonPrimitive(effect.name()));
			});
		
		return s;
	}
	
	public static JsonObject handleRessources(Vector<EffectType> ressources, boolean complete){
		JsonObject dic = new JsonObject();
		String toAdd;
		int nbToAdd = 1;
		int iChar;
		for(EffectType r : ressources){
			nbToAdd = 1;
			toAdd = r.name();
			iChar = 1;
			switch(r){
			case ProductionRare:
				if(!complete)
					continue;
				toAdd = "tissu-verre-papyrus";
				break;
			case ProductionCommon:
				if(!complete)
					continue;
				toAdd = "minerai-argile-bois-pierre";
				break;
			case ProductionArgileDouble:
			case ProductionBoisDouble:
			case ProductionMineraiDouble:
			case ProductionPierreDouble:
				nbToAdd += 1;
			case ProductionArgileSimple:
			case ProductionBoisSimple:
			case ProductionMineraiSimple:
			case ProductionPierreSimple:
				toAdd = toAdd.substring(0, toAdd.length()-6);
			case ProductionPapyrus:
			case ProductionTissu:
			case ProductionVerre:
				toAdd = toAdd.substring(10).toLowerCase();
				break;
			case ProductionArgileMinerai:
			case ProductionBoisArgile:
			case ProductionBoisMinerai:
			case ProductionMineraiPierre:
			case ProductionPierreArgile:
			case ProductionPierreBois:
				toAdd = toAdd.substring(10);
				while((int)toAdd.charAt(iChar)>96){
					iChar+=1;
				}
				toAdd = (toAdd.substring(0,iChar)+"-"+toAdd.substring(iChar)).toLowerCase();
				break;
			default:
				if(!complete){
					continue;
				}
				break;
			}
			dic.addProperty(toAdd, (dic.get(toAdd)!=null? dic.get(toAdd).getAsInt() : 0) + nbToAdd);
		}
		return dic;
	}
	
	public static JsonObject handleCardNumber(Player p){
		JsonObject s = new JsonObject();
		Arrays.stream(CategoryName.values())
		.forEach(category -> {
			s.addProperty(category.name(), p.buildings.stream()
				.filter(building -> building.categoryName == category)
				.collect(Collectors.counting()));
		});
		return s;
	}
	
	public static JsonObject handleTokenNumber(Player p){
		JsonObject s = new JsonObject();
		Arrays.stream(TokenName.values())
			.filter(token -> p.tokens.get(token)>=0)
			.forEach(token -> {
				s.addProperty(token.name(), p.tokens.get(token));
			});
		return s;
	}
	
	public static void handleAdditionalInfos(Player p, JsonObject body){
		body.add("cardNumber",handleCardNumber(p));
		body.add("tokenNumber",handleTokenNumber(p));
		body.add("cardNumberLeft",handleCardNumber(p.getLeftNeighbour()));
		body.add("cardNumberRight",handleCardNumber(p.getRightNeighbour()));
		body.add("tokenNumberLeft",handleTokenNumber(p.getLeftNeighbour()));
		body.add("tokenNumberRight",handleTokenNumber(p.getRightNeighbour()));
	}

	
}
