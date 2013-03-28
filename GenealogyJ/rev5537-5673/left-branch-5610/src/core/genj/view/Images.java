
package genj.view;

import genj.util.swing.ImageIcon;


final public class Images {

  private static Images instance = new Images();

  public static ImageIcon
    imgSettings,
    imgClose;

  
  private Images() {

    imgSettings  = new ImageIcon(this,"images/Settings");
    imgClose     = new ImageIcon(this,"images/Close");
  }
  
} 
