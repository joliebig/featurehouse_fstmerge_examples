package test.net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.SourceTypeDiscoverer;

import org.junit.Test;

import java.io.File;

import junit.framework.JUnit4TestAdapter;

public class SourceTypeDiscovererTest {

    
    @Test
    public void testJspFile() {
        SourceTypeDiscoverer discoverer = new SourceTypeDiscoverer();
        File jspFile = new File("/path/to/MyPage.jsp");

        SourceType type = discoverer.getSourceTypeOfFile(jspFile);

        assertEquals("SourceType must be JSP!", SourceType.JSP, type);
    }

    
    @Test
    public void testJavaFileUsingDefaults() {
        SourceTypeDiscoverer discoverer = new SourceTypeDiscoverer();
        File javaFile = new File("/path/to/MyClass.java");

        SourceType type = discoverer.getSourceTypeOfFile(javaFile);

        assertEquals("SourceType must be Java 1.4!", SourceType.JAVA_14, type);
    }

    
    @Test
    public void testJavaFileUsing15() {
        SourceTypeDiscoverer discoverer = new SourceTypeDiscoverer();
        discoverer.setSourceTypeOfJavaFiles(SourceType.JAVA_15);
        File javaFile = new File("/path/to/MyClass.java");

        SourceType type = discoverer.getSourceTypeOfFile(javaFile);

        assertEquals("SourceType must be Java 1.5!", SourceType.JAVA_15, type);
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(SourceTypeDiscovererTest.class);
    }
}
