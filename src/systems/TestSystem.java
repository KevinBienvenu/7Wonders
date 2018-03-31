package systems;

import java.util.HashMap;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import effects.PointsHandling;
import enums.TokenName;

public class TestSystem extends ClassSystem {
	
	public TestSystem(){
		
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		
	}

	@Override
	public void update(GameContainer arg0, int arg1) throws SlickException {
//		for(int i=3; i<8; i++){
//			Vector<Vector<Card>> deck = Game.gameSystem.handleDeckCreation(i, 3);
//			int j = 1;
//			for(Vector<Card> d : deck){
//				System.out.println("joueur "+j+++" - "+d.size());
////				for(Card c: d){
////					System.out.println("   "+c.building.name);
////				}
//			}
//			System.out.println("\n\n");
//		}
		HashMap<TokenName, Integer> tokens = new HashMap<TokenName, Integer>();
		tokens.put(TokenName.Compas, 5);
		tokens.put(TokenName.Engrenage, 1);
		tokens.put(TokenName.Tablette, 1);
		System.out.println(PointsHandling.pointsScience(tokens, 2, false));
		System.exit(0);
	}

}
