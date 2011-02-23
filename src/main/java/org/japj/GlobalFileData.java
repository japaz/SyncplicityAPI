package org.japj;

public class GlobalFileData {
	String Hash;
    Long Length;
    Boolean Stored;
    
    public GlobalFileData(String hash, Long length) {
    	Hash = hash;
    	Length = length;
    }
    
	public String getHash() {
		return Hash;
	}
	public Long getLength() {
		return Length;
	}
	public Boolean getStored() {
		return Stored;
	}
}
