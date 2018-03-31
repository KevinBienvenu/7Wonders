package enums;

public enum ActionNames {

	BUILDING("Building"),
	FREE_BUILDING("FreeBuilding"),
	BURY_LEADER("BuryLeader"),
	BUY_RESSOURCE("BuyRessource"),
	GOLD_FROM_BANK("GoldFromBank"),
	DISCARD("Discard"),
	LEADER_CHOICE("LeaderChoice"),
	WONDER_FACE_CHOICE("WonderFaceChoice"),
	WONDER("Wonder");
	
	public final String state;
	private ActionNames(String state){
		this.state = state;
	}
}
