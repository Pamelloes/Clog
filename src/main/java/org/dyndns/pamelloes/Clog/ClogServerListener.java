package org.dyndns.pamelloes.Clog;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;

import com.platymuus.bukkit.permissions.PermissionsPlugin;

public class ClogServerListener extends ServerListener {
	private Clog clog;
	
	public ClogServerListener(Clog clog) {
		this.clog=clog;
	}
	
	@Override
	public void onPluginEnable(PluginEnableEvent e) {
		if(e.getPlugin() instanceof PermissionsPlugin)  clog.setSuperPerms((PermissionsPlugin) e.getPlugin());
	}
	
	@Override
	public void onPluginDisable(PluginDisableEvent e) {
		if(e.getPlugin() instanceof PermissionsPlugin) clog.setSuperPerms(null);
	}
}
