
package genj.tree;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GridCache {
  
  
  private final static List EMPTY = new ArrayList(0);

  
  private Object[][] grid;
 
  
  private Rectangle2D system;

  
  private double resolution;
  
  
  public GridCache(Rectangle2D syStem, double resolUtion) {
    
    system = syStem;
    resolution = resolUtion;
    
    int 
      cols = (int)Math.ceil(system.getWidth ()/resolution),
      rows = (int)Math.ceil(system.getHeight()/resolution);
    
    grid = new Object[rows][cols];
    
  }
  
  
  public void put(Object object, Rectangle2D range, Point2D pos) {
    
    int
      scol = (int)Math.floor((range.getMinX()+pos.getX() - system.getMinX())/resolution),
      srow = (int)Math.floor((range.getMinY()+pos.getY() - system.getMinY())/resolution),
      ecol = (int)Math.ceil ((range.getMaxX()+pos.getX() - system.getMinX())/resolution),
      erow = (int)Math.ceil ((range.getMaxY()+pos.getY() - system.getMinY())/resolution);
      
    if (scol>grid[0].length||srow>grid.length||ecol<0||erow<0) return;
    if (srow<0) srow = 0;
    if (erow>grid.length) erow = grid.length;
    if (scol<0) scol = 0;
    if (ecol>grid[0].length) ecol = grid[0].length;
      
    
    for (int row=srow;row<erow;row++) {
      for (int col=scol;col<ecol;col++) {
        put(object, row, col);
      }
    }
    
  }
  
  
  public void put(Object object, int row, int col) {
    
    Object old = grid[row][col]; 
    if (old==null) {
      
      grid[row][col] = object;
    } else {
      
      if (old instanceof EntryList) {
        ((EntryList)old).add(object);
      } else {
        List l = new EntryList();
        l.add(old);
        l.add(object);
        grid[row][col] = l;
      }
    }
    
  }  

  
  public Set get(Rectangle2D range) {
    
    Set result = new HashSet();

    
    int
      scol = (int)Math.floor((range.getMinX() - system.getMinX())/resolution),
      srow = (int)Math.floor((range.getMinY() - system.getMinY())/resolution),
      ecol = (int)Math.ceil ((range.getMaxX() - system.getMinX())/resolution),
      erow = (int)Math.ceil ((range.getMaxY() - system.getMinY())/resolution);
      
    if (scol>grid[0].length||srow>grid.length||ecol<0||erow<0) return result;
    if (srow<0) srow = 0;
    if (erow>grid.length) erow = grid.length;
    if (scol<0) scol = 0;
    if (ecol>grid[0].length) ecol = grid[0].length;
      
    
    for (int row=srow;row<erow;row++) {
      for (int col=scol;col<ecol;col++) {
        get(result, row, col);
      }
    }
    
    
    return result;
  }

    public void get(Set set, int row, int col) {
    
    Object o = grid[row][col];
    if (o==null) return;
    if (o instanceof EntryList) set.addAll((EntryList)o);
    else set.add(o);
    
  }

  
  private class EntryList extends ArrayList {
        private EntryList() {
      super(8);
    }
  } 
       } 
