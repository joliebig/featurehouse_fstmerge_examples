
package net.sourceforge.squirrel_sql.client.update.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PathUtilsImplTest
{

	private PathUtils classUnderTest = null;
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new PathUtilsImpl();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testBuildPath()
	{
		String result = classUnderTest.buildPath(true, new String[] { "a", "path", "to", "a", "file" });
		assertEquals("/a/path/to/a/file", result);
	}

	@Test
	public void testGetFileFromPath()
	{
		String result = classUnderTest.getFileFromPath("/a/path/to/a/file");
		assertEquals("file", result);
		
		result = classUnderTest.getFileFromPath("file");
		assertEquals("file", result);
	}

}
