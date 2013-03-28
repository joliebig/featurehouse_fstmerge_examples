
package net.sourceforge.squirrel_sql.plugins.oracle;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import javax.swing.Action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.plugin.AbstractSessionPluginTest;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DTProperties;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeTimestamp;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.plugins.oracle.SGAtrace.NewSGATraceWorksheetAction;
import net.sourceforge.squirrel_sql.plugins.oracle.dboutput.NewDBOutputWorksheetAction;
import net.sourceforge.squirrel_sql.plugins.oracle.exception.OracleExceptionFormatter;
import net.sourceforge.squirrel_sql.plugins.oracle.invalidobjects.NewInvalidObjectsWorksheetAction;
import net.sourceforge.squirrel_sql.plugins.oracle.sessioninfo.NewSessionInfoWorksheetAction;
import net.sourceforge.squirrel_sql.test.TestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class OraclePluginTest extends AbstractSessionPluginTest
{

	OraclePlugin pluginUnderTest = null;

	
	ISession session = null;

	IApplication app = null;

	ISQLDatabaseMetaData md = null;

	
	String timestampClassName = DataTypeTimestamp.class.getName();

	@Before
	public void setUp() throws Exception
	{
		pluginUnderTest = new OraclePlugin();
		md = TestUtil.getEasyMockSQLMetaData("oracle", "jdbc:oracle:thin:@host:1521:sid", false, false);
		String[] functions = new String[] { OracleExceptionFormatter.OFFSET_FUNCTION_NAME };
		expect(md.getStringFunctions()).andReturn(functions);
		replay(md);
		ActionCollection col = getOraclePluginActionCollection();
		app = TestUtil.getEasyMockApplication(col);
		pluginUnderTest.load(app);
		pluginUnderTest.initialize();
		session = TestUtil.getEasyMockSession(md, true);
		classUnderTest = new OraclePlugin();
	}

	@After
	public void tearDown() throws Exception
	{
		session = null;
		app = null;
		md = null;
		classUnderTest = null;
	}

	
	@Test
	public void testSessionStarted()
	{
		
		
		DTProperties.put(timestampClassName, "", null); 
		pluginUnderTest.sessionStarted(session);
	}

	private ActionCollection getOraclePluginActionCollection()
	{
		ActionCollection result = TestUtil.getEasyMockActionCollection(false);
		Action someAction = createMock(Action.class);
		replay(someAction);
		expect(result.get(NewDBOutputWorksheetAction.class)).andReturn(someAction).anyTimes();
		expect(result.get(NewInvalidObjectsWorksheetAction.class)).andReturn(someAction).anyTimes();
		expect(result.get(NewSessionInfoWorksheetAction.class)).andReturn(someAction).anyTimes();
		expect(result.get(NewSGATraceWorksheetAction.class)).andReturn(someAction).anyTimes();
		result.add(isA(SquirrelAction.class));
		expectLastCall().anyTimes();
		replay(result);
		return result;
	}

	@Override
	protected String getDatabaseProductName()
	{
		return "oracle";
	}

	@Override
	protected String getDatabaseProductVersion()
	{
		return null;
	}

}
