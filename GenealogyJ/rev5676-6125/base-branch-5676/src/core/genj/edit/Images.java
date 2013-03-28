
package genj.edit;

import genj.util.swing.ImageIcon;


final public class Images {

  private static Images instance = new Images();

  public static ImageIcon
  
    imgView,
    imgAdvanced,
    
    imgStickOn,
    imgStickOff,
    imgBack,
    imgForward,
    
    imgUndo,
    imgRedo,
    
    imgCut,
    imgCopy, 
    imgPaste,
    
    imgNew,
	
    imgDelEntity,
    imgNewEntity;

  
  private Images() {
    
    imgView      = new ImageIcon(this,"images/View");
    imgAdvanced  = new ImageIcon(this,"images/Advanced");

    imgStickOn   = new ImageIcon(this,"images/StickOn");
    imgStickOff  = new ImageIcon(this,"images/StickOff");
    imgBack    = new ImageIcon(this,"images/Return");
    imgForward  = new ImageIcon(this,"images/Forward");

    imgUndo      = new ImageIcon(this,"images/Undo");
    imgRedo      = new ImageIcon(this,"images/Redo");
    
    imgCut       = new ImageIcon(this,"images/Cut");
    imgCopy      = new ImageIcon(this,"images/Copy");
    imgPaste     = new ImageIcon(this,"images/Paste");
    
    imgNew     = new ImageIcon(this,"images/New");
    
    imgNewEntity   = new ImageIcon(this,"images/entity/New");
    imgDelEntity    = new ImageIcon(this,"images/entity/Delete");
  }
}
