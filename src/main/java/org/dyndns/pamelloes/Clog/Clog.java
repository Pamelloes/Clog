package org.dyndns.pamelloes.Clog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dyndns.pamelloes.Clog.permissions.GenericGroup;
import org.dyndns.pamelloes.Clog.permissions.PermissionsHandler;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class Clog extends JavaPlugin implements CommandExecutor {
	public static enum Reason {
		SCFailed,
		Restore,//never shown, replaced with ""
		Disable//same as above.
	}

	public Logger log = Logger.getLogger("Clog");
	
	private PermissionsHandler handler;
	
	private ClogServerListener csel = new ClogServerListener(this);
	private ClogSpoutListener cspl = new ClogSpoutListener(this);
	private ClogPlayerListener cpl = new ClogPlayerListener(this);
	private ClogEntityListener cel = new ClogEntityListener(this);
	private ClogBlockListener cbl = new ClogBlockListener(this);
	
	private List<SpoutPlayer> notauthenticated = new ArrayList<SpoutPlayer>();

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Type.PLUGIN_ENABLE, csel, Priority.Monitor, this);
		pm.registerEvent(Type.CUSTOM_EVENT, cspl, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_QUIT, cpl, Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_KICK, cpl, Priority.Monitor, this);
		
		if(getConfig().getBoolean("options.blockimmediately", true)) {
			pm.registerEvent(Type.PLAYER_JOIN, cpl, Priority.Monitor, this);
			pm.registerEvent(Type.PLAYER_INTERACT, cpl, Priority.Monitor, this);
			pm.registerEvent(Type.PLAYER_CHAT, cpl, Priority.Monitor, this);
			pm.registerEvent(Type.PLAYER_MOVE, cpl, Priority.Monitor, this);
			pm.registerEvent(Type.ENTITY_DAMAGE, cel, Priority.Monitor, this);
			pm.registerEvent(Type.BLOCK_BREAK, cbl, Priority.Monitor, this);
		}
		
		getCommand("hasspout").setExecutor(this);
		
		for(Plugin p : pm.getPlugins()) csel.handleEnable(p);
		
		Permission p = new Permission("clog.ignore", "Allows the player to keep his or her rank without using Spoutcraft.");
		p.setDefault(PermissionDefault.OP);
		pm.addPermission(p);
		
		
		File config = new File(getDataFolder(), "config.yml");
		if(!config.exists()) {
			extractFile("config.yml", false);
		}
		try {
			getConfig().load(config);
		} catch (Exception e) {
			log.info("[Clog] Couldn't load config.");
		}
		
		for(Player pl : getServer().getOnlinePlayers()) {
			SpoutPlayer sp = (SpoutPlayer) pl;
			if(sp.hasPermission("clog.ignore")) continue;
			if(sp.isSpoutCraftEnabled()) continue;
			saveGroups(sp);
			setGroups(sp, Reason.SCFailed, getLowestGroup());
		}
		
		log.info("[Clog] Clog enabled.");
	}
	
	public void onDisable() {
		for(Player p : getServer().getOnlinePlayers()) restoreGroups(p, Reason.Disable);
		log.info("[Clog] Clog disabled.");
		log = null;
		handler = null;
		csel = null;
		cspl = null;
		cpl = null;
	}
	
	public void addUnauthenticatedPlayer(SpoutPlayer p) {
		notauthenticated.add(p);
		if(hasPermission(p, "clog.ignore.perms")) return;
		List<?> list = getConfig().getList("permissions.scperms",null);
		if(list!=null) for(Object o : list) takePermission(p,(String) o);
		list = getConfig().getList("permissions.vanillaperms",null);
		if(list!=null) for(Object o : list) givePermission(p,(String) o);
	}
	
	public void authenticate(SpoutPlayer p) {
		notauthenticated.remove(p);
		if(hasPermission(p, "clog.ignore.perms")) return;
		if(p.isSpoutCraftEnabled()) {
			List<?> list = getConfig().getList("permissions.scperms",null);
			if(list!=null) for(Object o : list) givePermission(p,(String) o);
		} else {
			List<?> list = getConfig().getList("permissions.vanillaperms",null);
			if(list!=null) for(Object o : list) givePermission(p,(String) o);
		}
	}
	
	public boolean isAuthenticated(SpoutPlayer p) {
		return !notauthenticated.contains(p);
	}
	
	public String getGroupChangeMessage(String[] groupnames, Reason reason) {
		String base = ChatColor.AQUA + getConfig().getString("messages.groupchange", "You have been put into groups {groups} because: {reason}");
		if(base.indexOf("{groups}")>=0) {
			String groups = "";
			for(String group : groupnames) groups+=" \""+group+"\"";
			if(groups.length() > 1) groups.substring(1);
			System.out.println(groups);
			base = base.replaceAll("\\{groups}", groups);
		}
		if(base.indexOf("{reason}")>=0) {
			String reas = "";
			switch(reason) {
			case SCFailed:
				reas="scfailed";
				break;
			default:
				reas=null;
				break;
			}
			if(reas!=null) reas = getConfig().getString("reasons." + reas, "");
			else reas="";
			reas = ChatColor.RED + reas + ChatColor.AQUA;
			base = base.replaceAll("\\{reason}", reas);
		}
		return base;
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
	
	public void givePermission(Player p, String permission) {
		handler.givePermission(p, permission);
	}
	
	public void takePermission(Player p, String permission) {
		handler.takePermission(p, permission);
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
		restoreGroups(p, Reason.Restore);
	}
	
	public void restoreGroups(Player p, Reason reason) {
		handler.restoreGroups(p, reason);
	}
	
	public void setGroups(Player p, Reason reason, GenericGroup ...groups) {
		handler.setGroups(p, reason, groups);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(cmd.getName().equalsIgnoreCase("hasspout") && args.length==0){
			Player p = getServer().getPlayer(args[0]);
			if(p==null) return false;
			sender.sendMessage(p.getName() + "has spoutcraft: " + ((SpoutPlayer) p).isSpoutCraftEnabled());
			return true;
		}
		return false; 
	}
	
	/**
	 * Extract files from the plugin jar and optionally cache them on the client.
	 * @param regex a pattern of files to extract
	 * @param cache if any files found should be added to the Spout cache
	 * @return if any files were extracted
	 */
	public boolean extractFile(String regex, boolean cache) {
		boolean found = false;
		try {
			JarFile jar = new JarFile(getFile());
			for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements();) {
				JarEntry entry = (JarEntry) entries.nextElement();
				String name = entry.getName();
				if (name.matches(regex)) {
					if (!getDataFolder().exists()) {
						getDataFolder().mkdir();
					}
					try {
						File file = new File(getDataFolder(), name);
						if (!file.exists()) {
							InputStream is = jar.getInputStream(entry);
							FileOutputStream fos = new FileOutputStream(file);
							while (is.available() > 0) {
								fos.write(is.read());
							}
							fos.close();
							is.close();
							found = true;
						}
						if (cache && name.matches(".*\\.(txt|yml|xml|png|jpg|ogg|midi|wav|zip)$")) {
							SpoutManager.getFileManager().addToPreLoginCache(this, file);
						}
					} catch (Exception e) {
					}
				}
			}
		} catch (Exception e) {
		}
		return found;
	}
}
