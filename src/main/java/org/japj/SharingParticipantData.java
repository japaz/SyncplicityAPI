package org.japj;

public class SharingParticipantData {
	
	String EmailAddress;
	String Permission;
	
	// For Add Sharing Participants In Bulk
	Long SyncPointId;
	UserData Inviter;
	UserData User;
	
	public String getEmailAddress() {
		return EmailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		EmailAddress = emailAddress;
	}
	public String getPermission() {
		return Permission;
	}
	public void setPermission(String permission) {
		Permission = permission;
	}
	
	

}
