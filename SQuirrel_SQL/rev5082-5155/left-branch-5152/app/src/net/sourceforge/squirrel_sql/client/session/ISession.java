package net.sourceforge.squirrel_sql.client.session;


import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ISessionWidget;
import net.sourceforge.squirrel_sql.client.gui.session.MainPanel;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionPanel;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessor;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

import javax.swing.*;
import java.sql.SQLException;


public interface ISession extends IHasIdentifier
{

   public interface IMainPanelTabIndexes extends MainPanel.ITabIndexes
   {
      
   }

   
   boolean isClosed();

   
   IApplication getApplication();

   
   ISQLConnection getSQLConnection();

   
   ISQLDatabaseMetaData getMetaData();
   
   
   ISQLDriver getDriver();

   
   ISQLAliasExt getAlias();

   
   SessionProperties getProperties();

    
   void commit();

   
   void rollback();

   
   void close() throws SQLException;

   
   void closeSQLConnection() throws SQLException;

   void setSessionInternalFrame(SessionInternalFrame sif);

   
   void reconnect();

   Object getPluginObject(IPlugin plugin, String key);
   Object putPluginObject(IPlugin plugin, String key, Object obj);
   void removePluginObject(IPlugin plugin, String key);

   void setMessageHandler(IMessageHandler handler);

   SessionPanel getSessionSheet();

   SessionInternalFrame getSessionInternalFrame();




   
   net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo getSchemaInfo();

   
   void selectMainTab(int tabIndex) throws IllegalArgumentException;

   public int getSelectedMainTabIndex();


   
   int addMainTab(IMainPanelTab tab);

   
   void addToStatusBar(JComponent comp);

   
   void removeFromStatusBar(JComponent comp);

   



   



   
   String getTitle();


   
   void addToToolbar(Action action);

   public void addSeparatorToToolbar();

   
   IParserEventsProcessor getParserEventsProcessor(IIdentifier sqlEntryPanelIdentifier);

   void setActiveSessionWindow(ISessionWidget activeActiveSessionWindow);

   
   ISessionWidget getActiveSessionWindow();

   
   ISQLPanelAPI getSQLPanelAPIOfActiveSessionWindow();

   
   IObjectTreeAPI getObjectTreeAPIOfActiveSessionWindow();

   
   public boolean isfinishedLoading();

   
   public void setPluginsfinishedLoading(boolean _finishedLoading);

   
   public boolean confirmClose();

   
   
   public void setQueryTokenizer(IQueryTokenizer tokenizer);
   
   
   IQueryTokenizer getQueryTokenizer();
       
   
   void setExceptionFormatter(ExceptionFormatter formatter);
   
   
   ExceptionFormatter getExceptionFormatter();

   
   String formatException(Throwable t);
   
   
   
   
   
   
   
   
   void showMessage(Throwable th);

   
   void showMessage(String msg);

   
   void showErrorMessage(Throwable th);

   
   void showErrorMessage(String msg);

   void showWarningMessage(String msg);    
   
}
