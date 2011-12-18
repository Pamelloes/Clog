package org.dyndns.pamelloes.Clog;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionsPlugin;

public class Clog extends JavaPlugin {
	public Logger log = Logger.getLogger("Clog");
	
	private PermissionsPlugin superperms;
	private Map<Player, List<Group>> superpermsmap = new HashMap<Player, List<Group>>();
	private Group lowestgroup;
	
	private ClogServerListener csel = new ClogServerListener(this);
	private ClogSpoutListener cspl = new ClogSpoutListener(this);
	private ClogPlayerListener cpl = new ClogPlayerListener(this);

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Type.PLUGIN_ENABLE, csel, Priority.Monitor, this);
		pm.registerEvent(Type.CUSTOM_EVENT, cspl, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_QUIT, cpl, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_KICK, cpl, Priority.Monitor, this);
		File config = new File(getDataFolder(), "config.yml");
		if(!config.exists()) {
			try {
				getConfig().save(config);
			} catch(IOException e) {
				log.log(Level.WARNING, "[Clog] Could not create a config file.", e);
			}
		}
		log.info("[Clog] Clog enabled.");
	}
	
	public void onDisable() {
		File config = new File(getDataFolder(), "config.yml");
		try {
			getConfig().save(config);
		} catch(IOException e) {
			log.log(Level.WARNING, "[Clog] Could not save config file.", e);
		}
		log.info("[Clog] Clog disabled.");
		log = null;
		lowestgroup = null;
		csel = null;
		cspl = null;
		cpl = null;
	}
	
	public void setSuperPerms(PermissionsPlugin plugin) {
		lowestgroup = superperms.getGroup( (String)getConfig().get("invalidgroup","default") );
		superperms = plugin;
	}
	
	public PermissionsPlugin getSuperPerms() {
		return superperms;
	}
	
	public Group getLowestGroupSuperPerms() {
		return lowestgroup;
	}
	
	public void saveGroupsSuperPerms(Player p) {
		saveGroupsSuperPerms(p, superperms.getGroups(p.getName()));
	}
	
	public void saveGroupsSuperPerms(Player p, List<Group> groups) {
		superpermsmap.put(p, groups);
	}
	
	public void restoreGroupsSuperPerms(Player p) {
		restoreGroupsSuperPerms(p,"Restoring saved groups");
	}
	
	public void restoreGroupsSuperPerms(Player p, String reason) {
		List<Group> groups = superpermsmap.remove(p);
		if(groups==null) return;
 		setGroupsSuperPerms(p, reason, groups.toArray(new Group[0]));
	}
	
	/**
	 * Utility method for setting a player's group.
	 * 
	 * @param player The Player whose group is to be set.
	 * @param groups The Player's new group.
	 * @param reason Reason to be given to the player.
	 */
	public void setGroupsSuperPerms(Player player, String reason, Group... groups) {
		try {
			Field f = PermissionsPlugin.class.getDeclaredField("commandExecutor");
			f.setAccessible(true);
			Object ce = f.get(superperms);
			Class<?> c = Class.forName("com.platymuus.bukkit.permissions.PermissionsCommand");
			Method m = c.getDeclaredMethod("playerCommand", CommandSender.class, Command.class, String[].class);
			m.setAccessible(true);
			m.invoke(ce, getServer().getConsoleSender(), null, new String[]{"player","setgroup",player.getName(),groups[0].getName()});
			for(int i=1;i<groups.length; i++) m.invoke(ce, getServer().getConsoleSender(), null, new String[]{"player","addgroup",player.getName(),groups[i].getName()});
			String message = ChatColor.AQUA + "You have been put into group" + (groups.length>0 ? "s" : "");
			for(Group g : groups) message+=" \""+g.getName()+"\"";
			message+=" because: " + ChatColor.RED + reason;
			player.sendMessage(message);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
