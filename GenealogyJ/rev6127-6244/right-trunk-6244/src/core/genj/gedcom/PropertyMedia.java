
package genj.gedcom;



public class PropertyMedia extends PropertyXRef {
  
  
   PropertyMedia(String tag) {
    super(tag);
    assertTag("OBJE");
  }

   PropertyMedia() {
    super("OBJE");
  }

  
  public void link() throws GedcomException {

    
    Media media = (Media)getCandidate();

    
    PropertyForeignXRef fxref = new PropertyForeignXRef();
    media.addProperty(fxref);

    
    link(fxref);

    

  }
  
  
  public String getTargetType() {
    return Gedcom.OBJE;
  }

} 
