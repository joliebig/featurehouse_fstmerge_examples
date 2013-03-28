package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;




public class DataTypeDoubleTest extends AbstractDataTypeComponentTest {

	
	@Override
	protected Object getWhereClauseValueObject()
	{
		return null;
	}

	public void setUp() throws Exception {
		ColumnDisplayDefinition columnDisplayDefinition = 
			super.getMockColumnDisplayDefinition();
		mockHelper.replayAll();
		classUnderTest = new DataTypeDouble(null, columnDisplayDefinition);
		mockHelper.resetAll();
		super.setUp();
	}

	@Override
	protected Object getEqualsTestObject()
	{
		return new Double(0);
	}


}
