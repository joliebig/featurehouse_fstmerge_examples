
package net.sourceforge.squirrel_sql.fw.dialects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SqlGenerationPreferencesTest extends BaseSQuirreLJUnit4TestCase
{

	SqlGenerationPreferences classUnderTest = null;
	
	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new SqlGenerationPreferences();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testSetGetQualifyTableNames()
	{
		classUnderTest.setQualifyTableNames(false);
		assertFalse(classUnderTest.isQualifyTableNames());
		classUnderTest.setQualifyTableNames(true);
		assertTrue(classUnderTest.isQualifyTableNames());
	}

	@Test
	public void testSetQuoteIdentifiers()
	{
		classUnderTest.setQuoteIdentifiers(false);
		assertFalse(classUnderTest.isQuoteIdentifiers());
		classUnderTest.setQuoteIdentifiers(true);
		assertTrue(classUnderTest.isQuoteIdentifiers());		
	}

	@Test
	public void testSetSqlStatementSeparator()
	{
		classUnderTest.setSqlStatementSeparator("\n");
		assertEquals("\n", classUnderTest.getSqlStatementSeparator());				
	}

}
