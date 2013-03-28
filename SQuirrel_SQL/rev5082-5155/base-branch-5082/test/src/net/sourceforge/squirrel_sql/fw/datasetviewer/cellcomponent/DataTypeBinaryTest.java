package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import static org.junit.Assert.assertNotNull;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;




public class DataTypeBinaryTest extends AbstractDataTypeComponentTest
{

	public void setUp() throws Exception
	{
		ColumnDisplayDefinition columnDisplayDefinition = super.getMockColumnDisplayDefinition();
		mockHelper.replayAll();
		classUnderTest = new DataTypeBinary(null, columnDisplayDefinition);
		mockHelper.resetAll();
		super.setUp();
	}

	@Override
	protected Object getEqualsTestObject()
	{
		return "aTestString".getBytes();
	}

	
	@Override
	public void testGetClassName() throws Exception
	{
		
		assertNotNull(classUnderTest.getClassName());
	}

}
