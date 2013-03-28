package net.sourceforge.squirrel_sql.client.session;

import java.util.HashMap;


import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessorFactory;

public interface ISQLEntryPanelFactory
{
	ISQLEntryPanel createSQLEntryPanel(ISession session, 
                                       HashMap<String, Object> props);
}
