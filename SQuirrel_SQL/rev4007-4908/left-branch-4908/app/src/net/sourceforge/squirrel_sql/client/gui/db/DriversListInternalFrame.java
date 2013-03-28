package net.sourceforge.squirrel_sql.client.gui.db;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.beans.PropertyVetoException;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import net.sourceforge.squirrel_sql.fw.gui.BasePopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.mainframe.action.CopyDriverAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CreateDriverAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.DeleteDriverAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.InstallDefaultDriversAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ModifyDriverAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ModifyDriverCommand;
import net.sourceforge.squirrel_sql.client.mainframe.action.ShowDriverWebsiteAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ShowLoadedDriversOnlyAction;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;

public class DriversListInternalFrame extends BaseListInternalFrame
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(BaseListInternalFrame.class);

	
	private IApplication _app;

	
	private UserInterfaceFactory _uiFactory;

	
	public DriversListInternalFrame(IApplication app, DriversList list)
	{
		super(new UserInterfaceFactory(app, list));
		_app = app;
		_uiFactory = (UserInterfaceFactory)getUserInterfaceFactory();
		_uiFactory.setDriversListInternalFrame(this);

		
		
		_uiFactory.enableDisableActions();

      addVetoableChangeListener(new VetoableChangeListener()
      {
         public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException
         {
            if(IS_CLOSED_PROPERTY.equals(evt.getPropertyName()) && Boolean.TRUE.equals(evt.getNewValue()))
            {
               
               throw new PropertyVetoException(s_stringMgr.getString("DriversListInternalFrame.error.ctrlF4key"), evt);
            }
         }
      });

      addInternalFrameListener(new InternalFrameAdapter()
      {
         public void internalFrameClosing(InternalFrameEvent e)
         {
            nowVisible(false);
         }
      });



      _app.getSquirrelPreferences().addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				final String propName = evt != null ? evt.getPropertyName() : null;
				_uiFactory.propertiesChanged(propName);
			}
		});
	}


   public void nowVisible(boolean b)
   {
      _app.getMainFrame().setEnabledDriversMenu(b);
   }


   
	public int getSelectedIndex()
	{
		return _uiFactory._driversList.getSelectedIndex();
	}

	private final static class UserInterfaceFactory
		implements BaseListInternalFrame.IUserInterfaceFactory
	{
		private IApplication _app;
		private DriversList _driversList;
		private ToolBar _tb;
		private BasePopupMenu _pm = new BasePopupMenu();
		private DriversListInternalFrame _tw;

		UserInterfaceFactory(IApplication app, DriversList list)
		{
			super();
			if (app == null)
			{
				throw new IllegalArgumentException("IApplication == null");
			}
			if (list == null)
			{
				throw new IllegalArgumentException("DriversList == null");
			}

			_app = app;

			_driversList = list;

			final ActionCollection actions = app.getActionCollection();
			_pm.add(actions.get(CreateDriverAction.class));
			_pm.addSeparator();
			_pm.add(actions.get(ModifyDriverAction.class));
			_pm.add(actions.get(CopyDriverAction.class));
            _pm.add(actions.get(ShowDriverWebsiteAction.class));
            _pm.addSeparator();
			_pm.add(actions.get(DeleteDriverAction.class));
			_pm.addSeparator();
		}

		public ToolBar getToolBar()
		{
			return _tb;
		}

		public BasePopupMenu getPopupMenu()
		{
			return _pm;
		}

		public JList getList()
		{
			return _driversList;
		}

		public String getWindowTitle()
		{
			return s_stringMgr.getString("DriversListInternalFrame.windowtitle");
		}

		public ICommand getDoubleClickCommand()
		{
			ICommand cmd = null;
			ISQLDriver driver = _driversList.getSelectedDriver();
			if (driver != null)
			{
				cmd = new ModifyDriverCommand(_app, driver);
			}
			return cmd;
		}

		
		public void enableDisableActions()
		{
			boolean enable = false;
			try
			{
				enable = _driversList.getSelectedDriver() != null;
			}
			catch (Exception ignore)
			{
				
				
				
				
				
				
				
			}

			final ActionCollection actions = _app.getActionCollection();
			actions.get(CopyDriverAction.class).setEnabled(enable);
			actions.get(DeleteDriverAction.class).setEnabled(enable);
			actions.get(ModifyDriverAction.class).setEnabled(enable);
            actions.get(ShowDriverWebsiteAction.class).setEnabled(enable);
		}

		void setDriversListInternalFrame(DriversListInternalFrame tw)
		{
			_tw = tw;
			propertiesChanged(null);
		}

		public void propertiesChanged(String propName)
		{
			if (propName == null ||
				propName.equals(SquirrelPreferences.IPropertyNames.SHOW_DRIVERS_TOOL_BAR))
			{
				boolean show = _app.getSquirrelPreferences().getShowDriversToolBar();
				if (show)
				{
					createToolBar();
				}
				else
				{
					_tb = null;
				}
				_tw.setToolBar(getToolBar());
			}
		}

		private void createToolBar()
		{
			_tb = new ToolBar();
			_tb.setUseRolloverButtons(true);
			_tb.setFloatable(false);

			final JLabel lbl = new JLabel(getWindowTitle(), SwingConstants.CENTER);
			lbl.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			_tb.add(lbl, 0);

			final ActionCollection actions = _app.getActionCollection();
			_tb.add(actions.get(CreateDriverAction.class));
			_tb.add(actions.get(ModifyDriverAction.class));
			_tb.add(actions.get(CopyDriverAction.class));
            _tb.add(actions.get(ShowDriverWebsiteAction.class));
			_tb.add(actions.get(DeleteDriverAction.class));
			_tb.addSeparator();
			_tb.add(actions.get(InstallDefaultDriversAction.class));
			_tb.addSeparator();


			final Action act = actions.get(ShowLoadedDriversOnlyAction.class);
			final JToggleButton btn = new JToggleButton(act);
			final boolean show = _app.getSquirrelPreferences().getShowLoadedDriversOnly();
			btn.setSelected(show);
			btn.setText(null);
			_tb.add(btn);
		}
	}
}
