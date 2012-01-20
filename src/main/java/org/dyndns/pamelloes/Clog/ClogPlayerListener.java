package org.dyndns.pamelloes.Clog;

import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.getspout.spoutapi.player.SpoutPlayer;

public class ClogPlayerListener extends PlayerListener {
	private Clog clog;
	
	public ClogPlayerListener(Clog clog) {
		this.clog=clog;
	}
	
	@Override
	public void onPlayerJoin(PlayerJoinEvent e) {
		clog.addUnauthenticatedPlayer((SpoutPlayer) e.getPlayer());
	}
	
	@Override
	public void onPlayerKick(PlayerKickEvent e) {
		clog.restoreGroups(e.getPlayer());
		clog.authenticate((SpoutPlayer) e.getPlayer());
	}
	
	@Override
	public void onPlayerQuit(PlayerQuitEvent e) {
		clog.restoreGroups(e.getPlayer());
		clog.authenticate((SpoutPlayer) e.getPlayer());
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent e) {
		if(!clog.isAuthenticated((SpoutPlayer) e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@Override
	public void onPlayerChat(PlayerChatEvent e) {
		if(!clog.isAuthenticated((SpoutPlayer) e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@Override
	public void onPlayerMove(PlayerMoveEvent e) {
		if(!clog.isAuthenticated((SpoutPlayer) e.getPlayer())) {
			e.setCancelled(true);
		}
	}
}
