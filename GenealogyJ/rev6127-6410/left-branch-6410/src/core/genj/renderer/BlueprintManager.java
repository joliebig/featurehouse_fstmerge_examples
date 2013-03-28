
package genj.renderer;

import genj.gedcom.Gedcom;
import genj.util.EnvironmentChecker;
import genj.util.Registry;
import genj.util.Resources;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BlueprintManager {
  
  private final static String SUFFIX = ".html";
  private final static Registry REGISTRY = Registry.get(BlueprintManager.class);
  private final static Resources RESOURCES = Resources.get(BlueprintManager.class);
  
  public final static String TXT_BLUEPRINT = RESOURCES.getString("blueprint");
  
  private final static String[][] DEFAULTS = {
   { "INDI", "default", "complete", "verbose", "colorful", "professional", "simple", "pastel", "light" },
   { "FAM", "default", "complete", "simple", "pastel", "light" },
   { "OBJE", "default", "complete", "55" },
   { "NOTE", "default", "complete" },
   { "SOUR", "default", "complete" },
   { "SUBM", "default", "complete" },
   { "REPO", "default", "complete" }
  };

   final static Logger LOG = Logger.getLogger("genj.renderer");

  
  private Map<String, List<Blueprint>> tag2blueprints = new HashMap<String, List<Blueprint>>();

  
  private static BlueprintManager instance;
  
  
  public static BlueprintManager getInstance() {
    if (instance==null)
      instance = new BlueprintManager();
    return instance;
  }
  
    private BlueprintManager() {
    
    
    for (int t=0;t<DEFAULTS.length;t++) {
      
      String[] defaults = DEFAULTS[t];
      
      String tag = defaults[0];

      for (int i=1;i<defaults.length;i++) {
        String key = defaults[i];
        String name = RESOURCES.getString("blueprints."+key, false);
        if (name==null)
          name = key;
        try {
          addBlueprint(loadBlueprint(
              getClass().getResourceAsStream("blueprints/"+tag+"/"+key+SUFFIX),
              tag,
              name,
              true
          ));
        } catch (Throwable e) {
          LOG.warning("can't read pre-defined blueprint "+tag+"/"+key);
        }
      }
      
    }
    
    
    loadBlueprints();
    
    
  }
  
  
  private File getBlueprintDirectory() {
    return new File(EnvironmentChecker.getProperty("user.home.genj/blueprints", "?", "Looking for blueprints"));
  }
  
  
  private File getBlueprintFile(Blueprint blueprint) throws IOException {
    
    if (blueprint.getName().indexOf('\"')>=0)
      throw new IOException("Quotes are not allowed in blueprint names");
    return new File(getBlueprintDirectory(), "/"+blueprint.getTag()+"/"+blueprint.getName()+SUFFIX).getCanonicalFile(); 
  }
  
  
   void saveBlueprint(Blueprint blueprint) throws IOException {
    
    
    if (!blueprint.isDirty())
      return;
    
    
    File file = getBlueprintFile(blueprint); 
    File parent = file.getParentFile();
    if ( (!parent.exists()&&!parent.mkdirs()) ||!parent.isDirectory() )
      throw new IOException("Cannot create folder for blueprint "+blueprint.getName());
    
    readwrite(new StringReader(blueprint.getHTML()), new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
    
    blueprint.clearDirty();
    
    LOG.log(Level.INFO, "saved blueprint "+file);
    
    
  }
  
  
  private void loadBlueprints() {
    
    File dir = getBlueprintDirectory();
    try {
      
      
      if (!dir.isDirectory()||!dir.exists())
        return;
  
      
      for (int i=0; i<Gedcom.ENTITIES.length; i++) 
        loadBlueprints(dir, Gedcom.ENTITIES[i]);
    
    } catch (Throwable t) {
      LOG.log(Level.WARNING, "unexpected throwable loading blueprints from "+dir, t);
    }
  }
  
  
  private void loadBlueprints(File dir, String tag) throws IOException {
    
    
    dir = new File(dir, tag);
    if (!dir.isDirectory()||!dir.exists())
      return;
    
    
    File[] files = dir.listFiles();
    for (int b=0;b<files.length;b++) {
      
      
      File file = files[b];
      String name = file.getName();
      if (!name.endsWith(SUFFIX)||file.isDirectory())
        continue;
      name = name.substring(0, name.length()-SUFFIX.length());
      
      Blueprint blueprint = loadBlueprint(new FileInputStream(file), tag, name, false);
      blueprint.clearDirty();
      addBlueprint(blueprint);
      
    }
    
    
  }
  
  
  private Blueprint loadBlueprint(InputStream in, String tag, String name, boolean readOnly) throws IOException {
    
    StringWriter html = new StringWriter(512);
    readwrite(new InputStreamReader(in, "UTF8"), html);
    in.close();
    
    return new Blueprint(tag, name, html.toString(), readOnly);
  }
  
  
  private void readwrite(Reader in, Writer out) throws IOException {
    
    BufferedReader bin = new BufferedReader(in);
    BufferedWriter bout = new BufferedWriter(out);
    while (true) {
      String line = bin.readLine();
      if (line==null) break;
      bout.write(line);
      bout.newLine();
    }
    bin.close();
    bout.close();
    
  }
  
    public Blueprint getBlueprint(String tag, String name) {
    
    if (name.length()==0)
      name = "Default";
    
    List<Blueprint> bps = getBlueprints(tag);
    for (int i=0; i<bps.size(); i++) {
      Blueprint bp = (Blueprint)bps.get(i);
      
      if (bp.getName().equals(name)) 
        return bp;   	
    }
    
    return (Blueprint)bps.get(0);
  }
  
    public List<Blueprint> getBlueprints(String tag) {
    return Collections.unmodifiableList(getBlueprintsInternal(tag));
  }
  
  private List<Blueprint> getBlueprintsInternal(String tag) {
    List<Blueprint> result = tag2blueprints.get(tag);
    if (result==null) {
      result = new ArrayList<Blueprint>();
      tag2blueprints.put(tag, result);
    }
    return result;
  }
  
    public Blueprint addBlueprint(Blueprint blueprint) throws IOException {
    
    
    if (!blueprint.isReadOnly())
      getBlueprintFile(blueprint);
    
    
    List<Blueprint> blueprints = getBlueprintsInternal(blueprint.getTag());
    for (ListIterator<Blueprint> it=blueprints.listIterator(); it.hasNext(); ) {
      Blueprint other = (Blueprint)it.next();
      
      if (other.getName().equalsIgnoreCase(blueprint.getName())) {
        
        if (other.isReadOnly())
          throw new IOException("Can't override read-only blueprint");
        
        it.remove();
        break;
      }
    }
    
    
    if (!blueprint.isReadOnly())
      saveBlueprint(blueprint);
    
    
    blueprints.add(blueprint);
    
    
    return blueprint;
  }
  
    public void delBlueprint(Blueprint blueprint) throws IOException {
    
    if (blueprint.isReadOnly()) 
      throw new IOException("Can't delete read-only Blueprint");
    
    if (!blueprint.isReadOnly()) {
      if (!getBlueprintFile(blueprint).delete()) 
        throw new IOException("Couldn't delete blueprint "+blueprint);
    }
    
    getBlueprintsInternal(blueprint.getTag()).remove(blueprint);
    
  }
  
} 
