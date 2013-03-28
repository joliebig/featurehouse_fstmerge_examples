

package genj.plugin.sosa;


import genj.util.swing.Action2;
import genj.util.Resources;

import java.util.logging.Logger;



public class MenuActionREMOVE extends Action2 {

	private String menuItem;

	private boolean status;

	private SosaIndexation sosaIndexation;

	private MenuActionSETorCHANGE menuActionSetOrChangeIndexation;

	private MenuActionGET menuActionGetIndividualFromIndex;

	private Logger LOG = Logger.getLogger("genj.plugin.sosa");

	private final Resources RESOURCES = Resources.get(this);

	
	public MenuActionREMOVE(String menuItem, boolean status, SosaIndexation sosaIndexation) {
		this.status = status;
		this.sosaIndexation = sosaIndexation;
		LOG.fine("Set menu item = " + menuItem);
		setString(menuItem);
	}

	
	public void setString(String menuItem) {
		this.menuItem = menuItem;
		setText(RESOURCES.getString(menuItem));
	}

	
	public void setSosaIndexationValue(SosaIndexation indexation) {
		this.sosaIndexation = indexation;
	}

	
	public void setVisibilityStatus(boolean status) {
		this.status = status;
	}

	
	public boolean getVisibilityStatus() {
		return status;
	}

	
	public void setInstanceOfSetOrChangeIndexationMenuAction(MenuActionSETorCHANGE instance) {
		menuActionSetOrChangeIndexation=instance;
	}

	
	public void setInstanceOfGetIndividualFromSosaIndexMenuAction(MenuActionGET instance) {
		menuActionGetIndividualFromIndex=instance;
	}

	
	protected void execute() {
		LOG.fine("Passe SOSA_REMOVE");
		
		
		if (sosaIndexation == null) {
			LOG.fine("GROS PROBLEM !");
		}
		
		sosaIndexation.removeSosaIndexationFromAllIndis();
		
		menuActionSetOrChangeIndexation.setString(SosaPlugin.SOSA_SET);
		menuActionGetIndividualFromIndex.setEnabled(false);
		menuActionGetIndividualFromIndex.setVisibilityStatus(false);
		status = false;
		setEnabled(status);
	}
}
