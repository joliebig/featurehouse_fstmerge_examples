
package genj.gedcom;


public class PropertyRepository extends PropertyXRef {

  
  private String repository;
  
  
  public PropertyRepository(String tag) {
    super(tag);
    assertTag("REPO");
  }

  
  public void link() throws GedcomException {

    
    Repository repository = (Repository)getCandidate();

    
    PropertyForeignXRef fxref = new PropertyForeignXRef();
    repository.addProperty(fxref);

    
    link(fxref);

    
  }

  
  public String getTargetType() {
    return Gedcom.REPO;
  }
  
  
  public boolean isValid() {
    
    return true;
  }
  
} 
