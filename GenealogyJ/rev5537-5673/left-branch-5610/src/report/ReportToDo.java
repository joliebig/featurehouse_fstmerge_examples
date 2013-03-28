

import genj.fo.Document;
import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.MultiLineProperty;
import genj.gedcom.Property;
import genj.gedcom.PropertyMultilineValue;
import genj.gedcom.PropertySex;
import genj.gedcom.PropertyXRef;
import genj.report.Report;

import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;


public class ReportToDo extends Report {

  private final static String PLACE_AND_DATE_FORMAT = "{$V }{$D}{ $P}";

  public String todoTag = "NOTE";

  public String todoStart = "TODO:";

  public boolean outputWorkingSheet = false;

  public boolean outputSummary = true;

  private final static String
	ROW_FORMAT_HEADER1 = "font-size=larger,background-color=#00ccff,font-weight=bold";
  private final static String
	FORMAT_HEADER2 = "font-size=large,background-color=#33ffff,font-weight=bold";
  private final static String
	FORMAT_HEADER3 = "background-color=#ffffcc,font-weight=bold";
  private final static String
	FORMAT_HEADER3_TODO = "background-color=#99cccc,font-weight=bold";
  private final static String
  	FORMAT_HEADER4 = "background-color=#ffffcc";
  private final static String FORMAT_EMPHASIS = "font-weight=italic";
  private final static String FORMAT_STRONG = "font-weight=bold";

  

  
  protected ImageIcon getImage() {
    return Report.IMG_FO;
  }

  
  public boolean usesStandardOut() {
    return false;
  }

  
  public Document start(Gedcom gedcom) {
    List ents = gedcom.getEntities();
    return start((Entity[])ents.toArray(new Entity[ents.size()]));
  }

  
  public void start(Indi indi) {
    start(new Indi[] { indi });
  }

  
  public Document start(Fam fam) {
    return start(new Fam[]{ fam });
  }

  
  public Document start(Entity[] entities) {

    
    Document doc = new Document(translate("titletodos"));

    
    if (outputWorkingSheet) {

      doc.startTable();
      doc.addTableColumn("column-width=12%");
      doc.addTableColumn("column-width=10%");
      doc.addTableColumn("column-width=20%");
      doc.addTableColumn("column-width=20%");
      doc.addTableColumn("column-width=19%");
      doc.addTableColumn("column-width=19%");

      exportWorkingSheet(entities, doc);
      doc.endTable();

    }

    
    if (outputSummary) {

      
    	doc.startTable("width=100%,border=0.5pt solid black,genj:csv=true");

      doc.nextTableRow(ROW_FORMAT_HEADER1);
      doc.addTableColumn("");
      doc.addTableColumn("");
      doc.addTableColumn("");
      doc.addTableColumn("");
      doc.addTableColumn("");

      doc.nextTableCell("number-columns-spanned=5");
      doc.addText(translate("titletodos"),ROW_FORMAT_HEADER1);

      doc.nextTableRow();
      doc.addText( translate("evt.col"),FORMAT_STRONG );
      doc.nextTableCell();
      doc.addText( translate("date.col"),FORMAT_STRONG );
      doc.nextTableCell();
      doc.addText( translate("place.col"),FORMAT_STRONG );
      doc.nextTableCell();
      doc.addText( translate("indi.col"),FORMAT_STRONG );
      doc.nextTableCell();
      doc.addText( translate("todo.col"),FORMAT_STRONG );

      int nbTodos = exportSummary(entities, doc);
      doc.endTable();

      doc.addText( translate("nbtodos", "" + nbTodos) );
    }

    
    return doc;

  }

  
  private void exportWorkingSheet(Entity[] entities, Document doc) {

    
    for (int e = 0; e < entities.length; e++) {

      Entity entity = entities[e];

      List todos = findProperties(entity);
      if (!todos.isEmpty()) {
        if (entity instanceof Indi)
          exportEntity((Indi)entity, doc);
        if (entity instanceof Fam)
          exportEntity((Fam)entity, doc);
      }
    }

  }

  
  private void exportEntity(Fam fam, Document doc) {
    Property prop;
    Property[] propArray;
    List todos;
    String tempString = "";
    Indi tempIndi;
    Fam tempFam;

    todos = findProperties(fam);
    if (todos.size() == 0)
      return;

    doc.nextTableRow(ROW_FORMAT_HEADER1);
    doc.nextTableCell("number-columns-spanned=6");
    doc.addText( translate("titlefam", new String[] { fam.toString(), fam.getId() }) );

    
    tempIndi = fam.getHusband();
    doc.nextTableRow(FORMAT_HEADER2);
    doc.addText( Gedcom.getName("HUSB"));
    doc.nextTableCell("number-columns-spanned=5");
    doc.addText( tempIndi.getName() ); 

    outputEventRow(tempIndi, "BIRT", todos, doc);
    outputEventRow(tempIndi, "BAPM", todos, doc);
    outputEventRow(tempIndi, "DEAT", todos, doc);
    outputEventRow(tempIndi, "BURI", todos, doc);

    if (tempIndi!=null) {
      tempFam = tempIndi .getFamilyWhereBiologicalChild();
      if (tempFam != null) {
        doc.nextTableRow();
        doc.nextTableCell(FORMAT_HEADER3);
        doc.addText( translate("father") + ":" );
        doc.nextTableCell("number-columns-spanned=5");
        addIndiString(tempFam.getHusband(), doc);
        doc.nextTableRow();
        doc.nextTableCell(FORMAT_HEADER3);
        doc.addText( translate("mother") + ":" );
        doc.nextTableCell("number-columns-spanned=5");
        addIndiString(tempFam.getWife(), doc);
      }
    }
    
    
    tempIndi = fam.getWife();
    doc.nextTableRow(FORMAT_HEADER2);
    doc.addText( Gedcom.getName("WIFE") );
    doc.nextTableCell("number-columns-spanned=5");
    doc.addText( tempIndi.getName() );

    outputEventRow(tempIndi, "BIRT", todos, doc);
    outputEventRow(tempIndi, "BAPM", todos, doc);
    outputEventRow(tempIndi, "DEAT", todos, doc);
    outputEventRow(tempIndi, "BURI", todos, doc);

    if (tempIndi!=null) {
      tempFam = tempIndi .getFamilyWhereBiologicalChild();
      if (tempFam != null) {
        doc.nextTableRow();
        doc.nextTableCell(FORMAT_HEADER3);
        doc.addText( translate("father") );
        doc.nextTableCell("number-columns-spanned=5");
        addIndiString(tempFam.getHusband(), doc) ;
        doc.nextTableRow();
        doc.nextTableCell(FORMAT_HEADER3);
        doc.addText( translate("mother") + ":" );
        doc.nextTableCell("number-columns-spanned=5");
        addIndiString(tempFam.getWife(), doc) ;
      }
    }
    outputEventRow(fam, "MARR", todos, doc);

    
    Indi[] children = fam.getChildren();
    if (children.length > 0) {
      doc.nextTableRow(FORMAT_HEADER2);
      doc.nextTableCell("number-columns-spanned=6");
      doc.addText( Gedcom.getName("CHIL", children.length > 1) );
      for (int c = 0; c < children.length; c++) {
        doc.nextTableRow();
        doc.nextTableCell(FORMAT_HEADER3);
        doc.addText("" + (c + 1) );
        doc.nextTableCell("number-columns-spanned=5");
        addIndiString(children[c], doc) ;
      }
    }

    
    propArray = fam.getProperties("NOTE");
    boolean seenNote = false;
    for (int i = 0; i < propArray.length; i++) {
      prop = (Property) propArray[i];
      if (todos.contains(prop))
        continue;
      if (!seenNote) {
        doc.nextTableRow(FORMAT_HEADER2);
        doc.nextTableCell("number-columns-spanned=6");
        doc.addText( translate("main.notes") );
        seenNote = true;
      }
      doc.nextTableRow();
      doc.nextTableCell();
      doc.nextTableCell("number-columns-spanned=5");
      outputPropertyValue(prop, doc);
    }

    
    doc.nextTableRow(FORMAT_HEADER2);
    doc.nextTableCell("number-columns-spanned=6");
    doc.addText( translate("titletodo") );
    for (int i = 0; i < todos.size(); i++) {
      prop = (Property) todos.get(i);
      Property parent = prop.getParent();
      doc.nextTableRow();
      if (parent instanceof Fam) {
        doc.nextTableCell();
        doc.nextTableCell("number-columns-spanned=5");
        outputPropertyValue(prop, doc);
      } else {
    	  doc.nextTableCell(FORMAT_HEADER3_TODO);
        doc.addText( Gedcom.getName(parent.getTag()) );
        doc.nextTableCell("number-columns-spanned=5,");
        doc.addText( parent.format(PLACE_AND_DATE_FORMAT) );
        doc.nextParagraph();
        outputPropertyValue(prop,doc);
        doc.nextParagraph();
        doc.addText( outputProperty(prop, prop.getPath().toString() + ":REPO") );
        doc.nextParagraph();
        doc.addText( outputProperty(prop, prop.getPath().toString() + ":NOTE") );
      }
    }

    
  }

  
  private void exportEntity(Indi indi, Document doc) {
    Property prop;
    Property[] propArray;
    List todos;
    String tempString = "";

    todos = findProperties(indi);
    if (todos.size() == 0)
      return;

    doc.nextTableRow(ROW_FORMAT_HEADER1);
    doc.nextTableCell("number-columns-spanned=6");
    doc.addText( translate("titleindi", new String[] { indi.getName(), indi.getId() }) );

    doc.nextTableRow();
    doc.nextTableCell("number-columns-spanned=6,"+FORMAT_HEADER2);
    doc.addText( translate("titleinfosperso") );

    doc.nextTableRow();
    doc.nextTableCell(FORMAT_HEADER3);
    doc.addText( Gedcom.getName("NAME") );
    doc.nextTableCell("number-columns-spanned=3");
    doc.addText( indi.getLastName()+" ", FORMAT_STRONG );
    doc.addText( indi.getFirstName() );
    doc.nextTableCell();
    doc.addText( "ID: " + indi.getId() );
    doc.nextTableCell();
    doc.addText( Gedcom.getName("SEX") + ": " + PropertySex.getLabelForSex(indi.getSex()) );

    doc.nextTableRow();
    doc.nextTableCell(FORMAT_HEADER3);
    doc.addText( Gedcom.getName("NICK"));
    doc.nextTableCell("number-columns-spanned=5");
    doc.addText( outputProperty(indi, "INDI:NAME:NICK") );

    outputEventRow(indi, "BIRT", todos, doc);
    outputEventRow(indi, "BAPM", todos, doc);
    outputEventRow(indi, "DEAT", todos, doc);
    outputEventRow(indi, "BURI", todos, doc);

    doc.nextTableRow();
    doc.nextTableCell(FORMAT_HEADER3);
    doc.addText( Gedcom.getName("REFN") );
    doc.nextTableCell("number-columns-spanned=3");
    doc.addText( outputProperty(indi, "INDI:REFN") );
    doc.nextTableCell(FORMAT_HEADER3);
    doc.addText( Gedcom.getName("CHAN") );
    doc.nextTableCell();
    doc.addText( outputProperty(indi, "INDI:CHAN") );

    Fam fam = indi.getFamilyWhereBiologicalChild();
    if (fam != null) {
      doc.nextTableRow();
      doc.nextTableCell(FORMAT_HEADER3);
      doc.addText( translate("father") + ":" );
      doc.nextTableCell("number-columns-spanned=5");
      addIndiString(fam.getHusband(), doc) ;

      doc.nextTableRow();
      doc.nextTableCell(FORMAT_HEADER3);
      doc.addText( translate("mother") + ":" );
      doc.nextTableCell("number-columns-spanned=5");
      addIndiString(fam.getWife(), doc) ;
    }

    
    Fam[] fams = indi.getFamiliesWhereSpouse();
    if (fams.length > 0) {
      doc.nextTableRow();
      doc.nextTableCell("number-columns-spanned=6,"+FORMAT_HEADER2);
      doc.addText( Gedcom.getName("FAM", fams.length > 1) );
    }

    for (int f = 0; f < fams.length; f++) {
      
      Fam famc = fams[f];
      Indi spouse = famc.getOtherSpouse(indi);
      if (spouse != null) {
        Indi[] children = famc.getChildren();

        doc.nextTableRow();
        doc.nextTableCell("number-rows-spanned="+(children.length+1)+","+FORMAT_HEADER3);
        doc.addText(translate("spouse") + ":" );
        doc.nextTableCell("number-columns-spanned=5");
        addIndiString(spouse, doc) ;
        doc.nextParagraph();
        doc.addText( Gedcom.getName("MARR") + " : ",FORMAT_STRONG);
        doc.addText( famc.format("MARR", PLACE_AND_DATE_FORMAT) ); 

        if (children.length > 0) {

          doc.nextTableRow();
          doc.nextTableCell("number-rows-spanned="+children.length+","+FORMAT_HEADER4);
          doc.addText(Gedcom.getName("CHIL", children.length > 1) );
          doc.nextTableCell("number-columns-spanned=4");
          addIndiString(children[0], doc) ;
          for (int c = 1; c < children.length; c++) {
            doc.nextTableRow();
            doc.nextTableCell("number-columns-spanned=4");
            addIndiString(children[c], doc) ;
          }
        }
      }
    }

    doc.nextTableRow();
    doc.nextTableCell("number-columns-spanned=6,"+FORMAT_HEADER2);
    doc.addText( Gedcom.getName("EVEN", true) );

    outputEventRow(indi, "OCCU", todos, doc);
    outputEventRow(indi, "RESI", todos, doc);

    
    propArray = indi.getProperties("NOTE");
    boolean seenNote = false;
    for (int i = 0; i < propArray.length; i++) {
      prop = (Property) propArray[i];
      if (todos.contains(prop))
        continue;
      if (!seenNote) {
        doc.nextTableRow();
        doc.nextTableCell("number-columns-spanned=6,"+FORMAT_HEADER2);
        doc.addText( translate("main.notes") );
        seenNote = true;
      }
      doc.nextTableRow();
      doc.nextTableCell("number-columns-spanned=6");
      outputPropertyValue(prop, doc);
    }

    
    doc.nextTableRow();
    doc.nextTableCell("number-columns-spanned=6,"+FORMAT_HEADER2);
    doc.addText( translate("titletodo") );
    for (int i = 0; i < todos.size(); i++) {
      prop = (Property) todos.get(i);
      Property parent = prop.getParent();
      String row;
      if (parent instanceof Indi) {
        doc.nextTableRow();
        doc.nextTableCell();
        doc.nextTableCell("number-columns-spanned=5");
        outputPropertyValue(prop,doc);
      } else {
        doc.nextTableRow();
        doc.nextTableCell(FORMAT_HEADER3_TODO);
        doc.addText( Gedcom.getName(parent.getTag()) );
        doc.nextTableCell("number-columns-spanned=5");
        doc.addText( parent.format(PLACE_AND_DATE_FORMAT) );
        doc.nextParagraph();
        outputPropertyValue(prop, doc);
        doc.nextParagraph();
        doc.addText( formatString("", outputProperty(prop, prop.getPath().toString() + ":REPO"), "") );
        doc.nextParagraph();
        doc.addText( formatString("", outputProperty(prop, prop.getPath().toString() + ":NOTE"), "") );
      }
    }
  }

  
  private void outputEventRow(Entity indi, String tag, List todos, Document doc) {

    if (indi == null)
      return;

    Property props[] = indi.getProperties(tag);
    if (props.length==0)
      return;

    if (props.length == 1) {

      doc.nextTableRow();
      doc.nextTableCell(FORMAT_HEADER3);
      doc.addText( Gedcom.getName(tag) );
      doc.nextTableCell("number-columns-spanned=5");
      doc.addText( indi.format(tag, PLACE_AND_DATE_FORMAT) );
      doc.nextParagraph();
      outputNotes( "Note : ", indi.getProperty(tag), todos, doc); 

      return;
    }

    for (int i = 0; i < props.length; i++) {
      doc.nextTableRow();
      doc.nextTableCell(FORMAT_HEADER3);
      doc.addText(Gedcom.getName(tag) );
      doc.nextTableCell("number-columns-spanned=5");
      doc.addText( props[i].format(PLACE_AND_DATE_FORMAT) );
      doc.nextParagraph();
      outputNotes( "Note : ", props[i], todos, doc); 
    }

    
  }

  
  private int exportSummary(Entity[] ents, Document doc) {

    List todos;
    boolean isFirstPage = true;
    int nbTodos = 0;

    
    for (int e = 0; e < ents.length; e++) {

      todos = findProperties(ents[e]);
      if (todos.size() == 0)
        continue;

      
      for (int i = 0; i < todos.size(); i++) {
        Property prop = (Property) todos.get(i);
        if ((prop instanceof PropertyMultilineValue)) continue;
        Property parent = prop.getParent();

        if (parent != null){
        	doc.nextTableRow();
        if ((parent instanceof Entity)) {
          doc.nextTableCell();
          doc.nextTableCell();
          doc.nextTableCell();
        } else {
          doc.addText( Gedcom.getName(parent.getTag()) );
          doc.nextTableCell();
          doc.addText( parent.getPropertyDisplayValue("DATE") );
          doc.nextTableCell();
          doc.addText( parent.getPropertyDisplayValue("PLAC") );
        }
        doc.nextTableCell();
        doc.addText( prop.getEntity().toString() );
        doc.nextTableCell();
        outputPropertyValue(prop, doc);

        nbTodos++;
        }
      }
    }

    
    return nbTodos;
  }

  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  

  
  private void outputNotes(String prefix, Property prop, List exclude, Document doc) {
    
    if (prop == null)
      return;

    Property[] props = prop.getProperties("NOTE");
    for (int i = 0; i < props.length; i++) {
      if (exclude.contains(props[i]))
        continue;
      doc.addText( prefix ,FORMAT_STRONG);
      outputPropertyValue(props[i], doc);
    }

    
  }

  private String outputProperty(Property prop, String tagPath) {
    Property subProp = prop.getPropertyByPath(tagPath);
    return (subProp == null) ? "" : subProp.toString();
  }

  private String formatString(String start, String middle, String end) {
    if (middle != null && middle.length() != 0) {
      return ((start == null) ? "" : start) + middle
          + ((end == null) ? "" : end);
    } else {
      return "";
    }
  }

  private void addIndiString(Indi indi, Document doc) {
    
    if (indi == null)
      return ;
    String birth = indi.format("BIRT", OPTIONS.getBirthSymbol() + PLACE_AND_DATE_FORMAT);
    String death = indi.format("DEAT", OPTIONS.getDeathSymbol() + PLACE_AND_DATE_FORMAT);
    doc.addText(indi.toString(),FORMAT_STRONG);
    doc.addText(" " + birth + " " + death);
  }

  
  private void outputPropertyValue(Property prop, Document doc) {

    
    if (prop instanceof PropertyXRef) {
      PropertyXRef xref = (PropertyXRef) prop;
      outputPropertyValue( xref.getTargetEntity(), doc);
      return;
    }

    
    if (!(prop instanceof MultiLineProperty)) {
      doc.addText(prop.getDisplayValue());
      return;
    }

    
     StringTokenizer lines = new StringTokenizer(prop.getValue(), "\n");
     while (lines.hasMoreTokens()) {
         doc.nextParagraph();
         doc.addText(lines.nextToken());
     }
    
  }

  private List findProperties(Property of) {
    return of.findProperties(Pattern.compile(todoTag), Pattern.compile(
        todoStart + ".*", Pattern.DOTALL));
  }

} 
