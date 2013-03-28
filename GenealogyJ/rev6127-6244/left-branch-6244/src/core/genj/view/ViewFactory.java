
package genj.view;

import genj.util.swing.ImageIcon;



public interface ViewFactory {
  
  
  public View createView();
  
  
  public ImageIcon getImage();
  
  
  public String getTitle();
  
} 
