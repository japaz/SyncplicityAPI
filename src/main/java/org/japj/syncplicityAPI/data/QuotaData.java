package org.japj.syncplicityAPI.data;

public class QuotaData {
	Long ActiveBytes; // Number of bytes used.
	Long AvailableBytes; // Total size of the account (used and unused) in bytes.
	
	public Long getActiveBytes() {
		return ActiveBytes;
	}
	public Long getAvailableBytes() {
		return AvailableBytes;
	}
}
