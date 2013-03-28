
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Pattern;

import genj.fo.Document;
import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.MultiLineProperty;
import genj.gedcom.Note;
import genj.gedcom.Property;
import genj.gedcom.PropertyFile;
import genj.gedcom.PropertyName;
import genj.gedcom.PropertyPlace;
import genj.gedcom.PropertyXRef;
import genj.gedcom.TagPath;
import genj.report.Report;

import javax.swing.ImageIcon;


public class ReportSummaryOfRecords extends Report {

  private final static TagPath PATH2IMAGES = new TagPath("INDI:OBJE:FILE");

  
  public  int generatePlaceIndex = 0;
  public String[] generatePlaceIndexs = {
    translate("place.index.none"), translate("place.index.one"), translate("place.index.each")
  };

  
  private  int maxImagesPerRecord = 4;

  
  public boolean includeIds = true;
  
  
  public boolean sortProperties = false;
  
  
  public String filterProperties = "CHAN";
  
  
  protected ImageIcon getImage() {
    return Report.IMG_FO;
  }

  public int getMaxImagesPerRecord() {
    return maxImagesPerRecord;
  }

  public void setMaxImagesPerRecord(int set) {
    maxImagesPerRecord = Math.max(0,set);
  }

  
  public boolean usesStandardOut() {
    return false;
  }

  
  public void start(Gedcom gedcom) {

    
    Document doc = new Document(translate("title", gedcom.getName()));

    doc.addText("This report shows information about all records in the Gedcom file "+gedcom.getName());
    
    
    Pattern tagFilter = null;
    try {
      if (filterProperties.length()>0)
        tagFilter = Pattern.compile(filterProperties);
    } catch (IllegalArgumentException e) {
       println("Filter for properties is not a valid regular expression ("+e.getMessage()+")");
    }

    
    exportEntities(gedcom.getEntities(Gedcom.INDI, "INDI:NAME"), doc, tagFilter);
    exportEntities(gedcom.getEntities(Gedcom.FAM, "FAM:HUSB:*:..:NAME"), doc, tagFilter);
    exportEntities(gedcom.getEntities(Gedcom.NOTE, "NOTE"), doc, tagFilter);

    
    doc.nextPage();

    
    showDocumentToUser(doc);
  }

  
  private void exportEntities(Entity[] ents, Document doc, Pattern tagFilter)  {
    for (int e = 0; e < ents.length; e++) {
      exportEntity(ents[e], doc, tagFilter);
    }
  }

  
  private void exportEntity(Entity ent, Document doc, Pattern tagFilter) {

    println(translate("exporting", ent.toString() ));

    
    doc.startSection( ent.toString(this.includeIds), ent );

    
    doc.startTable("width=100%");
    doc.addTableColumn("column-width=80%");
    doc.addTableColumn("column-width=20%");

    
    exportProperties(ent, doc, tagFilter, 0);

    
    doc.nextTableCell();
    Property[] files = ent.getProperties(PATH2IMAGES);
    for (int f=0;f<files.length && f<maxImagesPerRecord; f++) {
      PropertyFile file = (PropertyFile)files[f];
      doc.addImage(file.getFile(),"");
    }

    
    doc.endTable();
  }

  
  private void exportProperties(Property of, Document doc, Pattern tagFilter, int level) {

    
    if (of.getNoOfProperties()==0)
      return;

    
    doc.startList();
    
    
    Property[] props = of.getProperties();
    if (sortProperties)
      Arrays.sort(props, new Comparator() {
        public int compare(Object p1, Object p2) {
          return Gedcom.getName( ((Property)p1).getTag() ).compareTo( Gedcom.getName( ((Property)p2).getTag()) );
        }
      });

    
    for (int i=0;i<props.length;i++) {

      Property prop = props[i];
      
      if (tagFilter!=null&&tagFilter.matcher(prop.getTag()).matches())
        continue;

      
      if (prop instanceof PropertyXRef) {
        PropertyXRef xref = (PropertyXRef)prop;
        if (xref.isTransient() || !(xref.getTargetEntity() instanceof Indi||xref.getTargetEntity() instanceof Fam||xref.getTargetEntity() instanceof Note))
          continue;
      }

      
      doc.nextListItem();

      
      if (prop instanceof PropertyName) {
        PropertyName name = (PropertyName)prop;
        doc.addIndexTerm(translate("index.names"), name.getLastName(), name.getFirstName());
      }
      if (generatePlaceIndex>0&&(prop instanceof PropertyPlace)) {
        String index = generatePlaceIndex==1 ? translate("index.places") : translate("index.places.of", prop.getParent().getPropertyName());
        doc.addIndexTerm(index, ((PropertyPlace)prop).getCity());
      }

      
      String format = "";
      if (level==0) format = "text-decoration=underline";
      if (level==1) format =  "font-style=italic";
      doc.addText(Gedcom.getName(prop.getTag()), format);
      doc.addText(" ");

      
      exportPropertyValue(prop, doc);

      
      exportProperties(prop, doc,  tagFilter, level+1);
    }
    doc.endList();
  }

  
  private void exportPropertyValue(Property prop, Document doc) {

    
    if (prop instanceof PropertyXRef) {

      PropertyXRef xref = (PropertyXRef)prop;
      Entity ent = xref.getTargetEntity();
      doc.addLink(ent.toString(includeIds), ent);

      
      return;
    }

    
    if (prop instanceof MultiLineProperty) {
      MultiLineProperty.Iterator lines = ((MultiLineProperty)prop).getLineIterator();
      do {
        doc.addText(lines.getValue());
      } while (lines.next());
      
      return;
    }

    
    String value;
    if (prop instanceof PropertyName)
      value = ((PropertyName)prop).getName();
    else
      value = prop.getDisplayValue();

    doc.addText(value);

    
  }


} 
