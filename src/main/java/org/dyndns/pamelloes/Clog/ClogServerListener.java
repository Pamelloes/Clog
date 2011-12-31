package org.dyndns.pamelloes.Clog;

import java.lang.reflect.Constructor;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.dyndns.pamelloes.Clog.permissions.PermissionsHandler;

public class ClogServerListener extends ServerListener {
	private Clog clog;
	
	public ClogServerListener(Clog clog) {
		this.clog=clog;
	}
	
	@Override
	public void onPluginEnable(PluginEnableEvent e) {
		handleEnable(e.getPlugin());
	}
	
	public void handleEnable(Plugin p) {
		clog.log.info("[Clog] " + p.getDescription().getName());
		if(p.getDescription().getName().equals("PermissionsBukkit")) {
			try {
				Class<? extends Object> clazz = Class.forName("org.dyndns.pamelloes.Clog.permissions.SuperPermsHandler");
				Constructor<? extends Object> c = clazz.getConstructor(Clog.class, Plugin.class);
				PermissionsHandler ph = (PermissionsHandler) c.newInstance(clog, p);
				clog.setHandler(ph);
			} catch(Exception ex) {
				//ignore
			}
		} else if(p.getDescription().getName().equals("PermissionsEx")) {
			try {
				Class<? extends Object> clazz = Class.forName("org.dyndns.pamelloes.Clog.permissions.PEXHandler");
				Constructor<? extends Object> c = clazz.getConstructor(Clog.class, Plugin.class);
				PermissionsHandler ph = (PermissionsHandler) c.newInstance(clog, p);
				clog.setHandler(ph);
			} catch(Exception ex) {
				//ignore
			}
		} else if(p.getDescription().getName().equals("bPermissions")) {
			try {
				Class<? extends Object> clazz = Class.forName("org.dyndns.pamelloes.Clog.permissions.BPermsHandler");
				Constructor<? extends Object> c = clazz.getConstructor(Clog.class, Plugin.class);
				PermissionsHandler ph = (PermissionsHandler) c.newInstance(clog, p);
				clog.setHandler(ph);
			} catch(Exception ex) {
				//ignore
			}
		}
		
	}
	
	@Override
	public void onPluginDisable(PluginDisableEvent e) {
		if(clog.getHandler().getPlugin().equals(e.getPlugin())) clog.setHandler(null);
	}
}
