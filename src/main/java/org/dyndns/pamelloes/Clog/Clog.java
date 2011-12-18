package org.dyndns.pamelloes.Clog;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dyndns.pamelloes.Clog.permissions.GenericGroup;
import org.dyndns.pamelloes.Clog.permissions.PermissionsHandler;
import org.getspout.spoutapi.player.SpoutPlayer;

public class Clog extends JavaPlugin {
	public Logger log = Logger.getLogger("Clog");
	
	private PermissionsHandler handler;
	
	private ClogServerListener csel = new ClogServerListener(this);
	private ClogSpoutListener cspl = new ClogSpoutListener(this);
	private ClogPlayerListener cpl = new ClogPlayerListener(this);

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Type.PLUGIN_ENABLE, csel, Priority.Monitor, this);
		pm.registerEvent(Type.CUSTOM_EVENT, cspl, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_QUIT, cpl, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_KICK, cpl, Priority.Monitor, this);
		Permission p = new Permission("clog.ignore", "Allows the player to keep his or her rank without using Spoutcraft.");
		p.setDefault(PermissionDefault.OP);
		pm.addPermission(p);
		
		
		File config = new File(getDataFolder(), "config.yml");
		if(!config.exists()) {
			try {
				getConfig().save(config);
			} catch(IOException e) {
				log.log(Level.WARNING, "[Clog] Could not create a config file.", e);
			}
		}
		
		for(Player pl : getServer().getOnlinePlayers()) {
			SpoutPlayer sp = (SpoutPlayer) pl;
			if(sp.hasPermission("clog.ignore")) continue;
			if(sp.isSpoutCraftEnabled()) continue;
			saveGroups(sp);
			setGroups(sp, "You must be using Spoutcraft", getLowestGroup());
		}
		
		log.info("[Clog] Clog enabled.");
	}
	
	public void onDisable() {
		for(Player p : getServer().getOnlinePlayers()) restoreGroups(p, "Clog is being disabled.");
		File config = new File(getDataFolder(), "config.yml");
		try {
			getConfig().save(config);
		} catch(IOException e) {
			log.log(Level.WARNING, "[Clog] Could not save config file.", e);
		}
		log.info("[Clog] Clog disabled.");
		log = null;
		handler = null;
		csel = null;
		cspl = null;
		cpl = null;
	}
	
	public void setHandler(PermissionsHandler handler) {
		this.handler = handler;
	}
	
	public PermissionsHandler getHandler() {
		return handler;
	}
	
	public boolean hasPermission(Player p, String permission) {
		return handler.hasPermission(p, permission);
	}
	
	public GenericGroup getLowestGroup() {
		return handler.getLowestGroup();
	}
	
	public void saveGroups(Player p) {
		handler.saveGroups(p);
	}
	
	public void saveGroups(Player p, List<GenericGroup> groups) {
		handler.saveGroups(p, groups);
	}
	
	public void restoreGroups(Player p) {
		restoreGroups(p,"Restoring saved groups");
	}
	
	public void restoreGroups(Player p, String reason) {
		handler.restoreGroups(p, reason);
	}
	
	public void setGroups(Player p, String reason, GenericGroup ...groups) {
		handler.setGroups(p, reason, groups);
	}

}
