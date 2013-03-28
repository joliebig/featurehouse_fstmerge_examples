
package genj.util.swing;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;


public class ButtonHelper {
  
  
  private Class buttonType        = JButton.class;
  private Insets insets           = null;
  private JComponent container     = null;
  private ButtonGroup group       = null;
  private int fontSize            = -1;
  
      
  public ButtonHelper setButtonType(Class set) { buttonType=set; return this; }
  public ButtonHelper setInsets(Insets set) { insets=set; return this; }
  public ButtonHelper setInsets(int val) { insets=new Insets(val,val,val,val); return this; }
  public ButtonHelper setContainer(JComponent set) { container=set; return this; }
  public ButtonHelper setFontSize(int set) { fontSize=set; return this; }
  
    public ButtonGroup createGroup() {
    group = new ButtonGroup();
    return group;
  }

  
  public AbstractButton create(Action action, ImageIcon toggle, boolean state) {
    
    JToggleButton result = (JToggleButton)create(action, JToggleButton.class);
    result.setSelectedIcon(toggle);
    result.setSelected(state);
    return result;
  }

  
  public AbstractButton create(Action action) {
    return create(action, buttonType);
  }
      
  
  private AbstractButton create(final Action action, Class type) {
    
    
    if (container instanceof JToolBar)
      action.putValue(Action.MNEMONIC_KEY, null);
    
    
    final AbstractButton result = createButton(type);
    if (result instanceof JButton) {
        result.setVerticalTextPosition(SwingConstants.BOTTOM);
        result.setHorizontalTextPosition(SwingConstants.CENTER);
    }
    result.setAction(action);
    
    
    if (insets!=null)
      result.setMargin(insets);
    if (fontSize>0) {
      Font f = result.getFont();
      result.setFont(new Font(f.getName(), f.getStyle(), fontSize));
    }
    
    
    if (group!=null) {
      group.add(result);
    }
    if (container!=null) {
      container.add(result);
      if (container instanceof JToolBar) result.setMaximumSize(new Dimension(128,128));
    }

    
    return result;
  }

    
  private AbstractButton createButton(Class type) {
    try {
      return (AbstractButton)type.newInstance();
    } catch (Throwable t) {
      throw new IllegalStateException("Couldn't create AbstractButton for "+buttonType);
    }
  }
  
} 
