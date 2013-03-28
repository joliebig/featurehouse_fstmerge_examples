package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import static org.junit.Assert.assertFalse;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import org.junit.Before;
import org.junit.Test;




public class DataTypeOtherTest extends AbstractDataTypeComponentTest
{

	@Before
	public void setUp() throws Exception
	{
		ColumnDisplayDefinition mockColumnDisplayDefinition = getMockColumnDisplayDefinition();
		mockHelper.replayAll();
		classUnderTest = new DataTypeOther(null, mockColumnDisplayDefinition);
		mockHelper.resetAll();
		super.setUp();
		super.defaultValueIsNull = true;
	}

	@Override
	protected Object getEqualsTestObject()
	{
		
		return "aTestString";
	}

	@Override
	@Test
	public void testCanDoFileIO()
	{
		mockHelper.replayAll();
		assertFalse(classUnderTest.canDoFileIO());
		mockHelper.verifyAll();
	}

	@Test
	public void testIsEditableInCell()
	{
		mockHelper.replayAll();
		assertFalse(classUnderTest.isEditableInCell(""));
		assertFalse(classUnderTest.isEditableInCell(null));
		mockHelper.verifyAll();
	}

	@Test
	public void testIsEditableInPopup()
	{
		mockHelper.replayAll();
		assertFalse(classUnderTest.isEditableInPopup(""));
		assertFalse(classUnderTest.isEditableInPopup(null));
		mockHelper.verifyAll();
	}

}
