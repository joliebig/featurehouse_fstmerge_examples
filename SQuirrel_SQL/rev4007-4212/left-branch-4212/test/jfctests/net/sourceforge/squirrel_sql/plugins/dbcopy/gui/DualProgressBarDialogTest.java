
package net.sourceforge.squirrel_sql.plugins.dbcopy.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.Before;
import org.junit.Test;

public class DualProgressBarDialogTest extends BaseSQuirreLJUnit4TestCase {

    @Before
    public void setUp() {
    }
    
    @Test
    public void test() {
        
        final JFrame test = new JFrame();
        JButton showDialog = new JButton("Show Dialog");
        showDialog.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { 
                DualProgressBarDialog.getDialog(test, "test", true, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Cancel button pressed");
                        System.exit(0);
                    }
                });
            }
        });
        test.getContentPane().setLayout(new BorderLayout());
        test.getContentPane().add(showDialog, BorderLayout.CENTER);
        test.setSize(100,100);
        test.setVisible(true);
    }
    
        
}
