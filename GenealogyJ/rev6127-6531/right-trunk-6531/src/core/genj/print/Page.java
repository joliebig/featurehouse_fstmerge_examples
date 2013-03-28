
package genj.print;

import genj.renderer.DPI;


public class Page {
  
  private int x,y;
  private double widthInches;
  private double heightInches;
  private DPI dpi;
  
  
  public double width() {
    return widthInches;
  }

  
  public double height() {
    return heightInches;
  }

  public DPI dpi() {
    return dpi;
  }
  
  public int x() {
    if (x==Integer.MAX_VALUE)
      throw new IllegalArgumentException("no index information");
    return x;
  }

  public int y() {
    if (y==Integer.MAX_VALUE)
      throw new IllegalArgumentException("no index information");
    return y;
  }

  
  public Page(double widthInches, double heightInches, DPI dpi) {
    this.x = Integer.MAX_VALUE;
    this.y = Integer.MAX_VALUE;
    this.widthInches = widthInches;
    this.heightInches = heightInches;
    this.dpi = dpi;
  }
  
  
  public Page(int x, int y, double widthInches, double heightInches, DPI dpi) {
    if (x<0||x==Integer.MAX_VALUE)
      throw new IllegalArgumentException("invalid x");
    if (y<0||y==Integer.MAX_VALUE)
      throw new IllegalArgumentException("invalid y");
    this.x = x;
    this.y = y;
    this.widthInches = widthInches;
    this.heightInches = heightInches;
    this.dpi = dpi;
  }
}
