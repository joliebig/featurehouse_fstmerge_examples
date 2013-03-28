package net.sourceforge.squirrel_sql.plugins.codecompletion;

import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.properties.ISessionPropertiesPanel;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.plugins.codecompletion.prefs.CodeCompletionPreferences;
import net.sourceforge.squirrel_sql.plugins.codecompletion.prefs.CodeCompletionPreferencesController;

import javax.swing.*;
import java.io.File;
import java.util.Iterator;


public class CodeCompletionPlugin extends DefaultSessionPlugin
{
	
    @SuppressWarnings("unused")
	private final static ILogger
			s_log = LoggerController.createLogger(CodeCompletionPlugin.class);


	
	private Resources _resources;
	private static final String PREFS_FILE_NAME = "codecompletionprefs.xml";

	private CodeCompletionPreferences _newSessionPrefs;
	public static final String PLUGIN_OBJECT_PREFS_KEY = "codecompletionprefs";

   
	public String getInternalName()
	{
		return "codecompletion";
	}

	
	public String getDescriptiveName()
	{
		return "SQL Entry Code Completion";
	}

	
	public String getVersion()
	{
		return "1.0";
	}


	
	public String getContributors()
	{
		return "Christian Sell";
	}

	
	public String getAuthor()
	{
		return "Gerd Wagner";
	}

	
	public String getChangeLogFileName()
	{
		return "changes.txt";
	}

	
	public String getHelpFileName()
	{
		return "readme.html";
	}

	
	public String getLicenceFileName()
	{
		return "licence.txt";
	}

	
	public void initialize() throws PluginException
	{
		_resources = new Resources(this);
		loadPrefs();
	}

	public void unload()
	{
		savePrefs();
	}

	private void savePrefs()
	{
		try
		{
			File prefsFile = new File(getPluginUserSettingsFolder(), PREFS_FILE_NAME);
			final XMLBeanWriter wtr = new XMLBeanWriter(_newSessionPrefs);
			wtr.save(prefsFile);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}



	private void loadPrefs()
	{
		try
		{
			_newSessionPrefs = new CodeCompletionPreferences();
			File prefsFile = new File(getPluginUserSettingsFolder(), PREFS_FILE_NAME);
			if(prefsFile.exists())
			{
				XMLBeanReader reader = new XMLBeanReader();
				reader.load(prefsFile, getClass().getClassLoader());

				Iterator<?> it = reader.iterator();

				if (it.hasNext())
				{
					_newSessionPrefs = (CodeCompletionPreferences) it.next();
				}

			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}


	
	public INewSessionPropertiesPanel[] getNewSessionPropertiesPanels()
	{
		return new INewSessionPropertiesPanel[]
		{
			new CodeCompletionPreferencesController(_newSessionPrefs)
		};
	}

	
	public ISessionPropertiesPanel[] getSessionPropertiesPanels(ISession session)
	{
		CodeCompletionPreferences sessionPrefs = (CodeCompletionPreferences)session.getPluginObject(this, PLUGIN_OBJECT_PREFS_KEY);

		return new ISessionPropertiesPanel[]
		{
			new CodeCompletionPreferencesController(sessionPrefs)
		};
	}

	public void sessionCreated(ISession session)
	{
		CodeCompletionPreferences prefs = (CodeCompletionPreferences) Utilities.cloneObject(_newSessionPrefs, getClass().getClassLoader());
		session.putPluginObject(this, PLUGIN_OBJECT_PREFS_KEY, prefs);
	}


   public boolean allowsSessionStartedInBackground()
   {
      return true;
   }

   
	public PluginSessionCallback sessionStarted(final ISession session)
	{
		ISQLPanelAPI sqlPaneAPI = session.getSessionSheet().getSQLPaneAPI();
      initCodeCompletionSqlEditor(sqlPaneAPI, session);

      initCodeCompletionObjectTreeFind(session, session.getSessionSheet().getObjectTreePanel());

      PluginSessionCallback ret = new PluginSessionCallback()
		{
			public void sqlInternalFrameOpened(final SQLInternalFrame sqlInternalFrame, final ISession sess)
			{
            initCodeCompletionSqlEditor(sqlInternalFrame.getSQLPanelAPI(), sess);
			}

			public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
			{
            initCodeCompletionObjectTreeFind(sess, objectTreeInternalFrame.getObjectTreePanel());
			}
		};

		return ret;
	}

	private void initCodeCompletionSqlEditor(final ISQLPanelAPI sqlPaneAPI, final ISession session)
	{
      GUIUtils.processOnSwingEventThread(new Runnable()
      {
         public void run()
         {
            CodeCompletionInfoCollection c = new CodeCompletionInfoCollection(session, CodeCompletionPlugin.this);

            CompleteCodeAction cca =
               new CompleteCodeAction(session.getApplication(),
                  CodeCompletionPlugin.this,
                  sqlPaneAPI.getSQLEntryPanel(),
                  session,
                  c,
                  null);

            JMenuItem item = sqlPaneAPI.addToSQLEntryAreaMenu(cca);

            _resources.configureMenuItem(cca, item);

            JComponent comp = sqlPaneAPI.getSQLEntryPanel().getTextComponent();
            comp.registerKeyboardAction(cca, _resources.getKeyStroke(cca), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

            sqlPaneAPI.addToToolsPopUp("completecode", cca);
         }

      });
   }

	private void initCodeCompletionObjectTreeFind(final ISession session, final ObjectTreePanel objectTreePanel)
	{
      GUIUtils.processOnSwingEventThread(new Runnable()
      {
         public void run()
         {
            ISQLEntryPanel findEntryPanel = objectTreePanel.getFindController().getFindEntryPanel();

            CodeCompletionInfoCollection c = new CodeCompletionInfoCollection(session, CodeCompletionPlugin.this);

            CompleteCodeAction cca =
               new CompleteCodeAction(session.getApplication(),
                  CodeCompletionPlugin.this,
                  findEntryPanel,
                  session,
                  c,
                  objectTreePanel);


            JComponent comp = findEntryPanel.getTextComponent();
            comp.registerKeyboardAction(cca, _resources.getKeyStroke(cca), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
         }

      });
   }

	
	public PluginResources getResources()
	{
		return _resources;
	}

}
