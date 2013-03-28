package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;


public class CopyAliasCommand implements ICommand
{
   
   private final IApplication _app;

   
   private final SQLAlias _sqlAlias;

   
   public CopyAliasCommand(IApplication app, SQLAlias sqlAlias)
   {
      super();
      if (app == null)
      {
         throw new IllegalArgumentException("IApplication == null");
      }
      if (sqlAlias == null)
      {
         throw new IllegalArgumentException("Null ISQLAlias passed");
      }

      _app = app;
      _sqlAlias = sqlAlias;
   }

   public void execute()
   {
      _app.getWindowManager().showCopyAliasInternalFrame(_sqlAlias);
   }
}
