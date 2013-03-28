
package net.sourceforge.squirrel_sql.plugins.mssql;

import static org.junit.Assert.*;

import net.sourceforge.squirrel_sql.plugins.mssql.sql.constraint.MssqlConstraint;
import net.sourceforge.squirrel_sql.plugins.mssql.sql.constraint.TableConstraints;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class TableConstraintsTest {

    TableConstraints constraintsUnderTest = null;
    
    @Before
    public void setUp() throws Exception {
        constraintsUnderTest = new TableConstraints();
    }

    @After
    public void tearDown() throws Exception {
        constraintsUnderTest = null;
    }

    @Test
    public final void testGetConstraints() {
        MssqlConstraint[] constraints = constraintsUnderTest.getConstraints();
        assertEquals(0, constraints.length);
    }

    @Test
    public final void testAddConstraint() {
        constraintsUnderTest.addConstraint(new MssqlConstraint());
        MssqlConstraint[] constraints = constraintsUnderTest.getConstraints();
        assertEquals(1, constraints.length);
    }

    @Test
    @Ignore
    public final void testGetDefaultsForColumn() {
        fail("Not yet implemented"); 
    }

    @Test
    @Ignore
    public final void testGetCheckConstraints() {
        fail("Not yet implemented"); 
    }

    @Test
    @Ignore
    public final void testGetForeignKeyConstraints() {
        fail("Not yet implemented"); 
    }

    @Test
    @Ignore
    public final void testGetPrimaryKeyConstraints() {
        fail("Not yet implemented"); 
    }

}
