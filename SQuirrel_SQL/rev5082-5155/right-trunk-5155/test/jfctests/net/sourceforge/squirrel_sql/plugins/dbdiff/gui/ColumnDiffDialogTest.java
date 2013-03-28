
package net.sourceforge.squirrel_sql.plugins.dbdiff.gui;

import java.awt.Frame;

import javax.swing.JFrame;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ColumnDiffDialogTest extends BaseSQuirreLJUnit4TestCase {

    ColumnDiffDialog dialogUnderTest = null;
    
    @Before
    public void setUp() throws Exception {
        dialogUnderTest = new ColumnDiffDialog(createMainFrame(), false);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test (expected = IllegalArgumentException.class)
    public void setSession1Label() {
        dialogUnderTest.setSession1Label(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void setSession2Label() {
        dialogUnderTest.setSession2Label(null);
    }
    
    
    
    private Frame createMainFrame() {
        return new JFrame();
    }
    
}
