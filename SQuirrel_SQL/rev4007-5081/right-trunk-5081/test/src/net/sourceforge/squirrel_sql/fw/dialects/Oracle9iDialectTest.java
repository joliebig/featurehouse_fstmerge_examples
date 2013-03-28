package net.sourceforge.squirrel_sql.fw.dialects;

import org.junit.After;
import org.junit.Before;


public class Oracle9iDialectTest extends AbstractDialectExtTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new OracleDialectExt();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

}
