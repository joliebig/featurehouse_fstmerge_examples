
package net.sourceforge.squirrel_sql.plugins.oracle.tab;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractBasePreparedStatementTabTest;

import org.easymock.EasyMock;
import org.junit.Before;

public class SessionDetailsTabTest extends AbstractBasePreparedStatementTabTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new SessionDetailsTab();
	}

	
	@Override
	protected void setupMockDatabaseObjectInfo()
	{
		super.setupMockDatabaseObjectInfo();
		EasyMock.expect(mockDatabaseObjectInfo.getSimpleName()).andReturn("1").anyTimes();
	}

}
