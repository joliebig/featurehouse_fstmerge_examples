
package net.sourceforge.squirrel_sql.plugins.postgres.exp;


import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.AbstractSequenceParentExtractorTest;

import org.junit.Before;

public class PostgresSequenceParentExtractorImplTest extends AbstractSequenceParentExtractorTest
{
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new PostgresSequenceParentExtractorImpl();
	}
}
