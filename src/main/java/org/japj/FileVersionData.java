package org.japj;

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
	public void setSyncpointId(Long syncpointId) {
		SyncpointId = syncpointId;
	}
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
	public String getDataSourceName() {
		return DataSourceName;
	}
	public void setDataSourceName(String dataSourceName) {
		DataSourceName = dataSourceName;
	}
	public Long getAction() {
		return Action;
	}
	public void setAction(Long action) {
		Action = action;
	}
	public Long getLength() {
		return Length;
	}
	public void setLength(Long length) {
		Length = length;
	}
	public String getRevisionAge() {
		return RevisionAge;
	}
	public void setRevisionAge(String revisionAge) {
		RevisionAge = revisionAge;
	}
    
}
