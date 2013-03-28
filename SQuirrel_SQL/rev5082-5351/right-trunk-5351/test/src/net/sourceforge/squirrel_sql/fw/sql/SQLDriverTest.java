package net.sourceforge.squirrel_sql.fw.sql;

 

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;

import org.junit.Test;

import utils.EasyMockHelper;

import com.gargoylesoftware.base.testing.EqualsTester;


public class SQLDriverTest extends BaseSQuirreLJUnit4TestCase {

	SQLDriver classUnderTest = new SQLDriver();

	EasyMockHelper mockHelper = new EasyMockHelper();
	
	@Test
	public void testGetName() throws Exception
	{
		classUnderTest.setName("aTestString");
		assertEquals("aTestString", classUnderTest.getName());
	}

	@Test
	public void testGetJarFileNames() throws Exception
	{
		classUnderTest.setJarFileNames(null);
		assertEquals(0, classUnderTest.getJarFileNames().length);
	}

	@Test
	public void testGetDriverClassName() throws Exception
	{
		classUnderTest.setDriverClassName("aTestString");
		assertEquals("aTestString", classUnderTest.getDriverClassName());
	}

	@Test (expected = ValidationException.class)
	public void testSetDriverClassNameNull() throws Exception {
		classUnderTest.setDriverClassName(null);
	}
	
	
	@Test
	public void testGetUrl() throws Exception
	{
		classUnderTest.setUrl("aTestString");
		assertEquals("aTestString", classUnderTest.getUrl());
	}

	@Test (expected = ValidationException.class)
	public void testSetUrlNull() throws Exception {
		classUnderTest.setUrl(null);
	}
	
	@Test
	public void testIsJDBCDriverClassLoaded() throws Exception
	{
		classUnderTest.setJDBCDriverClassLoaded(true);
		assertEquals(true, classUnderTest.isJDBCDriverClassLoaded());
	}

	@Test
	public void testGetWebSiteUrl() throws Exception
	{
		classUnderTest.setWebSiteUrl("aTestString");
		assertEquals("aTestString", classUnderTest.getWebSiteUrl());
	}

	@Test
	public void testGetIdentifier() throws Exception
	{
		classUnderTest.setIdentifier(null);
		assertNull(classUnderTest.getIdentifier());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetJarFileName() throws Exception
	{
		classUnderTest.setJarFileName("aTestString");
		assertEquals("aTestString", classUnderTest.getJarFileName());
	}

	@Test
	public void testGetJarFileNameWrappers() throws Exception
	{
		classUnderTest.setJarFileNameWrappers(null);
		assertTrue(classUnderTest.getJarFileNameWrappers().length == 0);
	}

	@Test
	public void testGetJarFileNameWrapper() throws Exception
	{
		classUnderTest.setJarFileNames(new String[] { "test.jar" });
		assertEquals("test.jar", classUnderTest.getJarFileNameWrapper(0).getString());
	}

	@Test
	public void testEqualsAndHashcode() throws Exception {
		IIdentifier id1 = mockHelper.createMock(IIdentifier.class);
		IIdentifier id2 = mockHelper.createMock(IIdentifier.class);
		
		mockHelper.replayAll();
		
		SQLDriver a = new SQLDriver(id1);
		SQLDriver b = new SQLDriver(id1);
		SQLDriver c = new SQLDriver(id2);
		SQLDriver d = new SQLDriver(id1) {
			private static final long serialVersionUID = 1L;
		};
		new EqualsTester(a, b, c, d);
		
		mockHelper.verifyAll();
	}
}
