
package genj.gedcom;

import genj.util.WordBuffer;


public class PropertyAssociation extends PropertyXRef {
  
  
   PropertyAssociation(String tag) {
    super(tag);
    assertTag("ASSO");
  }

  
  public String getDisplayValue() {
    
    
    PropertyXRef target = getTarget();
    if (target==null)
      return super.getDisplayValue();
    
    
    Property parent = target.getParent();
    if (parent==null)
      return super.getDisplayValue();
    
    
    
    WordBuffer result = new WordBuffer(" - ");
    result.append(parent.getEntity());
    
    result.append(Gedcom.getName(parent.getTag()));
    
    Property date = parent.getProperty("DATE");
    if (date!=null)
      result.append(date);
    
    Property place = parent.getProperty("PLAC");
    if (place!=null)
      result.append(place);
    
    
    return result.toString();
  }
  
  
  protected String getForeignDisplayValue() {
    
    Property rela = getProperty("RELA");
    if (rela!=null&&rela.getDisplayValue().length()>0) 
      return rela.getDisplayValue() + ": " + getEntity().toString();
    
    return super.getForeignDisplayValue();
  }
  
  
  public String getDeleteVeto() {
    
    if (getTargetEntity()==null) 
      return null;
    return resources.getString("prop.asso.veto");
  }

  
  public void link() throws GedcomException {

     
    Entity ent = getCandidate();

    
    PropertyForeignXRef fxref = new PropertyForeignXRef();
    try {
      PropertyRelationship rela = (PropertyRelationship)getProperty("RELA");
      ent.getProperty(rela.getAnchor()).addProperty(fxref);
    } catch (Throwable t) {
      ent.addProperty(fxref);
    }

    
    link(fxref);

    
    Property type = getProperty("TYPE");
    if (type==null) type = addProperty(new PropertySimpleValue("TYPE"));
    type.setValue(ent.getTag());

    
  }

  
  public String getTargetType() {
    
    if (!getMetaProperty().allows("TYPE"))
      return Gedcom.INDI;
    
    Property type = getProperty("TYPE");
    if (type!=null)
      return type.getValue();
    
    String prefix = getValue().substring(1,2);
    for (int i = 0; i < Gedcom.ENTITIES.length; i++) {
      if (Gedcom.getEntityPrefix(Gedcom.ENTITIES[i]).startsWith(prefix))
        return Gedcom.ENTITIES[i];
    }
    
    return Gedcom.INDI;
  }
  
} 
