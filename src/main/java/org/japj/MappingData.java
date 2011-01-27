package org.japj;

public class MappingData {
	Long SyncPointId;
	MachineData Machine;
	String Path;
	Boolean Mapped;
	Boolean DownloadEnabled;
	Boolean UploadEnabled;
	public Long getSyncPointId() {
		return SyncPointId;
	}
	public void setSyncPointId(Long syncPointId) {
		SyncPointId = syncPointId;
	}
	public MachineData getMachine() {
		return Machine;
	}
	public void setMachine(MachineData machine) {
		Machine = machine;
	}
	public String getPath() {
		return Path;
	}
	public void setPath(String path) {
		Path = path;
	}
	public Boolean getMapped() {
		return Mapped;
	}
	public void setMapped(Boolean mapped) {
		Mapped = mapped;
	}
	public Boolean getDownloadEnabled() {
		return DownloadEnabled;
	}
	public void setDownloadEnabled(Boolean downloadEnabled) {
		DownloadEnabled = downloadEnabled;
	}
	public Boolean getUploadEnabled() {
		return UploadEnabled;
	}
	public void setUploadEnabled(Boolean uploadEnabled) {
		UploadEnabled = uploadEnabled;
	}
	
	
}
