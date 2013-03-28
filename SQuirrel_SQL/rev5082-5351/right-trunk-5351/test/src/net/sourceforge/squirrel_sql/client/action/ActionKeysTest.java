
package net.sourceforge.squirrel_sql.client.action;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.Test;


public class ActionKeysTest extends BaseSQuirreLJUnit4TestCase {

	private static final int TEST_MNEMONIC = 10;
	private static final String TEST_ACCELERATOR = "testAccelerator";
	private static final String TEST_ACTION_CLASS_NAME = "testActionClassName";
	ActionKeys classUnderTest = new ActionKeys();
	ActionKeys classUnderTest2 = new ActionKeys(TEST_ACTION_CLASS_NAME, TEST_ACCELERATOR, TEST_MNEMONIC);

	@Test
	public void testGetActionClassName() throws Exception
	{
		classUnderTest.setActionClassName("aTestString");
		assertEquals("aTestString", classUnderTest.getActionClassName());
		assertEquals(TEST_ACTION_CLASS_NAME, classUnderTest2.getActionClassName());
	}

	@Test
	public void testGetMnemonic() throws Exception
	{
		classUnderTest.setMnemonic(10);
		assertEquals(10, classUnderTest.getMnemonic());
		assertEquals(TEST_MNEMONIC, classUnderTest2.getMnemonic());
	}

	@Test
	public void testGetAccelerator() throws Exception
	{
		classUnderTest.setAccelerator("aTestString");
		assertEquals("aTestString", classUnderTest.getAccelerator());
		assertEquals(TEST_ACCELERATOR, classUnderTest2.getAccelerator());
	}

	@Test (expected = IllegalArgumentException.class)
	public void testNullActionClassName() {
		classUnderTest.setActionClassName(null);
	}
	
	@Test
	public void testNullAccelerator() {
		classUnderTest.setAccelerator(null);
		assertNotNull(classUnderTest.getAccelerator());
	}
}
