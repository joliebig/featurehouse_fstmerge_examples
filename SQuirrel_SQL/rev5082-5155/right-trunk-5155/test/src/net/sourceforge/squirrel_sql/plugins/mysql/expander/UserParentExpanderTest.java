
package net.sourceforge.squirrel_sql.plugins.mysql.expander;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.AbstractINodeExpanderTest;
import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;

import org.junit.Before;

public class UserParentExpanderTest extends AbstractINodeExpanderTest
{
	@Before
	public void setUp() throws Exception
	{
		MysqlPlugin mockPlugin = mockHelper.createMock(MysqlPlugin.class);
		classUnderTest = new UserParentExpander(mockPlugin);
		clazz = UserParentExpander.class;
	}
	

}
