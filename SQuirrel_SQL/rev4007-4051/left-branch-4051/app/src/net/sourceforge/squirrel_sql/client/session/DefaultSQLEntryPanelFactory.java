package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.client.session.ISession;

import java.util.Properties;
import java.util.HashMap;


public class DefaultSQLEntryPanelFactory implements ISQLEntryPanelFactory
{
	
	public ISQLEntryPanel createSQLEntryPanel(ISession session, HashMap<String, Object> props)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}

		return new DefaultSQLEntryPanel(session);
	}
}
