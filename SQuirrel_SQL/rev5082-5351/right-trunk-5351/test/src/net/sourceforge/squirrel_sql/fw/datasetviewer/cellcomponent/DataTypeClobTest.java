package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import static org.easymock.EasyMock.expect;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import org.junit.Before;

import utils.EasyMockHelper;




public class DataTypeClobTest extends AbstractDataTypeComponentTest {

	EasyMockHelper localMockHelper = new EasyMockHelper();
	
	@Before
	public void setUp() throws Exception {
		ColumnDisplayDefinition mockColumnDisplayDefinition = 
			localMockHelper.createMock("testCDD", ColumnDisplayDefinition.class);
		org.easymock.classextension.EasyMock.expect(mockColumnDisplayDefinition.isNullable()).andStubReturn(false);
		org.easymock.classextension.EasyMock.replay(mockColumnDisplayDefinition);
		
		classUnderTest = new DataTypeClob(null, mockColumnDisplayDefinition);

		super.setUp();
		super.defaultValueIsNull = true;
	}

	@Override
	protected Object getEqualsTestObject()
	{
		ClobDescriptor result = mockHelper.createMock("testClobDescriptor", ClobDescriptor.class);
		expect(result.getWholeClobRead()).andStubReturn(true);
		expect(result.getData()).andStubReturn("aTestString");
		ClobDescriptor nullClobDesc = null;
		expect(result.equals(nullClobDesc)).andStubReturn(false);
		return result;
	}

	
	@Override
	protected Object getWhereClauseValueObject()
	{

		return new ClobDescriptor(null, "testValue", true, true, -1);
	}

}
