
package net.sourceforge.squirrel_sql.client.gui;

import java.awt.Component;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ScrollableDesktopPane;

import org.junit.Before;
import org.junit.Test;

public class ScrollableDesktopPaneTest extends BaseSQuirreLJUnit4TestCase
{

	ScrollableDesktopPane classUnderTest = null;
	
	
	IApplication mockApplication = mockHelper.createMock("mockApplication", IApplication.class);
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new ScrollableDesktopPane(mockApplication);
	}

	@Test
	public void testRemoveComponent_nullarg()
	{
		mockHelper.replayAll();
		classUnderTest.remove((Component)null);
		mockHelper.verifyAll();
	}

	@Test
	public void testAddImplComponentObjectInt_nullargs()
	{
		mockHelper.replayAll();
		classUnderTest.addWidget((DialogWidget)null);
		mockHelper.verifyAll();
	}

}
