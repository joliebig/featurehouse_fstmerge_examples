
package genj.io;

import genj.Version;
import genj.crypto.Enigma;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.gedcom.PropertyXRef;
import genj.gedcom.time.PointInTime;
import genj.util.Trackable;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.UnmappableCharacterException;
import java.nio.charset.UnsupportedCharsetException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;


public class GedcomWriter implements Trackable {

  private static Logger LOG = Logger.getLogger("genj.io");
  
  
  private Gedcom gedcom;
  private BufferedWriter out;
  private String file;
  private String date;
  private String time;
  private int total;
  private int line;
  private int entity;
  private boolean cancel = false;
  private Filter[] filters = new Filter[0];
  private Enigma enigma = null;

  
  public GedcomWriter(Gedcom ged, OutputStream stream) throws IOException, GedcomEncodingException  {
    
    Calendar now = Calendar.getInstance();

    
    gedcom = ged;
    file = ged.getOrigin()==null ? "Uknown" : ged.getOrigin().getFileName();
    line = 0;
    date = PointInTime.getNow().getValue();
    time = new SimpleDateFormat("HH:mm:ss").format(now.getTime());

    CharsetEncoder encoder = getCharset(false, stream, ged.getEncoding()).newEncoder();
    encoder.onUnmappableCharacter(CodingErrorAction.REPORT);
    out = new BufferedWriter(new OutputStreamWriter(stream, encoder));
    
    
  }

  
  private Charset getCharset(boolean writeBOM, OutputStream out, String encoding) throws GedcomEncodingException {

    
    try {
      
      if (Gedcom.UNICODE.equals(encoding)) {
        if (writeBOM) try {
          out.write(GedcomReader.SniffedInputStream.BOM_UTF16BE);
        } catch (Throwable t) {
          
        }
        return Charset.forName("UTF-16BE");
      }
      
      if (Gedcom.UTF8.equals(encoding)) {
        if (writeBOM) try {
          out.write(GedcomReader.SniffedInputStream.BOM_UTF8);
        } catch (Throwable t) {
          
        }
        return Charset.forName("UTF-8");
      }
      
      if (Gedcom.ASCII.equals(encoding))
        return Charset.forName("ISO-8859-1"); 
      
      if (Gedcom.LATIN1.equals(encoding))
        return Charset.forName("ISO-8859-1");
      
      if (Gedcom.ANSI.equals(encoding))
        return Charset.forName("Windows-1252");
    } catch (UnsupportedCharsetException e) {
    }

    
    if (Gedcom.ANSEL.equals(encoding)) 
      return new AnselCharset();
      
    
    throw new GedcomEncodingException("Can't write with unknown encoding " + encoding);

  }

  
  public void cancelTrackable() {
    cancel = true;
  }

  
  public int getProgress() {
    if (entity == 0) 
      return 0;
    return entity * 100 / total;
  }

  
  public String getState() {
    return line + " Lines & " + entity + " Entities";
  }

  
  public void setFilters(Filter[] fs) {
    if (fs == null)
      fs = new Filter[0];
    filters = fs;
  }
  
  
  public int getLines() {
    return line;
  }
  
  
  public void write() throws GedcomIOException {

    
    if (gedcom==null)
      throw new IllegalStateException("can't call write() twice");
    
    Collection ents = gedcom.getEntities(); 
    total = ents.size();

    
    try {

      
      writeHeader();
      writeEntities(ents);
      writeTail();

      
      out.close();

    } catch( GedcomIOException ioe ) {
      throw ioe;
    } catch (Exception ex) {
      throw new GedcomIOException("Error while writing / "+ex.getMessage(), line);
    } finally {
      gedcom = null;
    }

    
  }
  
  
  private void writeLine(String line) throws IOException {
    out.write(line);
    out.newLine();
    this.line++;
  }
  
  
  private void writeHeader() throws IOException {
    
    
    writeLine( "0 HEAD");
    writeLine( "1 SOUR GENJ");
    writeLine( "2 VERS "+Version.getInstance());
    writeLine( "2 NAME GenealogyJ");
    writeLine( "2 CORP Nils Meier");
    writeLine( "3 ADDR http://genj.sourceforge.net");
    writeLine( "1 DEST ANY");
    writeLine( "1 DATE "+date);
    writeLine( "2 TIME "+time);
    if (gedcom.getSubmitter()!=null)
      writeLine( "1 SUBM @"+gedcom.getSubmitter().getId()+'@');
    writeLine( "1 FILE "+file);
    writeLine( "1 GEDC");
    writeLine( "2 VERS "+gedcom.getGrammar().getVersion());
    writeLine( "2 FORM Lineage-Linked");
    writeLine( "1 CHAR "+gedcom.getEncoding());
    if (gedcom.getLanguage()!=null)
      writeLine( "1 LANG "+gedcom.getLanguage());
    if (gedcom.getPlaceFormat().length()>0) {
      writeLine( "1 PLAC");
      writeLine( "2 FORM "+gedcom.getPlaceFormat());
    }
    
  }

  
  private void writeEntities(Collection ents) throws IOException {

    
    for (Iterator it=ents.iterator();it.hasNext();) {
      
      if (cancel) throw new GedcomIOException("Operation cancelled", line);
      
      Entity e = (Entity)it.next();
      
      try {
        line += new EntityWriter().write(0, e);
      } catch(UnmappableCharacterException unme) {
        throw new GedcomEncodingException(e, gedcom.getEncoding());
      }

      
      entity++;
    }

    
  }

  
  private void writeTail() throws IOException {
    
    writeLine("0 TRLR");
  }

  
  private class EntityWriter extends PropertyWriter {
    
    
    EntityWriter() {
      super(out, false);
    }

    
    protected void writeProperty(int level, Property prop) throws IOException {
      
      
      Entity target = null;
      if (prop instanceof PropertyXRef) {
        Property p = ((PropertyXRef) prop).getTarget();
        if (p != null)
          target = p.getEntity();
      }
      for (int f = 0; f < filters.length; f++) {
        if (filters[f].checkFilter(prop) == false)
          return;
        if (target != null)
          if (filters[f].checkFilter(target) == false)
            return;
      }

      
        super.writeProperty(level, prop);
    }
     
    
    protected String getValue(Property prop) throws IOException {
      return prop.isPrivate() ? encrypt(prop.getValue()) : super.getValue(prop);
    }
    
    
    private String encrypt(String value) throws IOException {
      
      
      if (value.length()==0)
        return value;
      
      
      if (enigma==null) {

        
        if (gedcom.getPassword()==Gedcom.PASSWORD_UNKNOWN)
          return value;
          
        
        if (gedcom.getPassword().length()==0)
          return value;

        
        if (gedcom.getPassword()==Gedcom.PASSWORD_NOT_SET)
          throw new IOException("Password not set - needed for encryption");
          
        
        enigma = Enigma.getInstance(gedcom.getPassword());
        if (enigma==null) 
          throw new IOException("Encryption not available");
          
      }
      
      
      return enigma.encrypt(value);
    }

  } 
  
} 
