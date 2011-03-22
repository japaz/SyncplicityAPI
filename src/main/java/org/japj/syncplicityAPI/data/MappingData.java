package org.japj.syncplicityAPI.data;

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
	public MachineData getMachine() {
		return Machine;
	}
	public String getPath() {
		return Path;
	}
	public Boolean getMapped() {
		return Mapped;
	}
	public Boolean getDownloadEnabled() {
		return DownloadEnabled;
	}
	public Boolean getUploadEnabled() {
		return UploadEnabled;
	}
}
