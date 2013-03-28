
package genj.view;

import javax.swing.Action;
import javax.swing.JComponent;


public interface ToolBar {

	public void add(Action action);
	
	public void add(JComponent component);
	
	public void addSeparator();
	
	public void beginUpdate();
	
	public void endUpdate();
}
