
package net.sourceforge.squirrel_sql.fw.resources;

import net.sourceforge.squirrel_sql.fw.util.AbstractResourcesTest;

import org.junit.Before;

public class LibraryResourcesTest extends AbstractResourcesTest
{
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new LibraryResources();
		super.getIconArgument = LibraryResources.IImageNames.TABLE_DESCENDING;
	}

}
