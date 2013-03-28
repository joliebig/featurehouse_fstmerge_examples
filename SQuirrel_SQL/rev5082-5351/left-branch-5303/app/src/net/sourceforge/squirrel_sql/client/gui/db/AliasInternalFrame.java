package net.sourceforge.squirrel_sql.client.gui.db;


import static net.sourceforge.squirrel_sql.client.preferences.PreferenceType.ALIAS_DEFINITIONS;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.mainframe.action.AliasPropertiesCommand;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasCommand;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifierFactory;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.IObjectCacheChangeListener;
import net.sourceforge.squirrel_sql.fw.util.ObjectCacheChangeEvent;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

@SuppressWarnings("serial")
public class AliasInternalFrame extends DialogWidget
{
   
	public interface IMaintenanceType
	{
		
		int NEW = 1;

		
		int MODIFY = 2;

		
		int COPY = 3;
	}
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AliasInternalFrame.class);

	
	private static final int COLUMN_COUNT = 25;

	
	private static final ILogger s_log =
		LoggerController.createLogger(AliasInternalFrame.class);

	
	transient private final IApplication _app;

	
	private final ISQLAlias _sqlAlias;

	
	private final JLabel _titleLbl = new JLabel();

	
	private final int _maintType;

	
	transient private DriversCacheListener _driversCacheLis;

	
	private final JTextField _aliasName = new JTextField();

	
	private DriversCombo _drivers;

	
	private final JTextField _url = new JTextField();

	
	private final JTextField _userName = new JTextField();

	
	private final JPasswordField _password = new JPasswordField();

	
	private final JCheckBox _autoLogonChk = new JCheckBox(s_stringMgr.getString("AliasInternalFrame.autologon"));

	
	private final JCheckBox _connectAtStartupChk = new JCheckBox(s_stringMgr.getString("AliasInternalFrame.connectatstartup"));




	
	private final JButton _aliasPropsBtn = new JButton(s_stringMgr.getString("AliasInternalFrame.props"));

	


	
	AliasInternalFrame(IApplication app, ISQLAlias sqlAlias, int maintType)
	{
		super("", true, app);
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		if (sqlAlias == null)
		{
			throw new IllegalArgumentException("ISQLAlias == null");
		}
		if (maintType < IMaintenanceType.NEW
			|| maintType > IMaintenanceType.COPY)
		{
            
			final String msg =
                s_stringMgr.getString("AliasInternalFrame.illegalValue",
                                      Integer.valueOf(maintType));
			throw new IllegalArgumentException(msg);
		}

		_app = app;
		_sqlAlias = sqlAlias;
		_maintType = maintType;
		createUserInterface();
		loadData();
		pack();
	}

	
	public void dispose()
	{
		if (_driversCacheLis != null)
		{
			_app.getDataCache().removeDriversListener(_driversCacheLis);
			_driversCacheLis = null;
		}
		super.dispose();
	}

	
	public void setTitle(String title)
	{
		super.setTitle(title);
		_titleLbl.setText(title);
	}

	
	ISQLAlias getSQLAlias()
	{
		return _sqlAlias;
	}

	
	private void loadData()
	{
		_aliasName.setText(_sqlAlias.getName());
		_userName.setText(_sqlAlias.getUserName());

		_password.setText(_sqlAlias.getPassword());

		_autoLogonChk.setSelected(_sqlAlias.isAutoLogon());
		_connectAtStartupChk.setSelected(_sqlAlias.isConnectAtStartup());
		

		if (_maintType != IMaintenanceType.NEW)
		{
			_drivers.setSelectedItem(_sqlAlias.getDriverIdentifier());
			_url.setText(_sqlAlias.getUrl());
		}
		else
		{
			final ISQLDriver driver = _drivers.getSelectedDriver();
			if (driver != null)
			{
				_url.setText(driver.getUrl());
			}
		}
	}

	private void performClose()
	{
		dispose();
	}

	
	private void performOk()
	{
		try
		{
			applyFromDialog(_sqlAlias);
			if (_maintType == IMaintenanceType.NEW
				|| _maintType == IMaintenanceType.COPY)
			{
				_app.getDataCache().addAlias(_sqlAlias);
			}
         _app.savePreferences(ALIAS_DEFINITIONS);
			dispose();
		}
		catch (ValidationException ex)
		{
			_app.showErrorDialog(ex);
		}
		catch (DuplicateObjectException ex)
		{
			_app.showErrorDialog(ex);
		}
	}

	private void applyFromDialog(ISQLAlias alias) throws ValidationException
	{
		ISQLDriver driver = _drivers.getSelectedDriver();
		if (driver == null)
		{
			throw new ValidationException(s_stringMgr.getString("AliasInternalFrame.error.nodriver"));
		}
		alias.setName(_aliasName.getText().trim());
		alias.setDriverIdentifier(_drivers.getSelectedDriver().getIdentifier());
		alias.setUrl(_url.getText().trim());
		alias.setUserName(_userName.getText().trim());

		StringBuffer buf = new StringBuffer();
		buf.append(_password.getPassword());
		alias.setPassword(buf.toString());

		alias.setAutoLogon(_autoLogonChk.isSelected());
		alias.setConnectAtStartup(_connectAtStartupChk.isSelected());


	}

	private void showNewDriverDialog()
	{
		_app.getWindowManager().showNewDriverInternalFrame();
	}

	private void showDriverPropertiesDialog()
	{
		try
		{
         applyFromDialog(_sqlAlias);
         new AliasPropertiesCommand(_sqlAlias, _app).execute();
		}
		catch (Exception ex)
		{
			_app.showErrorDialog(ex);
		}
	}

	
	private void createUserInterface()
	{
		setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

		

      makeToolWindow(true);

		String winTitle; 
		if (_maintType == IMaintenanceType.MODIFY)
		{
			winTitle = s_stringMgr.getString("AliasInternalFrame.changealias",
											_sqlAlias.getName());
		}
		else
		{
			winTitle = s_stringMgr.getString("AliasInternalFrame.addalias");
		}
		setTitle(winTitle);

		_aliasName.setColumns(COLUMN_COUNT);
		_url.setColumns(COLUMN_COUNT);
		_userName.setColumns(COLUMN_COUNT);
		_password.setColumns(COLUMN_COUNT);

		
		
		
		
		
		
		final JPanel contentPane = new JPanel();
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		super.setContentPane(contentPane);

		GridBagConstraints gbc = new GridBagConstraints();
		contentPane.setLayout(new GridBagLayout());

		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1;

		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 10, 5, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		contentPane.add(_titleLbl, gbc);

		
		++gbc.gridy;
		gbc.insets = new Insets(0, 10, 5, 10);
		contentPane.add(new JSeparator(), gbc);

		contentPane.add(createDataEntryPanel(), gbc);

		
		gbc.weighty = 0;
		++gbc.gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 10, 5, 10);
		contentPane.add(new JSeparator(), gbc);

		gbc.insets = new Insets(0, 0, 0, 0);

		
		++gbc.gridy;
		contentPane.add(createButtonsPanel(), gbc);

		_driversCacheLis = new DriversCacheListener();
		_app.getDataCache().addDriversListener(_driversCacheLis);

      AbstractAction closeAction = new AbstractAction()
      {
         public void actionPerformed(ActionEvent actionEvent)
         {
            performClose();
         }
      };
      KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
      getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "CloseAction");
      getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "CloseAction");
      getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "CloseAction");
      getRootPane().getActionMap().put("CloseAction", closeAction);
      

   }

	private JPanel createDataEntryPanel()
	{
		_aliasPropsBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				showDriverPropertiesDialog();
			}

		});










		JPanel pnl = new JPanel(new GridBagLayout());

		GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      pnl.add(new JLabel(s_stringMgr.getString("AliasInternalFrame.name"), SwingConstants.RIGHT), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
		pnl.add(_aliasName, gbc);


      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      pnl.add(new JLabel(s_stringMgr.getString("AliasInternalFrame.driver"), SwingConstants.RIGHT), gbc);


      _drivers = new DriversCombo();
		_drivers.addItemListener(new DriversComboItemListener());

		final Box driverPnl = Box.createHorizontalBox();
		driverPnl.add(_drivers);
		driverPnl.add(Box.createHorizontalStrut(5));
		JButton newDriverBtn = new JButton(s_stringMgr.getString("AliasInternalFrame.new"));
		newDriverBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				showNewDriverDialog();
			}
		});
		driverPnl.add(newDriverBtn);

      gbc = new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
		pnl.add(driverPnl, gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
		pnl.add(new JLabel(s_stringMgr.getString("AliasInternalFrame.url"), SwingConstants.RIGHT), gbc);

      gbc = new GridBagConstraints(1,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
		pnl.add(_url, gbc);

      gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
		pnl.add(new JLabel(s_stringMgr.getString("AliasInternalFrame.username"), SwingConstants.RIGHT), gbc);

      gbc = new GridBagConstraints(1,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
		pnl.add(_userName, gbc);

      gbc = new GridBagConstraints(0,4,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
		pnl.add(new JLabel(s_stringMgr.getString("AliasInternalFrame.password"), SwingConstants.RIGHT), gbc);

      gbc = new GridBagConstraints(1,4,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
		pnl.add(_password, gbc);

      gbc = new GridBagConstraints(0,5,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
		pnl.add(_autoLogonChk, gbc);

      gbc = new GridBagConstraints(1,5,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
		pnl.add(_connectAtStartupChk, gbc);


      gbc = new GridBagConstraints(1,6,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      _aliasPropsBtn.setIcon(_app.getResources().getIcon(SquirrelResources.IImageNames.ALIAS_PROPERTIES));
      pnl.add(_aliasPropsBtn, gbc);

      gbc = new GridBagConstraints(0,7,2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
		pnl.add(new JLabel(s_stringMgr.getString("AliasInternalFrame.cleartext")), gbc);


      
      gbc = new GridBagConstraints(0,8,2,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0,0);
		pnl.add(new JPanel(), gbc);

		return pnl;
	}

	private JPanel createButtonsPanel()
	{
		JPanel pnl = new JPanel();

		JButton okBtn = new JButton(s_stringMgr.getString("AliasInternalFrame.ok"));
		okBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performOk();
			}
		});
		JButton closeBtn = new JButton(s_stringMgr.getString("AliasInternalFrame.close"));
		closeBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				performClose();
			}
		});

		JButton testBtn = new JButton(s_stringMgr.getString("AliasInternalFrame.test"));
		testBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				final DataCache cache = _app.getDataCache();
				final IIdentifierFactory factory = IdentifierFactory.getInstance();
				final SQLAlias testAlias = cache.createAlias(factory.createIdentifier());
				try
				{
					applyFromDialog(testAlias);
					ConnectionCallBack cb = new ConnectionCallBack(_app, testAlias);
					ConnectToAliasCommand cmd = new ConnectToAliasCommand(_app,
													testAlias, false, cb);
					cmd.execute();
				}
				catch (ValidationException ex)
				{
					_app.showErrorDialog(ex);
				}
			}
		});

		pnl.add(okBtn);
		pnl.add(closeBtn);
		pnl.add(testBtn);

		GUIUtils.setJButtonSizesTheSame(new JButton[] { okBtn, closeBtn, testBtn });
		getRootPane().setDefaultButton(okBtn);

		return pnl;
	}

	private final class DriversComboItemListener implements ItemListener
	{
		public void itemStateChanged(ItemEvent evt)
		{
			ISQLDriver driver = (ISQLDriver) evt.getItem();
			if (driver != null)
			{
				_url.setText(driver.getUrl());
			}
		}
	}

	
	private final class DriversCombo extends JComboBox
	{
		private Map<IIdentifier, ISQLDriver> _map = 
            new HashMap<IIdentifier, ISQLDriver>();
        SquirrelPreferences prefs = _app.getSquirrelPreferences();
		@SuppressWarnings("unchecked")
        DriversCombo()
		{
			super();
			SquirrelResources res = _app.getResources();
			setRenderer(new DriverListCellRenderer(res.getIcon("list.driver.found"),
											res.getIcon("list.driver.notfound")));
			List<ISQLDriver> list = new ArrayList<ISQLDriver>();
			for (Iterator it = AliasInternalFrame.this._app.getDataCache().drivers();
					it.hasNext();)
			{
				ISQLDriver sqlDriver = ((ISQLDriver) it.next());
                if (prefs.getShowLoadedDriversOnly() 
                        && !sqlDriver.isJDBCDriverClassLoaded()) 
                {
                    continue;
                }
                _map.put(sqlDriver.getIdentifier(), sqlDriver);
                list.add(sqlDriver);                    
			}
			Collections.sort(list, new DriverComparator());
			for (Iterator it = list.iterator(); it.hasNext();)
			{
				addItem(it.next());
			}
		}

		void setSelectedItem(IIdentifier id)
		{
			super.setSelectedItem(_map.get(id));
		}

		ISQLDriver getSelectedDriver()
		{
			return (ISQLDriver) getSelectedItem();
		}

		private class DriverComparator implements Comparator<ISQLDriver>,
		                                          Serializable
		{
			public int compare(ISQLDriver o1, ISQLDriver o2)
			{
				return o1.toString().compareToIgnoreCase(o2.toString());
			}

		}
	}

	private final class ConnectionCallBack
		extends ConnectToAliasCallBack
	{
		private ConnectionCallBack(IApplication app, SQLAlias alias)
		{
			super(app, alias);
		}

		
		public void connected(ISQLConnection conn)
		{
			try
			{
				conn.close();
			}
			catch (Throwable th)
			{
				String msg = s_stringMgr.getString("AliasInternalFrame.error.errorclosingconn");
				s_log.error(msg, th);
				_app.showErrorDialog(msg + ": " + th.toString());
			}

			AliasInternalFrame.this.showOk(s_stringMgr.getString("AliasInternalFrame.connsuccess"));

         if(getAlias().isAutoLogon())
         {
            
            
            
            _userName.setText(getAlias().getUserName());
            _password.setText(getAlias().getPassword());
         }
      }

		
		public void sessionCreated(ISession session)
		{
            
			s_log.error(s_stringMgr.getString("AliasInternalFrame.error.sessioncreation"));
		}
	}

   
	private final class DriversCacheListener implements IObjectCacheChangeListener
	{
		public void objectAdded(ObjectCacheChangeEvent evt)
		{
			_drivers.addItem(evt.getObject());
		}
		public void objectRemoved(ObjectCacheChangeEvent evt)
		{
			_drivers.removeItem(evt.getObject());
		}
	}
}
