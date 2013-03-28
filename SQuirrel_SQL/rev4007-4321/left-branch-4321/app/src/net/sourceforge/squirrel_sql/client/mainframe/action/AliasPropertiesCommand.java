package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.AliasPropertiesController;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.IApplication;

public class AliasPropertiesCommand
{
   private ISQLAlias _selectedAlias;
   private IApplication _app;

   public AliasPropertiesCommand(ISQLAlias selectedAlias, IApplication app)
   {
      _selectedAlias = selectedAlias;
      _app = app;
   }

   public void execute()
   {
      
      AliasPropertiesController.showAliasProperties(_app, (SQLAlias)_selectedAlias);
   }
}
