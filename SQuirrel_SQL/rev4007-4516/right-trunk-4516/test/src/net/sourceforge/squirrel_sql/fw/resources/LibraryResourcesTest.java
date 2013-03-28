
package net.sourceforge.squirrel_sql.fw.resources;

import static org.junit.Assert.*;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LibraryResourcesTest extends BaseSQuirreLJUnit4TestCase
{

	private LibraryResources classUnderTest = null; 
	
	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testLibraryResources()
	{
		classUnderTest = new LibraryResources();
		assertNotNull(classUnderTest.getIcon(LibraryResources.IImageNames.TABLE_DESCENDING));
		assertNotNull(classUnderTest.getIcon(LibraryResources.IImageNames.TABLE_ASCENDING));
		classUnderTest.getIcon(new Object());
	}

}
