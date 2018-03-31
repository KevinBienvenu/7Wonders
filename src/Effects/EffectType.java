package effects;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import enums.CategoryName;
import main.Game;
import model.Building;
import ressources.Images;

public enum EffectType {

	None(true),

	// Ressources
	ProductionArgileSimple(true, Color.orange),
	ProductionMineraiSimple(true, Color.gray),
	ProductionPierreSimple(true, Color.darkGray),
	ProductionBoisSimple(true, Color.green),
	ProductionArgileDouble(true, Color.orange),
	ProductionMineraiDouble(true, Color.gray),
	ProductionPierreDouble(true, Color.darkGray),
	ProductionBoisDouble(true, Color.green),

	ProductionBoisArgile(false),
	ProductionPierreArgile(false),
	ProductionArgileMinerai(false),
	ProductionPierreBois(false),
	ProductionBoisMinerai(false),
	ProductionMineraiPierre(false),

	ProductionTissu(true, Color.pink),
	ProductionVerre(true, Color.blue),
	ProductionPapyrus(true, Color.lightGray),
	

	// Guildes
	VictoryPointsForCommonNeighbours(false),
	VictoryPointsForRareNeighbours(false),
	VictoryPointsForCommercialNeighbours(false),
	VictoryPointsForScientificNeighbours(false),
	VictoryPointsForMilitaryNeighbours(false),
	VictoryPointsForCivilianNeighbours(false),
	VictoryPointsForGuildNeighbours(false),
	VictoryPointsForLeaderNeighbours(false),
	VictoryPointsForDefeatedNeighbours(false),
	VictoryPointsForRareCommonGuildSet(false),
	VictoryPointsForWonderFloors(false),
	
	// Leaders
	VictoryPointsForScientific(false),
	VictoryPointsForCommercial(false),
	VictoryPointsForCivilian(false),
	VictoryPointsForMilitary(false),
	VictoryPointsForCommonRessources(false),
	VictoryPointsForRareRessources(false),
	VictoryPointsForGuilds(false),
	Coins2Chainage(true),
	Coins2Commercial(true),
	Coins2MilitaryVictory(true),
	Coins1Commerce(true),
	SendBackDefeat(true),
	PointsPlaton(true),
	PointsJustinien(true),
	PointsAristote(true),
	PointsForMilitaryVictory(true),
	PointsMidas(true),
	PointsAmytis(true),
	SpecialLeaderFree(true),
	SpecialGuildsFree(true),
	CheaperScientific(true),
	CheaperMilitary(true),
	CheaperCivilian(true),
	CheaperWonder(true),
	SpecialBilkis(true),
	
	// Victory Points
	VictoryPoints1(true),
	VictoryPoints2(true),
	VictoryPoints3(true),
	VictoryPoints4(true),
	VictoryPoints5(true),
	VictoryPoints6(true),
	VictoryPoints7(true),
	VictoryPoints8(true),
	VictoryPoints9(true),
	VictoryPoints10(true),
	VictoryPoints14(true),

	// Commerce
	Coins3(true),
	Coins4(true),
	Coins5(true),
	Coins9(true),
	Coins6(true),
	TradeCommonEastEqual1(false),
	TradeCommonWestEqual1(false),
	TradeCommonBothEqual1(false),
	TradeRareBothEqual1(false),
	ProductionRare(false),
	ProductionCommon(false),
	CoinsForAllCommonProd(false),
	CoinsForAllRareProd(false),
	CoinsAndVictoryPointsForCommonProd(false),
	CoinsAndVictoryPointsForRareProd(false),
	CoinsAndVictoryPointsForCommercial(false),
	CoinsAndVictoryPointsForWonderFloors(false),

	// Military
	Military1(true),
	Military2(true),
	Military3(true),
	Military5(true),
	Military7(true),

	// Special Wonder
	SpecialPlayLastCard(false),
	SpecialBuildFreeBuilding(false),
	SpecialBuildFromDiscard(false),
	SpecialCopyGuild(false),
	SpecialDraw4Leaders(false),
	SpecialPlayLeader(false),
	SpecialBuryLeader(false),
	SpecialLeaderCheaper(true),
	SpecialLeaderCheaper1(true),
	SpecialLeaderCheaper2(true),

	// Science
	Compas(true, new Color(0,150,0)),
	Engrenage(true, new Color(0,150,0)),
	Tablette(true, new Color(0,150,0)),
	ProductionScience(false);

	private EffectType(boolean simple, Color color){
		this.simple = simple;
		this.color = color;
	}
	private EffectType(boolean simple){
		this.simple = simple;
	}

	public boolean simple;
	public Color color;

	public int render(Graphics g, int x, int y, int size){

		if(this.simple){
			if(this.color!=null){
				g.setColor(Color.white);
				g.fillOval(x-size/2f, y-size/2f, size, size);
				g.setColor(Color.black);
				g.drawOval(x-size/2f, y-size/2f, size, size);
				g.setColor(this.color);
				g.fillOval(x-size/2f+size/10f, y-size/2f+size/10f, size*8f/10f, size*8f/10f);
			}
			if(Images.get(this.name()) != null)
				g.drawImage(Images.get(this.name()).getScaledCopy(size*4/5, size*4/5), x-size/2f+size/10f, y-size/2f+size/10f);
		}
		return 1;
	}
	




}
