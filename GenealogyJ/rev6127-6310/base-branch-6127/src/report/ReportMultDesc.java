
import genj.fo.Document;
import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.PrivacyPolicy;
import genj.gedcom.Property;
import genj.gedcom.PropertyMultilineValue;
import genj.report.Report;

import java.util.HashMap;

import javax.swing.ImageIcon;


public class ReportMultDesc extends Report {

  private final static String FORMAT_STRONG = "font-weight=bold";
  private final static String FORMAT_UNDERLINE = "text-decoration=underline";

  private int nbColumns;

  
  private int nbIndi = 0;

  private int nbFam = 0;

  private int nbLiving = 0;

  private final static int ONE_LINE = 0, ONE_EVT_PER_LINE = 1, TABLE = 2;
  public int reportFormat = ONE_LINE;
  public String reportFormats[] = { translate("IndiPerLine"),
      translate("EventPerLine"),
      translate("Table")};

  private final static int NUM_NONE = 0, NUM_ABBO = 1;
  public int reportNumberScheme = NUM_ABBO;
  public String reportNumberSchemes[] = { translate("NumNone"),
      translate("NumAbbo") };

  public int reportMaxGenerations = 999;

  public boolean showAllPlaceJurisdictions = false;

  public boolean reportPlaceOfBirth = true;

  public boolean reportDateOfBirth = true;

  public boolean reportPlaceOfDeath = true;

  public boolean reportDateOfDeath = true;

  public boolean reportPlaceOfMarriage = true;

  public boolean reportDateOfMarriage = true;

  public boolean reportPlaceOfOccu = true;

  public boolean reportDateOfOccu = true;

  public boolean reportPlaceOfResi = true;

  public boolean reportDateOfResi = true;

  public boolean reportMailingAddress = true;
  
  public boolean reportIds = true;

  
  	private Output output;

  
  public int publicGen = 0;

  
  public Object start(Indi indi) {
    return start( new Indi[] { indi }, translate("title.descendant", indi.getName()));
  }

  
  public Object start(Indi[] indis) {
    return start( indis, getName() + " - " + indis[0].getGedcom().getName());
  }

  
  private Document start(Indi[] indis, String title) {

	  switch (reportFormat){
	  case TABLE:
		  output = new OutputTable();
		  break;
	  case ONE_LINE:
	  case ONE_EVT_PER_LINE:
		  output = new OutputStandard();
		  break;
      default:
          throw new IllegalArgumentException("no such report type");
	  }
	  
    HashMap done = new HashMap();

    
    PrivacyPolicy policy = OPTIONS.getPrivacyPolicy();

    nbColumns = 2;
    if (reportPlaceOfBirth || reportDateOfBirth)
      nbColumns++;
    if (reportPlaceOfMarriage || reportDateOfMarriage)
      nbColumns++;
    if (reportPlaceOfDeath || reportDateOfDeath)
      nbColumns++;
    if (reportPlaceOfOccu || reportDateOfOccu)
      nbColumns++;
    if (reportPlaceOfResi || reportDateOfResi)
      nbColumns++;

    Document doc = new Document(title);

    
    for (int i = 0; i < indis.length; i++) {
      Indi indi = indis[i];
      output.title(indi,doc);
      iterate(indi, 1, (new Integer(i+1).toString()), done, policy, doc);
    }

    output.statistiques(doc);

    
    return doc;

  }

  
  private void iterate(Indi indi, int level, String num, HashMap done, PrivacyPolicy policy, Document doc) {

    nbIndi++;
    if (indi!=null&&!indi.isDeceased()) nbLiving ++;

    
    if (level > reportMaxGenerations)
      return;

    
    PrivacyPolicy localPolicy = level < publicGen + 1 ? PrivacyPolicy.PUBLIC : policy;

    output.startIndi(doc);
    format(indi, (Fam)null, num, localPolicy, doc);

    
    Fam[] fams = indi.getFamiliesWhereSpouse();
    for (int f = 0; f < fams.length; f++) {

      
      Fam fam = fams[f];

      Indi spouse = fam.getOtherSpouse(indi);

      
      output.startSpouse(doc);
        if (fams.length==1)
    	    format(spouse,fam,num+"x", localPolicy, doc);
    	else
    	    format(spouse,fam,num+"x"+(f+1), localPolicy, doc);

      
      if (done.containsKey(fam)) {
    	  output.link(fam,(String)done.get(fam),doc);
      } else {

   	    output.anchor(fam, doc);
          done.put(fam,num);
        nbIndi++;
        nbFam++;
        if (spouse!=null&&!spouse.isDeceased()) nbLiving ++;

        
        Indi[] children = fam.getChildren();
        for (int c = 0; c < children.length; c++) {
          
          if (fams.length == 1)
            iterate(children[c], level + 1, num+'.'+(c+1), done, policy, doc);
          else
            iterate(children[c], level + 1, num+'x'+(f+1)+'.'+(c+1), done, policy, doc);

          
        }

      }
      
    }

    
    output.endIndi(indi, doc);
  }

  
  private void format(Indi indi, Fam fam, String prefix, PrivacyPolicy policy, Document doc) {

    
    if (indi == null)
      return;

    
    output.number(prefix,doc);
    output.name(policy.getDisplayValue(indi, "NAME"),doc);
    if (reportIds)
      output.id(indi.getId(),doc);

    String birt = output.format(indi, "BIRT", OPTIONS.getBirthSymbol(), reportDateOfBirth, reportPlaceOfBirth, policy);
    String marr = fam!=null ? output.format(fam, "MARR", OPTIONS.getMarriageSymbol(), reportDateOfMarriage, reportPlaceOfMarriage, policy) : "";
    String deat = output.format(indi, "DEAT", OPTIONS.getDeathSymbol(), reportDateOfDeath, reportPlaceOfDeath, policy);
    String occu = output.format(indi, "OCCU", "{$T}", reportDateOfOccu, reportPlaceOfOccu, policy);
    String resi = output.format(indi, "RESI", "{$T}", reportDateOfResi, reportPlaceOfResi, policy);
    PropertyMultilineValue addr = reportMailingAddress ? indi.getAddress() : null;
    if (addr != null && policy.isPrivate(addr)) addr = null;

    

    	output.startEvents(doc);

    String[] infos = new String[] { birt, marr, deat, occu, resi };
    for (int i=0, j=0; i<infos.length ; i++) {
    	output.event(infos[i],doc);
    }
	if (addr != null) {
		output.addressPrefix(doc);
		String[] lines = addr.getLines();
		output.startEvents(doc);
		for (int i = 0; i < lines.length; i++) {
			output.event(lines[i],doc);
		}
	    output.endEvents(doc);
	}
    output.endEvents(doc);
    
  }

  abstract class Output{
	  abstract void title(Indi indi, Document doc);
	  abstract void statistiques(Document doc);
	  abstract void startIndi(Document doc);
	  abstract void startSpouse(Document doc);
	  abstract void link(Fam fam, String label, Document doc);
	  abstract void anchor(Fam fam, Document doc);
	  abstract void endIndi(Indi indi, Document doc);
	  abstract void name(String name, Document doc);
	  abstract void id(String id, Document doc);
	  abstract void startEvents(Document doc);
	  abstract void endEvents(Document doc);
	  abstract void event(String event, Document doc);
	  abstract void number(String num, Document doc);
	  abstract void addressPrefix(Document doc);

	  private HashMap format(Indi indi, Fam fam, String prefix, PrivacyPolicy policy) {
		  HashMap result = new HashMap();
		  
		  if (indi == null)
			  return null;

		  result.put("birt", format(indi, "BIRT", OPTIONS.getBirthSymbol(), reportDateOfBirth, reportPlaceOfBirth, policy));
		  result.put("marr", fam!=null ? format(fam, "MARR", OPTIONS.getMarriageSymbol(), reportDateOfMarriage, reportPlaceOfMarriage, policy) : "");
		  result.put("deat", format(indi, "DEAT", OPTIONS.getDeathSymbol(), reportDateOfDeath, reportPlaceOfDeath, policy));
		  result.put("occu", format(indi, "OCCU", "{$T}{ $V}", reportDateOfOccu, reportPlaceOfOccu, policy));
		  result.put("resi", format(indi, "RESI", "{$T}", reportDateOfResi, reportPlaceOfResi, policy));
		  PropertyMultilineValue addr = reportMailingAddress ? indi.getAddress() : null;
		  if (addr != null && policy.isPrivate(addr)) addr = null;
		  result.put("addr", addr);
		  return result;

	  }
	  
	  String format(Entity e, String tag, String prefix, boolean date, boolean place, PrivacyPolicy policy) {

	    Property prop = e.getProperty(tag);
	    if (prop == null)
	      return "";

	    String format = prefix + "{ $v}"+(date ? "{ $D}" : "")
	        + (place && showAllPlaceJurisdictions ? "{ $P}" : "")
	        + (place && !showAllPlaceJurisdictions ? "{ $p}" : "");

	    return prop.format(format, policy);

	  }

  }
  class OutputStandard extends Output{
	  private boolean isFirstEvent = true;
	  void title(Indi indi, Document doc){
	      doc.startSection( translate("title.descendant", indi.getName()) );
	  }
	  void statistiques(Document doc){
		  doc.startSection( translate("title.stats") );
		  doc.addText( translate("nb.fam", nbFam) );
		  doc.nextParagraph();
		  doc.addText( translate("nb.indi", nbIndi) );
		  doc.nextParagraph();
		  doc.addText( translate("nb.living", nbLiving) );
	  }
	  void startIndi(Document doc){
		  doc.startList();
	  }
	  void startSpouse(Document doc){
	  }
	  void link(Fam fam,String label, Document doc){
    	  doc.nextParagraph();
        doc.addText("====> " + translate("see") +" ");
        if (reportNumberScheme != NUM_NONE)
        	doc.addLink(label, fam);
        else
        	doc.addLink(fam.getDisplayValue(), fam);
	  }
	  void anchor(Fam fam, Document doc){
	   	    doc.addAnchor(fam);
	  }
	  void endIndi(Indi indi, Document doc){
		  doc.endList();
	  }
	  void number(String number, Document doc){
		  
		  doc.nextParagraph();
		  if (reportNumberScheme != NUM_NONE)
			  doc.nextListItem("genj:label="+number);
	  }
	  void name(String name, Document doc){
		  doc.addText(name, FORMAT_STRONG);
	  }
	  void id(String id, Document doc){
		  doc.addText(" (" + id + ")" );
	  }
	  void startEvents(Document doc){
		  if (reportFormat!=ONE_LINE)
			  doc.startList();
		  isFirstEvent = true;
	  }
	  void endEvents(Document doc){
		  if (reportFormat!=ONE_LINE)
			  doc.endList();
	  }
	  void event(String event, Document doc){
	      if (event.length()==0)
	    	  return;
	      
	      if (!isFirstEvent) {
	    	  if (reportFormat==ONE_LINE)  doc.addText(", ");
	    	  else doc.nextListItem();
	      }
	      doc.addText(event);
	      isFirstEvent = false;
	  }
	  void addressPrefix(Document doc){
	      
	      if (!isFirstEvent) {
	    	  if (reportFormat==ONE_LINE)  doc.addText(", ");
	    	  else doc.nextListItem();
	      }
	  }
  }

  



  class OutputTable extends Output{

	  String format(Entity e, String tag, String prefix, boolean date, boolean place, PrivacyPolicy policy) {
		  return super.format(e,tag,"",date,place,policy);
	  }

	void title(Indi indi, Document doc) {
		  doc.startTable("genj:csv=true");

		  doc.nextTableRow();

		  doc.nextTableCell("number-columns-spanned=7,"+FORMAT_STRONG );
		  doc.addText(translate("title.descendant", indi.getName()) );

		  doc.nextTableRow();
		  doc.addText( translate("num.col"),FORMAT_STRONG );
		  doc.nextTableCell();
		  doc.addText( Gedcom.getName("NAME"),FORMAT_STRONG );
		  doc.nextTableCell();
		  doc.addText( Gedcom.getName("BIRT"),FORMAT_STRONG );
		  doc.nextTableCell();
		  doc.addText( Gedcom.getName("MARR"),FORMAT_STRONG );
		  doc.nextTableCell();
		  doc.addText( Gedcom.getName("DEAT"),FORMAT_STRONG );
		  doc.nextTableCell();
		  doc.addText( Gedcom.getName("OCCU"),FORMAT_STRONG );
		  doc.nextTableCell();
		  doc.addText( Gedcom.getName("RESI"),FORMAT_STRONG );
		  doc.nextTableCell();
		  doc.addText( translate("addr1.col"),FORMAT_STRONG );
		  doc.nextTableCell();
		  doc.addText( translate("addr2.col"),FORMAT_STRONG );
		  doc.nextTableCell();
		  doc.addText( translate("addr3.col"),FORMAT_STRONG );
		  doc.nextTableCell();
		  doc.addText( translate("addr4.col"),FORMAT_STRONG );
		  doc.nextTableCell();
		  doc.addText( translate("addr5.col"),FORMAT_STRONG );
	}

	void statistiques(Document doc) {
		  doc.startSection( translate("title.stats") );
		  doc.addText( translate("nb.fam", nbFam) );
		  doc.nextParagraph();
		  doc.addText( translate("nb.indi", nbIndi) );
		  doc.nextParagraph();
		  doc.addText( translate("nb.living", nbLiving) );
	}

	void startIndi(Document doc) {
	    
		doc.nextTableRow();
	}

	void startSpouse(Document doc) {
	    
		doc.nextTableRow();
	}

	void link(Fam fam, String label, Document doc) {
		doc.nextTableRow();
		  doc.nextTableCell();
		  doc.nextTableCell();
      doc.addText("====> " + translate("see") +" ");
      if (reportNumberScheme != NUM_NONE)
      	doc.addText(label);
      else
      	doc.addText(fam.getDisplayValue());
	}

	void anchor(Fam fam, Document doc) {
	}

	void endIndi(Indi indi, Document doc) {
	}

	void name(String name, Document doc) {
		doc.nextTableCell();
		doc.addText(name, FORMAT_STRONG);
	}

	void id(String id, Document doc) {
		doc.addText(" (" + id + ")" );
	}

	void startEvents(Document doc) {
	}

	void endEvents(Document doc) {
	}

	void event(String event, Document doc) {
		doc.nextTableCell();
		doc.addText(event);
	}

	void number(String num, Document doc) {
		doc.nextTableCell();
		doc.addText(num);
	}

	void addressPrefix(Document doc){
	}
  }
} 
