package org.japj;

public class OwnerData {
	String EmailAddress;
	String FirstName;
	String LastName;
	
	private OwnerData() {
		
	}
	
	public OwnerData(String emailAddress) {
		this.EmailAddress = emailAddress;
	}
	
	public String getEmailAddress() {
		return EmailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		EmailAddress = emailAddress;
	}
	public String getFirstName() {
		return FirstName;
	}
	public void setFirstName(String firstName) {
		FirstName = firstName;
	}
	public String getLastName() {
		return LastName;
	}
	public void setLastName(String lastName) {
		LastName = lastName;
	}
	
	
}
