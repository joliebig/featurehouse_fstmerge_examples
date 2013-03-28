
package net.sourceforge.squirrel_sql.plugins.informix.exp;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.AbstractINodeExpanderFactoryTest;

import org.junit.After;
import org.junit.Before;

public class InformixSequenceInodeExpanderFactoryTest extends AbstractINodeExpanderFactoryTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new InformixSequenceInodeExpanderFactory();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

}
