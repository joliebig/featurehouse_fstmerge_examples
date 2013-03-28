
package genj.util.swing;

import genj.util.MnemonicAndText;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Stack;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


public class MenuHelper  {
  
  private Stack<JComponent> menus = new Stack<JComponent>();  
  private Component target = null;
  
  
  public final static Action2 NOOP = new Action2() {
    public void actionPerformed(ActionEvent e) {};
  };

      
  public MenuHelper popMenu() { 
    menus.pop(); 
    return this; 
  }
  
  public MenuHelper pushMenu(JComponent menu) { 
    if (!menus.isEmpty())
      menus.peek().add(menu);
    menus.push(menu);
    
    return this;
  }
  
  public MenuHelper setTarget(Component set) { 
    target=set; 
    return this; 
  }

  
  public JMenu createMenu(String text) {
    return createMenu(text, null);
  }

  
  public JMenu createMenu(String text, Icon img) {
    
    JMenu result = new JMenu();
    
    if (text!=null&&text.length()>0) {
      MnemonicAndText mat = new MnemonicAndText(text);
      result.setText(mat.getText());
      result.setMnemonic(mat.getMnemonic());
    }
    if (img!=null) 
      result.setIcon(img);
    pushMenu(result);
    return result;
  }
  
  
  public JPopupMenu createPopup() {
    
    JPopupMenu result = new JPopupMenu();
    
    pushMenu(result);
    
    return result;
  }

  
  public void createItems(Iterable<Action2> actions) {
    
    if (actions==null)
      return;
    
    boolean first = true;
    for (Action2 action : actions) {
      if (first) {
        createSeparator();
        first = false;
      }
      createItem(action);
    }
    
  }
  
  public JMenuItem createItem(Action2 action) {
    
    
    if (action instanceof Action2.Group) {
      JMenu sub = new JMenu(action);
      sub.setMnemonic(action.getMnemonic());
      pushMenu(sub);
      createItems((Action2.Group)action);
      popMenu();
      return sub;
    }
    
    
    if (action == MenuHelper.NOOP) {
      createSeparator();
      return null;
    }
    
    
    JMenuItem result = new JMenuItem();
    result.setAction(action);
    result.setMnemonic(action.getMnemonic());
    if (target!=null) 
      action.setTarget(target);
    
    
    menus.peek().add(result);
      
    
    return result;
  }

  
  public MenuHelper createSeparator() {
    
    JComponent menu = menus.peek();
    if (menu instanceof JMenu) {
      JMenu jmenu = (JMenu)menu;
      int count = jmenu.getMenuComponentCount();
      if (count>0 && jmenu.getMenuComponent(count-1).getClass() != JPopupMenu.Separator.class)
        jmenu.addSeparator();
    }
    if (menu instanceof JPopupMenu) {
      JPopupMenu pmenu = (JPopupMenu)menu;
      int count = pmenu.getComponentCount();
      if (count>0 && pmenu.getComponent(count-1).getClass() != JPopupMenu.Separator.class)
        pmenu.addSeparator();
    }
    
    return this;
  }
  
} 

