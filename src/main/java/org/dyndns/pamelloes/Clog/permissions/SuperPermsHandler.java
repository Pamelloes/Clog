package org.dyndns.pamelloes.Clog.permissions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.dyndns.pamelloes.Clog.Clog;

import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionsPlugin;

public class SuperPermsHandler implements PermissionsHandler {
	private Clog clog;
	
	private PermissionsPlugin superperms;
	private Map<Player, List<GenericGroup>> superpermsmap = new HashMap<Player, List<GenericGroup>>();
	private GenericGroup lowestgroup;

	public SuperPermsHandler(Clog clog, Plugin plugin) {
		this.clog = clog;
		if(!(plugin instanceof PermissionsPlugin)) throw new IllegalArgumentException("plugin must be a com.platymus.bukkit.permissions.PermissionsPlugin");
		superperms = (PermissionsPlugin) plugin;
		lowestgroup = new GenericGroup(superperms.getGroup( (String)clog.getConfig().get("invalidgroup","default") ));
	}
	
	public GenericGroup getLowestGroup() {
		return lowestgroup;
	}

	public void saveGroups(Player p) {
		List<Group> gs = superperms.getGroups(p.getName());
		List<GenericGroup> gs2 = new ArrayList<GenericGroup>();
		for(Group g : gs) gs2.add(new GenericGroup(g));
		saveGroups(p, gs2);
	}
	
	public void saveGroups(Player p, List<GenericGroup> groups) {
		superpermsmap.put(p, groups);
	}

	public void restoreGroups(Player p, String reason) {
		List<GenericGroup> groups = superpermsmap.remove(p);
		if(groups==null) return;
 		setGroups(p, reason, groups.toArray(new GenericGroup[0]));
	}

	public void setGroups(Player player, String reason, GenericGroup... group) {
		Group[] groups = new Group[group.length];
		for(int i=0;i<group.length;i++) groups[i] = (Group) group[i].getActualGroup();
		try {
			Field f = PermissionsPlugin.class.getDeclaredField("commandExecutor");
			f.setAccessible(true);
			Object ce = f.get(superperms);
			Class<?> c = Class.forName("com.platymuus.bukkit.permissions.PermissionsCommand");
			Method m = c.getDeclaredMethod("playerCommand", CommandSender.class, Command.class, String[].class);
			m.setAccessible(true);
			m.invoke(ce, clog.getServer().getConsoleSender(), null, new String[]{"player","setgroup",player.getName(),groups[0].getName()});
			for(int i=1;i<groups.length; i++) m.invoke(ce, clog.getServer().getConsoleSender(), null, new String[]{"player","addgroup",player.getName(),groups[i].getName()});
			String message = ChatColor.AQUA + "You have been put into group" + (groups.length>0 ? "s" : "");
			for(Group g : groups) message+=" \""+g.getName()+"\"";
			message+=" because: " + ChatColor.RED + reason;
			player.sendMessage(message);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public boolean hasPermission(Player p, String permission) {
		return p.hasPermission(permission);
	}

	public Plugin getPlugin() {
		return superperms;
	}
}
