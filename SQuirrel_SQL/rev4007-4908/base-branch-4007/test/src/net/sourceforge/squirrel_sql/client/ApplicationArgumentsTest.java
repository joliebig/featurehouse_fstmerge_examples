
package net.sourceforge.squirrel_sql.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ApplicationArgumentsTest {

    ApplicationArguments applicationArgumentsUnderTest = null;
    
    String[] _rawArgs = new String[] { "1", "2", "3", "4" };
    
    @Before
    public void setUp() throws Exception {
        ApplicationArguments.reset();
        ApplicationArguments.initialize(_rawArgs);
        applicationArgumentsUnderTest = ApplicationArguments.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        ApplicationArguments.reset();
    }

    @Test
    public final void testGetRawArguments() {
        String[] rawArgs = applicationArgumentsUnderTest.getRawArguments();
        assertNotNull(rawArgs);
        assertEquals(4, rawArgs.length);
        assertEquals("1", rawArgs[0]);
        assertEquals("2", rawArgs[1]);
        assertEquals("3", rawArgs[2]);
        assertEquals("4", rawArgs[3]);
    }
    
}
