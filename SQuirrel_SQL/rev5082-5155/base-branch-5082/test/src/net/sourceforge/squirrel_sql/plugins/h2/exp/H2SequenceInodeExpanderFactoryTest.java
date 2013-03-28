
package net.sourceforge.squirrel_sql.plugins.h2.exp;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.AbstractINodeExpanderFactoryTest;

import org.junit.After;
import org.junit.Before;

public class H2SequenceInodeExpanderFactoryTest extends AbstractINodeExpanderFactoryTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new H2SequenceInodeExpanderFactory();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

}
