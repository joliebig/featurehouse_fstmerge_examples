


import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.TagPath;
import genj.report.Report;



public class ReportFamily extends Report {

    public boolean reportParents = true;
    public boolean reportOtherSpouses = true;
    public boolean reportDetailedChildrenData = true;

    
    public void start(Gedcom gedcom) {
      Entity[] fams = gedcom.getEntities(Gedcom.FAM,"");
      for(int i=0; i<fams.length; i++) {
          analyzeFam((Fam)fams[i]);
          println();
          println("=====");
          println();
      }
    }

    
    public void start(Fam fam) {
      analyzeFam(fam);
    }

    private String trim(Object o) {
        if(o == null)
            return "";
        return o.toString();
    }

    private String familyToString(Fam f) {
        Indi husband = f.getHusband(), wife = f.getWife();
        String str = f.getId()+" ";
        if(husband!=null)
            str = str + husband;
        if(husband!=null && wife!=null)
            str=str+" + ";
        if(wife!=null)
            str = str + wife;
        return str;
    }

    private void analyzeFam(Fam f) {
        println(familyToString(f));
        if( (trim(f.getMarriageDate()).length()>0) || (trim(f.getProperty(new TagPath("FAM:MARR:PLAC"))).length()>0) )
            println(OPTIONS.getMarriageSymbol()+trim(f.getMarriageDate())+" "+trim(f.getProperty(new TagPath("FAM:MARR:PLAC"))));
        analyzeIndi(f.getHusband(), f);
        analyzeIndi(f.getWife(), f);
        analyzeChildren(f);
    }

    private void analyzeIndi(Indi indi, Fam f) {

        if(indi==null)
            return;

        println(getIndent(2)+indi);

        if(reportParents) {
          Fam fam = indi.getFamilyWhereBiologicalChild();
            if(fam!=null)
                println(getIndent(3)+OPTIONS.getChildOfSymbol()+familyToString(fam));
        }

        if( (trim(indi.getBirthAsString()).length()>0) || (trim(indi.getProperty(new TagPath("INDI:BIRT:PLAC"))).length()>0) )
            println(getIndent(3)+OPTIONS.getBirthSymbol()+trim(indi.getBirthAsString())+" "+trim(indi.getProperty(new TagPath("INDI:BIRT:PLAC"))));
        if(indi.getProperty("DEAT")!=null && ( (trim(indi.getDeathAsString()).length()>0) || (trim(indi.getProperty(new TagPath("INDI:DEAT:PLAC"))).length()>0) ) )
            println(getIndent(3)+OPTIONS.getDeathSymbol()+trim(indi.getDeathAsString())+" "+trim(indi.getProperty(new TagPath("INDI:DEAT:PLAC"))));
        if(reportOtherSpouses) {
            Fam[] families = indi.getFamiliesWhereSpouse();
            if(families.length > 1) {
                println(getIndent(3)+translate("otherSpouses"));
                for(int i=0; i<families.length; i++) {
                    if(families[i]!=f) {
                        String str = "";
                        if((trim(families[i].getMarriageDate()).length()>0) || (trim(families[i].getProperty(new TagPath("FAM:MARR:PLAC"))).length()>0))
                            str = OPTIONS.getMarriageSymbol()+trim(families[i].getMarriageDate())+" "+trim(families[i].getProperty(new TagPath("FAM:MARR:PLAC")))+" ";
                        println(getIndent(4)+str+" "+families[i]);
                    }
                }
            }
        }
    }

    private void analyzeChildren(Fam f) {

        Indi[] children = f.getChildren();
        Indi child;
        Fam[] families;
        Fam family;

        if(children.length>0)
            println(getIndent(2)+translate("children"));
        for(int i=0; i<children.length; i++) {
            child = children[i];
            println(getIndent(3)+child);
            if(reportDetailedChildrenData) {
                if ( (trim(child.getBirthAsString()).length()>0) || (trim(child.getProperty(new TagPath("INDI:BIRT:PLAC"))).length()>0) )
                    println(getIndent(4)+OPTIONS.getBirthSymbol()+trim(child.getBirthAsString())+" "+trim(child.getProperty(new TagPath("INDI:BIRT:PLAC"))));
                printBaptism(child, "BAPM");
                printBaptism(child, "BAPL");
                printBaptism(child, "CHR");
                printBaptism(child, "CHRA");
                families = child.getFamiliesWhereSpouse();
                for(int j=0; j<families.length; j++) {
                    family = (Fam)families[j];
                    println(getIndent(4)+OPTIONS.getMarriageSymbol()+family+" "+trim(family.getMarriageDate())+" "+trim(family.getProperty(new TagPath("FAM:MARR:PLAC"))));
                }
                if(child.getProperty("DEAT")!=null && ( (trim(child.getDeathAsString()).length()>0) || (trim(child.getProperty(new TagPath("INDI:DEAT:PLAC"))).length()>0) ) )
                    println(getIndent(4)+OPTIONS.getDeathSymbol()+trim(child.getDeathAsString())+" "+trim(child.getProperty(new TagPath("INDI:DEAT:PLAC"))));
            }
        }
    }

    private void printBaptism(Indi indi, String tag) {

        if( (indi.getProperty(tag)!=null) && ( (trim(indi.getProperty(new TagPath("INDI:"+tag+":DATE"))).length()>0) || (trim(indi.getProperty(new TagPath("INDI:"+tag+":PLAC"))).length()>0) ) )
            println(getIndent(4)+OPTIONS.getBaptismSymbol()+"("+tag+"): "+trim(indi.getProperty(new TagPath("INDI:"+tag+":DATE")))+" "+trim(indi.getProperty(new TagPath("INDI:"+tag+":PLAC"))));
    }

} 
