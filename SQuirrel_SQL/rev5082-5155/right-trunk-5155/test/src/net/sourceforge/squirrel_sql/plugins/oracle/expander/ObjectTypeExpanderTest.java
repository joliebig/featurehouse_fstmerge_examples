
package net.sourceforge.squirrel_sql.plugins.oracle.expander;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.AbstractINodeExpanderTest;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;

import org.junit.Before;
import org.junit.Test;

public class ObjectTypeExpanderTest extends AbstractINodeExpanderTest
{

	@Before
	public void setUp() throws Exception
	{
		ObjectType testObjectType =
			new ObjectType(DatabaseObjectType.TABLE_TYPE_DBO, "testColumnData", DatabaseObjectType.TABLE);

		classUnderTest = new ObjectTypeExpander(testObjectType);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testContructorNullArg() {
		classUnderTest = new ObjectTypeExpander(null);
	}
}
