package org.japj;

public class FolderContentData {
	String SyncpointId;
	String FolderId;
	FileData[] Files;
	FolderData[] Folders;
	
	public String getSyncpointId() {
		return SyncpointId;
	}
	public void setSyncpointId(String syncpointId) {
		SyncpointId = syncpointId;
	}
	public String getFolderId() {
		return FolderId;
	}
	public void setFolderId(String folderId) {
		FolderId = folderId;
	}
	public FileData[] getFiles() {
		return Files;
	}
	public void setFiles(FileData[] files) {
		Files = files;
	}
	public FolderData[] getFolders() {
		return Folders;
	}
	public void setFolders(FolderData[] folders) {
		Folders = folders;
	}
}
