
package narrative;

import genj.fo.Document;
import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.PropertyDate;
import genj.gedcom.PropertyEvent;
import genj.gedcom.PropertySex;
import genj.gedcom.PropertySource;
import genj.gedcom.PropertyXRef;
import genj.gedcom.Source;
import genj.gedcom.TagPath;
import genj.gedcom.time.Delta;
import genj.gedcom.time.PointInTime;
import genj.report.Report;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.io.File;

import javax.swing.ImageIcon;


public class ReportNarrative extends Report {

  public static final int DETAIL_NO_SHOW  = 0;
  public static final int DETAIL_NAME = 1;
  public static final int DETAIL_BRIEF = 2;
  public static final int DETAIL_DATES = 3;
  public static final int DETAIL_BRIEF_WITH_DATES = 2;
  public static final int DETAIL_FULL = 5;
  public static final int DETAIL_EVERYTHING = 6;

  public boolean ancestors = true;
  public boolean showIds = false;
  public boolean showRefns = false;
  public boolean withNameIndex = true;
  public boolean withPlaceIndex = true;
  private boolean withBibliography = false; 
  public boolean showImages = true;
  public boolean includePersonalTags = false;
  public boolean includeUnknownTags = false;
  public boolean useAbbrevations = false;
  public int maxGenerations = 99;

  public String htmlStylesheet = null;
  private boolean alignImages = true; 
  public boolean noAds = false;

  private String nameIndexTitle;
  private String placeIndexTitle;
  private String sourceIndexTitle;

  
  public boolean usesStandardOut() {
    return false;
  }

  
  protected ImageIcon getImage() {
    return Report.IMG_FO;
  }

  
  public Object start(Gedcom gedcom) {

    String resource = ancestors ? "ancestors.of" : "descendants.of";
    Indi indi = (Indi)getEntityFromUser(translate(resource), gedcom, Gedcom.INDI); 
    if (indi==null)
      return null;

    return start(indi);
  }

  
  public Document start(Indi indi) {

    println("indi = " + indi.getName());

    nameIndexTitle = withNameIndex ? translate("index.names") : null;
    placeIndexTitle = withPlaceIndex ? translate("index.places") : null;
    sourceIndexTitle = withBibliography ? translate("bibliography") : null;

    

    
    String title = getUtterance(ancestors ? "doc.ancestors.title" : "doc.descendants.title",
                                new String[] { new IndiWriter(indi, null).getName(indi) }).toString();
    Document doc = new Document(title);
    doc.startSection(title, 1);

    
    
    
    if (!noAds) {
      
      
      Utterance ad = getUtterance("doc.ad.1");

      PropertyDate dateFormatter = new PropertyDate();
      dateFormatter.setValue(PropertyDate.DATE, PointInTime.getPointInTime(System.currentTimeMillis()), null, "");
      
      ad.set("DATE", dateFormatter.getDisplayValue());
      doc.addText(ad.toString());
      doc.addText(" ");
      doc.addExternalLink("GenealogyJ", "http://genj.sourceforge.net");
      ad = getUtterance("doc.ad.2");
      ad.set("DATE", new Date().toString());
      doc.addText(ad.toString());
    }


    Set printed = new HashSet();
    Set gen = new HashSet();
    gen.add(indi);
    Set nextGen;
    int generationNumber = 1;
    do {
      nextGen = printGenerations(doc, generationNumber, gen, printed);
      generationNumber++;
      gen = nextGen;
    } while(gen.size() > 0 && generationNumber <= maxGenerations);

    if (withNameIndex)
      println(translate("log.printingNameIndex")); 

    if (withPlaceIndex)
      println(translate("log.printingPlaceIndex"));

    if (withBibliography) {
      
      
    }

    
    println(translate("log.finished"));
    return doc;

  }





















  private Utterance getUtterance(String key) {
    return getUtterance(key, new String[0]);
  }

  private Utterance getUtterance(String key, String[] params) {
    String template1 = translate(key);
    if (template1 == null) template1 = key;

    return Utterance.forTemplate(getResources(), template1, params);




  }

  private Set printGenerations(Document doc, int n, Set gen, Set printed) {

    Utterance generations = getUtterance("individuals.in.generation",
        new String[] {
          Integer.toString(gen.size()),
          Integer.toString(n)
        });
    println(generations.toString());

    Set nextGen = new LinkedHashSet(); 

    Utterance docTitle = getUtterance("section.title", new String[] { Integer.toString(n) });
    doc.startSection(docTitle.toString(), 2);

    for (Iterator i = gen.iterator(); i.hasNext();) {

      Indi indi = (Indi) i.next();
      IndiWriter writer = new IndiWriter(indi, doc);

      String sectionTitle = indi.getName();
      Property title = indi.getProperty("TITL");
      if (title != null) {
        if (title.getValue().indexOf(" of ") != -1 || title.getValue().startsWith("of ")) {
          
          sectionTitle += ", " +title;
        } else {
          sectionTitle = title + " " + sectionTitle;
        }
      }

      
      if (printed.contains(indi)) {
        doc.startSection(sectionTitle, 3); 
        doc.addLink("Refer to entry via different lineage", indi);
      } else {
        doc.startSection(sectionTitle, indi, 3); 
        if (withNameIndex) {
          doc.addIndexTerm(nameIndexTitle, indi.getLastName(), indi.getFirstName());
        }
        boolean showKids = indi.getSex() == PropertySex.MALE; 
        writer.writeEntry(showKids, DETAIL_FULL, true,  false, showImages);
      }

      addNextGeneration(nextGen, indi);

      printed.add(indi); 
    }







    return nextGen;
  }

  
  private void addNextGeneration(Set indis, Indi indi) {

    if (ancestors) {
      Indi parent = indi.getBiologicalFather();
      if (parent != null)
        indis.add(parent);
      parent = indi.getBiologicalMother();
      if (parent != null)
        indis.add(parent);
    } else {
      Indi[] children = indi.getChildren();
      for (int j = 0; j < children.length; j++) {
        indis.add(children[j]);
      }
    }
  }

  
  public class IndiWriter {

    private Indi indi;
    private Document doc;

    
    public IndiWriter(Indi indi, Document doc) {
      this.indi = indi;
      this.doc = doc;
    }

    
    
    private final Set INDIVIDUAL_ATTRIBUTES = new HashSet(Arrays.asList(
        new String[] {
        "CAST", 
        "DSCR", 
        "EDUC", 
        "IDNO", 
        "NATI", 
        "NCHI", 
        "NMR", 
        "PROP", 
        "RELI", 
        "SSN", 
        
     }
    ));
    
    

    
    public String getNamePlusIdAndReference(Indi i) {
      StringBuffer name = new StringBuffer(i.getFirstName());
      appendName(name, i.getLastName());
      appendName(name, i.getNameSuffix());
      boolean doShowRefn = showRefns && i.getProperties("REFN").length > 0;
      if (showIds || doShowRefn) {
        name.append(" (");
        if (showIds) {
          name.append(i.getId());
          if (doShowRefn) name.append("; ");
        }
        if (doShowRefn) {
          Property[] refns = i.getProperties("REFN"); 
          for (int j = 0; j < refns.length; j++) {
            Property refn = refns[j];
            if (j > 0)name.append(", ");
            name.append(refn.getValue());
          }
        }
        name.append(")");
      }
      return name.toString();
    }

    
    public String getName(Indi i) {
      StringBuffer name = new StringBuffer(i.getFirstName());
      appendName(name, i.getLastName());
      appendName(name, i.getNameSuffix());
      return name.toString();
    }

    private void appendName(StringBuffer name, String element) {
      if (element != null && element.length() > 0) {
        name.append(' ');
        name.append(element);
      }
    }

    public void writeEntry(boolean withChildren, int defaultDetailLevel, boolean withParents, boolean linkToIndi, boolean showImages) {

      int detailLevel = defaultDetailLevel;
      try {

        
        
        if (withNameIndex) {
          doc.addIndexTerm(nameIndexTitle, indi.getLastName(), indi.getFirstName());
        }

        
        if (showImages && alignImages && detailLevel >= DETAIL_FULL) {
          insertImages();
        }
        

        if (linkToIndi)
          doc.addLink(getNamePlusIdAndReference(indi), indi);
        else
          doc.addText(getNamePlusIdAndReference(indi));
          
          
          
          
          
          

        
        
        
        
        
        

        
        
        
        
        
        
        
        
        
        
        
        
        
        
        

        if (detailLevel >= DETAIL_DATES) {
          String date = getDateString(indi.getBirthDate());
          if (date.length() > 0) {
            doc.addText(", ");
            addGenderSpecificUtterance("born", indi, date);





          }

          
          Indi father = indi.getBiologicalFather(), mother = indi.getBiologicalMother();
          if (withParents && (father != null || mother != null)) {
            

            Utterance parenPhrase = null;
            if (indi.getBiologicalFather() != null && indi.getBiologicalMother() != null) {
              parenPhrase = Utterance.forProperty(getResources(), "phrase.childof.parents",
                new String[] { getName(indi.getBiologicalFather()), getName(indi.getBiologicalMother()) },
                  new Entity[] { indi.getBiologicalFather(), indi.getBiologicalMother() });
              
            } else {
              Indi parent = indi.getBiologicalFather() != null ? indi.getBiologicalFather()
                                 : indi.getBiologicalMother();
              parenPhrase = Utterance.forProperty(getResources(), "phrase.childof.parent",
                  new String[] { getName(parent) },
                  new Entity[] { parent } );
            }
            if (parenPhrase != null) {
              parenPhrase.setSubject(indi);
              doc.addText(" (");
              parenPhrase.addText(doc); 
              doc.addText(")");
            }
          }

          Fam[] fams = indi.getFamiliesWhereSpouse();
          for (int i = 0; i < fams.length; i++) {
            Fam fam = fams[i];
            PropertyDate marriage = fam.getMarriageDate();
            doc.addText(", ");
            if (!useAbbrevations) {
              addUtterance(indi, genderSpecificKey("phrase.married", indi.getSex()), date);
            } else {
              addUtterance("abbrev.married");
            }
            doc.addText(" ");
            if (fams.length > 1) doc.addText("(" + (i+1) + ") ");
            if (marriage != null) doc.addText(getDateString(marriage));
            Property age = (indi.getSex() == PropertySex.MALE)
              ? fam.getProperty(new TagPath("FAM:HUSB:AGE"))
              : fam.getProperty(new TagPath("FAM:WIFE:AGE"));
            if (age != null) {
               doc.addText(Utterance.forProperty(getResources(), "phrase.at_age", new String[] { age.getValue() }).toString());
            }
            Indi spouse = fam.getOtherSpouse(indi);
            if (spouse == null) {
              addUtterance("phrase.spouses_name_unknown");
            } else {
              if (marriage != null) {
                doc.addText(" ");
                addUtterance("prep.married_to");
              }
              doc.addText(" ");
              doc.addLink(getNamePlusIdAndReference(spouse), spouse);
            }
            
          }
          date = getDateString(indi.getDeathDate());
          if (date.length() > 0) {
            doc.addText(", ");
            if (!useAbbrevations) {
              addUtterance(indi, genderSpecificKey("phrase.died", indi.getSex()), date);
            } else {
              addUtterance("abbrev.died", date);
            }
          }
          



          doc.addText(".");

          Set tagsProcessed = new HashSet(Arrays.asList(new String[] {
            "REFN", "CHAN", "SEX", "BIRT", "DEAT", "FAMC", "FAMS",
            "NAME", 
            "OBJE",
            "ASSO", 
          }));
          
          if (detailLevel >= DETAIL_FULL) {

            Property[] props = indi.getProperties();
            for (int i = 0; i < props.length; i++) {
              Property prop = props[i];
              if (tagsProcessed.contains(prop.getTag())) {
                
                continue;
              }

              
              
              int numberOfLikeProperties = 0;
              for (int j = i+1; j < props.length; j++) {
                  if (props[j].getPropertyName().equals(props[i].getPropertyName())) {
                    numberOfLikeProperties++;
                  } else {
                    break;
                  }
              }
              Property[] likeProps = new Property[numberOfLikeProperties+1];
              numberOfLikeProperties = 0;
              for (int j = i; j < props.length; j++) {
                  if (props[j].getPropertyName().equals(props[i].getPropertyName())) {
                    likeProps[numberOfLikeProperties] = props[j];
                    numberOfLikeProperties++;
                  } else {
                    break;
                  }
              }

              doc.addText(" ");

              
              if (prop instanceof PropertyEvent) {
                i += (writeEvents(likeProps) -1);
              } else if (INDIVIDUAL_ATTRIBUTES.contains(prop.getTag())) {
                i += (writeEvents(likeProps) -1);
              } else if (prop.getTag().equals("RESI") || prop.getTag().equals("ADDR")) {
                i += (writeEvents(likeProps) -1);
              } else if (prop.getTag().equals("OCCU")) {
                if (prop.getValue().length() > 0) {
                  doc.addText(" ");
                  boolean past = true;
                  if (indi.getDeathDate() == null) {
                    Delta age = indi.getAge(PointInTime.getPointInTime(System.currentTimeMillis()));
                    if (age != null && age.getYears() < 65) past = false;
                  }
  
  
  
  
  
  
  

                  Utterance u = Utterance.forProperty(getResources(), "sentence.OCCU",
                        new String[] { prop.getValue() } );
                  
                  u.setSubject(indi);
                  u.set("tense", past ? "past" : "present"); 
                  doc.addText(u.toString());

                  writeNodeSource(prop);
                }
              } else if (prop.getTag().equals("NOTE")) {
                if (prop instanceof PropertyXRef) {
                  Entity ref = ((PropertyXRef)prop).getTargetEntity();
                  addUtterance("phrase.note", ref.getValue());
                   
                  Property source = ref.getProperty("SOUR");
                  if (source != null) {
                      writeSource((Source) ((PropertySource) source).getTargetEntity());
                  }
                  
                  
                } else {
                  addUtterance("phrase.note", prop.getValue());
                }
              } else if (prop.getTag().equals("SOUR") && prop instanceof PropertySource) {
                writeSource((Source) ((PropertySource) prop).getTargetEntity());
              } else if (prop.getTag().equals("SOUR")) {
                
                addUtterance("phrase.source", prop.getValue());
              } else if (prop.getTag().startsWith("_")) {
                  if (detailLevel >= DETAIL_EVERYTHING) {
                    
                    addUtterance("phrase.property", prop.getValue()); 
                  }
              } else {
                
                addUtterance("phrase.property", prop.getValue());
              }
            }
          } else if (detailLevel <= DETAIL_BRIEF_WITH_DATES) {
            
          }
        }

        if (withChildren ) {
          
          
          Indi[] children = indi.getChildren();
          if (children.length > 0) {
            Fam[] families = indi.getFamiliesWhereSpouse();
            if (families.length > 1) {
              doc.startList();
            }
            for (int i = 0; i < families.length; i++) {
              Fam family = families[i];
              if (families.length > 1) {
                doc.nextListItem("genj:label="+(i+1)+".");
                doc.addText(
                  Utterance.forProperty(getResources(), "phrase.children.of.parents",
                    new String[] { getName(family.getHusband()), getName(family.getWife()) },
                      new Entity[] { family.getHusband(), family.getWife() }).toString());
              }
              children = family.getChildren();
              doc.startList();
              for (int j = 0; j < children.length; j++) {
                doc.nextListItem("genj:label="+(j+1)+".");
                Indi child = children[j];
                IndiWriter w = new IndiWriter(child, doc);
                
                w.writeEntry( false, DETAIL_DATES,
                     false,  true, false);
              }
              doc.endList();
            }
            if (families.length > 1) {
              doc.endList();
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
        addUtterance("sentence.error");
      }

      



    }

    
    private void addGenderSpecificUtterance(String key, Indi indi, String param1) {
      
      String abbrevKey = "abbrev" + key;
      if (useAbbrevations && Utterance.isTranslatable(abbrevKey, getResources())) {
        addUtterance(indi, abbrevKey, param1); 
      } else {
        addUtterance(indi, genderSpecificKey("phrase." + key, indi.getSex()), param1);
      }
    }

    private void addUtterance(String key) {
      doc.addText(Utterance.forProperty(getResources(), key).toString());
    }

    private void addUtterance(String key, String value1) {
      doc.addText(Utterance.forProperty(getResources(), key, new String[] { value1 } ).toString());
    }

    private void addUtterance(String key, String[] values) {
      doc.addText(Utterance.forProperty(getResources(), key, values).toString());
    }

    private void addUtterance(Indi indi, String key) {
      Utterance u = Utterance.forProperty(getResources(), key);
      u.setSubject(indi);
      doc.addText(u.toString());
    }

    private void addUtterance(Indi indi, String key, String value1) {
      Utterance u = Utterance.forProperty(getResources(), key, new String[] { value1 } );
      u.setSubject(indi);
      doc.addText(u.toString());
    }

    private void addUtterance(Indi indi, String key, String[] values) {
      Utterance u = Utterance.forProperty(getResources(), key, values);
      u.setSubject(indi);
      doc.addText(u.toString());
    }

    
    private String getDateString(Property prop) {
      if (prop == null || !prop.isValid())
        return "";
      return prop.getDisplayValue();
    }

    private void insertImages() {
      
      
      
      
      
      
      
      
      
      
      
        
        
        Property[] props = indi.getProperties(new TagPath("INDI:OBJE"));
        for (int i = 0; i < props.length; i++) {
          Property prop = props[i];
          if (prop.getProperty("FILE") != null &&
              (isImagePath(prop.getProperty("FILE")) || isImagePath(prop.getProperty("FORM")))) {
            if (!alignImages) {
              doc.nextParagraph(); 
              addImage(prop.getProperty("FILE").getValue());
              
              if (prop.getProperty("NOTE") != null) doc.addText(prop.getProperty("NOTE").getValue());
              doc.nextParagraph();
            } else {
              
              addImage(prop.getProperty("FILE").getValue());
              
              
            }
          }
        }
    }

    private void addImage(String urlOrPath) {
      
      
      String attrs = ""; 
      if (urlOrPath.startsWith("file:///")) {
        doc.addImage(new File(urlOrPath.substring(8)), attrs);
      } else {
        doc.addImage(new File(urlOrPath), attrs);
      }
    }

    private boolean isImagePath(Property property) {
      
      if (property == null) return false;
      String path = property.getValue();
      return path.endsWith("jpg")
              ||
          path.endsWith("JPG")
          ||
          path.endsWith("gif")
          ||
          path.endsWith("GIF");
    }

  private void writeNodeSource(Property node) {
    Property prop = node.getProperty("SOUR");
    if (prop != null && prop instanceof PropertySource) {
      writeSource((Source) ((PropertySource) prop).getTargetEntity());
    }
  }

    private void writeSource(Source prop) {
      
      

      
      if (sourceIndexTitle != null) {
        String key = prop.getProperty("REFN") != null ? prop.getProperty("REFN").getValue()
            : prop.getId();
        doc.addLink("[" + key + "]", prop); 
        
      }

      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      

      Utterance u = getSentenceForTag("phrase.SOUR");
      String [] tags = new String[] { "REFN", "TYPE", "TITL", "AUTH", "EDIT",
                                      "INTV", "INFT", "OWNR" };
      for (int i = 0; i < tags.length; i++) {
        addOptionalParam(u, prop, tags[i]);
      }
      if (prop.getProperty("PAGE") != null) {
        u.set("OPTIONAL_PAGE", ", " + Utterance.forProperty(getResources(), "abbrev.page") + " " + prop.getProperty("PAGE").getValue());
      }
      String date = getDatePhrase(prop);
      if (date.length() > 0) {
        u.set("OPTIONAL_DATE", date);
      }
      doc.addText(" " + u.toString() + "");

      
    }

  private void addOptionalParam(Utterance u, Source prop, String tag) {
    if (prop.getProperty(tag) != null) {
      
      String phraseKey = "phrase." + prop.getTag() + "." + tag;
      String value;
      if (translate(phraseKey) != null) {
        Utterance phrase = Utterance.forTemplate(getResources(), translate(phraseKey),
            new String[] { prop.getProperty(tag).getValue() } );
        value = phrase.toString();
      } else {
        value = prop.getProperty(tag).getValue();
      }
      
      
      
      
      if (tag.equals("TYPE")) {
        value = "(" + value +")";
      } else if (tag.equals("TITL")) {
        value = "\"" + value +"\"";
      }
      u.set("OPTIONAL_" + tag, value);  

    }
  }

    private void writeOptionalProperty(Property prop, String tag, String prolog) {
      writeOptionalProperty(prop, tag, prolog, "");
    }

    private void writeOptionalProperty(Property prop, String tag, String prolog, String epilog) {
      if (prop.getProperty(tag) != null) {
        doc.addText(prolog);
        if (tag.equals("DATE")) {
            doc.addText(getDateString(prop));
        } else {
          doc.addText(prop.getProperty(tag).getValue());
        }
        doc.addText(epilog);
      }
    }

    private int writeEvents(Property[] likeProps) {
      return printEventUtterance(likeProps);

      
    }

    
    private void writeDate(Property prop) {
      String date = getDatePhrase(prop);
      if (date.length() > 0) {
        doc.addText(date);
      }
    }

    
    private String getDatePhrase(Property prop) {
      String date = "";
      PropertyDate propDate = null;
      if (prop instanceof PropertyEvent) {
        propDate = ((PropertyEvent)prop).getDate(true);
        date = getDateString(prop.getProperty("DATE"));
      }
      if (date.length() > 0) {
        if (date.startsWith("FROM")) { 
          date = " " + date;
        } else if (propDate != null && propDate.getStart().getDay() == PointInTime.UNKNOWN) {
          Utterance phrase;
          if (propDate.getStart().getMonth() == PointInTime.UNKNOWN) {
            phrase = Utterance.forProperty(getResources(), "phrase.date.year", new String[] { date });
          } else {
            phrase = Utterance.forProperty(getResources(), "phrase.date.month", new String[] { date });
          }
          date = phrase.toString();
        } else {
          Utterance phrase = Utterance.forProperty(getResources(), "phrase.date.day", new String[] { date });
          date = phrase.toString();
        }
      }
      return date;
    }

    private boolean propertyDefined(String key) {
        return !translate(key).equals(key);
    }

    private boolean propertyDefined(String key, int gender) {
      String suffix = gender == PropertySex.MALE ? ".male"
        : gender == PropertySex.FEMALE ? ".female"
        : ".genderUnknown";
      if (propertyDefined(key + suffix)) {
        return true;
      }
      return propertyDefined(key); 
    }

    private String genderSpecificKey(String key, int gender) {
      String suffix = gender == PropertySex.MALE ? ".male"
        : gender == PropertySex.FEMALE ? ".female"
        : ".genderUnknown";
      if (propertyDefined(key + suffix)) {
        return key + suffix;
      }
      
      return key;
   }

    private int printEventUtterance(Property[] props) {

      String seriesKey = "listFirst." + props[0].getTag();
      String itemKey = "listItem." + props[0].getTag();
      if (props.length == 1 ||
          (!propertyDefined(seriesKey) && !propertyDefined(itemKey))) {
        printEventUtterance(props[0]);
        return 1;
      }

      String list = getListUtterance(props); 

      
      Property prop = props[0];
      Utterance s = getSentenceForTag(prop.getTag(), new String[] { list } );
      s.setSubject(indi);













      doc.addText(" " + s.toString());

      return props.length; 
    }

    
    private String getListUtterance(Property[] props) {
      StringBuffer result = new StringBuffer(100);
      Property prop = props[0];

      
      String listFirst = "listFirst." + prop.getTag();
      if (translate(listFirst).equals(listFirst)) listFirst = "listFirst";
      String listNextKey = "listNext." + prop.getTag();
      if (translate(listNextKey).equals(listNextKey)) listNextKey = "listNext";
      String listLastKey = "listLast." + prop.getTag();
      if (translate(listLastKey).equals(listLastKey)) listLastKey = "listLast";

      Utterance item = getListItemUtterance(prop);
      result.append(Utterance.forTemplate(getResources(), translate(listFirst),
            new String[] { item.toString() }));

      for (int i = 1; i < props.length-1; i++) {
        prop = props[i];
        item = getListItemUtterance(prop);
        result.append(Utterance.forTemplate(getResources(), translate(listNextKey),
            new String[] { item.toString() }));
      }

      prop = props[props.length-1];
      item = getListItemUtterance(prop);
      result.append(Utterance.forTemplate(getResources(), translate(listLastKey),
          new String[] { item.toString() }));

      return result.toString();
   }

   private Utterance getListItemUtterance(Property prop) {
     String listItemKey = "listItem." + prop.getTag();
     Utterance s = Utterance.forTemplate(getResources(), translate(listItemKey), new String[] { prop.getValue() });
     completeEventUtterance(s, prop);
     return s;
   }

    private void printEventUtterance(Property prop) {
      Utterance s = getSentenceForTag(prop.getTag(), new String[] { prop.getValue() } );
      s.setSubject(indi);
      completeEventUtterance(s, prop);
      doc.addText(" " + s.toString());
    }

    private void completeEventUtterance(Utterance s, Property prop) {
      String place = getPlaceString(prop, null);
      if (place.length() > 0) s.set("OPTIONAL_PP_PLACE", place);
      if (prop.getProperty("AGNC") != null) {
        
        Utterance agency = Utterance.forProperty(getResources(), "phrase." + prop.getTag()+ ".AGENCY",
            new String[] { prop.getProperty("AGNC").getValue() });
        s.set("OPTIONAL_AGENCY", agency.toString());
      }
      String date = "";
      if (prop instanceof PropertyEvent) {
        date = getDateString(prop.getProperty("DATE"));
      }
      if (date.length() > 0) s.set("OPTIONAL_PP_DATE", date); 
    }

    private Utterance getSentenceForTag(String tag) {
      return getSentenceForTag(tag, new String[0]);
    }

    private Utterance getSentenceForTag(String tag, String[] params) {
      String template1 = translate("sentence." + tag);
      if (template1 == null) template1 = "{SUBJECT} " + tag + "{OPTIONAL_AGENCY}{OPTIONAL_PP_PLACE}{OPTIONAL_PP_DATE}.";

      Utterance u = Utterance.forTemplate(getResources(), template1, params);
      u.setSubject(indi);
      return u;
      
    }

    private Utterance getPhraseForTag(String tag, String[] params) {
      String template1 = translate("phrase." + tag);
      if (template1 == null) template1 = tag + "{OPTIONAL_AGENCY}{OPTIONAL_PP_PLACE}{OPTIONAL_PP_DATE}.";

      Utterance u = Utterance.forTemplate(getResources(), template1, params);
      return u;
    }

    
  private String getPlaceString(Property prop, String preposition) {


      StringBuffer result = new StringBuffer();
      Property addr = prop.getProperty("ADDR");
      if (addr != null) {
        appendToPlace(result, addr);
        appendToPlace(result, addr.getProperty("ADR1"));
        appendToPlace(result, addr.getProperty("ADR2"));
        appendToPlace(result, addr.getProperty("CITY"));
        
        appendToPlace(result, addr.getProperty("STAE"));
        appendToPlace(result, addr.getProperty("CTRY"));
      } else if (prop.getProperty("PLAC") != null) {
        String place = prop.getProperty("PLAC").getValue();
        String[] parts = place.split(",\\s*");
        if (parts.length == 0) return ""; 

        String lastPart = parts[parts.length-1];
        
        if (parts.length >= 4 &&
            (lastPart.equalsIgnoreCase("us") || lastPart.equalsIgnoreCase("usa"))) {
          
          String county = parts[parts.length-3];
          if (!county.endsWith(" County") && !county.endsWith("Co") && !county.endsWith(" Co.")) {
            parts[parts.length-3] += " Co.";
          }
        }

        
        int nParts = parts.length;
        if (lastPart.equals("US") ||
            lastPart.equals("GB")) {
          nParts--;
        } else {
          
          parts[parts.length-1] = new Locale("en", lastPart).getDisplayCountry();
        }
        

        boolean firstWritten = true;
        for (int i = 0; i < nParts; i++) {
          String part = parts[i];
          if (part.length() == 0) continue;
          if (firstWritten) {
            firstWritten = false;
          } else {
            result.append(", ");
          }
          result.append(part);
        }
        








      }
      if (result.length() == 0) return "";

      doc.addIndexTerm(placeIndexTitle, result.toString()); 

      if (preposition == null) {
        String key = "prep.in_city";
        if (Character.isDigit(result.charAt(0))) key = "prep.at_street_address"; 
        preposition = translate(key);
      }
      return " " + preposition + " " + result;
    }

    
    private void writePlace(Property prop, String preposition) {
      String result = getPlaceString(prop, preposition);
      if (result.length() == 0) return;
      doc.addText(result);
      doc.addIndexTerm(placeIndexTitle, result, null);
    }

    
    private void appendToPlace(StringBuffer result, Property prop) {
      if (prop != null) {
        if (result.length() > 0) result.append(", ");
        result.append(prop.getValue());
      }
    }

    private String getPersonalPronoun(boolean asSubject) {
      String pronoun;
      if (asSubject) {
        if (indi.getSex() == PropertySex.MALE) pronoun = translate("pronoun.nom.male");
        else pronoun = translate("pronoun.nom.female");
        pronoun = Character.toUpperCase(pronoun.charAt(0)) + pronoun.substring(1); 
      } else {
        
        if (indi.getSex() == PropertySex.MALE) pronoun = translate("pronoun.acc.male");
        else pronoun = translate("pronoun.acc.female");
      }
      return pronoun;
    }

  }

}

