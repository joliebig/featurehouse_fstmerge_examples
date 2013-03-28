
package gj.example.treemodel;

import gj.example.Example;
import gj.layout.Graph2D;
import gj.layout.LayoutException;
import gj.layout.graph.tree.Alignment;
import gj.layout.graph.tree.EdgeLayout;
import gj.layout.graph.tree.TreeLayout;
import gj.model.Edge;
import gj.model.Graph;
import gj.model.Vertex;
import gj.ui.GraphRenderer;
import gj.ui.GraphWidget;
import gj.util.DefaultGraph;
import gj.util.DefaultLayoutContext;
import gj.util.DefaultVertex;
import gj.util.TreeGraphAdapter;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreeNode;


public class SwingTree implements Example {
  
  public String getName() {
    return "SwingTree";
  }

  public JComponent prepare(GraphWidget graphWidget) {
    
    
    final JTree treeWidget = new JTree();
    treeWidget.setVisibleRowCount(4);
    for (int i=0;i<treeWidget.getRowCount();i++)
      treeWidget.expandRow(i); 
    
    
    TreeGraphAdapter.Tree<DefaultVertex<TreeNode>> tree = new TreeGraphAdapter.Tree<DefaultVertex<TreeNode>>() {

      public DefaultVertex<TreeNode> getRoot() {
        return new DefaultVertex<TreeNode>((TreeNode)treeWidget.getModel().getRoot());
      }
      
      public DefaultVertex<TreeNode> getParent(DefaultVertex<TreeNode> child) {
        return new DefaultVertex<TreeNode>( child.getContent().getParent() );
      }
      
      public List<DefaultVertex<TreeNode>> getChildren(DefaultVertex<TreeNode> parent) {
        List<DefaultVertex<TreeNode>> result = new ArrayList<DefaultVertex<TreeNode>>();
        for (int i=parent.getContent().getChildCount(); i>0; i--)
          result.add(new DefaultVertex<TreeNode>(parent.getContent().getChildAt(i-1)));
        return result;
      }
      
    };
    
    final Graph graph = new TreeGraphAdapter<DefaultVertex<TreeNode>>(tree);
    
    
    Graph2D graph2d = new DefaultGraph(graph) {
      @Override
      protected Shape getDefaultShape(Vertex v) {
        boolean leaf = v.getEdges().size() == 1;
        Dimension dim = treeWidget.getCellRenderer().getTreeCellRendererComponent(treeWidget, v, false, false, leaf, 0, false).getPreferredSize();
        return new Rectangle(0, 0, dim.width, dim.height);
      }
    };
    
    try {
      TreeLayout layout = new TreeLayout();
      layout.setDistanceBetweenGenerations(16);
      layout.setDistanceInGeneration(7);
      layout.setOrientation(90);
      layout.setAlignmentOfParents(Alignment.RightOffset);
      layout.setEdgeLayout(EdgeLayout.Orthogonal);
      layout.setOrderSiblingsByPosition(false);
      layout.apply(graph2d, new DefaultLayoutContext());
    } catch (LayoutException e) {
      throw new RuntimeException("hmm, can't layout swing tree model", e);
    }
    
    
    graphWidget.setGraph2D(graph2d);
    graphWidget.setRenderer(new GraphRenderer() {
      public void render(Graph2D graph2d, Graphics2D graphics) {
        
        for (Vertex v : graph.getVertices()) {
          Rectangle r = graph2d.getShape(v).getBounds();
          boolean leaf = v.getEdges().size() == 1;
          Component c = treeWidget.getCellRenderer().getTreeCellRendererComponent(treeWidget, v, false, false, leaf, 0, false);
          c.setBounds(r);
          c.paint(graphics.create(r.x, r.y, r.width, r.height));
        }
        
        graphics.setColor(Color.LIGHT_GRAY);
        for (Edge edge : graph.getEdges()) {
          graphics.draw(graph2d.getRouting(edge));
        }
      }
    });
 
    
    JPanel content = new JPanel(new GridLayout(1,2));
    content.add(new JScrollPane(graphWidget));
    content.add(new JScrollPane(treeWidget));
    
    
    return content;
  }
}
