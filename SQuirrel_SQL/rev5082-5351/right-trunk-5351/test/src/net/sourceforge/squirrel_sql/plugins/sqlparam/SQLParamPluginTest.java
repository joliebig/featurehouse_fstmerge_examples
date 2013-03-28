
package net.sourceforge.squirrel_sql.plugins.sqlparam;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import net.sourceforge.squirrel_sql.client.gui.session.SessionPanel;
import net.sourceforge.squirrel_sql.client.plugin.AbstractPluginTest;
import net.sourceforge.squirrel_sql.client.plugin.DatabaseProductVersionData;
import net.sourceforge.squirrel_sql.client.plugin.ISessionPlugin;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class SQLParamPluginTest extends AbstractPluginTest implements DatabaseProductVersionData
{	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new SQLParamPlugin();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}		

	
	
	
	
	
	@Test
	public void testMultipleSessionEndings() {
		
		ISession mockSession = mockHelper.createMock("mockSession", ISession.class);
		SessionPanel mockSessionPanel = mockHelper.createMock("mockSessionPanel", SessionPanel.class);
		ISQLPanelAPI mockSQLPanelAPI = mockHelper.createMock("mockSQLPanelAPI", ISQLPanelAPI.class); 
		EasyMock.makeThreadSafe(mockSQLPanelAPI, true);
		
		expect(mockSession.getSessionSheet()).andStubReturn(mockSessionPanel);
		expect(mockSessionPanel.getSQLPaneAPI()).andStubReturn(mockSQLPanelAPI);
		mockSQLPanelAPI.addSQLExecutionListener(isA(ISQLExecutionListener.class));
		expectLastCall().anyTimes();
		mockSQLPanelAPI.removeSQLExecutionListener(isA(ISQLExecutionListener.class));
		expectLastCall().anyTimes();
		
		mockHelper.replayAll();
		
		((ISessionPlugin)classUnderTest).sessionCreated(mockSession);
		((ISessionPlugin)classUnderTest).sessionStarted(mockSession);
		
		Thread.yield();
		
		((ISessionPlugin)classUnderTest).sessionEnding(mockSession);
		((ISessionPlugin)classUnderTest).sessionEnding(mockSession);

		
		mockHelper.verifyAll();
	}
	
}
