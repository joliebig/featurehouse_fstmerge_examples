package net.sourceforge.squirrel_sql.plugins.dbdiff;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( { ColumnDifferenceTest.class })
public class AllTests {
    
	public static Test suite() {
        TestSuite result = new TestSuite("squirrel_sql dbdiff tests");
        result.addTest(new JUnit4TestAdapter(ColumnDifferenceTest.class));
        return result;
	}
    
}
