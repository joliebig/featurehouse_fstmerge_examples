
package genj.help;

import genj.util.Resources;
import genj.util.swing.ImageIcon;
import genj.view.View;
import genj.view.ViewFactory;


public class HelpViewFactory implements ViewFactory {

  public View createView() {
    return new HelpView();
  }

  public ImageIcon getImage() {
    return HelpView.IMG;
  }

  public String getTitle() {
    return Resources.get(this).getString("help.title");
  }

}
