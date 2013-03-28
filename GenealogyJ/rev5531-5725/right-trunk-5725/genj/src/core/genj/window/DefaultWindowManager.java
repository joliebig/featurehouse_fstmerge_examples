
package genj.window;

import genj.util.Registry;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;


public class DefaultWindowManager extends WindowManager {

  
  private Rectangle screen = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
  
  
  private JFrame defaultFrame = new JFrame();
  
  
  public DefaultWindowManager(Registry registry, ImageIcon defaultDialogImage) {
    super(registry);
    if (defaultDialogImage!=null) defaultFrame.setIconImage(defaultDialogImage.getImage());
  }
  
  
  protected Component openWindowImpl(final String key, String title, ImageIcon image, JComponent content, JMenuBar menu, Rectangle bounds, boolean maximized, final Action onClosing) {
    
    
    final JFrame frame = new JFrame() {
      
      public void dispose() {
        
        closeNotify(key, getBounds(), getExtendedState()==MAXIMIZED_BOTH);
        
        super.dispose();
      }
    };

    
    if (title!=null) frame.setTitle(title);
    if (image!=null) frame.setIconImage(image.getImage());
    if (menu !=null) frame.setJMenuBar(menu);

    
    frame.getContentPane().add(content);

    
    if (onClosing==null) {
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    } else {
      
      frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          onClosing.actionPerformed(new ActionEvent(this, 0, key));
        }
      });
    }

    
    if (bounds==null) {
      frame.pack();
      Dimension dim = frame.getSize();
      bounds = new Rectangle(screen.width/2-dim.width/2, screen.height/2-dim.height/2,dim.width,dim.height);
      LOG.log(Level.FINE, "Sizing window "+key+" to "+bounds+" after pack()");
    }
    frame.setBounds(bounds.intersection(screen));
    
    if (maximized)
      frame.setExtendedState(Frame.MAXIMIZED_BOTH);

    
    frame.setVisible(true);
    
    
    return frame;
  }
  
  
  protected Component openNonModalDialogImpl(final String key, String title,  int messageType, JComponent content, Action[] actions, Component owner, Rectangle bounds) {

    
    JOptionPane optionPane = new Content(messageType, content, actions);
    
    
    final JDialog dlg = optionPane.createDialog(owner != null ? owner : defaultFrame, title);
    dlg.setResizable(true);
    dlg.setModal(false);
    if (bounds==null) {
      dlg.pack();
      if (owner!=null)
        dlg.setLocationRelativeTo(owner.getParent());
    } else {
      if (owner==null) {
        dlg.setBounds(bounds.intersection(screen));
      } else {
        dlg.setBounds(new Rectangle(bounds.getSize()).intersection(screen));
        dlg.setLocationRelativeTo(owner.getParent());
      }
    }

    
    dlg.addComponentListener(new ComponentAdapter() {
      public void componentHidden(ComponentEvent e) {
        closeNotify(key, dlg.getBounds(), false);
        dlg.dispose();
      }
    });
    
    
    dlg.setVisible(true);
    
    
    return dlg;
  }
  
  
  protected Object openDialogImpl(final String key, String title,  int messageType, JComponent content, Action[] actions, Component owner, Rectangle bounds) {

    
    JOptionPane optionPane = new Content(messageType, content, actions);
    
    
    final JDialog dlg = optionPane.createDialog(owner != null ? owner : defaultFrame, title);
    dlg.setResizable(true);
    dlg.setModal(true);
    if (bounds==null) {
      dlg.pack();
      if (owner!=null)
        dlg.setLocationRelativeTo(owner.getParent());
    } else {
      if (owner==null) {
        dlg.setBounds(bounds.intersection(screen));
      } else {
        dlg.setBounds(new Rectangle(bounds.getSize()).intersection(screen));
        dlg.setLocationRelativeTo(owner.getParent());
      }
    }

    
    dlg.addComponentListener(new ComponentAdapter() {
      public void componentHidden(ComponentEvent e) {
        closeNotify(key, dlg.getBounds(), false);
        dlg.dispose();
      }
    });
    
    
    dlg.setVisible(true);
    
    
    return optionPane.getValue();
  }

  @Override
  public void setTitle(String key, String title) {
    
    Object framedlg = recall(key);
    
    if (framedlg instanceof JFrame) {
      ((JFrame)framedlg).setTitle(title); 
      return;
    }

    if (framedlg instanceof JDialog) {
      ((JDialog)framedlg).setTitle(title);
      return;
    }
    
  }
  
  
  public boolean show(String key) {

    Object framedlg = recall(key);
    
    if (framedlg instanceof JFrame) {
      ((JFrame)framedlg).toFront(); 
      return true;
    }

    if (framedlg instanceof JDialog) {
      ((JDialog)framedlg).toFront();
      return true;
    }

    return false;
  }
  
  
  public void close(String key) {

    Object framedlg = recall(key);
    
    if (framedlg instanceof JFrame) {
      JFrame frame = (JFrame)framedlg;
      frame.dispose(); 
      return;
    }

    if (framedlg instanceof JDialog) {
      JDialog dlg = (JDialog)framedlg;
      dlg.setVisible(false); 
      return;
    }

    
  }
  
  
  public List getRootComponents() {

    List result = new ArrayList();
    
    
    String[] keys = recallKeys();
    for (int k=0; k<keys.length; k++) {
      
      Object framedlg = recall(keys[k]);

      if (framedlg instanceof JFrame)      
        result.add(((JFrame)framedlg).getRootPane());

      if (framedlg instanceof JDialog)      
        result.add(((JDialog)framedlg).getRootPane());
    }
    
    
    return result;
  }
  
  
  public JComponent getContent(String key) {
    
    Object framedlg = recall(key);
    
    if (framedlg instanceof JFrame)
      return (JComponent)((JFrame)framedlg).getContentPane().getComponent(0); 

    if (framedlg instanceof JDialog)
      return (JComponent)((JDialog)framedlg).getContentPane().getComponent(0);

    return null;
  }

    
  private Window getWindowForComponent(Component c) {
    if (c instanceof Frame || c instanceof Dialog || c==null)
      return (Window)c;
    return getWindowForComponent(c.getParent());
  }
  
} 