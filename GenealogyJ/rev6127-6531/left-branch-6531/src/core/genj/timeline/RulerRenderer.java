
package genj.timeline;

import genj.almanac.Almanac;
import genj.almanac.Event;
import genj.gedcom.GedcomException;
import genj.gedcom.time.PointInTime;
import genj.util.swing.UnitGraphics;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Set;


public class RulerRenderer extends ContentRenderer {
  
  
   Color cTick = null;
  
  
   Set acats = null;
  
  
  private Shape tickMark, eventMark;
  
  
  public void render(UnitGraphics graphics, Model model) {
    
    
    init(graphics);
    
    
    FontMetrics fm = graphics.getFontMetrics();
    double
      from  = Math.ceil(model.min),
      to    = Math.floor(model.max),
      width = fm.stringWidth(" 0000 ") * dotSize.x;

    
    renderBackground(graphics, model);

    
    renderYear(graphics, model, fm, from, 0.0D);
    renderYear(graphics, model, fm, to  , 1.0D);
    
    from += width;
    to += -width;
    
    
    renderSpan(graphics, model, fm, from, to, width);
    
    
    renderAlmanac(graphics);
    
    
  }
  
  
  private void renderAlmanac(UnitGraphics g) {
    
    
    g.setColor(cTimespan);
    Rectangle2D clip = g.getClip();
    PointInTime 
      from = Model.toPointInTime(clip.getX()),
      to   = Model.toPointInTime(clip.getMaxX());

    
    double timePerPixel = dotSize.x;
    
    
    Iterator almanac = Almanac.getInstance().getEvents(from, to, acats);
    double last = 0;
    while (almanac.hasNext()) {
      
      Event event = (Event)almanac.next();
      try {
        
        PointInTime time = event.getTime();
        double year = Model.toDouble(time, false);
        
        
        
        if (year-last>=timePerPixel) {
          g.draw(eventMark, year, 0, false);
          last = year;
        }
      } catch (GedcomException e) {
      }
      
    }
  }
  
  
  private void renderSpan(UnitGraphics g, Model model, FontMetrics fm, double from, double to, double width) {

    
    if (to-from<width||to-from<1) return;

    
    Rectangle2D clip = g.getClip();
    if (!clip.intersects(from, 0, to-from, 1)) return;
    
    
    double year = Math.rint((from+to)/2);

    
    if (year-from<width/2||to-year<width/2) {
      return;
    }

    
    renderYear(g, model, fm, year, 0.5D);
    
    
    renderSpan(g, model, fm, year+width/2, to         , width);
    renderSpan(g, model, fm, from       , year-width/2, width);
    
    
  }
  
  
  private void renderYear(UnitGraphics g, Model model,  FontMetrics fm, double year, double align) {
    
    g.setColor(cTick);
    g.draw(tickMark, year, 1, true);
    
    
    g.setColor(cText);
    g.draw(Integer.toString((int)year), year, 1, align, 1.0);
    
    
  }

  
  protected void init(UnitGraphics graphics) {
    super.init(graphics);
    
    GeneralPath gp = new GeneralPath();
    gp.moveTo( (float)( 0F*dotSize.x), (float)( 0F*dotSize.y) );
    gp.lineTo( (float)( 3F*dotSize.x), (float)(-3F*dotSize.y) );
    gp.lineTo( (float)(-3F*dotSize.x), (float)(-3F*dotSize.y) );
    gp.closePath();
    
    tickMark = gp;
    eventMark = new Line2D.Double(0,0,0,5F*dotSize.y);
  }
  
} 
