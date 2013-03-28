package net.sourceforge.squirrel_sql.plugins.dbcopy.event;

 

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;


public class CopyEventTest extends BaseSQuirreLJUnit4TestCase {

	CopyEvent classUnderTest = null;

	EasyMockHelper mockHelper = new EasyMockHelper();
	
	SessionInfoProvider mockSessionInfoProvider =  mockHelper.createMock(SessionInfoProvider.class);
	
	@Before
	public void setUp() {
		classUnderTest = new CopyEvent(mockSessionInfoProvider);
	}
	
	@After
	public void tearDown() {
		classUnderTest = null;
	}
	
	@Test
	public void testGetTableCounts() throws Exception
	{
		classUnderTest.setTableCounts(null);
		assertNull(classUnderTest.getTableCounts());
		int[] tableCounts = new int[] { 0, 1, 1 };
		classUnderTest.setTableCounts(tableCounts);
		assertEquals(tableCounts, classUnderTest.getTableCounts());
	}

	@Test
	public void testGetSessionInfoProvider() throws Exception
	{
		Assert.assertEquals(mockSessionInfoProvider, classUnderTest.getSessionInfoProvider());
	}

}
