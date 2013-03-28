package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import java.math.BigDecimal;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import org.junit.Before;




public class DataTypeBigDecimalTest extends AbstractDataTypeComponentTest
{

	@Before
	public void setUp() throws Exception
	{
		ColumnDisplayDefinition columnDisplayDefinition = getMockColumnDisplayDefinition();
		mockHelper.replayAll();
		classUnderTest = new DataTypeBigDecimal(null, columnDisplayDefinition);
		mockHelper.resetAll();
		super.setUp();
	}

	@Override
	protected Object getEqualsTestObject()
	{
		return BigDecimal.ONE;
	}

}
