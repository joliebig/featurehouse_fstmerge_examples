
package net.sourceforge.squirrel_sql.client.plugin;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.Test;

public class AbstractPluginResourcesTest extends BaseSQuirreLJUnit4TestCase
{
	
	@Test (expected = IllegalArgumentException.class)
	public final void testPluginResources()
	{
		IPlugin nullPlugin = null;
		new PluginResources("testBundleName", nullPlugin);
	}

}
