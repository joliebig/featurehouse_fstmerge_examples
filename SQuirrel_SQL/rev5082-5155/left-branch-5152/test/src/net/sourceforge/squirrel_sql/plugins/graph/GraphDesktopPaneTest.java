
package net.sourceforge.squirrel_sql.plugins.graph;


import net.sourceforge.squirrel_sql.AbstractSerializableTest;
import net.sourceforge.squirrel_sql.client.IApplication;

import org.junit.Before;

import utils.EasyMockHelper;

public class GraphDesktopPaneTest extends AbstractSerializableTest
{
	private EasyMockHelper mockHelper = new EasyMockHelper();
	
	private IApplication mockApplication = null; 

	@Before
	public void setUp() throws Exception
	{
		mockApplication = mockHelper.createMock(IApplication.class);
		mockHelper.replayAll();
		super.serializableToTest = new GraphDesktopPane(mockApplication);
	}


}
