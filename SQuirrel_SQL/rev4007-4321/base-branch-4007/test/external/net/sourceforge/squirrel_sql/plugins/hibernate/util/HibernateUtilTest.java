
package net.sourceforge.squirrel_sql.plugins.hibernate.util;

import java.sql.SQLException;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.MockSession;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HibernateUtilTest extends BaseSQuirreLJUnit4TestCase {

    ISession session = null;
    
    @Before
    public void setUp() throws Exception {
        session = new MockSession("oracle.jdbc.driver.OracleDriver",
                                "jdbc:oracle:thin:@localhost:1521:csuite",
                                "test", "password");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testGetSessionFactory() throws SQLException {
        HibernateUtil.getSessionFactory(session,
                new String[] { "C:/home/projects/squirrel-sql/sql12/test/src/net/sourceforge/squirrel_sql/plugins/hibernate/util" });
    }

}
