package org.dyndns.pamelloes.Clog.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.dyndns.pamelloes.Clog.Clog;

import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.worlds.WorldPermissionsManager;

public class BPermsHandler implements PermissionsHandler {
	private Clog clog;
	
	private Permissions bperm;
	private WorldPermissionsManager wpm;
	private Map<World, String> lowestgroup = new HashMap<World, String>();
	private GenericGroup lowestgroupwrapper = new GenericGroup(lowestgroup);
	private Map<Player, List<GenericGroup>> map = new HashMap<Player, List<GenericGroup>>();
	
	public BPermsHandler(Clog clog, Plugin plugin) {
		this.clog = clog;
		if(!(plugin instanceof Permissions)) throw new IllegalArgumentException("plugin must be a de.bananaco.permissions.Permissions");
		bperm = (Permissions) plugin;
		wpm = Permissions.getWorldPermissionsManager();
		for(World w : clog.getServer().getWorlds()) {
			lowestgroup.put(w, (String)clog.getConfig().get("invalidgroup."+w.getName(),wpm.getPermissionSet(w).getDefaultGroup()));
		}
	}
	
	public GenericGroup getLowestGroup() {
		return lowestgroupwrapper;
	}

	public void saveGroups(Player p) {
		List<GenericGroup> groups = new ArrayList<GenericGroup>();
		for(World w : clog.getServer().getWorlds()) {
			List<String> g = wpm.getPermissionSet(w).getGroups(p);
			groups.add(new GenericGroup(new Object[]{w,g}));
		}
		saveGroups(p,groups);
	}

	public void saveGroups(Player p, List<GenericGroup> groups) {
		map.put(p, groups);
	}

	public void restoreGroups(Player p, String reason) {
		List<GenericGroup> groups = map.remove(p);
		setGroups(p, reason, groups.toArray(new GenericGroup[0]));
	}

	public boolean hasPermission(Player p, String permission) {
		return wpm.getPermissionSet(p.getWorld()).getPlayerNodes(p).contains(permission);
	}

	public void setGroups(Player p, String reason, GenericGroup... groups) {
		for(World w : clog.getServer().getWorlds()) {
			wpm.getPermissionSet(w).setGroup(p, null);
		}
		String message = ChatColor.AQUA + "You have been put into group" + (groups.length>0 ? "s" : "");
		for(GenericGroup g : groups) {
			if(g instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<World, String> data = (Map<World, String>) g.getActualGroup();
				String group = data.get(p.getWorld());
				wpm.getPermissionSet(p.getWorld()).addGroup(p, group);
				 message+=" \""+group+"\"";
			} else {
				Object[] data = (Object[]) g.getActualGroup();
				@SuppressWarnings("unchecked")
				List<String> g2 = (List<String>) data[1];
				for(String s : g2) {
					wpm.getPermissionSet((World) data[0]).addGroup(p, s);
					 message+=" \""+s+"\"";
				}
			}
		}
		message+=" because: " + ChatColor.RED + reason;
		p.sendMessage(message);
	}

	public Plugin getPlugin() {
		return bperm;
	}

}
