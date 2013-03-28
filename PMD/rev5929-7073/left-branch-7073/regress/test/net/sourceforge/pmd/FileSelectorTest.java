package test.net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.SourceFileSelector;

import org.junit.Test;

import java.io.File;

public class FileSelectorTest {

    
    @Test
    public void testSelectJavaFile() {
        SourceFileSelector fileSelector = new SourceFileSelector();

        File javaFile = new File("/path/to/MyClass.java");

        boolean selected = fileSelector.isWantedFile(javaFile);
        assertEquals("Java file must be selected!", true, selected);
    }

    
    @Test
    public void testSelectJspFile() {
        SourceFileSelector fileSelector = new SourceFileSelector();
        fileSelector.setSelectJspFiles(true);

        File javaFile = new File("/path/to/MyPage.jsp");

        boolean selected = fileSelector.isWantedFile(javaFile);
        assertEquals("Jsp file must be selected!", true, selected);
    }

    
    @Test
    public void testUnwantedFile() {
        SourceFileSelector fileSelector = new SourceFileSelector();

        File javaFile = new File("/path/to/myFile.txt");

        boolean selected = fileSelector.isWantedFile(javaFile);
        assertEquals("Not-source file must not be selected!", false, selected);
    }

    
    @Test
    public void testUnwantedJavaFile() {
        SourceFileSelector fileSelector = new SourceFileSelector();
        fileSelector.setSelectJavaFiles(false);

        File javaFile = new File("/path/to/MyClass.java");

        boolean selected = fileSelector.isWantedFile(javaFile);
        assertEquals("Unwanted java file must not be selected!", false, selected);
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(FileSelectorTest.class);
    }
}
