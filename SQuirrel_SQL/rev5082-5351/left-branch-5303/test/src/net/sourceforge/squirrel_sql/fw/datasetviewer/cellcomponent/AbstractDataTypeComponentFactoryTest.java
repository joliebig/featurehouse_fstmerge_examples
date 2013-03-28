
package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import static org.junit.Assert.assertNotNull;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.Test;

public abstract class AbstractDataTypeComponentFactoryTest extends BaseSQuirreLJUnit4TestCase
{

	protected IDataTypeComponentFactory classUnderTest = null;
	
	@Test
	public void testConstructDataTypeComponent()
	{
		assertNotNull(classUnderTest.constructDataTypeComponent());
	}

	@Test
	public void testGetDialectType()
	{
		assertNotNull(classUnderTest.getDialectType());
	}

}
