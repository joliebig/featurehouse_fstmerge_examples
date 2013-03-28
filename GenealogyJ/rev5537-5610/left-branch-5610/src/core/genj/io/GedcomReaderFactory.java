
package genj.io;

import genj.crypto.Enigma;
import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.Grammar;
import genj.gedcom.Property;
import genj.gedcom.PropertyDate;
import genj.gedcom.PropertyXRef;
import genj.gedcom.Submitter;
import genj.util.EnvironmentChecker;
import genj.util.MeteredInputStream;
import genj.util.Origin;
import genj.util.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GedcomReaderFactory {
  
  private final static Resources RESOURCES = Resources.get("genj.io");
  private static Logger LOG = Logger.getLogger("genj.io");
  
  
  private final static int ENTITY_AVG_SIZE = 150;
  
  
  public static GedcomReader createReader(Origin origin, GedcomReaderContext context) throws IOException {
    LOG.info("Initializing reader for "+origin);
    return new Impl(new Gedcom(origin), origin.open(), context!=null?context:new DefaultContext());
  }

  public static GedcomReader createReader(InputStream in, GedcomReaderContext context) throws IOException {
    return new Impl(new Gedcom(), in, context!=null?context:new DefaultContext());
  }
  
  
  private static class Impl implements GedcomReader {

    
    private final static int READHEADER = 0, READENTITIES = 1, LINKING = 2;
  
    
    private Gedcom gedcom;
    private int progress;
    private int entity = 0;
    private int state;
    private int length;
    private String gedcomLine;
    private ArrayList<LazyLink> lazyLinks = new ArrayList<LazyLink>();
    private String tempSubmitter;
    private boolean cancel=false;
    private Thread worker;
    private Object lock = new Object();
    private EntityReader reader;
    private MeteredInputStream meter;
    private Enigma enigma;
    private GedcomReaderContext context;
  
    
    private Impl(Gedcom ged, InputStream in, GedcomReaderContext context) throws IOException {
      
      GedcomEncodingSniffer sniffer = new GedcomEncodingSniffer(in);
      Charset charset = sniffer.getCharset();
      String encoding = sniffer.getEncoding();
  
      if (!sniffer.isDeterministic())
        context.handleWarning(0, RESOURCES.getString("read.warn.nochar"), new Context(ged));
  
      String charsetName = EnvironmentChecker.getProperty(this, "genj.gedcom.charset", null, "checking for forced charset for read of "+ged.getName());
      if (charsetName!=null) {
        try {
          charset = Charset.forName(charsetName);
          encoding = Gedcom.UTF8;
        } catch (Throwable t) {
          LOG.log(Level.WARNING, "Can't force charset "+charset, t);
        }
      }
  
      
      this.length = sniffer.available();
      this.gedcom = ged;
      this.gedcom.setEncoding(encoding);
      this.context = context;
      this.meter = new MeteredInputStream(sniffer);
      this.reader = new EntityReader(new InputStreamReader(meter, charset));
  
      
    }
  
    
    public void cancelTrackable() {
  
      
      cancel=true;
      synchronized (lock) {
        if (worker!=null)
          worker.interrupt();
      }
      
    }
  
    
    public int getProgress() {
      
      if (state==READENTITIES&&length>0)
          progress = (int)Math.min(100, meter.getCount()*100/length);
  
      
      return progress;
    }
  
    
    public String getState() {
      switch (state) {
        case READHEADER :
          return RESOURCES.getString("progress.read.header");
        case READENTITIES :default:
          return RESOURCES.getString("progress.read.entities", new String[]{ ""+reader.getLines(), ""+entity} );
        case LINKING      :
          return RESOURCES.getString("progress.read.linking");
      }
    }
  
    
    public int getLines() {
      return reader.getLines();
    }
  
    
    public Gedcom read() throws GedcomEncryptionException, GedcomIOException, GedcomFormatException {
  
      
      if (gedcom==null)
        throw new IllegalStateException("can't call read() twice");
  
      
      synchronized (lock) {
        worker=Thread.currentThread();
      }
  
      
      try {
        readGedcom();
        return gedcom;
      } catch (GedcomIOException gex) {
        throw gex;
      } catch (Throwable t) {
        
        LOG.log(Level.SEVERE, "unexpected throwable", t);
        throw new GedcomIOException(t.toString(), reader.getLines());
      } finally  {
        
        try { reader.in.close(); } catch (Throwable t) {};
        
        synchronized (lock) {
          worker=null;
        }
        
        gedcom  = null;
        lazyLinks.clear();
      }
  
      
    }
  
    
    private void readGedcom() throws IOException {
  
      long start = System.currentTimeMillis();
  
      
      readHeader();
      state++;
      long header =System.currentTimeMillis();
  
      
      while (reader.readEntity()!=null);
  
      long records = System.currentTimeMillis();
  
      
      state++;
  
      
      if (tempSubmitter.length()>0) {
        try {
          Submitter sub = (Submitter)gedcom.getEntity(Gedcom.SUBM, tempSubmitter.replace('@',' ').trim());
          gedcom.setSubmitter(sub);
        } catch (IllegalArgumentException t) {
          context.handleWarning(0, RESOURCES.getString("read.warn.setsubmitter", tempSubmitter), new Context(gedcom));
        }
      }
  
      
      linkReferences();
      long linking = System.currentTimeMillis();
  
      long total = System.currentTimeMillis();
      LOG.log(Level.FINE, gedcom.getName()+" loaded in "+(total-start)/1000+"s (header "+(header-start)/1000+"s, records "+(records-header)/1000+"s, linking "+(linking-records)/1000+"s)");
  
      
    }
  
    
    private void linkReferences() throws GedcomIOException {
  
      
      for (int i=0,n=lazyLinks.size(); i<n; i++) {
        LazyLink lazyLink = (LazyLink)lazyLinks.get(i);
        try {
          if (lazyLink.xref.getTarget()==null)
            lazyLink.xref.link();
          progress = Math.min(100,(int)(i*(100*2)/n));  
        } catch (GedcomException ex) {
          context.handleWarning(lazyLink.line, ex.getMessage(), new Context(lazyLink.xref));
        } catch (Throwable t) {
          throw new GedcomIOException(RESOURCES.getString("read.error.xref", new Object[]{ lazyLink.xref.getTag(), lazyLink.xref.getValue() }), lazyLink.line);
        }
      }
  
      
    }
  
    
    private boolean readHeader() throws IOException {
  
      Entity header = reader.readEntity();
      if (header==null||!header.getTag().equals("HEAD"))
        throw new GedcomFormatException(RESOURCES.getString("read.error.noheader"),0);
  
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
  
      
      tempSubmitter = header.getPropertyValue("SUBM");
  
  
  
  
  
  
      
      String source = header.getPropertyValue("SOUR");
  
  
  
  
      
      
      
      
      Property vers = header.getPropertyByPath("HEAD:GEDC:VERS");
      if (vers==null||header.getPropertyByPath("HEAD:GEDC:FORM")==null)
        context.handleWarning(0, RESOURCES.getString("read.warn.badgedc"), new Context(gedcom));
      else {
        String v = vers.getValue();
        if ("5.5".equals(v)) {
          gedcom.setGrammar(Grammar.V55);
          LOG.info("Found VERS "+v+" - Gedcom version is 5.5");
        } else if ("5.5.1".equals(v)) {
          gedcom.setGrammar(Grammar.V551);
          LOG.info("Found VERS "+v+" - Gedcom version is 5.5.1");
        } else {
          String s = RESOURCES.getString("read.warn.badversion", new String[] { v, gedcom.getGrammar().getVersion() } );
          context.handleWarning(0, RESOURCES.getString("read.warn.badversion", new String[] { v, gedcom.getGrammar().getVersion() } ), new Context(gedcom));
          LOG.warning(s);
        }
      }
  
  
      
      String lang = header.getPropertyValue("LANG");
      if (lang.length()>0) {
        gedcom.setLanguage(lang);
        LOG.info("Found LANG "+lang+" - Locale is "+gedcom.getLocale());
      }
  
      
      String encoding = header.getPropertyValue("CHAR");
      if (encoding.length()>0) {
        gedcom.setEncoding(encoding);
        if (encoding.equals("ASCII"))
          context.handleWarning(0, RESOURCES.getString("read.warn.ascii"), new Context(gedcom));
      }
  
      
      
      
      Property plac = header.getProperty("PLAC");
      if (plac!=null) {
        String form = plac.getPropertyValue("FORM");
        gedcom.setPlaceFormat(form);
        LOG.info("Found Place.Format "+form);
      }
  
      
      gedcom.deleteEntity(header);
  
      
      return true;
    }
  
    
    private class EntityReader extends PropertyReader {
  
      
      EntityReader(Reader in) {
        super(in, null, false);
      }
  
      
      Entity readEntity() throws IOException {
  
        if (!readLine(true))
          throw new GedcomFormatException(RESOURCES.getString("read.error.norecord"),lines);
  
        if (level!=0)
          throw new GedcomFormatException(RESOURCES.getString("read.error.nonumber"), lines);
  
        
        if (tag.equals("TRLR")) {
          
          if (readLine(true))
            throw new GedcomFormatException(RESOURCES.getString("read.error.aftertrlr"), lines);
          return null;
        }
  
        
        Entity result;
        try {
  
          result = gedcom.createEntity(tag, xref);
  
          
          if (result.getClass()!=Entity.class&&xref.length()==0)
            context.handleWarning(getLines(), RESOURCES.getString("read.warn.recordnoid", Gedcom.getName(tag)), new Context(result));
  
          
          result.setValue(value);
  
          
          readProperties(result, 0, 0);
  
        } catch (GedcomException ex) {
          throw new GedcomIOException(ex.getMessage(), lines);
        }
  
        
        if (tag.equals("TRLR"))
  
        
        entity++;
        return result;
      }
  
      
      protected void readProperties(Property prop, int currentLevel, int pos) throws IOException {
        
        super.readProperties(prop, currentLevel, pos);
        
        decryptLazy(prop);
      }
  
      
      private void decryptLazy(Property prop) throws GedcomIOException {
  
        
        if (prop instanceof PropertyXRef)
          return;
        
        if ((prop instanceof PropertyDate)&&prop.isValid())
          return;
  
        
        String value = prop.getValue();
        if (!Enigma.isEncrypted(value))
          return;
  
        
        prop.setPrivate(true, false);
  
        
        if (gedcom.getPassword()==Gedcom.PASSWORD_UNKNOWN) 
          return;
  
        
        while (enigma==null) {

          
          String pwd = context.getPassword();
          
          
          if (pwd==null) {
            context.handleWarning(getLines(), RESOURCES.getString("crypt.password.unknown"), new Context(prop));
            gedcom.setPassword(Gedcom.PASSWORD_UNKNOWN);
            return;
          }
          
          
          try {
            enigma = Enigma.getInstance(pwd);
            enigma.decrypt(value);
          } catch (IOException e) {
            enigma = null;
          }

          
        }
  
        
        try {
          prop.setValue(enigma.decrypt(value));
        } catch (IOException e) {
          throw new GedcomIOException(RESOURCES.getString("crypt.password.invalid"), lines);
        }
  
        
      }
  
      
      protected void link(PropertyXRef xref, int line) {
        
        lazyLinks.add(new LazyLink(xref, line));
      }
  
      
      protected void trackEmptyLine() {
        
        if (!"TRLR".equals(tag))
          context.handleWarning(getLines(), RESOURCES.getString("read.error.emptyline"), new Context(gedcom));
      }
  
      
      protected void trackBadLevel(int level, Property parent) {
        context.handleWarning(getLines(), RESOURCES.getString("read.warn.badlevel", ""+level), new Context(parent));
      }
  
      
      protected void trackBadProperty(Property property, String message) {
        context.handleWarning(getLines(), message, new Context(property));
      }
  
    } 
  
    
    private static class LazyLink {
  
      private PropertyXRef xref;
      private int line;
  
      LazyLink(PropertyXRef xref, int line) {
        this.xref = xref;
        this.line = line;
      }
    }

  } 
  
  private static class DefaultContext implements GedcomReaderContext {
    public String getPassword() {
      return null;
    }
    public void handleWarning(int line, String warning, Context context) {
    }
  }

}