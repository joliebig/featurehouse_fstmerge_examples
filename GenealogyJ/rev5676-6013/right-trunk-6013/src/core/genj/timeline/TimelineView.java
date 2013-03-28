
package genj.timeline;

import genj.almanac.Almanac;
import genj.almanac.Event;
import genj.gedcom.Context;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.Property;
import genj.gedcom.TagPath;
import genj.gedcom.time.PointInTime;
import genj.renderer.DPI;
import genj.renderer.Options;
import genj.renderer.RenderSelectionHintKey;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.WordBuffer;
import genj.util.swing.SliderWidget;
import genj.util.swing.UnitGraphics;
import genj.util.swing.ViewPortAdapter;
import genj.view.ContextProvider;
import genj.view.ScreenshotAction;
import genj.view.SelectionSink;
import genj.view.SettingsAction;
import genj.view.ToolBar;
import genj.view.View;
import genj.view.ViewContext;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;




public class TimelineView extends View {

  
  private final DPI DPI;
  private final Point2D DPC;
  
  
  private Resources resources = Resources.get(this);
  
  
   Map<String,Color> colors = new HashMap<String, Color>();
    
  
  private Model model;
  
  
  private Content content;
  
  
  private Set<Model.Event> selection = new HashSet<Model.Event>();
  
  
  private Ruler ruler;

    
  private SliderWidget sliderCmPerYear;
  
  
  private JScrollPane scrollContent;

  
  private RulerRenderer rulerRenderer = new RulerRenderer();
  
  
  private ContentRenderer contentRenderer = new ContentRenderer();
  
  
  private List<String> ignoredAlmanacCategories = new ArrayList<String>();
  
  
   final static double 
    MIN_CM_PER_YEAR =  0.1D,
    DEF_CM_PER_YEAR =  1.0D,
    MAX_CM_PER_YEAR = 10.0D,
    
    MIN_CM_BEF_EVENT = 0.1D,
    DEF_CM_BEF_EVENT = 0.5D,
    MAX_CM_BEF_EVENT = 2.0D,

    MIN_CM_AFT_EVENT = 2.0D,
    DEF_CM_AFT_EVENT = 2.0D,
    MAX_CM_AFT_EVENT = 9.0D;
    
  
  private double 
    cmPerYear = DEF_CM_PER_YEAR,
    cmBefEvent = DEF_CM_BEF_EVENT,
    cmAftEvent = DEF_CM_AFT_EVENT;
    
  
  private double centeredYear = 0;
  
  
  private boolean 
    isPaintDates = true,
    isPaintGrid = false,
    isPaintTags = true;

  
  private final static Registry REGISTRY = Registry.get(TimelineView.class);
  
  private ModelListener callback = new ModelListener();
    
  
  public TimelineView() {
    
    
    DPI = Options.getInstance().getDPI();
    DPC = new Point2D.Float(
      DPI.horizontal() / 2.54F,
      DPI.vertical() / 2.54F
    );

    
    cmPerYear = Math.max(MIN_CM_PER_YEAR, Math.min(MAX_CM_PER_YEAR, REGISTRY.get("cmperyear", (float)DEF_CM_PER_YEAR)));
    cmBefEvent = Math.max(MIN_CM_BEF_EVENT, Math.min(MAX_CM_BEF_EVENT, REGISTRY.get("cmbefevent", (float)DEF_CM_BEF_EVENT)));
    cmAftEvent = Math.max(MIN_CM_AFT_EVENT, Math.min(MAX_CM_AFT_EVENT, REGISTRY.get("cmaftevent", (float)DEF_CM_AFT_EVENT)));
    isPaintDates = REGISTRY.get("paintdates", true);
    isPaintGrid  = REGISTRY.get("paintgrid" , false);
    isPaintTags  = REGISTRY.get("painttags" , false);

    colors.put("background", Color.WHITE);
    colors.put("text"      , Color.BLACK);
    colors.put("tag"       , Color.GREEN);
    colors.put("date"      , Color.GRAY );
    colors.put("timespan"  , Color.BLUE );
    colors.put("grid"      , Color.LIGHT_GRAY);
    colors.put("selected"  , Color.RED  );
    colors = REGISTRY.get("color", colors);
   
    String[] ignored= REGISTRY.get("almanac.ignore", new String[0]);
    for (int i=0;i<ignored.length;i++)
      ignoredAlmanacCategories.add(ignored[i]);
    
    
    model = new Model();
    model.setTimePerEvent(cmBefEvent/cmPerYear, cmAftEvent/cmPerYear);
    
    String[] ps = REGISTRY.get("paths", (String[])null);
    if (ps!=null) {
      List<TagPath> paths = new ArrayList<TagPath>(ps.length);
      for (String p : ps) try {
        paths.add(new TagPath(p));
      } catch (Throwable t) {}
      model.setPaths(paths);
    }
    
    content = new Content();
    ruler = new Ruler();
    
    
    scrollContent = new JScrollPane(new ViewPortAdapter(content));
    scrollContent.setColumnHeaderView(new ViewPortAdapter(ruler));
    scrollContent.getHorizontalScrollBar().addAdjustmentListener(new ChangeCenteredYear());
   
    
    setLayout(new BorderLayout());
    add(scrollContent, BorderLayout.CENTER);
    
    
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        centeredYear = REGISTRY.get("centeryear", 0F);
        scroll2year(centeredYear);
      }
    });
    
    
  }

  public void addNotify() {
    
    super.addNotify();
    
    model.addListener(callback);
  }
  
  
  public void removeNotify() {
    
    
    model.removeListener(callback);
    
    
    REGISTRY.put("cmperyear"  , (float)Math.rint(cmPerYear*10)/10);
    REGISTRY.put("cmbefevent" , (float)cmBefEvent);
    REGISTRY.put("cmaftevent" , (float)cmAftEvent);
    REGISTRY.put("paintdates" , isPaintDates);
    REGISTRY.put("paintgrid"  , isPaintGrid);
    REGISTRY.put("painttags"  , isPaintTags);
    REGISTRY.put("filter"     , model.getPaths());
    REGISTRY.put("centeryear" , (float)centeredYear);
    REGISTRY.put("color", colors);
    REGISTRY.put("paths", model.getPaths());
    
    String[] ignored = new String[ignoredAlmanacCategories.size()];
    for (int i=0;i<ignored.length;i++)
      ignored[i] = ignoredAlmanacCategories.get(i).toString();
    REGISTRY.put("almanac.ignore", ignored);

    
    super.removeNotify();
  }
  
  
  public Dimension getPreferredSize() {
    return new Dimension(480,256);
  }
  
  
  public Model getModel() {
    return model;
  }
  
  
  public Set<String> getAlmanacCategories() {
    HashSet<String> result = new HashSet<String>(Almanac.getInstance().getCategories());
    result.removeAll(ignoredAlmanacCategories);
    return result;
  }
  
  
  public void setAlmanacCategories(Set<String> set) {
    ignoredAlmanacCategories.clear();
    ignoredAlmanacCategories.addAll(Almanac.getInstance().getCategories());
    ignoredAlmanacCategories.removeAll(set);
    repaint();
  }
  
  
  public boolean isPaintTags() {
    return isPaintTags;
  }

  
  public void setPaintTags(boolean set) {
    isPaintTags = set;
    repaint();
  }

  
  public boolean isPaintDates() {
    return isPaintDates;
  }

  
  public void setPaintDates(boolean set) {
    isPaintDates = set;
    repaint();
  }

  
  public boolean isPaintGrid() {
    return isPaintGrid;
  }

  
  public void setPaintGrid(boolean set) {
    isPaintGrid = set;
    repaint();
  }

  
  public void setCMPerEvents(double before, double after) {
    
    cmBefEvent = before;
    cmAftEvent = after;
    
    model.setTimePerEvent(cmBefEvent/cmPerYear, cmAftEvent/cmPerYear);
  }
  
  
  public double getCmBeforeEvents() {
    return cmBefEvent;
  }
  
  
  public double getCmAfterEvents() {
    return cmAftEvent;
  }
  
  
  public void populate(ToolBar toolbar) {

    
    int value = (int)(
      Math.log( (cmPerYear-MIN_CM_PER_YEAR) / (MAX_CM_PER_YEAR-MIN_CM_PER_YEAR) * Math.exp(10) ) * 10
    );

    sliderCmPerYear = new SliderWidget(1, 100, Math.min(100, Math.max(1,value)));
    sliderCmPerYear.setToolTipText(resources.getString("view.peryear.tip"));
    sliderCmPerYear.addChangeListener(new ChangeCmPerYear());
    sliderCmPerYear.setOpaque(false);

    toolbar.add(sliderCmPerYear);
    
    toolbar.add(new Settings());
    toolbar.add(new ScreenshotAction(content));
    
  }

  
  public void setContext(Context context, boolean isActionPerformed) {

    if (context==null) {
      model.setGedcom(null);
      selection.clear();
    } else {
      model.setGedcom(context.getGedcom());
      selection = model.getEvents(context);
    }
    
    
    content.repaint();
      
    
  }

  
  protected Model.Event getEventAt(Point pos) {
    double year = pixel2year(pos.x);
    int layer = pos.y/(getFontMetrics(getFont()).getHeight()+1);
    return model.getEvent(year, layer);
  }
  
  
  protected double pixel2year(int x) {
    return model.min + x/(DPC.getX()*cmPerYear);
  }

  
  protected void scroll2year(double year) {
    centeredYear = year;
    int x = (int)((year-model.min)*DPC.getX()*cmPerYear) - scrollContent.getViewport().getWidth()/2;
    scrollContent.getHorizontalScrollBar().setValue(x);
  }
  
  
  protected void makeVisible(Model.Event event) {
    double 
      min = model.min + scrollContent.getHorizontalScrollBar().getValue()/DPC.getX()/cmPerYear,
      max = min + scrollContent.getViewport().getWidth()/DPC.getX()/cmPerYear;

    if (event.to>max || event.from<min)      
      scroll2year(event.from);
  }
    
  
  private class Ruler extends JComponent implements MouseMotionListener, ChangeListener {
    
    
    public void addNotify() {
      
      super.addNotify();
      
      addMouseMotionListener(this);
      Almanac.getInstance().addChangeListener(this);
      
      
      ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
    }
    
    
    public void removeNotify() {
      
      removeMouseMotionListener(this);
      Almanac.getInstance().removeChangeListener(this);
      
      super.removeNotify();
    }
    
    
    public void stateChanged(ChangeEvent e) {
      repaint();
    }
    
    
    protected void paintComponent(Graphics g) {
      
      rulerRenderer.cBackground = (Color)colors.get("background");
      rulerRenderer.cText = (Color)colors.get("text");
      rulerRenderer.cTick = rulerRenderer.cText;
      rulerRenderer.cTimespan = (Color)colors.get("timespan");
      rulerRenderer.acats = getAlmanacCategories();
      
      UnitGraphics graphics = new UnitGraphics(
        g,
        DPC.getX()*cmPerYear, 
        getFontMetrics(getFont()).getHeight()+1
      );
      graphics.translate(-model.min,0);
      
      rulerRenderer.render(graphics, model);
      
    }
  
    
    public Dimension getPreferredSize() {
      return new Dimension(
        content.getPreferredSize().width,
        getFontMetrics(getFont()).getHeight()+1
      );
    }
    
    
    public void mouseDragged(MouseEvent e) {
    }

    
    public void mouseMoved(MouseEvent e) {
      
      double year = pixel2year(e.getPoint().x);
      
      PointInTime when = Model.toPointInTime(year);
      int days = (int)Math.ceil(5F/DPC.getX()/cmPerYear*365);
      
      WordBuffer text = new WordBuffer();
      int cursor = Cursor.DEFAULT_CURSOR;
      try {
	      Iterator<Event> almanac = Almanac.getInstance().getEvents(when, days, getAlmanacCategories());
	      if (almanac.hasNext()) {
		      text.append("<html><body>");
		      for (int i=0;i<10&&almanac.hasNext();i++) {
		        text.append("<div width=\""+TimelineView.this.getWidth()/2+"\">");
		        text.append(almanac.next());
		        text.append("</div>");
		      }
		      text.append("</body></html>");
          cursor = Cursor.TEXT_CURSOR;
	      }
      } catch (GedcomException ex) {
      }
      
      setCursor(Cursor.getPredefinedCursor(cursor));
      setToolTipText(text.length()==0 ? null : text.toString());
      
    }
    
  } 

  
  private class Content extends JComponent implements MouseListener, ContextProvider {
    
    
    private Content() {
      addMouseListener(this);
    }

    
    public ViewContext getContext() {
      
      
      Gedcom gedcom = model.getGedcom();
      if (gedcom==null)
        return null;
      
      List<Property> props = new ArrayList<Property>();
      for (Model.Event event : selection) 
        props.add(event.pe);
      
      return new ViewContext(gedcom, null, props);
    }
    
    
    public Dimension getPreferredSize() {
      return new Dimension(
        (int)((model.max-model.min) * DPC.getX()*cmPerYear),
         model.layers.size()  * (getFontMetrics(getFont()).getHeight()+1)
      );
    }
  
    
    protected void paintComponent(Graphics g) {
      
      
      Boolean rsel = (Boolean) ((Graphics2D)g).getRenderingHint(RenderSelectionHintKey.KEY);
      if (rsel==null)
        rsel = true;
      
      
      contentRenderer.selection = rsel ? selection : Collections.<Model.Event>emptySet();
      contentRenderer.cBackground = (Color)colors.get("background" );
      contentRenderer.cText       = (Color)colors.get("text"    );
      contentRenderer.cDate       = (Color)colors.get("date"    );
      contentRenderer.cTag        = (Color)colors.get("tag"     );
      contentRenderer.cTimespan   = (Color)colors.get("timespan");
      contentRenderer.cGrid       = (Color)colors.get("grid"    );
      contentRenderer.cSelected   = (Color)colors.get("selected");
      contentRenderer.paintDates = isPaintDates;
      contentRenderer.paintGrid = isPaintGrid;
      contentRenderer.paintTags = isPaintTags;
      
      
      UnitGraphics graphics = new UnitGraphics(
        g,
        DPC.getX()*cmPerYear, 
        getFontMetrics(getFont()).getHeight()+1
      );
      graphics.translate(-model.min,0);

      
      contentRenderer.render(graphics, model);
      
      
    }
    
    public void mouseClicked(MouseEvent e) {
      
      
      if (e.getButton()!=MouseEvent.BUTTON1)
        return;
      
      if (!e.isShiftDown())
        selection.clear();
      
      
      Model.Event hit = getEventAt(e.getPoint());
      if (hit!=null) {
        selection.add(hit);
        
        
        SelectionSink.Dispatcher.fireSelection(e, getContext());
      }
      
      
      repaint();
    }
    public void mouseEntered(MouseEvent e) {
    }
    public void mouseExited(MouseEvent e) {
    }
    public void mouseReleased(MouseEvent e) {
    }
    public void mousePressed(MouseEvent e) {
    }
  } 
  
  
  private class ChangeCenteredYear implements AdjustmentListener {
    private boolean mute = false;
    
    public void adjustmentValueChanged(AdjustmentEvent e) {
      
      
      
      if (scrollContent.getHorizontalScrollBar().getValueIsAdjusting()) {
        
        int x = scrollContent.getHorizontalScrollBar().getValue() + scrollContent.getViewport().getWidth()/2;
        centeredYear = pixel2year(x);
      } else {
        
        
        if (!mute) try {
          mute = true;
          scroll2year(centeredYear);
        } finally {
          mute = false;
        }
      }
    }
  } 
  
  
  private class ChangeCmPerYear implements ChangeListener {
    
    public void stateChanged(ChangeEvent e) {
      
      cmPerYear = MIN_CM_PER_YEAR + 
         Math.exp(sliderCmPerYear.getValue()*0.1)/Math.exp(10) * (MAX_CM_PER_YEAR-MIN_CM_PER_YEAR);
      
      model.setTimePerEvent(cmBefEvent/cmPerYear, cmAftEvent/cmPerYear);
      
    }
  } 
    
  
  private class ModelListener implements Model.Listener {
    
    public void dataChanged() {
      repaint();
    }
    
    public void structureChanged() {
      ruler.revalidate();
      content.revalidate();
      repaint();
    }
  } 
  
  private class Settings extends SettingsAction {

    @Override
    protected TimelineViewSettings getEditor() {
      return new TimelineViewSettings(TimelineView.this);
    }
    
  }

} 
