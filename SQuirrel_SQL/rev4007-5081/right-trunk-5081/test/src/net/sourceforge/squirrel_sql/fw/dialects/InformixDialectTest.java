package net.sourceforge.squirrel_sql.fw.dialects;

import org.junit.After;
import org.junit.Before;


public class InformixDialectTest extends AbstractDialectExtTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new InformixDialectExt();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

}
