
package net.sourceforge.squirrel_sql.fw.dialects;

import org.junit.After;
import org.junit.Before;

public class PointbaseDialectTest extends AbstractDialectExtTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new PointbaseDialectExt();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

}
