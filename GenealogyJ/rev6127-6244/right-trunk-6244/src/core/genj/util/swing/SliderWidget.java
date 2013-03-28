
package genj.util.swing;

import javax.swing.BoundedRangeModel;
import javax.swing.JSlider;
import javax.swing.JToolBar;


public class SliderWidget extends JSlider {

  
  public SliderWidget() {
    super();
  }

  
  public SliderWidget(int orientation) {
    super(orientation);
  }

  
  public SliderWidget(int min, int max) {
    super(min, max);
  }

  
  public SliderWidget(int min, int max, int value) {
    super(min, max, value);
  }

  
  public SliderWidget(int orientation, int min, int max, int value) {
    super(orientation, min, max, value);
  }

  
  public SliderWidget(BoundedRangeModel brm) {
    super(brm);
  }

  
  public void addNotify() {
    
    if (getParent() instanceof JToolBar) {
      int orientation = ((JToolBar)getParent()).getOrientation();
      setOrientation(orientation);
    }
    setMaximumSize(getPreferredSize());
    setAlignmentX(0);
    
    super.addNotify();
  }
} 
