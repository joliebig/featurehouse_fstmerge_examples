
package genj.help;

import genj.util.swing.ImageIcon;
import genj.view.View;

import java.awt.BorderLayout;


public class HelpView extends View {
  
  public final static ImageIcon IMG = new ImageIcon(HelpView.class,"Help.png");

  public HelpView() {
    super(new BorderLayout());
    add(new HelpWidget(), BorderLayout.CENTER);
  }
}
