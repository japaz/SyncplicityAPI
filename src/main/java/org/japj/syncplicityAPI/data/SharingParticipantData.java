package org.japj.syncplicityAPI.data;

public class SharingParticipantData {
	
	public static final String PERMISSION_COLLABORATOR = "1";
	public static final String PERMISSION_READER = "3";
	
	String EmailAddress;
	String Permission;
	
	// For Add Sharing Participants In Bulk
	Long SyncPointId;
	UserData Inviter;
	UserData User;
	
	private SharingParticipantData() {
		
	}
	public SharingParticipantData(String emailAddress, String permission) {
		EmailAddress = emailAddress;
		Permission = permission;
	}
	public SharingParticipantData(Long syncPointId, UserData inviter, 
			String permission, UserData user) {
		SyncPointId = syncPointId;
		Inviter = inviter;
		Permission = permission;
		User = user;
		
	}
	public String getEmailAddress() {
		return EmailAddress;
	}
	public String getPermission() {
		return Permission;
	}
}
