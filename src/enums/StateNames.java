package enums;


public enum StateNames {
	CARD_CHOICE("CardChoice"),
	LEADER_CHOICE("LeaderChoice"),
	BURY_LEADER("BuryLeader"),
	PLAY_FROM_DISCARD("PlayFromDiscard"),
	WONDER_FACE_CHOICE("WonderFaceChoice");
	
	public String state;
	private StateNames(String state){
		this.state = state;
	}
}
