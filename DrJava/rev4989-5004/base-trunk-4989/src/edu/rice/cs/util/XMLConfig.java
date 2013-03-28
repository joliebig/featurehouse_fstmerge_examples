

package edu.rice.cs.util;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;


public class XMLConfig {
  
  public static final String NL = System.getProperty("line.separator");
  
  
  private Document _document;
  
  
  private XMLConfig _parent = null;
  
  
  private Node _startNode = null;
  
  
  public XMLConfig() {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      _document = builder.newDocument();  
      
    }
    catch(ParserConfigurationException e) {
      e.printStackTrace();
    }
  }
  
  
  public XMLConfig(InputStream is) {
    init(new InputSource(is));
  }
  
  
  public XMLConfig(Reader r) {
    init(new InputSource(r));
  }
  
  
  public XMLConfig(XMLConfig parent, Node node) {
    if ((parent==null) || (node==null)) { throw new XMLConfigException("Error in ctor: parent or node is null"); }
    _parent = parent;
    _startNode = node;
    _document = null;
  }
  
  
  private void init(InputSource is) {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = null;
    try {
      builder = factory.newDocumentBuilder();
      _document = builder.parse(is);
      
    }
    catch(Exception e) {
      throw new XMLConfigException("Error in ctor", e);
    }
    _document.normalize();
  }
  
  
  public XMLConfig(File f) {
    try {
      init(new InputSource(new FileInputStream(f)));
    }
    catch(FileNotFoundException e) {
      throw new XMLConfigException("Error in ctor", e);
    }
  }
  
  
  public XMLConfig(String filename)  {
    try {
      init(new InputSource(new FileInputStream(filename)));
    }
    catch(FileNotFoundException e) {
      throw new XMLConfigException("Error in ctor", e);
    }
  }
  
  public boolean isDelegated() { return (_parent!=null); }
  
  
  public void save(OutputStream os) {
    if (isDelegated()) { _parent.save(os); return; }
    
    
    Source source = new DOMSource(_document);
    
    
    try {
      TransformerFactory tf = TransformerFactory.newInstance();
      tf.setAttribute("indent-number", Integer.valueOf(2));
      Transformer t = tf.newTransformer();
      t.setOutputProperty(OutputKeys.INDENT, "yes");
      t.transform(source, new StreamResult(new OutputStreamWriter(os, "utf-8")));
      
    }
    catch(TransformerException e) {
      throw new XMLConfigException("Error in save", e);
    }
    catch(UnsupportedEncodingException e) {
      throw new XMLConfigException("Error in save", e);
    }
  }
  
  
  public void save(File f) {
    if (isDelegated()) { _parent.save(f); return; }
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(f);
      save(fos);
    }
    catch(FileNotFoundException e) {
      throw new XMLConfigException("Error in save", e);
    }
    finally {
      try {
        if (fos!=null) fos.close();
      }
      catch(IOException ioe) {  }
    }
  }
  
  
  public void save(String filename) {
    save(new File(filename));
  }
  
  
  
  
  public String get(String path) {
    List<String> r = getMultiple(path);
    if (r.size()!=1) throw new XMLConfigException("Number of results != 1");
    return r.get(0);
  }

  
  public String get(String path, Node root) {
    List<String> r = getMultiple(path, root);
    if (r.size()!=1) throw new XMLConfigException("Number of results != 1");
    return r.get(0);
  }
  
    
  public String get(String path, String defaultVal) {
    try {
      return get(path);
    }
    catch(XMLConfigException e) {
      return defaultVal;
    }
  }
  
  
  public String get(String path, Node root, String defaultVal) {
    try {
      return get(path, root);
    }
    catch(XMLConfigException e) {
      return defaultVal;
    }
  }
  
  
  
  
  public int getInt(String path) {
    List<String> r = getMultiple(path);
    if (r.size()!=1) throw new XMLConfigException("Number of results != 1");
    try {
      return Integer.valueOf(r.get(0));
    }
    catch(NumberFormatException nfe) { throw new IllegalArgumentException("Not an integer value.", nfe); }
  }

  
  public int getInt(String path, Node root) {
    List<String> r = getMultiple(path, root);
    if (r.size()!=1) throw new XMLConfigException("Number of results != 1");
    try {
      return Integer.valueOf(r.get(0));
    }
    catch(NumberFormatException nfe) { throw new IllegalArgumentException("Not an integer value.", nfe); }
  }
  
  
  public int getInt(String path, int defaultVal) {
    try {
      return getInt(path);
    }
    catch(XMLConfigException e) {
      return defaultVal;
    }
  }
  
  
  public int getInt(String path, Node root, int defaultVal) {
    try {
      return getInt(path, root);
    }
    catch(XMLConfigException e) {
      return defaultVal;
    }
  }

  
  
  
  public boolean getBool(String path) {
    List<String> r = getMultiple(path);
    if (r.size()!=1) throw new XMLConfigException("Number of results != 1");
    String s = r.get(0).toLowerCase().trim();
    if ((s.equals("true")) ||
        (s.equals("yes")) ||
        (s.equals("on"))) return true;
    if ((s.equals("false")) ||
        (s.equals("no")) ||
        (s.equals("off"))) return false;
    throw new IllegalArgumentException("Not a Boolean vlaue.");
  }

  
  public boolean getBool(String path, Node root) {
    List<String> r = getMultiple(path, root);
    if (r.size()!=1) throw new XMLConfigException("Number of results != 1");
    String s = r.get(0).toLowerCase().trim();
    if ((s.equals("true")) ||
        (s.equals("yes")) ||
        (s.equals("on"))) return true;
    if ((s.equals("false")) ||
        (s.equals("no")) ||
        (s.equals("off"))) return false;
    throw new IllegalArgumentException("Not a Boolean vlaue.");

  }
  
  
  public boolean getBool(String path, boolean defaultVal) {
    try {
      return getBool(path);
    }
    catch(XMLConfigException e) {
      return defaultVal;
    }
  }
  
  
  public boolean getBool(String path, Node root, boolean defaultVal) {
    try {
      return getBool(path, root);
    }
    catch(XMLConfigException e) {
      return defaultVal;
    }
  }
  
  
  
  
  public List<String> getMultiple(String path) {
    if (isDelegated()) { return getMultiple(path, _startNode); }

    return getMultiple(path, _document);
  }
  
  
  public List<String> getMultiple(String path, Node root) {
    List<Node> accum = getNodes(path, root);
    List<String> strings = new LinkedList<String>();
    for(Node n: accum) {
      if (n instanceof Attr) {
        strings.add(n.getNodeValue());
      }
      else {
        Node child;
        String acc = "";
        child = n.getFirstChild();
        while(child!=null) {
          if (child.getNodeName().equals("#text")) {
            acc += " " + child.getNodeValue();
          }
          else if (child.getNodeName().equals("#comment")) {
            
          }
          else {
            throw new XMLConfigException("Node "+n.getNodeName()+" contained node "+child.getNodeName()+", but should only contain #text and #comment.");
          }
          child = child.getNextSibling();
        }
        strings.add(acc.trim());
      }
    }
    return strings;
  }
  
  
  public List<Node> getNodes(String path) {
    if (isDelegated()) { return getNodes(path, _startNode); }

    return getNodes(path, _document);
  }
  
  
  public List<Node> getNodes(String path, Node root) {
    List<Node> accum = new LinkedList<Node>();
    getMultipleHelper(path, root, accum, false);
    return accum;
  }
  
  
  private void getMultipleHelper(String path, Node n, List<Node> accum, boolean dotRead) {
    int dotPos = path.indexOf('.');
    boolean initialDot = (dotPos==0);
    if ((path.length()>0) && (dotPos == -1) && (!path.endsWith("/"))) {
      path = path + "/";
    }
    int slashPos = path.indexOf('/');
    
    if(dotPos != -1 && path.indexOf('.', dotPos+1) != -1)
      throw new XMLConfigException("An attribute cannot have subparts (foo.bar.fum and foo.bar/fum not allowed)");
    
    if(dotPos != -1 && path.indexOf('/', dotPos+1) != -1)
      throw new XMLConfigException("An attribute cannot have subparts (foo.bar.fum and foo.bar/fum not allowed)");
    
    if (((slashPos > -1) || (dotPos > -1)) && !dotRead || initialDot)  {
      String nodeName;
      if ((slashPos > -1) && ((dotPos == -1) || (slashPos < dotPos))) {
        nodeName = path.substring(0, slashPos);
        path = path.substring(slashPos+1);
      }
      else {
        if (slashPos > -1) {
          throw new XMLConfigException("An attribute cannot have subparts (foo.bar.fum and foo.bar/fum not allowed)");
        }
        if (!initialDot) {
          nodeName = path.substring(0, dotPos);
          path = path.substring(dotPos+1);
          dotRead = true;
        }
        else {
          path = path.substring(1);
          getMultipleAddAttributesHelper(path, n, accum);
          return;
        }
      }
      Node child = n.getFirstChild();
      if (nodeName.equals("*")) {
        while(child!=null) {
          if (!child.getNodeName().equals("#text") && !child.getNodeName().equals("#comment")) {
            if (dotRead) {
              getMultipleAddAttributesHelper(path, child, accum);
            }
            else {
              getMultipleHelper(path, child, accum, false);
            }
          }
          child = child.getNextSibling();
        }
        return;
      }
      else {
        while(child!=null) {
          if (child.getNodeName().equals(nodeName)) {
            
            if (dotRead) {
              getMultipleAddAttributesHelper(path, child, accum);
            }
            else {
              getMultipleHelper(path, child, accum, false);
            }
          }
          child = child.getNextSibling();
        }
        return;
      }
    }
    else {
      accum.add(n);
    }
  }
  
  private void getMultipleAddAttributesHelper(String path, Node n, List<Node> accum) {
    if ((path.indexOf('.') > -1) || (path.indexOf('/') > -1)) {
      throw new XMLConfigException("An attribute cannot have subparts (foo.bar.fum and foo.bar/fum not allowed)");
    }
    NamedNodeMap attrMap = n.getAttributes();
    if (path.equals("*")) {
      for(int i=0; i<attrMap.getLength(); ++i) {
        Node attr = attrMap.item(i);
        accum.add(attr);
      }
    }
    else {
      Node attr = attrMap.getNamedItem(path);
      if (attr!=null) {
        accum.add(attr);
      }
    }
  }
  
  
  public Node set(String path, String value) {
    if (isDelegated()) { return set(path, value, _startNode, true); }

    return set(path, value, _document, true);
  }
  
  
  public Node set(String path, String value, boolean overwrite) {
    if (isDelegated()) { return set(path, value, _startNode, overwrite); }

    return set(path, value, _document, overwrite);
  }
  
  
  
  public Node set(String path, String value, Node n, boolean overwrite) {
    if (isDelegated()) { return _parent.set(path, value, n, overwrite); }
    
    int dotPos = path.lastIndexOf('.');
    Node node;
    if (dotPos==0) {
      node = n;
    }
    else {
      node = createNode(path, n, overwrite);
    }
    if (dotPos>=0) {
      Element e = (Element)node;
      e.setAttribute(path.substring(dotPos+1),value);
    }
    else {
      node.appendChild(_document.createTextNode(value));
    }
    return node;
  }
  
  
  public Node createNode(String path) {
    if (isDelegated()) { return createNode(path, _startNode, true); }
    
    return createNode(path, _document, true);
  }
  
  
  public Node createNode(String path, Node n) {
    return createNode(path, n, true);
  }
  
  
  public Node createNode(String path, Node n, boolean overwrite) {
    if (isDelegated()) { return _parent.createNode(path, n, overwrite); }

    if (n==null) { n = _document; }
    while(path.indexOf('/') > -1) {
      Node child = null;
      String nodeName = path.substring(0, path.indexOf('/'));
      path = path.substring(path.indexOf('/')+1);
      child = n.getFirstChild();
      while(child!=null) {
        if (child.getNodeName().equals(nodeName)) {
          
          n = child;
          break;
        }
        child = child.getNextSibling();
      }
      if (child==null) {
        
        child = _document.createElement(nodeName);
        n.appendChild(child);
        n = child;
      }
    }
    
    String nodeName;
    if (path.indexOf('.') > -1) {
      nodeName = path.substring(0, path.indexOf('.'));
    }
    else {
      if (path.length()==0) {
        throw new XMLConfigException("Cannot set node with empty name");
      }
      nodeName = path;
    }
    Node child = null;
    if (nodeName.length()>0) {
      if (overwrite) {
        child = n.getFirstChild();
        while(child!=null) {
          if (child.getNodeName().equals(nodeName)) {
            
            n = child;
            break;
          }
          child = child.getNextSibling();
        }
        if (child==null) {
          child = _document.createElement(nodeName);
          n.appendChild(child);
          n = child;
        }
      }
      else {
        child = _document.createElement(nodeName);
        n.appendChild(child);
        n = child;
      }
    }
    
    if (path.indexOf('.') > -1) {
      if (!(n instanceof Element)) {
        throw new XMLConfigException("Node "+n.getNodeName()+" should be an element so it can contain attributes");
      }
      return n;
    }
    else {
      if (overwrite) {
        child = n.getFirstChild();
        
        while(child!=null) {
          Node temp = child.getNextSibling();
          n.removeChild(child);
          child = temp;
        }
        return n;
      }
      else {
        return child;
      }
    }
  }
  
  
  
  public String toString() {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    save(os);
    return os.toString();
  }
  
  
  public static String getNodePath(Node n) {
    if (n==null) { return ""; }
    String path = "";
    while(n.getParentNode()!=null) {
      path = n.getNodeName()+"/"+path;
      n = n.getParentNode();
    }
    
    return path.substring(0,path.length()-1);
  }
  
  
  public static class XMLConfigException extends RuntimeException {
    public XMLConfigException() {
      super();
    }
    
    public XMLConfigException(String message) {
      super(message);
    }
    
    public XMLConfigException(String message, Throwable cause) {
      super(message, cause);
    }
    
    public XMLConfigException(Throwable cause) {
      super(cause);
    }
  }
}
