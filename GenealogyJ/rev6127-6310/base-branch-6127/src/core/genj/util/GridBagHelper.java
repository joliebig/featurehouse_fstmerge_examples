
package genj.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;


public class GridBagHelper {

  public final static int
    FILL_HORIZONTAL =  1,
    FILL_VERTICAL   =  2,
    FILL_BOTH       =  4,
    GROW_HORIZONTAL = 16,
    GROW_VERTICAL   = 32,
    GROW_BOTH       = 64;

  public final static int
    GROWFILL_HORIZONTAL = FILL_HORIZONTAL|GROW_HORIZONTAL,
    GROWFILL_VERTICAL   = FILL_VERTICAL  |GROW_VERTICAL  ,
    GROWFILL_BOTH       = FILL_BOTH      |GROW_BOTH      ;
    
  
  private GridBagLayout layout;
  
  
  private GridBagConstraints constraints;
  
  
  private Container container;
  
  
  private Insets presetInsets = new Insets(0,0,0,0);

  
  private int presetParameter = 0;

  
  private class Fill extends Component {
    
    Dimension size;
    Fill(Dimension size) {
      this.size=size;
    }
    public Dimension getPreferredSize() {
      return size;
    }
    public Dimension getMinimumSize() {
      return size;
    }
    
  }

  
  public GridBagHelper(Container container) {

    
    this.container=container;

    
    layout = new GridBagLayout();
    container.setLayout(layout);

    
    constraints = new GridBagConstraints();

    
  }

  
  
  public GridBagHelper setInsets(Insets set) {
    presetInsets = set;
    return this;
  }
  
  
  public GridBagHelper setParameter(int set) {
    presetParameter = set;
    return this;
  }

  
  public Component add(Component component,int x,int y) {
    return add(component,x,y,1,1);
  }

  
  public Component add(Component component,int x,int y,int w,int h) {
    return add(component,x,y,w,h,presetParameter);
  }

  
  public Component add(Component component,int x,int y,int w,int h,int parm) {
    return add(component,x,y,w,h,parm,presetInsets);
  }

  
  public Component add(Component component,int x,int y,int w,int h,int parm, Insets insets) {

    
    container.add(component);

    
    constraints.gridx     = x;
    constraints.gridy     = y;
    constraints.gridwidth = w;
    constraints.gridheight= h;
    constraints.weightx   = isSet(parm,GROW_BOTH) || isSet(parm,GROW_HORIZONTAL) ? 1 : 0;
    constraints.weighty   = isSet(parm,GROW_BOTH) || isSet(parm,GROW_VERTICAL  ) ? 1 : 0;
    constraints.fill      = GridBagConstraints.NONE;
    constraints.insets    = insets;

    if ( isSet(parm,FILL_BOTH) || (isSet(parm,FILL_HORIZONTAL)&&isSet(parm,FILL_VERTICAL)) )
      constraints.fill = GridBagConstraints.BOTH      ;
    else if (isSet(parm,FILL_HORIZONTAL))
      constraints.fill = GridBagConstraints.HORIZONTAL;
    else if (isSet(parm,FILL_VERTICAL  ))
      constraints.fill = GridBagConstraints.VERTICAL  ;

    
    layout.setConstraints(component,constraints);

    
    return component;
  }

  
  public void addFiller(int x, int y) {
    add(new Fill(new Dimension(0,0)), x, y, 1, 1, GROW_BOTH);
  }

  
  public void addFiller(int x, int y, Dimension dim) {
    add(new Fill(dim), x, y, 1, 1);
  }

  
  private boolean isSet(int value, int mask) {
    return ((value&mask)!=0);
  }

} 
