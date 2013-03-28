
package genj.io;

import genj.gedcom.Entity;
import genj.gedcom.MultiLineProperty;
import genj.gedcom.Property;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;


public class PropertyWriter {
  
  boolean useIndents = false;
  private int lines = 0;
  private BufferedWriter out;

    
  public PropertyWriter(Writer out, boolean useIndents) {
    this.out = new BufferedWriter(out);
    this.useIndents = useIndents;
  }
  
  
  public int write(int level, Property prop) throws IOException {
    
    writeProperty(level, prop);
    out.flush();
    
    
    return lines;
  }
  
  
  public int getLines() {
    return lines;
  }
  
  
  protected String getValue(Property prop) throws IOException {
    return prop.getValue();
  }
  
  
  protected void writeProperty(int level, Property prop) throws IOException {
    
    
    if (prop.isTransient())
      return;
    
    
    if (prop instanceof MultiLineProperty)
      writeMultiLine(level, prop);
    else
      writeLine(level, getTag(prop), getValue(prop));

    
    int num = prop.getNoOfProperties();
    for (int i = 0; i < num; i++) 
      writeProperty(level+1, prop.getProperty(i));
    
    
    
  }
  
  
  protected String getTag(Property prop) {
    
    
    if (prop instanceof Entity) {
      
      String xref = ((Entity)prop).getId();
      if (xref.length()>0) 
        return '@'+xref+"@ "+prop.getTag();
    }
    
    
    return prop.getTag();
  }
  
  
  private void writeMultiLine(int level, Property prop) throws IOException {
    
    
    MultiLineProperty.Iterator lines = ((MultiLineProperty)prop).getLineIterator();
    lines.setValue(getValue(prop));
    
    
    writeLine(level + lines.getIndent(), getTag(prop), lines.getValue());
    while (lines.next()) {
      writeLine(level + lines.getIndent(), lines.getTag(), lines.getValue());
    }
    
    
  }
  
  
  private void writeLine(int level, String tag, String value) throws IOException {

    
    if (useIndents) {
      for (int i=0;i<level;i++)
        out.write(' ');
    } else {
        out.write(Integer.toString(level));
        out.write(' ');
    }
    
    
    out.write(tag);

    
    if (value!=null&&value.length()>0) {
      
      out.write(' ');
      out.write(value);
    }
    out.newLine();

    
    lines++;

  }
      
} 
