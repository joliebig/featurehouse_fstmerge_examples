
package genj.gedcom;



public class PropertySubmitter extends PropertyXRef {
  
  private String tag;

  
  Property init(MetaProperty meta, String value) throws GedcomException {
    this.tag = meta.getTag();
    return super.init(meta, value);
  }

  
  public String getTag() {
    return tag;
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

