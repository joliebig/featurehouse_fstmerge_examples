
package test.net.sourceforge.pmd.ant;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.sourceforge.pmd.ant.Formatter;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

public class FormatterTest {

    @Ignore
    @Test
    public void testType() {

    }

    @Test
    public void testNull() {
        Formatter f = new Formatter();
        assertTrue("Formatter toFile should start off null!", f.isNoOutputSupplied());
        f.setToFile(new File("foo"));
        assertFalse("Formatter toFile should not be null!", f.isNoOutputSupplied());
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(FormatterTest.class);
    }
}
