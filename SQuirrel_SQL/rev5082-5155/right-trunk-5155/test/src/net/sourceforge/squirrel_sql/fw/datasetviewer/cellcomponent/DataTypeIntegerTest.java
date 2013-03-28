package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import org.junit.Before;




public class DataTypeIntegerTest extends AbstractDataTypeComponentTest {

	@Before
	public void setUp() throws Exception {
		ColumnDisplayDefinition mockColumnDisplayDefinition = getMockColumnDisplayDefinition();
		mockHelper.replayAll();
		classUnderTest = new DataTypeInteger(null, mockColumnDisplayDefinition);
		mockHelper.resetAll();
		super.setUp();
		
	}

	@Override
	protected Object getEqualsTestObject()
	{
		return Integer.valueOf(1);
	}

	
}
