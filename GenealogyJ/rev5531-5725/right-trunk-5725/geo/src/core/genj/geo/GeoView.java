
package genj.geo;

import genj.gedcom.Gedcom;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.ButtonHelper;
import genj.util.swing.ImageIcon;
import genj.util.swing.PopupWidget;
import genj.view.ContextSelectionEvent;
import genj.view.ToolBarSupport;
import genj.view.ViewManager;
import genj.window.WindowBroadcastEvent;
import genj.window.WindowBroadcastListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.Timer;
import javax.swing.ToolTipManager;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureCollection;
import com.vividsolutions.jump.feature.FeatureSchema;
import com.vividsolutions.jump.workbench.model.FeatureEventType;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.model.LayerManager;
import com.vividsolutions.jump.workbench.ui.LayerViewPanel;
import com.vividsolutions.jump.workbench.ui.LayerViewPanelContext;
import com.vividsolutions.jump.workbench.ui.cursortool.CursorTool;
import com.vividsolutions.jump.workbench.ui.renderer.style.BasicStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.LabelStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.RingVertexStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.SquareVertexStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.VertexStyle;
import com.vividsolutions.jump.workbench.ui.zoom.PanTool;
import com.vividsolutions.jump.workbench.ui.zoom.ZoomTool;


public class GeoView extends JPanel implements WindowBroadcastListener, ToolBarSupport {
  
   final static Logger LOG = Logger.getLogger("genj.geo");
  
  private final static ImageIcon 
    IMG_MAP = new ImageIcon(GeoView.class, "images/Map.png"),
    IMG_ZOOM = new ImageIcon(GeoView.class, "images/Zoom.png"),
    IMG_ZOOM_EXTENT = new ImageIcon(GeoView.class, "images/ZoomExtend.png");
  
  private final static ImageIcon[] STATUS2IMG = {
    new ImageIcon(GeoView.class, "images/Ok.png"),
    new ImageIcon(GeoView.class, "images/Warning.png"),
    new ImageIcon(GeoView.class, "images/Error.png")
  };
  
   final static Resources RESOURCES = Resources.get(GeoView.class);
  
  
  private Gedcom gedcom;
  
  
  private ViewManager viewManager;
  
  
  private GeoMap currentMap;
  
  
  private JSplitPane split;
  
  
  private LayerViewPanel layerPanel;
  
  
  private ActionLocate locate;
  
  
  private Registry registry;
  
  
  private GeoModel model;
  private GeoList locationList;
  private LocationsLayer locationLayer;  
  private SelectionLayer selectionLayer;
  private CursorTool currentTool;
  
  
  public GeoView(String title, Gedcom gedcom, Registry registry) {
    
    
    this.registry = registry;
    this.gedcom = gedcom;
    
    
    model = new GeoModel();
    
    
    locationList = new GeoList(model, this, viewManager);
    
    
    locationLayer = new LocationsLayer();  
    selectionLayer = new SelectionLayer();
    
    
    split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, null, new JScrollPane(locationList));
    split.setResizeWeight(1.0D);
    setLayout(new BorderLayout());
    add(BorderLayout.CENTER, split);
    
    
  }
  
  
  public void addNotify() {
    
    super.addNotify();
    
    model.setGedcom(gedcom);
    
    String map = registry.get("map", (String)null);
    GeoMap[] maps = GeoService.getInstance().getMaps();
    for (int i=0;i<maps.length;i++) {
      if (map==null||maps[i].getKey().equals(map)) {
        try { setMap(maps[i]); } catch (Throwable t) {}
        break;
      }
    }
    
  }
  
  
  public void removeNotify() {
    
    model.setGedcom(null);
    
    if (currentMap!=null)
      registry.put("map", currentMap.getKey());
    
    registry.put("split", split.getDividerLocation());
    
    super.removeNotify();
  }
  
  
  public boolean handleBroadcastEvent(WindowBroadcastEvent event) {
    
    ContextSelectionEvent cse = ContextSelectionEvent.narrow(event, gedcom);
    if (event.isInbound() && cse!=null)
      locationList.setSelectedContext(cse.getContext());
    
    return true;
  }
  
  
  public void populate(JToolBar bar) {
    
    
    GeoMap[] maps = GeoService.getInstance().getMaps();
    List actions = new ArrayList(maps.length);
    for (int i=0;i<maps.length;i++) {
      actions.add(new ChooseMap(maps[i]));
    }

    
    PopupWidget chooseMap = new PopupWidget(null, IMG_MAP, actions);
    chooseMap.setToolTipText(RESOURCES.getString("toolbar.map"));
    chooseMap.setEnabled(!actions.isEmpty());
    bar.add(chooseMap);
    
    
    ButtonHelper bh = new ButtonHelper().setInsets(0);
    bh.setContainer(bar);
    bh.create(new ZoomExtent());
    bh.setButtonType(JToggleButton.class).create(new ZoomOnOff());
    
    
    locate = new ActionLocate();
    bh.setButtonType(JButton.class).create(locate);
    
    
    
  }
  
  
  public void setSelection(GeoLocation location) {
    setSelection(location!=null ? Collections.singletonList(location) : Collections.EMPTY_LIST);
  }
  
  
  public void setSelection(Collection locations) {
    selectionLayer.setLocations(locations);
  }
  
  
  public void setMap(GeoMap map) throws IOException {
    
    
    currentMap = map;
    
    
    LayerManager layerManager = new LayerManager();
    layerManager.addLayer("GenJ", locationLayer);
    layerManager.addLayer("GenJ", selectionLayer);
    
    selectionLayer.setLayerManager(layerManager);
    locationLayer.setLayerManager(layerManager);
  
    
    map.load(layerManager);
    
    
    layerPanel = new LayerPanel(layerManager);
    layerPanel.setBackground(map.getBackground());
    if (currentTool!=null)
      layerPanel.setCurrentCursorTool(currentTool);
    
    
    split.setLeftComponent(layerPanel);
    revalidate();
    repaint();
    
    
    int pos = registry.get("split", -1);
    if (pos<0)
      split.setDividerLocation(0.7D);
    else {
      split.setLastDividerLocation(pos);
      split.setDividerLocation(pos);
    }
    
    
    ToolTipManager.sharedInstance().registerComponent(layerPanel);
    
    
  }

  
  private class ViewContext implements LayerViewPanelContext {
    public void warnUser(String warning) {
      LOG.warning("[JUMP]"+warning);
    }
    public void handleThrowable(Throwable t) {
      LOG.log(Level.WARNING, "[JUMP]", t);
    }
    public void setStatusMessage(String message) {
      if (message!=null&&message.length()>0)
        LOG.warning("[JUMP]"+message);
    }
  }
  
  
  private class ZoomExtent extends Action2 {

    
    private ZoomExtent() {
      setImage(IMG_ZOOM_EXTENT);
      setTip(RESOURCES, "toolbar.extent");
    }
    
    public void execute() {
      if (layerPanel!=null) try {
          layerPanel.getViewport().zoomToFullExtent();
        } catch (Throwable t) {
        }
      }
    
  } 

  
  private class ZoomOnOff extends Action2 {
    
    private ZoomOnOff() {
      setImage(IMG_ZOOM);
      setTip(RESOURCES, "toolbar.zoom");
    }
    
    protected void execute() {
      currentTool =  currentTool instanceof ZoomTool ? (CursorTool)new PanTool(null) :  new ZoomTool(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
      if (layerPanel!=null) 
        layerPanel.setCurrentCursorTool(currentTool);
    }
  }
 
  
  private class ChooseMap extends Action2 {
    private GeoMap map;
    
    private ChooseMap(GeoMap map) {
      this.map = map;
      setText(map.getName());
    }
    
    protected void execute() {
      
      registry.put("split", split.getDividerLocation());
      
      try {
        setMap(map);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    public Icon getImage() {
      return currentMap!=map ? null : IMG_MAP;
    }
  }
 
  
  private class SelectionLayer extends LocationsLayer{
    
    private List selection = Collections.EMPTY_LIST;
    
    
    private SelectionLayer() {
    }

    
    protected void initStyles() {
      
      
      addStyle(new BasicStyle(Color.RED));
       
      VertexStyle vertices = new RingVertexStyle();
      vertices.setEnabled(true);
      vertices.setSize(5);
      addStyle(vertices);
       
      LabelStyle labels = new LabelStyle();
      labels.setEnabled(false);
      addStyle(labels);
      
      
    }
    
    
    public void setLocations(Collection set) {
      synchronized (this) {
        selection = new ArrayList(set);
      }
      LayerManager mgr = getLayerManager();
      if (mgr!=null)
        mgr.fireFeaturesChanged(new ArrayList(), FeatureEventType.ADDED, this);
    }
    
    
    public void locationAdded(GeoLocation location) {
      setLocations(Collections.EMPTY_SET);
    }

    
    public void locationUpdated(GeoLocation location) {
      setLocations(Collections.EMPTY_SET);
    }

    
    public void locationRemoved(GeoLocation location) {
      setLocations(Collections.EMPTY_SET);
    }

    
    public int size() {
      return selection.size();
    }
    
    
    public List getFeatures() {






      return selection;
    }
    
  } 
  
  
  private class LocationsLayer extends Layer implements FeatureCollection, GeoModelListener, ActionListener {
    
    private List locations = new ArrayList();
    
    protected Timer updateTimer;
    
    
    private LocationsLayer() {
      
      updateTimer = new Timer(500, this);
      updateTimer.setRepeats(false);
      
      
      setName(getClass().toString());
      setFeatureCollection(this);

      
      model.addGeoModelListener(this);

      
      initStyles();
    }
    
    private void reset() {
      for (Iterator it = model.getLocations().iterator(); it.hasNext(); ) {
        GeoLocation location = (GeoLocation)it.next();
        if (location.isValid())
          locations.add(location);
      }
      updateTimer.start();
    }
    
    
    protected void initStyles() {
      
      
      addStyle(new BasicStyle(Color.LIGHT_GRAY));
       
      VertexStyle vertices = new SquareVertexStyle();
      vertices.setEnabled(true);
      vertices.setSize(5);
      addStyle(vertices);
       
      LabelStyle labels = new LabelStyle();
      labels.setColor(Color.BLACK);
      labels.setEnabled(true);
      labels.setAttribute("PLAC");
      labels.setHidingOverlappingLabels(true);
      addStyle(labels);
      
      
    }
    
    
    public void actionPerformed(ActionEvent e) {
      LayerManager mgr = getLayerManager();
      if (mgr!=null)
        mgr.fireFeaturesChanged(new ArrayList(), FeatureEventType.ADDED, this);
    }
    
    
    public void asyncResolveEnd(int status, String msg) {
      if (locate!=null) {
        locate.setEnabled(true);
        locate.setTip(msg + " - " + RESOURCES.getString("resolve.again"));
        locate.setImage(STATUS2IMG[status]);
      }
    }

    
    public void asyncResolveStart() {
      if (locate!=null) locate.setEnabled(false);
    }

    
    public void locationAdded(GeoLocation location) {
      if (location.isValid())
        locations.add(location);
      updateTimer.start();
    }

    
    public void locationUpdated(GeoLocation location) {
      if (location.isValid()&&!locations.contains(location))
        locations.add(location);
      updateTimer.start();
    }

    
    public void locationRemoved(GeoLocation location) {
      locations.remove(location);
      updateTimer.start();
    }

    
    public FeatureSchema getFeatureSchema() {
      return GeoLocation.SCHEMA;
    }

    
    public Envelope getEnvelope() {
      return new Envelope();
    }
    
    
    public int size() {
      return locations.size();
    }
    
    
    public List getFeatures() {
      return locations;
    }
    
    
    public List query(Envelope envelope) {
      List locations = getFeatures();
      ArrayList result = new ArrayList(locations.size());
      for (Iterator it = locations.iterator(); it.hasNext();) {
        Feature feature = (Feature) it.next();
        if (feature.getGeometry().getEnvelopeInternal().intersects(envelope)) 
          result.add(feature);
      }
      return result;
    }
    
  } 
  
  
  private class LayerPanel extends LayerViewPanel implements ComponentListener, MouseListener {

    public LayerPanel(LayerManager mgr) {
      super(mgr, new ViewContext());
      addComponentListener(this);
      addMouseListener(this);
    }
    
    private List getLocations(MouseEvent event) {
      try {
        List result = new ArrayList(layerPanel.featuresWithVertex(event.getPoint(), 3,  model.getLocations()));
        Collections.sort(result);
        return result;
      } catch (Throwable t) {
        return Collections.EMPTY_LIST;
      }
    }
    
    private Coordinate getCoordinate(MouseEvent event) {
      try {
        return getViewport().toModelCoordinate(event.getPoint());
      } catch (Throwable t) {
        return null;
      }
    }
    
    public String getToolTipText(MouseEvent event) {
      
      
      Coordinate coord  = getCoordinate(event);
      if (coord==null)
        return null;
      
      Collection locations = getLocations(event);
      
      StringBuffer text = new StringBuffer();
      text.append("<html><body>");
      text.append( GeoLocation.getCoordinateAsString(coord));
      
      for (Iterator it = locations.iterator(); it.hasNext(); )  {
        GeoLocation location = (GeoLocation)it.next();
        text.append("<br><b>");
        text.append(location);
        text.append("</b>");
      }
      
      return text.toString();
    }

    
    public void componentHidden(ComponentEvent e) {
    }
    
    public void componentMoved(ComponentEvent e) {
    }
    
    public void componentResized(ComponentEvent e) {
      new ZoomExtent().trigger();
    }
    
    public void componentShown(ComponentEvent e) {
    }

    
    public void mouseClicked(MouseEvent e) {
      Collection locations = getLocations(e);
      if (!locations.isEmpty()) {
        locationList.setSelectedLocations(locations);
        selectionLayer.setLocations(locations);
      }
    }
    public void mouseEntered(MouseEvent e) {
    }
    public void mouseExited(MouseEvent e) {
    }
    public void mousePressed(MouseEvent e) {
    }
    public void mouseReleased(MouseEvent e) {
    }
    
  }

  
  private class ActionLocate extends Action2 {
    private ActionLocate() {
      setImage(STATUS2IMG[GeoModel.ALL_MATCHED]);
    }
    protected void execute() {
      model.resolveAll();
    }
  }

} 
