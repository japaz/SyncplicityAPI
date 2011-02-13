package org.japj;

public class QuotaData {
	Long ActiveBytes; // Number of bytes used.
	Long AvailableBytes; // Total size of the account (used and unused) in bytes.
	
	public Long getActiveBytes() {
		return ActiveBytes;
	}
	public void setActiveBytes(Long activeBytes) {
		ActiveBytes = activeBytes;
	}
	public Long getAvailableBytes() {
		return AvailableBytes;
	}
	public void setAvailableBytes(Long availableBytes) {
		AvailableBytes = availableBytes;
	}
}
