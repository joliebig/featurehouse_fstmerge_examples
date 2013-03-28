package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.gui.WindowManager;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

public class SessionPropertiesCommand implements ICommand
{
	
	private final ISession _session;
   private int _tabIndexToSelect = -1;

   
   public SessionPropertiesCommand(ISession session)
   {
      this(session, -1);
   }

   public SessionPropertiesCommand(ISession session, int tabIndexToSelect)
   {
      super();
      _tabIndexToSelect = tabIndexToSelect;
      if (session == null)
      {
         throw new IllegalArgumentException("Null ISession passed");
      }
      _session = session;
   }

   
   public void execute()
   {
      if (_session != null)
      {
         WindowManager winMgr = _session.getApplication().getWindowManager();
         winMgr.showSessionPropertiesDialog(_session, _tabIndexToSelect);
      }
   }
}
