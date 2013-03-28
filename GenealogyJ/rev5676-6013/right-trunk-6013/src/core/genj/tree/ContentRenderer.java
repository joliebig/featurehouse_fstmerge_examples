
package genj.tree;

import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Indi;
import genj.renderer.EmptyHintKey;
import genj.renderer.EntityRenderer;
import genj.renderer.RenderPreviewHintKey;
import genj.util.swing.UnitGraphics;
import gj.model.Node;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;


public class ContentRenderer {
  
   Font font = null;

  
   Color cIndiShape = null;
  
  
   Color cFamShape = null;
  
  
   Color cArcs = null;

  
   Color cSelectedShape = null;

  
   Collection<? extends Entity> selected = new ArrayList<Entity>(0);
  
  
   EntityRenderer indiRenderer, famRenderer;
  
  
  public void render(UnitGraphics g, Model model) {  

    
    Rectangle bounds = model.getBounds();
    g.translate(-bounds.getX(), -bounds.getY());
    
    renderArcs(g, model);
    
    renderNodes(g, model);
    
  }  
  
  
  private void renderNodes(UnitGraphics g, Model model) {
    
    
    Rectangle clip = g.getClip().getBounds();
    
    
    int count = 0;
    for (Node node : model.getNodesIn(clip)) {
      
      Shape shape = node.getShape();
      Point2D pos = node.getPosition();
      
      if (shape==null) continue;
      
      Rectangle r = shape.getBounds();
      if (!clip.intersects(
        pos.getX()+r.getMinX(), 
        pos.getY()+r.getMinY(),
        r.getWidth(),
        r.getHeight() 
      )) continue;
      
      count++;
      renderNode(g, pos, shape, node.getContent());
      
    }
    if (count>0)
      g.getGraphics().setRenderingHint(EmptyHintKey.KEY, false);
    
    
  }
  
  
  private void renderNode(UnitGraphics g, Point2D pos, Shape shape, Object content) {
    
    double 
      x = pos.getX(),
      y = pos.getY();
    
    g.setColor(getColor(content));
    g.draw(shape, x, y);
    
    if (!Boolean.TRUE.equals(g.getGraphics().getRenderingHint(RenderPreviewHintKey.KEY)))
      renderContent(g, x, y, shape, content);
    
  }
  
    private Color getColor(Object content) {
    
    if (cSelectedShape!=null&&selected.contains(content)) {
      return cSelectedShape;
    }
    
    if (content instanceof Fam)
      return cFamShape;
    
    return cIndiShape;
  }
  
  
  private void renderContent(UnitGraphics g, double x, double y, Shape shape, Object content) {
    
    
    EntityRenderer renderer = null;
    if (content instanceof Indi) renderer = indiRenderer;
    if (content instanceof Fam ) renderer = famRenderer;
    if (renderer==null) 
      return;
    
    Rectangle r2d = shape.getBounds();
    g.pushClip(x, y, r2d);
    g.pushTransformation();
    
    g.translate(x, y);
    Rectangle r = g.getRectangle(r2d);
    r.x+=2;r.y+=2;r.width-=4;r.height-=4;
    g.setColor(Color.black);
    g.setFont(font);
    renderer.render(g.getGraphics(), (Entity)content, r);
    
    g.popTransformation();    
    g.popClip();
    
  }
  
  
  private void renderArcs(UnitGraphics g, Model model) {
    
    Rectangle clip = g.getClip().getBounds();
    
    g.setColor(cArcs);
    
    Collection<TreeArc> arcs = model.getArcsIn(clip);
    for (TreeArc arc : arcs) 
      g.draw(arc.getPath(), 0, 0);
    if (!arcs.isEmpty())
      g.getGraphics().setRenderingHint(EmptyHintKey.KEY, false);

    
  }
  
} 
