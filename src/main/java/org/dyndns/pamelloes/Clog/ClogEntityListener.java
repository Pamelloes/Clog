package org.dyndns.pamelloes.Clog;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

public class ClogEntityListener extends EntityListener {
	private Clog clog;
	
	public ClogEntityListener(Clog clog) {
		this.clog=clog;
	}
	
	@Override
	public void onEntityDamage(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player)) return;
		Player p = (Player) e.getEntity();
		if(clog.isAuthenticated(p)) return;
		e.setCancelled(true);
	}
}
