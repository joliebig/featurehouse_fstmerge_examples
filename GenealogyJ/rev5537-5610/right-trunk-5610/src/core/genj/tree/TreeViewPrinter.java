
package genj.tree;

import genj.gedcom.Gedcom;
import genj.print.Printer;
import genj.util.swing.UnitGraphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

public class TreeViewPrinter implements Printer {
  
  
  private TreeView tree;
  
    public void setView(JComponent view) {
    tree = (TreeView)view;
  }


  
  public Dimension calcSize(Dimension2D pageSizeInInches, Point dpi) {
    Rectangle mmbounds = tree.getModel().getBounds();
    return new Dimension(
      (int)Math.ceil(mmbounds.width*0.1F/2.54F / pageSizeInInches.getWidth()), 
      (int)Math.ceil(mmbounds.height*0.1F/2.54F  / pageSizeInInches.getHeight())
    );
  }

  
  public void renderPage(Graphics2D g, Point page, Dimension2D pageSizeInInches, Point dpi, boolean preview) {

    
    UnitGraphics ug = new UnitGraphics(g, dpi.x, dpi.y);
    ug.setColor(Color.LIGHT_GRAY);
    ug.draw(new Rectangle2D.Double(0,0,pageSizeInInches.getWidth(),pageSizeInInches.getHeight()),0,0);
    ug.translate(
      -page.x*pageSizeInInches.getWidth(), 
      -page.y*pageSizeInInches.getHeight()
    );

    
    UnitGraphics graphics = new UnitGraphics(g, dpi.x/2.54F*0.1D, dpi.y/2.54F*0.1D);
    
    ContentRenderer renderer = new ContentRenderer();
    renderer.cArcs          = Color.black;
    renderer.cFamShape      = Color.black;
    renderer.cIndiShape     = Color.black;
    renderer.selection      = null;

    if (!preview) {    
      renderer.indiRenderer   = tree.createEntityRenderer(Gedcom.INDI).setResolution(dpi);
      renderer.famRenderer    =  tree.createEntityRenderer(Gedcom.FAM).setResolution(dpi);
    }
    
    renderer.render(graphics, tree.getModel());

  }

} 
