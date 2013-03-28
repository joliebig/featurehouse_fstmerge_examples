

package genj.plugin.sosa;


import genj.gedcom.Gedcom;
import genj.util.swing.Action2;
import genj.util.Resources;
import genj.window.WindowManager;
import genj.gedcom.Indi;
import genj.util.swing.ChoiceWidget;
import genj.common.SelectEntityWidget;

import java.util.logging.Logger;



public class _GetInformationMenuAction extends Action2 {

	private String menuItem;

	

	

	

	private Logger LOG = Logger.getLogger("genj.plugin.sosa");

	private final Resources RESOURCES = Resources.get(this);


	
	public _GetInformationMenuAction(String menuItem) {
		this.menuItem = menuItem;
		
		
		LOG.fine("Pass icic : Set menu item = " + menuItem);
		setText(RESOURCES.getString(menuItem));
	}

	
	
	
	
	

	
	protected void execute() {
		LOG.fine("Click sur menu item = " + menuItem);
		LOG.fine("Et oui ! = " + menuItem);
		
	}
}
