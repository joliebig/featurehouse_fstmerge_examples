
package genj.renderer;

import genj.option.CustomOption;
import genj.option.OptionProvider;
import genj.option.PropertyOption;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.ScreenResolutionScale;
import genj.window.WindowManager;

import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.List;


public class Options extends OptionProvider {

  private final static Resources RESOURCES = Resources.get(Options.class);

  
  private final static Options instance = new Options();

  
  private Font defaultFont = new Font("SansSerif", 0, 11);

  
  private Point dpi = new Point(
    Toolkit.getDefaultToolkit().getScreenResolution(),
    Toolkit.getDefaultToolkit().getScreenResolution()
  );

  
  public static Options getInstance() {
    return instance;
  }

  
  public Font getDefaultFont() {
    return defaultFont;
  }

  
  public void setDefaultFont(Font set) {
    defaultFont = set;
  }

  
  public List getOptions() {
    List result = PropertyOption.introspect(getInstance());
    result.add(new ScreenResolutionOption());
    return result;
  }

  
  public Point getDPI() {
    return dpi;
  }

  
  private class ScreenResolutionOption extends CustomOption {

    
    public String getName() {
      return RESOURCES.getString("option.screenresolution");
    }

    
    public String getToolTip() {
      return RESOURCES.getString("option.screenresolution.tip", false);
    }

    
    public void persist(Registry registry) {
      registry.put("dpi", dpi);
    }

    
    public void restore(Registry registry) {
      Point set = registry.get("dpi", (Point)null);
      if (set!=null)
        dpi = set;
    }

    
    protected void edit() {
      ScreenResolutionScale scale = new ScreenResolutionScale(dpi);
      int rc = widget.getWindowManager().openDialog(null, getName(), WindowManager.QUESTION_MESSAGE, scale, Action2.okCancel(), widget);
      if (rc==0)
        dpi = scale.getDPI();
    }

  } 

} 
