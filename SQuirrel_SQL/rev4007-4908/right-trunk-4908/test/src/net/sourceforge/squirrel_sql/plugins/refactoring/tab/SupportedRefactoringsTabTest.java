
package net.sourceforge.squirrel_sql.plugins.refactoring.tab;


import static org.easymock.EasyMock.expect;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.AbstractBaseDataSetTabTest;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;

import org.easymock.classextension.EasyMock;
import org.junit.Before;

public class SupportedRefactoringsTabTest extends AbstractBaseDataSetTabTest
{

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		
		ISession session = EasyMock.createMock(ISession.class);
		ISQLDatabaseMetaData metaData = EasyMock.createMock(ISQLDatabaseMetaData.class);
		expect(session.getMetaData()).andStubReturn(metaData);
		expect(metaData.getDatabaseProductName()).andStubReturn("Oracle");
		expect(metaData.getDatabaseProductVersion()).andStubReturn("10g");
		
		classUnderTest = new SupportedRefactoringsTab(session);
		clazz = SupportedRefactoringsTab.class;
	}

}
