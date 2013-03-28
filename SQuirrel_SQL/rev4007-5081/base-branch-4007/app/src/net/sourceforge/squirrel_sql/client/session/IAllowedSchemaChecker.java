package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;

public interface IAllowedSchemaChecker
{
   
   String[] getAllowedSchemas(ISQLConnection con, ISQLAliasExt alias);
}
