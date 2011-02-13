package org.japj;

public class FolderData {
	public static final Long STATUS_ADDED = 1L;
	public static final Long STATUS_REMOVED = 4L;
	
	Long FolderId; // The unique ID of the folder.
	String Name; // The name of the folder.
	Long Status; // 1 for Added (the folder exists), 4 for Removed (the folder has been deleted).
	FolderData Folders; // A list of Folder entries located inside this folder. This element will always be either absent or empty. When empty, the caller can assume the folder contains no subfolders.
	
	public Long getFolderId() {
		return FolderId;
	}
	public void setFolderId(Long folderId) {
		FolderId = folderId;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public Long getStatus() {
		return Status;
	}
	public void setStatus(Long status) {
		Status = status;
	}
	public FolderData getFolders() {
		return Folders;
	}
	public void setFolders(FolderData folders) {
		Folders = folders;
	}
	public static Long getStatusAdded() {
		return STATUS_ADDED;
	}
	public static Long getStatusRemoved() {
		return STATUS_REMOVED;
	}
	
	
}
