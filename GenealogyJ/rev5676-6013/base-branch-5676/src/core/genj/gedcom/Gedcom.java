
package genj.gedcom;

import genj.util.Origin;
import genj.util.ReferenceSet;
import genj.util.Resources;
import genj.util.swing.ImageIcon;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Gedcom implements Comparable {
  
   final static Logger LOG = Logger.getLogger("genj.gedcom");
  
  
  static private Random seed = new Random();
  static  Resources resources = Resources.get(Gedcom.class);

  public static final String
   
    UNICODE  = "UNICODE", 
    ASCII    = "ASCII",      
    ANSEL    = "ANSEL",
    UTF8     = "UTF-8", 
   
    LATIN1   = "LATIN1",     
    ANSI     = "ANSI";       
  
    
  public static final String[] ENCODINGS = { 
    ANSEL, UNICODE, ASCII, LATIN1, ANSI, UTF8 
  };

    
  public static final String[] LANGUAGES = {
    "Afrikaans","Albanian","Amharic","Anglo-Saxon","Arabic","Armenian","Assamese",
    "Belorusian","Bengali","Braj","Bulgarian","Burmese", 
    "Cantonese","Catalan","Catalan_Spn","Church-Slavic","Czech", 
    "Danish","Dogri","Dutch", 
    "English","Esperanto","Estonian", 
    "Faroese","Finnish","French", 
    "Georgian","German","Greek","Gujarati", 
    "Hawaiian","Hebrew","Hindi","Hungarian", 
    "Icelandic","Indonesian","Italian",
    "Japanese", 
    "Kannada","Khmer","Konkani","Korean",
    "Lahnda","Lao","Latvian","Lithuanian", 
    "Macedonian","Maithili","Malayalam","Mandrin","Manipuri","Marathi","Mewari", 
    "Navaho","Nepali","Norwegian",
    "Oriya", 
    "Pahari","Pali","Panjabi","Persian","Polish","Prakrit","Pusto","Portuguese", 
    "Rajasthani","Romanian","Russian", 
    "Sanskrit","Serb","Serbo_Croa","Slovak","Slovene","Spanish","Swedish", 
    "Tagalog","Tamil","Telugu","Thai","Tibetan","Turkish", 
    "Ukrainian","Urdu", 
    "Vietnamese", 
    "Wendic" ,
    "Yiddish"
  };

  
  public final static String
    INDI = "INDI", 
    FAM  = "FAM" ,
    OBJE = "OBJE", 
    NOTE = "NOTE", 
    SOUR = "SOUR", 
    SUBM = "SUBM", 
    REPO = "REPO";
    
  public final static String[] 
    ENTITIES = { INDI, FAM, OBJE, NOTE, SOUR, SUBM, REPO };      

  private final static Map 
    E2PREFIX = new HashMap();
    static {
      E2PREFIX.put(INDI, "I");
      E2PREFIX.put(FAM , "F");
      E2PREFIX.put(OBJE, "M");
      E2PREFIX.put(NOTE, "N");
      E2PREFIX.put(SOUR, "S");
      E2PREFIX.put(SUBM, "B");
      E2PREFIX.put(REPO, "R");
    }
    
  private final static Map 
    E2TYPE = new HashMap();
    static {
      E2TYPE.put(INDI, Indi.class);
      E2TYPE.put(FAM , Fam .class);
      E2TYPE.put(OBJE, Media.class);
      E2TYPE.put(NOTE, Note.class);
      E2TYPE.put(SOUR, Source.class);
      E2TYPE.put(SUBM, Submitter.class);
      E2TYPE.put(REPO, Repository.class);
    }
    
  private final static Map
    E2IMAGE = new HashMap();

  
  private final static ImageIcon image = new ImageIcon(Gedcom.class, "images/Gedcom");
  
  
  private Submitter submitter;
  
  
  private Grammar grammar = Grammar.V55;

  
  private Origin origin;
  
  
  private PropertyChange lastChange = null;
  
  
  private int maxIDLength = 0;
  
  
  private LinkedList allEntities = new LinkedList();
  private Map tag2id2entity = new HashMap();
  
  
  private boolean isDirty = false;
  private List 
    undoHistory = new ArrayList(),
    redoHistory = new ArrayList();

  
  private Object writeSemaphore = new Object();
  
  
  private Lock lock = null;
  
  
  private List listeners = new ArrayList(10);
  
  
  private Map tags2refsets = new HashMap();

  
  private String encoding = ENCODINGS[Math.min(ENCODINGS.length-1, Options.getInstance().defaultEncoding)];
    
  
  private String language = null;
  
  
  private Locale cachedLocale = null;

  
  private Collator cachedCollator = null;
  
  
  private String placeFormat = "";

  
  private String password = PASSWORD_NOT_SET;

  public final static String
    PASSWORD_NOT_SET = "PASSWORD_NOT_SET",
    PASSWORD_UNKNOWN = "PASSWORD_UNKNOWN";

  
  public Gedcom() {
    this(null);
  }

  
  public Gedcom(Origin origin) {
    
    this.origin = origin;
    
  }

  
  public Origin getOrigin() {
    return origin;
  }
  
  
  public void setGrammar(Grammar grammar) {
    this.grammar = grammar;
  }
  
  
  public Grammar getGrammar() {
    return grammar;
  }

  
  public Submitter getSubmitter() {
    if (submitter==null)
      return (Submitter)getFirstEntity(Gedcom.SUBM);
    return submitter;
  }
  
  
  public void setSubmitter(Submitter set) {
    
    
    if (set!=null&&!getEntityMap(SUBM).containsValue(set))
      throw new IllegalArgumentException("Submitter is not part of this gedcom");

    
    final Submitter old = submitter;
    submitter = set;
    
    
    if (lock==null) 
      return;
      
    
    lock.addChange(new Undo() {
      void undo() {
          setSubmitter(old);
      }
    });
    
    
    GedcomListener[] gls = (GedcomListener[])listeners.toArray(new GedcomListener[listeners.size()]);
    for (int l=0;l<gls.length;l++) {
      GedcomListener gl = (GedcomListener)gls[l];
      if (gl instanceof GedcomMetaListener) try {
        ((GedcomMetaListener)gl).gedcomHeaderChanged(this);
      } catch (Throwable t) {
        LOG.log(Level.FINE, "exception in gedcom listener "+gls[l], t);
      }
    }
    
    
  }
  
  
  public String toString() {
    return getName();
  }

  
  public void addGedcomListener(GedcomListener listener) {
    if (listener==null)
      throw new IllegalArgumentException("listener can't be null");
    synchronized (listeners) {
      if (!listeners.add(listener))
        throw new IllegalArgumentException("can't add gedcom listener "+listener+"twice");
    }
    LOG.log(Level.FINER, "addGedcomListener() from "+new Throwable().getStackTrace()[1]+" (now "+listeners.size()+")");
    
  }

  
  public void removeGedcomListener(GedcomListener listener) {
    synchronized (listeners) {
      
      
      
      listeners.remove(listener);
    }
    LOG.log(Level.FINER, "removeGedcomListener() from "+new Throwable().getStackTrace()[1]+" (now "+listeners.size()+")");
  }
  
  
  private List getCurrentUndoSet() {
    return (List)undoHistory.get(undoHistory.size()-1);
  }
  
  
  protected void propagateXRefLinked(final PropertyXRef property1, final PropertyXRef property2) {
    
    if (LOG.isLoggable(Level.FINER))
      LOG.finer("Property "+property1.getTag()+" and "+property2.getTag()+" linked");
    
    
    if (lock==null) 
      return;
      
    
    lock.addChange(new Undo() {
      void undo() {
        property1.unlink();
      }
    });
    
    
    GedcomListener[] gls = (GedcomListener[])listeners.toArray(new GedcomListener[listeners.size()]);
    for (int l=0;l<gls.length;l++) {
      try {
        gls[l].gedcomPropertyChanged(this, property1);
        gls[l].gedcomPropertyChanged(this, property2);
      } catch (Throwable t) {
        LOG.log(Level.FINE, "exception in gedcom listener "+gls[l], t);
      }
    }

    
  }
  
  
  protected void propagateXRefUnlinked(final PropertyXRef property1, final PropertyXRef property2) {
    
    if (LOG.isLoggable(Level.FINER))
      LOG.finer("Property "+property1.getTag()+" and "+property2.getTag()+" unlinked");
    
    
    if (lock==null) 
      return;
      
    
    lock.addChange(new Undo() {
        void undo() {
          property1.link(property2);
        }
      });
    
    
    GedcomListener[] gls = (GedcomListener[])listeners.toArray(new GedcomListener[listeners.size()]);
    for (int l=0;l<gls.length;l++) {
      try {
        gls[l].gedcomPropertyChanged(this, property1);
        gls[l].gedcomPropertyChanged(this, property2);
      } catch (Throwable t) {
        LOG.log(Level.FINE, "exception in gedcom listener "+gls[l], t);
      }
    }

    
  }

  
  protected void propagateEntityAdded(final Entity entity) {
    
    if (LOG.isLoggable(Level.FINER))
      LOG.finer("Entity "+entity.getId()+" added");
    
    
    if (lock==null) 
      return;
      
    
    lock.addChange(new Undo() {
      void undo() {
        deleteEntity(entity);
      }
    });
    
    
    GedcomListener[] gls = (GedcomListener[])listeners.toArray(new GedcomListener[listeners.size()]);
    for (int l=0;l<gls.length;l++) {
      try {
        gls[l].gedcomEntityAdded(this, entity);
      } catch (Throwable t) {
        LOG.log(Level.FINE, "exception in gedcom listener "+gls[l], t);
      }
    }

    
  }
  
  
  protected void propagateEntityDeleted(final Entity entity) {
    
    if (LOG.isLoggable(Level.FINER))
      LOG.finer("Entity "+entity.getId()+" deleted");
    
    
    if (lock==null) 
      return;
    
    
    lock.addChange(new Undo() {
        void undo() throws GedcomException  {
          addEntity(entity);
        }
      });
    
    
    GedcomListener[] gls = (GedcomListener[])listeners.toArray(new GedcomListener[listeners.size()]);
    for (int l=0;l<gls.length;l++) {
      try {
        gls[l].gedcomEntityDeleted(this, entity);
      } catch (Throwable t) {
        LOG.log(Level.FINE, "exception in gedcom listener "+gls[l], t);
      }
    }

    
  }
  
  
  protected void propagatePropertyAdded(Entity entity, final Property container, final int pos, Property added) {
    
    if (LOG.isLoggable(Level.FINER))
      LOG.finer("Property "+added.getTag()+" added to "+container.getTag()+" at position "+pos+" (entity "+entity.getId()+")");
    
    
    if (lock==null) 
      return;
      
    
    lock.addChange(new Undo() {
        void undo() {
          container.delProperty(pos);
        }
      });
    
    
    GedcomListener[] gls = (GedcomListener[])listeners.toArray(new GedcomListener[listeners.size()]);
    for (int l=0;l<gls.length;l++) {
      try {
        gls[l].gedcomPropertyAdded(this, container, pos, added);
      } catch (Throwable t) {
        LOG.log(Level.FINE, "exception in gedcom listener "+gls[l], t);
      }
    }

    
  }
  
  
  protected void propagatePropertyDeleted(Entity entity, final Property container, final int pos, final Property deleted) {
    
    if (LOG.isLoggable(Level.FINER))
      LOG.finer("Property "+deleted.getTag()+" deleted from "+container.getTag()+" at position "+pos+" (entity "+entity.getId()+")");
    
    
    if (lock==null) 
      return;
      
    
    lock.addChange(new Undo() {
        void undo() {
          container.addProperty(deleted, pos);
        }
      });
    
    
    GedcomListener[] gls = (GedcomListener[])listeners.toArray(new GedcomListener[listeners.size()]);
    for (int l=0;l<gls.length;l++) {
      try {
        gls[l].gedcomPropertyDeleted(this, container, pos, deleted);
      } catch (Throwable t) {
        LOG.log(Level.FINE, "exception in gedcom listener "+gls[l], t);
      }
    }
    
    
  }
  
  
  protected void propagatePropertyChanged(Entity entity, final Property property, final String oldValue) {
    
    if (LOG.isLoggable(Level.FINER))
      LOG.finer("Property "+property.getTag()+" changed in (entity "+entity.getId()+")");
    
    
    if (lock==null) 
      return;
      
    
    lock.addChange(new Undo() {
        void undo() {
          property.setValue(oldValue);
        }
      });
    
    
    GedcomListener[] gls = (GedcomListener[])listeners.toArray(new GedcomListener[listeners.size()]);
    for (int l=0;l<gls.length;l++) {
      try {
        gls[l].gedcomPropertyChanged(this, property);
      } catch (Throwable t) {
        LOG.log(Level.FINE, "exception in gedcom listener "+gls[l], t);
      }
    }

    
  }

  
  protected void propagatePropertyMoved(final Property property, final Property moved, final int from, final int to) {
    
    if (LOG.isLoggable(Level.FINER))
      LOG.finer("Property "+property.getTag()+" moved from "+from+" to "+to+" (entity "+property.getEntity().getId()+")");
    
    
    if (lock==null) 
      return;
      
    
    lock.addChange(new Undo() {
        void undo() {
          property.moveProperty(moved, from<to ? from : from+1);
        }
      });
    
    
    GedcomListener[] gls = (GedcomListener[])listeners.toArray(new GedcomListener[listeners.size()]);
    for (int l=0;l<gls.length;l++) {
      try {
          gls[l].gedcomPropertyDeleted(this, property, from, moved);
          gls[l].gedcomPropertyAdded(this, property, to, moved);
      } catch (Throwable t) {
        LOG.log(Level.FINE, "exception in gedcom listener "+gls[l], t);
      }
    }

    
  }
  
  
  protected void propagateWriteLockAqcuired() {
    GedcomListener[] gls = (GedcomListener[])listeners.toArray(new GedcomListener[listeners.size()]);
    for (int l=0;l<gls.length;l++) {
      GedcomListener gl = (GedcomListener)gls[l];
      if (gl instanceof GedcomMetaListener) try {
        ((GedcomMetaListener)gl).gedcomWriteLockAcquired(this);
      } catch (Throwable t) {
        LOG.log(Level.WARNING, "exception in gedcom listener "+gls[l], t);
      }
    }
  }
  
  
  protected void propagateBeforeUnitOfWork() {
    GedcomListener[] gls = (GedcomListener[])listeners.toArray(new GedcomListener[listeners.size()]);
    for (int l=0;l<gls.length;l++) {
      GedcomListener gl = (GedcomListener)gls[l];
      if (gl instanceof GedcomMetaListener) try {
        ((GedcomMetaListener)gl).gedcomBeforeUnitOfWork(this);
      } catch (Throwable t) {
        LOG.log(Level.WARNING, "exception in gedcom listener "+gls[l], t);
      }
    }
  }
  
  
  protected void propagateAfterUnitOfWork() {
    GedcomListener[] gls = (GedcomListener[])listeners.toArray(new GedcomListener[listeners.size()]);
    for (int l=0;l<gls.length;l++) {
      GedcomListener gl = (GedcomListener)gls[l];
      if (gl instanceof GedcomMetaListener) try {
        ((GedcomMetaListener)gl).gedcomAfterUnitOfWork(this);
      } catch (Throwable t) {
        LOG.log(Level.WARNING, "exception in gedcom listener "+gls[l], t);
      }
    }
  }
  
  
  protected void propagateWriteLockReleased() {
    
    GedcomListener[] gls = (GedcomListener[])listeners.toArray(new GedcomListener[listeners.size()]);
    for (int l=0;l<gls.length;l++) {
      GedcomListener gl = (GedcomListener)gls[l];
      if (gl instanceof GedcomMetaListener) try {
        ((GedcomMetaListener)gl).gedcomWriteLockReleased(this);
      } catch (Throwable t) {
        LOG.log(Level.FINE, "exception in gedcom listener "+gls[l], t);
      }
    }
  }  
  
  
  protected void propagateEntityIDChanged(final Entity entity, final String old) throws GedcomException {
    
    Map id2entity = getEntityMap(entity.getTag());
    
    
    if (!id2entity.containsValue(entity))
      throw new GedcomException("Can't change ID of entity not part of this Gedcom instance");
    
    
    String id = entity.getId();
    if (id==null||id.length()==0)
      throw new GedcomException("Need valid ID length");
    
    
    if (getEntity(id)!=null)
      throw new GedcomException("Duplicate ID is not allowed");

    
    id2entity.remove(old);
    id2entity.put(entity.getId(), entity);
    
    
    maxIDLength = Math.max(id.length(), maxIDLength);
    
    
    if (LOG.isLoggable(Level.FINER))
      LOG.finer("Entity's ID changed from  "+old+" to "+entity.getId());
    
    
    if (lock==null) 
      return;
      
    
    lock.addChange(new Undo() {
        void undo() throws GedcomException {
          entity.setId(old);
        }
      });
    
    
    GedcomListener[] gls = (GedcomListener[])listeners.toArray(new GedcomListener[listeners.size()]);
    for (int l=0;l<gls.length;l++) {
      try {
        gls[l].gedcomPropertyChanged(this, entity);
      } catch (Throwable t) {
        LOG.log(Level.FINE, "exception in gedcom listener "+gls[l], t);
      }
    }

    
  }

  
  private void addEntity(Entity entity) throws GedcomException {
    
    String id = entity.getId();
    
    
    
    if (id.length()>0) {
      Map id2entity = getEntityMap(entity.getTag());
      if (id2entity.containsKey(id))
        throw new GedcomException(resources.getString("error.entity.dupe", id));
      
      
      id2entity.put(id, entity);
    }
    
    
    allEntities.add(entity);
    
    
    entity.addNotify(this);
    
  }
  
  
  public PropertyChange getLastChange() {
    return lastChange;
  }
  
  
  protected void updateLastChange(PropertyChange change) {
    if (lastChange==null || lastChange.compareTo(change)<0)
      lastChange = change;
  }

  
  public Entity createEntity(String tag) throws GedcomException {
    return createEntity(tag, null);
  }    
    
  
  public Entity createEntity(String tag, String id) throws GedcomException {
    
    
    if (id==null)
      id = getNextAvailableID(tag);
    
    
    maxIDLength = Math.max(id.length(), maxIDLength);

    
    Class clazz = (Class)E2TYPE.get(tag);
    if (clazz!=null) {
      if (id.length()==0)
        throw new GedcomException(resources.getString("entity.error.noid", tag));
    } else {
      clazz = Entity.class;
    }
    
    
    Entity result; 
    try {
      result = (Entity)clazz.newInstance();
    } catch (Throwable t) {
      throw new RuntimeException("Can't instantiate "+clazz);
    }

    
    result.init(tag, id);
    
    
    addEntity(result);

    
    return result;
  }  

  
  public void deleteEntity(Entity which) {

    
    
    String id = which.getId();
    if (id.length()>0) {
      
      
      Map id2entity = getEntityMap(which.getTag());
  
      
      if (!id2entity.containsKey(id))
        throw new IllegalArgumentException("Unknown entity with id "+which.getId());

      
      id2entity.remove(id);
    }
    
    
    which.beforeDelNotify();

    
    allEntities.remove(which);

    
    if (submitter==which) submitter = null;

    
  }

  
  private Map getEntityMap(String tag) {
    
    Map id2entity = (Map)tag2id2entity.get(tag);
    if (id2entity==null) {
      id2entity = new HashMap();
      tag2id2entity.put(tag, id2entity);
    }
    
    return id2entity;
  }
  
  
  public Property[] getProperties(TagPath path) {
    ArrayList result = new ArrayList(100);
    for (Iterator it=getEntities(path.getFirst()).iterator(); it.hasNext(); ) {
      Entity ent = (Entity)it.next();
      Property[] props = ent.getProperties(path);
      for (int i = 0; i < props.length; i++) result.add(props[i]);
    }
    return Property.toArray(result);
  }
  
  
  public List getEntities() {
    return Collections.unmodifiableList(allEntities);
  }

  
  public Collection getEntities(String tag) {
    return Collections.unmodifiableCollection(getEntityMap(tag).values());
  }

  
  public Entity[] getEntities(String tag, String sortPath) {
    return getEntities(tag, sortPath!=null&&sortPath.length()>0 ? new PropertyComparator(sortPath) : null);
  }

  
  public Entity[] getEntities(String tag, Comparator comparator) {
    Collection ents = getEntityMap(tag).values();
    Entity[] result = (Entity[])ents.toArray(new Entity[ents.size()]);
    
    if (comparator!=null) 
      Arrays.sort(result, comparator);
    else
      Arrays.sort(result);
    
    return result;
  }

  
  public Entity getEntity(String id) {
    
    for (Iterator tags=tag2id2entity.keySet().iterator();tags.hasNext();) {
      Entity result = (Entity)getEntityMap((String)tags.next()).get(id);
      if (result!=null)
        return result;
    }
    
    
    return null;
  }

  
  public Entity getEntity(String tag, String id) {
    
    return (Entity)getEntityMap(tag).get(id);
  }
  
  
  public static Class getEntityType(String tag) {
    Class result =(Class)E2TYPE.get(tag);
    if (result==null)
      throw new IllegalArgumentException("no such type");
    return result;
  }
  
  
  public Entity getFirstEntity(String tag) {
    
    for (Iterator it = allEntities.iterator(); it.hasNext(); ) {
      Entity e = (Entity)it.next();
      if (e.getTag().equals(tag))
        return e;
    }
    
    return null;
  }

  
  public String getNextAvailableID(String entity) {
    
    
    Map id2entity = getEntityMap(entity);
    
    
    
    
    
    
    
    
    
    int id = Options.getInstance().isFillGapsInIDs ? 1 : (id2entity.isEmpty() ? 1 : id2entity.size());
    
    StringBuffer buf = new StringBuffer(maxIDLength);
    
    search: while (true) {
      
      
      
      
      
      buf.setLength(0);
      buf.append(getEntityPrefix(entity));
      buf.append(id);
      
      while (true) {
        if (id2entity.containsKey(buf.toString())) break;
        if (buf.length()>=maxIDLength) break search;
        buf.insert(1, '0');
      } 
      
      
      id++;
    }
    
    
    
    return getEntityPrefix(entity) + id;
  }
  
  
  public boolean hasChanged() {
    return isDirty || !undoHistory.isEmpty();
  }

  
  public void setUnchanged() {
    
    
    undoHistory.clear();
    isDirty = false;
    
    
    if (lock==null)
      return;
    
    
    GedcomListener[] gls = (GedcomListener[])listeners.toArray(new GedcomListener[listeners.size()]);
    for (int l=0;l<gls.length;l++) {
      GedcomListener gl = (GedcomListener)gls[l];
      if (gl instanceof GedcomMetaListener) try {
        ((GedcomMetaListener)gl).gedcomHeaderChanged(this);
      } catch (Throwable t) {
        LOG.log(Level.WARNING, "exception in gedcom listener "+gls[l], t);
      }
    }

    
  }
  
  
  public boolean isWriteLocked() {
    return lock!=null;
  }
  
  
  public void doMuteUnitOfWork(UnitOfWork uow) {
    try {
      doUnitOfWork(uow);
    } catch (GedcomException e) {
      LOG.log(Level.WARNING, "Unexpected gedcom exception", e);
    }
  }
  
  
  public void doUnitOfWork(UnitOfWork uow) throws GedcomException {
    
    PropertyChange.Monitor updater;
    
    
    synchronized (writeSemaphore) {
      
      if (lock!=null)
        throw new GedcomException("Cannot obtain write lock");
      lock = new Lock(uow);

      
      updater = new PropertyChange.Monitor();
      addGedcomListener(updater);

      
      redoHistory.clear();
      
    }

    
    propagateWriteLockAqcuired();
    
    
    Throwable rethrow = null;
    try {
      uow.perform(this);
    } catch (Throwable t) {
      rethrow = t;
    }
    
    synchronized (writeSemaphore) {

      
      if (!lock.undos.isEmpty()) {
        undoHistory.add(lock.undos);
        
        while (undoHistory.size()>Options.getInstance().getNumberOfUndos()) {
          undoHistory.remove(0);
          isDirty = true;
        }
      }
      
      
      propagateWriteLockReleased();
        
      
      lock = null;
      
      
      removeGedcomListener(updater);
    }

    
    if (rethrow!=null) {
      if (rethrow instanceof GedcomException)
        throw (GedcomException)rethrow;
      throw new RuntimeException(rethrow);
    }
  }

  
  public boolean canUndo() {
    return !undoHistory.isEmpty();
  }
  
  
  public void undoUnitOfWork() {
    
    
    if (undoHistory.isEmpty())
      throw new IllegalArgumentException("undo n/a");

    synchronized (writeSemaphore) {
      
      if (lock!=null)
        throw new IllegalStateException("Cannot obtain write lock");
      lock = new Lock();
  
    }
    
    
    propagateWriteLockAqcuired();
    
    
    List todo = (List)undoHistory.remove(undoHistory.size()-1);
    for (int i=todo.size()-1;i>=0;i--) {
      Undo undo = (Undo)todo.remove(i);
      try {
        undo.undo();
      } catch (Throwable t) {
        LOG.log(Level.SEVERE, "Unexpected throwable during undo()", t);
      }
    }
    
    synchronized (writeSemaphore) {

      
      redoHistory.add(lock.undos);
      
      
      propagateWriteLockReleased();
      
      
      lock = null;
    }
    
    
  }
    
  
  public boolean canRedo() {
    return !redoHistory.isEmpty();
  }
  
  
  public void redoUnitOfWork() {
    
    
    if (redoHistory.isEmpty())
      throw new IllegalArgumentException("redo n/a");

    synchronized (writeSemaphore) {
      
      if (lock!=null)
        throw new IllegalStateException("Cannot obtain write lock");
      lock = new Lock();
  
    }
    
    
    propagateWriteLockAqcuired();
    
    
    List todo = (List)redoHistory.remove(redoHistory.size()-1);
    for (int i=todo.size()-1;i>=0;i--) {
      Undo undo = (Undo)todo.remove(i);
      try {
        undo.undo();
      } catch (Throwable t) {
        LOG.log(Level.SEVERE, "Unexpected throwable during undo()", t);
      }
    }
    
    
    synchronized (writeSemaphore) {
      
      
      undoHistory.add(lock.undos);

      
      propagateWriteLockReleased();
      
      
      lock = null;
    }

    
    
  }
  
  
   ReferenceSet getReferenceSet(String tag) {
    
    ReferenceSet result = (ReferenceSet)tags2refsets.get(tag);
    if (result==null) {
      
      result = new ReferenceSet();
      tags2refsets.put(tag, result);
      
      String defaults = Gedcom.resources.getString(tag+".vals",false);
      if (defaults!=null) {
        StringTokenizer tokens = new StringTokenizer(defaults,",");
        while (tokens.hasMoreElements()) result.add(tokens.nextToken().trim(), null);
      }
    }
    
    return result;
  }

  
  public String getName() {
    return origin==null ? null : origin.getName();
  }

    public static String getName(String tag) {
    return getName(tag, false);
  }

  
  public static String getName(String tag, boolean plural) {
    if (plural) {
      String name = resources.getString(tag+".s.name", false);
      if (name!=null)
        return name;
    }
    String name = resources.getString(tag+".name", false);
    return name!=null ? name : tag;
  }

  
  public static String getEntityPrefix(String tag) {
    String result = (String)E2PREFIX.get(tag);
    if (result==null)
      result = "X";
    return result;
  }

  
  public static ImageIcon getImage() {
    return image;
  }

  
  public static ImageIcon getEntityImage(String tag) {
    ImageIcon result = (ImageIcon)E2IMAGE.get(tag);
    if (result==null) {
      result = Grammar.V55.getMeta(new TagPath(tag)).getImage();
      E2IMAGE.put(tag, result);
    }
    return result;
  }
  
  
  public static Resources getResources() {
    return resources;
  }

  
  public String getEncoding() {
    return encoding;
  }
  
  
  public void setEncoding(String set) {
     encoding = set;
  }
  
  
  public String getPlaceFormat() {
    return placeFormat;
  }
  
  
  public void setPlaceFormat(String set) {
    placeFormat = set.trim();
  }
  
  
  public String getLanguage() {
    return language;
  }
  
  
  public void setLanguage(String set) {
    language = set;
  }
  
  
  public void setPassword(String set) {
    if (set==null)
      throw new IllegalArgumentException("Password can't be null");
    password = set;
  }
  
  
  public String getPassword() {
    return password;
  }
  
  
  public boolean hasPassword() {
    return password!=PASSWORD_NOT_SET && password!=PASSWORD_UNKNOWN;
  }
  
  
  public boolean contains(Entity entity) {
    return getEntityMap(entity.getTag()).containsValue(entity);
  }
  
  
  public Locale getLocale() {
    
    
    if (cachedLocale==null) {
      
      
      if (language!=null) {
        
        
        Locale[] locales = Locale.getAvailableLocales();
        for (int i = 0; i < locales.length; i++) {
          if (locales[i].getDisplayLanguage(Locale.ENGLISH).equalsIgnoreCase(language)) {
            cachedLocale = new Locale(locales[i].getLanguage(), Locale.getDefault().getCountry());
            break;
          }
        }
        
      }
      
      
      if (cachedLocale==null)
        cachedLocale = Locale.getDefault();
      
    }
    
    
    return cachedLocale;
  }
  
  
  public Collator getCollator() {
    
    
    if (cachedCollator==null) {
      cachedCollator = Collator.getInstance(getLocale());
      
      
      
      
      
      cachedCollator.setStrength(Collator.PRIMARY);
    }
    
    
    return cachedCollator;
  }
  
  
  public int compareTo(Object other) {
    Gedcom that = (Gedcom)other;
    return getName().compareTo(that.getName());
  };
  
  
  private abstract class Undo {
    abstract void undo() throws GedcomException;
  }
  
  
  private class Lock implements UnitOfWork {
    UnitOfWork uow;
    List undos = new ArrayList();
    
    Lock() {
      this.uow = this;
    }
    
    Lock(UnitOfWork uow) {
      this.uow = uow;
    }
    
    public void perform(Gedcom gedcom) {
      
    }
    
    void addChange(Undo run) {
      undos.add(run);
    }
    
  }
  
} 
