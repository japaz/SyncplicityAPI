package org.japj.syncplicityAPI.data;

public class FileData {
	
	public enum FileDataStatus {
		ADDED(1), UPDATED(2), REMOVED(3);
		
		private Integer status;
		
		FileDataStatus(Integer status) {
			this.status = status;
		}
		
		public Integer toInteger() {
			return status;
		}
		
		static public FileDataStatus fromInteger(Integer status) {
			for (FileDataStatus fileDataStatus : FileDataStatus.values()) {
				if (fileDataStatus.status.equals(status)) {
					return fileDataStatus;
				}
			}
			throw new IllegalArgumentException(String.format("Status not allowed: %s", status));
		}
	}
	
	Long SyncpointId; // The ID of the SyncPoint the file is located in.
	Long FileId; // The unique ID of the file.
	String Filename; // The name of the file.
	Long Length; // The size of the file.
	String LastWriteTimeUtc; // A UTC timestamp of when the file was last modified.
	Integer Status; // 1 for Added (the file exists), 3 for Removed (the file has been deleted).
	Long LatestVersionId; // The ID of the latest version of this file.
	String ThumbnailUrl; // The URL of a 40x40 thumbnail version of the file, if the file is an image.
	
	String VirtualPath;
	String Hash;
	String CreationTimeUtc;
	Long SyncPriority;
	
	FileData() {
		
	}
	
	public FileData(String virtualPath, String fileName, Long length, String hash, 
					String creationTimeUtc, String lastWriteTimeUtc, Long syncPriority, FileDataStatus status) {
		VirtualPath = virtualPath;
		Filename = fileName;
		Length = length;
		Hash = hash;
		CreationTimeUtc = creationTimeUtc;
		LastWriteTimeUtc = lastWriteTimeUtc;
		SyncPriority = syncPriority;
		Status = status.toInteger();
	}
	
	public Long getSyncpointId() {
		return SyncpointId;
	}
	public Long getFileId() {
		return FileId;
	}
	public String getFilename() {
		return Filename;
	}
	public Long getLength() {
		return Length;
	}
	public String getLastWriteTimeUtc() {
		return LastWriteTimeUtc;
	}
	public FileDataStatus getStatus() {
		return FileDataStatus.fromInteger(Status);
	}
	public Long getLatestVersionId() {
		return LatestVersionId;
	}
	public String getThumbnailUrl() {
		return ThumbnailUrl;
	}
}
