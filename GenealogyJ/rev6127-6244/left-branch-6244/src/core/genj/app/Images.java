
package genj.app;

import genj.util.swing.ImageIcon;


final public class Images {

  private static Images instance = new Images();

  public static ImageIcon
    imgClose, imgNew, imgOpen, imgExit, imgSave, imgAbout;

  
  private Images() {
    imgClose        = new ImageIcon(this,"images/Close");
    imgNew         = new ImageIcon(this,"images/New");
    imgOpen         = new ImageIcon(this,"images/Open");
    imgExit         = new ImageIcon(this,"images/Exit");
    imgSave         = new ImageIcon(this,"images/Save");
    imgAbout        = new ImageIcon(this,"images/About");
  }
}
