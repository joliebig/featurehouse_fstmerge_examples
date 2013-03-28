
package genj.tree;

import genj.util.swing.ImageIcon;


final class Images {

  private static Images instance = new Images();

  static ImageIcon
    imgView,
    imgOverview,
    imgHori,
    imgVert,
    imgDoFams,
    imgDontFams,
    imgFoldSymbols;
    
  
  private Images() {

    imgView        = new ImageIcon(this,"images/View"       );

    imgOverview    = new ImageIcon(this,"images/Overview"   );
    
    imgHori        = new ImageIcon(this,"images/Hori"       );
    imgVert        = new ImageIcon(this,"images/Vert"       );
    
    imgDoFams      = new ImageIcon(this,"images/DoFams"     ); 
    imgDontFams    = new ImageIcon(this,"images/DontFams"   ); 

    imgFoldSymbols = new ImageIcon(this,"images/FoldUnfold"    );
  }
  
} 
