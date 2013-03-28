
package gj.shell.factory;

import gj.shell.model.EditableGraph;

import java.awt.geom.Rectangle2D;


public class EmptyFactory extends AbstractGraphFactory {

  
  @Override
  public EditableGraph create(Rectangle2D bounds) {
    return new EditableGraph();
  }

} 
