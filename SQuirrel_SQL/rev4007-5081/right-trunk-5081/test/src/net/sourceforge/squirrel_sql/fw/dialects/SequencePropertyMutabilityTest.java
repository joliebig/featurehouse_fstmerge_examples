
package net.sourceforge.squirrel_sql.fw.dialects;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SequencePropertyMutabilityTest
{

	SequencePropertyMutability classUnderTest = null;
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new SequencePropertyMutability();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testRestart()
	{
		classUnderTest.setRestart(true);
		assertTrue(classUnderTest.isRestart());
		
		classUnderTest.setRestart(false);
		assertFalse(classUnderTest.isRestart());
	}

	@Test
	public void testStartWith()
	{
		classUnderTest.setStartWith(true);
		assertTrue(classUnderTest.isStartWith());
		
		classUnderTest.setStartWith(false);
		assertFalse(classUnderTest.isStartWith());		
	}

	@Test
	public void testMinValue()
	{
		classUnderTest.setMinValue(true);
		assertTrue(classUnderTest.isMinValue());
		
		classUnderTest.setMinValue(false);
		assertFalse(classUnderTest.isMinValue());				
	}

	@Test
	public void testMaxValue()
	{
		classUnderTest.setMaxValue(true);
		assertTrue(classUnderTest.isMaxValue());
		
		classUnderTest.setMaxValue(false);
		assertFalse(classUnderTest.isMaxValue());		
	}

	@Test
	public void testCycle()
	{
		classUnderTest.setCycle(true);
		assertTrue(classUnderTest.isCycle());
		
		classUnderTest.setCycle(false);
		assertFalse(classUnderTest.isCycle());		
	}

	@Test
	public void testCache()
	{
		classUnderTest.setCache(true);
		assertTrue(classUnderTest.isCache());
		
		classUnderTest.setCache(false);
		assertFalse(classUnderTest.isCache());		
	}

}
