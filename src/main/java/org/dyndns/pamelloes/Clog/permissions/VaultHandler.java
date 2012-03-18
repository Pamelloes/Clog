package org.dyndns.pamelloes.Clog.permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.dyndns.pamelloes.Clog.Clog;
import org.dyndns.pamelloes.Clog.Clog.Reason;

public class VaultHandler implements PermissionsHandler {
	private Clog clog;
	
	private Vault vault;
	
	private Permission permission = null;
	private GenericGroup lowest;
	
	private Map<Player, List<GenericGroup>> map = new HashMap<Player,List<GenericGroup>>();
	
	public VaultHandler(Clog clog, Plugin plugin) {
		this.clog = clog;
		if(!(plugin instanceof Vault)) throw new IllegalArgumentException("plugin must be a net.milkbowl.vault.Vault");
		vault = (Vault) vault;
		
		RegisteredServiceProvider<Permission> permissionProvider = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) permission = permissionProvider.getProvider();
        if (permission == null) throw new RuntimeException("Vault hasn't found a permissions plugin!");
        
        List<String> groups = Arrays.asList(permission.getGroups());
        String search = (String)clog.getConfig().get("invalidgroup","default");
        if(groups.contains(search)) lowest = new GenericGroup(search);
        else throw new RuntimeException("Can't find lowest group!");
	}

	public GenericGroup getLowestGroup() {
		return lowest;
	}

	public void saveGroups(Player p) {
		String[] groupnames = permission.getPlayerGroups(p);
		List<GenericGroup> groups = new ArrayList<GenericGroup>();
		for(String s : groupnames) groups.add(new GenericGroup(s));
		saveGroups(p,groups);
	}

	public void saveGroups(Player p, List<GenericGroup> groups) {
		map.put(p, groups);
	}

	public void restoreGroups(Player p, Reason reason) {
		List<GenericGroup> restore = map.remove(p);
		if(restore==null) return;
		setGroups(p, reason, restore.toArray(new GenericGroup[0]));
	}

	public boolean hasPermission(Player p, String permission) {
		return this.permission.has(p, permission);
	}

	public void givePermission(Player p, String permission) {
		this.permission.playerAdd(p, permission);
	}

	public void takePermission(Player p, String permission) {
		this.permission.playerRemove(p, permission);
	}

	public void setGroups(Player p, Reason reason, GenericGroup... groups) {
		for(String s : permission.getPlayerGroups(p)) permission.playerRemoveGroup(p, s);
		
		String[] names = new String[groups.length];
		for(int i=0;i<groups.length;i++) {
			String group = (String) groups[i].getActualGroup();
			permission.playerAddGroup(p, group);
			names[i]=group;
		}
		String message = clog.getGroupChangeMessage(names, reason);
		p.sendMessage(message);
	}

	public Plugin getPlugin() {
		return vault;
	}

}
