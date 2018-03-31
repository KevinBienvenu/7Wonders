package network;

public enum Routes {
	
	GAMESLIST("/gameslist"),
	CONNECTED_PLAYERS("/connectedPlayers"),
	USERSTATES("/userstates"),
	USERACTIONS("/useractions"),
	DATA("/data"),
	SCORES("/scores"), 
	USERSTATS("/userinfo/stats");
	
	public String route;
	private Routes(String route){
		this.route = route;
	}

}
