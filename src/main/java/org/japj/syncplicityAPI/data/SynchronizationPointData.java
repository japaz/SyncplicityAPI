package org.japj.syncplicityAPI.data;

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
	
	private SynchronizationPointData() {
		
	}
	
	public SynchronizationPointData(Long type, String name, OwnerData owner) {
		this.Type = type;
		this.Name = name;
		this.Owner = owner;
	}
	
	public SynchronizationPointData(Long type, String name, OwnerData owner, String path) {
		this(type, name, owner);
		this.Path = path;
	}
	
	// Review if needed
	OwnerData User;
	
	public Long getId() {
		return Id;
	}
	public Long getType() {
		return Type;
	}
	public String getName() {
		return Name;
	}
	public Long getRootFolderId() {
		return RootFolderId;
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
	public OwnerData getOwner() {
		return Owner;
	}
	public Long getPermission() {
		return Permission;
	}
	public MappingData[] getMappings() {
		return Mappings;
	}
	public String getPath() {
		return Path;
	}
	public OwnerData getUser() {
		return User;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SynchronizationPointData) {
			SynchronizationPointData spd = (SynchronizationPointData) obj;
			if (this.Id != null) {
				return(this.Id.equals(spd.getId()));
			}
			
			return super.equals(obj);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		assert false : "hashCode not designed";
		return this.Id.hashCode();
	}
	
}
