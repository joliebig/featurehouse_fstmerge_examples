
package genj.tree;

import genj.common.SelectEntityWidget;
import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.io.Filter;
import genj.renderer.Blueprint;
import genj.renderer.BlueprintManager;
import genj.renderer.EntityRenderer;
import genj.renderer.Options;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.ButtonHelper;
import genj.util.swing.ImageIcon;
import genj.util.swing.PopupWidget;
import genj.util.swing.SliderWidget;
import genj.util.swing.UnitGraphics;
import genj.util.swing.ViewPortAdapter;
import genj.util.swing.ViewPortOverview;
import genj.view.ActionProvider;
import genj.view.ContextProvider;
import genj.view.SelectionSink;
import genj.view.ToolBar;
import genj.view.View;
import genj.view.ViewContext;
import genj.window.WindowManager;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class TreeView extends View implements ContextProvider, ActionProvider, Filter {
  
  protected final static ImageIcon BOOKMARK_ICON = new ImageIcon(TreeView.class, "images/Bookmark");      
  protected final static Registry REGISTRY = Registry.get(TreeView.class);
  protected final static Resources RESOURCES = Resources.get(TreeView.class);
  
  
  private final Point DPI;
  private final Point2D DPMM;
  
  
  private Model model;
  
  
  private Content content;
  
  
  private Overview overview;
  
  
  private ContentRenderer contentRenderer;
  
  
  private double zoom = 1.0D;

    
  private SliderWidget sliderZoom;  
  
  
  private String title;
  
  
  private boolean isAntialiasing = false;
  
  
  private boolean isAdjustFonts = false; 
  
  
   Map colors = new HashMap();
  
  
  private Map<String,String> tag2blueprint = new HashMap<String,String>();
  
  
  private Map tag2renderer = new HashMap();
  
  
  private Font contentFont = new Font("SansSerif", 0, 12);
  
  
  private Point2D.Double center = new Point2D.Double(0,0);
  
  
  private Context context = new Context();
  
  
  public TreeView() {
    
    
    DPI = Options.getInstance().getDPI();
    DPMM = new Point2D.Float(
      DPI.x / 2.54F / 10,
      DPI.y / 2.54F / 10
    );
    
    
    colors.put("background", Color.WHITE);
    colors.put("indis"     , Color.BLACK);
    colors.put("fams"      , Color.DARK_GRAY);
    colors.put("arcs"      , Color.BLUE);
    colors.put("selects"   , Color.RED);
    colors = REGISTRY.get("color", colors);
    
    
    contentFont = REGISTRY.get("font", contentFont);
    isAdjustFonts = REGISTRY.get("adjust", isAdjustFonts);
    
    
    BlueprintManager bpm = BlueprintManager.getInstance();
    for (int t=0;t<Gedcom.ENTITIES.length;t++) {
      String tag = Gedcom.ENTITIES[t];
      tag2blueprint.put(tag, REGISTRY.get("blueprint."+tag, ""));
    }
    
    
    model = new Model();
    model.setVertical(REGISTRY.get("vertical",true));
    model.setFamilies(REGISTRY.get("families",true));
    model.setBendArcs(REGISTRY.get("bend"    ,true));
    model.setMarrSymbols(REGISTRY.get("marrs",true));
    TreeMetrics defm = model.getMetrics();
    model.setMetrics(new TreeMetrics(
      REGISTRY.get("windis",defm.wIndis),
      REGISTRY.get("hindis",defm.hIndis),
      REGISTRY.get("wfams" ,defm.wFams ),
      REGISTRY.get("hfams" ,defm.hFams ),
      REGISTRY.get("pad"   ,defm.pad   )
    ));
    isAntialiasing = REGISTRY.get("antial", false);
    model.setHideAncestorsIDs(REGISTRY.get("hide.ancestors", new ArrayList()));
    model.setHideDescendantsIDs(REGISTRY.get("hide.descendants", new ArrayList()));
 











    











    
    contentRenderer = new ContentRenderer();
    content = new Content();
    JScrollPane scroll = new JScrollPane(new ViewPortAdapter(content));
    overview = new Overview(scroll);
    overview.setVisible(REGISTRY.get("overview", false));
    overview.setSize(REGISTRY.get("overview", new Dimension(64,64)));
    zoom = Math.max(0.1, Math.min(1.0, REGISTRY.get("zoom", 1.0F)));
    
    
    add(overview);
    add(scroll);
    






    
    
  }
  
  
  public void removeNotify() {
    
    REGISTRY.put("overview", overview.isVisible());
    REGISTRY.put("overview", overview.getSize());
    REGISTRY.put("zoom", (float)zoom);
    REGISTRY.put("vertical", model.isVertical());
    REGISTRY.put("families", model.isFamilies());
    REGISTRY.put("bend"    , model.isBendArcs());
    REGISTRY.put("marrs"   , model.isMarrSymbols());
    TreeMetrics m = model.getMetrics();
    REGISTRY.put("windis"  , m.wIndis);
    REGISTRY.put("hindis"  , m.hIndis);
    REGISTRY.put("wfams"   , m.wFams );
    REGISTRY.put("hfams"   , m.hFams );
    REGISTRY.put("pad"     , m.pad   );
    REGISTRY.put("antial"  , isAntialiasing );
    REGISTRY.put("font"    , contentFont);
    REGISTRY.put("adjust"  , isAdjustFonts);
    REGISTRY.put("color", colors);
    
    for (int t=0;t<Gedcom.ENTITIES.length;t++) {
      String tag = Gedcom.ENTITIES[t];
      REGISTRY.put("blueprint."+tag, getBlueprint(tag).getName()); 
    }
    
    
    if (model.getRoot()!=null) 
      REGISTRY.put("root", model.getRoot().getId());
    
    
    String[] bs = new String[model.getBookmarks().size()];
    Iterator it = model.getBookmarks().iterator();
    for (int b=0;it.hasNext();b++) {
      bs[b] = it.next().toString();
    }
    REGISTRY.put("bookmarks", bs);
    
    
    REGISTRY.put("hide.ancestors"  , model.getHideAncestorsIDs());
    REGISTRY.put("hide.descendants", model.getHideDescendantsIDs());
    
    
    super.removeNotify();
  }
  
  
  public ViewContext getContext() {
    return new ViewContext(context);
  }
  
  
  public void doLayout() {
    
    int 
      w = getWidth(),
      h = getHeight();
    Component[] cs = getComponents();
    for (int c=0; c<cs.length; c++) {
      if (cs[c]==overview) continue;
      cs[c].setBounds(0,0,w,h);
    }
    
  }

  
  public Dimension getPreferredSize() {
    return new Dimension(480,480);
  }

  
  public boolean isOptimizedDrawingEnabled() {
    return !overview.isVisible();
  }

  
  public boolean isAntialising() {
    return isAntialiasing;
  }

  
  public void setAntialiasing(boolean set) {
    if (isAntialiasing==set) return;
    isAntialiasing = set;
    repaint();
  }
  
  
  public boolean isAdjustFonts() {
    return isAdjustFonts;
  }

  
  public void setAdjustFonts(boolean set) {
    if (isAdjustFonts==set) return;
    isAdjustFonts = set;
    
    tag2renderer.clear();
    
    repaint();
  }
  
  
  public Font getContentFont() {
    return contentFont;
  }

  
  public void setContentFont(Font set) {
    
    if (contentFont.equals(set)) return;
    
    contentFont = set;
    
    tag2renderer.clear();
    
    repaint();
  }

  
   Map getBlueprints() {
    return tag2blueprint;
  }

  
   void setBlueprints(Map set) {
    
    tag2blueprint = set;
    tag2renderer.clear();
    
    repaint();
    
  }

  
  public Model getModel() {
    return model;
  }

  
  @Override
  public void setContext(Context newContext, boolean isActionPerformed) {
    
    
    context = new Context(newContext.getGedcom(), newContext.getEntities());
    
    
    if (isActionPerformed)
      setRoot(context.getEntity());
    
    
    repaint();
    





    
    
  }
  
  
   void show(Entity entity) {
    
    
    if (!(entity instanceof Indi||entity instanceof Fam)) 
      return;
    
    
    TreeNode node = model.getNode(entity);
    if (node==null) 
      return;
    
    
    scrollTo(node.pos);
    
    
    content.repaint();
    overview.repaint();
    
    
  }
  
    private void scrollTo(Point p) {
    
    center.setLocation(p);
    
    Rectangle2D b = model.getBounds();
    Dimension   d = getSize();
    content.scrollRectToVisible(new Rectangle(
      (int)( (p.getX()-b.getMinX()) * (DPMM.getX()*zoom) ) - d.width /2,
      (int)( (p.getY()-b.getMinY()) * (DPMM.getY()*zoom) ) - d.height/2,
      d.width ,
      d.height
    ));
    
  }
  
    private void scrollToCurrent() {

















  }
  
  
	private void setZoom(double d) {
		zoom = Math.max(0.1D, Math.min(1.0, d));
		content.invalidate();
		TreeView.this.validate();
		scrollToCurrent();
		repaint();
	}
  
  
  
  public void populate(ToolBar toolbar) {

    
    sliderZoom = new SliderWidget(1, 100, (int)(zoom*100));
    sliderZoom.addChangeListener(new ZoomGlue());
    sliderZoom.setAlignmentX(0F);
    sliderZoom.setOpaque(false);
    toolbar.add(sliderZoom);
    
    
    ButtonHelper bh = new ButtonHelper();
    toolbar.add(bh.create(new ActionOverview(), null, overview.isVisible()));
    
    
    toolbar.addSeparator();
    
    
    toolbar.add(bh.create(new ActionOrientation(), Images.imgVert, model.isVertical()));
    
    
    toolbar.add(bh.create(new ActionFamsAndSpouses(), Images.imgDoFams, model.isFamilies()));
      
    
    toolbar.add(bh.create(new ActionFoldSymbols(), null, model.isFoldSymbols()));
      
    
    toolbar.addSeparator();
        
    
    PopupWidget pb = new PopupWidget("",BOOKMARK_ICON) {
      
      public List getActions() {
        return TreeView.this.model.getBookmarks();
      }
    };
    pb.setToolTipText(RESOURCES.getString("bookmark.tip"));
    pb.setOpaque(false);
    toolbar.add(pb);
    
    
  }
  
  
  public int getPriority() {
    return NORMAL;
  }

  
  public List<Action2> createActions(Context context, Purpose purpose) {
    
    List<Action2> result = new ArrayList<Action2>(2);

    
    if (purpose==Purpose.CONTEXT&&context.getEntities().size()==1) {
      
      Entity entity = context.getEntity();
      if (entity instanceof Indi||entity instanceof Fam) { 
        
        result.add(new ActionRoot(entity));
        result.add(new ActionBookmark(entity, false));
      }
    }
    
    
    return result;
  }

  
  public void setRoot(Entity root) {
    if (!(root instanceof Indi||root instanceof Fam)) 
      return;
    model.setRoot(root);
  }

  
  private Point view2model(Point pos) {
    Rectangle bounds = model.getBounds();
    return new Point(
      (int)Math.rint(pos.x / (DPMM.getX()*zoom) + bounds.getMinX()), 
      (int)Math.rint(pos.y / (DPMM.getY()*zoom) + bounds.getMinY())
    );
  }

  
   Blueprint getBlueprint(String tag) {
    return BlueprintManager.getInstance().getBlueprint(tag, tag2blueprint.get(tag));
  }
  
    private EntityRenderer getEntityRenderer(String tag) {
    EntityRenderer result = (EntityRenderer)tag2renderer.get(tag);
    if (result==null) { 
      result = createEntityRenderer(tag);
      result.setResolution(DPI);
      result.setScaleFonts(isAdjustFonts);
      tag2renderer.put(tag,result);
    }
    return result;
  }
  
  
   EntityRenderer createEntityRenderer(String tag) {
    return new EntityRenderer(getBlueprint(tag), contentFont);
  }

  
  public boolean checkFilter(Property prop) {
    return false;


































  }

  
  public String getFilterName() {
    return model.getEntities().size()+" nodes in "+title;
  }

    private class Overview extends ViewPortOverview implements ModelListener {
        private Overview(JScrollPane scroll) {
      super(scroll.getViewport());
      super.setSize(new Dimension(TreeView.this.getWidth()/4,TreeView.this.getHeight()/4));
    }
    
    public void addNotify() {
      
      super.addNotify();
      
      model.addListener(this);
    }
    
    public void removeNotify() {
      model.removeListener(this);
      
      super.removeNotify();
    }
    
    
    public void setSize(int width, int height) {
      width = Math.max(32,width);
      height = Math.max(32,height);
      super.setSize(width, height);
    }
    
    protected void renderContent(Graphics g, double zoomx, double zoomy) {

      
      g.setColor(Color.WHITE);
      Rectangle r = g.getClipBounds();
      g.fillRect(r.x,r.y,r.width,r.height);

      
      UnitGraphics gw = new UnitGraphics(g,DPMM.getX()*zoomx*zoom, DPMM.getY()*zoomy*zoom);
      
      
      contentRenderer.cIndiShape     = Color.BLACK;
      contentRenderer.cFamShape      = Color.BLACK;
      contentRenderer.cArcs          = Color.LIGHT_GRAY;
      contentRenderer.cSelectedShape = Color.RED;
      contentRenderer.selected       = context.getEntities();
      contentRenderer.indiRenderer   = null;
      contentRenderer.famRenderer    = null;
      
      
      contentRenderer.render(gw, model);
      
      
      gw.popTransformation();

      
    }
    
    public void nodesChanged(Model arg0, Collection arg1) {
      repaint();
    }
    
    public void structureChanged(Model arg0) {
      repaint();
    }
  } 
  
  
  private class Content extends JComponent implements ModelListener, MouseWheelListener, MouseListener, ContextProvider  {

    
    private Content() {
      
      addMouseListener(this);
      addMouseWheelListener(this);
    }
    
    public void addNotify() {
      
      super.addNotify();
      
      model.addListener(this);
    }
    
    public void removeNotify() {
      model.removeListener(this);
      
      super.removeNotify();
    }
    
    public void mouseWheelMoved(MouseWheelEvent e) {
    	setZoom(zoom - e.getWheelRotation()*0.1);
    }
    
    
    public ViewContext getContext() {
      ViewContext result = new ViewContext(context);
      if (context.getEntity() instanceof Indi) {
        result.addAction(new ActionBookmark((Indi)context.getEntity(), true));
      }
      result.addAction(new ActionChooseRoot());
      return result;
    }
    
    
    public void structureChanged(Model model) {
      
      
      
      
      
      invalidate();
      TreeView.this.validate(); 
      
      repaint();
      
      scrollToCurrent();
    }
    
    
    public void nodesChanged(Model model, Collection nodes) {
      repaint();
    }
    
    
    public Dimension getPreferredSize() {
      Rectangle2D bounds = model.getBounds();
      double 
        w = bounds.getWidth () * (DPMM.getX()*zoom),
        h = bounds.getHeight() * (DPMM.getY()*zoom);
      return new Dimension((int)w,(int)h);
    }
  
    
    public void paint(Graphics g) {
      
      g.setColor((Color)colors.get("background"));
      Rectangle r = g.getClipBounds();
      g.fillRect(r.x,r.y,r.width,r.height);
      
      UnitGraphics gw = new UnitGraphics(g,DPMM.getX()*zoom, DPMM.getY()*zoom);
      gw.setAntialiasing(isAntialiasing);
      
      contentRenderer.cIndiShape     = (Color)colors.get("indis");
      contentRenderer.cFamShape      = (Color)colors.get("fams");
      contentRenderer.cArcs          = (Color)colors.get("arcs");
      contentRenderer.cSelectedShape = (Color)colors.get("selects");
      contentRenderer.selected       = context.getEntities();
      contentRenderer.indiRenderer   = getEntityRenderer(Gedcom.INDI);
      contentRenderer.famRenderer    = getEntityRenderer(Gedcom.FAM );
      
      contentRenderer.render(gw, model);
      
    }
    
    
    public void mousePressed(MouseEvent e) {
      
      Point p = view2model(e.getPoint());
      Object content = model.getContentAt(p.x, p.y);
      
      if (content==null) {
        repaint();
        overview.repaint();
        return;
      }
      
      if (content instanceof Entity) {
        Entity entity = (Entity)content;
        
        if ((e.getModifiers()&MouseEvent.CTRL_DOWN_MASK)!=0) {
          List<Entity> entities = new ArrayList<Entity>(context.getEntities());
          if (entities.contains(entity))
            entities.remove(entity);
          else
            entities.add(entity);
        } else {
          context = new Context(entity);
        }
        repaint();
        overview.repaint();
        
        SelectionSink.Dispatcher.fireSelection(e, context);
        return;
      }
      
      if (content instanceof Runnable) {
        ((Runnable)content).run();
        return;
      }
      
    }
    
    
    public void mouseClicked(MouseEvent e) {
    }
    
    public void mouseEntered(MouseEvent e) {
    }
    
    public void mouseExited(MouseEvent e) {
    }
    
    public void mouseReleased(MouseEvent evt) {
    }
  } 

  
  private class ZoomGlue implements ChangeListener {
    
    public void stateChanged(ChangeEvent e) {
    	setZoom(sliderZoom.getValue()*0.01D);
    }

  } 
  
  
  private class ActionOverview extends Action2 {
    
    private ActionOverview() {
      setImage(Images.imgOverview);
      setTip(RESOURCES, "overview.tip");
    }
    
    public void actionPerformed(ActionEvent event) {
      overview.setVisible(!overview.isVisible());
    }
  } 

  
  private class ActionRoot extends Action2 {
    
    private Entity root;
    
    private ActionRoot(Entity entity) {
      root = entity;
      setText(RESOURCES.getString("root",title));
      setImage(Images.imgView);
    }
    
    
    public void actionPerformed(ActionEvent event) {
      setRoot(root);
    }
  } 

    private class ActionOrientation extends Action2 {
        private ActionOrientation() {
      super.setImage(Images.imgHori);
      super.setTip(RESOURCES, "orientation.tip");
    }
    
    public void actionPerformed(ActionEvent event) {
      model.setVertical(!model.isVertical());
      scrollToCurrent();
    }
  } 
  
  
  private class ActionFamsAndSpouses extends Action2 {
    
    private ActionFamsAndSpouses() {
      super.setImage(Images.imgDontFams);
      super.setTip(RESOURCES, "families.tip");
    }
    
    public void actionPerformed(ActionEvent event) {
      model.setFamilies(!model.isFamilies());
      scrollToCurrent();
    }
  } 

  
  private class ActionFoldSymbols extends Action2 {
    
    private ActionFoldSymbols() {
      super.setImage(Images.imgFoldSymbols);
      super.setTip(RESOURCES, "foldsymbols.tip");
    }
    
    public void actionPerformed(ActionEvent event) {
      model.setFoldSymbols(!model.isFoldSymbols());
      scrollToCurrent();
    }
  } 
  
  
  private class ActionChooseRoot extends Action2 {

    
    private ActionChooseRoot() {
      setText(RESOURCES, "select.root");
    }

    
    public void actionPerformed(ActionEvent event) {
      
      
      SelectEntityWidget select = new SelectEntityWidget(context.getGedcom(), Gedcom.INDI, null);
      int rc = WindowManager.getInstance().openDialog("select.root", getText(), WindowManager.QUESTION_MESSAGE, select, Action2.okCancel(), TreeView.this);
      if (rc==0) 
        setRoot(select.getSelection());
      
      
    }
    
  } 

  
  private class ActionBookmark extends Action2 {
    
    private Entity entity;
    
    private ActionBookmark(Entity e, boolean local) {
      entity = e;
      if (local) {
        setText(RESOURCES, "bookmark.add");
        setImage(BOOKMARK_ICON);
      } else {
        setText(RESOURCES.getString("bookmark.in",title));
        setImage(Images.imgView);
      }
    } 
    
    public void actionPerformed(ActionEvent event) {
      
      
      String name = "";
      if (entity instanceof Indi) {
        name = ((Indi)entity).getName();
      }
      if (entity instanceof Fam) {
        Indi husb = ((Fam)entity).getHusband();
        Indi wife = ((Fam)entity).getWife();
        if (husb!=null&&wife!=null) name = husb.getName() + " & " + wife.getName();
      }
      
      
      name = WindowManager.getInstance().openDialog(
        null, title, WindowManager.QUESTION_MESSAGE, RESOURCES.getString("bookmark.name"), name, TreeView.this
      );
      
      if (name==null) return;
      
      
      model.addBookmark(new Bookmark(TreeView.this, name, entity));
      
      
    }
  
  } 

} 
