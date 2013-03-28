
package gj.io;

import static gj.geom.PathIteratorKnowHow.SEG_NAMES;
import static gj.geom.PathIteratorKnowHow.SEG_SIZES;
import gj.layout.Routing;
import gj.shell.model.EditableEdge;
import gj.shell.model.EditableGraph;
import gj.shell.model.EditableVertex;
import gj.util.DefaultRouting;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;


public class GraphWriter {

  
  private final static String EMPTY = "                                                         ";
  
  
  private PrintWriter out;
  
  
  private Stack<String> stack = new Stack<String>();
  
  
  private Map<Object,Integer> element2id  = new HashMap<Object,Integer>();
  
    
  public GraphWriter(OutputStream out) throws IOException {
    this.out = new PrintWriter(out);
  }

  
  public void write(EditableGraph g) throws IOException {
    
    push("graph",null,false);
    
    writeVertices(g);
    
    writeEdges(g);
    
    pop();
    out.flush();
  }

  
  private void writeEdges(EditableGraph g) throws IOException {
    
    push("edges",null,false);
    
    
    for (EditableEdge edge : g.getEdges()) 
      writeEdge(g, edge);
    
    pop();
  }

  
  private void writeEdge(EditableGraph g, EditableEdge edge) throws IOException {

    Routing path = edge.getPath();
    
    ElementInfo info = new ElementInfo();
    info.put("id", getId(edge));
    info.put("s", getId(edge.getStart()));
    info.put("e", getId(edge.getEnd()));
    if (path instanceof DefaultRouting && ((DefaultRouting)path).isInverted())
      info.put("d", "-1" );
    push("edge",info,false);
      writeShape("path", path);
    pop();
  }

  
  private void writeShape(String element, Shape shape) throws IOException {

    ElementInfo info = new ElementInfo();
    push(element,info,false);
    PathIterator it = shape.getPathIterator(null);
    double[] segment = new double[6];
    while (!it.isDone()) {
      int type = it.currentSegment(segment);
      writeSegment(type,segment);
      it.next();
    };
    pop();
  }
  
  
  private void writeSegment(int type, double[] segment) throws IOException {
    ElementInfo info = new ElementInfo();
    for (int i=0;i<SEG_SIZES[type]/2;i++) {
      info.put("x"+i, segment[i*2+0]);
      info.put("y"+i, segment[i*2+1]);
    }
    push(SEG_NAMES[type],info,true);
  }
  
  
  
  private void writeVertices(EditableGraph g) throws IOException {
    push("vertices",null,false);
    for (EditableVertex vertex : g.getVertices()) 
      writeVertex(vertex);
    pop();
  }
  
  
  private void writeVertex(EditableVertex v) throws IOException {
    
    
    ElementInfo info = new ElementInfo();
    info.put("id", getId(v));

    Object content = v.getContent();
    if (content!=null)
      info.put("c", content.toString());

    push("vertex",info,false);

    
    Shape shape = v.getShape();
    writeShape("shape", shape);
    
    Shape original = v.getOriginalShape();
    if (!shape.equals(original))
      writeShape("original", original);
    
    pop();

    
  }
  
  
  private int getId(Object element) {
    
    Integer result = element2id.get(element);
    if (result==null) {
      result = element2id.size()+1;
      element2id.put(element, result);
    }
    
    return result;
  }

  
  private void push(String tag, ElementInfo info, boolean close) {
    StringBuffer b = new StringBuffer();
    b.append('<').append(tag);
    if (info!=null) info.append(b);
    if (close) {
      write(b.append("/>").toString());
    } else {
      write(b.append('>').toString());
      stack.push(tag);
    }
  }
  
  
  private void pop() {
    write("</"+stack.pop()+">");
  }
  
  
  private void write(String txt) {
    out.print(EMPTY.substring(0,stack.size()));
    out.println(txt);
  }

  
  public static class ElementInfo {
    private ArrayList<String> list = new ArrayList<String>(6);
    public void put(String key, double val) {
      list.add(key);
      list.add(Double.toString(val));
    }    
    public void put(String key, int val) {
      list.add(key);
      list.add(Integer.toString(val));
    }    
    public void put(String key, String val) {
      if (val==null)
        return;
      list.add(key);
      list.add(val);
    }    
    public void append(StringBuffer b) {
      Iterator<String> it = list.iterator();
      while (it.hasNext()) {
        String key = it.next();
        String val = it.next();
        b.append(' ').append(key).append("=\"").append(val).append("\"");
      }
    }
  } 

} 

