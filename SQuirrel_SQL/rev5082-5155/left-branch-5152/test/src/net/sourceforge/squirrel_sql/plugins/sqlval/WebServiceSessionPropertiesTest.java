
package net.sourceforge.squirrel_sql.plugins.sqlval;


import net.sourceforge.squirrel_sql.AbstractSerializableTest;

import org.junit.After;
import org.junit.Before;

public class WebServiceSessionPropertiesTest extends AbstractSerializableTest
{

	WebServicePreferences mockPrefs = mockHelper.createMock(WebServicePreferences.class); 
	
	@Before
	public void setUp() throws Exception
	{
		super.serializableToTest = new WebServiceSessionProperties(mockPrefs);
	}

	@After
	public void tearDown() throws Exception
	{
	}

}
