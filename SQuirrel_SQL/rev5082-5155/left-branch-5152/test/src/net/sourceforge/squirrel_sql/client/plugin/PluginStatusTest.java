
package net.sourceforge.squirrel_sql.client.plugin;


import net.sourceforge.squirrel_sql.AbstractSerializableTest;

import org.junit.Before;

public class PluginStatusTest extends AbstractSerializableTest 
{

	@Before
	public void setUp() throws Exception
	{
		super.serializableToTest = new PluginStatus("testPlugin");
	}


}
