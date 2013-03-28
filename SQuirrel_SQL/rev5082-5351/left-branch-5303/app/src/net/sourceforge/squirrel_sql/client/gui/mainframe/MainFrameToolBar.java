package net.sourceforge.squirrel_sql.client.gui.mainframe;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.mainframe.action.CascadeAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasCommand;
import net.sourceforge.squirrel_sql.client.mainframe.action.GlobalPreferencesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.MaximizeAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.NewSessionPropertiesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.TileAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.TileHorizontalAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.TileVerticalAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.client.session.action.CommitAction;
import net.sourceforge.squirrel_sql.client.session.action.NewObjectTreeAction;
import net.sourceforge.squirrel_sql.client.session.action.NewSQLWorksheetAction;
import net.sourceforge.squirrel_sql.client.session.action.RollbackAction;
import net.sourceforge.squirrel_sql.client.session.action.ToggleAutoCommitAction;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.IToggleAction;
import net.sourceforge.squirrel_sql.fw.gui.SortedComboBoxModel;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.IObjectCacheChangeListener;
import net.sourceforge.squirrel_sql.fw.util.ObjectCacheChangeEvent;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

class MainFrameToolBar extends ToolBar
{
    private static final long serialVersionUID = 1L;

    
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(MainFrameToolBar.class);

	
	transient private IApplication _app;

	private boolean _dontReactToSessionDropDownAction = false;
   
   
   MainFrameToolBar(IApplication app)
   {
      super();
      if (app == null)
      {
         throw new IllegalArgumentException("IApplication == null");
      }
      _app = app;
      setUseRolloverButtons(true);
      setFloatable(true);

      ActionCollection actions = _app.getActionCollection();
      JLabel lbl = new JLabel(s_stringMgr.getString("MainFrameToolBar.connectTo"));
      lbl.setAlignmentY(0.5f);
      add(lbl);
      AliasesDropDown drop = new AliasesDropDown(app);
      drop.setAlignmentY(0.5f);
      add(drop);
      addSeparator();
      add(actions.get(GlobalPreferencesAction.class));
      add(actions.get(NewSessionPropertiesAction.class));
      if (_app.getDesktopStyle().isInternalFrameStyle())
      {
         addSeparator();
         add(actions.get(TileAction.class));
         add(actions.get(TileHorizontalAction.class));
         add(actions.get(TileVerticalAction.class));
         add(actions.get(CascadeAction.class));
         add(actions.get(MaximizeAction.class));
         addSeparator();
      }
      JLabel lbl2 = new JLabel(" " + s_stringMgr.getString("MainFrameToolBar.activeSession") + " ");
      lbl.setAlignmentY(0.5f);
      add(lbl2);
      SessionDropDown sessionDropDown = new SessionDropDown(app);
      sessionDropDown.setAlignmentY(0.5f);
      add(sessionDropDown);

      addToggleAction((IToggleAction) actions.get(ToggleAutoCommitAction.class));
      add(actions.get(CommitAction.class));
      add(actions.get(RollbackAction.class));

      addSeparator();

      add(actions.get(NewSQLWorksheetAction.class));
      add(actions.get(NewObjectTreeAction.class));
   }


   
	private static class AliasesDropDown extends JComboBox
											implements ActionListener
	{
	    private static final long serialVersionUID = 1L;
        
        transient final private IApplication _myApp;

		AliasesDropDown(IApplication app)
		{
			super();
			_myApp = app;
			final AliasesDropDownModel model = new AliasesDropDownModel(app, this);
			setModel(model);

			
			
			if (getModel().getSize() > 0)
			{
				setSelectedIndex(0);
			}

			
			else
			{
				final Dimension dm = getPreferredSize();
				dm.width = 100;
				setPreferredSize(dm);
			}
			addActionListener(this);
			setMaximumSize(getPreferredSize());

			app.getDataCache().addAliasesListener(new MyAliasesListener(model, this));
			
			this.setName(this.getClass().getCanonicalName());
		}

		
		public void actionPerformed(ActionEvent evt)
		{
			try
			{
				Object obj = getSelectedItem();
				if (obj instanceof SQLAlias && this.isEnabled())
				{
					new ConnectToAliasCommand(_myApp, (SQLAlias)obj).execute();
				}
			}
			finally
			{
				if (getModel().getSize() > 0)
				{
					setSelectedIndex(0);
				}
			}
		}
	}

	
	private static class AliasesDropDownModel extends SortedComboBoxModel
	{
        private static final long serialVersionUID = 1L;

        transient private IApplication _myApp;
        private AliasesDropDown _aliasDropDown;
		
		public AliasesDropDownModel(IApplication app, AliasesDropDown drop)
		{
			super();
			_myApp = app;
            _aliasDropDown = drop;
			load();
			
		}

		
		private void load()
		{
			Iterator<ISQLAlias> it = _myApp.getDataCache().aliases();
			while (it.hasNext())
			{
				addAlias(it.next());
			}
		}

		
		private void addAlias(ISQLAlias alias)
		{
            _aliasDropDown.setEnabled(false);
			addElement(alias);
            if (_aliasDropDown.getModel().getSize() > 0) {
                _aliasDropDown.setSelectedIndex(0);
            }
            _aliasDropDown.setEnabled(true);            
		}

		
		private void removeAlias(ISQLAlias alias)
		{
            _aliasDropDown.setEnabled(false);
			removeElement(alias);
            if (_aliasDropDown.getModel().getSize() > 0) {
                _aliasDropDown.setSelectedIndex(0);
            }
            _aliasDropDown.setEnabled(true);
		}
	}

	
	private static class MyAliasesListener implements IObjectCacheChangeListener
	{
		
		private AliasesDropDownModel _model;

		
		private AliasesDropDown _control;

		
		MyAliasesListener(AliasesDropDownModel model, AliasesDropDown control)
		{
			super();
			_model = model;
			_control = control;
		}

		
		public void objectAdded(ObjectCacheChangeEvent evt)
		{
			Object obj = evt.getObject();
			if (obj instanceof ISQLAlias)
			{
				_model.addAlias((ISQLAlias) obj);
			}
			if (_control.getItemCount() == 1)
			{
				_control.setSelectedIndex(0);
			}
		}

		
		public void objectRemoved(ObjectCacheChangeEvent evt)
		{
			Object obj = evt.getObject();
			if (obj instanceof ISQLAlias)
			{
				_model.removeAlias((ISQLAlias)obj);
			}
		}
	}

	
	private class SessionDropDown extends JComboBox
										implements ActionListener
	{
        private static final long serialVersionUID = 1L;
        private IApplication _app;
		private boolean _closing = false;

		SessionDropDown(IApplication app)
		{
			super();
			_app = app;
			final SessionManager sessionManager = _app.getSessionManager();
			final SessionDropDownModel model = new SessionDropDownModel(
															sessionManager);
			setModel(model);

			
			
			if (getModel().getSize() > 0)
			{
				setSelectedIndex(0);
			}
			else
			{
				
				Dimension dm = getPreferredSize();
				dm.width = 200;
				setPreferredSize(dm);
				
				setEnabled(false);
			}
			addActionListener(this);
			setMaximumSize(getPreferredSize());

			sessionManager.addSessionListener(new MySessionListener(model, this));
		}

		
		public void actionPerformed(ActionEvent evt)
		{
			if (!_closing && !_dontReactToSessionDropDownAction)
			{
				final Object obj = getSelectedItem();
				if (obj instanceof ISession)
				{
					_app.getSessionManager().setActiveSession((ISession)obj);
				}
			}
		}
	}

	
	private static class SessionDropDownModel extends SortedComboBoxModel
	{
        private static final long serialVersionUID = 1L;
        transient private SessionManager _sessionManager;

		
		public SessionDropDownModel(SessionManager sessionManager)
		{
			super();
			_sessionManager = sessionManager;
			load();
		}

		
		private void load()
		{
			final ISession[] s = _sessionManager.getConnectedSessions();
			if (s != null)
			{
				for (int i = 0; i < s.length; i++)
				{
					addSession(s[i]);
				}
			}
		}

		
		private void addSession(ISession session)
		{
			addElement(session);
		}

		
		private void removeSession(ISession session)
		{
			removeElement(session);
		}
	}

	
	private class MySessionListener extends SessionAdapter
	{
		
		private final SessionDropDownModel _model;

		
		private final SessionDropDown _sessionDropDown;

      
		MySessionListener(SessionDropDownModel model, SessionDropDown control)
		{
			super();
			_model = model;
			_sessionDropDown = control;
		}

		public void sessionConnected(SessionEvent evt)
		{
			final ISession session = evt.getSession();
         
			GUIUtils.processOnSwingEventThread(new Runnable()
			{
				public void run()
				{
					_model.addSession(session);
					_sessionDropDown.setEnabled(true);
				}
			});
		}

		public void sessionClosing(SessionEvent evt)
		{
			final ISession session = evt.getSession();
			GUIUtils.processOnSwingEventThread(new Runnable()
			{
				public void run()
				{
					_sessionDropDown._closing = true;
					_model.removeSession(session);
					if (_model.getSize() == 0)
					{
						_sessionDropDown.setEnabled(false);
					}
					_sessionDropDown._closing = false;
				}
			});

		}

      public void sessionActivated(SessionEvent evt)
      {
         final ISession session = evt.getSession();

         
         
         GUIUtils.processOnSwingEventThread(new Runnable()
         {
            public void run()
            {
               try
               {
                  _dontReactToSessionDropDownAction = true;
                  _sessionDropDown.setSelectedItem(session);
               }
               finally
               {
                  _dontReactToSessionDropDownAction = false;
               }
            }
         });
      }

   }
}

