
package genj.edit;

import genj.util.swing.ImageIcon;


final public class Images {

  private static Images instance = new Images();

  public static ImageIcon
  
    imgView,
    
    imgStickOn,
    imgStickOff,
    imgFocus,
    
    imgUndo,
    imgRedo,
    
    imgCut,
    imgCopy, 
    imgPaste,
    
    imgAdd,
	
    imgDel,
    imgNew;

  
  private Images() {
    
    imgView      = new ImageIcon(this,"images/View");

    imgStickOn   = new ImageIcon(this,"images/StickOn");
    imgStickOff  = new ImageIcon(this,"images/StickOff");
    imgFocus     = new ImageIcon(this,"images/Focus");

    imgUndo      = new ImageIcon(this,"images/Undo");
    imgRedo      = new ImageIcon(this,"images/Redo");
    
    imgCut       = new ImageIcon(this,"images/Cut");
    imgCopy      = new ImageIcon(this,"images/Copy");
    imgPaste     = new ImageIcon(this,"images/Paste");
    
    imgAdd       = new ImageIcon(this,"images/Add");
    
    imgNew       = new ImageIcon(this,"images/New");
    imgDel       = new ImageIcon(this,"images/Delete");
  }
}
