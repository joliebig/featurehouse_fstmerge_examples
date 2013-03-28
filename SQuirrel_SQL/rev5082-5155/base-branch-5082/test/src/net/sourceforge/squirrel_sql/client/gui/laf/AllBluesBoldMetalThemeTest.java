
package net.sourceforge.squirrel_sql.client.gui.laf;

import static org.junit.Assert.*;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AllBluesBoldMetalThemeTest extends BaseSQuirreLJUnit4TestCase
{
	private AllBluesBoldMetalTheme classUnderTest = null;
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new AllBluesBoldMetalTheme();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public final void testGetName()
	{
		assertNotNull(classUnderTest.getName());
	}

	@Test
	public final void testGetMenuItemSelectedBackground()
	{
		assertNotNull(classUnderTest.getMenuItemSelectedBackground());
	}

	@Test
	public final void testGetMenuItemSelectedForeground()
	{
		assertNotNull(classUnderTest.getMenuItemSelectedForeground());
	}

	@Test
	public final void testGetMenuSelectedBackground()
	{
		assertNotNull(classUnderTest.getMenuSelectedBackground());	
	}

	@Test
	public final void testGetPrimary1()
	{
		assertNotNull(classUnderTest.getPrimary1());	
	}

	@Test
	public final void testGetPrimary2()
	{
		assertNotNull(classUnderTest.getPrimary2());	
	}

	@Test
	public final void testGetPrimary3()
	{
		assertNotNull(classUnderTest.getPrimary3());
	}

	@Test
	public final void testGetSecondary1()
	{
		assertNotNull(classUnderTest.getSecondary1());
	}

	@Test
	public final void testGetSecondary2()
	{
		assertNotNull(classUnderTest.getSecondary2());
	}

	@Test
	public final void testGetSecondary3()
	{
		assertNotNull(classUnderTest.getSecondary3());
	}

}
