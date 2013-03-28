
package genj.util.swing;

import genj.util.MnemonicAndText;
import genj.view.ActionProvider.SeparatorAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


public class MenuHelper  {

  private List<Action2> actions = new ArrayList<Action2>(16);
  private Stack<JComponent> menus = new Stack<JComponent>();  
  
      
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
  
  public JMenu createMenu(Action2.Group action) {
    JMenu result = new JMenu(action);
    pushMenu(result);
    for (Action2 sub : action)
      createItem(sub);
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
  
  public List<Action2> getActions() {
    return actions;
  }
  
  public JMenuItem createItem(Action2 action) {
    
    
    if (action instanceof Action2.Group) {
      Action2.Group group = (Action2.Group)action;
      if (group.size()==0)
        return null;
      JMenu sub = new JMenu(action);
      sub.setMnemonic(action.getMnemonic());
      pushMenu(sub);
      createItems(group);
      popMenu();
      return sub;
    }
    
    
    
    if (action instanceof SeparatorAction) {
      createSeparator();
      return null;
    }
    
    
    JMenuItem result;
    if (action.getValue(Action2.KEY_SELECTED)!=null)
      result = new JCheckBoxMenuItem();
    else
      result = new JMenuItem();
    result.setAction(action);
    result.setMnemonic(action.getMnemonic());
    
    actions.add(action);
    
    
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

