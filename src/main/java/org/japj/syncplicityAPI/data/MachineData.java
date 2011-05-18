package org.japj.syncplicityAPI.data;

public class MachineData {
	String Id;
	String Name;
	String SystemName;
	String SystemVersion;
	String Model;
	public String getId() {
		return Id;
	}
	public String getName() {
		return Name;
	}
	public String getSystemName() {
		return SystemName;
	}
	public String getSystemVersion() {
		return SystemVersion;
	}
	public String getModel() {
		return Model;
	}
	private MachineData() {
		
	}
	
	public MachineData(String name, String systemName, String systemVersion, String model) {
		Name = name;
		SystemName = systemName;
		SystemVersion = systemVersion;
		Model = model;
	}
}
