package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.IAliasesList;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;


public class ConnectToAliasAction extends AliasAction
{
   private static final long serialVersionUID = 1L;

   
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
