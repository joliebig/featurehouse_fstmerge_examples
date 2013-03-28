
package genj.gedcom;


public interface GedcomMetaListener extends GedcomListener {

  public void gedcomHeaderChanged(Gedcom gedcom);
  
  public void gedcomWriteLockAcquired(Gedcom gedcom);
  
  public void gedcomBeforeUnitOfWork(Gedcom gedcom);
  
  public void gedcomAfterUnitOfWork(Gedcom gedcom);
  
  public void gedcomWriteLockReleased(Gedcom gedcom);
  
}
