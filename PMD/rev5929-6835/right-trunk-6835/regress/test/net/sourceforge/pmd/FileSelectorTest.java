package test.net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;

import java.io.File;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageFilenameFilter;

import org.junit.Test;


public class FileSelectorTest {

    
    @Test
    public void testWantedFile() {
        LanguageFilenameFilter fileSelector = new LanguageFilenameFilter(Language.JAVA);

        File javaFile = new File("/path/to/myFile.java");

        boolean selected = fileSelector.accept(javaFile.getParentFile(), javaFile.getName());
        assertEquals("This file should be selected !",true, selected);
    }

    
    
    @Test
    public void testUnwantedFile() {
        LanguageFilenameFilter fileSelector = new LanguageFilenameFilter(Language.JAVA);

        File javaFile = new File("/path/to/myFile.txt");

        boolean selected = fileSelector.accept(javaFile.getParentFile(), javaFile.getName());
        assertEquals("Not-source file must not be selected!", false, selected);
    }

    
    @Test
    public void testUnwantedJavaFile() {
        LanguageFilenameFilter fileSelector = new LanguageFilenameFilter(Language.XML);

        File javaFile = new File("/path/to/MyClass.java");

        boolean selected = fileSelector.accept(javaFile.getParentFile(), javaFile.getName());
        assertEquals("Unwanted java file must not be selected!", false, selected);
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(FileSelectorTest.class);
    }
}
