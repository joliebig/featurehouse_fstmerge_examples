

package genj.plugin.sosa;


import genj.gedcom.Gedcom;
import genj.util.swing.Action2;
import genj.util.Resources;
import genj.window.WindowManager;
import genj.gedcom.Indi;

import genj.common.SelectEntityWidget;

import java.util.logging.Logger;



public class MenuActionSETorCHANGE extends Action2 {

	private String menuItem;
	private boolean status;
	private Gedcom gedcom;

	private Indi sosaRoot;

	private SosaIndexation sosaIndexation;

	private MenuActionGET menuActionGetIndividualFromIndex;
	private MenuActionREMOVE menuActionRemoveAllIndexation;
	private Logger LOG = Logger.getLogger("genj.plugin.sosa");

	private final Resources RESOURCES = Resources.get(this);

	public enum myMenuEnum {

		
		SET(SosaPlugin.SOSA_SET), CHANGE(SosaPlugin.SOSA_CHANGE);

		private String item;

		
		myMenuEnum(String item) {
			this.item = item;
		}

		
		public String getItem() {
			return item;
		}

		
		public static myMenuEnum valueFor(String item) {
			for (myMenuEnum instance : values()) {
				if (instance.item.equals(item)) {
					return instance;
				}
			}
			return null;
		}
	}

	
	public void setSosaIndexationValue(SosaIndexation indexation) {
		LOG.fine("sosaIndexation= " + indexation.toString());
		this.sosaIndexation=indexation;
	}

	
	public void setInstanceOfGetIndividualFromSosaIndexMenuAction(MenuActionGET instance) {
		menuActionGetIndividualFromIndex=instance;
	}

	
	public void setInstanceOfRemoveIndexationMenuAction(MenuActionREMOVE instance) {
		 menuActionRemoveAllIndexation=instance;
	}

	
	public MenuActionSETorCHANGE(String menuItem, boolean status, SosaIndexation sosaIndexation, Gedcom gedcom) {
		
		this.status = status;
		this.sosaIndexation = sosaIndexation;
		this.gedcom = gedcom;
		LOG.fine("Set menu item = " + menuItem);
		
		setString(menuItem);
	}

	
	public void setString(String menuItem) {
		this.menuItem = menuItem;
		setText(RESOURCES.getString(menuItem));
	}
	
	
	public void	setVisibilityStatus(boolean status) {
		this.status=status;
	}

	
	public boolean getVisibilityStatus() {
		return status;
	}

	
	protected void execute() {
		LOG.fine("Passe SOSA_SETorCHANGE");
			
			SelectEntityWidget select = new SelectEntityWidget(gedcom,
					Gedcom.INDI, null);
			int rc;
			
			switch (myMenuEnum.valueFor(menuItem)) {
			case SET:
				if (sosaIndexation == null) {
					LOG.fine("Serious problem !");
				}
				
				rc = WindowManager.getInstance(getTarget()).openDialog(null,
						"Select Sosa Root", WindowManager.QUESTION_MESSAGE,
						select, Action2.okCancel(), getTarget());
				if (rc != 0) {
					LOG.fine("No selection");
				} else {
					sosaRoot = (Indi) select.getSelection();
					
					sosaIndexation.setSosaRoot(sosaRoot);
					
					sosaIndexation.setSosaGedcom(gedcom);
					
					sosaIndexation.setSosaIndexation(sosaRoot);
					LOG.fine("Indexation Sosa construite with :"
							+ sosaRoot.toString());
					
					LOG.fine("passe ici***");
					setString(myMenuEnum.CHANGE.getItem());
					menuActionGetIndividualFromIndex.setVisibilityStatus(true);
					menuActionGetIndividualFromIndex.setSosaIndexationValue(sosaIndexation);
					menuActionGetIndividualFromIndex.setEnabled(true);
					menuActionRemoveAllIndexation.setVisibilityStatus(true);
					menuActionRemoveAllIndexation.setSosaIndexationValue(sosaIndexation);
					menuActionRemoveAllIndexation.setEnabled(true);
				}
				break;
			case CHANGE:
				
				LOG.fine("Need here ask for DeCujus");
				rc = WindowManager.getInstance(getTarget()).openDialog(null,
						"Select Sosa Root", WindowManager.QUESTION_MESSAGE,
						select, Action2.okCancel(), getTarget());
				sosaRoot = rc == 0 ? (Indi) select.getSelection() : null;
				if (sosaRoot != null) {
					LOG.fine("Sosa root=" + sosaRoot.toString());
					if (sosaRoot != sosaIndexation.getSosaRoot()) {
						
						sosaIndexation.removeSosaIndexationFromIndi(
								sosaIndexation.getSosaRoot(), 1);
						
						sosaIndexation.setSosaRoot(sosaRoot);
						
						sosaIndexation.setSosaGedcom(gedcom);
						
						sosaIndexation.setSosaIndexation(sosaRoot);
						LOG.fine("Indexation Sosa built with :"
								+ sosaRoot.toString());
					}
				}
				break;
			}
	}
}
