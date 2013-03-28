package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;



import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UnknownDialectException;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.sqlscript.FrameWorkAcessor;
import net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin;

public class CreateDataScriptCommand implements ICommand, InternalFrameListener
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(CreateDataScriptCommand.class);

   
   private static final ILogger s_log = 
       LoggerController.createLogger(CreateDataScriptCommand.class);

   
   private Boolean dialectSupportsSubSecondTimestamps = null; 
   
	protected JInternalFrame _statusFrame;
   protected boolean _bStop = false;

   
   protected ISession _session;

   
   private final SQLScriptPlugin _plugin;
   private boolean _templateScriptOnly;

   
   public CreateDataScriptCommand(ISession session, SQLScriptPlugin plugin, boolean templateScriptOnly)
   {
      super();
      _session = session;
      _plugin = plugin;
      _templateScriptOnly = templateScriptOnly;
   }

   protected void showAbortFrame()
   {
      if (_statusFrame == null)
      {
			
         JOptionPane optionPane = new JOptionPane(s_stringMgr.getString("sqlscript.abort"), JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION);

			
			_statusFrame = optionPane.createInternalFrame(_session.getSessionSheet(), s_stringMgr.getString("sqlscript.creatingDataScript"));
         _statusFrame.addInternalFrameListener(this);
      }
      _bStop = false;
      _statusFrame.setVisible(true);
   }

   protected void hideAbortFrame()
   {
      if (_statusFrame != null)
      {
         _statusFrame.removeInternalFrameListener(this);
         try
         {
            _statusFrame.setClosed(true);
         }
         catch (Exception e)
         {
         }
         _statusFrame.setVisible(false);
      }
   }

   
   public void execute()
   {
      final StringBuffer sbRows = new StringBuffer(1000);
      _session.getApplication().getThreadPool().addTask(new Runnable()
      {
         public void run()
         {
            ISQLConnection conn = _session.getSQLConnection();
            try
            {
               final Statement stmt = conn.createStatement();
               try
               {
                  
                  IObjectTreeAPI api = FrameWorkAcessor.getObjectTreeAPI(_session, _plugin);


                  IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();

                  for (int k = 0; k < dbObjs.length; k++)
                  {
                     if (dbObjs[k] instanceof ITableInfo)
                     {
                        if (_bStop) break;
                        ITableInfo ti = (ITableInfo) dbObjs[k];
                        String sTable = ScriptUtil.getTableName(ti);
                        StringBuffer sql = new StringBuffer();
                        sql.append("select * from ");
                        sql.append(ti.getQualifiedName());
                        
                        
                        if (!JDBCTypeMapper.isLongType(getFirstColumnType(ti))) 
                        {
                            sql.append(" order by ");
                            sql.append(getFirstColumnName(ti));
                            sql.append(" asc ");
                        } 
                        ResultSet srcResult = stmt.executeQuery(sql.toString());
                        genInserts(srcResult, sTable, sbRows, false);
                     }
                  }
               }
               finally
               {
                  try
                  {
                     stmt.close();
                  }
                  catch (Exception e)
                  {
                  }
               }
            }
            catch (Exception e)
            {
               _session.showErrorMessage(e);
            }
            SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  if (sbRows.length() > 0)
                  {

                     
                     FrameWorkAcessor.getSQLPanelAPI(_session, _plugin).appendSQLScript(sbRows.toString(), true);

                     _session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
                  }
                  hideAbortFrame();
               }
            });
         }
      });
      showAbortFrame();
   }
   
   protected String getFirstColumnName(ITableInfo ti) throws SQLException {
       TableColumnInfo[] infos = 
           _session.getSQLConnection().getSQLMetaData().getColumnInfo(ti);
       return infos[0].getColumnName();
   }

   protected int getFirstColumnType(ITableInfo ti) throws SQLException {
       TableColumnInfo[] infos = 
           _session.getSQLConnection().getSQLMetaData().getColumnInfo(ti);
       return infos[0].getDataType();
   }
   
   
   protected void genInserts(ResultSet srcResult, String sTable, StringBuffer sbRows, boolean headerOnly)
      throws SQLException
   {
      ResultSetMetaData metaData = srcResult.getMetaData();

      int iColumnCount = metaData.getColumnCount();
      ColumnInfo[] colInfo = new ColumnInfo[iColumnCount];


      for (int i = 1; i <= iColumnCount; i++)
      {
         colInfo[i-1] = new ColumnInfo(metaData.getColumnName(i), metaData.getColumnType(i));
      }

      
      
      boolean fromResultSet = !_templateScriptOnly && !headerOnly;

      sbRows.append("\n\n");
      Timestamp currentTime = new Timestamp(System.currentTimeMillis());
      while (srcResult.next() || _templateScriptOnly || headerOnly)
      {
         if (_bStop) break;
         sbRows.append("INSERT INTO ");
         StringBuffer sbValues = new StringBuffer();
         sbRows.append(sTable);
         sbRows.append(" (");
         sbValues.append(" VALUES (");

         ScriptUtil su = new ScriptUtil();

         for (int i = 0; i < iColumnCount; i++)
         {
            int iIndexPoint = colInfo[i].columnName.lastIndexOf('.');
            sbRows.append(su.makeColumnNameUnique(colInfo[i].columnName.substring(iIndexPoint + 1)));

            if (Types.TINYINT == colInfo[i].sqlType
               || Types.BIGINT == colInfo[i].sqlType
               || Types.SMALLINT == colInfo[i].sqlType
               || Types.INTEGER == colInfo[i].sqlType
               || Types.BIGINT == colInfo[i].sqlType
               || Types.FLOAT == colInfo[i].sqlType
               || Types.REAL == colInfo[i].sqlType
               || Types.DOUBLE == colInfo[i].sqlType
               || Types.NUMERIC == colInfo[i].sqlType
               || Types.DECIMAL == colInfo[i].sqlType
               || Types.NUMERIC == colInfo[i].sqlType
               || Types.NUMERIC == colInfo[i].sqlType)
            {
               Object value = fromResultSet ? srcResult.getObject(i + 1) : "0" + getNullableComment(metaData, i+1);
               sbValues.append(value);
            }
            else if (Types.DATE == colInfo[i].sqlType
               || Types.TIME == colInfo[i].sqlType
               || Types.TIMESTAMP == colInfo[i].sqlType)
            {
               Calendar calendar = Calendar.getInstance();
               java.util.Date timestamp = null;
               if (Types.DATE == colInfo[i].sqlType)
               {
                  timestamp = fromResultSet ? srcResult.getDate(i + 1): currentTime;
               }
               else if (Types.TIME == colInfo[i].sqlType)
               {
                  timestamp = fromResultSet ? srcResult.getTime(i + 1): currentTime;
               }
               else if (Types.TIMESTAMP == colInfo[i].sqlType)
               {
                  timestamp = fromResultSet ? srcResult.getTimestamp(i + 1): currentTime;
               }


               if (timestamp == null)
               {
                  sbValues.append("null");
               }
               else
               {
                  calendar.setTime(timestamp);

                  if (Types.DATE == colInfo[i].sqlType)
                  {
                     String esc = "{d '" + prefixNulls(calendar.get(Calendar.YEAR), 4) + "-" +
                        prefixNulls(calendar.get(Calendar.MONTH) + 1, 2) + "-" +
                        prefixNulls(calendar.get(Calendar.DAY_OF_MONTH), 2) + "'}";
                     esc = fromResultSet ? esc : esc + getNullableComment(metaData, i+1);
                     sbValues.append(esc);
                  }
                  else if (Types.TIME == colInfo[i].sqlType)
                  {
                     String esc = "{t '" + prefixNulls(calendar.get(Calendar.HOUR_OF_DAY), 2) + ":" +
                        prefixNulls(calendar.get(Calendar.MINUTE), 2) + ":" +
                        prefixNulls(calendar.get(Calendar.SECOND), 2) + "'}";
                     esc = fromResultSet ? esc : esc + getNullableComment(metaData, i+1);
                     sbValues.append(esc);
                  }
                  else if (Types.TIMESTAMP == colInfo[i].sqlType)
                  {
                  	Timestamp ts = (Timestamp)timestamp;
                  	
                     StringBuilder esc = new StringBuilder("{ts '");
                     esc.append(prefixNulls(calendar.get(Calendar.YEAR), 4)).append("-");
                     esc.append(prefixNulls(calendar.get(Calendar.MONTH) + 1, 2)).append("-");
                     esc.append(prefixNulls(calendar.get(Calendar.DAY_OF_MONTH), 2)).append(" ");
                     esc.append(prefixNulls(calendar.get(Calendar.HOUR_OF_DAY), 2)).append(":");
                     esc.append(prefixNulls(calendar.get(Calendar.MINUTE), 2)).append(":");
                     esc.append(prefixNulls(calendar.get(Calendar.SECOND), 2)).append(".");
                     esc.append(prefixNulls(calendar.get(Calendar.MILLISECOND), 3));
                     esc.append(getNanos(ts));
                     esc.append("'}");

                     if (!fromResultSet) {
                     	esc.append(getNullableComment(metaData, i+1));
                     }
                     sbValues.append(esc);
                  }

               }
            }
            else if (Types.BIT == colInfo[i].sqlType
                     || Types.BOOLEAN == colInfo[i].sqlType)
            {
               boolean iBoolean = fromResultSet ? srcResult.getBoolean(i + 1) : false;

               if(fromResultSet && srcResult.wasNull())
               {
                  sbValues.append("null");
               }
               else if (iBoolean)
               {
                   
                   if (DialectFactory.isPostgreSQL(_session.getMetaData())) {
                       sbValues.append("true");
                   } else {
                       sbValues.append(1);
                   }
               }
               else
               {
                   
                   if (DialectFactory.isPostgreSQL(_session.getMetaData())) {
                       sbValues.append("false");
                   } else {
                       sbValues.append(0);
                   }
               }

               if(false == fromResultSet)
               {
                  sbValues.append(getNullableComment(metaData, i+1));
               }
            }
            else 
                 
                 
                 
                 
                 
                 
                 
                 
                 
                 
                 
                 
                 
            {
               String sResult = fromResultSet ? srcResult.getString(i + 1) : "s";
               if (sResult == null)
               {
                  sbValues.append("null");
               }
               else
               {
                  int iIndex = sResult.indexOf("'");
                  if (iIndex != -1)
                  {
                     int iPrev = 0;
                     StringBuffer sb = new StringBuffer();
                     sb.append(sResult.substring(iPrev, iIndex));
                     sb.append('\'');
                     iPrev = iIndex;
                     iIndex = sResult.indexOf("'", iPrev + 1);
                     while (iIndex != -1)
                     {
                        sb.append(sResult.substring(iPrev, iIndex));
                        sb.append('\'');
                        iPrev = iIndex;
                        iIndex = sResult.indexOf("'", iPrev + 1);
                     }
                     sb.append(sResult.substring(iPrev));
                     sResult = sb.toString();
                  }

                  iIndex = sResult.indexOf('\n');
                  if (iIndex != -1)
                  {
                     int iPrev = 0;
                     StringBuffer sb = new StringBuffer();
                     sb.append(sResult.substring(iPrev, iIndex));
                     sb.append("\\n");
                     iPrev = iIndex + 1;
                     iIndex = sResult.indexOf('\n', iPrev + 1);
                     while (iIndex != -1)
                     {
                        sb.append(sResult.substring(iPrev, iIndex));
                        sb.append("\\n");
                        iPrev = iIndex + 1;
                        iIndex = sResult.indexOf('\n', iPrev + 1);
                     }
                     sb.append(sResult.substring(iPrev));
                     sResult = sb.toString();
                  }
                  sbValues.append("\'");
                  sbValues.append(sResult);
                  sbValues.append("\'");

                  if(false == fromResultSet)
                  {
                     sbValues.append(getNullableComment(metaData, i+1));
                  }
               }
            }
            sbValues.append(",");
            sbRows.append(",");
         }
         
         sbValues.setLength(sbValues.length() - 1);
         sbRows.setLength(sbRows.length() - 1);

         
         sbValues.append(")").append(getStatementSeparator()).append("\n");
         sbRows.append(")");

         if(false == headerOnly)
         {
            sbRows.append(sbValues.toString());
         }

         if(_templateScriptOnly || headerOnly)
         {
            break;
         }
      }
      srcResult.close();
   }

   
   private String getNanos(Timestamp ts) throws SQLException {
   	boolean dialectSupportsSubSecondTimestamps = getTimestampFlag();
   	if (!dialectSupportsSubSecondTimestamps) {
   		return "";
   	}
   	String nanos = ""+(ts.getNanos()/1000);
   	if (nanos.length() > 3) {
   		nanos = nanos.substring(3);
   	} else {
   		nanos = "";
   	}   	
   	return nanos;
   }
   
   
   private boolean getTimestampFlag() throws SQLException {
   	if (dialectSupportsSubSecondTimestamps == null) {
   		ISQLDatabaseMetaData md = _session.getMetaData();
      	try {
      		HibernateDialect dialect = DialectFactory.getDialect(md);
      		dialectSupportsSubSecondTimestamps = dialect.supportsSubSecondTimestamps();
      	} catch (UnknownDialectException e) {
      		
      		dialectSupportsSubSecondTimestamps = true;
      		if (s_log.isDebugEnabled()) {
      			s_log.debug("getTimestampFlag: database "+md.getDatabaseProductName()+" version "+
      				md.getDatabaseProductVersion()+" does not have a supported dialect.  Please submit a " +
      						"feature request to add a dialect for SQuirreL to better support this database.");
      		}
      	}   		
   	}
   	return dialectSupportsSubSecondTimestamps;
   }
   
   private String getNullableComment(ResultSetMetaData metaData, int colIndex) throws SQLException
   {
      if(ResultSetMetaData.columnNoNulls == metaData.isNullable(colIndex))
      {
         return " /*not nullable*/";
      }
      else
      {
         return "";
      }
   }

   private String prefixNulls(int toPrefix, int digitCount)
   {
      String ret = "" + toPrefix;

      while (ret.length() < digitCount)
      {
         ret = 0 + ret;
      }

      return ret;
   }


   private String getStatementSeparator()
   {
      String statementSeparator = 
          _session.getQueryTokenizer().getSQLStatementSeparator();

      if (1 < statementSeparator.length())
      {
         statementSeparator = "\n" + statementSeparator + "\n";
      }

      return statementSeparator;
   }


   
   public void internalFrameActivated(InternalFrameEvent e)
   {
   }

   
   public void internalFrameClosed(InternalFrameEvent e)
   {
      _bStop = true;
   }

   
   public void internalFrameClosing(InternalFrameEvent e)
   {
   }

   
   public void internalFrameDeactivated(InternalFrameEvent e)
   {
   }

   
   public void internalFrameDeiconified(InternalFrameEvent e)
   {
   }

   
   public void internalFrameIconified(InternalFrameEvent e)
   {
   }

   
   public void internalFrameOpened(InternalFrameEvent e)
   {
   }

   private static class ColumnInfo
   {
      int sqlType; 
      String columnName;

      public ColumnInfo(String columnName, int sqlType)
      {
         this.columnName = columnName;
         this.sqlType = sqlType;
      }
   }

}
