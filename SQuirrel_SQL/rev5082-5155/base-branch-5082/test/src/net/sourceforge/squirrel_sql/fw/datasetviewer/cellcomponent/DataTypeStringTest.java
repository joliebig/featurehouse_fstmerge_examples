package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import org.junit.Before;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;




public class DataTypeStringTest extends AbstractDataTypeComponentTest {

	@Before
	public void setUp() throws Exception {
		ColumnDisplayDefinition mockColumnDisplayDefinition = getMockColumnDisplayDefinition();
		mockHelper.replayAll();
		classUnderTest = new DataTypeString(null, mockColumnDisplayDefinition);
		mockHelper.resetAll();
		super.setUp();
		
	}

	@Override
	protected Object getEqualsTestObject()
	{
		return "testString";
	}
}
