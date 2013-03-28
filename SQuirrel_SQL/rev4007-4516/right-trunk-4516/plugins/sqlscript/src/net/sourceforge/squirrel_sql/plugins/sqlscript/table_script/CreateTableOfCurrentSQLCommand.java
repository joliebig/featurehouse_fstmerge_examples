package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;



import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.session.DefaultSQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.sqlscript.FrameWorkAcessor;
import net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin;

public class CreateTableOfCurrentSQLCommand extends CreateDataScriptCommand
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(CreateTableOfCurrentSQLCommand.class);


   
   private final SQLScriptPlugin _plugin;

   
   public CreateTableOfCurrentSQLCommand(ISession session, SQLScriptPlugin plugin)
   {
      super(session, plugin, true);
      _plugin = plugin;
   }

   
   public void execute()
   {

      CreateTableOfCurrentSQLCtrl ctrl = new CreateTableOfCurrentSQLCtrl(_session);

      if(false == ctrl.isOK())
      {
         return;
      }

      final String sTable = ctrl.getTableName();
      final boolean scriptOnly = ctrl.isScriptOnly();
      final boolean dropTable = ctrl.isDropTable();



      _session.getApplication().getThreadPool().addTask(new Runnable()
      {
         public void run()
         {

            doCreateTableOfCurrentSQL(sTable, scriptOnly, dropTable);
         }
      });
      showAbortFrame();
   }

   private void doCreateTableOfCurrentSQL(final String sTable, final boolean scriptOnly, final boolean dropTable)
   {

      final StringBuffer sbScript = new StringBuffer();
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
         Statement stmt = null;
         try
         {
            StringBuffer sbCreate = new StringBuffer();
            StringBuffer sbInsert = new StringBuffer();
            StringBuffer sbDrop = new StringBuffer();
            String statSep = ScriptUtil.getStatementSeparator(_session);



            stmt = conn.createStatement();
            stmt.setMaxRows(1);
            String sql = qt.nextQuery();
            ResultSet srcResult = stmt.executeQuery(sql);

            genCreate(srcResult, sTable, sbCreate);

            genInserts(srcResult, sTable, sbInsert, true);
            sbInsert.append('\n').append(sql);

            sbDrop.append("DROP TABLE " + sTable);

            if(dropTable && _session.getSchemaInfo().isTable(sTable))
            {
               sbScript.append(sbDrop).append(statSep);
            }

            sbScript.append(sbCreate).append(statSep);
            sbScript.append(sbInsert).append(statSep);
         }
         finally
         {
            try {stmt.close();} catch (Exception e) {}
         }



         if (false == scriptOnly)
         {
            try
            {
               SQLExecuterTask executer = new SQLExecuterTask(_session, sbScript.toString(), new DefaultSQLExecuterHandler(_session));
               executer.run();

               
               _session.showMessage(s_stringMgr.getString("sqlscript.successCreate", sTable));
            }
            catch (Exception e)
            {
               _session.showErrorMessage(e);

               
               String msg = s_stringMgr.getString("sqlscript.storeSqlInTableFailed", sTable);
               JOptionPane.showMessageDialog(_session.getApplication().getMainFrame(), msg);
            }

         }
      }
      catch (Exception e)
      {
         _session.showErrorMessage(e);
         e.printStackTrace();
      }
      finally
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               hideAbortFrame();
               if (scriptOnly && 0 < sbScript.toString().trim().length())
               {
                  FrameWorkAcessor.getSQLPanelAPI(_session, _plugin).appendSQLScript(sbScript.toString(), true);
                  _session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
               }
            }
         });
      }
   }

   private void genCreate(ResultSet srcResult, String sTable, StringBuffer sbCreate)
   {
      try
      {
         ResultSetMetaData metaData = srcResult.getMetaData();

         sbCreate.append("\n\nCREATE TABLE ").append(sTable).append('\n');
         sbCreate.append("(\n");

         ScriptUtil su = new ScriptUtil();

         String sColName = metaData.getColumnName(1);
         String sColType = metaData.getColumnTypeName(1);
         int colSize = metaData.getColumnDisplaySize(1);
         int decimalDigits =  metaData.getScale(1);
         sbCreate.append("   ").append(su.getColumnDef(sColName, sColType, colSize, decimalDigits));

         for(int i=2; i <= metaData.getColumnCount(); ++i)
         {
            sbCreate.append(",\n");

            sColName = metaData.getColumnName(i);
            sColType = metaData.getColumnTypeName(i);
            colSize = metaData.getColumnDisplaySize(i);
            decimalDigits =  metaData.getScale(i);
            sbCreate.append("   ").append(su.getColumnDef(sColName, sColType, colSize, decimalDigits));
         }

         sbCreate.append("\n)");

      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   private String getTableName()
   {
      return "ygwaTest";
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

   private int getTokenBeginIndex(String selectSQL, String token)
   {
      String lowerSel = selectSQL.toLowerCase();
      String lowerToken = token.toLowerCase().trim();

      int curPos = 0;
      while(-1 != curPos)
      {
         curPos = lowerSel.indexOf(lowerToken);

         if(
                -1 < curPos
             && (0 == curPos || Character.isWhitespace(lowerSel.charAt(curPos-1)))
             && (lowerSel.length() == curPos + lowerToken.length() || Character.isWhitespace(lowerSel.charAt(curPos + lowerToken.length())))
           )
         {
            return curPos;
         }
      }

      return curPos;
   }
}