package org.dyndns.pamelloes.Clog;

import org.dyndns.pamelloes.Clog.Clog.Reason;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;
import org.getspout.spoutapi.event.spout.SpoutcraftFailedEvent;
import org.getspout.spoutapi.player.SpoutPlayer;

public class ClogSpoutListener extends SpoutListener {
	private Clog clog;
	
	public ClogSpoutListener(Clog clog) {
		this.clog = clog;
	}
	
	@Override
	public void onSpoutCraftEnable(SpoutCraftEnableEvent e) {
		clog.authenticate(e.getPlayer());
	}
	
	@Override
	public void onSpoutcraftFailed(SpoutcraftFailedEvent e) {
		SpoutPlayer p = e.getPlayer();
		if(clog.hasPermission(p,"clog.ignore")) return;
		clog.saveGroups(p);
		clog.setGroups(p, Reason.SCFailed, clog.getLowestGroup());
		clog.authenticate(e.getPlayer());
	}
}
