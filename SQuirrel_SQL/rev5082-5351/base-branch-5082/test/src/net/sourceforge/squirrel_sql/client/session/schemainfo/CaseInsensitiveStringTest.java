
package net.sourceforge.squirrel_sql.client.session.schemainfo;

import net.sourceforge.squirrel_sql.AbstractSerializableTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.base.testing.EqualsTester;

public class CaseInsensitiveStringTest extends AbstractSerializableTest
{
	
	
	@Before
	public void setUp() throws Exception
	{
		super.serializableToTest = new CaseInsensitiveString();
	}

	@After
	public void tearDown() throws Exception
	{
		super.serializableToTest = null;
	}

	@Test
	public void testEqualsObject()
	{
		CaseInsensitiveString a = new CaseInsensitiveString("A");
		CaseInsensitiveString b = new CaseInsensitiveString("A");
		CaseInsensitiveString c = new CaseInsensitiveString("B");
		CaseInsensitiveString d = new CaseInsensitiveString("A") {
			private static final long serialVersionUID = 1L;			
		};
		
		new EqualsTester(a, b, c, d);
	}

}
