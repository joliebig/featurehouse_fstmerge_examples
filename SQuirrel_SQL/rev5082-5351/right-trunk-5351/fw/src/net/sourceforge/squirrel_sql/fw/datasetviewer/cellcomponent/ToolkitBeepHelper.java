
package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import java.awt.Component;

public class ToolkitBeepHelper implements IToolkitBeepHelper
{

	public void beep(Component component)
	{
		component.getToolkit().beep();
	}

}
