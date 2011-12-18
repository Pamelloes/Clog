package org.dyndns.pamelloes.Clog;

import org.getspout.spoutapi.event.spout.SpoutListener;
import org.getspout.spoutapi.event.spout.SpoutcraftFailedEvent;
import org.getspout.spoutapi.player.SpoutPlayer;

public class ClogSpoutListener extends SpoutListener {
	private Clog clog;
	
	public ClogSpoutListener(Clog clog) {
		this.clog = clog;
	}
	
	@Override
	public void onSpoutcraftFailed(SpoutcraftFailedEvent e) {
		SpoutPlayer p = e.getPlayer();
		if(p.hasPermission("clog.ignore")) return;
		clog.saveGroups(p);
		clog.setGroups(p, "You must be using Spoutcraft", clog.getLowestGroup());
	}
}
