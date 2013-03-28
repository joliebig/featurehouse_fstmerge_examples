
package net.sourceforge.squirrel_sql.plugins.oracle.tab;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractBasePreparedStatementTabTest;

import org.junit.Before;

public class UserDetailsTabTest extends AbstractBasePreparedStatementTabTest
{

	@Before
	public void setUp() throws Exception
	{
		expect(mockSession.getApplication()).andStubReturn(mockApplication);
		expect(mockApplication.getThreadPool()).andStubReturn(mockThreadPool);
		mockThreadPool.addTask(isA(Runnable.class));

		mockHelper.replayAll();
		classUnderTest = new UserDetailsTab(mockSession);
		mockHelper.resetAll();
	}

}
