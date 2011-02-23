package org.japj;

public class LinkData {
	Long SyncPointId; // The ID of the SyncPoint the file is located in.
	String VirtualPath; // The virtual path to the file inside the SyncPoint.
	String Token; // The unique, randomly generated token for this particular shareable link. Can be used to delete the shareable link later on.
	String LandingPageUrl; // A URL to the page the file can be downloaded from.
	
	public Long getSyncPointId() {
		return SyncPointId;
	}
	public String getVirtualPath() {
		return VirtualPath;
	}
	public String getToken() {
		return Token;
	}
	public String getLandingPageUrl() {
		return LandingPageUrl;
	}
}
