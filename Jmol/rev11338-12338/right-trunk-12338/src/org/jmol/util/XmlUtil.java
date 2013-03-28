

package org.jmol.util;

import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.jmol.script.Token;

public class XmlUtil {

  

  public static void openDocument(StringBuffer data) {
    data.append("<?xml version=\"1.0\"?>\n");
  }

  public static void openTag(StringBuffer sb, String name) {
    sb.append("<").append(name).append(">\n");
  }

  public static void openTag(StringBuffer sb, String name, Object[] attributes) {
    appendTag(sb, name, attributes, null, false, false);
    sb.append("\n");
  }

  public static void closeTag(StringBuffer sb, String name) {
    sb.append("</").append(name).append(">\n");
  }

  public static void appendTag(StringBuffer sb, String name,
                               Object[] attributes, Object data,
                               boolean isCdata, boolean doClose) {
    String closer = ">";
    if (name.endsWith("/")){
      name = name.substring(0, name.length() - 1);
      if (data == null) {
        closer = "/>\n";
        doClose = false;
      }
    }
    sb.append("<").append(name);
    if (attributes != null)
      for (int i = 0; i < attributes.length; i++) {
        Object o = attributes[i];
        if (o instanceof Object[])
          appendAttrib(sb, ((Object[]) o)[0], ((Object[]) o)[1]);
        else
          appendAttrib(sb, o, attributes[++i]);
      }
    sb.append(closer);
    if (data != null) {
      if (isCdata)
        data = wrapCdata(data);
      sb.append(data);
    }
    if (doClose)
      closeTag(sb, name);
  }

  
  public static String wrapCdata(Object data) {
    String s = "" + data;
    return (s.indexOf("&") < 0 && s.indexOf("<") < 0 ? (s.startsWith("\n") ? "" : "\n") + s 
        : "<![CDATA[" + TextFormat.simpleReplace(s, "]]>", "]]]]><![CDATA[>") + "]]>");
  }
  
  
  public static String unwrapCdata(String s) {
    return (s.startsWith("<![CDATA[") && s.endsWith("]]>") ?
        s.substring(9, s.length()-3).replace("]]]]><![CDATA[>", "]]>") : s);
  }
  
  
  public static void appendTag(StringBuffer sb, String name,
                               Object[] attributes, Object data) {
    appendTag(sb, name, attributes, data, false, true);
  }

  
  public static void appendTag(StringBuffer sb, String name, Object data) {
    if (data instanceof Object[])
      appendTag(sb, name, (Object[]) data, null, false, true);
    else
      appendTag(sb, name, null, data, false, true);
  }

  
  public static void appendCdata(StringBuffer sb, String name, 
                                 Object[] attributes, String data) {
    appendTag(sb, name, attributes, data, true, true);
  }

  
  public static void appendAttrib(StringBuffer sb, Object name, Object value) {
    if (value == null)
      return;
    
    
    
    sb.append(" ").append(name).append("=\"").append(value).append("\"");
  }

  public static void toXml(StringBuffer sb, String name, Vector properties) {
    for (int i = 0; i < properties.size(); i++) {
      Object[] o = (Object[]) properties.get(i);
      appendTag(sb, name, (Object[]) o[0], o[1]);
    }
  }

  public static Object escape(String name, Vector atts, Object value,
                              boolean asString, String indent) {

    StringBuffer sb;
    String type = (value == null ? null : value.getClass().getName());
    if (name == "token") {
      type = null;
      value = Token.nameOf(((Integer) value).intValue());
    } else if (type != null) {
      type = type.substring(0, type.lastIndexOf("[") + 1)
          + type.substring(type.lastIndexOf(".") + 1);
      if (value instanceof String) {
        value = wrapCdata(value);
      } else if (value instanceof BitSet) {
        value = Escape.escape((BitSet) value);
      } else if (value instanceof Vector) {
        Vector v = (Vector) value;
        sb = new StringBuffer("\n");
        if (atts == null)
          atts = new Vector();
        atts.add(new Object[] { "count", new Integer(v.size()) });
        for (int i = 0; i < v.size(); i++)
          sb.append(
              escape(null, null, v.get(i), true, indent + "  "));
        value = sb.toString();
      } else if (value instanceof Hashtable) {
        Hashtable ht = (Hashtable) value;
        sb = new StringBuffer("\n");
        Enumeration e = ht.keys();
        int n = 0;
        while (e.hasMoreElements()) {
          n++;
          String name2 = (String) e.nextElement();
          sb.append(
              escape(name2, null, ht.get(name2), true, indent + "  "));
        }
        if (atts == null)
          atts = new Vector();
        atts.add(new Object[] { "count", new Integer(n) });
        value = sb.toString();
      } else if (type.startsWith("[")) {
        if (value instanceof float[]) {
          float[] f = (float[]) value;
          sb = new StringBuffer("\n");
          if (atts == null)
            atts = new Vector();
          atts.add(new Object[] { "count", new Integer(f.length) });
          for (int i = 0; i < f.length; i++)
            sb.append(escape(null, null, new Float(f[i]), true, indent + "  "));
          value = sb.toString();
        } else if (value instanceof int[]) {
          int[] iv = (int[]) value;
          sb = new StringBuffer("\n");
          if (atts == null)
            atts = new Vector();
          atts.add(new Object[] { "count", new Integer(iv.length) });
          for (int i = 0; i < iv.length; i++)
            sb.append(escape(null, null, new Integer(iv[i]), true, indent + "  "));
          value = sb.toString();
          
        } else if (value instanceof Object[]) {
          Object[] o = (Object[]) value;
          sb = new StringBuffer("\n");
          if (atts == null)
            atts = new Vector();
          atts.add(new Object[] { "count", new Integer(o.length) });
          for (int i = 0; i < o.length; i++)
            sb.append(escape(null, null, o[i], true, indent + "  "));
          value = sb.toString();
          
        } else {
          
        }
          
      }
    }
    Vector attributes = new Vector();
    attributes.add(new Object[] { "name", name });
    attributes.add(new Object[] { "type", type });
    if (atts != null)
      for (int i = 0; i < atts.size(); i++)
        attributes.add(atts.get(i));
    if (!asString)
      return new Object[] { attributes.toArray(), value };
    sb = new StringBuffer();
    sb.append(indent);
    appendTag(sb, "val", attributes.toArray(), null, false, false);
    sb.append(value);
    if (value instanceof String && ((String)value).contains("\n"))
      sb.append(indent);      
    closeTag(sb, "val");
    return sb.toString();
  }

}
