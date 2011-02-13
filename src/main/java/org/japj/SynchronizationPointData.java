package org.japj;

public class SynchronizationPointData {
	
	public static final Long SYNCPOINT_TYPE_DOCUMENTS = 1L;
	public static final Long SYNCPOINT_TYPE_MUSIC = 2L;
	public static final Long SYNCPOINT_TYPE_PICTURES = 3L;
	public static final Long SYNCPOINT_TYPE_DESKTOP = 4L;
	public static final Long SYNCPOINT_TYPE_FAVORITES = 5L;
	public static final Long SYNCPOINT_TYPE_CUSTOM = 6L;
	

	Long Id;
	Long Type;
	String Name;
	Long RootFolderId;
	Boolean Mapped;
	Boolean DownloadEnabled;
	Boolean UploadEnabled;
	OwnerData Owner;
	Long Permission;
	MappingData[] Mappings;
	String Path;
	
	// Review if needed
	OwnerData User;
	
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	public Long getType() {
		return Type;
	}
	public void setType(Long type) {
		Type = type;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public Long getRootFolderId() {
		return RootFolderId;
	}
	public void setRootFolderId(Long rootFolderId) {
		RootFolderId = rootFolderId;
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
	public OwnerData getOwner() {
		return Owner;
	}
	public void setOwner(OwnerData owner) {
		Owner = owner;
	}
	public Long getPermission() {
		return Permission;
	}
	public void setPermission(Long permission) {
		Permission = permission;
	}
	public MappingData[] getMappings() {
		return Mappings;
	}
	public void setMappings(MappingData[] mappings) {
		Mappings = mappings;
	}
	public String getPath() {
		return Path;
	}
	public void setPath(String path) {
		Path = path;
	}
	public OwnerData getUser() {
		return User;
	}
	public void setUser(OwnerData user) {
		// TODO Auto-generated method stub
		User = user;
	}
}
