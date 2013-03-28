
package genj.window;

import genj.util.Registry;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.util.List;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;


public class LightweightWindowManager extends DefaultWindowManager {
  
  
  private JDesktopPane desktop;
  
  
  public LightweightWindowManager(Registry registry, ImageIcon defaultDialogImage) {
    super(registry, defaultDialogImage);
  }
  
  
  private JDesktopPane getDesktop(String title, ImageIcon img) {
    
    if (desktop!=null) 
      return desktop;
    
    desktop = new JDesktopPane() {
      
      public Dimension getPreferredSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
      }
    };
    
    JFrame frame = new JFrame(title);
    frame.setIconImage(img.getImage());
    frame.getContentPane().add(new JScrollPane(desktop));
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
    
    return desktop; 
  }

  
  public boolean show(String key) {
    
    Object frame = recall(key);
    if (frame instanceof JInternalFrame) {
      ((JInternalFrame)frame).toFront();
    }
    
    return super.show(key);
  }
  
  
  protected Component openWindowImpl(final String key, String title, ImageIcon image, JComponent content, JMenuBar menu, Rectangle bounds, boolean maximized, final Action onClosing) {
    
    
    final JInternalFrame frame = new JInternalFrame(title, true, true, true, true) {
      
      public void dispose() {
        
        closeNotify(key, getBounds(), isMaximum());
        
        super.dispose();
      }
    };

    
    if (image!=null) frame.setFrameIcon(image);
    if (menu !=null) frame.setJMenuBar(menu);

    
    frame.getContentPane().add(content);

    
    if (onClosing==null) {
      frame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
    } else {
      
      frame.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
      frame.addInternalFrameListener(new InternalFrameAdapter() {
        public void internalFrameClosing(InternalFrameEvent e) {
          onClosing.actionPerformed(new ActionEvent(this, 0, key));
        }
      });
    }

    
    JDesktopPane desktop = getDesktop(title, image);
    Rectangle screen = new Rectangle(desktop.getSize());
    
    if (bounds==null) { 
      frame.pack();
      Dimension dim = frame.getSize();
      bounds = new Rectangle(screen.width/2-dim.width/2, screen.height/2-dim.height/2,dim.width,dim.height);
    }
    frame.setBounds(bounds.intersection(screen));
    
    if (maximized) try {
      frame.setMaximum(true);
    } catch (PropertyVetoException veto) {
    }

    
    desktop.add(frame);
    
    frame.show();
    
    
    return frame;
  }
  
  
  public void close(String key) {
    
    Object frame = recall(key);
    if (frame instanceof JInternalFrame) {
      ((JInternalFrame)frame).dispose();
    }
    
    
    super.close(key);
    
    
  }
  
  
  public List getRootComponents() {
    List result = super.getRootComponents();
    if (desktop!=null)
      result.add(desktop);
    return result;
  }
  
  
  public JComponent getContent(String key) {

    
    Object frame = recall(key);
    if (frame instanceof JInternalFrame)
      return (JComponent)((JInternalFrame)frame).getContentPane().getComponent(0);
    
    
    return super.getContent(key);
  }
  
} 