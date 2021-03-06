
package net.sf.jabref;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicFileChooserUI;
import javax.swing.plaf.metal.MetalFileChooserUI;





public class JabRefFileChooser extends JFileChooser
{

    
    private static Dimension lastSize = null;

    public JabRefFileChooser()
    {
        super();
        
    }

    public JabRefFileChooser(File file){
        super(file);
    }

    

    

    
    
    

    protected void setUI(ComponentUI newUI) {
      if (Globals.osName.equals(Globals.MAC))
        super.setUI(newUI);
      else
        super.setUI(new JabRefUI(this));
     }
    
    
    

    public static void main(String[] args) {
        JabRefFileChooser fc = new JabRefFileChooser();
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
        }
    }
}

class JabRefUI extends MetalFileChooserUI {
    public JabRefUI(JFileChooser filechooser) {
        super(filechooser);
    }
    protected class DoubleClickListener extends BasicFileChooserUI.DoubleClickListener {
        JList list;
        public DoubleClickListener(JList list) {
            super(list);
            this.list = list;
        }
        public void mouseEntered(MouseEvent e) {
            
            MouseListener [] l = list.getMouseListeners();
            for (int i = 0; i < l.length; i++) {
                if (l[i] instanceof MetalFileChooserUI.SingleClickListener) {
                    list.removeMouseListener(l[i]);
                }
            }
            super.mouseEntered(e);
        }
    }
    protected MouseListener createDoubleClickListener(JFileChooser fc, JList list) {
        return new DoubleClickListener(list);
    }
}
