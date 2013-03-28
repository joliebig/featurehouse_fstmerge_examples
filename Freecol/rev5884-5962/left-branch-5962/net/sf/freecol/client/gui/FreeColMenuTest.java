

package net.sf.freecol.client.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.common.FreeColException;

public class FreeColMenuTest implements ActionListener, ItemListener {
    JTextArea output;
    JScrollPane scrollPane;
    String newline = "\n";
    
    public JMenuBar createMenuBar() {
        JMenuBar menuBar = null;

        try {
            ImageLibrary lib = new ImageLibrary("/home/johnathanj/work/freecol/freecol/data/");
            FreeColClient client = new FreeColClient(true, new Dimension(-1, -1), lib, null, null, false); 
            
            
            menuBar = new InGameMenuBar(client);
            
            client.getActionManager().update();
            ((FreeColMenuBar)menuBar).update();
        }
        catch (FreeColException e) {
            e.printStackTrace();
        }

        return menuBar;
    }

    public Container createContentPane() {
        
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setOpaque(true);

        
        output = new JTextArea(5, 30);
        output.setEditable(false);
        scrollPane = new JScrollPane(output);

        
        contentPane.add(scrollPane, BorderLayout.CENTER);

        return contentPane;
    }

    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        String s = "Action event detected."
                   + newline
                   + "    Event source: " + source.getText()
                   + " (an instance of " + getClassName(source) + ")";
        output.append(s + newline);
        output.setCaretPosition(output.getDocument().getLength());
    }

    public void itemStateChanged(ItemEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        String s = "Item event detected."
                   + newline
                   + "    Event source: " + source.getText()
                   + " (an instance of " + getClassName(source) + ")"
                   + newline
                   + "    New state: "
                   + ((e.getStateChange() == ItemEvent.SELECTED) ?
                     "selected":"unselected");
        output.append(s + newline);
        output.setCaretPosition(output.getDocument().getLength());
    }

    
    protected String getClassName(Object o) {
        String classString = o.getClass().getName();
        int dotIndex = classString.lastIndexOf(".");
        return classString.substring(dotIndex+1);
    }

    
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = FreeColMenuTest.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    
    private static void createAndShowGUI() {
        
        
        FullScreenFrame frame = new FullScreenFrame(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice());
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        
        FreeColMenuTest demo = new FreeColMenuTest();
        frame.setJMenuBar(demo.createMenuBar());
        frame.setContentPane(demo.createContentPane());

        
        frame.setSize(450, 260);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
