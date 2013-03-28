package net.sourceforge.squirrel_sql;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
	
	public static Test suite() {
		TestSuite result = new TestSuite("squirrel_sql tests");
        result.addTest(net.sourceforge.squirrel_sql.client.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.client.action.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.client.db.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.client.gui.db.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.client.mainframe.action.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.client.session.mainpanel.AllTests.suite());
		result.addTest(net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.client.session.schemainfo.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.client.session.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.fw.codereformat.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.fw.dialects.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.fw.gui.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.fw.id.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.fw.sql.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.fw.util.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.plugins.dbcopy.AllTests.suite());
		result.addTest(net.sourceforge.squirrel_sql.plugins.dbcopy.util.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.plugins.dbdiff.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.plugins.derby.tokenizer.AllTests.suite());
        result.addTest(net.sourceforge.squirrel_sql.plugins.oracle.tokenizer.AllTests.suite());        
		return result;
	}
}
