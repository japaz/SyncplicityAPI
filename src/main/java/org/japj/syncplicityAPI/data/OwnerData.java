package org.japj.syncplicityAPI.data;

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
	public String getFirstName() {
		return FirstName;
	}
	public String getLastName() {
		return LastName;
	}
}
