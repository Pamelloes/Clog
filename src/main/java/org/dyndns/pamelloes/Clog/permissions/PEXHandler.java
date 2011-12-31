package org.dyndns.pamelloes.Clog.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.dyndns.pamelloes.Clog.Clog;
import org.dyndns.pamelloes.Clog.Clog.Reason;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PEXHandler implements PermissionsHandler {
	private Clog clog;
	private PermissionsEx pex;
	private PermissionManager pexh;
	private GenericGroup lowestgroup;
	private Map<Player, List<GenericGroup>> map = new HashMap<Player,List<GenericGroup>>();
	
	public PEXHandler(Clog clog, Plugin plugin) {
		this.clog = clog;
		if(!(plugin instanceof PermissionsEx)) throw new IllegalArgumentException("plugin must be a ru.tehkode.permissions.bukkit.PermissionsEx");
		pex = (PermissionsEx) plugin;
		pexh = PermissionsEx.getPermissionManager();
		PermissionGroup group = pexh.getGroup( (String)clog.getConfig().get("invalidgroup",pexh.getDefaultGroup().getName()) );
		lowestgroup = new GenericGroup(new Object[]{null, group});
	}
	
	public GenericGroup getLowestGroup() {
		return lowestgroup;
	}

	public void saveGroups(Player p) {
		Map<String, PermissionGroup[]> allgroups = pexh.getUser(p).getAllGroups();
		List<GenericGroup> groups = new ArrayList<GenericGroup>();
		for(String s : allgroups.keySet()) {
			PermissionGroup[] g = allgroups.get(s);
			for(PermissionGroup g2 : g) groups.add(new GenericGroup(new Object[]{s,g2}));
		}
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
		return pexh.has(p, permission);
	}

	public void setGroups(Player p, Reason reason, GenericGroup... groups) {
		PermissionUser user = pexh.getUser(p);
		user.setGroups(new PermissionGroup[0]);
		String[] names = new String[groups.length];
		for(int i=0;i<groups.length;i++) {
			Object[] data = (Object[]) groups[i].getActualGroup();
			String world = (String) data[0];
			PermissionGroup group = (PermissionGroup) data[1];
			if(world!=null) user.addGroup(group, world);
			else user.addGroup(group);
			names[i]=group.getName();
		}
		String message = clog.getGroupChangeMessage(names, reason);
		p.sendMessage(message);
	}

	public Plugin getPlugin() {
		return pex;
	}

}
