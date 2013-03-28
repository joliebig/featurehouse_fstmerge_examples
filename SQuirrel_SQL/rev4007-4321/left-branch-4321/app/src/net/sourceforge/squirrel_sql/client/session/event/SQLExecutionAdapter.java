package net.sourceforge.squirrel_sql.client.session.event;

import java.util.List;

public class SQLExecutionAdapter implements ISQLExecutionListener
{

	
	public String statementExecuting(String sql)
	{
		return sql;
	}

   public void statementExecuted(String sql)
   {
   }
}
