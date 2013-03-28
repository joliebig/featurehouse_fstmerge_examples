

package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class SQLBookmarkPlugin extends DefaultSessionPlugin
{

   
   private final static ILogger s_log = 
       LoggerController.createLogger(SQLBookmarkPlugin.class);  

   private ArrayList<ISQLPanelAPI> _sqlPanelAPIsListeningForBookmarks = 
       new ArrayList<ISQLPanelAPI>();


   private static final String BOOKMARKS_PROPS_FILE = "bookmarks.properties";
   static final String BOOKMARK_PROP_DEFAULT_MARKS_IN_POPUP = "squirrelMarksInPopup";
   private Properties _boomarkProps;

   private interface IMenuResourceKeys
   {
      String BOOKMARKS = "bookmarks";
   }

   private static String RESOURCE_PATH =
      "net.sourceforge.squirrel_sql.plugins.sqlbookmark.sqlbookmark";

   private static ILogger logger =
      LoggerController.createLogger(SQLBookmarkPlugin.class);

   
   private File pluginAppFolder;


   private PluginResources resources;

   
   private JMenu menu;

   
   private BookmarkManager bookmarkManager;

   
   public String getVersion()
   {
      return "2.0.1";
   }

   
   public String getAuthor()
   {
      return "Joseph Mocker";
   }

   public String getContributors()
   {
      return "Gerd Wagner";
   }


   
   public String getInternalName()
   {
      return "sqlbookmark";
   }

   
   public String getDescriptiveName()
   {
      return "SQL Bookmark Plugin";
   }

   
   public String getHelpFileName()
   {
      return "readme.html";
   }

   
   public String getLicenceFileName()
   {
      return "licence.txt";
   }

   
   public String getChangeLogFileName()
   {
      return "changes.txt";
   }

   
   protected PluginResources getResources()
   {
      return resources;
   }

   
   protected String getResourceString(String name)
   {
      return resources.getString(name);
   }

   
   BookmarkManager getBookmarkManager()
   {
      return bookmarkManager;
   }

   
   protected void setBookmarkManager(BookmarkManager bookmarks)
   {
      this.bookmarkManager = bookmarks;
   }


   public Object getExternalService()
   {
      return new BoomarksExternalServiceImpl(this);
   }

   
   public synchronized void initialize() throws PluginException
   {
      super.initialize();

      IApplication app = getApplication();

      
      
      try
      {
         pluginAppFolder = getPluginAppSettingsFolder();
      }
      catch (IOException ex)
      {
         throw new PluginException(ex);
      }

      
      resources = new SQLBookmarkResources(RESOURCE_PATH, this);

      bookmarkManager = new BookmarkManager(this);
      
      try
      {
         bookmarkManager.load();
      }
      catch (IOException e)
      {
         if (!(e instanceof FileNotFoundException))
         {
            logger.error("Problem loading bookmarkManager", e);
         }
      }

      ActionCollection coll = app.getActionCollection();
      coll.add(new AddBookmarkAction(app, resources, this));
      coll.add(new EditBookmarksAction(app, resources, this));
      coll.add(new RunBookmarkAction(app, resources, this));
      createMenu();

      rebuildMenu();
   }

   public boolean allowsSessionStartedInBackground()
   {
      return true;
   }

   public PluginSessionCallback sessionStarted(final ISession session)
   {
      GUIUtils.processOnSwingEventThread(new Runnable()
      {
         public void run()
         {
            addBookmarkAction(session);
         }
      });

      PluginSessionCallback ret = new PluginSessionCallback()
      {
         public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
         {
            ActionCollection coll = getApplication().getActionCollection();
            sqlInternalFrame.addSeparatorToToolbar();
            sqlInternalFrame.addToToolbar(coll.get(AddBookmarkAction.class));
            sqlInternalFrame.addToToolbar(coll.get(EditBookmarksAction.class));
            sqlInternalFrame.addToToolsPopUp("bookmarkadd", coll.get(AddBookmarkAction.class));
            sqlInternalFrame.addToToolsPopUp("bookmarkedit", coll.get(EditBookmarksAction.class));

            ISQLPanelAPI sqlPaneAPI = sqlInternalFrame.getSQLPanelAPI();
            CompleteBookmarkAction cba = new CompleteBookmarkAction(sess.getApplication(), resources, sqlPaneAPI.getSQLEntryPanel(), SQLBookmarkPlugin.this);
            JMenuItem item = sqlPaneAPI.addToSQLEntryAreaMenu(cba);
            resources.configureMenuItem(cba, item);
            JComponent comp = sqlPaneAPI.getSQLEntryPanel().getTextComponent();
            comp.registerKeyboardAction(cba, resources.getKeyStroke(cba), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            sqlInternalFrame.addToToolsPopUp("bookmarkselect", cba);
         }

         public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
         {
         }
      };
      return ret;
   }

   private void addBookmarkAction(ISession session)
   {
      ActionCollection coll = getApplication().getActionCollection();
      session.addSeparatorToToolbar();
      session.addToToolbar(coll.get(AddBookmarkAction.class));
      session.addToToolbar(coll.get(EditBookmarksAction.class));
      session.getSessionInternalFrame().addToToolsPopUp("bookmarkadd", coll.get(AddBookmarkAction.class));
      session.getSessionInternalFrame().addToToolsPopUp("bookmarkedit", coll.get(EditBookmarksAction.class));

      ISQLPanelAPI sqlPaneAPI = session.getSessionInternalFrame().getSQLPanelAPI();
      CompleteBookmarkAction cba =
         new CompleteBookmarkAction(session.getApplication(),
            resources,
            sqlPaneAPI.getSQLEntryPanel(),
            SQLBookmarkPlugin.this);
      JMenuItem item = sqlPaneAPI.addToSQLEntryAreaMenu(cba);
      resources.configureMenuItem(cba, item);
      JComponent comp = sqlPaneAPI.getSQLEntryPanel().getTextComponent();
      comp.registerKeyboardAction(cba,
         resources.getKeyStroke(cba),
         JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
      session.getSessionInternalFrame().addToToolsPopUp("bookmarkselect", cba);
   }

   
   protected void rebuildMenu()
   {
      ActionCollection coll = getApplication().getActionCollection();

      menu.removeAll();
      resources.addToMenu(coll.get(AddBookmarkAction.class), menu);
      menu.add(new JSeparator());

      for (Iterator<Bookmark> i = bookmarkManager.iterator(); i.hasNext();)
      {
         Object o = i.next();
         Bookmark bookmark = (Bookmark) o;

         addBookmarkItem(bookmark);
      }

      String defaultMarksInPopup =
         getBookmarkProperties().getProperty(SQLBookmarkPlugin.BOOKMARK_PROP_DEFAULT_MARKS_IN_POPUP, "" + false);

      if(Boolean.valueOf(defaultMarksInPopup).booleanValue())
      {
         Bookmark[] defaultBookmarks = DefaultBookmarksFactory.getDefaultBookmarks();

         for (int i = 0; i < defaultBookmarks.length; i++)
         {
            addBookmarkItem(defaultBookmarks[i]);
         }
      }

   }




   
   private void createMenu()
   {
      IApplication app = getApplication();

      menu = resources.createMenu(IMenuResourceKeys.BOOKMARKS);

      app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, menu);
   }

   
   protected void addBookmarkItem(Bookmark bookmark)
   {
      IApplication app = getApplication();
      ActionCollection coll = app.getActionCollection();

      SquirrelAction action =
         (SquirrelAction) coll.get(RunBookmarkAction.class);

      JMenuItem item = new JMenuItem(action);
      item.setText(bookmark.getName());

      menu.add(item);
   }

   
   public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
   {
      return new IGlobalPreferencesPanel[]{
         new SQLBookmarkPreferencesController(this)
      };
   }

   public void addSQLPanelAPIListeningForBookmarks(ISQLPanelAPI sqlApi)
   {
      if(false == _sqlPanelAPIsListeningForBookmarks.contains(sqlApi))
      {
         _sqlPanelAPIsListeningForBookmarks.add(sqlApi);
      }
   }

   public void removeSQLPanelAPIListeningForBookmarks(ISQLPanelAPI sqlApi)
   {
      _sqlPanelAPIsListeningForBookmarks.remove(sqlApi);
   }

   public void removeALLSQLPanelsAPIListeningForBookmarks()
   {
      _sqlPanelAPIsListeningForBookmarks = new ArrayList<ISQLPanelAPI>();
   }

   public ISQLPanelAPI[] getSQLPanelAPIsListeningForBookmarks()
   {
      return _sqlPanelAPIsListeningForBookmarks.toArray(new ISQLPanelAPI[_sqlPanelAPIsListeningForBookmarks.size()]);
   }


   Properties getBookmarkProperties()
   {
      FileInputStream fis = null;
      try
      {
         if(null == _boomarkProps)
         {
            File usf = getPluginUserSettingsFolder();
            File boomarkPropsFile = new File(usf, BOOKMARKS_PROPS_FILE);

            if(false == boomarkPropsFile.exists())
            {
               _boomarkProps = new Properties();
            }
            else
            {
               fis = new FileInputStream(boomarkPropsFile);
               _boomarkProps = new Properties();
               _boomarkProps.load(fis);
            }
         }
         return _boomarkProps;
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      } finally {
          if (fis != null) {
              try {
                  fis.close();
              } catch (IOException ex) {
                  s_log.error("Unable to close output stream: "+ex.getMessage(),
                              ex);
              }
          }
      }
   }

   void saveBookmarkProperties()
   {
       FileOutputStream fos = null;
      try
      {
         if(null == _boomarkProps)
         {
            return;
         }

         File usf = getPluginUserSettingsFolder();
         File boomarkPropsFile = new File(usf, BOOKMARKS_PROPS_FILE);
         fos = new FileOutputStream(boomarkPropsFile);
         _boomarkProps.store(fos, "Bookmark properties");
      } catch (IOException e) {
          throw new RuntimeException(e);
      } finally {
          if (fos != null) {
              try {
                  fos.close();
              } catch (IOException ex) {
                  s_log.error("Unable to close output stream: "+ex.getMessage(),
                              ex);
              }
          }
      }
   }



}
    
