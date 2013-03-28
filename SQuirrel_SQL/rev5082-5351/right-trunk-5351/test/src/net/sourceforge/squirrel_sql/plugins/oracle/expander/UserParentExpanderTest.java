
package net.sourceforge.squirrel_sql.plugins.oracle.expander;


import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.AbstractINodeExpanderTest;

import org.junit.Before;

public class UserParentExpanderTest extends AbstractINodeExpanderTest
{

	@Before
	public void setUp() throws Exception
	{
		expect(mockSession.getSQLConnection()).andStubReturn(mockSQLConnection);
		
		expect(mockSQLConnection.prepareStatement(isA(String.class))).andStubReturn(mockPreparedStatement);
		expect(mockPreparedStatement.executeQuery()).andStubReturn(mockResultSet);
		mockResultSet.close();
		expect(mockResultSet.getStatement()).andReturn(mockPreparedStatement);
		mockPreparedStatement.close();
		mockHelper.replayAll();
		classUnderTest = new UserParentExpander(mockSession);
		mockHelper.verifyAll();
		mockHelper.resetAll();
	}

}
