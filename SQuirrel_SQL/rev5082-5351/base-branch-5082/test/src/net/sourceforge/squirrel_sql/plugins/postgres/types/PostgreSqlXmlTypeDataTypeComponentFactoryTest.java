
package net.sourceforge.squirrel_sql.plugins.postgres.types;


import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.AbstractDataTypeComponentFactoryTest;

import org.junit.Before;

public class PostgreSqlXmlTypeDataTypeComponentFactoryTest extends AbstractDataTypeComponentFactoryTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new PostgreSqlXmlTypeDataTypeComponentFactory();
	}

}
