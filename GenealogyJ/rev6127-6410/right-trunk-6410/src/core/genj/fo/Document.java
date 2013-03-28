
package genj.fo;

import genj.gedcom.Entity;
import genj.util.ImageSniffer;
import genj.util.Resources;

import java.awt.geom.Dimension2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class Document {
  
  public final static int FONT_XX_SMALL=0;
  
  public final static int FONT_X_SMALL=1;
  
  public final static int FONT_SMALL=2;
  
  public final static int FONT_MEDIUM=3;
  
  public final static int FONT_LARGE=4;
  
  public final static int FONT_X_LARGE=5;
  
  public final static int FONT_XX_LARGE=6;

  private final static Resources RESOURCES = Resources.get(Document.class);
  
  
  protected final static Pattern REGEX_ATTR = Pattern.compile("([^,]+)=([^,\\(]*(\\(.*?\\))?)");
  
  
  private final static String 
    NS_XSLFO = "http://www.w3.org/1999/XSL/Format",
    NS_GENJ = "http://genj.sourceforge.net/XSL/Format";
  
  private org.w3c.dom.Document doc;
  private Element cursor;
  private String title;
  private boolean needsTOC = false;
  private Map file2elements = new HashMap();
  private List toc = new ArrayList();
  private String formatSection = "font-weight=bold,space-before=0.5cm,space-after=0.2cm,keep-with-next.within-page=always";
  private String formatSectionLarger = "font-size=larger," + formatSection;
  private static final String[] fontSizes = new String[] {
      "xx-small", "x-small", "small", "medium", "large", "x-large", "xx-large"
  };
  private int minSectionFontSize;
  private int maxSectionFontSize;
  private Map index2primary2secondary2elements = new TreeMap();
  private int idSequence = 0;
  private boolean containsCSV = false;
  
  
  public Document(String title) {
    
    
    this.title = title;

    
    setSectionSizes(FONT_MEDIUM, FONT_XX_LARGE);
    
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      doc = dbf.newDocumentBuilder().newDocument();
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    cursor = (Element)doc.appendChild(doc.createElementNS(NS_XSLFO, "root")); 
    cursor.setAttribute("xmlns", NS_XSLFO);
    cursor.setAttribute("xmlns:genj", NS_GENJ);

    
    
    cursor.setAttributeNS(NS_GENJ, "genj:title", title);
    
    push("layout-master-set");
    
    push("simple-page-master", "master-name=master,margin-top=1cm,margin-bottom=1cm,margin-left=1cm,margin-right=1cm");
    push("region-body", "margin-bottom=1cm").pop();
    push("region-after", "extent=0.8cm").pop();
    pop().pop().push("page-sequence","master-reference=master");

    
    push("static-content", "flow-name=xsl-region-after");
    push("block", "text-align=center");
    
    push("page-number").pop();
    pop(); 
    pop(); 

    
    

    push("flow", "flow-name=xsl-region-body");
    push("block");

    
  }

  
  public void setSectionSizes(int minSize, int maxSize) {
    if (minSize < 0 || minSize > maxSize || maxSize > fontSizes.length-1) throw new IllegalArgumentException("setSectionSizes("+minSize+","+maxSize+")");
    minSectionFontSize = minSize;
    maxSectionFontSize = maxSize;
  }

  
  protected boolean containsCSV() {
    return containsCSV;
  }
  
  
  public String toString() {
    return getTitle();
  }
  
  
  protected void close() {
    
    
    if (cursor==null)
      return;
    
    
    indexes();
    
    
    if (needsTOC) 
      toc();
    
    
    cursor = null;
  }
  
  
  public String getTitle() {
    return title;
  }
  
  
   DOMSource getDOMSource() {
    return new DOMSource(doc);
  }
  
  
  public Document addTOC() {
    needsTOC = true;
    
    return this;
  }
  
  
  public Document addTOCEntry(String title) {
    addTOC();
    
    String id = "toc"+toc.size();
    addAnchor(id);
    
    toc.add(new TOCEntry(id, title));
    
    return this;
  }

  
  private String getFontSize(int sectionDepth) {
    int i = maxSectionFontSize + 1 - sectionDepth;
    if (i < minSectionFontSize) i=minSectionFontSize;
    return fontSizes[i];
  }

  
  public Document startSection(String title, String id, int sectionDepth) {
    
    
    if (id!=null&&id.startsWith("_"))
      throw new IllegalArgumentException("underscore is reserved for internal IDs");
    
    
    pop("flow", "addSection() is not applicable outside document flow");
    cursor = (Element)cursor.getLastChild();
    
    
    if (id==null||id.length()==0)
      id = "toc"+toc.size();
      
    
    String fontSize = getFontSize(sectionDepth);
    pop().push("block", "font-size="+fontSize + "," + formatSection + ",id="+id);
    
    
    toc.add(new TOCEntry(id, title));
    
    
    addText(title);
    
    
    nextParagraph();
    
    
    return this;
  }

  
  public Document startSection(String title, String id) {
    return startSection(title, id, 1);
  }

  
  public Document startSection(String title, Entity entity, int sectionDepth) {
    return startSection(title,entity.getTag()+"_"+entity.getId(), sectionDepth);
  }

  
  public Document startSection(String title, Entity entity) {
    return startSection(title,entity.getTag()+"_"+entity.getId());
  }

  
  public Document startSection(String title, int sectionDepth) {
    return startSection(title, "", sectionDepth);
  }

  
  public Document startSection(String title) {
    return startSection(title, "");
  }
    
  
  public Document addIndexTerm(String index, String primary) {
    return addIndexTerm(index, primary, "");
  }
  
  
  public Document addIndexTerm(String index, String primary, String secondary) {
    
    
    if (index==null)
      throw new IllegalArgumentException("addIndexTerm() requires name of index");
    index = index.trim();
    if (index.length()==0)
      throw new IllegalArgumentException("addIndexTerm() name of index can't be empty");
    
    
    primary = trimIndexTerm(primary);
    if (primary.length()==0)
      return this;
    
    
    secondary = trimIndexTerm(secondary);
    
    
    Map primary2secondary2elements = (Map)index2primary2secondary2elements.get(index);
    if (primary2secondary2elements==null) {
      primary2secondary2elements = new TreeMap();
      index2primary2secondary2elements.put(index, primary2secondary2elements);
    }
    Map secondary2elements = (Map)primary2secondary2elements.get(primary);
    if (secondary2elements==null) {
      secondary2elements = new TreeMap();
      primary2secondary2elements.put(primary, secondary2elements);
    }
    List elements = (List)secondary2elements.get(secondary);
    if (elements==null) {
      elements = new ArrayList();
      secondary2elements.put(secondary, elements);
    }
    
    
    
    
    
    String id = cursor.getAttribute("id");
    if (id.length()==0) {
      id = ""+(++idSequence);
      cursor.setAttribute("id", id);
    }
    
    
    if (!elements.contains(cursor))
        elements.add(cursor);
    
    return this;
  }
  
  private String trimIndexTerm(String term) {
    
    if (term==null) 
      return "";
    
    int bracket = term.indexOf('(');
    if (bracket>=0) 
      term = term.substring(0,bracket);
    
    int comma = term.indexOf('(');
    if (comma>=0) 
      term = term.substring(0,comma);
    
    return term.trim();
  }
    
  
  public Document addText(String text) {
    return addText(text, "");
  }
  
  
  public Document addText(String text, String atts) {
    text(text, atts);
    return this;
  }
    
  
  public Document addImage(File file, String atts) {
    
    
    if (file==null||!file.exists())
      return this;

    
    Dimension2D dim = new ImageSniffer(file).getDimensionInInches();
    if (dim==null)
      return this;
    if (dim.getWidth()>dim.getHeight()) {
      if (dim.getWidth()>1) atts = "width=1in,content-width=scale-to-fit,"+atts; 
    } else {
      if (dim.getHeight()>1) atts = "height=1in,content-height=scale-to-fit,"+atts; 
    }
    
    
    push("external-graphic", "src="+file.getAbsolutePath()+","+atts);
    
    
    List elements = (List)file2elements.get(file);
    if (elements==null) {
      elements = new ArrayList(3);
      file2elements.put(file, elements);
    }
    elements.add(cursor);
    
    
    pop();

    
    addText(" ");
    
    
    return this;
  }
  
  
  
  protected File[] getImages() {
    Set files = file2elements.keySet();
    return (File[])files.toArray(new File[files.size()]);
  }
  
  
  protected void setImage(File file, String value) {
    List nodes = (List)file2elements.get(file);
    for (int i = 0; i < nodes.size(); i++) {
      Element external = (Element)nodes.get(i);
      external.setAttribute("src", value);
    }
  }
  
  
  public Document nextParagraph() {
    return nextParagraph("");
  } 
  
  
  public Document nextParagraph(String format) {
    
    
    if (cursor.getFirstChild()!=null)
      pop().push("block", format);
    else
      attributes(cursor, format);
    
    return this;
  }
    
  
  public Document startList() {
    return startList("");
  }
    
  
  public Document startList(String format) {
    
    
    pop();
    push("list-block", "provisional-distance-between-starts=0.6em, provisional-label-separation=0pt,"+format);
    nextListItem();
    
    return this;
  }
    
  
  public Document nextListItem() {
    return nextListItem("");
  }
    
  
  public Document nextListItem(String format) {
    
    
    
    
    
    
    
    
    
    
    Element list = peek("list-block", "nextListItem() is not applicable outside list block");
    
    
    if (list.getChildNodes().getLength()==1&&cursor.getFirstChild()==null&&cursor.getPreviousSibling()==null&&cursor.getParentNode().getLocalName().equals("list-item-body")) {
      
      list.removeChild(list.getFirstChild());
    } 
    
    
    cursor = list;
    
    
    String label = attribute("genj:label", format);
    if (label!=null) {
      
      String dist = list.getAttribute("provisional-distance-between-starts");
      if (dist.endsWith("em")) {
        float len = label.length()*0.6F;
        if (Float.parseFloat(dist.substring(0, dist.length()-2))<len)
          list.setAttribute("provisional-distance-between-starts", len+"em");
      }
    } else {
      label = "\u";  
    }

    
    push("list-item");
     push("list-item-label", "end-indent=label-end()");
      push("block");
       text(label, ""); 
      pop();
     pop();
    push("list-item-body", "start-indent=body-start()");
     push("block");

    return this;
  }
    
  
  public Document endList() {

    
    
    pop("list-block", "endList() is not applicable outside list-block").pop();
    push("block","");
    
    return this;
  }
  
  
  public Document startTable() {
    return startTable("width=100%,border=0.5pt solid black");
  }
  
  public Document startTable(String format) {

    
    format  = "table-layout=fixed,"+format;
    
    
    
    
    
    
    
    
    
    
    
    
    
    push("table", format);
    Element table = cursor;
    
    
    if ("true".equals(attribute("genj:csv", format))) {
      containsCSV = true;
      cursor.setAttributeNS(NS_GENJ, "genj:csv", "true");
      
      String prefix = attribute("genj:csvprefix", format);
      if (prefix!=null)
        cursor.setAttributeNS(NS_GENJ, "genj:csvprefix", prefix);
    }
    
    
    if (format.indexOf("genj:header=true")>=0) {
      push("table-header"); 
      push("table-row", "color=#ffffff,background-color=#c0c0c0,font-weight=bold");
    } else { 
      push("table-body");
      push("table-row");
    }
    
    
    push("table-cell", "border="+table.getAttribute("border"));  
    push("block");
    
    return this;
  }
  
  
  public Document addTableColumn(String atts) {
    
    
    
    Element save = cursor;
    
    
    pop("table", "addTableColumn() is not applicable outside enclosing table");
    
    
    Node before = cursor.getFirstChild();
    while (before!=null && before.getNodeName().equals("table-column"))
      before = before.getNextSibling();
    
    push("table-column", atts, before);

    
    cursor = save;
    
    return this;
  }
  
  
  public Document nextTableCell() {
    return nextTableCell("");
  }
  public Document nextTableCell(String atts) {
    
    
    
    
    
    Element cell = peek("table-cell", "nextTableCell() is not applicable outside enclosing table");
    if (cell.getPreviousSibling()==null&&cursor==cell.getFirstChild()&&!cursor.hasChildNodes()) {
      attributes(cell, atts);
      
      push("inline", "").pop(); 
      return this;
    }
    
    
    Element row = peek("table-row", "nextTableCell() is not applicable outside enclosing table row");
    int cells = row.getElementsByTagName("table-cell").getLength();
    
    
    Element table = peek("table", "nextTableCell() is not applicable outside enclosing table");
    int cols = table.getElementsByTagName("table-column").getLength();
    if (cols>0&&cells==cols) 
      return nextTableRow();

    
    pop("table-row", "nextTableCell() is not applicable outside enclosing table row");
    
    
    
    
    
    
    
    
    
    
    push("table-cell", "border="+table.getAttribute("border")+","+atts);  
    push("block");

    
    return this;
  }
  
  
  public Document nextTableRow() {
    return nextTableRow("");
  }
  public Document nextTableRow(String atts) {
    
    
    
    
    
    Element cell = peek("table-cell", "nextTableRow() is not applicable outside enclosing table");
    if (cell.getPreviousSibling()==null&&cursor==cell.getFirstChild()&&!cursor.hasChildNodes()) {
      attributes((Element)cell.getParentNode(), atts);
      return this;
    }
    
    
    pop("table", "nextTableRow() is not applicable outside enclosing table");
    Element table = cursor;
    
    
    if ( table.getLastChild().getNodeName().equals("table-body") ) {
      cursor = (Element)table.getLastChild();
    } else {
      push("table-body");
    }
    
    
    push("table-row", atts);
    
    
    push("table-cell", "border="+table.getAttribute("border"));  
    push("block");
    
    
    return this;
  }
  
  
  public Document endTable() {
    
    
    pop("table", "endTable() is not applicable outside enclosing table").pop();
    
    
    return this;
  }
  
  
  public Document nextPage() {
    pop();
    push("block", "page-break-before=always");
    return this;
  }
  
  
  public Document addAnchor(String id) {
    if (id.startsWith("_"))
      throw new IllegalArgumentException("underscore is reserved for internal IDs");
    
    
    
    
    if (cursor.getAttribute("id").length()==0)
      cursor.setAttribute("id", id);
    else
      push("block", "id="+id).pop();
    return this;
  }
    
  
  public Document addAnchor(Entity entity) {
    return addAnchor(entity.getTag()+"_"+entity.getId());
  }

  
  public Document addExternalLink(String text, String id) {

    
    push("basic-link", "external-destination="+id);
    text(text, "");
    pop();
    
    return this;
  }

  
  public Document addLink(String text, String id) {
    
    
    push("basic-link", "internal-destination="+id);
    text(text, "");
    pop();
    
    return this;
  }
  
  
  public Document addLink(String text, Entity entity) {
    addLink(text, entity.getTag()+"_"+entity.getId());
    return this;
  }
  
  
  public Document addLink(Entity entity) {
    return addLink(entity.toString(), entity);
  }
  
  
  private Document indexes() {
    
    
    for (Iterator indexes = index2primary2secondary2elements.keySet().iterator(); indexes.hasNext(); ) {
      
      String index = (String)indexes.next();
      Map primary2secondary2elements = (Map)index2primary2secondary2elements.get(index);
      
      
      nextPage();
      startSection(index);
      push("block", "start-indent=1cm");
      
      
      for (Iterator primaries = primary2secondary2elements.keySet().iterator(); primaries.hasNext(); ) {
        
        String primary = (String)primaries.next();
        Map secondary2elements = (Map)primary2secondary2elements.get(primary);
        
        
        push("block", "");
        text(primary+" ", "");

        
        for (Iterator secondaries = secondary2elements.keySet().iterator(); secondaries.hasNext(); ) {
          
          String secondary = (String)secondaries.next();
          List elements = (List)secondary2elements.get(secondary);
          
          if (secondary.length()>0) {
            push("block", "start-indent=2cm"); 
            text(secondary+" ", "");
          }
          
          
          for (int e=0;e<elements.size();e++) {
            if (e>0) text(", ", "");
            Element element = (Element)elements.get(e);
            String id = element.getAttribute("id");
            
            push("basic-link", "internal-destination="+id);
            push("page-number-citation", "ref-id="+id);
            cursor.setAttributeNS(NS_GENJ, "genj:citation", Integer.toString(e+1));
            pop();
            pop();
          }
          
          if (secondary.length()>0)
            pop();
          
        }
        
        
        pop();
      }

      
      pop();
    }

    
    
    return this;
  }
  
  
  private Document toc() {
    
    
    if (toc.isEmpty())
      return this;
    Element old = cursor;
    
    
    pop("flow", "can't create TOC without enclosing flow");
    
    
    push("block", "", cursor.getFirstChild());
    
    
    
    
    
    
    
    
    
    
    push("block", formatSectionLarger);
    text(RESOURCES.getString("toc"), "");
    pop();

    
    for (Iterator it = toc.iterator(); it.hasNext(); ) {
      push("block", "start-indent=1cm,end-indent=1cm,text-indent=0cm,text-align-last=justify,text-align=justify");
      TOCEntry entry = (TOCEntry)it.next();
      addLink(entry.text, entry.id);
      push("leader", "leader-pattern=dots").pop();
      push("page-number-citation", "ref-id="+entry.id).pop();

      pop();
    }
    
    
    cursor = old;
    return this;
  }
  
  
  private Document push(String name) {
    return push(name, "");
  }
  
  
  private Document push(String name, String attributes) {
    return push(name, attributes, null);
  }
  
  
  private Document push(String name, String attributes, Node before) {
    
    Element elem = doc.createElementNS(NS_XSLFO, name);
    if (before!=null)
      cursor.insertBefore(elem, before);
    else
      cursor.appendChild(elem);
    cursor =  elem;
    
    return attributes(elem, attributes);
  }
  
  
  private Document attributes(Element elem, String format) {
    
    Matcher m = REGEX_ATTR.matcher(format);
    while (m.find()) {
      
      String key = m.group(1).trim();
      if (key.indexOf(':')<0) {
        String val = m.group(2).trim();
        elem.setAttribute(key, val);
      }
    }
    
    return this;
  }
  
  
  private String attribute(String key, String format) {
    
    Matcher m = REGEX_ATTR.matcher(format);
    while (m.find()) {
      if (m.group(1).trim().equals(key) )
          return m.group(2).trim();
    }
    
    return null;
  }
  
  
  private Document text(String text, String atts) {
    
    
    if (text.length()==0)
      return this;

    
    Node txt = doc.createTextNode(text);
    if (atts.length()>0) {
      push("inline", atts);
      cursor.appendChild(txt);
      pop();
    } else {
      cursor.appendChild(txt);
    }
    return this;
  }

  
  private Document pop() {
    cursor = (Element)cursor.getParentNode();
    return this;
  }
  
  
  private Document pop(String qname, String error) {
    cursor = peek(qname, error);
    return this;
  }
  
  
  private Element peek(String qname, String error) {
    Node loop = cursor;
    while (loop instanceof Element) {
      if (loop.getLocalName().equals(qname)) 
        return (Element)loop;
      loop = loop.getParentNode();
    }
    throw new IllegalArgumentException(error);
  }

  
  private class TOCEntry {
    String id;
    String text;
    private TOCEntry(String id, String text) {
      this.id = id;
      this.text = text;
    }
  }
  
  
  public static void main(String[] args) {
    
    try {
    
      Document doc = new Document("Testing FO");
      
      doc.addText("A paragraph");
      doc.nextParagraph("start-indent=10pt");
      doc.addText("The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. ");

      doc.nextParagraph("text-decoration=underline");
      doc.addText("this paragraph is underlined");
      
      doc.nextParagraph();
      doc.addText("this line contains ");
      doc.addText("underlined", "text-decoration=underline");
      doc.addText(" text");
      
      doc.startList();
      doc.nextListItem("genj:label=a)");
      doc.addText("A foo'd bullet");
      doc.nextListItem("genj:label=b)");
      doc.addText("A foo'd bullet");
      doc.nextListItem();
      doc.addText("A normal bullet");

      doc.addTOC();
      doc.startSection("Section 1");
      doc.addText("here comes a ").addText("table", "font-weight=bold, color=rgb(255,0,0)").addText(" for you:");
      doc.addImage(new File("C:/Documents and Settings/Nils/My Documents/Java/Workspace/GenJ/gedcom/meiern.jpg"), "vertical-align=middle");
      doc.addImage(new File("C:/Documents and Settings/Nils/My Documents/My Pictures/usamap.gif"), "vertical-align=middle");
      














































      doc.startSection("Section 2");
      doc.addText("Text and a page break");

      
      doc.addTOCEntry("Foo");
      
      doc.startSection("Section 3");
      doc.addText("Text");

      Format format;
      if (args.length>0)
        format = Format.getFormat(args[0]);
      else 
        format = new PDFFormat();

      File file = null;
      String ext = format.getFileExtension();
      if (ext!=null) {
        file = new File("c:/temp/foo."+ext);
      }
      format.format(doc, file);

      if (file!=null)
        Runtime.getRuntime().exec("c:/Program Files/Internet Explorer/iexplore.exe \""+file.getAbsolutePath()+"\"");

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
}
