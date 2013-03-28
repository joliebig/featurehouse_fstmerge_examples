package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import org.junit.Before;




public class DataTypeUnknownTest extends AbstractDataTypeComponentTest
{

	@Before
	public void setUp() throws Exception
	{
		ColumnDisplayDefinition columnDisplayDefinition = getMockColumnDisplayDefinition();
		mockHelper.replayAll();
		classUnderTest = new DataTypeUnknown(null, columnDisplayDefinition);
		mockHelper.resetAll();
		super.setUp();
		super.defaultValueIsNull = true;
		super.canDoFileIO = false;
		super.isEditableInCell = false;
		super.isEditableInPopup = false;
	}

	@Override
	protected Object getEqualsTestObject()
	{
		
		return "aTestString";
	}

}
