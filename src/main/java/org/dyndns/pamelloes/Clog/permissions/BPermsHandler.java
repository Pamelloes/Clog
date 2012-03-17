package org.dyndns.pamelloes.Clog.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.dyndns.pamelloes.Clog.Clog;
import org.dyndns.pamelloes.Clog.Clog.Reason;

import de.bananaco.bpermissions.api.Group;
import de.bananaco.bpermissions.api.User;
import de.bananaco.bpermissions.api.World;
import de.bananaco.bpermissions.api.WorldManager;
import de.bananaco.bpermissions.imp.Permissions;

public class BPermsHandler implements PermissionsHandler {
	private Clog clog;
	
	private Permissions bperm;
	private WorldManager wm = WorldManager.getInstance();
	private Map<World, String> lowestgroup = new HashMap<World, String>();
	private GenericGroup lowestgroupwrapper = new GenericGroup(lowestgroup);
	private Map<Player, List<GenericGroup>> map = new HashMap<Player, List<GenericGroup>>();
	
	public BPermsHandler(Clog clog, Plugin plugin) {
		this.clog = clog;
		if(!(plugin instanceof Permissions)) throw new IllegalArgumentException("plugin must be a de.bananaco.permissions.Permissions");
		bperm = (Permissions) plugin;
		for(World w : wm.getAllWorlds()) lowestgroup.put(w, (String)clog.getConfig().get("invalidgroup."+w.getName(),w.getDefaultGroup()));
	}
	
	public GenericGroup getLowestGroup() {
		return lowestgroupwrapper;
	}

	public void saveGroups(Player p) {
		List<GenericGroup> groups = new ArrayList<GenericGroup>();
		for(World w : wm.getAllWorlds()) {
			User u = w.getUser(p.getName());
			List<String> g = u.serialiseGroups();
			groups.add(new GenericGroup(new Object[]{w,g}));
		}
		saveGroups(p,groups);
	}

	public void saveGroups(Player p, List<GenericGroup> groups) {
		map.put(p, groups);
	}

	public void restoreGroups(Player p, Reason reason) {
		List<GenericGroup> groups = map.remove(p);
		if(groups==null) return;
		setGroups(p, reason, groups.toArray(new GenericGroup[0]));
	}

	public boolean hasPermission(Player p, String permission) {
		return wm.getWorld(p.getWorld().getName()).getUser(p.getName()).hasPermission(permission);
	}

	public void givePermission(Player p, String permission) {
		for(World w : wm.getAllWorlds()) {
			User u = w.getUser(p.getName());
			if(u.hasPermission(permission)) u.removePermission(permission);
			u.addPermission(permission, true);
		}
	}

	public void takePermission(Player p, String permission) {
		for(World w : wm.getAllWorlds())  w.getUser(p.getName()).removePermission(permission);
	}

	public void setGroups(Player p, Reason reason, GenericGroup... groups) {
		for(World w : wm.getAllWorlds()) for(Group g : w.getUser(p.getName()).getGroups()) w.getUser(p.getName()).removeGroup(g.getName());
		List<String> names = new ArrayList<String>();
		for(GenericGroup g : groups) {
			if(g.getActualGroup() instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<World, String> data = (Map<World, String>) g.getActualGroup();
				String group = data.get(p.getWorld());
				wm.getWorld(p.getWorld().getName()).getUser(p.getName()).addGroup(group);
				names.add(group);
			} else {
				Object[] data = (Object[]) g.getActualGroup();
				@SuppressWarnings("unchecked")
				List<String> g2 = (List<String>) data[1];
				for(String s : g2) ((World) data[0]).getUser(p.getName()).addGroup(s);
				names.addAll(g2);
			}
		}
		String message = clog.getGroupChangeMessage(names.toArray(new String[0]), reason);
		p.sendMessage(message);
	}

	public Plugin getPlugin() {
		return bperm;
	}
}
