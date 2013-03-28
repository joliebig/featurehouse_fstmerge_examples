
package genj.tree;

import genj.gedcom.Gedcom;
import genj.print.PrintRenderer;
import genj.renderer.DPI;
import genj.renderer.BlueprintRenderer;
import genj.util.swing.UnitGraphics;
import gj.awt.geom.Dimension2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class TreeViewPrinter implements PrintRenderer {
  
  private TreeView view;
  
  
  public TreeViewPrinter(TreeView view) {
    this.view = view;
  }
  
  
  public Dimension2D getSize() {
    Rectangle mmbounds = view.getModel().getBounds();
    return new Dimension2D.Double(mmbounds.width*0.1F/2.54F, mmbounds.height*0.1F/2.54F);
  }

  
  public void render(Graphics2D g) {
    
    
    DPI dpi = DPI.get(g);
    UnitGraphics graphics = new UnitGraphics(g, dpi.horizontal()/DPI.INCH*0.1D, dpi.vertical()/DPI.INCH*0.1D);
    
    ContentRenderer renderer = new ContentRenderer();
    renderer.font           = view.getContentFont();
    renderer.cArcs          = Color.black;
    renderer.cFamShape      = Color.black;
    renderer.cIndiShape     = Color.black;
    renderer.indiRenderer   = new BlueprintRenderer(view.getBlueprint(Gedcom.INDI));
    renderer.famRenderer    = new BlueprintRenderer(view.getBlueprint(Gedcom.FAM));
    
    renderer.render(graphics, view.getModel());

  }

} 
