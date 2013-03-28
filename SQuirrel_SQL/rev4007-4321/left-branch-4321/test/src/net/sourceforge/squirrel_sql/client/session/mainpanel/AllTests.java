package net.sourceforge.squirrel_sql.client.session.mainpanel;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite("client session mainpanel tests");
		suite.addTestSuite(BaseMainPanelTabTest.class);
        suite.addTestSuite(SQLHistoryItemTest.class);
		return suite;
	}
}
