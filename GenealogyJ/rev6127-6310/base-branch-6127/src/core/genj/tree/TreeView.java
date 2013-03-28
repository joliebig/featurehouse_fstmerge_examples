
package genj.tree;

import genj.common.SelectEntityWidget;
import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.io.Filter;
import genj.print.PrintAction;
import genj.print.PrintRenderer;
import genj.renderer.Blueprint;
import genj.renderer.BlueprintManager;
import genj.renderer.ChooseBlueprintAction;
import genj.renderer.DPI;
import genj.renderer.BlueprintRenderer;
import genj.renderer.Options;
import genj.renderer.RenderSelectionHintKey;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.ButtonHelper;
import genj.util.swing.DialogHelper;
import genj.util.swing.ImageIcon;
import genj.util.swing.PopupWidget;
import genj.util.swing.ScrollPaneWidget;
import genj.util.swing.SliderWidget;
import genj.util.swing.UnitGraphics;
import genj.util.swing.ViewPortAdapter;
import genj.util.swing.ViewPortOverview;
import genj.util.swing.Action2.Group;
import genj.view.ActionProvider;
import genj.view.ContextProvider;
import genj.view.ScreenshotAction;
import genj.view.SelectionSink;
import genj.view.SettingsAction;
import genj.view.ToolBar;
import genj.view.View;
import genj.view.ViewContext;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class TreeView extends View implements ContextProvider, ActionProvider, Filter {
  
  protected final static ImageIcon BOOKMARK_ICON = new ImageIcon(TreeView.class, "images/Bookmark");      
  protected final static Registry REGISTRY = Registry.get(TreeView.class);
  protected final static Resources RESOURCES = Resources.get(TreeView.class);
  protected final static String TITLE = RESOURCES.getString("title");
  
  
  private final Point2D DPMM;
  
  
  private Model model;
  
  
  private Content content;
  
  
  private Overview overview;
  
  
  private ContentRenderer contentRenderer;
  
  
  private double zoom = 1.0D;

    
  private SliderWidget sliderZoom;  
  
  
  private boolean isAntialiasing = false;
  
  
  private Map<String,Color> colors = new HashMap<String, Color>();
  
  
  private Map<String,String> tag2blueprint = new HashMap<String,String>();
  
  
  private Map<String,BlueprintRenderer> tag2renderer = new HashMap<String, BlueprintRenderer>();
  
  
  private Font contentFont = new Font("SansSerif", 0, 10);
  
  
  private Point2D.Double center = new Point2D.Double(0,0);
  
  
  private Context context = new Context();
  
  private boolean ignoreContextChange = false;
  
  
  public TreeView() {
    
    
    DPI dpi = Options.getInstance().getDPI();
    DPMM = new Point2D.Float(
      dpi.horizontal() / 2.54F / 10,
      dpi.vertical() / 2.54F / 10
    );
    
    
    colors.put("background", Color.WHITE);
    colors.put("indis"     , Color.BLACK);
    colors.put("fams"      , Color.DARK_GRAY);
    colors.put("arcs"      , Color.BLUE);
    colors.put("selects"   , Color.RED);
    colors = REGISTRY.get("color", colors);
    
    
    contentFont = REGISTRY.get("font", contentFont);
    
    
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
    model.setHideAncestorsIDs(REGISTRY.get("hide.ancestors", new ArrayList<String>()));
    model.setHideDescendantsIDs(REGISTRY.get("hide.descendants", new ArrayList<String>()));
 
    
    contentRenderer = new ContentRenderer();
    content = new Content();
    JScrollPane scroll = new ScrollPaneWidget(new ViewPortAdapter(content));
    overview = new Overview(scroll);
    overview.setVisible(REGISTRY.get("overview", true));
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
    REGISTRY.put("color", colors);
    
    for (String tag : tag2blueprint.keySet()) {
      REGISTRY.put("blueprint."+tag, getBlueprint(tag).getName()); 
    }
    
    
    if (model.getRoot()!=null) 
      REGISTRY.put("root", model.getRoot().getId());
    
    
    REGISTRY.put("hide.ancestors"  , model.getHideAncestorsIDs());
    REGISTRY.put("hide.descendants", model.getHideDescendantsIDs());
    
    
    super.removeNotify();
  }
  
  
  public ViewContext getContext() {
    return content.getContext();
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
  
  
  public Font getContentFont() {
    return contentFont;
  }

  
  public void setContentFont(Font set) {
    
    if (contentFont.equals(set)) return;
    
    contentFont = set;
    
    repaint();
  }
  
  public void setColors(Map<String,Color> set) {
    for (String key : colors.keySet()) {
      Color c = set.get(key);
      if (c!=null)
        colors.put(key, c);
    }
    repaint();
  }
  
  public Map<String,Color> getColors() {
    return Collections.unmodifiableMap(colors);
  }

  
  public Model getModel() {
    return model;
  }

  
  @Override
  public void setContext(Context newContext, boolean isActionPerformed) {
    
    
    if (ignoreContextChange)
      return;
    
    
    context = new Context(newContext.getGedcom(), newContext.getEntities());
    
    
    if (isActionPerformed || context.getGedcom()==null) {
      setRoot(context.getEntity());
      return;
    }
    
    
    if (context.getEntity()==null)
      return;

    
    if (!show(context.getEntity()))
      setRoot(context.getEntity());
    
    
  }
  
  
   boolean show(Entity entity) {
    
    
    if (!(entity instanceof Indi||entity instanceof Fam)) 
      return true;
    
    
    TreeNode node = model.getNode(entity);
    if (node==null) 
      return false;
    
    
    scrollTo(node.pos);
    
    
    content.repaint();
    overview.repaint();
    
    
    return true;
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
    
    Entity current = context.getEntity();
    if (current==null)
      return;
    
    
    TreeNode node = model.getNode(current);
    if (node==null) 
      return;
    
    
    scrollTo(node.pos);

    
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
    sliderZoom.setFocusable(false);
    toolbar.add(sliderZoom);
    
    
    ButtonHelper bh = new ButtonHelper();
    toolbar.add(bh.create(new ActionOverview(), null, overview.isVisible()));
    
    
    toolbar.addSeparator();
    
    
    toolbar.add(bh.create(new ActionOrientation(), Images.imgVert, model.isVertical()));
    
    
    toolbar.add(bh.create(new ActionFamsAndSpouses(), Images.imgDoFams, model.isFamilies()));
      
    
    toolbar.add(bh.create(new ActionFoldSymbols(), null, model.isFoldSymbols()));
      
    
    toolbar.addSeparator();
        
    
    PopupWidget pb = new PopupWidget("",BOOKMARK_ICON) {
      @Override
      public void showPopup() {
        removeItems();
        for (Bookmark bookmark : TreeView.this.model.getBookmarks())
          addItem(new ActionGoto(bookmark));
        
        super.showPopup();
      }
    };
    pb.setToolTipText(RESOURCES.getString("bookmark.tip"));
    pb.setOpaque(false);
    toolbar.add(pb);
    
    
    toolbar.add(new Settings());
    toolbar.add(new ScreenshotAction(content));
    toolbar.add(new Print());
    
    
  }
  
  
  public void createActions(Context context, Purpose purpose, Group result) {
    
    
    if (context instanceof TreeContext)
      return;

    
    if (purpose==Purpose.CONTEXT&&context.getEntities().size()==1) {
      
      Entity entity = context.getEntity();
      if (entity instanceof Indi||entity instanceof Fam) { 
        
        result.add(new ActionRoot(entity));
        result.add(new ActionBookmark(entity, false));
      }
    }
    
    
  }

  
  public void setRoot(Entity root) {
    
    
    Entity old = model.getRoot();
    if (old!=null) {
      Gedcom gedcom = old.getGedcom();
      REGISTRY.put(gedcom.getName()+".bookmarks", model.getBookmarks());
    }
    
    
    if (root==null || root instanceof Indi ||root instanceof Fam) 
      model.setRoot(root);
    
    
    if (root!=null) {
      Gedcom gedcom = root.getGedcom();
      List<Bookmark> bookmarks = new ArrayList<Bookmark>();
      for (String b : REGISTRY.get(gedcom.getName()+".bookmarks", new String[0])) {
        try {
          bookmarks.add(new Bookmark(gedcom, b));
        } catch (Throwable t) {
        }
      }
      model.setBookmarks(bookmarks);
    }

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
  
  
  private BlueprintRenderer getEntityRenderer(String tag) {
    BlueprintRenderer result = tag2renderer.get(tag);
    if (result==null) { 
      result = new BlueprintRenderer(getBlueprint(tag));
      tag2renderer.put(tag,result);
    }
    return result;
  }
  
  
  public boolean checkFilter(Property prop) {
    return false;


































  }

  
  public String getFilterName() {
    return model.getEntities().size()+" nodes in "+TITLE;
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
    
    public void nodesChanged(Model arg0, Collection<TreeNode> arg1) {
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



      new Up().install(this, "U", JComponent.WHEN_FOCUSED);
    }

    private class Up extends Action2 {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("up");
      }
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
      
      
      if (e.isControlDown()) {
        sliderZoom.setValue(sliderZoom.getValue() - e.getWheelRotation()*10);
        return;
      }
      
      
      JViewport viewport = (JViewport)getParent().getParent();
      Rectangle r = viewport.getVisibleRect();
      if (e.isShiftDown()) 
        r.x += e.getWheelRotation()*16;
      else
        r.y += e.getWheelRotation()*16;
      viewport.scrollRectToVisible(r);
      
    }
    
    
    public ViewContext getContext() {
      TreeContext result = new TreeContext(context);
      Entity entity = context.getEntity();
      if (entity instanceof Indi) {
        result.addAction(new ActionBookmark((Indi)context.getEntity(), true));
      }
      if (entity!=null)
        result.addAction(new ChooseBlueprintAction(entity, getBlueprint(entity.getTag())) {
          @Override
          protected void commit(Entity recipient, Blueprint blueprint) {
            tag2blueprint.put(recipient.getTag(), blueprint.getName());
            tag2renderer.remove(recipient.getTag());
            repaint();
          }
        });
      result.addAction(new ActionChooseRoot());
      return result;
    }
    
    
    public void structureChanged(Model model) {
      
      
      
      
      
      invalidate();
      TreeView.this.validate(); 
      
      repaint();
      
      scrollToCurrent();
    }
    
    
    public void nodesChanged(Model model, Collection<TreeNode> nodes) {
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
      
      
      Boolean selection = (Boolean) ((Graphics2D)g).getRenderingHint(RenderSelectionHintKey.KEY);
      if (selection==null)
        selection = true;
      
      
      contentRenderer.font           = contentFont;
      contentRenderer.cIndiShape     = (Color)colors.get("indis");
      contentRenderer.cFamShape      = (Color)colors.get("fams");
      contentRenderer.cArcs          = (Color)colors.get("arcs");
      contentRenderer.cSelectedShape = (Color)colors.get("selects");
      contentRenderer.selected       = selection ? context.getEntities() : new ArrayList<Entity>() ;
      contentRenderer.indiRenderer   = getEntityRenderer(Gedcom.INDI);
      contentRenderer.famRenderer    = getEntityRenderer(Gedcom.FAM );
      
      contentRenderer.render(gw, model);
      
    }
    
    
    public void mousePressed(MouseEvent e) {
      requestFocusInWindow();
      
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
        
        try {
          ignoreContextChange = true;
          SelectionSink.Dispatcher.fireSelection(e, context);
        } finally {
          ignoreContextChange = false;
        }
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
      setText(RESOURCES.getString("root",TITLE));
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
      setImage(Images.imgView);
    }

    
    public void actionPerformed(ActionEvent event) {
      
      
      SelectEntityWidget select = new SelectEntityWidget(context.getGedcom(), Gedcom.INDI, null);
      int rc = DialogHelper.openDialog(getText(), DialogHelper.QUESTION_MESSAGE, select, Action2.okCancel(), TreeView.this);
      if (rc==0) 
        setRoot(select.getSelection());
      
      
    }
    
  } 

  private class ActionGoto extends Action2 {
    private Bookmark bookmark;
    private ActionGoto(Bookmark bookmark) {
      this.bookmark = bookmark;
      
      setText(bookmark.getName());
      setImage(Gedcom.getEntityImage(bookmark.getEntity().getTag()));
    }
    
    public void actionPerformed(ActionEvent event) {
      
      SelectionSink.Dispatcher.fireSelection(TreeView.this, new Context(bookmark.getEntity()), false);
    }

  }
  
  
  private class ActionBookmark extends Action2 {
    
    private Entity entity;
    
    private ActionBookmark(Entity e, boolean local) {
      entity = e;
      setImage(BOOKMARK_ICON);
      if (local) {
        setText(RESOURCES, "bookmark.add");
      } else {
        setText(RESOURCES.getString("bookmark.in",TITLE));
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
      
      
      name = DialogHelper.openDialog(
        TITLE, DialogHelper.QUESTION_MESSAGE, RESOURCES.getString("bookmark.name"), name, TreeView.this
      );
      
      if (name==null) return;
      
      
      model.addBookmark(new Bookmark(name, entity));
      
      
    }
  
  } 
  
  private class Settings extends SettingsAction {

    @Override
    protected TreeViewSettings getEditor() {
      return new TreeViewSettings(TreeView.this);
    }
    
  }
  
  private class TreeContext extends ViewContext {
    public TreeContext(Context context) {
      super(context);
    } 
  }
  
  private class Print extends PrintAction {
    
    public boolean yes;
    
    protected Print() {
      super(TITLE);
    }
    @Override
    protected PrintRenderer getRenderer() {
      return new TreeViewPrinter(TreeView.this);
    }
  }
  
} 
