
package genj.timeline;

import genj.gedcom.Gedcom;
import genj.gedcom.PropertyDate;
import genj.renderer.Options;
import genj.util.swing.ImageIcon;
import genj.util.swing.UnitGraphics;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class ContentRenderer {
  
  
  protected Point2D.Double dotSize = new Point2D.Double();
  
  
  protected GeneralPath fromMark, toMark; 
  
  
   boolean paintTags = false;
  
  
   boolean paintDates = true;
  
  
   boolean paintGrid = false;
  
  
   Set<Model.Event> selection = new HashSet<Model.Event>();
  
  
   Color cBackground = null;
  
  
   Color cText = null;
  
  
   Color cTag = null;
  
  
   Color cDate = null;
  
  
   Color cTimespan= null;
  
  
   Color cGrid = null;
  
  
   Color cSelected = null;
  
  
  public void render(UnitGraphics graphics, Model model) {
    
    init(graphics);
    
    renderBackground(graphics, model);
    
    renderGrid(graphics, model);
    
    renderLayers(graphics, model);
    
  }
  
  
  protected void renderBackground(UnitGraphics g, Model model) {
    if (cBackground==null) return;
    g.setColor(cBackground);
    Rectangle2D r = new Rectangle2D.Double(model.min, 0, model.max-model.min, 1024);
    g.draw(r, 0, 0, true);
  }
  
  
  private final void renderGrid(UnitGraphics g, Model model) {
    
    if (!paintGrid) return;
    
    g.setColor(cGrid);
    
    Rectangle2D r = g.getClip();
    int layers = model.layers.size();
    double 
      from = Math.floor(r.getMinX()),
      to = Math.ceil(r.getMaxX());
    for (double year=from;year<=to;year++) {
      g.draw(year, 0, year, layers);
    }
    
  }

  
  private final void renderLayers(UnitGraphics g, Model model) {
    
    Rectangle2D clip = g.getClip();
    
    List layers = model.layers;
    for (int l=0; l<layers.size(); l++) {
      if (l<Math.floor(clip.getMinY())||l>Math.ceil(clip.getMaxY())) continue;
      List layer = (List)layers.get(l);
      renderEvents(g, model, layer, l);
    }
    
  }
  
  
  private final void renderEvents(UnitGraphics g, Model model, List layer, int level) {
    
    Rectangle2D clip = g.getClip();
    
    Iterator events = layer.iterator();
    Model.Event event = (Model.Event)events.next();
    while (true) {
      
      Model.Event next = events.hasNext() ? (Model.Event)events.next() : null;
      
      if ((next==null||next.from>clip.getMinX())&&(event.from<clip.getMaxX())) {
        renderEvent(g, model, event, next, level);
      }
      
      if (next==null) break;
      
      event = next;
    } 
    
  }
  
  
  private final void renderEvent(UnitGraphics g, Model model, Model.Event event, Model.Event next, int level) {

    
    boolean em  = selection.contains(event); 
    FontMetrics fm = g.getFontMetrics();
       
    
    g.setColor(cTimespan);
    
    PropertyDate.Format format = event.pd.getFormat();
    if (format==PropertyDate.AFTER||format==PropertyDate.FROM) {
      g.draw(fromMark, event.from, level+1, true);
    } else if (format==PropertyDate.BEFORE||format==PropertyDate.TO) {
      g.draw(toMark, event.from, level+1, true);
    } else {
      g.draw(fromMark, event.from, level+1, true);
      g.draw( event.from, level + 1 - dotSize.y, event.to, level + 1 - dotSize.y );
      g.draw(toMark, event.to, level+1, true);
    }

    
    g.pushClip(event.from, level, next==null?Integer.MAX_VALUE:next.from, level+1);

    int dx = 0;
    
    
    if (!paintTags) {
      ImageIcon img = event.pe.getImage(false);
      g.draw(img, event.from, level+0.5, 0, 0.5);
      dx+=img.getIconWidth() + 2;
    }
    
    
    if (paintTags) {
      String tag = Gedcom.getName(event.pe.getTag());
      g.setColor(cTag);
      g.draw(tag, event.from, level+1, 0, 1, dx, 0);
      dx+=fm.stringWidth(tag)+fm.charWidth(' ');
    }

    
    g.setFont(Options.getInstance().getDefaultFont());
    g.setColor(em ? cSelected : cText);
    String txt = event.content;
    g.draw(txt, event.from, level+1, 0, 1, dx, 0);
    dx+=fm.stringWidth(txt)+fm.charWidth(' ');
    
    
    if (paintDates) {
      String date = " (" + event.pd.getDisplayValue() + ')';
      g.setColor(cDate);
      g.draw(date, event.from, level+1, 0, 1, dx, 0);
    }

    
    g.popClip();

    
  }
  
    protected void init(UnitGraphics graphics) {
    
    
    dotSize.setLocation( 1D / graphics.getUnit().getX(), 1D / graphics.getUnit().getY() );

    
    fromMark = new GeneralPath();
    fromMark.moveTo((float)(3F*dotSize.x),(float)(-1F*dotSize.y));
    fromMark.lineTo((float)(-1F*dotSize.x),(float)(-5F*dotSize.y));
    fromMark.lineTo((float)(-1F*dotSize.x),(float)(+3F*dotSize.y));
    fromMark.closePath();

    
    toMark = new GeneralPath();
    toMark  .moveTo((float)(-3F*dotSize.x),(float)(-1F*dotSize.y));
    toMark  .lineTo((float)( 1F*dotSize.x),(float)(-6F*dotSize.y));
    toMark  .lineTo((float)( 1F*dotSize.x),(float)(+4F*dotSize.y));
    toMark  .closePath();
    
    
  }
  
} 
