
package gj.example.ftree;

import gj.example.Example;
import gj.layout.Graph2D;
import gj.layout.LayoutException;
import gj.layout.graph.tree.TreeLayout;
import gj.model.Graph;
import gj.ui.GraphWidget;
import gj.util.DefaultGraph;
import gj.util.DefaultLayoutContext;
import gj.util.TreeGraphAdapter;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;


public class FamilyTree implements Example {
  
  
  public String getName() {
    return "Family Tree";
  }
  
  
  public JComponent prepare(GraphWidget widget) {
    
    
    final String family = 
      "L&M>L&A,"+
      "L&M>S&S,"+
      "L&M>Nils,"+
      "L&A>Yaro,"+
      "S&S>Jonas,"+
      "S&S>Alisa,"+
      "S&S>Luka";

    
    TreeGraphAdapter.Tree<String> tree = new TreeGraphAdapter.Tree<String>() {
      public List<String> getChildren(String parent) {
        List<String> result = new ArrayList<String>();
        for (String relationship : family.split(",")) {
          if (relationship.startsWith(parent+">"))
            result.add(relationship.substring(parent.length()+1));
        }
        return result;
      }
      public String getParent(String child) {
        for (String relationship : family.split(",")) {
          if (relationship.endsWith(">"+child))
            return relationship.substring(0, relationship.length()-child.length()-1);
        }
        return null;
      }
      public String getRoot() {
        return family.substring(0, family.indexOf('>'));
      }
    };

    Graph graph = new TreeGraphAdapter<String>(tree);
    
    
    Graph2D graph2d = new DefaultGraph(graph, new Rectangle2D.Double(-20,-16,40,32));
    
    try {
      new TreeLayout().apply(graph2d, new DefaultLayoutContext());
    } catch (LayoutException e) {
      throw new RuntimeException("hmm, can't layout my family", e);
    }
    
    
    widget.setGraph2D(graph2d);
    
    
    return widget;
  }

}
