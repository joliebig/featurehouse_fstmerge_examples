
package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.base.testing.EqualsTester;

public class ErrorInfoTest
{

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testEqualsAndHashCode()
	{
		ErrorInfo a = new ErrorInfo("message", 1, 10);
		ErrorInfo b = new ErrorInfo("message", 1, 10);
		ErrorInfo c = new ErrorInfo("message", 2, 5);
		ErrorInfo d = new ErrorInfo("message", 1, 10) {};
		
		new EqualsTester(a, b, c, d); 
	}

}
