
import genj.fo.Document;
import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.PrivacyPolicy;
import genj.gedcom.Property;
import genj.gedcom.PropertySource;
import genj.gedcom.Source;
import genj.gedcom.TagPath;
import genj.report.Report;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;


public class ReportSosa extends Report {

  
  private final static int
    SOSA_REPORT = 0,
    TABLE_REPORT = 1,
    LINEAGE_REPORT = 2,
    AGNATIC_REPORT = 3;

  public int reportType = SOSA_REPORT;

  public String reportTypes[] = {
      translate("SosaReport"),
      translate("TableReport"),
      translate("LineageReport"),
      translate("AgnaticReport")
   };

  
  private final static int
    ONE_LINE = 0,
    ONE_EVT_PER_LINE = 1;

  public int reportFormat = ONE_LINE;
  public String reportFormats[] = {
      translate("IndiPerLine"),
      translate("EventPerLine")
  };

  public boolean displayBullet = true;
  
  public int startSosa=1;
  public String sosaTag="_SOSA";

  
  public int privateGen = 0;
  public int reportMinGenerations = 1;
  public int reportMaxGenerations = 999;
  
  
  public boolean reportPlaceOfBirth = true;
  public boolean reportDateOfBirth = true;
  public boolean reportPlaceOfBaptism = true;
  public boolean reportDateOfBaptism = true;
  public boolean reportPlaceOfMarriage = true;
  public boolean reportDateOfMarriage = true;
  public boolean reportPlaceOfDeath = true;
  public boolean reportDateOfDeath = true;
  public boolean reportPlaceOfBurial = true;
  public boolean reportDateOfBurial = true;
  public boolean reportOccu = true; 
  public boolean reportPlaceOfOccu = true;
  public boolean reportDateOfOccu = true;
  public boolean reportResi = true; 
  public boolean reportPlaceOfResi = true;
  public boolean reportDateOfResi = true;

  
  public boolean showAllPlaceJurisdictions = false;
  private final static int
     SRC_NO = 0,
     SRC_TITLE_NO_TEXT = 1,
     SRC_TITLE_GEN_NO_TEXT = 2,
     SRC_TITLE_END_NO_TEXT = 3,
     SRC_TITLE_TEXT_GEN = 4,
     SRC_TITLE_TEXT_END = 5,
     SRC_TITLE_GEN_TEXT_GEN = 6,
     SRC_TITLE_END_TEXT_END = 7;
  public int displaySource = SRC_TITLE_NO_TEXT;
  public String displaySources[] = {
   translate("src_no"),                
   translate("src_title_no_text"),     
   translate("src_title_gen_no_text"), 
   translate("src_title_end_no_text"), 
   translate("src_title_text_gen"),    
   translate("src_title_text_end"),    
   translate("src_title_gen_text_gen"),
   translate("src_title_end_text_end") 
  };

  public boolean displayEmpty = true;
  public boolean prefixEvent = false;
  public String prefixSource = "Src: ";
  
  
  private static String format_one_line = "";
  private static String format_multi_lines = "";
 
  private final static int
    COLOR_BLACK = 0,    
    COLOR_GREY = 1,     
    COLOR_PURPLE = 2,   
    COLOR_INDIGO = 3,   
    COLOR_BLUE = 4,     
    COLOR_GREEN = 5,    
    COLOR_YELLOW = 6,   
    COLOR_ORANGE = 7,   
    COLOR_RED = 8;      
  
  public int srcColor = COLOR_BLUE;

  public String srcColors[] = {
      translate("Black"),
      translate("Grey"),
      translate("Purple"),
      translate("Indigo"),
      translate("Blue"),
      translate("Green"),
      translate("Yellow"),
      translate("Orange"),
      translate("Red")
   };

  
  
  
  
  
  
  
  
  
  
  
  private static SortedSet globalSrcList  = new TreeSet();  
  private static Map       globalSrcNotes = new TreeMap();  

  
  String[] events = { "BIRT", "BAPM", "MARR", "DEAT", "BURI", "OCCU", "RESI" };
  boolean[] dispEv = { true, true, true, true, false, false, false };
  String[] symbols = new String[7];
  private boolean 
     srcLinkSrc    = false,
     srcLinkGenSrc = false,
     srcTitle      = false,
     srcAtGen      = false,
     srcAtEnd      = false,
     srcTextAtGen  = false,
     srcTextAtEnd  = false,
     srcDisplay    = false;
  private static String NOTE  = ".:NOTE";       
  private static String DATA  = ".:DATA:TEXT";  


  public void start(Indi indi) {

    
    PrivacyPolicy policy = OPTIONS.getPrivacyPolicy();
    InitVariables();
    assignColor(srcColor);

    if (startSosa == 0){
    	Property sosaProp = indi.getProperty(sosaTag);
    	if (sosaProp != null){
    		try {
    			startSosa = Integer.parseInt(sosaProp.getValue(),10);
    		} catch (NumberFormatException e){
    		}
    	}
    }
    if (startSosa == 0){
    	startSosa = 1;
    }
    
    Recursion recursion;
    switch (reportType) {
      case AGNATIC_REPORT:
        recursion = new Agnatic();
        break;
      case SOSA_REPORT:
        recursion = new Sosa();
        break;
      case LINEAGE_REPORT:
        recursion = new Lineage();
        break;
      case TABLE_REPORT:
        recursion = new Table();
        break;
      default:
        throw new IllegalArgumentException("no such report type");
    }

    
    String title = recursion.getTitle(indi);
    Document doc = new Document(title);
    doc.startSection(title);

    
    recursion.start(indi, policy, doc);

    
    showDocumentToUser(doc);
  }

  
  abstract class Recursion {

    
    abstract void start(Indi indi, PrivacyPolicy policy, Document doc);

    
    abstract String getTitle(Indi root);

    
    abstract void formatStart(Indi indi, Document doc);

    
    abstract void formatIndi(Indi indi, Fam fam, int gen, int sosa, PrivacyPolicy policy, Document doc);

    
    abstract void formatEnd(Document doc);

     
     String getProperty(Entity entity, String tag, String prefix, boolean date, boolean place, PrivacyPolicy policy) {
       Property prop = entity.getProperty(tag);
       if (prop == null)
         return "";
       String format = prefix + (date ? "{ $D}" : "") + (place && showAllPlaceJurisdictions ? "{ $P}" : "") + (place && !showAllPlaceJurisdictions ? "{ $p}" : "");
       return prop.format(format, policy).trim();
     }

     
     List getSources(Entity entity, String tagPath, String description) {
       String descStr = entity.toString();
       List src = new ArrayList();
       if (description.length() != 0) descStr+=" "+description+" :$#@ ";
       Property prop[] = entity.getProperties(new TagPath(tagPath));
       for (int p=0; p<prop.length; p++) {
          if ((prop[p] != null) && (prop[p].toString().trim().length() != 0) && (prop[p] instanceof PropertySource)) {
             PropertySource propSrc = (PropertySource)prop[p];
             Source source = (Source)(propSrc.getTargetEntity());
             src.add(source);
             
             globalSrcList.add(source);
             
             
             List listOfNotes = (List)globalSrcNotes.get(source);
             
             if (listOfNotes == null) {
                listOfNotes = new ArrayList();
                String sText = source.getText();
                if ((sText != null) && (sText.trim().length() > 0))
                   listOfNotes.add(sText);
                String sNote = "";
                Property sProp = source.getPropertyByPath(NOTE);
                if (sProp != null) sNote = sProp.toString();
                if ((sNote != null) && (sNote.trim().length() > 0))
                   listOfNotes.add(sNote);
                globalSrcNotes.put(source, listOfNotes);
                }
             
             
             
             String strNote = "";
             Property sProp2 = propSrc.getPropertyByPath(NOTE);
             if (sProp2 != null) strNote = sProp2.toString();
             if ((strNote != null) && (strNote.trim().length() > 0) && (!isAlreadyIn(listOfNotes, strNote))) {
                listOfNotes.add(descStr+strNote);
                }
             strNote = "";
             sProp2 = propSrc.getPropertyByPath(DATA);
             if (sProp2 != null) strNote = sProp2.toString();
             if ((strNote != null) && (strNote.trim().length() > 0) && (!isAlreadyIn(listOfNotes, strNote))) {
                listOfNotes.add(descStr+strNote);
                }
             }
          }
       return src;
       }
           
    
    String getName(Indi indi, int sosa, PrivacyPolicy privacy) {
      return (sosa>0?sosa+" ":"") + privacy.getDisplayValue(indi, "NAME") + " (" + indi.getId() + ")";
    }

    
    void getProperties(Indi indi, Fam fam, PrivacyPolicy privacy, boolean usePrefixes, boolean returnEmpties, Map eDesc, Map eSrc) {

      
      String event = "";
      String description = "";
      List sources = new ArrayList();
      int ev = 0;
    
      
      ev = 0;
      event = "BIRT";
      if (dispEv[ev]) {
         description = getProperty(indi, event, usePrefixes ? symbols[ev] : "", reportDateOfBirth, reportPlaceOfBirth, privacy);
         if (returnEmpties||description.length()>0)
           eDesc.put(event, description);

         if (srcDisplay) {
           sources = getSources(indi, "INDI:"+event+":SOUR", description);
           if (displayEmpty||sources.size() > 0)
             eSrc.put(event, sources);
           }
        }

      
      ev = 1;
      event = "BAPM";
      if (dispEv[ev]) {
         description = getProperty(indi, event, usePrefixes ? symbols[ev] : "", reportDateOfBaptism, reportPlaceOfBaptism, privacy);
         if (returnEmpties||description.length()>0)
           eDesc.put(event, description);

         if (srcDisplay) {
           sources = getSources(indi, "INDI:"+event+":SOUR", description);
           if (displayEmpty||sources.size() > 0)
             eSrc.put(event, sources);
           }
        }

      
      ev = 2;
      event = "MARR";
      if (dispEv[ev]) {
         if (fam!=null) {
           String prefix = "";
           description = getProperty(fam, event, prefix, reportDateOfMarriage, reportPlaceOfMarriage, privacy);
           if (usePrefixes)
             prefix = symbols[ev] + (fam.getOtherSpouse(indi) != null ? " " + fam.getOtherSpouse(indi).getName() : "");
           if (returnEmpties||description.length()>0)
             eDesc.put(event, prefix+" "+description);
           if (srcDisplay) {
              sources = getSources(fam, "FAM:"+event+":SOUR", (usePrefixes ? symbols[ev] : "")+" "+description);
              if (sources.size() > 0)
                eSrc.put(event, sources);
             }
           }
        }
 
      
      ev = 3;
      event = "DEAT";
      if (dispEv[ev]) {
         description = getProperty(indi, event, usePrefixes ? symbols[ev] : "", reportDateOfDeath, reportPlaceOfDeath, privacy);
         if (returnEmpties||description.length()>0)
           eDesc.put(event, description);

         if (srcDisplay && (reportDateOfDeath || reportPlaceOfDeath)) {
            sources = getSources(indi, "INDI:"+event+":SOUR", description);
            if (sources.size() > 0)
              eSrc.put(event, sources);
           }
        }

      
      ev = 4;
      event = "BURI";
      if (dispEv[ev]) {
         description = getProperty(indi, event, usePrefixes ? symbols[ev] : "", reportDateOfBurial, reportPlaceOfBurial, privacy);
         if (returnEmpties||description.length()>0)
           eDesc.put(event, description);

         if (srcDisplay && (reportDateOfBurial || reportPlaceOfBurial)) {
            sources = getSources(indi, "INDI:"+event+":SOUR", description);
            if (sources.size() > 0)
              eSrc.put(event, sources);
           }
        }

      
      ev = 5;
      event = "OCCU";
      if (reportOccu) {
         description = getProperty(indi, event, (usePrefixes ? symbols[ev] : "")+"{ $V} ", reportDateOfOccu, reportPlaceOfOccu, privacy);
         if (returnEmpties||description.length()>0)
           eDesc.put(event, description);
   
         if (srcDisplay) {
            sources = getSources(indi, "INDI:"+event+":SOUR", description);
            if (sources.size() > 0)
              eSrc.put(event, sources);
           }
        }

      
      ev = 6;
      event = "RESI";
      if (reportResi) {
         description = getProperty(indi, event, (usePrefixes ? symbols[ev] : "")+"{ $V} ", reportDateOfResi, reportPlaceOfResi, privacy);
         if (returnEmpties||description.length()>0)
           eDesc.put(event, description);

         if (srcDisplay) {
            sources = getSources(indi, "INDI:"+event+":SOUR", description);
            if (sources.size() > 0)
              eSrc.put(event, sources);
           }
        }

      
      return;
    }

  } 

  
  abstract class DepthFirst  extends Recursion {

    
    void start(Indi indi, PrivacyPolicy policy, Document doc) {
      formatStart(indi, doc);
      Fam[] fams = indi.getFamiliesWhereSpouse();
      Fam fam = null;
      if ((fams != null) && (fams.length > 0)) {
         fam = fams[0];
         }
      else {
         fam = null;
         }
      recursion(indi, fam, 0, startSosa, policy, doc);
      formatEnd(doc);
    }

    
    void recursion(Indi indi, Fam fam, int gen, int sosa, PrivacyPolicy policy, Document doc) {

      
      if (gen > reportMaxGenerations)
        return;

      
      formatIndi(indi, fam, gen, sosa, gen < privateGen ? PrivacyPolicy.PRIVATE : policy, doc);

      
      Fam famc = indi.getFamilyWhereBiologicalChild();
      if (famc == null)
        return;

      Indi father = famc.getHusband();
      Indi mother = famc.getWife();

      if (father==null&&mother==null)
        return;

      
      if (father != null)
        recursion(father, famc, gen+1,  sosa*2, policy, doc);

      
      if (mother != null)
        recursion(mother, famc, gen+1, sosa*2+1, policy, doc);

      
    }
  } 

  
  abstract class BreadthFirst extends Recursion {

    
    void start(Indi indi, PrivacyPolicy policy, Document doc) {
      formatStart(indi, doc);
      List list = new ArrayList(3);
      list.add(new Integer(startSosa));
      list.add(indi);
      Fam[] fams = indi.getFamiliesWhereSpouse();
      if ((fams != null) && (fams.length > 0)) {
         list.add(fams[0]);
         }
      else {
         list.add(null);
         }
      recursion(list, 0, policy, doc);
      formatEnd(doc);
    }

   
   void recursion(List generation, int gen, PrivacyPolicy policy, Document doc) {

     
     if (gen > reportMaxGenerations)
       return;

     
     formatGeneration(gen, doc);

     
     List nextGeneration = new ArrayList();
     for (int i=0; i<generation.size(); ) {

       
       int sosa = ((Integer)generation.get(i++)).intValue();
       Indi indi = (Indi)generation.get(i++);
       Fam fam = (Fam)generation.get(i++);

       
       Fam famc = indi.getFamilyWhereBiologicalChild();
       if (famc!=null)  {
         Indi father = famc.getHusband();
         if (father!=null) {
           nextGeneration.add(new Integer(sosa*2));
           nextGeneration.add(father);
           nextGeneration.add(famc);
         }
         Indi mother = famc.getWife();
         if (mother!=null) {
           nextGeneration.add(new Integer(sosa*2+1));
           nextGeneration.add(mother);
           nextGeneration.add(famc);
         }
       }

       
       formatIndi(indi, fam, gen, sosa, gen < privateGen ? PrivacyPolicy.PRIVATE : policy, doc);
     } 

     
     if ((srcDisplay) && (srcAtGen || srcTextAtGen)) {
        if (gen >= reportMinGenerations-1) 
           writeSourceList(doc, gen, srcAtGen, srcTextAtGen);
        else
           globalSrcList.clear();
        }
     if (!nextGeneration.isEmpty()) {
       recursion(nextGeneration, gen+1, policy, doc);
       }

     
   }

   
   abstract void formatGeneration(int gen, Document doc);

  } 

  
  class Sosa extends BreadthFirst {

    
    String getTitle(Indi root) {
      return translate("title.sosa", root.getName());
    }

    
    void formatStart(Indi root, Document doc) {
      
      doc.startTable("width=100%");
      doc.addTableColumn("");
      doc.addTableColumn("");
    }

    
    void formatGeneration(int gen, Document doc) {
      if (gen < reportMinGenerations-1) return;
      doc.nextTableRow();
      doc.nextTableCell("color=#ffffff");
      doc.addText(".");
      doc.nextTableRow();
      doc.nextTableCell("number-columns-spanned=2,font-size=18pt,background-color=#f0f0f0,border-after-width=0.5pt");
      doc.addText(translate("Generation")+" "+(gen+1));
    }

    
    void formatIndi(Indi indi, Fam fam, int gen, int sosa, PrivacyPolicy policy, Document doc) {

      
      if (gen < reportMinGenerations-1) return;

      
      Map eventDesc = new TreeMap();     
      Map eventSources = new TreeMap();  
      
      
      doc.nextTableRow();

      
      doc.addText(getName(indi, sosa, policy)); 

      
      getProperties(indi, fam, policy, true, false, eventDesc, eventSources);
      
      if (eventDesc.size()>0) {
        doc.nextTableCell();
        writeEvents(doc, gen, eventDesc, eventSources, false);
        }
        
    }

    
    void formatEnd(Document doc) {
      if ((srcDisplay) && (srcAtEnd || srcTextAtEnd)) writeSourceList(doc, -1, srcAtEnd, srcTextAtEnd);
      
      doc.endTable();
    }

  } 
  
  
  class Lineage extends DepthFirst  {

    
    String getTitle(Indi root) {
      return translate("title.lineage", root.getName());
    }

    
    void formatStart(Indi indi, Document doc) {
      
    }

    
    void formatIndi(Indi indi, Fam fam, int gen, int sosa, PrivacyPolicy policy, Document doc) {

      if (gen < reportMinGenerations-1) return;
      
      
      doc.nextParagraph("space-after=10pt,space-before=10pt,start-indent="+(gen*20)+"pt");
      doc.addText(getName(indi, sosa, policy)+" ", "font-weight=bold");
      doc.nextParagraph("start-indent="+(gen*20+10)+"pt");

      
      
      Map eventDesc = new TreeMap();     
      Map eventSources = new TreeMap();  
      
      getProperties(indi, fam, policy, true, false, eventDesc, eventSources);
      if (eventDesc.size()>0) {
         writeEvents(doc, gen, eventDesc, eventSources, true);
         }
      
    }

    
    void formatEnd(Document doc) {
      if ((srcDisplay) && (srcAtEnd || srcTextAtEnd)) writeSourceList(doc, -1, srcAtEnd, srcTextAtEnd);
      
    }

  } 

  
  class Agnatic extends DepthFirst  {

      
      void recursion(Indi indi, Fam fam, int gen, int sosa, PrivacyPolicy policy, Document doc) {

        
        if (gen > reportMaxGenerations)
          return;

        
        formatIndi(indi, fam, gen, sosa, gen < privateGen ? PrivacyPolicy.PRIVATE : policy, doc);

        
        Fam famc = indi.getFamilyWhereBiologicalChild();
        if (famc == null)
          return;

        Indi father = famc.getHusband();

        
        if (father != null)
          recursion(father, famc, gen+1,  sosa*2, policy, doc);

        
      }

    
    String getTitle(Indi root) {
      return translate("title.agnatic", root.getName());
    }

    
    void formatStart(Indi indi, Document doc) {
      
    }

    
    void formatIndi(Indi indi, Fam fam, int gen, int sosa, PrivacyPolicy policy, Document doc) {
      if (gen < reportMinGenerations-1) return;

      
      if (gen>1&&fam!=null&&fam.getHusband()!=indi)
        return;

      
      doc.nextParagraph("space-after=10pt,space-before=10pt,start-indent="+(gen*20)+"pt");
      doc.addText(getName(indi, sosa, policy)+" ", "font-weight=bold");
      doc.nextParagraph("start-indent="+(gen*20+10)+"pt");

      
      
      Map eventDesc = new TreeMap();     
      Map eventSources = new TreeMap();  
      
      getProperties(indi, fam, policy, true, false, eventDesc, eventSources);
      
      if (eventDesc.size()>0) {
         writeEvents(doc, gen, eventDesc, eventSources, true);
         }
      
    }

    
    void formatEnd(Document doc) {
      if ((srcDisplay) && (srcAtEnd || srcTextAtEnd)) writeSourceList(doc, -1, srcAtEnd, srcTextAtEnd);
      
    }

  }

  
  class Table extends BreadthFirst  {

    String[] header = { "#", Gedcom.getName("NAME"), Gedcom.getName("BIRT"), Gedcom.getName("BAPM"), Gedcom.getName("MARR"), Gedcom.getName("DEAT"), Gedcom.getName("BURI"), Gedcom.getName("OCCU"), Gedcom.getName("RESI") };
    int[] widths = { 3, 22, 12, 10, 10, 10, 10, 10, 10 };

    
    String getTitle(Indi root) {
      return translate("title.sosa", root.getName());
    }

    
    void formatStart(Indi root, Document doc) {
      
      
      doc.startTable("genj:csv=true,width=100%"); 
      
      for (int i=0;i<header.length;i++) {
        doc.addTableColumn("column-width="+widths[i]+"%");
      }
      
      doc.nextTableRow("background-color=#f0f0f0");
      for (int i=0;i<header.length;i++) {
        if (i>0) doc.nextTableCell("background-color=#f0f0f0");
        doc.addText(header[i], "font-weight=bold");
      }
    }

    
    void formatGeneration(int gen, Document doc) {
      
    }

    
    void formatIndi(Indi indi, Fam fam, int gen, int sosa, PrivacyPolicy policy, Document doc) {

      if (gen < reportMinGenerations-1) return;
    
      
      Map eventDesc = new TreeMap();     
      Map eventSources = new TreeMap();  
      String[] props =  {""};
      getProperties(indi, fam, policy, false, true, eventDesc, eventSources);

      
      doc.nextTableRow();
      doc.addText(""+sosa);
      doc.nextTableCell();
      doc.addText(getName(indi, 0, policy)); 

      if (eventDesc.size()>0) {
        doc.nextTableCell();
        writeEvents(doc, gen, eventDesc, eventSources, false);
        }
      
    }

    
    void formatEnd(Document doc) {
      
      doc.endTable();
      if ((srcDisplay) && (srcAtEnd || srcTextAtEnd)) writeSourceList(doc, -1, srcAtEnd, srcTextAtEnd);
    }

  } 

  
  
  
  void InitVariables() {
    
    symbols[0] = OPTIONS.getBirthSymbol();
    symbols[1] = OPTIONS.getBaptismSymbol(); 
    symbols[2] = OPTIONS.getMarriageSymbol(); 
    symbols[3] = OPTIONS.getDeathSymbol(); 
    symbols[4] = OPTIONS.getBurialSymbol(); 
    symbols[5] = OPTIONS.getOccuSymbol();   
    symbols[6] = OPTIONS.getResiSymbol();    

    
    dispEv[0] = reportDateOfBirth    || reportPlaceOfBirth;
    dispEv[1] = reportDateOfBaptism  || reportPlaceOfBaptism;
    dispEv[2] = reportDateOfMarriage || reportPlaceOfMarriage;
    dispEv[3] = reportDateOfDeath    || reportPlaceOfDeath;
    dispEv[4] = reportDateOfBurial   || reportPlaceOfBurial;
    
    
    
    
    if (reportType == TABLE_REPORT) { 
       displayBullet = true;
       if (displaySource == SRC_TITLE_GEN_NO_TEXT) 
          displaySource = SRC_TITLE_END_NO_TEXT;
       if (displaySource == SRC_TITLE_TEXT_GEN) 
          displaySource = SRC_TITLE_TEXT_END;
       if (displaySource == SRC_TITLE_GEN_TEXT_GEN) 
          displaySource = SRC_TITLE_END_TEXT_END;
       }
    
    
    srcLinkSrc    = false;   
    srcLinkGenSrc = false;   
    srcTitle      = false;   
    srcAtGen      = false;   
    srcAtEnd      = false;   
    srcTextAtGen  = false;   
    srcTextAtEnd  = false;   
    srcDisplay    = false;   
    switch (displaySource) {
      case SRC_NO:
        break;
      case SRC_TITLE_NO_TEXT:
        srcTitle      = true;
        srcDisplay    = true;
        break;
      case SRC_TITLE_GEN_NO_TEXT:
        srcLinkGenSrc = true;
        srcAtGen      = true;
        srcDisplay    = true;
        break;
      case SRC_TITLE_TEXT_GEN:
        srcLinkGenSrc = true;
        srcTitle      = true;
        srcTextAtGen  = true;
        srcDisplay    = true;
        break;
      case SRC_TITLE_GEN_TEXT_GEN:
        srcLinkGenSrc = true;
        srcAtGen      = true;
        srcTextAtGen  = true;
        srcDisplay    = true;
        break;
      case SRC_TITLE_END_NO_TEXT:
        srcLinkSrc    = true;
        srcAtEnd      = true;
        srcDisplay    = true;
        break;
      case SRC_TITLE_TEXT_END:
        srcLinkSrc    = true;
        srcTitle      = true;
        srcTextAtEnd  = true;
        srcDisplay    = true;
        break;
      case SRC_TITLE_END_TEXT_END:
        srcLinkSrc    = true;
        srcAtEnd      = true;
        srcTextAtEnd  = true;
        srcDisplay    = true;
        break;
      default:
        ;
    }
    
  }

  
  void assignColor(int srcColor) {
    
    String cs = "#000000";
  
    switch (srcColor) {
      case COLOR_BLACK:
        cs = "#000000"; break;
      case COLOR_GREY:
        cs = "#a0a0a0"; break;
      case COLOR_PURPLE:
        cs = "#ff60ff"; break;
      case COLOR_INDIGO:
        cs = "#8560ff"; break;
      case COLOR_BLUE:
        cs = "#6060ff"; break;
      case COLOR_GREEN:
        cs = "#00a71c"; break;
      case COLOR_YELLOW:
        cs = "#d1de00"; break;
      case COLOR_ORANGE:
        cs = "#ffb260"; break;
      case COLOR_RED:
        cs = "#ff6060"; break;
      default:
        cs = "#000000";
    }
  format_one_line = "font-style=italic,color="+cs;
  format_multi_lines = "margin-left=0px,font-style=italic,color="+cs;
  }

  
  
  void writeEvents(Document doc, int gen, Map eventDesc, Map eventSources, boolean isIndented) {
    
    String indent = "";
    if (isIndented) indent = "start-indent="+(gen*20+10)+"pt";
    
    
    
    if ((reportFormat==ONE_EVT_PER_LINE) && (displayBullet) && (reportType != TABLE_REPORT)) doc.startList(indent);
    for (int ev = 0; ev < events.length; ev++) {
       String evStr = events[ev];
       String description = (String)eventDesc.get(evStr);
       if (description == null) description = "";   
       List sources = (List)eventSources.get(evStr);
       boolean noSrc = false;
       if ((sources == null) || (sources.size() == 0)) noSrc = true;
       String preSrc = " ";
       if ((prefixEvent) && (symbols[ev].trim().length() > 0))
          preSrc = " ("+symbols[ev]+") ";
          
       
       if (ev!=0) 
          writeStartNextItem(doc, reportFormat, displayBullet, description.length() != 0, indent);
       
       writeDescription(doc, description);
       
       if (!noSrc && srcDisplay) {
          for (Iterator s = sources.iterator(); s.hasNext(); ) {
             Source source = (Source)s.next();
             String sId = source.getId();
             if (srcTitle) {
                if (!isValidText(source)) sId = "none";
                writeStartNextParagraph(doc, reportFormat, indent);
                writeSourceWithEvent(doc, reportFormat, noSrc, prefixSource, preSrc+source.getTitle()+" ("+source.getId()+")", gen, sId);
                }
             else {
                writeSourceWithEvent(doc, reportFormat, noSrc, " ( "+prefixSource+preSrc+" "+source.getId()+" )", "", gen, sId);
                }
             }
          }
       
       if (noSrc && srcDisplay && displayEmpty && dispEv[ev]) {
          if ((displayBullet) && (description.length() == 0) && (reportType != TABLE_REPORT))
             writeStartNextItem(doc, reportFormat, displayBullet, true, indent);
          else if (displayBullet)
             writeStartNextParagraph(doc, reportFormat, "");
          else
             writeStartNextParagraph(doc, reportFormat, indent);
          writeSourceWithEvent(doc, reportFormat, noSrc, prefixSource, preSrc+translate("noSource"), 0, "");
          }
       } 
    if ((reportFormat==ONE_EVT_PER_LINE) && (displayBullet) && (reportType != TABLE_REPORT)) doc.endList();
  
    return;
  }
  
  
  void writeStartNextItem(Document doc, int format, boolean bullet, boolean isDescription, String style) {
    if (reportType == TABLE_REPORT) {
       doc.nextTableCell();
       return;
       }
    if (!isDescription) return;
    if (format == ONE_EVT_PER_LINE) {
       if (bullet) doc.nextListItem();
       else doc.nextParagraph(style); 
       }
    else 
       doc.addText(", ");  
    return;
  }
  
  void writeStartNextParagraph(Document doc, int format, String style) {
    if (format == ONE_EVT_PER_LINE) 
       doc.nextParagraph(style);
    else 
       doc.addText(", ");  
    return;
  }
  
  void writeDescription(Document doc, String text) {
    if (text.length() != 0)
       doc.addText(text);
    return;
  }
    
  void writeSourceWithEvent(Document doc, int format, boolean noSrcFound, String link, String source, int gen, String id) {
    String formatText = "";
    if (format == ONE_EVT_PER_LINE) formatText = format_multi_lines;
    else                            formatText = format_one_line;
    
    if (noSrcFound) {
       
       doc.addText(link+source, formatText);
       return;   
       }
    
    if (srcLinkSrc) {
       
       doc.addLink(link, "0-"+id);
       }
    
    else if (srcLinkGenSrc) {   
       
       doc.addLink(link, (gen+1)+"-"+id);
       }
    
    else doc.addText(link, formatText);
    
    if (srcTitle) 
       doc.addText(source, formatText);   
    return;
  }
  
  boolean isValidText(Source source) {
    
    List listOfNotes = (List)globalSrcNotes.get(source);
    return (!listOfNotes.isEmpty());
  }

  boolean isAlreadyIn(List listOfStr, String strNote) {
    for (Iterator n = listOfStr.iterator(); n.hasNext(); ) {
       String str = (String)n.next();
       if (str.indexOf(strNote) != -1) 
          return true;
       }
    return false;
  }

  void writeSourceNotes(Document doc, Source source, String format) {
    List listOfNotes = (List)globalSrcNotes.get(source);
    format +=",margin-left=8px,font-style=italic,color=#707070";
    for (Iterator n = listOfNotes.iterator(); n.hasNext(); ) {
       String strNote = (String)n.next();
       doc.nextParagraph(format);
       
       int i = strNote.indexOf("$#@");
       if (i != -1) { 
          String beg = strNote.substring(0, i);
          String end = strNote.substring(i+3);
          doc.addText(beg, "font-style=normal");
          doc.addText(end, "font-style=italic");
          }
       else {
          doc.addText(strNote);
          }
       }
    return;
  }
    
  void writeSourceList(Document doc, int gen, boolean isTitle, boolean isText) {
     
     
     
     
     
     
     if (globalSrcList.size() == 0) return;
  
     
     List noTextSources = new ArrayList();
     
     
     String format = "space-after=8pt";
     if (reportType == SOSA_REPORT) {
        if (gen == -1) doc.nextPage();
        doc.nextTableRow();
        doc.nextTableCell("margin-left=0px,number-columns-spanned=2,text-decoration=underline");
        doc.nextParagraph("space-before=20pt");
        if (gen == -1) {
           doc.addText("________________________________________________");
           doc.nextParagraph();
           }
        doc.addText(translate("sourceList"));
        doc.nextTableRow();
        doc.nextTableCell("margin-left=10px,number-columns-spanned=2");
        }
     if ((reportType == LINEAGE_REPORT) || (reportType == AGNATIC_REPORT) || (reportType == TABLE_REPORT)) {
        doc.nextPage();
        doc.nextParagraph("space-before=10pt");
        doc.addText("________________________________________________");
        doc.nextParagraph("space-after=10pt,space-before=10pt,text-decoration=underline");
        doc.addText(translate("sourceList"));
        doc.nextParagraph(format);
        }
     
     
     for (Iterator s = globalSrcList.iterator(); s.hasNext(); ) {
        Source source = (Source)s.next();
        String sId = source.getId();
        String sTitle = source.getTitle();
        if (isTitle) {
           doc.nextParagraph(format);
           doc.addAnchor((gen+1)+"-"+sId);
           doc.addText("("+sId+") "+sTitle, "color=#303030");
           if (isText)  {
              if (isValidText(source)) {
                 writeSourceNotes(doc, source, format);
                 }
              }
           }
        else if (isText) {
           if (isValidText(source)) {
              doc.nextParagraph(format);
              doc.addAnchor((gen+1)+"-"+sId);
              doc.addText("("+sId+") ");
              writeSourceNotes(doc, source, format);
              }
           else { 
              noTextSources.add(sId);
              }
           }
        } 

     
     if (noTextSources.size() > 0) {
        doc.nextParagraph("space-after=0pt");
        doc.addAnchor((gen+1)+"-none");
        for (Iterator n = noTextSources.iterator(); n.hasNext(); ) {
           doc.addText("("+(String)n.next()+") ");
           }
        doc.nextParagraph(format);
        doc.addText(translate("noText"));
        doc.nextParagraph(format);
        }
     
     globalSrcList.clear();
     globalSrcNotes.clear();
     return;
  }
  
}
