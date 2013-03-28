
package gj.example;

import gj.example.ftree.FamilyTree;
import gj.example.inheritance.InheritanceTree;
import gj.example.treemodel.SwingTree;
import gj.ui.GraphWidget;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class Main {
  
  private final static Example[] EXAMPLES = {
    new FamilyTree(),
    new InheritanceTree(),
    new SwingTree()
  };

  
  public static void main(String[] args) {
    
    
    JTabbedPane pane = new JTabbedPane();

    
    for (int i = 0; i < EXAMPLES.length; i++) {
      prepare(EXAMPLES[i], pane);
    }
    
    
    JFrame frame = new JFrame("GraphJ - Examples");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.getContentPane().add(pane);
    frame.setSize(new Dimension(640,480));
    frame.setVisible(true);

  }
  
  private static void prepare(Example example, JTabbedPane pane) {
    
    
    pane.addTab(example.getName(), new JScrollPane(example.prepare(new GraphWidget())));

  }

}
