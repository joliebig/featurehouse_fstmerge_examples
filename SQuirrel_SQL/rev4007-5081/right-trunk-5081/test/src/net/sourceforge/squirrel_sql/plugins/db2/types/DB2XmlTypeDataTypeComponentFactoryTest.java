
package net.sourceforge.squirrel_sql.plugins.db2.types;


import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.AbstractDataTypeComponentFactoryTest;

import org.junit.Before;

public class DB2XmlTypeDataTypeComponentFactoryTest extends AbstractDataTypeComponentFactoryTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new DB2XmlTypeDataTypeComponentFactory();
	}

}
