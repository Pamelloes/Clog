package org.dyndns.pamelloes.Clog.permissions;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface PermissionsHandler {
	
	public GenericGroup getLowestGroup();
	
	public void saveGroups(Player p);
	public void saveGroups(Player p, List<GenericGroup> reason);
	
	public void restoreGroups(Player p, String reason);
	
	/**
	 * Utility method for setting a player's group.
	 * 
	 * @param player The Player whose group is to be set.
	 * @param groups The Player's new group.
	 * @param reason Reason to be given to the player.
	 */
	public void setGroups(Player p, String reason, GenericGroup ...groups);
	
	public Plugin getPlugin();
}
