package org.japj;

public class AuthenticationData {

	String Id; // Session token used to authenticate future requests.
	String Duration; // Duration, in seconds, the token is valid for.
	UserData User;
	MachineData Machine;
	
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
	public MachineData getMachine() {
		return Machine;
	}
	public void setMachine(MachineData machine) {
		Machine = machine;
	}
}
