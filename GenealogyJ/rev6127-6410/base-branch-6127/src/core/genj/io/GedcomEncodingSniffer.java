package genj.io;

import genj.gedcom.Gedcom;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GedcomEncodingSniffer extends BufferedInputStream {

  private final static Logger LOG = Logger.getLogger("genj.io");

  static final byte[] BOM_UTF8 = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF }, BOM_UTF16BE = { (byte) 0xFE, (byte) 0xFF }, BOM_UTF16LE = { (byte) 0xFF, (byte) 0xFE };

  private boolean deterministic = true;
  private String encoding;
  private Charset charset;
  private String header;
  private String warning;

  
  public GedcomEncodingSniffer(InputStream in) throws IOException {

    super(in, 4096);

    
    super.mark(4096);
    super.read();
    super.reset();

    
    if (matchPrefix(BOM_UTF8)) {
      LOG.info("Found BOM_UTF8 - trying encoding UTF-8");
      charset = Charset.forName("UTF-8");
      encoding = Gedcom.UTF8;
      return;
    }
    if (matchPrefix(BOM_UTF16BE)) {
      LOG.info("Found BOM_UTF16BE - trying encoding UTF-16BE");
      charset = Charset.forName("UTF-16BE");
      encoding = Gedcom.UNICODE;
      return;
    }
    if (matchPrefix(BOM_UTF16LE)) {
      LOG.info("Found BOM_UTF16LE - trying encoding UTF-16LE");
      charset = Charset.forName("UTF-16LE");
      encoding = Gedcom.UNICODE;
      return;
    }

    
    if (matchCHAR(Gedcom.UTF8) || matchCHAR("UTF8")) {
      LOG.info("Found " + Gedcom.UTF8 + " - trying encoding UTF-8");
      charset = Charset.forName("UTF-8");
      encoding = Gedcom.UTF8;
      return;
    }
    if (matchCHAR(Gedcom.UNICODE)) {
      LOG.info("Found single byte " + Gedcom.UNICODE + " - trying encoding UTF-8");
      charset = Charset.forName("UTF-8");
      encoding = Gedcom.UNICODE;
      return;
    }

    if (matchCHAR(Gedcom.UNICODE, "UTF-16BE")) {
      LOG.info("Found " + Gedcom.UNICODE + "/big endian - trying encoding UTF-16BE");
      charset = Charset.forName("UTF-16BE");
      encoding = Gedcom.UNICODE;
      return;
    }
    if (matchCHAR(Gedcom.UNICODE, "UTF-16LE")) {
      LOG.info("Found " + Gedcom.UNICODE + "/little endian - trying encoding UTF-16LE");
      charset = Charset.forName("UTF-16LE");
      encoding = Gedcom.UNICODE;
      return;
    }
    if (matchCHAR(Gedcom.ASCII)) {
      
      
      LOG.info("Found " + Gedcom.ASCII + " - trying encoding ISO-8859-1");
      charset = Charset.forName("ISO-8859-1"); 
      encoding = Gedcom.ASCII;
      return;
    }
    if (matchCHAR(Gedcom.ANSEL)) {
      LOG.info("Found " + Gedcom.ANSEL + " - trying encoding ANSEL");
      charset = new AnselCharset();
      encoding = Gedcom.ANSEL;
      return;
    }
    if (matchCHAR(Gedcom.ANSI)) {
      LOG.info("Found " + Gedcom.ANSI + " - trying encoding Windows-1252");
      charset = Charset.forName("Windows-1252");
      encoding = Gedcom.ANSI;
      return;
    }
    if (matchCHAR(Gedcom.LATIN1) || matchCHAR("IBMPC")) { 
                                                          
      LOG.info("Found " + Gedcom.LATIN1 + " or IBMPC - trying encoding ISO-8859-1");
      charset = Charset.forName("ISO-8859-1");
      encoding = Gedcom.LATIN1;
      return;
    }

    
    deterministic = false;
    LOG.info("Could not sniff encoding - trying ANSEL");
    charset = new AnselCharset();
    encoding = Gedcom.ANSEL;

  }

  public boolean isDeterministic() {
    return deterministic;
  }

  
  private boolean matchCHAR(String line) {
    return matchCHAR(line, null);
  }

  private boolean matchCHAR(String value, String charset) {
    try {
      String header = charset != null ? new String(super.buf, super.pos, super.count, charset) : new String(super.buf, super.pos, super.count);
      return header.indexOf("1 CHAR " + value) >= 0;
    } catch (UnsupportedEncodingException e) {
      LOG.log(Level.WARNING, "Couldn't parse header in charset " + charset, e);
      return false;
    }
  }

  
  private boolean matchPrefix(byte[] prefix) throws IOException {
    
    if (super.count < prefix.length)
      return false;
    
    for (int i = 0; i < prefix.length; i++) {
      if (super.buf[pos + i] != prefix[i])
        return false;
    }
    
    super.skip(prefix.length);
    
    return true;
  }

  
  Charset getCharset() {
    return charset;
  }

  
  String getEncoding() {
    return encoding;
  }

} 

