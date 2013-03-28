
package gj.example;

import javax.swing.JComponent;

import gj.ui.GraphWidget;


public interface Example {

  public String getName();
  
  public JComponent prepare(GraphWidget widget);
   
}
