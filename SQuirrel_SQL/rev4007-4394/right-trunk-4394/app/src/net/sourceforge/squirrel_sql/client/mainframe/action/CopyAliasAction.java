package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;

import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.db.AliasesListInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.db.IAliasesList;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;


public class CopyAliasAction extends AliasAction
{

   
   private final IAliasesList _aliases;

   
   public CopyAliasAction(IApplication app, IAliasesList list)
   {
      super(app);
      if (list == null)
      {
         throw new IllegalArgumentException("Null AliasesList passed");
      }
      _aliases = list;
   }

   
   public void actionPerformed(ActionEvent evt)
   {
      moveToFrontAndSelectAliasFrame();
      SQLAlias alias = _aliases.getSelectedAlias();
      if (alias != null)
      {
         new CopyAliasCommand(getApplication(), alias).execute();
      }
   }
}
