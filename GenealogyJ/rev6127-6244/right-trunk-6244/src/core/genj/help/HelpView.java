
package genj.help;

import genj.util.swing.Action2;
import genj.util.swing.ImageIcon;
import genj.view.ToolBar;
import genj.view.View;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.SwingUtilities;


public class HelpView extends View {
  
  public final static ImageIcon IMG = new ImageIcon(HelpView.class,"Help.png");

  private HelpWidget content = new HelpWidget();
  private Back back;
  private Forward forward;
  
  public HelpView() {
    super(new BorderLayout());
    add(content, BorderLayout.CENTER);
    
    back = new Back();
    forward = new Forward();
   
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        if (content.getPage()==null)
          content.setPage(HelpWidget.MANUAL);
      }
    });
  }

  public void setPage(String page) {
    content.setPage(page);
  }
  
  @Override
  public void populate(ToolBar toolbar) {
    toolbar.add(back);
    toolbar.add(forward);
  }
  
  private class Back extends Action2 implements PropertyChangeListener {
    
    private ArrayList<String> urls = new ArrayList<String>();
    public Back() {
      setImage(new ImageIcon(this, "Back.png"));
      content.addPropertyChangeListener("url", this);
      setEnabled(false);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      String old = (String)evt.getOldValue();
      if (old!=null) {
        if (urls.isEmpty() || !urls.get(urls.size()-1).equals(old)) 
          urls.add(old);
        setEnabled(true);
      }
      forward.clear();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
      String old = content.getPage();
      String page = urls.get(urls.size()-1);
      content.removePropertyChangeListener("url", this);
      content.setPage(page);
      content.addPropertyChangeListener("url", this);
      urls.remove(urls.size()-1);
      setEnabled(!urls.isEmpty());
      if (old!=null)
        forward.push(old);
    }
  }
  
  private class Forward extends Action2 {
    
    private ArrayList<String> pages = new ArrayList<String>();
    
    Forward() {
      setImage(new ImageIcon(this, "Forward.png"));
      setEnabled(false);
    }
    
    void clear() {
      pages.clear();
      setEnabled(false);
    }
    
    void push(String page) {
      pages.add(page);
      setEnabled(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
      content.setPage(pages.remove(pages.size()-1));
      setEnabled(!pages.isEmpty());
    }
  }
}
