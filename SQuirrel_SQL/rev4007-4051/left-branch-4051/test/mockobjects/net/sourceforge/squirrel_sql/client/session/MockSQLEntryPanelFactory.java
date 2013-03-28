package net.sourceforge.squirrel_sql.client.session;

import java.util.HashMap;

import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessorFactory;

public class MockSQLEntryPanelFactory implements ISQLEntryPanelFactory {

	MockSQLEntryPanel panel = null;
	
	public MockSQLEntryPanelFactory() {
		
	}
	
	public ISQLEntryPanel createSQLEntryPanel(ISession session) {
        panel = new MockSQLEntryPanel(session);
        return panel;
	}

    
    public ISQLEntryPanel createSQLEntryPanel(ISession session, 
                                              HashMap<String, Object> props) {
        return createSQLEntryPanel(session);
    }

}
