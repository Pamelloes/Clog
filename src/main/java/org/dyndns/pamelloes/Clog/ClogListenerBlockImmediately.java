package org.dyndns.pamelloes.Clog;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.getspout.spoutapi.player.SpoutPlayer;

public class ClogListenerBlockImmediately implements Listener {
	private Clog clog;

	public ClogListenerBlockImmediately(Clog clog) {
		this.clog=clog;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		clog.addUnauthenticatedPlayer((SpoutPlayer) e.getPlayer());
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if(!clog.isAuthenticated((SpoutPlayer) e.getPlayer())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerChat(PlayerChatEvent e) {
		if(!clog.isAuthenticated((SpoutPlayer) e.getPlayer())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if(!clog.isAuthenticated((SpoutPlayer) e.getPlayer())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player)) return;
		SpoutPlayer p = (SpoutPlayer) e.getEntity();
		if(clog.isAuthenticated(p)) return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if(clog.isAuthenticated((SpoutPlayer) e.getPlayer())) return;
		e.setCancelled(true);
	}
}
