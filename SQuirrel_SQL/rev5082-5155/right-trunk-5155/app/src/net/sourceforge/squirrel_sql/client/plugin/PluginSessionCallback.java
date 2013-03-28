package net.sourceforge.squirrel_sql.client.plugin;

import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;


public interface PluginSessionCallback
{
   

   void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess);
   void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess);
}
