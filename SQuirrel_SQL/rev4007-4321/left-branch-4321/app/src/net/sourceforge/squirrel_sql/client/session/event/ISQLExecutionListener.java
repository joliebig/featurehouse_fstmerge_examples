package net.sourceforge.squirrel_sql.client.session.event;

import java.util.EventListener;
import java.util.List;

public interface ISQLExecutionListener extends EventListener
{

   
	String statementExecuting(String sql);

   
   void statementExecuted(String sql);
}
