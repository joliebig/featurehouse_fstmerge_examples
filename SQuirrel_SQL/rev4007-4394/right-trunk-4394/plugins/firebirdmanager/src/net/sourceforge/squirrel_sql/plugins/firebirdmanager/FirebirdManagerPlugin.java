
package net.sourceforge.squirrel_sql.plugins.firebirdmanager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.sql.SQLException;

import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame.IMenuIDs;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.gui.FirebirdManagerBackupRestoreFrame;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.gui.FirebirdManagerCreateDatabaseFrame;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.gui.FirebirdManagerGlobalPreferencesTab;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.gui.FirebirdManagerGrantFrame;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.gui.FirebirdManagerRoleFrame;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.gui.FirebirdManagerUserManagerFrame;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.gui.IFirebirdManagerFrame;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.pref.PreferencesManager;

import org.firebirdsql.jdbc.FBDriver;


public class FirebirdManagerPlugin extends DefaultSessionPlugin {
	
	
	
	
	private final static ILogger log = LoggerController
			.createLogger(FirebirdManagerPlugin.class);

	private static final StringManager stringManager = StringManagerFactory
			.getStringManager(FirebirdManagerPlugin.class);

	
	private final int FB_SHEET_TYPE_CREATE = 1;
	private final int FB_SHEET_TYPE_BACKUP = 2;
	private final int FB_SHEET_TYPE_USER = 3;
	private final int FB_SHEET_TYPE_ROLE = 4;
	private final int FB_SHEET_TYPE_GRANT =5;

	
	private boolean jaybird2Driver = false; 

    
	public String getAuthor() {
		return "Michael Romankiewicz";
	}

    
	public String getDescriptiveName() {
		return "Firebird Manager Plugin";
	}

    
	public String getInternalName() {
		return "firebirdmanager";
	}

    
	public String getVersion() {
		return "1.0";
	}
	
    
    public String getHelpFileName()
    {
       return "readme.html";
    }    
    
    
    public String getChangeLogFileName()
    {
        return "changes.txt";
    }

    
    public String getLicenceFileName()
    {
        return "licence.txt";
    }

    
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels() {
		FirebirdManagerGlobalPreferencesTab tab = new FirebirdManagerGlobalPreferencesTab();
		return new IGlobalPreferencesPanel[] { tab };
	}

	
	public synchronized void initialize() throws PluginException {
		jaybird2Driver = isJaybird2DriverInstalled();
		PreferencesManager.initialize(this);

		IApplication application = getApplication();
		application.addToMenu(IMenuIDs.PLUGINS_MENU,
				getFirebirdManagerMenu(getApplication(), null, false));
	}

	public void unload() {
		super.unload();
		PreferencesManager.unload();
	}

	
	public PluginSessionCallback sessionStarted(final ISession session) {
		if (isFirebirdDB(session)) {
			try {
				
				
				IObjectTreeAPI objectTreeApi = session
						.getSessionInternalFrame().getObjectTreeAPI();
				objectTreeApi.addToPopup(DatabaseObjectType.SESSION,
						getFirebirdManagerMenu(getApplication(), session, true));

				return new PluginSessionCallback() {
					public void sqlInternalFrameOpened(
							SQLInternalFrame sqlInternalFrame, ISession sess) {
						
					}

					public void objectTreeInternalFrameOpened(
							ObjectTreeInternalFrame objectTreeInternalFrame,
							ISession sess) {
						
					}
				};
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			return null;
		}
	}

	
	private interface i18n {
		String MENU_TITLE_FIREBIRDMANAGER = stringManager
				.getString("firebirdmanager.menu.title");

		String MENU_TITLE_FIREBIRDMANAGER_CREATE_DATABASE = stringManager
				.getString("firebirdmanager.menu.createdatabase.title");

		String MENU_TITLE_FIREBIRDMANAGER_USER_MANAGER = stringManager
				.getString("firebirdmanager.menu.usermanager.title");

		String MENU_TITLE_FIREBIRDMANAGER_BACKUP_RESTORE_MANAGER = stringManager
				.getString("firebirdmanager.menu.backuprestoremanager.title");

		String MENU_TITLE_FIREBIRDMANAGER_ROLE_MANAGER = stringManager
				.getString("firebirdmanager.menu.rolemanager.title");

		String MENU_TITLE_FIREBIRDMANAGER_GRANT_MANAGER = stringManager
				.getString("firebirdmanager.menu.grantmanager.title");

		String ERROR_DRIVER_NOT_INSTALLED = stringManager
				.getString("firebirdmanager.error.driver.not.installed");
		String ERROR_GET_DATABASE_PRODUCT_NAME = stringManager
				.getString("firebirdmanager.error.get.database.product.name");
	}

	
	private JMenu getFirebirdManagerMenu(final IApplication application,
			ISession session, boolean forPopupMenu) {
		JMenu menu = new JMenu(i18n.MENU_TITLE_FIREBIRDMANAGER);

		if (jaybird2Driver) {
			menu.add(addMenuItem(application,
					i18n.MENU_TITLE_FIREBIRDMANAGER_BACKUP_RESTORE_MANAGER,
					FB_SHEET_TYPE_BACKUP, session));
			menu.add(addMenuItem(application,
					i18n.MENU_TITLE_FIREBIRDMANAGER_CREATE_DATABASE,
					FB_SHEET_TYPE_CREATE, session));
			menu.add(addMenuItem(application,
					i18n.MENU_TITLE_FIREBIRDMANAGER_USER_MANAGER,
					FB_SHEET_TYPE_USER, session));
		}

		if (forPopupMenu) {
			menu.add(addMenuItem(application,
					i18n.MENU_TITLE_FIREBIRDMANAGER_ROLE_MANAGER,
					FB_SHEET_TYPE_ROLE, session));

			menu.add(addMenuItem(application,
					i18n.MENU_TITLE_FIREBIRDMANAGER_GRANT_MANAGER,
					FB_SHEET_TYPE_GRANT, session));
		}
		
		return menu;
	}
	
	
	private JMenuItem addMenuItem(final IApplication application, String title,
			final int sheetType, final ISession session) {
		JMenuItem menuItem = new JMenuItem(title);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JInternalFrame frame = isInternalFrameUsed(application, sheetType);
				if (frame == null) {
					if (sheetType == FB_SHEET_TYPE_ROLE) {
						frame = new FirebirdManagerRoleFrame(session);
					} else if (sheetType == FB_SHEET_TYPE_GRANT) {
						frame = new FirebirdManagerGrantFrame(session);
					} else if (sheetType == FB_SHEET_TYPE_BACKUP){
						frame = new FirebirdManagerBackupRestoreFrame();
					} else if (sheetType == FB_SHEET_TYPE_CREATE){
						frame = new FirebirdManagerCreateDatabaseFrame();
					} else if (sheetType == FB_SHEET_TYPE_USER){
						frame = new FirebirdManagerUserManagerFrame();
					}
					application.getMainFrame().addInternalFrame(frame, true, null);
					frame.pack();
					if (frame instanceof FirebirdManagerBackupRestoreFrame) {
						frame.setSize(650, frame.getHeight());
					} else if (frame instanceof FirebirdManagerUserManagerFrame) {
						frame.setSize(frame.getWidth(), 500);
					}
					GUIUtils.centerWithinDesktop(frame);
				} else {
					frame.setVisible(true);
					frame.moveToFront();
				}


				try {
					frame.setSelected(true);
					if (frame instanceof IFirebirdManagerFrame) {
						((IFirebirdManagerFrame)frame).setFocusToFirstEmptyInputField();
					}
				} catch (PropertyVetoException e) {
					log.error(e.getLocalizedMessage());
				}
			}
		});
		return menuItem;
	}

	private JInternalFrame isInternalFrameUsed(IApplication application,
			int sheetType) {
		JInternalFrame[] frames = application.getMainFrame().getDesktopPane()
				.getAllFrames();
		for (int i = 0; i < frames.length; i++) {
			if ((sheetType == FB_SHEET_TYPE_CREATE && frames[i] instanceof FirebirdManagerCreateDatabaseFrame)
					|| (sheetType == FB_SHEET_TYPE_USER && frames[i] instanceof FirebirdManagerUserManagerFrame)
					|| (sheetType == FB_SHEET_TYPE_BACKUP && frames[i] instanceof FirebirdManagerBackupRestoreFrame)
					|| (sheetType == FB_SHEET_TYPE_GRANT && frames[i] instanceof FirebirdManagerGrantFrame)
					|| (sheetType == FB_SHEET_TYPE_ROLE && frames[i] instanceof FirebirdManagerRoleFrame)
					) {
				return frames[i];
			}
		}
		return null;
	}

	@Override
	public void sessionEnding(ISession session) {
		super.sessionEnding(session);
		JInternalFrame[] frames = session.getApplication().getMainFrame().getDesktopPane()
				.getAllFrames();
		for (int i = 0; i < frames.length; i++) {
			if (frames[i] instanceof FirebirdManagerGrantFrame
				|| frames[i] instanceof FirebirdManagerRoleFrame) {
				frames[i].dispose();
			}
		}
	}

	
	private boolean isJaybird2DriverInstalled() {
		try {
			Class.forName("org.firebirdsql.jdbc.FBDriver");
			FBDriver fb = new FBDriver();
			return fb.getMajorVersion() >= 2;
		} catch (ClassNotFoundException e) {
			log.error(i18n.ERROR_DRIVER_NOT_INSTALLED);
			JOptionPane.showMessageDialog(null, i18n.ERROR_DRIVER_NOT_INSTALLED);
			return false;
		}
	}

	
	private boolean isFirebirdDB(ISession session) {
		try {
			return session.getSQLConnection().getSQLMetaData()
					.getDatabaseProductName().toLowerCase().startsWith(
							"firebird");
		} catch (SQLException e) {
			log.error(i18n.ERROR_GET_DATABASE_PRODUCT_NAME);
			JOptionPane.showMessageDialog(null, i18n.ERROR_GET_DATABASE_PRODUCT_NAME);
		}
		return false;
	}
}
