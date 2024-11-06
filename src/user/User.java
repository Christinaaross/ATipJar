package user;

public class User {
	private int id;
	private String name;
	//private String email;
	private static int idCount = 0;
	

	public User(int userID, String name) {
		this.id = idCount++;
		this.name = name;
		//this.email = email;
	
	}
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getId() {
		return id;
	}

}