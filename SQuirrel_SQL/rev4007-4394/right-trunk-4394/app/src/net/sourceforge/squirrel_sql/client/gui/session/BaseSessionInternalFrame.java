package net.sourceforge.squirrel_sql.client.gui.session;

import java.io.File;

import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import net.sourceforge.squirrel_sql.client.gui.BaseInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class BaseSessionInternalFrame extends BaseInternalFrame  {
   protected ISession _session;
   private String _titleWithoutFile = "";
   private String _sqlFilePath = null;
   
   
   private static final StringManager s_stringMgr =
       StringManagerFactory.getStringManager(BaseSessionInternalFrame.class);        
   
   
   
	public BaseSessionInternalFrame(ISession session)
	{
		super();
		setupSheet(session);
	}

   
	public BaseSessionInternalFrame(ISession session, String title, boolean resizable)
	{
		super(title, resizable);
      _titleWithoutFile = title;
		setupSheet(session);
	}

   
	public BaseSessionInternalFrame(ISession session, String title, boolean resizable,
			boolean closable, boolean maximizable, boolean iconifiable)
	{
		super(title, resizable, closable, maximizable, iconifiable);
      _titleWithoutFile = title;
		setupSheet(session);
	}

   public void setTitle(String title)
   {
      _titleWithoutFile = title;


      if(null == _sqlFilePath)
      {
         super.setTitle(_titleWithoutFile);
      }
      else
      {
         
         String compositetitle = 
             s_stringMgr.getString("BaseSessionInternalFrame.title",
                                   new String[] { _titleWithoutFile,
                                                  _sqlFilePath });
         super.setTitle(compositetitle);
      }
   }

	private final void setupSheet(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}
		_session = session;
		_session.getApplication().getWindowManager().registerSessionSheet(this);
		addInternalFrameListener(new SheetActivationListener());
	}

	public ISession getSession()
	{
		return _session;
	}

   public void setSqlFile(File sqlFile)
   {
       if (sqlFile == null) {
           _sqlFilePath = null;
       } else {
           _sqlFilePath = sqlFile.getAbsolutePath();
       }
       setTitle(_titleWithoutFile);
   }

   
   public void setUnsavedEdits(boolean unsavedEdits) {
       String title = super.getTitle();
       
       if (unsavedEdits && !title.endsWith("*")) {
           super.setTitle(title + "*");
       }
       if (!unsavedEdits && title.endsWith("*")) {
           super.setTitle(title.substring(0, title.length() - 1));
       }
   }
   
   public boolean hasSQLPanelAPI()
   {
      return false;
   }

	public void closeFrame(boolean withEvents)
	{
        if (!_session.isfinishedLoading()) {
            return;
        }
		if(withEvents)
		{
			fireInternalFrameEvent(InternalFrameEvent.INTERNAL_FRAME_CLOSING);
		}
		dispose();

		if(withEvents)
		{
			fireInternalFrameEvent(InternalFrameEvent.INTERNAL_FRAME_CLOSED);
		}
	}
    
    
	private class SheetActivationListener extends InternalFrameAdapter
	{
		public void internalFrameActivated(InternalFrameEvent e)
		{
         _session.setActiveSessionWindow((BaseSessionInternalFrame)e.getInternalFrame());
			_session.getApplication().getSessionManager().setActiveSession(_session);
		}
	}
}
