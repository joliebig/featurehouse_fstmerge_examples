
package genj.gedcom;



public class PropertyMedia extends PropertyXRef {

  
   Property init(MetaProperty meta, String value) throws GedcomException {
    
    meta.assertTag("OBJE");
    
    if (value.startsWith("@")&&value.endsWith("@"))
      return super.init(meta,value);
    
    return new PropertySimpleReadOnly().init(meta, value);
  }


  
  public String getTag() {
    return "OBJE";
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
