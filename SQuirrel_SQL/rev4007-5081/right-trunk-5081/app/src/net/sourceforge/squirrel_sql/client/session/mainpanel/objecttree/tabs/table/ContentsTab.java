package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.table;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sourceforge.squirrel_sql.client.gui.session.SessionPanel;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.DataSetUpdateableTableModelImpl;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.PleaseWaitDialog;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.session.sqlfilter.OrderByClausePanel;
import net.sourceforge.squirrel_sql.client.session.sqlfilter.SQLFilterClauses;
import net.sourceforge.squirrel_sql.client.session.sqlfilter.WhereClausePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetUpdateableTableModelListener;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataModelImplementationDetails;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.sql.dbobj.BestRowIdentifier;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class ContentsTab extends BaseTableTab
	implements IDataSetUpdateableTableModel
{
   private DataSetUpdateableTableModelImpl _dataSetUpdateableTableModel = new DataSetUpdateableTableModelImpl();

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ContentsTab.class);  

	
	String previousTableName = "";

   private SQLFilterClauses _sqlFilterClauses = new SQLFilterClauses();


	
	private static final ILogger s_log =
		LoggerController.createLogger(ContentsTab.class);


	private ObjectTreePanel _treePanel = null;
	
   private PleaseWaitDialog _waitDialog = null;

	private SquirrelPreferences _prefs = null;
   
   public ContentsTab(ObjectTreePanel treePanel) { 
      _treePanel = treePanel;
   	_prefs = _treePanel.getSession().getApplication().getSquirrelPreferences();

   }

	
	public String getTitle()
	{
		return getContentsTabTitle();
	}

	
	public static String getContentsTabTitle()
	{
		
		return s_stringMgr.getString("ContentsTab.title");
	}
	
	
	
	public String getHint()
	{
		
		return s_stringMgr.getString("ContentsTab.hint");
	}
	

   public SQLFilterClauses getSQLFilterClauses()
   {
      return _sqlFilterClauses;
   }


   
   protected IDataSet createDataSet() throws DataSetException
   {
      final ISession session = getSession();
      final ISQLConnection conn = session.getSQLConnection();
      ISQLDatabaseMetaData md = session.getMetaData();

      
      try
      {
         final Statement stmt = conn.createStatement();
         try
         {
            final SessionProperties props = session.getProperties();
            if (props.getContentsLimitRows())
            {
               try
               {
                  stmt.setMaxRows(props.getContentsNbrRowsToShow());
               }
               catch (Exception ex)
               {
                  s_log.error("Error on Statement.setMaxRows()", ex);
               }
            }
            final ITableInfo ti = getTableInfo();

            
            final String currentTableName = ti.getQualifiedName();
            if (!currentTableName.equals(previousTableName))
            {
               previousTableName = currentTableName;	
               _dataSetUpdateableTableModel.setEditModeForced(false);

               
               
            }

            
            String pseudoColumn = "";

            try
            {
               BestRowIdentifier[] rowIDs = md.getBestRowIdentifier(ti);
               for (int i = 0; i < rowIDs.length; ++i)
               {
                  short pseudo = rowIDs[i].getPseudoColumn();
                  if (pseudo == DatabaseMetaData.bestRowPseudo)
                  {
                     pseudoColumn = " ," + rowIDs[i].getColumnName();
                     break;
                  }
               }
            }

            
            
            catch (Throwable th)
            {
            	if (s_log.isDebugEnabled()) {
	               s_log.debug("getBestRowIdentifier not supported for table "+ currentTableName, th);
            	}
            }

            
            
            
            
            
            
            
            
            
            
            
            
            
            if (pseudoColumn.length() == 0)
            {
                if (DialectFactory.isPostgreSQL(md)) {
                    pseudoColumn = ", oid";
                }
                if (DialectFactory.isOracle(md)) {
                    pseudoColumn = ", ROWID";
                }
            }

            ResultSet rs = null;
            try
            {
               
               
               
               
               
               final StringBuffer buf = new StringBuffer();
               buf.append("select tbl.*")
                  .append(pseudoColumn)
                  .append(" from ")
                  .append(ti.getQualifiedName())
                  .append(" tbl");

               String clause = _sqlFilterClauses.get(WhereClausePanel.getClauseIdentifier(), ti.getQualifiedName());
               if ((clause != null) && (clause.length() > 0))
               {
                 buf.append(" where ").append(clause);
               }
               clause = _sqlFilterClauses.get(OrderByClausePanel.getClauseIdentifier(), ti.getQualifiedName());
               if ((clause != null) && (clause.length() > 0))
               {
                 buf.append(" order by ").append(clause);
               }

               if (s_log.isDebugEnabled()) {
                   s_log.debug("createDataSet running SQL: "+buf.toString());
               }
                           
               showWaitDialog(stmt);               

               rs = stmt.executeQuery(buf.toString());

            }
            catch (SQLException ex)
            {
                if (s_log.isDebugEnabled()) {
                        s_log.debug(
                            "createDataSet: exception from pseudocolumn query - "
                                    + ex, ex);
                    }
                
                
                
                
               if (pseudoColumn.length() == 0)
               {
                  throw ex;
               }
               
               
               
               pseudoColumn = "";

               
               
               
               
               
               
               
               
               
               
               
               
               
               final StringBuffer buf = new StringBuffer();
               buf.append("select *")
                  .append(" from ")
                  .append(ti.getQualifiedName())
                  .append(" tbl");

               String clause = _sqlFilterClauses.get(WhereClausePanel.getClauseIdentifier(), ti.getQualifiedName());
               if ((clause != null) && (clause.length() > 0))
               {
                 buf.append(" where ").append(clause);
               }
               clause = _sqlFilterClauses.get(OrderByClausePanel.getClauseIdentifier(), ti.getQualifiedName());
               if ((clause != null) && (clause.length() > 0))
               {
                 buf.append(" order by ").append(clause);
               }

               rs = stmt.executeQuery(buf.toString());
            }

            final ResultSetDataSet rsds = new ResultSetDataSet();

            
            
            
            
            
            
            
            
            
            rsds.setContentsTabResultSet(rs,
                                         _dataSetUpdateableTableModel.getFullTableName(),
                                         DialectFactory.getDialectType(md));
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) {}
            }
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            TableColumnInfo[] columnInfos = md.getColumnInfo(getTableInfo());
            final ColumnDisplayDefinition[] colDefs = 
                rsds.getDataSetDefinition().getColumnDefinitions();

            
            
            
            for (int i = 0; i < columnInfos.length; i++) {
                boolean isNullable = true;
                TableColumnInfo info = columnInfos[i];
                if (info.isNullAllowed() == DatabaseMetaData.columnNoNulls) {
                    isNullable = false;
                }
                if (i < colDefs.length) {
                    colDefs[i].setIsNullable(isNullable);
                }
            }

            
            
            if (pseudoColumn.length() > 0)
            {
               _dataSetUpdateableTableModel.setRowIDCol(rsds.getColumnCount() - 1);
            }

            return rsds;
         }
         finally
         {
             SQLUtilities.closeStatement(stmt);
         }

      }
      catch (SQLException ex)
      {
         throw new DataSetException(ex);
      } finally {
          disposeWaitDialog();
      }
   }

   
   private boolean objectTreeTabIsSelected() {
      boolean result = false;
      ISession session = _treePanel.getSession();
      if (session != null) {
         SessionPanel sessionPanel = session.getSessionSheet();
         if (sessionPanel != null) {
            result = sessionPanel.isObjectTreeTabSelected();
         }
      }
      return result;
   }
   
   
   private void showWaitDialog(final Statement stmt) {
      
   	
      if (!_prefs.getShowPleaseWaitDialog()) return;
      
      
      
      if (objectTreeTabIsSelected()) {
         
         
         
         _treePanel.saveSelectedPaths();
         
         GUIUtils.processOnSwingEventThread(new Runnable() {
            public void run() {
               _waitDialog = new PleaseWaitDialog(stmt, _app);
               _waitDialog.showDialog(_app);                                          
               
               _treePanel.restoreSavedSelectedPaths();
            }
         });         
      }
   }
   
   
   private void disposeWaitDialog() {
   	if (!_prefs.getShowPleaseWaitDialog()) return;
      if (_waitDialog != null) {
          GUIUtils.processOnSwingEventThread(new Runnable() {
              public void run() {
                  _waitDialog.dispose();
              }
          });
      }       
   }
   
   public void setDatabaseObjectInfo(IDatabaseObjectInfo value)
   {
      super.setDatabaseObjectInfo(value);
      _dataSetUpdateableTableModel.setTableInfo(getTableInfo());
   }

   public void setSession(ISession session) throws IllegalArgumentException
   {
      super.setSession(session);
      _dataSetUpdateableTableModel.setSession(session);
   }


	
	public static String getUnambiguousTableName(ISession session, String name)
   {
		return DataSetUpdateableTableModelImpl.getUnambiguousTableName(session, name);
	}

   
   
   
   public String getWarningOnCurrentData(Object[] values, ColumnDisplayDefinition[] colDefs, int col, Object oldValue)
   {
      return _dataSetUpdateableTableModel.getWarningOnCurrentData(values, colDefs, col, oldValue);
   }

   public String getWarningOnProjectedUpdate(Object[] values, ColumnDisplayDefinition[] colDefs, int col, Object newValue)
   {
      return _dataSetUpdateableTableModel.getWarningOnProjectedUpdate(values, colDefs, col, newValue);
   }

   public Object reReadDatum(Object[] values, ColumnDisplayDefinition[] colDefs, int col, StringBuffer message)
   {
      return _dataSetUpdateableTableModel.reReadDatum(values, colDefs, col, message);
   }

   public String updateTableComponent(Object[] values, ColumnDisplayDefinition[] colDefs, int col, Object oldValue, Object newValue)
   {
      return _dataSetUpdateableTableModel.updateTableComponent(values, colDefs, col, oldValue, newValue);
   }

   public int getRowidCol()
   {
      return _dataSetUpdateableTableModel.getRowidCol();
   }

   public String deleteRows(Object[][] rowData, ColumnDisplayDefinition[] colDefs)
   {
      return _dataSetUpdateableTableModel.deleteRows(rowData, colDefs);
   }

   public String[] getDefaultValues(ColumnDisplayDefinition[] colDefs)
   {
      return _dataSetUpdateableTableModel.getDefaultValues(colDefs);
   }

   public String insertRow(Object[] values, ColumnDisplayDefinition[] colDefs)
   {
      return _dataSetUpdateableTableModel.insertRow(values, colDefs);
   }

   public void addListener(DataSetUpdateableTableModelListener l)
   {
      _dataSetUpdateableTableModel.addListener(l);
   }

   public void removeListener(DataSetUpdateableTableModelListener l)
   {
      _dataSetUpdateableTableModel.removeListener(l);
   }

   public void forceEditMode(boolean mode)
   {
      _dataSetUpdateableTableModel.forceEditMode(mode);
   }

   public boolean editModeIsForced()
   {
      return _dataSetUpdateableTableModel.editModeIsForced();
   }

   public IDataModelImplementationDetails getDataModelImplementationDetails()
   {
      return new IDataModelImplementationDetails()
      {
         public String getStatementSeparator()
         {
            return getSession().getQueryTokenizer().getSQLStatementSeparator();
         }
      };
   }

   protected String getDestinationClassName()
   {
      return _dataSetUpdateableTableModel.getDestinationClassName();
   }
   
   
}
