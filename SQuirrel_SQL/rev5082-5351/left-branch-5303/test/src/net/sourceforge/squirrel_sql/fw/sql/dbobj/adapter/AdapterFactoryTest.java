
package net.sourceforge.squirrel_sql.fw.sql.dbobj.adapter;

import static org.junit.Assert.*;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.sql.dbobj.BestRowIdentifier;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

public class AdapterFactoryTest extends BaseSQuirreLJUnit4TestCase
{
	
	EasyMockHelper mockHelper = new EasyMockHelper();
	
	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testGetInstance()
	{
		assertNotNull(AdapterFactory.getInstance());
	}

	@Test
	public void testCreateBestRowIdentifierAdapter()
	{
		BestRowIdentifier[] beans = new BestRowIdentifier[1];
		beans[0] = mockHelper.createMock(BestRowIdentifier.class);
		assertNotNull(AdapterFactory.getInstance().createBestRowIdentifierAdapter(beans));
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testCreateBestRowIdentifierAdapter_NullArg() {
		AdapterFactory.getInstance().createBestRowIdentifierAdapter(null);
	}
	
}
