package net.sourceforge.squirrel_sql.client.session;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.session.properties.EditWhereCols;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetUpdateableTableModelListener;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataModelImplementationDetails;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class DataSetUpdateableTableModelImpl implements IDataSetUpdateableTableModel
{

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DataSetUpdateableTableModelImpl.class);       
    
   
   
   private final String TI_ERROR_MESSAGE = s_stringMgr.getString("DataSetUpdateableTableModelImpl.error.tablenotfound");

   
   private static final ILogger s_log = LoggerController.createLogger(DataSetUpdateableTableModelImpl.class);


   
   private String fullTableName = null;
   private ITableInfo ti;
   private ISession _session;

   
   private boolean editModeForced = false;


   
   String sqlOutputClassNameAtTimeOfForcedEdit = "";

   private Vector<DataSetUpdateableTableModelListener> _dataSetUpdateableTableModelListener = 
       new Vector<DataSetUpdateableTableModelListener>();

   
   int _rowIDcol = -1;

   public void setTableInfo(ITableInfo ti)
   {
      this.ti = ti;
      
      fullTableName = null;
   }

   public void setSession(ISession session)
   {
      this._session = session;
   }


   
   public static String getUnambiguousTableName(ISession session, String name) {
      return session.getAlias().getUrl()+":"+name;
   }

   
   public String getFullTableName() {
      if (fullTableName == null) {
         try {
            final String name = ti.getQualifiedName();
            fullTableName = getUnambiguousTableName(_session, name);
         }
         catch (Exception e) {
            s_log.error(
                "getFullTableName: Unexpected exception - "+e.getMessage(), e);
         }
      }
      return fullTableName;
   }

   
   public void forceEditMode(boolean mode)
   {
      editModeForced = mode;
      sqlOutputClassNameAtTimeOfForcedEdit =
         _session.getProperties().getTableContentsOutputClassName();

      DataSetUpdateableTableModelListener[] listeners =
         _dataSetUpdateableTableModelListener.toArray(new DataSetUpdateableTableModelListener[0]);

      for (int i = 0; i < listeners.length; i++)
      {
         listeners[i].forceEditMode(mode);
      }


      

   }

   
   public boolean editModeIsForced()
   {
      return editModeForced;
   }


   public IDataModelImplementationDetails getDataModelImplementationDetails()
   {
      return new IDataModelImplementationDetails()
      {
         public String getStatementSeparator()
         {
            return _session.getQueryTokenizer().getSQLStatementSeparator();
         }
      };
   }

   
   public String getDestinationClassName()
   {
      if (editModeForced)
      {
         if (_session.getProperties().getTableContentsOutputClassName().equals(
            sqlOutputClassNameAtTimeOfForcedEdit))
         {
            return _session.getProperties().getEditableTableOutputClassName();
         }
         
         editModeForced = false;
      }

      
      
      return _session.getProperties().getTableContentsOutputClassName();
   }

   
   public String getWarningOnCurrentData(
      Object[] values,
      ColumnDisplayDefinition[] colDefs,
      int col,
      Object oldValue)
   {

      
      if (ti == null)
         return TI_ERROR_MESSAGE;

      String whereClause = getWhereClause(values, colDefs, col, oldValue);

      
      
      
      
      if (whereClause.length() == 0)
         
         return s_stringMgr.getString("DataSetUpdateableTableModelImpl.confirmupdateallrows");

      final ISession session = _session;
      final ISQLConnection conn = session.getSQLConnection();

      int count = -1;	

      try
      {
         Statement stmt = null;
         ResultSet rs = null;
         try
         {
            stmt = conn.createStatement();
            String countSql = "select count(*) from " + ti.getQualifiedName() + whereClause;
            rs = stmt.executeQuery(countSql);
            rs.next();
            count = rs.getInt(1);
         }
         finally
         {
            
            
            
            SQLUtilities.closeResultSet(rs);
            SQLUtilities.closeStatement(stmt);
         }
      }
      catch (SQLException ex)
      {
          
          
          
          String msg = 
              s_stringMgr.getString("DataSetUpdateableTableModelImpl.error.exceptionduringcheck", ex.getMessage());
          s_log.error(msg, ex);
          return msg;
      }

      if (count == -1) {
          
         return s_stringMgr.getString("DataSetUpdateableTableModelImpl.error.unknownerror");
      }
      if (count == 0) {
          
         return s_stringMgr.getString("DataSetUpdateableTableModelImpl.error.staleupdaterow");
      }
      if (count > 1) {
          
          return s_stringMgr.getString("DataSetUpdateableTableModelImpl.info.updateidenticalrows",
                                       Long.valueOf(count));
      }
      
      return null;	
   }

   
   public String getWarningOnProjectedUpdate(
      Object[] values,
      ColumnDisplayDefinition[] colDefs,
      int col,
      Object newValue)
   {
      try
      {
         
         if (ti == null)
            return TI_ERROR_MESSAGE;

         String whereClause = getWhereClause(values, colDefs, col, newValue);

         final ISession session = _session;
         final ISQLConnection conn = session.getSQLConnection();

         int count = -1;	

         try
         {
            final Statement stmt = conn.createStatement();
            try
            {
               final ResultSet rs = stmt.executeQuery("select count(*) from "
                              + ti.getQualifiedName() + whereClause);
               rs.next();
               count = rs.getInt(1);
            }
            finally
            {
               stmt.close();
            }
         }
         catch (SQLException ex)
         {
             
             s_stringMgr.getString("DataSetUpdateableTableModelImpl.error.exceptionduringcheck", ex.getMessage());
         }

         if (count == -1) {
             
            return s_stringMgr.getString("DataSetUpdateableTableModelImpl.error.unknownerror");
         }
         
         
         
         
         
         
         
         
         
         
         
         
         
         
         
         
         
         
         if (count > 1) {
             
             
             
             return s_stringMgr.getString("DataSetUpdateableTableModelImpl.info.identicalrows",
                                          Long.valueOf(count));
         }
         
         
         return null;	
      }
      catch (Exception e)
      {
         throw new  RuntimeException(e);
      }

   }

   
   public Object reReadDatum(
      Object[] values,
      ColumnDisplayDefinition[] colDefs,
      int col,
      StringBuffer message) {

      
      if (ti == null)
         return TI_ERROR_MESSAGE;

      
      
      
      
      
      
      
      
      
      String whereClause = getWhereClause(values, colDefs, -1, null);

      final ISession session = _session;
      final ISQLConnection conn = session.getSQLConnection();

      Object wholeDatum = null;

      try
      {
         final Statement stmt = conn.createStatement();
         final String queryString =
            "SELECT " + colDefs[col].getLabel() +" FROM "+ti.getQualifiedName() +
            whereClause;

         try
         {
            ResultSet rs = stmt.executeQuery(queryString);

            
            if (rs.next() == false) {
               
               
               throw new SQLException(s_stringMgr.getString("DataSetUpdateableTableModelImpl.error.nomatchingrow"));
            }

            
            
            wholeDatum = CellComponentFactory.readResultSet(colDefs[col], rs, 1, false);

            
            
            
            if (rs.next() == true) {
               
               wholeDatum = null;
               
               throw new SQLException(s_stringMgr.getString("DataSetUpdateableTableModelImpl.error.multimatchingrows"));
            }
         }
         finally
         {
            stmt.close();
         }
      }
      catch (Exception ex)
      {
          
          message.append(
              s_stringMgr.getString(
                          "DataSetUpdateableTableModelImpl.error.rereadingdb", 
                          ex.getMessage()));

         
         
         
         
      }


      
      return wholeDatum;
   };

   
   public String updateTableComponent(
      Object[] values,
      ColumnDisplayDefinition[] colDefs,
      int col,
      Object oldValue,
      Object newValue)
   {
      
      if (ti == null)
         return TI_ERROR_MESSAGE;

      
      String whereClause = getWhereClause(values, colDefs, col, oldValue);

      if (s_log.isDebugEnabled()) {
          s_log.debug("updateTableComponent: whereClause = "+whereClause);
      }
      
      final ISession session = _session;
      final ISQLConnection conn = session.getSQLConnection();

      int count = -1;

      final String sql = constructUpdateSql(
            ti.getQualifiedName(), colDefs[col].getLabel(), whereClause);
      
      if (s_log.isDebugEnabled()) {
          s_log.debug("updateTableComponent: executing SQL - "+sql);
      }
      PreparedStatement pstmt = null;
      try
      {
         pstmt = conn.prepareStatement(sql);

         
         
         CellComponentFactory.setPreparedStatementValue(
                colDefs[col], pstmt, newValue, 1);
         count = pstmt.executeUpdate();
      }
      catch (SQLException ex)
      {
          
          
          
          
          
          
          String errMsg = s_stringMgr.getString(
                "DataSetUpdateableTableModelImpl.error.updateproblem",
                ex.getMessage());
          s_log.error("updateTableComponent: unexpected exception - "+
                      ex.getMessage()+" while executing SQL: "+sql);
          
          
         return errMsg;           
      } finally {
          SQLUtilities.closeStatement(pstmt);
      }

      if (count == -1) {
          
         return s_stringMgr.getString("DataSetUpdateableTableModelImpl.error.unknownupdateerror");
      }
      if (count == 0) {
          
         return s_stringMgr.getString("DataSetUpdateableTableModelImpl.info.norowsupdated");
      }
      
      return null;
   }

   
   
   private String constructUpdateSql(String table, String column,
           String whereClause) {
       StringBuilder result = new StringBuilder();
       result.append("UPDATE ");
       result.append(table);
       result.append(" SET ");
       result.append(column);
       result.append(" = ? ");
       result.append(whereClause);
       return result.toString();
   }
   
   
   public int getRowidCol()
   {
      return _rowIDcol;
   }


   
   private String getWhereClause(
      Object[] values,
      ColumnDisplayDefinition[] colDefs,
      int col,
      Object colValue)
   {
      try
      {
         StringBuffer whereClause = new StringBuffer("");

         
         
         HashMap<String, String> colNames = (EditWhereCols.get(getFullTableName()));



			ColumnDisplayDefinition editedCol = null;
			if(-1 != col)
			{
				editedCol = colDefs[col];
			}

			
			for (int i=0; i< colDefs.length; i++) {

            if(i != col &&
					null != editedCol &&
					colDefs[i].getFullTableColumnName().equalsIgnoreCase(editedCol.getFullTableColumnName()))
            {
               
               
               continue;
            }

            
            if (colNames != null) {
               
               
               
               if (colNames.get(colDefs[i].getLabel()) == null)
                  continue;	
            }

            
            
            
            Object value = values[i];
            if (i == col)
               value = colValue;

            
            if (value != null && value.toString().equals("<null>"))
               value = null;

            
            ISQLDatabaseMetaData md = _session.getMetaData();
            String clause = CellComponentFactory.getWhereClauseValue(colDefs[i], value, md);

            if (clause != null && clause.length() > 0)
               if (whereClause.length() == 0)
               {
                  whereClause.append(clause);
               }
               else
               {
                  whereClause.append(" AND ");
                  whereClause.append(clause);
               }
         }

         
         if (whereClause.length() == 0)
            return "";

         whereClause.insert(0, " WHERE ");
         return whereClause.toString();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }


   
   public String deleteRows(Object[][] rowData, ColumnDisplayDefinition[] colDefs) {

      
      if (ti == null)
         return TI_ERROR_MESSAGE;

      
      final ISession session = _session;
      final ISQLConnection conn = session.getSQLConnection();

      
      
      String rowCountErrorMessage = "";

      
      
      for (int i = 0; i < rowData.length; i++) {
         
         
         
         String whereClause = getWhereClause(rowData[i], colDefs, -1, null);

         
         try {
            
            final Statement stmt = conn.createStatement();
            try
            {
               ResultSet rs = stmt.executeQuery("SELECT count(*) FROM " +
                  ti.getQualifiedName()+whereClause);

               rs.next();
               if (rs.getInt(1) != 1) {
                  if (rs.getInt(1) == 0) {
                      
                     rowCountErrorMessage += 
                         s_stringMgr.getString(
                                 "DataSetUpdateableTableModelImpl.error.rownotmatch",
                                 Integer.valueOf(i+1));
                  } else {
                      
                      rowCountErrorMessage += 
                          s_stringMgr.getString(
                                  "DataSetUpdateableTableModelImpl.error.rowmatched", 
                                  new Object[] { Integer.valueOf(i+1), Integer.valueOf(rs.getInt(1)) });
                  }
               }
            }
            finally
            {
               stmt.close();
            }
         }
         catch (Exception e) {
            
             
             return 
                 s_stringMgr.getString(
                         "DataSetUpdateableTableModelImpl.error.preparingdelete",
                         e);
         }
      }

      
      
      if (rowCountErrorMessage.length() > 0) {
          
          String msg = 
              s_stringMgr.getString("DataSetUpdateableTableModelImpl.error.tabledbmismatch",
                                    rowCountErrorMessage);
         
         int option = 
             JOptionPane.showConfirmDialog(null, msg, "Warning", 
                                           JOptionPane.YES_NO_OPTION, 
                                           JOptionPane.WARNING_MESSAGE);
         
         if ( option != JOptionPane.YES_OPTION) {
             
            return s_stringMgr.getString("DataSetUpdateableTableModelImpl.info.deletecancelled");
         }
      }

      
      for (int i = 0; i < rowData.length; i++) {
         
         
         
         String whereClause = getWhereClause(rowData[i], colDefs, -1, null);

         
         try {
            
            final Statement stmt = conn.createStatement();
            try
            {
               stmt.executeUpdate("DELETE FROM " +
                  ti.getQualifiedName() + whereClause);
            }
            finally
            {
               stmt.close();
            }
         }
         catch (Exception e) {
            
             
             return s_stringMgr.getString("DataSetUpdateableTableModelImpl.error.deleteFailed", e);
         }
      }

      return null;	
   }

   
   public String[] getDefaultValues(ColumnDisplayDefinition[] colDefs) {

      
      final String[] defaultValues = new String[colDefs.length];

      
      if (ti == null)
      {
         return defaultValues;
      }

      final ISession session = _session;
      final ISQLConnection conn = session.getSQLConnection();

      try
      {
         SQLDatabaseMetaData md = conn.getSQLMetaData();
         TableColumnInfo[] infos = md.getColumnInfo(ti);
         
         
         
         
         int expectedColDefIndex = 0;
         
         for (int idx = 0; idx < infos.length; idx++) {
             String colName = infos[idx].getColumnName();
             String defValue = infos[idx].getDefaultValue();
             
             
             
             
             if (defValue != null &&  defValue.length() > 0) {
                
                if (colDefs[expectedColDefIndex].getLabel().equals(colName)) {
                   
                   defaultValues[expectedColDefIndex] = defValue;
                }
                else {
                   
                   
                   
                   
                   for (int i=0; i<colDefs.length; i++) {
                      if (colDefs[i].getLabel().equals(colName)) {
                         defaultValues[i] = defValue;
                         break;
                      }
                   }
                }
             }

             
             
             expectedColDefIndex++;
             
         }
      }
      catch (Exception ex)
      {
          
          s_log.error(s_stringMgr.getString("DataSetUpdateableTableModelImpl.error.retrievingdefaultvalues"), ex);
      }

      return defaultValues;
   }


   
   public String insertRow(Object[] values, ColumnDisplayDefinition[] colDefs) {

      
      if (ti == null) {
         return TI_ERROR_MESSAGE;
      }
      
      final ISession session = _session;
      final ISQLConnection conn = session.getSQLConnection();
      
      int count = -1;
      
      try
      {
         
         StringBuilder buf = new StringBuilder("INSERT INTO ");
         buf.append(ti.getQualifiedName());

         
         
         buf.append(" ( ");
         for (int i=0; i<colDefs.length; i++) {
             if (i == _rowIDcol) {
                 continue;
             }
             if (colDefs[i].isAutoIncrement()) {
                 if (s_log.isInfoEnabled()) {
                     s_log.info("insertRow: skipping auto-increment column "+
                                colDefs[i].getColumnName());
                 }
                 continue;
             } 
             buf.append(colDefs[i].getColumnName());
             buf.append(",");
         }
         buf.setCharAt(buf.length()-1, ')');
         buf.append(" VALUES (");
         
         
         for (int i=0; i<colDefs.length; i++) {
            if (i != _rowIDcol && !colDefs[i].isAutoIncrement() )
                
               buf.append(" ?,");
         }

         
         buf.setCharAt(buf.length()-1, ')');

         String pstmtSQL = buf.toString();
         if (s_log.isInfoEnabled()) {
             s_log.info("insertRow: pstmt sql = "+pstmtSQL);
         }
         final PreparedStatement pstmt = conn.prepareStatement(pstmtSQL);

         try
         {
            
            
            
            int bindVarIdx = 1;
             
            
            
            for (int i=0; i<colDefs.length; i++) {
               if (i != _rowIDcol && !colDefs[i].isAutoIncrement()) {
                   CellComponentFactory.setPreparedStatementValue(
                           colDefs[i], pstmt, values[i], bindVarIdx);
                   bindVarIdx++;
               }
            }
            count = pstmt.executeUpdate();
         }
         finally
         {
            pstmt.close();
         }
      }
      catch (SQLException ex)
      {
          
          return s_stringMgr.getString(
                  "DataSetUpdateableTableModelImpl.error.duringInsert", 
                  ex.getMessage());
      }

      if (count != 1)
          
         return s_stringMgr.getString("DataSetUpdateableTableModelImpl.error.unknownerrorupdate");

      
      try {
          IObjectTreeAPI api = _session.getObjectTreeAPIOfActiveSessionWindow();
          api.refreshSelectedTab();
      } catch (Exception e) {
          e.printStackTrace();
      }

      return null;
   }

   public void addListener(DataSetUpdateableTableModelListener l)
   {
      _dataSetUpdateableTableModelListener.add(l);
   }

   public void removeListener(DataSetUpdateableTableModelListener l)
   {
      _dataSetUpdateableTableModelListener.remove(l);
   }


   public void setEditModeForced(boolean b)
   {
      editModeForced = b;
   }

   public void setRowIDCol(int rowIDCol)
   {
      _rowIDcol = rowIDCol;
   }
}
