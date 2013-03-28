package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import org.junit.Before;




public class DataTypeShortTest extends AbstractDataTypeComponentTest {


	@Before
	public void setUp() throws Exception {
		ColumnDisplayDefinition mockColumnDisplayDefinition = getMockColumnDisplayDefinition();
		mockHelper.replayAll();
		classUnderTest = new DataTypeShort(null, mockColumnDisplayDefinition);
		mockHelper.resetAll();
		super.setUp();
		
	}

	@Override
	protected Object getEqualsTestObject()
	{
		return Short.valueOf((short) 1);
	}

}
