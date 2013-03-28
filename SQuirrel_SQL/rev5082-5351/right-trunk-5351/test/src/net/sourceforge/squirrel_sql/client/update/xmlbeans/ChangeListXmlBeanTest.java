
package net.sourceforge.squirrel_sql.client.update.xmlbeans;

import static org.junit.Assert.assertNull;
import net.sourceforge.squirrel_sql.AbstractSerializableTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ChangeListXmlBeanTest extends AbstractSerializableTest
{

	ChangeListXmlBean classUnderTest = null;
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new ChangeListXmlBean();
		super.serializableToTest = new ChangeListXmlBean();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
		super.serializableToTest = null;
	}

	@Test
	public void testGetChanges() throws Exception
	{
		classUnderTest.setChanges(null);
		assertNull(classUnderTest.getChanges());
	}	
}
