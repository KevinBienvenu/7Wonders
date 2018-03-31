package render;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;

import effects.EffectType;
import enums.TokenName;
import main.Game;
import main.Main;
import model.Building;
import model.Card;
import ressources.Data;
import ressources.Fonts;
import ressources.Images;
import ressources.Sounds;

public class WarRender {

	public static int cooldown;
	public static int idAge;
	public static Sound guerre;
	public static boolean isPlaying = false;
	public static int idPlayer = -1, idOther = 0;;
	public static int current_military, temp_military;
	public static int cooldownTotal = 200;
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
			if(cooldown>cooldownTotal || Main.quickGame){
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
				Fonts.font_big.drawString(Game.resX*3/10-Fonts.font_big.getWidth(s)/2, Game.resY*2/5+15, s);
				s = ""+current_military;
				Fonts.font_big.drawString(Game.resX*3/10-Fonts.font_big.getWidth(s), Game.resY*2/5+100, s);
				// idother
				g.setColor(Color.black);
				g.fillRect(3*Game.resX/5, Game.resY*2/5, Game.resX/5, Game.resY/5);
				s = Game.gameSystem.board.players.get(idOther).nickName;
				Fonts.font_big.drawString(Game.resX*7/10-Fonts.font_big.getWidth(s)/2, Game.resY*2/5+15, s);
				s = ""+temp_military;
				Fonts.font_big.drawString(Game.resX*7/10-Fonts.font_big.getWidth(s)-20, Game.resY*2/5+100, s);
				//shields
				Image imLeft, imRight, im = Images.get("military");
				Image tokenLeft, tokenRight, tokenVictory, tokenDefeat;
				float xLeftStart, xLeftEnd, xLeft;
				float yLeftStart, yLeftEnd, yLeft;
				float xRightStart, xRightEnd, xRight;
				float yRightStart, yRightEnd, yRight;
				xLeftStart = Game.resX*6.5f/10;
				yLeftStart = Game.resY*3/5;
				xRightStart = Game.resX*3.5f/10;
				yRightStart = Game.resY*3/5;
				xLeftEnd = PlayerRender.getRenderDimensionForPlayer(idPlayer).get(0)+PlayerRender.getRenderDimensionForPlayer(idPlayer).get(2)/2;
				yLeftEnd = PlayerRender.getRenderDimensionForPlayer(idPlayer).get(1)+PlayerRender.getRenderDimensionForPlayer(idPlayer).get(3)/2;
				xRightEnd = PlayerRender.getRenderDimensionForPlayer(idOther).get(0)+PlayerRender.getRenderDimensionForPlayer(idOther).get(2)/2;
				yRightEnd = PlayerRender.getRenderDimensionForPlayer(idOther).get(1)+PlayerRender.getRenderDimensionForPlayer(idOther).get(3)/2;
				int cooldownTemp = Math.min(cooldown, cooldownTotal/2);
				switch(Game.gameSystem.currentAge){
				case AGEI:
					tokenVictory = Images.get("victorytoken1").getScaledCopy(1.8f);
					break;
				case AGEII:
					tokenVictory = Images.get("victorytoken3").getScaledCopy(1.8f);
					break;
				case AGEIII:
					tokenVictory = Images.get("victorytoken5").getScaledCopy(1.8f);
					break;
				default:
					tokenVictory = Images.get("victorytoken1").getScaledCopy(1.8f);
					break;
				}
				tokenDefeat = Images.get("defeattoken1").getScaledCopy(1.8f);
				imLeft = im.getScaledCopy(0.5f);
				imRight = im.getScaledCopy(0.5f);
				tokenLeft = null;
				tokenRight = null;
				if(current_military<temp_military){
					imLeft = im.getScaledCopy(Math.min(0.5f,  (200f-cooldown)/100f));
					tokenLeft = tokenDefeat;
					tokenRight = tokenVictory;
					if(Game.gameSystem.board.players.get(idPlayer).specialEffects.contains(EffectType.SendBackDefeat)){
						xLeftEnd = xRightEnd+50;
						yLeftEnd = yRightEnd+50;
						Game.gameSystem.board.players.get(idPlayer).leaderToShow = Data.getBuildingByName("tomyris");
					}
					if(Game.gameSystem.board.players.get(idOther).specialEffects.contains(EffectType.Coins2MilitaryVictory)){
						Game.gameSystem.board.players.get(idOther).leaderToShow = Data.getBuildingByName("neron");
					}
				}
				if(current_military>temp_military){
					imRight = im.getScaledCopy(Math.min(0.5f,  (200f-cooldown)/100f));
					tokenLeft = tokenVictory;
					tokenRight = tokenDefeat;
					if(Game.gameSystem.board.players.get(idOther).specialEffects.contains(EffectType.SendBackDefeat)){
						xRightEnd = xLeftEnd+50;
						yRightEnd = yLeftEnd+50;
					}
					if(Game.gameSystem.board.players.get(idPlayer).specialEffects.contains(EffectType.Coins2MilitaryVictory)){
						Game.gameSystem.board.players.get(idPlayer).leaderToShow = Data.getBuildingByName("neron");
					}
				}
				xLeft = 2.0f*(xLeftStart*(cooldownTotal/2-cooldownTemp) + xLeftEnd*cooldownTemp)/cooldownTotal;
				yLeft = 2.0f*(yLeftStart*(cooldownTotal/2-cooldownTemp) + yLeftEnd*cooldownTemp)/cooldownTotal;
				xRight = 2.0f*(xRightStart*(cooldownTotal/2-cooldownTemp) + xRightEnd*cooldownTemp)/cooldownTotal;
				yRight = 2.0f*(yRightStart*(cooldownTotal/2-cooldownTemp) + yRightEnd*cooldownTemp)/cooldownTotal;
				g.drawImage(imLeft, Game.resX*3.5f/10-imLeft.getWidth()/2, Game.resY/2-imLeft.getHeight()/2+50);
				g.drawImage(imRight, Game.resX*6.5f/10-imRight.getWidth()/2, Game.resY/2-imRight.getHeight()/2+50);
				if(tokenLeft!=null){
					g.drawImage(tokenLeft, xLeft-tokenLeft.getWidth()/2, yLeft-tokenLeft.getHeight()/2);
					g.drawImage(tokenRight, xRight-tokenRight.getWidth()/2, yRight-tokenRight.getHeight()/2);
				}
			}
		}
	}
}
