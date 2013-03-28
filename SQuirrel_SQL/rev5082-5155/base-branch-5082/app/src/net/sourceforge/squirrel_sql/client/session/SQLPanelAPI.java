package net.sourceforge.squirrel_sql.client.session;

import java.io.File;
import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.ToolsPopupController;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ISessionWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.SessionTabWidget;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.action.*;
import net.sourceforge.squirrel_sql.client.session.event.IResultTabListener;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.client.session.event.ISQLPanelListener;
import net.sourceforge.squirrel_sql.client.session.event.ISQLResultExecuterTabListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ISQLResultExecuter;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLHistoryItem;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SqlPanelListener;
import net.sourceforge.squirrel_sql.client.util.PrintUtilities;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class SQLPanelAPI implements ISQLPanelAPI
{
	
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(SQLPanelAPI.class);

	
	private SQLPanel _panel;

   private ToolsPopupController _toolsPopupController;
   private FileManager _fileManager = new FileManager(this);

   private boolean fileOpened = false;
   private boolean fileSaved = false;
    private boolean unsavedEdits = false;
   
   
	public SQLPanelAPI(SQLPanel panel)
	{
		super();
		if (panel == null)
		{
			throw new IllegalArgumentException("SQLPanel == null");
		}
		_panel = panel;
        _panel.getSQLEntryPanel().addUndoableEditListener(new SQLEntryUndoListener());
      initToolsPopUp();

      createStandardEntryAreaMenuItems();
	}

   private void initToolsPopUp()
   {
      _toolsPopupController = new ToolsPopupController(getSession(), _panel.getSQLEntryPanel());


      ActionCollection ac = getSession().getApplication().getActionCollection();

      _toolsPopupController.addAction("undo", _panel.getUndoAction());
      _toolsPopupController.addAction("redo", _panel.getRedoAction());
      _toolsPopupController.addAction("runsql", ac.get(ExecuteSqlAction.class));
      _toolsPopupController.addAction("fileopen", ac.get(FileOpenAction.class));
      _toolsPopupController.addAction("filesave", ac.get(FileSaveAction.class));
      _toolsPopupController.addAction("filesaveas", ac.get(FileSaveAsAction.class));
      _toolsPopupController.addAction("filenew", ac.get(FileNewAction.class));
      _toolsPopupController.addAction("fileappend", ac.get(FileAppendAction.class));
      _toolsPopupController.addAction("fileprint", ac.get(FilePrintAction.class));
      _toolsPopupController.addAction("fileclose", ac.get(FileCloseAction.class));

      _toolsPopupController.addAction("tabnext", ac.get(GotoNextResultsTabAction.class));
      _toolsPopupController.addAction("tabprevious", ac.get(GotoPreviousResultsTabAction.class));
      _toolsPopupController.addAction("tabcloseall", ac.get(CloseAllSQLResultTabsAction.class));
      _toolsPopupController.addAction("tabcloseallbutcur", ac.get(CloseAllSQLResultTabsButCurrentAction.class));
      _toolsPopupController.addAction("tabclosecur", ac.get(CloseCurrentSQLResultTabAction.class));
      _toolsPopupController.addAction("tabsticky", ac.get(ToggleCurrentSQLResultTabStickyAction.class));

      _toolsPopupController.addAction("sqlprevious", ac.get(PreviousSqlAction.class));
      _toolsPopupController.addAction("sqlnext", ac.get(NextSqlAction.class));
      _toolsPopupController.addAction("sqlselect", ac.get(SelectSqlAction.class));

      _toolsPopupController.addAction("sqlhist", ac.get(OpenSqlHistoryAction.class));

      if (_panel.isInMainSessionWindow())
      {
         _toolsPopupController.addAction("viewinobjecttree", ac.get(ViewObjectAtCursorInObjectTreeAction.class));
      }

   }


   private void createStandardEntryAreaMenuItems()
   {
      JMenuItem item;
      SquirrelResources resources = getSession().getApplication().getResources();


      Action toolsPopupAction = _panel.getSession().getApplication().getActionCollection().get(ToolsPopupAction.class);
      item = getSQLEntryPanel().addToSQLEntryAreaMenu(toolsPopupAction);
      resources.configureMenuItem(toolsPopupAction, item);

      if(_panel.isInMainSessionWindow())
      {
         Action vioAction = _panel.getSession().getApplication().getActionCollection().get(ViewObjectAtCursorInObjectTreeAction.class);
         item = getSQLEntryPanel().addToSQLEntryAreaMenu(vioAction);
         resources.configureMenuItem(vioAction, item);
      }

   }


   public void addToToolsPopUp(String selectionString, Action action)
   {
      _toolsPopupController.addAction(selectionString, action);
   }

   public boolean fileSave()
   {
      if (_fileManager.save()) {
          fileSaved = true;
          unsavedEdits = false;
          getActiveSessionTabWidget().setUnsavedEdits(false);
          ActionCollection actions = 
              getSession().getApplication().getActionCollection();
          actions.enableAction(FileSaveAction.class, false);
          return true;
      } else {
          return false;
      }
   }

   private SessionTabWidget getActiveSessionTabWidget()
   {
      return (SessionTabWidget) getSession().getActiveSessionWindow();
   }

   
   public void fileAppend() {
       if (_fileManager.open(true)) {
           fileOpened = true;
           fileSaved = false;
           unsavedEdits = false;
           ActionCollection actions = 
               getSession().getApplication().getActionCollection();
           actions.enableAction(FileSaveAction.class, true);           
       }
   }
   
   
   
   public void fileClose() {
       if (unsavedEdits) {
           showConfirmSaveDialog();
       }
       setEntireSQLScript("");
       getActiveSessionTabWidget().setSqlFile(null);
       fileOpened = false;
       fileSaved = false;
       unsavedEdits = false;
       ActionCollection actions = 
           getSession().getApplication().getActionCollection();
       actions.enableAction(FileSaveAction.class, true);
       _fileManager.clearCurrentFile();
   }
   
   
   
   public void fileNew() {
       fileClose();
	}
   
   
   public void fileSaveAs()
   {
       if (_fileManager.saveAs()) {
           fileSaved = true;
           unsavedEdits = false;
           getActiveSessionTabWidget().setUnsavedEdits(false);
           ActionCollection actions = 
               getSession().getApplication().getActionCollection();
           actions.enableAction(FileSaveAction.class, false);         
       }
   }

   
   public void fileOpen()
   {
       if (unsavedEdits) {
           showConfirmSaveDialog();
       }       
       if (_fileManager.open(false)) {
           fileOpened = true;
           fileSaved = false;
           unsavedEdits = false;
           ActionCollection actions = 
               getSession().getApplication().getActionCollection();
           actions.enableAction(FileSaveAction.class, false);           
       }
   }

   public void fileOpen(File f) {
       if (unsavedEdits) {
           showConfirmSaveDialog();
       }              
       if (_fileManager.open(f)) {
           fileOpened = true;
           fileSaved = false;
           unsavedEdits = false;
           ActionCollection actions = 
               getSession().getApplication().getActionCollection();
           actions.enableAction(FileSaveAction.class, false);           
       }
       
   }
   
   
   public void filePrint() {
       if (_panel == null) {
           throw new IllegalStateException("_panel is null");
       }
       ISQLEntryPanel panel = _panel.getSQLEntryPanel();
       if (panel == null) {
           throw new IllegalStateException("_panel.getSQLEntryPanel() is null");
       }
       PrintUtilities.printComponent(panel.getTextComponent());
   }       
   
   public void showToolsPopup()
   {
      _toolsPopupController.showToolsPopup();
   }


   public void addExecutor(ISQLResultExecuter exec)
	{
		_panel.addExecutor(exec);
	}

	public void removeExecutor(ISQLResultExecuter exec)
	{
		_panel.removeExecutor(exec);
	}

	
	public synchronized void addSQLExecutionListener(ISQLExecutionListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("null ISQLExecutionListener passed");
		}
		_panel.addSQLExecutionListener(lis);
	}

	
	public synchronized void removeSQLExecutionListener(ISQLExecutionListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("null ISQLExecutionListener passed");
		}
		_panel.removeSQLExecutionListener(lis);
	}

	

	public synchronized void addResultTabListener(IResultTabListener lis)
	{





	}

	

	public synchronized void removeResultTabListener(IResultTabListener lis)
	{





	}

	
	public void addExecuterTabListener(ISQLResultExecuterTabListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("ISQLResultExecuterTabListener == null");
		}
		_panel.addExecuterTabListener(lis);
	}

	
	public void removeExecuterTabListener(ISQLResultExecuterTabListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("ISQLResultExecuterTabListener == null");
		}
		_panel.removeExecuterTabListener(lis);
	}

	
	public synchronized void addSQLPanelListener(ISQLPanelListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("ISQLPanelListener == null");
		}
		_panel.addSQLPanelListener(lis);
	}

	
	public synchronized void removeSQLPanelListener(ISQLPanelListener lis)
	{
		if (lis == null)
		{
			throw new IllegalArgumentException("ISQLPanelListener == null");
		}
		_panel.removeSQLPanelListener(lis);
	}

	public ISQLEntryPanel getSQLEntryPanel()
	{
		return _panel.getSQLEntryPanel();
	}

    
    public ISQLResultExecuter getSQLResultExecuter() {
        return _panel.getSQLExecPanel();
    }
    
	
	public synchronized String getEntireSQLScript()
	{
		return _panel.getSQLEntryPanel().getText();
	}

	
	public String getSelectedSQLScript()
	{
		return _panel.getSQLEntryPanel().getSelectedText();
	}

	
	public synchronized String getSQLScriptToBeExecuted()
	{
		return _panel.getSQLEntryPanel().getSQLToBeExecuted();
	}

	
	public synchronized void appendSQLScript(String sqlScript)
	{
		_panel.getSQLEntryPanel().appendText(sqlScript);
	}

	
	public synchronized void appendSQLScript(String sqlScript, boolean select)
	{
        _panel.getSQLEntryPanel().appendText(sqlScript, select);
	}

	
	public synchronized void setEntireSQLScript(String sqlScript)
	{
		_panel.getSQLEntryPanel().setText(sqlScript);
	}

	
	public synchronized void setEntireSQLScript(String sqlScript, boolean select)
	{
		_panel.getSQLEntryPanel().setText(sqlScript, select);
	}

	
	public synchronized void replaceSelectedSQLScript(String sqlScript, boolean select)
	{
		if (sqlScript == null)
		{
			sqlScript = "";
		}
		final ISQLEntryPanel pnl = _panel.getSQLEntryPanel();
		int selStart = -1;
		if (select)
		{
			selStart = pnl.getSelectionStart();
		}
		pnl.replaceSelection(sqlScript);
		if (select)
		{
			int entireLen = getEntireSQLScript().length();
			if (selStart == -1)
			{
				selStart = 0;
			}
			int selEnd = selStart + sqlScript.length() - 1;
			if (selEnd > entireLen - 1)
			{
				selEnd = entireLen - 1;
			}
			if (selStart <= selEnd)
			{
				pnl.setSelectionStart(selStart);
				pnl.setSelectionEnd(selEnd);
			}
		}
	}

	
	public synchronized int getSQLScriptSelectionStart()
	{
		return _panel.getSQLEntryPanel().getSelectionStart();
	}

	
	public synchronized int getSQLScriptSelectionEnd()
	{
		return _panel.getSQLEntryPanel().getSelectionEnd();
	}

	
	public synchronized void setSQLScriptSelectionStart(int start)
	{
		_panel.getSQLEntryPanel().setSelectionStart(start);
	}

	
	public synchronized void setSQLScriptSelectionEnd(int end)
	{
		_panel.getSQLEntryPanel().setSelectionEnd(end);
	}

	
	public void executeCurrentSQL()
	{
		_panel.runCurrentExecuter();
	}

   
	public void executeSQL(String sql)
	{
		
		
	}

	
	public void closeAllSQLResultTabs()
	{
		_panel.getSQLExecPanel().closeAllSQLResultTabs();
	}

   public void closeAllButCurrentResultTabs()
   {
      _panel.getSQLExecPanel().closeAllButCurrentResultTabs();
   }

   public void closeCurrentResultTab()
   {
      _panel.getSQLExecPanel().closeCurrentResultTab();
   }

   public void toggleCurrentSQLResultTabSticky()
   {
      _panel.getSQLExecPanel().toggleCurrentSQLResultTabSticky();
   }

	
	public void closeAllSQLResultFrames()
	{
		_panel.getSQLExecPanel().closeAllSQLResultFrames();
	}

	
	public synchronized void gotoNextResultsTab()
	{
		_panel.getSQLExecPanel().gotoNextResultsTab();
	}

	
	public void gotoPreviousResultsTab()
	{
		_panel.getSQLExecPanel().gotoPreviousResultsTab();
	}

	
	public synchronized void addSQLToHistory(String sql)
	{
		if (sql == null)
		{
			throw new IllegalArgumentException("sql == null");
		}

      final ISession session = _panel.getSession();
		final SQLHistoryItem shi = new SQLHistoryItem(sql, session.getAlias().getName());
		if (session.getProperties().getSQLShareHistory())
		{
			session.getApplication().getSQLHistory().add(shi);
		}
		_panel.addSQLToHistory(shi);
	}

	
	public void addToSQLEntryAreaMenu(JMenu menu)
	{
		if (menu == null)
		{
			throw new IllegalArgumentException("Menu == null");
		}
		_panel.addToSQLEntryAreaMenu(menu);
	}

	
	public JMenuItem addToSQLEntryAreaMenu(Action action)
	{
		if (action == null)
		{
			throw new IllegalArgumentException("Action == null");
		}
		return _panel.addToSQLEntryAreaMenu(action);
	}

	
	public ISession getSession()
	{
		return _panel.getSession();
	}

   public boolean isInMainSessionWindow()
   {
      return _panel.isInMainSessionWindow();
   }

   public boolean confirmClose() {
       if (unsavedEdits) {
           return showConfirmSaveDialog();
       }
       return true;
   }

   public void addSqlPanelListener(SqlPanelListener sqlPanelListener)
   {
      _panel.addSqlPanelListener(sqlPanelListener);
   }

   public ArrayList<SQLHistoryItem> getSQLHistoryItems()
   {
      return _panel.getSQLHistoryItems();
   }

   private boolean showConfirmSaveDialog() 
   {
       File file = _fileManager.getFile();
       
       
       String filename = s_stringMgr.getString("SQLPanelAPI.untitledLabel");
       
       if (file != null) {
           filename = file.getAbsolutePath();
       }
       String msg = s_stringMgr.getString("SQLPanelAPI.unsavedchanges",
                                          filename);
       
       
       
       String title = 
           s_stringMgr.getString("SQLPanelAPI.unsavedchangestitle",
                                 ": "+_panel.getSession().getAlias().getName());
       
       JFrame f = getSession().getApplication().getMainFrame();
       int option = 
           JOptionPane.showConfirmDialog(f, 
                                         msg, 
                                         title, 
                                         JOptionPane.YES_NO_OPTION);
       if (option == JOptionPane.YES_OPTION) {
           return fileSave();
       }
       return true;
   }   
   
   
   private class SQLEntryUndoListener implements UndoableEditListener {

    
    public void undoableEditHappened(UndoableEditEvent e) {
        IApplication app = getSession().getApplication();
        SquirrelPreferences prefs = app.getSquirrelPreferences();
        
        if (fileOpened || fileSaved) {
            if (prefs.getWarnForUnsavedFileEdits()) {
                unsavedEdits = true;
            }
            getActiveSessionTabWidget().setUnsavedEdits(true);
            ActionCollection actions = 
                getSession().getApplication().getActionCollection();
            actions.enableAction(FileSaveAction.class, true);
        } else if (prefs.getWarnForUnsavedBufferEdits()) {
            unsavedEdits = true;
        }
    }
       
   }   
}
