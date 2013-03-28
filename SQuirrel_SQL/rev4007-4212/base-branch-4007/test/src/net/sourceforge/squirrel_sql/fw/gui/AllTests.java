package net.sourceforge.squirrel_sql.fw.gui;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
	public static Test suite() {
		TestSuite suite = new TestSuite("fw gui tests");
		suite.addTestSuite(GUIUtilsTest.class);
		return suite;
	}
}
