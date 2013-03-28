package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import static org.junit.Assert.assertFalse;

import java.sql.Date;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import org.junit.Before;
import org.junit.Test;




public class DataTypeDateTest extends AbstractDataTypeComponentTest
{

	@Before
	public void setUp() throws Exception
	{
		ColumnDisplayDefinition columnDisplayDefinition = getMockColumnDisplayDefinition();
		mockHelper.replayAll();
		classUnderTest = new DataTypeDate(null, columnDisplayDefinition);
		mockHelper.resetAll();
		super.setUp();
	}

	
	
	@Test
	public void testGetReadDateAsTimestamp()
	{
		assertFalse("Expected default value to be false for read date as timestamp",
			DataTypeDate.getReadDateAsTimestamp());
	}

	@Override
	protected Object getEqualsTestObject()
	{
		return new Date(System.currentTimeMillis());
	}

}
