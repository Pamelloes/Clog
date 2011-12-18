package org.dyndns.pamelloes.Clog.permissions;

public class GenericGroup {
	private Object group;
	
	public GenericGroup(Object group) {
		this.group = group;
	}
	
	public Object getActualGroup() {
		return group;
	}
}
