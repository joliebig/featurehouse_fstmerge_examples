
package net.sourceforge.squirrel_sql.fw.util;

import static org.junit.Assert.assertNotNull;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Test;

public abstract class AbstractResourcesTest extends BaseSQuirreLJUnit4TestCase
{

	protected Resources classUnderTest = null;
	
	protected String getIconArgument = null; 
	
	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test (expected = IllegalArgumentException.class)
	public final void testGetKeyStroke_nullAction()
	{
		mockHelper.replayAll();
		classUnderTest.getKeyStroke(null);
		mockHelper.verifyAll();
	}

	@Test (expected = IllegalArgumentException.class)
	public final void testAddToPopupMenu_nullAction()
	{
		JPopupMenu mockPopupMenu = mockHelper.createMock(JPopupMenu.class);
		
		mockHelper.replayAll();
		classUnderTest.addToPopupMenu(null, mockPopupMenu);
		mockHelper.verifyAll();
	}

	@Test (expected = IllegalArgumentException.class)
	public final void testAddToPopupMenu_nullMenu()
	{
		Action mockAction = mockHelper.createMock(Action.class);
		
		mockHelper.replayAll();
		classUnderTest.addToPopupMenu(mockAction, null);
		mockHelper.verifyAll();
	}

	@Test (expected = IllegalArgumentException.class)
	public final void testAddToMenuAsCheckBoxMenuItemActionJMenu_nullAction()
	{
		JMenu mockMenu = mockHelper.createMock(JMenu.class);
		
		mockHelper.replayAll();
		classUnderTest.addToMenuAsCheckBoxMenuItem(null, mockMenu);
		mockHelper.verifyAll();
	}

	@Test (expected = IllegalArgumentException.class)
	public final void testAddToMenuAsCheckBoxMenuItemActionJMenu_nullMenu()
	{
		JMenu nullMenu = null;
		Action mockAction = mockHelper.createMock(Action.class);
		
		mockHelper.replayAll();
		classUnderTest.addToMenuAsCheckBoxMenuItem(mockAction, nullMenu);
		mockHelper.verifyAll();
	}
	
	@Test
	public void testGetIcon() {
		if (getIconArgument != null) {
			assertNotNull(classUnderTest.getIcon(getIconArgument));
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public final void testGetIconString_nullArg()
	{
		String nullStr = null;
		mockHelper.replayAll();
		classUnderTest.getIcon(nullStr);
		mockHelper.verifyAll();
	}

	@Test (expected = IllegalArgumentException.class)
	public final void testGetIconStringString_nullKeyName()
	{
		String nullKey = null;
		mockHelper.replayAll();
		classUnderTest.getIcon(nullKey, "testPropName");
		mockHelper.verifyAll();
	}

	@Test (expected = IllegalArgumentException.class)
	public final void testGetIconStringString_nullPropName()
	{
		String nullPropName = null;
		mockHelper.replayAll();
		classUnderTest.getIcon("testKeyName", nullPropName);
		mockHelper.verifyAll();
	}
	
	@Test (expected = IllegalArgumentException.class)
	public final void testGetString_nullArg()
	{
		mockHelper.replayAll();
		assertNotNull(classUnderTest.getString(null));
		mockHelper.verifyAll();		
	}

	@Test (expected = IllegalArgumentException.class)
	public void testConfigureMenuItem_nullAction() {
		Action mockAction = mockHelper.createMock(Action.class);
		JMenuItem nullMenuItem = null;

		mockHelper.replayAll();
		classUnderTest.configureMenuItem(mockAction, nullMenuItem);
		mockHelper.verifyAll();
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testConfigureMenuItem_nullMenuItem()
	{
		Action nullAction = null;
		JMenuItem mockMenuItem = mockHelper.createMock(JMenuItem.class);
		
		mockHelper.replayAll();
		classUnderTest.configureMenuItem(nullAction, mockMenuItem);
		mockHelper.verifyAll();
	}

	@Test
	public final void testGetBundle()
	{
		mockHelper.replayAll();
		assertNotNull(classUnderTest.getBundle());
		mockHelper.verifyAll();
	}


}
