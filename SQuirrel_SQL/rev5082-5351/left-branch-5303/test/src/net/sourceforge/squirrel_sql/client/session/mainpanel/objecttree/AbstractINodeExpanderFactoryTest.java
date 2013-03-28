
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import static org.junit.Assert.assertNotNull;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;

import org.junit.Test;

public abstract class AbstractINodeExpanderFactoryTest extends BaseSQuirreLJUnit4TestCase
{

	protected INodeExpanderFactory classUnderTest = null;

	public AbstractINodeExpanderFactoryTest()
	{
		super();
	}

	@Test
	public void testCreateExpander()
	{
		assertNotNull(classUnderTest.createExpander(DatabaseObjectType.SEQUENCE));
	}

	@Test
	public void testGetParentLabelForType()
	{
		assertNotNull(classUnderTest.getParentLabelForType(DatabaseObjectType.SEQUENCE));
	}

}