
package net.sourceforge.squirrel_sql.client.gui.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.swing.Action;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Test;

public class ToolbarItemTest extends BaseSQuirreLJUnit4TestCase
{
	ToolbarItem classUnderTest = null;

	private Action mockAction = mockHelper.createMock(Action.class);

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testActionItem()
	{
		classUnderTest = new ToolbarItem(mockAction);
		assertEquals(mockAction, classUnderTest.getAction());
		assertFalse(classUnderTest.isSeparator());
	}

	@Test
	public void testSeparatorItem()
	{
		classUnderTest = new ToolbarItem();
		assertTrue(classUnderTest.isSeparator());
	}

}
