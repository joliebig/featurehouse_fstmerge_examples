
package genj.util.swing;

import genj.util.MnemonicAndText;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


public class MenuHelper  {
  
  private Stack menus            = new Stack();  
  private Component target       = null;

      
  public MenuHelper popMenu() { 
    
    JMenu menu = (JMenu)menus.pop(); 
    
    if (menu.getMenuComponentCount()==0)
      menu.getParent().remove(menu);
    
    return this; 
  }
  public MenuHelper pushMenu(JPopupMenu popup) { menus.push(popup); return this; }
  public MenuHelper setTarget(Component set) { target=set; return this; }

  
  public JMenuBar createBar() {
    JMenuBar result = new JMenuBar();
    menus.push(result);
    return result;
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
    Object menu = peekMenu();
    if (menu instanceof JMenu)
      ((JMenu)menu).add(result);
    if (menu instanceof JPopupMenu)
      ((JPopupMenu)menu).add(result);
    if (menu instanceof JMenuBar)
      ((JMenuBar)menu).add(result);

    menus.push(result);
    return result;
  }
  
  
  public JPopupMenu createPopup() {
    
    JPopupMenu result = new JPopupMenu();
    
    pushMenu(result);
    
    return result;
  }

  
  public JPopupMenu createPopup(Component component) {
    
    
    final JPopupMenu result = createPopup();
    
    
    component.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        
        
        mouseReleased(e);
      }
      public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
          result.show(e.getComponent(),e.getX(), e.getY());
        }
      }
    });
    
    
    return result;
  }
  
  
  public JLabel createItem(String txt, ImageIcon img, boolean emphasized) {
    JLabel item = new JLabel(txt, img, JLabel.CENTER);
    if (emphasized) {
      item.setFont(item.getFont().deriveFont(Font.BOLD));
    }
    createItem(item);
    return item;
  }
  
  private void createItem(Component item) {

    Object menu = peekMenu();
    if (menu instanceof JMenu)
      ((JMenu)menu).add(item);
    if (menu instanceof JPopupMenu)
      ((JPopupMenu)menu).add(item);
    if (menu instanceof JMenuBar)
      ((JMenuBar)menu).add(item);
    
  }

  
  public void createItems(List actions) {
    
    if (actions==null||actions.isEmpty())
      return;
    createSeparator();
    
    Iterator it = actions.iterator();
    while (it.hasNext()) {
      Object o = it.next();
      
      if (o instanceof Action2.Group) {
        createMenu(((Action2.Group)o).getName(), ((Action2.Group)o).getIcon());
        createItems((List)o);
        popMenu();
        continue;
      }
      
      if (o instanceof List) {
        createSeparator();
        createItems((List)o);
        continue;
      }
      
      if (o instanceof Component) {
        createItem((Component)o);
        continue;
      }
      
      if (o instanceof Action2) {
        createItem((Action2)o);
        continue;
      }
      
      throw new IllegalArgumentException("type "+o.getClass()+" n/a");
    }
    
  }

  
  public JMenuItem createItem(Action2 action) {
    
    
    if (action == Action2.NOOP) {
      createSeparator();
      return null;
    }
    
    
    JMenuItem result = new JMenuItem();
    result.setAction(action);

    
    if (action.getAccelerator()!=null)
      result.setAccelerator(action.getAccelerator());
  
    
    Object menu = peekMenu();
    if (menu instanceof JMenu)
      ((JMenu)menu).add(result);
    if (menu instanceof JPopupMenu)
      ((JPopupMenu)menu).add(result);
    if (menu instanceof JMenuBar)
      ((JMenuBar)menu).add(result);
      
    
    if (target!=null) action.setTarget(target);
    
    
    return result;
  }

  
  public MenuHelper createSeparator() {
    
    Object menu = peekMenu();
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

    
  private Object peekMenu() {
    if (menus.size()==0) return null;
    return menus.peek();
  }
  
} 

