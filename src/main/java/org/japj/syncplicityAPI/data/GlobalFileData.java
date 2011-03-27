package org.japj.syncplicityAPI.data;

public class GlobalFileData {
	String Hash;
    Long Length;
    Boolean Stored;
    
    GlobalFileData() {
    	
    }
    
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
	public Boolean isStored() {
		return Stored;
	}
}
