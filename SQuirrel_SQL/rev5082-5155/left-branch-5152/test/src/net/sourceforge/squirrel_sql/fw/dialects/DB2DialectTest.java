package net.sourceforge.squirrel_sql.fw.dialects;

import org.junit.After;
import org.junit.Before;


public class DB2DialectTest extends AbstractDialectExtTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new DB2DialectExt();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

}
