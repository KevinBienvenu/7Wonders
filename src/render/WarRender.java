package render;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;

import Effects.EffectType;
import main.Game;
import main.Main;
import ressources.Data;
import ressources.Images;
import ressources.Sounds;
import ressources.TokenName;

public class WarRender {

	public static int cooldown;
	public static int idAge;
	public static Sound guerre;
	public static boolean isPlaying = false;
	public static int idPlayer = -1, idOther = 0;;
	public static int current_military, temp_military;
	public static boolean rendering = false;

	public static void init(int idAge){
		WarRender.idAge = idAge;
		cooldown = 0;
		Images.images.put("age"+idAge, Images.get("age"+idAge).getScaledCopy(3f));
		rendering = true;
		idPlayer = -1;
		idOther = 0;
	}

	public static boolean update(){
		if(idAge==0){
			return true;
		}
		if(isPlaying){
//			if(!guerre.playing()){
//				guerre.play();
//			}
			cooldown++;
			if(temp_military!=current_military && Sounds.isInit()){
				if(cooldown==60){
					Sounds.get("epee1").play();
				} else if(cooldown==80){
					Sounds.get("epee2").play();
				} else if(cooldown==100){
					Sounds.get("epee3").play();
				} else if(cooldown==1){
					Sounds.get("philippe"+(int)(Math.random()*4)).play();
				}
			}
			if(cooldown>200 || Main.quickGame){
				isPlaying = false;
				if(idAge>0){
					TokenName temp_token;
					switch(Game.gameSystem.currentAge){
					case AGEI:
						temp_token = TokenName.VictoryToken1;
						break;
					case AGEII:
						temp_token = TokenName.VictoryToken3;
						break;
					case AGEIII:
						temp_token = TokenName.VictoryToken5; 
						break;
					default:
						temp_token = TokenName.VictoryToken1;
						break;
					}
					if(temp_military<current_military){
						if(Game.gameSystem.board.players.get(idOther).specialEffects.contains(EffectType.SendBackDefeat)){
							Game.gameSystem.board.players.get(idPlayer).addToken(TokenName.DefeatToken1, 1);
						} else {
							Game.gameSystem.board.players.get(idOther).addToken(TokenName.DefeatToken1, 1);
						}
						Game.gameSystem.board.players.get(idPlayer).addToken(temp_token, 1);
						if(Game.gameSystem.board.players.get(idPlayer).specialEffects.contains(EffectType.Coins2MilitaryVictory)){
							Game.gameSystem.board.players.get(idPlayer).coins+=2;
						}
					} else if(temp_military>current_military){
						if(Game.gameSystem.board.players.get(idPlayer).specialEffects.contains(EffectType.SendBackDefeat)){
							Game.gameSystem.board.players.get(idOther).addToken(TokenName.DefeatToken1, 1);
						} else {
							Game.gameSystem.board.players.get(idPlayer).addToken(TokenName.DefeatToken1, 1);
						}
						Game.gameSystem.board.players.get(idOther).addToken(temp_token, 1);
						if(Game.gameSystem.board.players.get(idOther).specialEffects.contains(EffectType.Coins2MilitaryVictory)){
							Game.gameSystem.board.players.get(idOther).coins+=2;
						}
					}
				}
			}
		} else {
			idPlayer += 1;
			if(idPlayer==Game.gameSystem.board.players.size()){
				rendering = false;
				return true;
			}
			idOther += 1;
			if(idOther==Game.gameSystem.board.players.size()){
				idOther = 0;
			}
			current_military = Game.gameSystem.board.players.get(idPlayer).tokens.get(TokenName.Military);
			temp_military = Game.gameSystem.board.players.get(idOther).tokens.get(TokenName.Military);
			cooldown = 0;
			isPlaying = true;
			if(temp_military==current_military && Sounds.isInit()){
				Sounds.get("ooh").play();
			} 
		}
		return false;
	}

	public static void render(Graphics g){
		if(rendering){
			g.setColor(new Color(1.0f,0f,0f,0.3f));
			g.fillRect(0, 0, Game.resX, Game.resY);
			if(isPlaying){
				// idplayer
				g.setColor(Color.black);
				g.fillRect(Game.resX/5, Game.resY*2/5, Game.resX/5, Game.resY/5);
				String s = Game.gameSystem.board.players.get(idPlayer).nickName;
				Data.font_big.drawString(Game.resX*3/10-Data.font_big.getWidth(s)/2, Game.resY*2/5+15, s);
				s = ""+current_military;
				Data.font_big.drawString(Game.resX*3/10-Data.font_big.getWidth(s), Game.resY*2/5+100, s);
				// idother
				g.setColor(Color.black);
				g.fillRect(3*Game.resX/5, Game.resY*2/5, Game.resX/5, Game.resY/5);
				s = Game.gameSystem.board.players.get(idOther).nickName;
				Data.font_big.drawString(Game.resX*7/10-Data.font_big.getWidth(s)/2, Game.resY*2/5+15, s);
				s = ""+temp_military;
				Data.font_big.drawString(Game.resX*7/10-Data.font_big.getWidth(s)-20, Game.resY*2/5+100, s);
				//shields
				Image imLeft, imRight, im = Images.get("military");
				imLeft = im.getScaledCopy(0.5f);
				imRight = im.getScaledCopy(0.5f);
				if(current_military<temp_military){
					imLeft = im.getScaledCopy(Math.min(0.5f,  (200f-cooldown)/100f));
				}
				if(current_military>temp_military){
					imRight = im.getScaledCopy(Math.min(0.5f,  (200f-cooldown)/100f));
				}
				g.drawImage(imRight, Game.resX*3.5f/10-imRight.getWidth()/2, Game.resY/2-imRight.getHeight()/2);
				g.drawImage(imLeft, Game.resX*6.5f/10-imLeft.getWidth()/2, Game.resY/2-imLeft.getHeight()/2);
			}
		}
	}
}
