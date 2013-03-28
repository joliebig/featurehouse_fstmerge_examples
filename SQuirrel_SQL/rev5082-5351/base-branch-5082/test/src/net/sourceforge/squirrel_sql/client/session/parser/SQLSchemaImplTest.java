
package net.sourceforge.squirrel_sql.client.session.parser;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

import java.util.List;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.SQLSchema;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.TaskThreadPool;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SQLSchemaImplTest extends BaseSQuirreLJUnit4TestCase
{

	SQLSchemaImpl classUnderTest = null;
	
	ISession mockSession = mockHelper.createMock(ISession.class);
	ISQLConnection mockConnection = mockHelper.createMock(ISQLConnection.class);
	SQLDatabaseMetaData mockMetaData = mockHelper.createMock(SQLDatabaseMetaData.class);
	IApplication mockApplication = mockHelper.createMock(IApplication.class);
	TaskThreadPool mockThreadPool = mockHelper.createMock(TaskThreadPool.class);
	SchemaInfo mockSchemaInfo = mockHelper.createMock(SchemaInfo.class);
	
	
	
	String catalog  = "aCatalog";
	String schema = "aSchema";
	String name = "aTableName";

	String[] tables = new String[] { name };
	
	@Before
	public void setUp() throws Exception
	{
		expect(mockSession.getApplication()).andStubReturn(mockApplication);
		expect(mockSession.getSQLConnection()).andStubReturn(mockConnection);
		expect(mockConnection.getSQLMetaData()).andStubReturn(mockMetaData);
		expect(mockApplication.getThreadPool()).andStubReturn(mockThreadPool);
		expect(mockSession.getSchemaInfo()).andStubReturn(mockSchemaInfo);
		mockThreadPool.addTask(EasyMock.isA(Runnable.class));
		EasyMock.expectLastCall().anyTimes();
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testGetTable()
	{

		expect(mockSchemaInfo.isTable(name)).andReturn(true);
		
		mockHelper.replayAll();
		classUnderTest = new SQLSchemaImpl(mockSession);
		classUnderTest.getTable(catalog, schema, name);
		mockHelper.verifyAll();
	}

	@Test
	public void testGetTables()
	{
		
		expect(mockSchemaInfo.getTables()).andReturn(tables);
		mockHelper.replayAll();
		classUnderTest = new SQLSchemaImpl(mockSession);
		List<SQLSchema.Table> result = classUnderTest.getTables(catalog, schema, name);
		assertEquals(tables.length, result.size());
		mockHelper.verifyAll();
		
	}

	@Test
	public void testGetTables_null_Catalog_and_Schema()
	{
		
		expect(mockSchemaInfo.getTables()).andReturn(tables);
		mockHelper.replayAll();
		classUnderTest = new SQLSchemaImpl(mockSession);
		List<SQLSchema.Table> result = classUnderTest.getTables(null, null, name);
		assertEquals(tables.length, result.size());
		mockHelper.verifyAll();
		
	}
	
	@Test
	public void testGetTableForAlias()
	{
		mockHelper.replayAll();
		classUnderTest = new SQLSchemaImpl(mockSession);
		assertNull(classUnderTest.getTableForAlias("aTableAlias"));
		mockHelper.verifyAll();
	}

}
