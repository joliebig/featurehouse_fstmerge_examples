
package genj.edit.actions;

import genj.gedcom.Context;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.MetaProperty;
import genj.gedcom.Property;
import genj.util.swing.Action2;
import genj.window.WindowManager;

import java.awt.event.ActionEvent;
import java.util.Collection;


public class TogglePrivate extends AbstractChange {
  
  
  private Collection<? extends Property> properties;
  
  
  private boolean makePrivate;
  
  
  public TogglePrivate(Gedcom gedcom, Collection<? extends Property> properties) {
    super(gedcom, MetaProperty.IMG_PRIVATE, "");
    this.gedcom = gedcom;
    this.properties = properties;
    
    
    makePrivate = true;
    for (Property p : properties)
      if (p.isPrivate()) makePrivate = false;
    setText(resources.getString(makePrivate?"private":"public"));
  }
  
  protected Context execute(Gedcom gedcom, ActionEvent event) throws GedcomException {
    
    
    String pwd = gedcom.getPassword();
    if (pwd==Gedcom.PASSWORD_UNKNOWN) {
        WindowManager.getInstance().openDialog(null,getText(),WindowManager.WARNING_MESSAGE,"This Gedcom file contains encrypted information that has to be decrypted before changing private/public status of other information",Action2.okOnly(),event);
        return null;              
    }
      
    
    if (pwd==null) {
      
      pwd = WindowManager.getInstance().openDialog(
        (String)null,
        getText(),
        WindowManager.QUESTION_MESSAGE,
        AbstractChange.resources.getString("password", gedcom.getName()),
        "",
        event 
      );
      
      
      if (pwd==null)
        return null;
    }

    
    int recursive = WindowManager.getInstance().openDialog(null,getText(),WindowManager.QUESTION_MESSAGE,AbstractChange.resources.getString("recursive"), Action2.okCancel(),event);

    
    gedcom.setPassword(pwd); 
    
    for (Property p : properties)
      p.setPrivate(makePrivate, recursive==0);

    
    return null;
  }
  
} 

