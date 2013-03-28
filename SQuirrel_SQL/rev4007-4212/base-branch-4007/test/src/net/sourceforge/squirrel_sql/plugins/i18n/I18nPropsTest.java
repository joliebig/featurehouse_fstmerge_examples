
package net.sourceforge.squirrel_sql.plugins.i18n;


import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class I18nPropsTest {

    InMemoryI18nProperties propsUnderTest = null; 
        
    @Before
    public void setUp() throws Exception {
        propsUnderTest = new InMemoryI18nProperties();
    }

    @After
    public void tearDown() throws Exception {
        propsUnderTest = null;
    }

    
    @Test
    public void testGetTranslateableProperties_Bug1787731() {
        
        propsUnderTest.getTranslateableProperties();
    }
    
    
    private class InMemoryI18nProperties extends I18nProps {
        
        public InMemoryI18nProperties() {
            super(new File("foo"), new URL[] {});
        }
        
        @Override
        Properties getProperties() {
            StringBuilder propsFile = new StringBuilder();
            propsFile.append("test1.image=test1.jpg\n");
            propsFile.append("test2.image=test2.jpg\n");
            propsFile.append("test3.image=test3.jpg\n");
            propsFile.append("test4.image=test4.jpg\n");
            ByteArrayInputStream is = 
                new ByteArrayInputStream(propsFile.toString().getBytes());
            Properties result = new Properties();
            try {
                result.load(is);
            } catch (Exception e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
            return result;
        }
        
    }
}
