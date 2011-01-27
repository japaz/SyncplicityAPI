package org.japj;

public class AuthenticationData {

	private String Id;
	private String Duration;
	private UserData User;
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getDuration() {
		return Duration;
	}
	public void setDuration(String duration) {
		Duration = duration;
	}
	public UserData getUser() {
		return User;
	}
	public void setUser(UserData user) {
		User = user;
	}
}
