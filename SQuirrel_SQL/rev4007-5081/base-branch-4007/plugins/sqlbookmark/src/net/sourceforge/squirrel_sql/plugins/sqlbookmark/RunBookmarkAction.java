

package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.fw.util.Resources;


public class RunBookmarkAction extends SquirrelAction
   implements ISQLPanelAction
{
    private static final long serialVersionUID = 1L;

    
	transient private ISession session;

   
   transient private SQLBookmarkPlugin plugin;

   public RunBookmarkAction(IApplication app, Resources rsrc,
                            SQLBookmarkPlugin plugin)
      throws IllegalArgumentException
   {
      super(app, rsrc);
      if (plugin == null)
      {
         throw new IllegalArgumentException("null IPlugin passed");
      }
      this.plugin = plugin;
   }

   public void actionPerformed(ActionEvent evt)
   {
      if (session != null)
      {
         Object source = evt.getSource();
         if (source instanceof JMenuItem)
         {
            JMenuItem item = (JMenuItem) source;

            Bookmark bookmark =
               plugin.getBookmarkManager().get(item.getText());

            ISQLEntryPanel sqlEntryPanel;

            if (session.getActiveSessionWindow() instanceof SessionInternalFrame)
            {
               sqlEntryPanel = ((SessionInternalFrame) session.getActiveSessionWindow()).getSQLPanelAPI().getSQLEntryPanel();
            }
            else if (session.getActiveSessionWindow() instanceof SQLInternalFrame)
            {
               sqlEntryPanel = ((SQLInternalFrame) session.getActiveSessionWindow()).getSQLPanelAPI().getSQLEntryPanel();
            }
            else
            {
               return;
            }


            if (bookmark != null)
               new RunBookmarkCommand(getParentFrame(evt), session, bookmark, plugin, sqlEntryPanel).execute();
         }
      }
   }

   public void setSQLPanel(ISQLPanelAPI panel)
   {
      if(null != panel)
      {
         session = panel.getSession();
      }
      else
      {
         session = null;
      }
      setEnabled(null != session);
   }
   
}
