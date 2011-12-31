package org.dyndns.pamelloes.Clog;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

public class ClogBlockListener extends BlockListener {
	private Clog clog;
	
	public ClogBlockListener(Clog clog) {
		this.clog = clog;
	}
	
	@Override
	public void onBlockBreak(BlockBreakEvent e) {
		if(clog.isAuthenticated(e.getPlayer())) return;
		e.setCancelled(true);
	}
}
