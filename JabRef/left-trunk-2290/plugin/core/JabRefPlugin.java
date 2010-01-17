package net.sf.jabref.plugin.core;

import net.sf.jabref.plugin.core.generated._JabRefPlugin;

import org.java.plugin.PluginLifecycleException;
import org.java.plugin.PluginManager;


public class JabRefPlugin extends _JabRefPlugin {

    public void doStart(){
        
    }

    public void doStop(){
        
    }

    
	public static JabRefPlugin getInstance(PluginManager manager) {
		try {
			return (JabRefPlugin) manager
					.getPlugin(JabRefPlugin.getId());
		} catch (PluginLifecycleException e) {
			return null;
		} catch (IllegalArgumentException e) {
		    return null;
		}
	}
}