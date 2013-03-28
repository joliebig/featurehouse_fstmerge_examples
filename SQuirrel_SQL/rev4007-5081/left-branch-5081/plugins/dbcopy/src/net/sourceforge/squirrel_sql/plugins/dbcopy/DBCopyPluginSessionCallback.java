
package net.sourceforge.squirrel_sql.plugins.dbcopy;

import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class DBCopyPluginSessionCallback implements PluginSessionCallback {

    DBCopyPlugin _plugin = null;
    
    public DBCopyPluginSessionCallback(DBCopyPlugin plugin) {
        _plugin = plugin;
    }
    
    public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame,
                                       ISession session) {
        

    }

    public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, 
                                              ISession session) {
        _plugin.addMenuItemsToContextMenu(objectTreeInternalFrame.getObjectTreeAPI());
        
    }

}
