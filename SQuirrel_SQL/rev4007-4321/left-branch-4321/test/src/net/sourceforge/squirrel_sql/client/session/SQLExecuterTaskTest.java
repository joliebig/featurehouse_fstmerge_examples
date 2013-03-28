package net.sourceforge.squirrel_sql.client.session;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.sql.Connection;

import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;

public class SQLExecuterTaskTest extends BaseSQuirreLTestCase {

    private ISession session = null;
    private IQueryTokenizer tokenizer = null;
    private ISQLDatabaseMetaData md = null;
    private ISQLDriver driver = null;
    private Connection con = null;
    private SQLConnection sqlCon = null;
    
    
    protected void setUp() throws Exception {
        super.setUp();
        session = createMock(ISession.class);
        tokenizer = createMock(IQueryTokenizer.class);
        md = createMock(ISQLDatabaseMetaData.class);
        con = createMock(Connection.class);
        driver = createMock(ISQLDriver.class);
        sqlCon = new SQLConnection(con, null, driver);
        session.setQueryTokenizer(tokenizer);
        expect(session.getMetaData()).andReturn(md);
        expect(session.getSQLConnection()).andReturn(sqlCon);
        expect(session.getQueryTokenizer()).andReturn(tokenizer);
        replay(session);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testNullSQL() {
        SQLExecuterTask task = new SQLExecuterTask(session, null, null, null);
        task.run();
    }
}
