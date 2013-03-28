
package net.sourceforge.squirrel_sql.plugins.dbdiff;

import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.session.ISession;


public class DBDiffPluginSessionCallback implements PluginSessionCallback {

    DBDiffPlugin _plugin = null;
    
    public DBDiffPluginSessionCallback(DBDiffPlugin plugin) {
        _plugin = plugin;
    }
    
    public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame,
                                       ISession session) {
        

    }

    public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, 
                                              ISession session) {
        _plugin.addMenuItemsToContextMenu(session);
    }

}
