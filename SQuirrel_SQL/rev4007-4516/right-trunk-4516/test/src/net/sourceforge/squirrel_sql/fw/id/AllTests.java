package net.sourceforge.squirrel_sql.fw.id;


import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("fw id tests");
		suite.addTest(new JUnit4TestAdapter(UidIdentifierTest.class));
		suite.addTest(new JUnit4TestAdapter(IntegerIdentifierTest.class));
		return suite;
	}
}
