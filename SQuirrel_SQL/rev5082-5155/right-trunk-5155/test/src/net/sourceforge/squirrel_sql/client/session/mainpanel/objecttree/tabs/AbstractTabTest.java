
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;

import java.sql.Connection;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.TaskThreadPool;

public class AbstractTabTest extends BaseSQuirreLJUnit4TestCase
{
	public static final String STMT_SEP = ";";

	public static final String HINT = "aHint";

	public static final int TRANSACTION_ISOLATION = Connection.TRANSACTION_READ_COMMITTED;

	protected ISession mockSession = mockHelper.createMock(ISession.class);

	protected ISQLConnection mockSQLConnection = mockHelper.createMock(ISQLConnection.class);

	protected IApplication mockApplication = mockHelper.createMock(IApplication.class);

	protected IIdentifier mockSessionId = mockHelper.createMock(IIdentifier.class);

	protected SessionManager mockSessionManager = mockHelper.createMock(SessionManager.class);

	protected TaskThreadPool mockThreadPool = mockHelper.createMock(TaskThreadPool.class);

	protected SQLDatabaseMetaData mockSQLMetaData = mockHelper.createMock(SQLDatabaseMetaData.class);

	protected Connection mockConnection = mockHelper.createMock(Connection.class);

	protected String databaseProductName = null;

	public static final String TEST_COLUMN_NAME = "aColumnName";

	protected static final String DATABASE_PRODUCT_VERSION = "1.0";

	public static final String[] mockCatalogs = new String[] { TEST_CATALOG_NAME, "testCatalogName2" };

	public static final String METADATA_OUTPUT_CLASSNAME = "aMetaDataOutputClassName";

	public AbstractTabTest()
	{
		super();
	}

}