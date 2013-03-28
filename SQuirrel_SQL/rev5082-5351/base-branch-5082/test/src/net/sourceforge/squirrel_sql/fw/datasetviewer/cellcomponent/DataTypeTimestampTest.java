package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import static java.lang.System.currentTimeMillis;

import java.sql.Timestamp;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import org.junit.Before;




public class DataTypeTimestampTest extends AbstractDataTypeComponentTest {

	@Before
	public void setUp() throws Exception {
		ColumnDisplayDefinition mockColumnDisplayDefinition = getMockColumnDisplayDefinition();
		mockHelper.replayAll();
		classUnderTest = new DataTypeTimestamp(null, mockColumnDisplayDefinition);
		mockHelper.resetAll();
		super.setUp();
		
	}

	@Override
	protected Object getEqualsTestObject()
	{
		return new Timestamp(currentTimeMillis());
	}

	
}
