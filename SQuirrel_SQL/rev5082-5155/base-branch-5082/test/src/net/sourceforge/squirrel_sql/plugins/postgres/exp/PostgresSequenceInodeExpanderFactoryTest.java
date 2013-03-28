
package net.sourceforge.squirrel_sql.plugins.postgres.exp;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.AbstractINodeExpanderFactoryTest;

import org.junit.After;
import org.junit.Before;

public class PostgresSequenceInodeExpanderFactoryTest extends AbstractINodeExpanderFactoryTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new PostgresSequenceInodeExpanderFactory();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

}
