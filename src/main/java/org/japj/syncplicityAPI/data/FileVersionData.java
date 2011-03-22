package org.japj.syncplicityAPI.data;

public class FileVersionData {
    Long SyncpointId;
    Long Id;
    String UserName;
    String DataSourceName;
    Long Action;
    Long Length;
    String RevisionAge;
    
	public Long getSyncpointId() {
		return SyncpointId;
	}
	public Long getId() {
		return Id;
	}
	public String getUserName() {
		return UserName;
	}
	public String getDataSourceName() {
		return DataSourceName;
	}
	public Long getAction() {
		return Action;
	}
	public Long getLength() {
		return Length;
	}
	public String getRevisionAge() {
		return RevisionAge;
	}
}
