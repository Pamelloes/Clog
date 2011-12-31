package org.dyndns.pamelloes.Clog.permissions;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.dyndns.pamelloes.Clog.Clog.Reason;

public interface PermissionsHandler {
	
	public GenericGroup getLowestGroup();
	
	public void saveGroups(Player p);
	public void saveGroups(Player p, List<GenericGroup> groups);
	
	public void restoreGroups(Player p, Reason reason);
	
	public boolean hasPermission(Player p, String permission);
	
	/**
	 * Utility method for setting a player's group.
	 * 
	 * @param player The Player whose group is to be set.
	 * @param groups The Player's new group.
	 * @param reason Reason to be given to the player.
	 */
	public void setGroups(Player p, Reason reason, GenericGroup ...groups);
	
	public Plugin getPlugin();
}
