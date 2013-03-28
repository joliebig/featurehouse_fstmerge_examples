
package net.sourceforge.squirrel_sql.plugins.db2.exp;


import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.AbstractINodeExpanderTest;

import org.junit.Before;
import org.junit.Test;

public class SequenceParentExpanderTest extends AbstractINodeExpanderTest
{

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new SequenceParentExpander(false);
	}

	@Test
	public void testOS400() throws SQLException {
		classUnderTest = new SequenceParentExpander(true);
		super.testCreateChildren();
	}
	
}
