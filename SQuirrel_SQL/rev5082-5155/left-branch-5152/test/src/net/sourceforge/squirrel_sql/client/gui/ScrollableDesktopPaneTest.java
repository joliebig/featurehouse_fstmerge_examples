
package net.sourceforge.squirrel_sql.client.gui;

import java.awt.Component;

import org.junit.Before;
import org.junit.Test;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ScrollableDesktopPane;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.MockApplication;

public class ScrollableDesktopPaneTest extends BaseSQuirreLJUnit4TestCase
{

	ScrollableDesktopPane classUnderTest = null;
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new ScrollableDesktopPane(new MockApplication());
	}

	@Test
	public void testRemoveComponent_nullarg()
	{
		classUnderTest.remove((Component)null);
	}

	@Test
	public void testAddImplComponentObjectInt_nullargs()
	{
		classUnderTest.addWidget((DialogWidget)null);
	}

}
