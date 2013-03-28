
package genj.gedcom;


public interface UnitOfWork {
  
  public void perform(Gedcom gedcom) throws GedcomException;

}
