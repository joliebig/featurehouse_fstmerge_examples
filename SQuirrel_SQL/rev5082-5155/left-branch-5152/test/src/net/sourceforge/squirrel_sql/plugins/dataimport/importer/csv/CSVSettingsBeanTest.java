
package net.sourceforge.squirrel_sql.plugins.dataimport.importer.csv;


import net.sourceforge.squirrel_sql.AbstractSerializableTest;

import org.junit.Before;

public class CSVSettingsBeanTest extends AbstractSerializableTest
{

	@Before
	public void setUp() throws Exception
	{
		super.serializableToTest = new CSVSettingsBean();
	}


}
