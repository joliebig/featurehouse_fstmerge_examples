package net.sourceforge.squirrel_sql.client.session;


import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses( { ExtendedColumnInfoTest.class,
                 MessagePanelTest.class,
                 SQLExecuterTaskTest.class })
public class AllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("Client Session tests");
        suite.addTest(new JUnit4TestAdapter(ExtendedColumnInfoTest.class));		
        suite.addTest(new JUnit4TestAdapter(MessagePanelTest.class));
        suite.addTest(new JUnit4TestAdapter(SessionTest.class));
        suite.addTestSuite(SQLExecuterTaskTest.class);
		return suite;
	}
}
