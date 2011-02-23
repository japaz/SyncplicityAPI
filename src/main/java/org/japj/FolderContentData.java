package org.japj;

public class FolderContentData {
	public static final Long STATUS_ADDED = 1L;
	public static final Long STATUS_REMOVED = 4L;

	Long SyncpointId;
	Long FolderId;
	String VirtualPath;
	String Name;
	Long Status;
	FileData[] Files;
	FolderData[] Folders;
	
	public Long getSyncpointId() {
		return SyncpointId;
	}
	public Long getFolderId() {
		return FolderId;
	}
	public String getVirtualPath() {
		return VirtualPath;
	}
	public String getName() {
		return Name;
	}
	public Long getStatus() {
		return Status;
	}
	public FileData[] getFiles() {
		return Files;
	}
	public FolderData[] getFolders() {
		return Folders;
	}
}
