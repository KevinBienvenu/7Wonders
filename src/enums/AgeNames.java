package enums;

public enum AgeNames {

	INIT(-1, ""),
	LEADERCHOICE(0, "LEADER CHOICE"),
	AGEI(1, "Age I"),
	AGEII(2, "Age II"),
	AGEIII(3, "Age III"),
	END(4, "");
	
	public int idAge;
	public String text;
	
	private AgeNames(int idAge, String text){
		this.idAge = idAge;
		this.text = text;
	}
}
