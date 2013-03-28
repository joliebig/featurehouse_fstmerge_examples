package net.sourceforge.squirrel_sql.fw.dialects;

import org.junit.After;
import org.junit.Before;


public class IngresDialectTest extends AbstractDialectExtTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new IngresDialectExt();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

}
