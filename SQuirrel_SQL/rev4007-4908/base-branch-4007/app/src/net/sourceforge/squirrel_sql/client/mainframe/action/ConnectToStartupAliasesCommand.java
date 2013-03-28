package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.DataCache;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;


public class ConnectToStartupAliasesCommand implements ICommand
{
   
   private final IApplication _app;

   
   public ConnectToStartupAliasesCommand(IApplication app)
   {
      super();
      if (app == null)
      {
         throw new IllegalArgumentException("IApplication == null");
      }

      _app = app;
   }

   public void execute()
   {
      final List<ISQLAlias> aliases = new ArrayList<ISQLAlias>();
      final DataCache cache = _app.getDataCache();
      synchronized (cache)
      {
         for (Iterator<ISQLAlias> it = cache.aliases(); it.hasNext();)
         {
            ISQLAlias alias = it.next();
            if (alias.isConnectAtStartup())
            {
               aliases.add(alias);
            }
         }
      }
      final Iterator<ISQLAlias> it = aliases.iterator();
      while (it.hasNext())
      {
         final SQLAlias alias = (SQLAlias) it.next();
         new ConnectToAliasCommand(_app, alias).execute();
      }
   }
}
