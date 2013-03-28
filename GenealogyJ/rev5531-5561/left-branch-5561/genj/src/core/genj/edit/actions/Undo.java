
package genj.edit.actions;

import java.awt.event.ActionEvent;

import genj.edit.Images;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomMetaListener;
import genj.gedcom.Property;
import genj.util.swing.Action2;

  
public class Undo extends Action2 implements GedcomMetaListener {
  
  
  private Gedcom gedcom;
  
  
  public Undo(Gedcom gedcom) {
    this(gedcom, gedcom.canUndo());
    setText(null);
  }

  
  public Undo(Gedcom gedcom, boolean enabled) {
    
    
    setImage(Images.imgUndo);
    setText(AbstractChange.resources.getString("undo"));    
    setTip(getText());
    setEnabled(enabled);
    
    
    this.gedcom = gedcom;
    
  }

  
  public void actionPerformed(ActionEvent event) {
    if (gedcom.canUndo())
      gedcom.undoUnitOfWork();
  }
  
  public void gedcomEntityAdded(Gedcom gedcom, Entity entity) {
    
  }

  public void gedcomEntityDeleted(Gedcom gedcom, Entity entity) {
    
  }

  public void gedcomPropertyAdded(Gedcom gedcom, Property property, int pos, Property added) {
    
  }

  public void gedcomPropertyChanged(Gedcom gedcom, Property prop) {
    
  }

  public void gedcomPropertyDeleted(Gedcom gedcom, Property property, int pos, Property removed) {
    
  }

  public void gedcomHeaderChanged(Gedcom gedcom) {
    
  }

  public void gedcomBeforeUnitOfWork(Gedcom gedcom) {
    
  }
  
  public void gedcomAfterUnitOfWork(Gedcom gedcom) {
    
  }

  public void gedcomWriteLockAcquired(Gedcom gedcom) {
    
  }

  public void gedcomWriteLockReleased(Gedcom gedcom) {
    setEnabled(gedcom.canUndo());
  }
} 

