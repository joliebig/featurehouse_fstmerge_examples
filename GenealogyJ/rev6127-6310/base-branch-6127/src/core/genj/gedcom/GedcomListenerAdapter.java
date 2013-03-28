
package genj.gedcom;


public class GedcomListenerAdapter implements GedcomListener, GedcomMetaListener {
  
  public void gedcomHeaderChanged(Gedcom gedcom) {
  }
  
  public void gedcomWriteLockAcquired(Gedcom gedcom) {
  }
  
  public void gedcomWriteLockReleased(Gedcom gedcom) {
  }
  
  public void gedcomBeforeUnitOfWork(Gedcom gedcom) {
  }
  
  public void gedcomAfterUnitOfWork(Gedcom gedcom) {
  }

  public void gedcomEntityAdded(Gedcom gedcom, Entity entity) {
  }

  public void gedcomEntityDeleted(Gedcom gedcom, Entity entity) {
  }
  
  public void gedcomPropertyChanged(Gedcom gedcom, Property property) {
  }
  
  public void gedcomPropertyAdded(Gedcom gedcom, Property property, int pos, Property added) {
  }
  
  public void gedcomPropertyDeleted(Gedcom gedcom, Property property, int pos, Property removed) {
  }
  
} 
