
package net.sourceforge.squirrel_sql.client.session.sqlfilter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SQLFilterClausesTest
{

	SQLFilterClauses classUnderTest = null;
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new SQLFilterClauses();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testPutGet()
	{
		
		String clauseName = "aClause";
		String tableName = "aTable";
		classUnderTest.put(clauseName, tableName, "aClauseInformation");
		classUnderTest.get(clauseName, tableName);
	}

}
