
package net.sourceforge.squirrel_sql.client.session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.Action;
import javax.swing.JComponent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.MockApplication;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.session.BaseSessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionPanel;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessor;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.UidIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.MockSQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.QueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.MockMessageHandler;
import net.sourceforge.squirrel_sql.mo.sql.MockDatabaseMetaData;

import com.mockobjects.sql.MockConnection2;

public class MockSession implements ISession {

   ISQLAliasExt sqlAlias = null;
    ISQLDriver sqlDriver = null;
    SQLConnection con = null;
    MockDatabaseMetaData mdata = null;
    MockApplication app = null;
    SessionProperties props = null;
    IMessageHandler messageHandler = null;
    SchemaInfo schemaInfo = null;
    SessionPanel sessionPanel = null;
    SquirrelPreferences prefs = null;
    UidIdentifier id = null;
    boolean closed;
        
    
    
    private String defaultCatalog = "";
    private String defaultSchema = "";
    
    public MockSession() {
    	init(true);
    }
    
    public MockSession(String className, 
    				   String jdbcUrl, 
    				   String u, 
    				   String p) 
    	throws Exception 
    {
    	System.out.println("Attempting to load class="+className);
    	Class.forName(className);
        System.out.println("Getting connection for url="+jdbcUrl);
    	Connection c = DriverManager.getConnection(jdbcUrl, u, p);
    	sqlDriver = new MockSQLDriver(className, jdbcUrl);
    	con = new SQLConnection(c, null, sqlDriver);
    	init(false);
        sqlAlias.setUrl(jdbcUrl);
        sqlAlias.setUserName(u);
        sqlAlias.setPassword(p);
        sqlDriver.setDriverClassName(className);
    }
    
    private void init(boolean initConnection) {
    	if (initConnection) {
            
    		con = new SQLConnection(getMockConnection(), null, sqlDriver);
    	}
    	id = new UidIdentifier();
    	messageHandler = new MockMessageHandler();
    	props = new SessionProperties();
    	props.setLoadSchemasCatalogs(false);
    	app = new MockApplication();
    	app.getMockSessionManager().setSession(this);
    	sqlAlias = new SQLAlias(new UidIdentifier());
    	schemaInfo = new SchemaInfo(app);
    	schemaInfo.initialLoad(this);
    	prefs = app.getSquirrelPreferences();
    	try {
    		UIFactory.initialize(prefs, app);
    	} catch (Throwable e) {
    		
    	}
        
        
        
        
        if (!initConnection) {
            sessionPanel = new SessionPanel(this);
        }
    }
    
    private MockConnection2 getMockConnection() {
    	MockConnection2 result = new MockConnection2();
        sqlDriver = new MockSQLDriver("JUnitTestClassName", "JUnitJDBCURL");
        mdata = new MockDatabaseMetaData();
        mdata.setupDriverName("junit");
        result.setupMetaData(mdata);    
        return result;
    }
    
    
    public ExceptionFormatter getExceptionFormatter() {
        
        System.err.println("MockSession.getExceptionFormatter: stub not yet implemented");
        return null;
    }

    
    public void setExceptionFormatter(ExceptionFormatter formatter) {
        
        System.err.println("MockSession.setExceptionFormatter: stub not yet implemented");
    }

    public boolean isClosed() {
    	return closed;
    }

    public IApplication getApplication() {
        return app;
    }

    public ISQLConnection getSQLConnection() {
    	return con;
    }

    public ISQLDriver getDriver() {
    	return sqlDriver;
    }

    public ISQLAliasExt getAlias() {
        return sqlAlias;
    }

    public SessionProperties getProperties() {
        return props;
    }

    public void commit() {
        try {
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rollback() {
        try {
            con.rollback();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() throws SQLException {
    	if (!closed) {
    		con.close();
    	}
    }

    public void closeSQLConnection() throws SQLException {
    	con.close();
    }

    public void setSessionInternalFrame(SessionInternalFrame sif) {
        
        System.err.println("MockSession.setSessionInternalFrame: stub not yet implemented");
    }

    public void reconnect() {
        
        System.err.println("MockSession.reconnect: stub not yet implemented");
    }

    public Object getPluginObject(IPlugin plugin, String key) {
        
        System.err.println("MockSession.getPluginObject: stub not yet implemented");
        return null;
    }

    public Object putPluginObject(IPlugin plugin, String key, Object obj) {
        
        System.err.println("MockSession.putPluginObject: stub not yet implemented");
        return null;
    }

    public void removePluginObject(IPlugin plugin, String key) {
        
    	System.err.println("MockSession.removePluginObject: stub not yet implemented");
    }

    public void setMessageHandler(IMessageHandler handler) {
        messageHandler = handler;
    }

    public IMessageHandler getMessageHandler() {
        return messageHandler;
    }

    public SessionPanel getSessionSheet() {
        return sessionPanel;
    }

    public SessionInternalFrame getSessionInternalFrame() {
        
    	System.err.println("MockSession.getSessionInternalFrame: stub not yet implemented");    	
        return null;
    }

    
    public SchemaInfo getSchemaInfo() {
    	return schemaInfo;
    }

    public void selectMainTab(int tabIndex) throws IllegalArgumentException {
        
    	System.err.println("MockSession.selectMainTab: stub not yet implemented");
    }

    public int addMainTab(IMainPanelTab tab) {
        
    	System.err.println("MockSession.addMainTab: stub not yet implemented");
        return 0;
    }

    public void addToStatusBar(JComponent comp) {
        
    	System.err.println("MockSession.addToStatusBar: stub not yet implemented");
    }

    public void removeFromStatusBar(JComponent comp) {
        
    	System.err.println("MockSession.removeFromStatusBar: stub not yet implemented");
    }

    public String getTitle() {
        
    	System.err.println("MockSession.getTitle: stub not yet implemented");
        return null;
    }

    public String getDatabaseProductName() {
        String result = null;
        try {
        	result = con.getSQLMetaData().getDatabaseProductName();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void addToToolbar(Action action) {
        
    	System.err.println("MockSession.addToToolbar: stub not yet implemented");
    }

    public void addSeparatorToToolbar() {
        
    	System.err.println("MockSession.addSeparatorToToolbar: stub not yet implemented");
    }

    public IParserEventsProcessor getParserEventsProcessor(
            							    IIdentifier sqlEntryPanelIdentifier) 
    {
        
    	System.err.println("MockSession.getParserEventsProcessor: stub not yet implemented");
        return null;
    }

    public void setActiveSessionWindow(
            BaseSessionInternalFrame activeActiveSessionWindow) {
        
    	System.err.println("MockSession.setActiveSessionWindow: stub not yet implemented");
    }

    public BaseSessionInternalFrame getActiveSessionWindow() {
        
    	System.err.println("MockSession.getActiveSessionWindow: stub not yet implemented");
        return null;
    }

    public ISQLPanelAPI getSQLPanelAPIOfActiveSessionWindow() {
        
    	System.err.println("MockSession.getSQLPanelAPIOfActiveSessionWindow: stub not yet implemented");
        return null;
    }

    public IObjectTreeAPI getObjectTreeAPIOfActiveSessionWindow() {
        
    	System.err.println("MockSession.getObjectTreeAPIOfActiveSessionWindow: stub not yet implemented");
        return null;
    }

    public boolean isfinishedLoading() {
        
    	System.err.println("MockSession.isfinishedLoading: stub not yet implemented");
        return true;
    }

    public void setPluginsfinishedLoading(boolean _finishedLoading) {
        
    	System.err.println("MockSession.setPluginsfinishedLoading: stub not yet implemented");
    }

    public boolean confirmClose() {
        
    	System.err.println("MockSession.confirmClose: stub not yet implemented");
        return false;
    }

    public IIdentifier getIdentifier() {
        return id;
    }

    public MockDatabaseMetaData getMockDatabaseMetaData() {
    	return mdata;
    }

    
    public void setDefaultCatalog(String defaultCatalog) {
        this.defaultCatalog = defaultCatalog;
    }

    
    public String getDefaultCatalog() {
        return defaultCatalog;
    }

    
    public void setDefaultSchema(String defaultSchema) {
        this.defaultSchema = defaultSchema;
    }

    
    public String getDefaultSchema() {
        return defaultSchema;
    }
    
    public IQueryTokenizer getQueryTokenizer() {
        return new QueryTokenizer(";", "--", true);
    }

    public void setQueryTokenizer(IQueryTokenizer tokenizer) {
        
    }

    
    public ISQLDatabaseMetaData getMetaData() {
        return con.getSQLMetaData();
    }

    
    public void showErrorMessage(String msg) {
        
        System.err.println("MockSession.showErrorMessage: stub not yet implemented");
    }

    
    public void showErrorMessage(Throwable th) {
        
        System.err.println("MockSession.showErrorMessage: stub not yet implemented");
    }

    
    public void showMessage(String msg) {
        
        System.err.println("MockSession.showMessage: stub not yet implemented");
    }

    
    public void showMessage(Throwable th) {
        
        System.err.println("MockSession.showMessage: stub not yet implemented");
    }

    
    public void showWarningMessage(String msg) {
        
        System.err.println("MockSession.showWarningMessage: stub not yet implemented");
    }    
    
    public String formatException(Throwable th) {
        
        System.err.println("MockSession.format: stub not yet implemented");
        return null;        
    }

    
    public int getSelectedMainTabIndex() {
       System.err.println("MockSession.getSelectedMainTabIndex: stub not yet implemented");
       return 0;
    }

}
