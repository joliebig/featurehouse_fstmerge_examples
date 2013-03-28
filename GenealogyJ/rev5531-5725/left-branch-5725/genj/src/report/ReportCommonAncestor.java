
import genj.gedcom.Context;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.report.Report;
import genj.view.ViewContext;
import genj.view.ViewContext.ContextList;

import java.util.ArrayList;
import java.util.List;


public class ReportCommonAncestor extends Report {

  
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

  
  public ContextList start(Indi indi) {
    
    Indi other = (Indi)getEntityFromUser(translate("select"), indi.getGedcom(), Gedcom.INDI);
    if (other==null)
      return null;
    
    return start(new Indi[] { indi, other});
  }

  
  public ContextList start(Indi[] indis) {

    
    Indi indi = indis[0];
    Indi other = indis[1];

    
    Indi ancestor = getCommonAncestor(indi, other);

    
    if (ancestor==null) {
      getOptionFromUser(translate("nocommon"), Report.OPTION_OK);
      return null;
    }

    
    ContextList result = new ContextList(indi.getGedcom(), getName());
    result.add(new ViewContext(translate("result.first", indi), new Context(indi)));
    result.add(new ViewContext(translate("result.second", other), new Context(other)));
    result.add(new ViewContext(translate("result.ancestor", ancestor), new Context(ancestor)));
    return result;
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
