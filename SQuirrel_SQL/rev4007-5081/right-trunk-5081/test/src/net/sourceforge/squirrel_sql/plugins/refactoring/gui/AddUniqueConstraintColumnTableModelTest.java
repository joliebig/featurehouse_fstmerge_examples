
package net.sourceforge.squirrel_sql.plugins.refactoring.gui;

import net.sourceforge.squirrel_sql.client.update.gui.AbstractTableModelTest;

import org.junit.Before;

public class AddUniqueConstraintColumnTableModelTest extends AbstractTableModelTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new AddUniqueConstraintColumnTableModel();
	}

}
