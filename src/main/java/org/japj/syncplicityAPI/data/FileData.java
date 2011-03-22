package org.japj.syncplicityAPI.data;

public class FileData {
	public static final Long STATUS_ADDED = 1L;
	public static final Long STATUS_UPDATED = 2L;
	public static final Long STATUS_REMOVED = 3L;
	
	Long SyncpointId; // The ID of the SyncPoint the file is located in.
	Long FileId; // The unique ID of the file.
	String Filename; // The name of the file.
	Long Length; // The size of the file.
	String LastWriteTimeUtc; // A UTC timestamp of when the file was last modified.
	Long Status; // 1 for Added (the file exists), 3 for Removed (the file has been deleted).
	Long LatestVersionId; // The ID of the latest version of this file.
	String ThumbnailUrl; // The URL of a 40x40 thumbnail version of the file, if the file is an image.
	
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
	public Long getStatus() {
		return Status;
	}
	public Long getLatestVersionId() {
		return LatestVersionId;
	}
	public String getThumbnailUrl() {
		return ThumbnailUrl;
	}
	public static Long getStatusAdded() {
		return STATUS_ADDED;
	}
	public static Long getStatusRemoved() {
		return STATUS_REMOVED;
	}
}
