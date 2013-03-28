package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.awt.Frame;

import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;


public class DeleteAliasCommand implements ICommand
{
   
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(DeleteAliasCommand.class);

   
   private final IApplication _app;

   
   private Frame _frame;

   
   private SQLAlias _sqlAlias;

   
   public DeleteAliasCommand(IApplication app, Frame frame, SQLAlias sqlAlias)
   {
      super();
      if (app == null)
      {
         throw new IllegalArgumentException("Null IApplication passed");
      }
      if (sqlAlias == null)
      {
         throw new IllegalArgumentException("Null ISQLAlias passed");
      }

      _app = app;
      _frame = frame;
      _sqlAlias = sqlAlias;
   }

   
   public void execute()
   {
      if (Dialogs.showYesNo(_frame, s_stringMgr.getString("DeleteAliasCommand.confirm", _sqlAlias.getName())))
      {
         _app.getDataCache().removeAlias(_sqlAlias);
      }
   }
}
