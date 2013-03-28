
package net.sourceforge.squirrel_sql.plugins.smarttools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.smarttools.gui.ISmarttoolFrame;
import net.sourceforge.squirrel_sql.plugins.smarttools.gui.SmarttoolChangeValuesFrame;
import net.sourceforge.squirrel_sql.plugins.smarttools.gui.SmarttoolFindBadNullValuesFrame;
import net.sourceforge.squirrel_sql.plugins.smarttools.gui.SmarttoolMissingIndicesFrame;


public class SmarttoolsPlugin extends DefaultSessionPlugin {
	
	
	
	
	private final static ILogger log = LoggerController
			.createLogger(SmarttoolsPlugin.class);

	private static final StringManager stringManager = StringManagerFactory
			.getStringManager(SmarttoolsPlugin.class);

	
	private final int ST_SHEET_TYPE_FIND_VALUES = 1;
	private final int ST_SHEET_TYPE_CHANGE_VALUES = 2;
	private final int ST_SHEET_TYPE_MISSING_INICES = 3;

    
	public String getAuthor() {
		return "Michael Romankiewicz";
	}

    
	public String getDescriptiveName() {
		return "Smarttools Plugin";
	}

    
	public String getInternalName() {
		return "smarttools";
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

	
	public synchronized void initialize() throws PluginException {



	}

	public void unload() {
		super.unload();
	}

	
	public PluginSessionCallback sessionStarted(final ISession session) {
			try {
			
			
			IObjectTreeAPI objectTreeApi = session.getSessionInternalFrame()
					.getObjectTreeAPI();
			objectTreeApi.addToPopup(DatabaseObjectType.SESSION,
					getSmarttoolsMenu(getApplication(), session, true));

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
	}

	
	private interface i18n {
		String MENU_TITLE_SMARTTOOLS = stringManager
				.getString("smarttools.menu.title");
		String MENU_TITLE_SMARTTOOLS_FIND_VALUES = stringManager
				.getString("smarttools.menu.findvalues.title");
		String MENU_TITLE_SMARTTOOLS_CHANGE_VALUES = stringManager
				.getString("smarttools.menu.changevalues.title");
		String MENU_TITLE_SMARTTOOLS_MISSING_INDICES = stringManager
				.getString("smarttools.menu.missingindices.title");
	}

	
	private JMenu getSmarttoolsMenu(final IApplication application,
			ISession session, boolean forPopupMenu) {
		JMenu menu = new JMenu(i18n.MENU_TITLE_SMARTTOOLS);

		if (forPopupMenu) {
			menu.add(addMenuItem(application,
					i18n.MENU_TITLE_SMARTTOOLS_FIND_VALUES,
					ST_SHEET_TYPE_FIND_VALUES, session));

			menu.add(addMenuItem(application,
					i18n.MENU_TITLE_SMARTTOOLS_CHANGE_VALUES,
					ST_SHEET_TYPE_CHANGE_VALUES, session));

			menu.add(addMenuItem(application,
					i18n.MENU_TITLE_SMARTTOOLS_MISSING_INDICES,
					ST_SHEET_TYPE_MISSING_INICES, session));
		}
		
		return menu;
	}
	
	
	private JMenuItem addMenuItem(final IApplication application, final String title,
			final int sheetType, final ISession session) {
		JMenuItem menuItem = new JMenuItem(title);
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JInternalFrame frame = isInternalFrameUsed(application, sheetType);
				if (frame == null) {
					if (sheetType == ST_SHEET_TYPE_FIND_VALUES) {
						frame = new SmarttoolFindBadNullValuesFrame(session, title);
					} else if (sheetType == ST_SHEET_TYPE_CHANGE_VALUES) {
						frame = new SmarttoolChangeValuesFrame(session, title);
					} else if (sheetType == ST_SHEET_TYPE_MISSING_INICES) {
						frame = new SmarttoolMissingIndicesFrame(session, title);
					}
					application.getMainFrame().addInternalFrame(frame, true, null);
					frame.pack();
					if (frame instanceof SmarttoolFindBadNullValuesFrame) {
						frame.setSize(frame.getWidth(), 500);
					} else if (frame instanceof SmarttoolChangeValuesFrame) {
						frame.setSize(frame.getWidth(), 500);
					} else if (frame instanceof SmarttoolMissingIndicesFrame) {
						frame.setSize(frame.getWidth(), 500);
					} 
					GUIUtils.centerWithinDesktop(frame);
				} else {
					frame.setVisible(true);
					frame.moveToFront();
				}


				try {
					frame.setSelected(true);
					if (frame instanceof ISmarttoolFrame) {
						((ISmarttoolFrame)frame).setFocusToFirstEmptyInputField();
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
			if ((sheetType == ST_SHEET_TYPE_FIND_VALUES && frames[i] instanceof SmarttoolFindBadNullValuesFrame)
					|| (sheetType == ST_SHEET_TYPE_CHANGE_VALUES && frames[i] instanceof SmarttoolChangeValuesFrame)
					|| (sheetType == ST_SHEET_TYPE_MISSING_INICES && frames[i] instanceof SmarttoolMissingIndicesFrame)
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
			if (frames[i] instanceof SmarttoolFindBadNullValuesFrame
					|| frames[i] instanceof SmarttoolChangeValuesFrame
					|| frames[i] instanceof SmarttoolMissingIndicesFrame
				) {
				frames[i].dispose();
			}
		}
	}
}
