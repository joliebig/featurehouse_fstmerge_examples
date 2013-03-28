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


public class ConnectToAliasAction extends AliasAction
{

   
   private final IAliasesList _aliases;

   
   public ConnectToAliasAction(IApplication app, IAliasesList list)
   {
      super(app);
      _aliases = list;
   }

   
   public void actionPerformed(ActionEvent evt)
   {
      moveToFrontAndSelectAliasFrame();      
      final SQLAlias alias = _aliases.getSelectedAlias();
      if (alias != null)
      {
         new ConnectToAliasCommand(getApplication(), alias).execute();
      }
   }
}
