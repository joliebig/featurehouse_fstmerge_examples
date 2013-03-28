
package gj.example.inheritance;

import gj.example.Example;
import gj.geom.Geometry;
import gj.geom.ShapeHelper;
import gj.geom.TransformedShape;
import gj.layout.Graph2D;
import gj.layout.LayoutException;
import gj.layout.graph.radial.RadialLayout;
import gj.model.Vertex;
import gj.ui.DefaultGraphRenderer;
import gj.ui.GraphWidget;
import gj.util.DefaultGraph;
import gj.util.DefaultLayoutContext;
import gj.util.TreeGraphAdapter;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;


public class InheritanceTree implements Example {

  private static final Class<?> root = JLabel.class;
  
  public String getName() {
    return "Inheritance of "+root;
  }
  
  public JComponent prepare(GraphWidget widget) {
    
    
    TreeGraphAdapter.Tree<Class<?>> tree = new TreeGraphAdapter.Tree<Class<?>>() {
      public List<Class<?>> getChildren(Class<?> parent) {
        List<Class<?>> result = new ArrayList<Class<?>>();
        for (Class<?> c : Arrays.asList(parent.getInterfaces()))
          result.add(c);
        result.remove(Serializable.class);
        if (parent.getSuperclass()!=null)
          result.add(parent.getSuperclass());
        return result;
      }
      public Class<?> getParent(Class<?> child) {
        return getParent(getRoot(), child);
      }
      private Class<?> getParent(Class<?> parent, Class<?> child) {
        throw new IllegalArgumentException("not supported");
      }
      public Class<?> getRoot() {
        return root;
      }
    };

    final TreeGraphAdapter<Class<?>> adapter  = new TreeGraphAdapter<Class<?>>(tree);
 
    
    final int w = 150, h = 16;
    
    Graph2D graph2d = new DefaultGraph(adapter, new Rectangle2D.Double(-h/2,-w/2,h,w));
    
    try {
      RadialLayout r = new RadialLayout();
      r.setDistanceBetweenGenerations(220);
      r.apply(graph2d, new DefaultLayoutContext());
    } catch (LayoutException e) {
      throw new RuntimeException("hmm, can't layout inheritance of "+root, e);
    }
    
    
    widget.setGraph2D(graph2d);
    
    
    widget.setRenderer(new DefaultGraphRenderer() {
      @Override
      protected void renderVertex(Graph2D graph2d, Vertex vertex, java.awt.Graphics2D graphics) {
        
        
        AffineTransform oldt = graphics.getTransform();
        Point2D pos = ShapeHelper.getCenter(graph2d.getShape(vertex));
        graphics.translate(pos.getX(), pos.getY());
        
        Shape shape = graph2d.getShape(vertex);
        if (shape instanceof TransformedShape) {
          graphics.transform(((TransformedShape)shape).getTransformation());
          graphics.transform(AffineTransform.getRotateInstance(Geometry.QUARTER_RADIAN));
        }
        
        
        Class<?> clazz = adapter.getContent(vertex);
        StringBuffer content = new StringBuffer();
        content.append(clazz.getSimpleName());
        Method[] methods = clazz.getDeclaredMethods();
        for (int i=0,j=0;j<5 && i<methods.length;i++) {
          if (methods[i].getName().startsWith("get")) {
            content.append("\n"+methods[i].getName()+"()");
            j++;
          }
        }
        draw(content.toString(), null, new Rectangle2D.Double(-w/2,-h/2,w,h), 0, 0.5, graphics);
        
        
        graphics.setTransform(oldt);
      }
    });
 
    
    return widget;
  }

}
