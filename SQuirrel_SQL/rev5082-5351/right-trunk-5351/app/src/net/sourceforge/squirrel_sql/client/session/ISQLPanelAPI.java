package net.sourceforge.squirrel_sql.client.session;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.sourceforge.squirrel_sql.client.session.event.IResultTabListener;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.client.session.event.ISQLPanelListener;
import net.sourceforge.squirrel_sql.client.session.event.ISQLResultExecuterTabListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ISQLResultExecuter;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SqlPanelListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLHistoryItem;

import java.io.File;
import java.util.ArrayList;


public interface ISQLPanelAPI
{
	void addExecutor(ISQLResultExecuter exec);

	void removeExecutor(ISQLResultExecuter exec);

	
	void addSQLExecutionListener(ISQLExecutionListener lis);

	
	void removeSQLExecutionListener(ISQLExecutionListener lis);

	
	void addResultTabListener(IResultTabListener lis);

	
	void removeResultTabListener(IResultTabListener lis);

	
	void addSQLPanelListener(ISQLPanelListener lis);

	
	void removeSQLPanelListener(ISQLPanelListener lis);

	
	void addExecuterTabListener(ISQLResultExecuterTabListener lis);

	
	void removeExecuterTabListener(ISQLResultExecuterTabListener lis);


	ISQLEntryPanel getSQLEntryPanel();

    
    ISQLResultExecuter getSQLResultExecuter();
    
	
	String getEntireSQLScript();

	
	String getSelectedSQLScript();

	
	String getSQLScriptToBeExecuted();

	
	void appendSQLScript(String sqlScript);

	
	void appendSQLScript(String sqlScript, boolean select);

	
	void setEntireSQLScript(String sqlScript);

	
	void setEntireSQLScript(String sqlScript, boolean select);

	
	void replaceSelectedSQLScript(String sqlScript, boolean select);

	
	int getSQLScriptSelectionStart();

	
	int getSQLScriptSelectionEnd();

	
	void setSQLScriptSelectionStart(int start);

	
	void setSQLScriptSelectionEnd(int end);

   
	void executeCurrentSQL();

   
	void closeAllSQLResultTabs();

   
   void closeAllButCurrentResultTabs();

   
   void closeCurrentResultTab();

   
   void toggleCurrentSQLResultTabSticky();


	
	void closeAllSQLResultFrames();

	
	void gotoNextResultsTab();

	
	void gotoPreviousResultsTab();

	
	void addSQLToHistory(String sql);

	
	void addToSQLEntryAreaMenu(JMenu menu);

	
	JMenuItem addToSQLEntryAreaMenu(Action action);

	ISession getSession();

	boolean isInMainSessionWindow();

	void addToToolsPopUp(String selectionString, Action action);

	boolean fileSave();

	void fileSaveAs();

	void fileOpen();
	
	void fileOpen(File f);

	void fileAppend();

	void fileClose();

	void fileNew();

	void filePrint();

	void showToolsPopup();

	boolean confirmClose();

	void addSqlPanelListener(SqlPanelListener sqlPanelListener);

	ArrayList<SQLHistoryItem> getSQLHistoryItems();
}

