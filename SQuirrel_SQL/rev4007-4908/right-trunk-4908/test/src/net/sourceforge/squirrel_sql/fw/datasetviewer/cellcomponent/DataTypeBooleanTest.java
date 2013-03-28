package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;




public class DataTypeBooleanTest extends AbstractDataTypeComponentTest {

	public void setUp() throws Exception {
		ColumnDisplayDefinition columnDisplayDefinition = getMockColumnDisplayDefinition(); 
		mockHelper.replayAll();
		classUnderTest = new DataTypeBoolean(null, columnDisplayDefinition);
		mockHelper.resetAll();
		super.setUp();
	}

	@Override
	protected Object getEqualsTestObject()
	{
		return Boolean.TRUE;
	}

}
