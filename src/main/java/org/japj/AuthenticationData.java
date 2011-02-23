package org.japj;

public class AuthenticationData {

	String Id; // Session token used to authenticate future requests.
	String Duration; // Duration, in seconds, the token is valid for.
	UserData User;
	MachineData Machine;
	
	public String getId() {
		return Id;
	}
	public String getDuration() {
		return Duration;
	}
	public UserData getUser() {
		return User;
	}
	public MachineData getMachine() {
		return Machine;
	}
}
