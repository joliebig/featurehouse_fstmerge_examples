package net.sourceforge.squirrel_sql.plugins.codecompletion;

import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;

import java.sql.SQLException;
import java.util.ArrayList;


public abstract class CodeCompletionInfo extends CompletionInfo
{
   
   public ArrayList<CodeCompletionInfo> getColumns(net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo schemaInfo, String colNamePattern) throws SQLException
   {
      return new ArrayList<CodeCompletionInfo>();
   }


	
	public int getMoveCarretBackCount()
	{
		return 0;
	}
}
