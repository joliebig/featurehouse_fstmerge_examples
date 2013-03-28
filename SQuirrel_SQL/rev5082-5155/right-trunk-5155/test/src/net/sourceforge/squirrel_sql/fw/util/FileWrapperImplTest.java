
package net.sourceforge.squirrel_sql.fw.util;


import static org.junit.Assert.assertEquals;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.base.testing.EqualsTester;

public class FileWrapperImplTest extends BaseSQuirreLJUnit4TestCase
{

	FileWrapperImpl classUnderTest = null;
	
	private final static String tmpDir = System.getProperty("java.io.tmpdir");
	private final static String userHome = System.getProperty("user.home");
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new FileWrapperImpl(tmpDir);
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}
	
	@Test
	public void testGetAbsolutePath() {
		assertEquals(tmpDir, classUnderTest.getAbsolutePath());
	}
	
	@Test
	public void testEqualsAndHashCode() {
		FileWrapperImpl a = new FileWrapperImpl(tmpDir);
		FileWrapperImpl b = new FileWrapperImpl(tmpDir);
		FileWrapperImpl c = new FileWrapperImpl(userHome);
		FileWrapperImpl d = new FileWrapperImpl(tmpDir) {
			private static final long serialVersionUID = 1L;			
		};
		new EqualsTester(a, b, c, d);		
	}

}
