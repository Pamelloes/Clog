package org.dyndns.pamelloes.Clog;

import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class ClogPlayerListener extends PlayerListener {
	private Clog clog;
	
	public ClogPlayerListener(Clog clog) {
		this.clog=clog;
	}
	
	@Override
	public void onPlayerKick(PlayerKickEvent e) {
		clog.restoreGroupsSuperPerms(e.getPlayer());
	}
	
	@Override
	public void onPlayerQuit(PlayerQuitEvent e) {
		clog.restoreGroupsSuperPerms(e.getPlayer());
	}
}
