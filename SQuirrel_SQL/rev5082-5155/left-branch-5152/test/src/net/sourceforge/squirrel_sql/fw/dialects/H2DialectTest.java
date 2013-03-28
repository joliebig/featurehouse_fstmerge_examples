package net.sourceforge.squirrel_sql.fw.dialects;

import org.junit.After;
import org.junit.Before;


public class H2DialectTest extends AbstractDialectExtTest {

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new H2DialectExt();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

    
}
