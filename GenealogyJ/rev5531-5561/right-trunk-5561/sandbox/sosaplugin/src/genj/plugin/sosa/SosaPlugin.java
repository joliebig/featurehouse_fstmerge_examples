

package genj.plugin.sosa;


import genj.app.ExtendGedcomClosed;
import genj.app.ExtendGedcomOpened;
import genj.app.ExtendMenubar;
import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomLifecycleEvent;
import genj.gedcom.GedcomLifecycleListener;
import genj.gedcom.GedcomListener;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.PropertyChild;
import genj.gedcom.PropertyFamilyChild;
import genj.gedcom.PropertyFamilySpouse;
import genj.gedcom.PropertyHusband;
import genj.gedcom.PropertyWife;
import genj.plugin.ExtensionPoint;
import genj.plugin.Plugin;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.ImageIcon;
import genj.view.ExtendContextMenu;

import java.util.logging.Logger;


public class SosaPlugin implements Plugin, GedcomLifecycleListener, GedcomListener {

	static final String SOSA_MENU = "Sosa indexation";

	static String SOSA_INFORMATION="Information...";
	static String SOSA_SET="Set indexation with...";
	static String SOSA_CHANGE="Change indexation to...";
	static String SOSA_GET="Get individual from index...";
	static String SOSA_REMOVE="Remove all indexation...";
	private boolean fileRecordedDataFlag = true;

	
	private final ImageIcon IMG = new ImageIcon(this, "/Sosa.gif");

	
	private final Resources RESOURCES = Resources.get(this);

	private Logger LOG = Logger.getLogger("genj.plugin.sosa");

	private Registry sosaRegistry;

	private ExtendMenubar menuSosa;

	private MenuActionSETorCHANGE menuActionSetOrChangeIndexation;
	private MenuActionGET menuActionGetIndividualFromIndex;
	private MenuActionREMOVE menuActionRemoveAllIndexation;
	private boolean visibilityStatus;

	private Indi sosaRoot;

	
	private SosaIndexation sosaIndexation;

	
	
	
	private Indi _toIndi;

	private Indi _fromIndi;

	private Fam _fromFam;

	
	private String _sosaValue;

	private Property _sosaProperty;

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	Indi _indi;

	Fam _fam;

	
	
	
	
	private enum interactionType {
		_NULL, _SOSACutFromINDI, _SOSAAddedToINDI, _SOSAModifiedInINDI, _SOSADeletedFromINDI, _SOSASetValueToINDI, _CHILCutFromFAM, _CHILAddedToFAM, _newINDIInFAM, _newFAM
	};

	private interactionType action = interactionType._NULL;

	
	public void extend(ExtensionPoint ep) {
		
		LOG.fine("EP= :"+ep);

		if (ep instanceof ExtendMenubar) {
			
			
			
			Gedcom gedcom = ((ExtendMenubar) ep).getGedcom();
			LOG.fine("A : ON INSTALLE LES MENUS DU GEDCOM  avec gedcom = "+gedcom);
			menuSosa = (ExtendMenubar) ep;
			LOG.fine("A-1 : installation menuSosa = " + menuSosa);
			
			
				LOG.fine("A-1a : Addition of Info sub-menu");
				
				menuSosa.addAction(SOSA_MENU, new Action2(RESOURCES.getString("Info"), true));
				
				LOG.fine("A-1b : Addition of menuActionSetOrChangeIndexation menu-item");
				visibilityStatus=false;
				
				menuActionSetOrChangeIndexation = new MenuActionSETorCHANGE(SOSA_SET, visibilityStatus, null, gedcom);
				
				menuSosa.addAction(SOSA_MENU, menuActionSetOrChangeIndexation);
				
				menuActionSetOrChangeIndexation.setEnabled(false);
				LOG.fine("A-1c : Addition of menuActionGetIndividualFromIndex sub-menu");
				
				menuActionGetIndividualFromIndex = new MenuActionGET(SOSA_GET, visibilityStatus, null);
				
				menuSosa.addAction(SOSA_MENU, menuActionGetIndividualFromIndex);
				 
				menuActionGetIndividualFromIndex.setEnabled(false);
				LOG.fine("A-1d : Addition of menuActionRemoveAllIndexation sub-menu");
				
				LOG.fine("A-1e : Addition of MenuActionREMOVE sub-menu");
				menuActionRemoveAllIndexation = new MenuActionREMOVE(SOSA_REMOVE, visibilityStatus, null);
				menuActionRemoveAllIndexation.setInstanceOfSetOrChangeIndexationMenuAction(menuActionSetOrChangeIndexation);
				menuActionRemoveAllIndexation.setInstanceOfGetIndividualFromSosaIndexMenuAction(menuActionGetIndividualFromIndex);
				
				menuSosa.addAction(SOSA_MENU, menuActionRemoveAllIndexation);
				
				menuActionRemoveAllIndexation.setEnabled(false);
		}

		if (ep instanceof ExtendGedcomOpened) {
			
		    Gedcom gedcom = ((ExtendGedcomOpened)ep).getGedcom();
			LOG.fine("B : ON ATTACHE LE GEDCOM :"+gedcom);
			LOG.fine("Flag= " + isExtendSosaIndexation());
			
			gedcom.addLifecycleListener(this);
			gedcom.addGedcomListener(this);
			LOG.fine("B-1-Ouverture Plugin");
			LOG.fine("B-2-Vérification sosa.root");
			sosaRegistry = new Registry();
			sosaRegistry = genj.util.Registry.lookup(gedcom);
			
			String registryValue = sosaRegistry.get("sosa.root", (String) null);
			LOG.fine("sosaRegistry = "+registryValue);
			
			
			
			if (registryValue == null) {
				
				LOG.fine("B-2a-Première installation : pas d'indexation Sosa");
				
				sosaRoot = null;
				
				
				sosaIndexation = new SosaIndexation(sosaRoot, gedcom);
				menuActionSetOrChangeIndexation.setSosaIndexationValue(sosaIndexation);
				
				menuActionSetOrChangeIndexation.setString(SOSA_SET);
				
				menuActionSetOrChangeIndexation.setEnabled(true);
				
				menuActionGetIndividualFromIndex.setEnabled(false);
				
				menuActionRemoveAllIndexation.setEnabled(false);
				menuActionSetOrChangeIndexation.setInstanceOfGetIndividualFromSosaIndexMenuAction(menuActionGetIndividualFromIndex);
				menuActionSetOrChangeIndexation.setInstanceOfRemoveIndexationMenuAction(menuActionRemoveAllIndexation);
				
				
				
			} else {
				LOG.fine("B-2b-on a une indexation Sosa");
				
				if (registryValue.equals("")) {
					
					
					
					
					LOG.fine("B-2ba : Pas d'indexation Sosa");
					
					menuActionSetOrChangeIndexation.setString(SOSA_SET);
					
					menuSosa.addAction(SOSA_MENU,menuActionSetOrChangeIndexation);
					
				} else {
					
					LOG.fine("B-2bb : sosa.root = " + registryValue);
					
					sosaRoot = (Indi) gedcom.getEntity(Gedcom.INDI,	registryValue.substring(registryValue.lastIndexOf("(") + 1, registryValue.lastIndexOf(")")));
					LOG.fine("Sosa root=" + sosaRoot);
					
					boolean setIndexationFlag;
					if (fileRecordedDataFlag) {
						LOG.fine("B-2bba Enregistrement indexation Sosa = "+ fileRecordedDataFlag);
						LOG.fine("Rien à faire");
						setIndexationFlag = false;
					} else {
						LOG.fine("B-2bbb Enregistrement indexation Sosa = "+ fileRecordedDataFlag);
						
						LOG.fine("Indexation Sosa construite");
						LOG.fine("We have to set Sosa from DeCuJus");
						setIndexationFlag = true;
					}
					
					sosaIndexation = new SosaIndexation(sosaRoot, gedcom);
					
					LOG.fine("Addition of menuActionSetOrChangeIndexation sub-menu");
					menuActionSetOrChangeIndexation.setString(SOSA_CHANGE);
					menuActionSetOrChangeIndexation.setSosaIndexationValue(sosaIndexation);
					menuActionSetOrChangeIndexation.setEnabled(true);
					menuActionGetIndividualFromIndex.setSosaIndexationValue(sosaIndexation);
					menuActionGetIndividualFromIndex.setEnabled(true);
					menuActionRemoveAllIndexation.setSosaIndexationValue(sosaIndexation);
					menuActionRemoveAllIndexation.setEnabled(true);
					menuActionSetOrChangeIndexation.setInstanceOfGetIndividualFromSosaIndexMenuAction(menuActionGetIndividualFromIndex);
					menuActionSetOrChangeIndexation.setInstanceOfRemoveIndexationMenuAction(menuActionRemoveAllIndexation);
				}
			}
			
			return;
		}

		if (ep instanceof ExtendGedcomClosed) {
			
			Gedcom gedcom = ((ExtendGedcomClosed) ep).getGedcom();
			
			LOG.fine("C : ON DÉTACHE LE GEDCOM :"+gedcom);
			
			sosaRoot=sosaIndexation.getSosaRoot();
			LOG.fine("sosaRoot=" + sosaRoot);
			LOG.fine("sosaRegistry=" + sosaRegistry);
			sosaRegistry.put("sosa.root", sosaRoot.toString());
			
			
			if (!fileRecordedDataFlag) {
				
				sosaIndexation.removeSosaIndexationFromAllIndis();
				LOG.fine("Pas de sauvegarde des index");
			}
			gedcom.removeLifecycleListener(this);
			gedcom.removeGedcomListener(this);
			LOG.fine("Fermeture gedcom="+gedcom);
			
				
				LOG.fine("PASSE********");
				menuActionSetOrChangeIndexation.setEnabled(false);
				menuActionGetIndividualFromIndex.setEnabled(false);
				menuActionRemoveAllIndexation.setEnabled(false);
			
			return;
		}

		if (ep instanceof ExtendContextMenu) {
			
			
			ExtendContextMenu _menuSosa = (ExtendContextMenu) ep;
			LOG.fine("passe dans ExtendContextMenu");
		}


	}

	public void handleLifecycleEvent(GedcomLifecycleEvent event) {
		
		
		
		
		
		
		LOG.fine("Lifecycle event ID = " + event.getId());
		if (event.getId() == GedcomLifecycleEvent.AFTER_UNIT_OF_WORK) {
			switch (action) {
			case _CHILCutFromFAM:
				sosaIndexation.restoreSosaInChildCutFromFam(_toIndi, _fromFam);
				action = interactionType._NULL;
				break;
			case _CHILAddedToFAM:
				sosaIndexation.restoreSosaInChildAddedToFam(_toIndi, _fromFam);
				action = interactionType._NULL;
				break;
			case _SOSACutFromINDI:
				action = interactionType._SOSAAddedToINDI;
				sosaIndexation.restoreSosaValueToIndi(_fromIndi, _sosaValue);
				action = interactionType._NULL;
				break;
			case _SOSAAddedToINDI:
				action = interactionType._SOSACutFromINDI;
				sosaIndexation.deleteExistingSosaIndexFromIndi(_toIndi, _sosaProperty);
				action = interactionType._NULL;
				break;
			case _SOSADeletedFromINDI:
				_toIndi.delProperty(_sosaProperty);
				action = interactionType._NULL;
				break;
			case _SOSASetValueToINDI:
				
				LOG.fine("passe ici coucou");
				_sosaProperty.setValue(_sosaValue);
				action = interactionType._NULL;
				break;
			case _newINDIInFAM:
				
				action = interactionType._NULL;
				break;
			case _newFAM:
				
				action = interactionType._NULL;
				break;
			default:
				LOG.fine("2- Lifecycle event ID = " + event.getId());
				break;
			}
		}
	}

	public void gedcomPropertyLinked(Gedcom gedcom, Property from, Property to) {
		LOG.fine("Link Property from : " + from.getValue());
		LOG.fine("Link Property to : " + to.getValue());
		LOG.fine("Link Property from : " + from.getEntity());
		LOG.fine("Link Property to : " + to.getEntity());
		if (from instanceof PropertyChild) {
			
			_toIndi = (Indi) to.getEntity();
			_fromFam = (Fam) from.getEntity();
			action = interactionType._CHILAddedToFAM;
		} else {
			if (from instanceof PropertyFamilyChild) {
				
				LOG.fine("PASS:PropertyFamilyChild");
			} else {
				if (from instanceof PropertyFamilySpouse) {
					
					LOG.fine("PASS:PropertyFamilySpouse");
				} else {
					if (from instanceof PropertyHusband) {
						
						LOG.fine("PASS:PropertyHusband");
					} else {
						if (from instanceof PropertyWife) {
							
							LOG.fine("PASS:PropertyWife");
						}
					}
				}
			}
		}
	}

	public void gedcomPropertyUnlinked(Gedcom gedcom, Property from, Property to) {
		LOG.fine("Unlink Property from : " + from.getValue());
		LOG.fine("Unlink Property to : " + to.getValue());
		LOG.fine("Unlink Property from : " + from.getEntity());
		LOG.fine("Unlink Property to : " + to.getEntity());
		action = interactionType._NULL;
		if (from instanceof PropertyChild) {
			
			_toIndi = (Indi) to.getEntity();
			_fromFam = (Fam) from.getEntity();
			action = interactionType._CHILCutFromFAM;
		} else {
			if (from instanceof PropertyFamilyChild) {
				
				LOG.fine("PASS:PropertyFamilyChild");
			} else {
				if (from instanceof PropertyFamilySpouse) {
					
					LOG.fine("PASS:PropertyFamilySpouse");
				} else {
					if (from instanceof PropertyHusband) {
						
						LOG.fine("PASS:PropertyHusband");
					} else {
						if (from instanceof PropertyWife) {
							
							LOG.fine("PASS:PropertyWife");
						}
					}
				}
			}
		}
	}

	

	public void gedcomEntityAdded(Gedcom gedcom, Entity entity) {
		
		
		LOG.fine("Entity added : " + entity);
		if (entity.getTag().equals(Gedcom.INDI)) {

			
			
			action = interactionType._newINDIInFAM;
		} else {
			if (entity.getTag().equals(Gedcom.FAM)) {
				
				
				
				action = interactionType._newFAM;
			}
		}
	}

	

	public void gedcomEntityDeleted(Gedcom gedcom, Entity entity) {
		
		LOG.fine("Entity deleted : " + entity);
	}

	

	public void gedcomPropertyAdded(Gedcom gedcom, Property property, int pos,
			Property added) {
		
		
		String propertyTag = property.getTag();
		String addedTag = added.getTag();
		
		LOG.fine("((addedTag.equals(\"" + addedTag
				+ "\")) && (propertyTag.equals(\"" + propertyTag + "\")))");
		
		
		if (addedTag.equals("_SOSA") && (propertyTag.equals("INDI"))) {
			
			switch (action) {
			case _NULL:
				boolean b = false;
				if (b) {
					LOG
							.fine("1 - Sorry addition of _SOSA tag is not possible !");
					_toIndi = (Indi) added.getEntity();
					_sosaProperty = added;
					
					action = interactionType._SOSAAddedToINDI;
				} else {
					
					LOG
							.fine("1 - Sorry addition of _SOSA tag is not possible !");
					_toIndi = (Indi) added.getEntity();
					_sosaProperty = added;
					
					action = interactionType._SOSAAddedToINDI;
				}
				break;
			case _SOSAAddedToINDI:
				LOG.fine("Addition is ok");
				break;
			default:
				LOG.fine("Cut action is cancelled");
				break;
			}
		}
		
	}

	

	public void gedcomPropertyChanged(Gedcom gedcom, Property property) {

		
		LOG.fine("sosa indexation " + isExtendSosaIndexation());
		LOG.fine("Property modified = " + property.getTag());
		LOG.fine("Property value = " + property.getValue());
		if (property.getTag().equals("_SOSA")) {
			LOG.fine("_SOSA modified");
			switch (action) {
			case _SOSAModifiedInINDI:
				
				action = interactionType._NULL;
				LOG.fine("_SOSA modification : confirmed");
				break;
			default:
				LOG.fine("_SOSA : passe ici");
				break;
			}
		}
	}

	

	public void gedcomPropertyDeleted(Gedcom gedcom, Property property,
			int pos, Property deleted) {
		
		String propertyTag = property.getTag();
		String deletedTag = deleted.getTag();
		LOG.fine("((deletedTag.equals(\"" + deletedTag
				+ "\")) && (propertyTag.equals(\"" + propertyTag + "\")))");
		
		
		if (deletedTag.equals("_SOSA") && (propertyTag.equals("INDI"))) {
			
			switch (action) {
			case _NULL:
				LOG.fine("Sorry cut of _SOSA tag is not possible !");
				_fromIndi = (Indi) deleted.getEntity();
				_sosaValue = deleted.getValue();
				action = interactionType._SOSACutFromINDI;
				break;
			case _SOSADeletedFromINDI:
				LOG.fine("_SOSA removal is confirmed");
				action = interactionType._NULL;
				break;
			default:
				LOG.fine("Add action is cancelled");
				break;
			}
		}
		
	}

	
	private boolean isExtendSosaIndexation() {
		return SosaOptions.getInstance().isExtendSosaIndexation;
	}
}