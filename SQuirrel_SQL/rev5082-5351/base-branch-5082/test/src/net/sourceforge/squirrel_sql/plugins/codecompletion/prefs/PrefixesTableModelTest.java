
package net.sourceforge.squirrel_sql.plugins.codecompletion.prefs;


import net.sourceforge.squirrel_sql.client.update.gui.AbstractTableModelTest;

import org.junit.Before;
import org.junit.Test;

public class PrefixesTableModelTest extends AbstractTableModelTest
{

	@Before
	public void setUp() throws Exception
	{
		PrefixedConfig mockPrefix = mockHelper.createMock(PrefixedConfig.class);
		PrefixedConfig[] prefixes = new PrefixedConfig[] { mockPrefix };
		mockHelper.replayAll();
		classUnderTest = new PrefixesTableModel(prefixes);
	}

	@Test
	public void testGetValueAt_InvalidColumn() {
		
	}

}
