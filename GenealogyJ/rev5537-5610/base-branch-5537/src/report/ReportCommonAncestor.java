
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.report.AnnotationsReport;


public class ReportCommonAncestor extends AnnotationsReport {

  
  public String accepts(Object context) {
    
    if (context instanceof Indi)
      return getName();
    
    if (context instanceof Indi[]) {
      Indi[] indis = (Indi[])context;
      if (indis.length==2)
        return getName();
    }
    
    return null;
  }

  
  public void start(Indi indi) {
    
    Indi other = (Indi)getEntityFromUser(translate("select"), indi.getGedcom(), Gedcom.INDI);
    if (other==null)
      return;
    
    start(new Indi[] { indi, other});
  }

  
  public void start(Indi[] indis) {

    
    Indi indi = indis[0];
    Indi other = indis[1];

    
    Indi ancestor = getCommonAncestor(indi, other);

    
    if (ancestor==null) {
      setMessage(translate("nocommon"));
      return;
    }

    
    addAnnotation(indi, translate("result.first", indi));
    addAnnotation(other, translate("result.second", other));
    addAnnotation(ancestor, translate("result.ancestor", ancestor));
  }

  private Indi getCommonAncestor(Indi indi, Indi other) {
    
    Indi father = indi.getBiologicalFather();
    if (father!=null) {
      if (father.isAncestorOf(other))
        return father;
      Indi ancestor = getCommonAncestor(father, other);
      if (ancestor!=null)
        return ancestor;
    }
    Indi mother = indi.getBiologicalMother();
    if (mother!=null) {
      if (mother.isAncestorOf(other))
        return mother;
      Indi ancestor = getCommonAncestor(mother, other);
      if (ancestor!=null)
        return ancestor;
    }
    
    return null;
  }

}
