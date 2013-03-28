
package net.sourceforge.squirrel_sql.plugins.derby.types;


import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.AbstractDataTypeComponentFactoryTest;

import org.junit.Before;

public class DerbyClobDataTypeComponentFactoryTest extends AbstractDataTypeComponentFactoryTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new DerbyClobDataTypeComponentFactory();
	}

}
