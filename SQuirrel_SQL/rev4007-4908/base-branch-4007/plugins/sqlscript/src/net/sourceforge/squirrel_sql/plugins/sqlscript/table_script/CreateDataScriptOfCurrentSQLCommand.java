package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;



import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.plugins.sqlscript.FrameWorkAcessor;
import net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin;

public class CreateDataScriptOfCurrentSQLCommand extends CreateDataScriptCommand
{

   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(CreateDataScriptOfCurrentSQLCommand.class);


   
   private final SQLScriptPlugin _plugin;

   
   public CreateDataScriptOfCurrentSQLCommand(ISession session, SQLScriptPlugin plugin)
   {
      super(session, plugin, false);
      _plugin = plugin;
   }

   
   public void execute()
   {
      _session.getApplication().getThreadPool().addTask(new Runnable()
      {
         public void run()
         {

            final StringBuffer sbRows = new StringBuffer(1000);

            try
            {
                ISQLPanelAPI api = 
                    FrameWorkAcessor.getSQLPanelAPI(_session, _plugin);
                
                String script = api.getSQLScriptToBeExecuted();
                
               IQueryTokenizer qt = _session.getQueryTokenizer();
               qt.setScriptToTokenize(script);
                
               if(false == qt.hasQuery())
               {
                  
                  _session.showErrorMessage(s_stringMgr.getString("CreateTableOfCurrentSQLCommand.noQuery"));
                  return;
               }



               ISQLConnection conn = _session.getSQLConnection();


               final Statement stmt = conn.createStatement();
               try
               {
                  String sql = qt.nextQuery();

                  ResultSet srcResult = stmt.executeQuery(sql);
                  ResultSetMetaData metaData = srcResult.getMetaData();
                  String sTable = metaData.getTableName(1);
                  if (sTable == null || sTable.equals(""))
                  {
                     int iFromIndex = 
                         StringUtilities.getTokenBeginIndex(sql, "from");
                     sTable = getNextToken(sql, iFromIndex + "from".length());
                  }
                  genInserts(srcResult, sTable, sbRows, false);
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
            finally
            {
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
         }
      });
      showAbortFrame();
   }

   private String getNextToken(String selectSQL, int startPos)
   {
      int curPos = startPos;
      while(curPos < selectSQL.length() && true == Character.isWhitespace(selectSQL.charAt(curPos)))
      {
         
         ++curPos;
      }

      int startPosTrimed = curPos;


      while(curPos < selectSQL.length() && false == Character.isWhitespace(selectSQL.charAt(curPos)))
      {
         ++curPos;
      }

      return selectSQL.substring(startPosTrimed, curPos);
   }

}