
package genj.gedcom;



public class PropertySubmitter extends PropertyXRef {
  
  
  public PropertySubmitter(String tag) {
    super(tag);
  }
  
  
  public void link() throws GedcomException {

    
    Submitter subm = (Submitter)getCandidate();

    
    PropertyForeignXRef fxref = new PropertyForeignXRef();
    subm.addProperty(fxref);

    
    link(fxref);

    
  }

  
  public String getTargetType() {
    return Gedcom.SUBM;
  }
  
  
  public boolean isValid() {
    
    return true;
  }

} 

