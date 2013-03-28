
package genj.renderer;

import genj.option.CustomOption;
import genj.option.Option;
import genj.option.OptionProvider;
import genj.option.PropertyOption;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.DialogHelper;
import genj.util.swing.ScreenResolutionScale;

import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.ArrayList;
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

  
  public List<? extends Option> getOptions() {
    List<Option> result = new ArrayList<Option>(PropertyOption.introspect(getInstance()));
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

    
    public void persist() {
      Registry.get(this).put("dpi", dpi);
    }

    
    public void restore() {
      Point set = Registry.get(this).get("dpi", (Point)null);
      if (set!=null)
        dpi = set;
    }

    
    protected void edit() {
      ScreenResolutionScale scale = new ScreenResolutionScale(dpi);
      int rc = DialogHelper.openDialog(getName(), DialogHelper.QUESTION_MESSAGE, scale, Action2.okCancel(), widget);
      if (rc==0)
        dpi = scale.getDPI();
    }

  } 

} 
