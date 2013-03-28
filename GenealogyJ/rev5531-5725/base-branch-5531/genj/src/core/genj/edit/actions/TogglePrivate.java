
package genj.edit.actions;

import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.MetaProperty;
import genj.gedcom.Property;
import genj.util.swing.Action2;
import genj.view.ViewManager;
import genj.window.WindowManager;

import java.util.Collection;
import java.util.Iterator;


public class TogglePrivate extends AbstractChange {
  
  
  private Collection properties;
  
  
  private boolean makePrivate;
  
  
  public TogglePrivate(Gedcom gedcom, Collection properties, ViewManager mgr) {
    super(gedcom, MetaProperty.IMG_PRIVATE, "", mgr);
    this.gedcom = gedcom;
    this.properties = properties;
    
    
    makePrivate = true;
    for (Iterator ps = properties.iterator(); ps.hasNext();) {
      Property p = (Property) ps.next();
      if (p.isPrivate()) makePrivate = false;
    }
    setText(resources.getString(makePrivate?"private":"public"));
  }
  
  public void perform(Gedcom gedcom) throws GedcomException {
    
    
    String pwd = gedcom.getPassword();
    if (pwd==Gedcom.PASSWORD_UNKNOWN) {
        WindowManager.getInstance(getTarget()).openDialog(null,getText(),WindowManager.WARNING_MESSAGE,"This Gedcom file contains encrypted information that has to be decrypted before changing private/public status of other information",Action2.okOnly(),getTarget());
        return;              
    }
      
    
    if (pwd==Gedcom.PASSWORD_NOT_SET) {
      
      pwd = WindowManager.getInstance(getTarget()).openDialog(
        null,
        getText(),
        WindowManager.QUESTION_MESSAGE,
        AbstractChange.resources.getString("password", gedcom.getName()),
        "",
        getTarget() 
      );
      
      
      if (pwd==null)
        return;
    }

    
    int recursive = WindowManager.getInstance(getTarget()).openDialog(null,getText(),WindowManager.QUESTION_MESSAGE,AbstractChange.resources.getString("recursive"), Action2.okCancel(),getTarget());

    
    gedcom.setPassword(pwd); 
    
    for (Iterator ps = properties.iterator(); ps.hasNext();) {
      Property p = (Property) ps.next();
      p.setPrivate(makePrivate, recursive==0);
    }

    
  }
  
} 

