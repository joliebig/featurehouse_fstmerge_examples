
package net.sourceforge.squirrel_sql.fw.sql;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TableInfoDataSetTest extends BaseSQuirreLJUnit4TestCase
{
	TableInfoDataSet classUnderTest = null;

	ITableInfo mockTableInfo = mockHelper.createMock(ITableInfo.class);

	@Before
	public void setUp() throws Exception
	{
		setupDboExpectations(mockTableInfo);
		expect(mockTableInfo.getType()).andStubReturn("testType");
		expect(mockTableInfo.getRemarks()).andStubReturn(null);
		classUnderTest = new TableInfoDataSet();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public final void testGetColumnCount()
	{
		assertEquals(2, classUnderTest.getColumnCount());
	}

	@Test
	public final void testGetDataSetDefinition()
	{
		mockHelper.replayAll();
		classUnderTest.setTableInfo(mockTableInfo);
		assertNotNull(classUnderTest.getDataSetDefinition());
		mockHelper.verifyAll();
	}

	@Test
	public final void testSetTableInfo()
	{
		mockHelper.replayAll();
		classUnderTest.setTableInfo(mockTableInfo);
		mockHelper.verifyAll();
	}

	@Test
	public final void testNext()
	{
		mockHelper.replayAll();
		classUnderTest.setTableInfo(mockTableInfo);
		for (int i = 0; i < 6; i++)
		{
			assertTrue(classUnderTest.next(null));
		}
		assertFalse(classUnderTest.next(null));
		mockHelper.verifyAll();
	}

	@Test
	public final void testGet()
	{
		mockHelper.replayAll();
		classUnderTest.setTableInfo(mockTableInfo);
		mockHelper.verifyAll();
		classUnderTest.next(null);
		assertNotNull(classUnderTest.get(0));
	}

}
