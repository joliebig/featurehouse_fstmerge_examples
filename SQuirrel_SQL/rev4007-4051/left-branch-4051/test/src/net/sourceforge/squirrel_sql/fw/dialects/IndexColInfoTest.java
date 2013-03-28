
package net.sourceforge.squirrel_sql.fw.dialects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.base.testing.EqualsTester;

public class IndexColInfoTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testEqualsObject() {
        IndexColInfo col1 = new IndexColInfo("col1", 1);
        IndexColInfo col2 = new IndexColInfo("col1", 1);
        IndexColInfo col3 = new IndexColInfo("col2", 22222222);
        IndexColInfo col4 = new IndexColInfo("col1", 1) {
        };
        new EqualsTester(col1, col2, col3, col4);
    }
}
