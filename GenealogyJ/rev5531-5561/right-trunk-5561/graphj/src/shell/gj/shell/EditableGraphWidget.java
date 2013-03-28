
package gj.shell;

import static gj.geom.Geometry.*;
import static gj.geom.ShapeHelper.*;

import gj.layout.Graph2D;
import gj.layout.GraphLayout;
import gj.model.Edge;
import gj.model.Vertex;
import gj.shell.model.EditableEdge;
import gj.shell.model.EditableGraph;
import gj.shell.model.EditableVertex;
import gj.shell.swing.Action2;
import gj.shell.swing.SwingHelper;
import gj.shell.util.ReflectHelper;
import gj.ui.DefaultGraphRenderer;
import gj.ui.GraphRenderer;
import gj.ui.GraphWidget;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;



public class EditableGraphWidget extends GraphWidget {
  
  private final static Stroke DASH = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, new float[]{ 2, 3}, 0.0f);
  
  
  private EditableGraph graph;
  private Collection<Shape> debugShapes;
  
  
  private boolean quickNode = false;
  
  
  private DnD
    dndNoOp = new DnDIdle(),
    dndMoveNode = new DnDMoveVertex(),
    dndCreateEdge = new DnDCreateEdge(),
    dndResizeNode = new DnDResizeVertex(),
    dndCurrent = null;
    
  
  private GraphLayout currentLayout;

  
  private GraphRenderer renderer = new DefaultGraphRenderer() {
    
    @Override
    protected Color getColor(Vertex vertex) {
      return vertex==graph.getSelection() ? Color.BLUE : Color.BLACK;    
    }
    
    
    @Override
    protected Color getColor(Edge edge) {
      return edge==graph.getSelection() ? Color.BLUE : Color.BLACK;    
    }
    
    @Override
    protected Stroke getStroke(Vertex vertexOrEdge) {
      
      if (currentLayout!=null) {
        for (Method prop : ReflectHelper.getMethods(currentLayout, "get.*", new Class[] { Graph2D.class }, false)) {
          try {
            Object value = prop.invoke(currentLayout, graph);
            if (value.equals(vertexOrEdge))
              return DASH;
          } catch (Throwable t) {
          }
        }
      }
      return super.getStroke(vertexOrEdge);
    }
    
    @Override
    public void render(Graph2D graph2d, Graphics2D graphics) {
    
      graphics.setColor(Color.LIGHT_GRAY);
      if (debugShapes!=null) for (Shape shape : debugShapes) {
        graphics.draw(shape);
      }
      
      super.render(graph2d, graphics);
    }

  };
  
  
  public EditableGraphWidget() {
    super(new EditableGraph());
    setRenderer(renderer);
  }
  
  
  public void setDebugShapes(Collection<Shape> shapes) {
    debugShapes = shapes;
    repaint();
  }
    
  
  @Override
  public void setGraph2D(Graph2D setGraph, Rectangle bounds) {

    if (!(setGraph instanceof EditableGraph))
      throw new IllegalArgumentException();
    
    
    debugShapes = null;
    
    
    graph = (EditableGraph)setGraph;
    
    
    super.setGraph2D(setGraph, bounds);
    
    
    dndNoOp.start(null);
    
    
  }
  
  
  public void setCurrentLayout(GraphLayout set) {
    currentLayout = set;
    repaint();
  }
  
  
  protected JPopupMenu getEdgePopupMenu(EditableEdge e, Point pos) {

    
    JPopupMenu result = new JPopupMenu();
    result.add(new ActionDeleteEdge());
    result.add(new ActionReverseEdge());

    
    if (currentLayout!=null) {
      List<Method> methods = ReflectHelper.getMethods(currentLayout, "set.*", new Class[] { Graph2D.class, e.getClass() }, true );
      for (Method method : methods) { 
        result.add(new ActionInvoke(currentLayout, method, new Object[] { graph, e }));
      }
    }
    
    return result;
  }

  
  protected JPopupMenu getVertexPopupMenu(EditableVertex v, Point pos) {

    
    JPopupMenu result = new JPopupMenu();
    result.add(new ActionResizeVertex());
    result.add(new ActionSetVertexContent());
    JMenu mShape = new JMenu("Set Shape");
    for (int i=0;i<Shell.shapes.length;i++)
      mShape.add(new ShapeMenuItemWidget(Shell.shapes[i], new ActionSetVertexShape(Shell.shapes[i])));
    result.add(mShape);
    result.add(new ActionDeleteVertex());

    
    if (currentLayout!=null) {
      List<Method> methods = ReflectHelper.getMethods(currentLayout, "set.*", new Class[] { Graph2D.class, v.getClass()}, true );
      for (Method method : methods) { 
        result.add(new ActionInvoke(currentLayout, method, new Object[] { graph, v }));
      }
    }
    
    
    return result;    
  }
  
  
  protected JPopupMenu getCanvasPopupMenu(Point pos) {
    
    JPopupMenu result = new JPopupMenu();
    result.add(new ActionCreateVertex(getPoint(pos)));
    result.add(SwingHelper.getCheckBoxMenuItem(new ActionToggleQuickVertex()));
    return result;
  }
  
  
  protected void layoutConfiguredNotify(GraphLayout layout) {
    
  }

  
  private abstract class DnD extends MouseAdapter implements MouseMotionListener {
    
    protected void start(Point p) {
      
      if (dndCurrent!=null) {
        dndCurrent.stop();
        removeMouseListener(dndCurrent);
        removeMouseMotionListener(dndCurrent);
      }
      
      dndCurrent = this;
      
      addMouseListener(dndCurrent);
      addMouseMotionListener(dndCurrent);
      
    }
    
    protected void stop() { }
     
    @SuppressWarnings("all")
    public void mouseDragged(MouseEvent e) {
    }
     
    @SuppressWarnings("all")
    public void mouseMoved(MouseEvent e) {
    }
  } 
  
  
  private class DnDIdle extends DnD {
    private Object selection;
    private int button;
    
    @Override
    public void mousePressed(MouseEvent e) {
      
      if (graph==null) return;
      
      this.selection = graph.getElement(getPoint(e.getPoint()));
      graph.setSelection(selection);
      button = e.getButton();
      
      if (e.isPopupTrigger()) {
        popup(e.getPoint());
      }
      
      repaint();
      
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
      
      if (selection instanceof EditableVertex) {
        if (button==MouseEvent.BUTTON1)
          dndMoveNode.start(e.getPoint());
        else
          dndCreateEdge.start(e.getPoint());
      }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
      
      
      if (e.isPopupTrigger()) {
        popup(e.getPoint());
      } else {
        
        if (selection==null&&quickNode) {
          graph.addVertex(createShape(Shell.shapes[0],getPoint(e.getPoint())), ""+(graph.getNumVertices() + 1) );
        }
        repaint();
      }
      
    }
    
    private void popup(Point at) {
      Object selection = graph.getSelection();
      JPopupMenu menu = null;
      if (selection instanceof EditableVertex) 
        menu = getVertexPopupMenu((EditableVertex)selection, at);
      if (selection instanceof EditableEdge)
        menu = getEdgePopupMenu((EditableEdge)selection, at);
      if (menu==null)
        menu = getCanvasPopupMenu(at);
      menu.show(EditableGraphWidget.this,at.x,at.y);
    }
  } 
  
  
  private class DnDMoveVertex extends DnD {
    Point from;
    
    @Override
    protected void start(Point at) {
      super.start(at);
      from = at;
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
      dndNoOp.start(e.getPoint());
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
      
      EditableVertex v = (EditableVertex)graph.getSelection();
      double dx = e.getPoint().x - from.getX();
      double dy = e.getPoint().y - from.getY();
      v.setOriginalShape(createShape(v.getOriginalShape(), AffineTransform.getTranslateInstance(dx, dy)));
      from.setLocation(e.getPoint());
      
      repaint();
    }
  } 
  
  
  private class DnDCreateEdge extends DnD{
    
    private EditableVertex from;
    
    private EditableVertex dummy;
    
    @Override
    protected void start(Point at) {
      super.start(at);
      from = (EditableVertex)graph.getSelection();
      dummy = null;
      graph.setSelection(null);
    }
    
    @Override
    protected void stop() {
      
      if (dummy!=null)  {
        graph.removeVertex(dummy);
        dummy = null;
      }
      
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
      
      stop();
      
      if (graph.getSelection()!=null) {
        Object selection = graph.getSelection();
        if (selection instanceof EditableVertex) {
          EditableVertex v = (EditableVertex)selection;
	        if (from.isNeighbour(v))
	          graph.removeEdge(from.getEdge(v));
	        new ActionCreateEdge(from, v).trigger();
        }
      }
      
      repaint();
      
      dndNoOp.start(e.getPoint());
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
      
      if (dummy==null) {
        
        if (graph.getVertex(getPoint(e.getPoint()))==from)
          return;
        
        dummy = graph.addVertex(null, null);      
        graph.addEdge(from, dummy);
      }
      
      Point2D pos = getPoint(e.getPoint());
      dummy.setShape(createShape(new Rectangle2D.Double(), pos));
      
      EditableVertex selection = graph.getVertex(pos);
      if (selection!=from)
        graph.setSelection(graph.getVertex(pos));
      
      repaint();
    }
  } 

  
  private class DnDResizeVertex extends DnD {
    
    private EditableVertex vertex;
    private Shape shape;
    private Dimension dim;
    private Point2D start = null;
    
    @Override
    protected void start(Point pos) {
      super.start(pos);
      
      vertex = (EditableVertex)graph.getSelection();
      shape = vertex.getOriginalShape();
      dim = shape.getBounds().getSize();
      
      start = model2screen(getCenter(shape));
      repaint();
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
      dndNoOp.start(e.getPoint());
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
      
      
      Point2D delta = getDelta(start, e.getPoint());
      double 
        sx = Math.max(0.1,Math.abs(delta.getX())/dim.width *2),
        sy = Math.max(0.1,Math.abs(delta.getY())/dim.height*2);

      vertex.setOriginalShape(createShape(shape, AffineTransform.getScaleInstance(sx, sy)));

      
      repaint();
    }
  } 

  
  private class ActionDeleteVertex extends Action2 {
    protected ActionDeleteVertex() { super("Delete Vertex"); }
    @Override
    protected void execute() {
      EditableVertex selection = (EditableVertex)graph.getSelection();
      if (selection==null)
        return;
      graph.removeVertex(selection);
      repaint();
    }
  } 

  
  private class ActionReverseEdge extends Action2 {
    protected ActionReverseEdge() { super("Reverse Edge"); }
    @Override
    protected void execute() {
      EditableEdge selection = (EditableEdge)graph.getSelection();
      if (selection==null)
        return;
      EditableVertex start = selection.getStart(), end = selection.getEnd();
      graph.removeEdge(selection);
      graph.addEdge(end, start);
      repaint();
    }
  } 

  
  private class ActionDeleteEdge extends Action2 {
    protected ActionDeleteEdge() { super("Delete Edge"); }
    @Override
    protected void execute() {
      EditableEdge selection = (EditableEdge)graph.getSelection();
      if (selection==null)
        return;
      graph.removeEdge(selection);
      repaint();
    }
  } 

  
  private class ActionSetVertexShape extends Action2 {
    Shape shape;
    protected ActionSetVertexShape(Shape set) {
      shape = set;
    }
    @Override
    protected void execute() {
      EditableVertex vertex = (EditableVertex)graph.getSelection();
      vertex.setOriginalShape(createShape(shape, getCenter(vertex.getShape())));
      repaint();
    }
  }

  
  private class ActionSetVertexContent extends Action2 {
    protected ActionSetVertexContent() { super("Set content"); }
    @Override
    protected void execute() {
      String txt = SwingHelper.showDialog(EditableGraphWidget.this, "Set content", "Please enter text here:");
      if (txt==null) 
        return;
      EditableVertex vertex = (EditableVertex)graph.getSelection();
      vertex.setContent(txt);
      repaint();
    }
  }

  
  private class ActionResizeVertex extends Action2 {
    protected ActionResizeVertex() { super("Resize"); }
    @Override
    protected void execute() { dndResizeNode.start(null); }
  }
  
  
  private class ActionCreateEdge extends Action2 {
    private EditableVertex from, to;
    protected ActionCreateEdge(EditableVertex v1, EditableVertex v2) {
      from = v1;
      to = v2;
    }
    @Override
    protected void execute() throws Exception {
      graph.addEdge(from, to);
    }
  }
  
  
  private class ActionCreateVertex extends Action2 {
    private Point2D pos;
    protected ActionCreateVertex(Point2D setPos) { 
      super("Create node"); 
      pos = setPos;
    }
    @Override
    protected void execute() {
      String txt = SwingHelper.showDialog(EditableGraphWidget.this, "Set content", "Please enter text here:");
      if (txt!=null) 
        graph.addVertex(createShape(Shell.shapes[0], pos), txt);
      repaint();
    }
  }

  
  private class ActionToggleQuickVertex extends Action2 {
    protected ActionToggleQuickVertex() { super("QuickNode"); }
    @Override
    protected void execute() { quickNode=!quickNode; }
    @Override
    public boolean isSelected() { return quickNode; }
  }
  
  
  private class ActionInvoke extends Action2 {
    private GraphLayout target;
    private Method method;
    private Object[] values;
    protected ActionInvoke(GraphLayout target, Method method, Object[] values) { 
      super.setName(ReflectHelper.getName(target.getClass())+"."+method.getName()+"(...)"); 
      this.target = target;
      this.method = method;
      this.values = values;
    }
    @Override
    protected void execute() { 
      try {
        Class<?>[] types = method.getParameterTypes();
        Object[] args = new Object[types.length];

        
        for (int i=0;i<args.length;i++) {
          if (i<values.length)
            args[i] = values[i];
          else
            args[i] = ReflectHelper.wrap(JOptionPane.showInputDialog("Provide input for "+getName()), types[i] );
        }
        
        
        method.invoke(target, args);
        
        
        layoutConfiguredNotify(target);
        
      } catch (Exception e) {
      }
      repaint();
    }
  }
  
} 
