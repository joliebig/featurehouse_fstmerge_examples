
package net.sourceforge.squirrel_sql.plugins.derby.types;


import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;

import java.awt.Component;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.AbstractDataTypeComponentTest;

public class DerbyClobDataTypeComponentTest extends AbstractDataTypeComponentTest
{

	
	@Override
	public void setUp() throws Exception
	{
		mockColumnDisplayDefinition = super.getMockColumnDisplayDefinition();
		mockHelper.replayAll();
		classUnderTest = new DerbyClobDataTypeComponent();
		classUnderTest.setColumnDisplayDefinition(mockColumnDisplayDefinition);
		mockHelper.resetAll();
		
		classUnderTest.setBeepHelper(mockBeepHelper);

		expect(mockMetaData.getDatabaseProductName()).andStubReturn("testDatabaseProductName");
		expect(mockMetaData.getDatabaseProductVersion()).andStubReturn("testDatabaseProductVersion");
		mockBeepHelper.beep(isA(Component.class));
		expectLastCall().anyTimes();
		
		super.defaultValueIsNull = true;

	}

	@Override
	protected Object getEqualsTestObject()
	{
		return new DerbyClobDescriptor("aTestString");
	}

	@Override
	protected Object getWhereClauseValueObject()
	{
		return new DerbyClobDescriptor("aTestValue");
	}
	
	

}
