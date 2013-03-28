

package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.fw.util.Resources;


public class AddBookmarkAction extends SquirrelAction
   implements ISQLPanelAction
{
    private static final long serialVersionUID = 1L;
    transient private ISession session;
    transient private SQLBookmarkPlugin plugin;

   public AddBookmarkAction(IApplication app, Resources rsrc,
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
         new AddBookmarkCommand(getParentFrame(evt), session, plugin).execute();
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
