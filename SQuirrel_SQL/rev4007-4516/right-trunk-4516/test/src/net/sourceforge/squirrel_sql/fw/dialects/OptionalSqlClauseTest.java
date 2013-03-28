
package net.sourceforge.squirrel_sql.fw.dialects;

import static org.junit.Assert.*;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.antlr.stringtemplate.StringTemplate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class OptionalSqlClauseTest extends BaseSQuirreLJUnit4TestCase
{

	OptionalSqlClause classUnderTest = null;
	
	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testOptionalSqlClauseStringString()
	{
		String staticPart = "static";
		String variablePart = "variable";
		classUnderTest = new OptionalSqlClause(staticPart, variablePart);
		String result = classUnderTest.toString();
		assertEquals(staticPart + " " + variablePart, result);
	}

	@Test
	public void testOptionalSqlClauseStringTemplateStringString()
	{
		String staticPart = "A test template ";
		StringTemplate st = new StringTemplate(staticPart + "$subst$");
		
		String key = "subst";
		String value = "works!";
		
		classUnderTest = new OptionalSqlClause(st, key, value);
		
		String result = classUnderTest.toString();
		assertEquals(staticPart+value, result);
	}


}
