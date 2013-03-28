
package genj.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import junit.framework.TestCase;


public class ResourcesTest extends TestCase {
  
  private final static File dir = new File("./language");
  
  
  public void testLoad() throws IOException {
    
    Resources r = new Resources(getClass().getResourceAsStream("resources.properties"));
    
    String[] keys =   { "one",  "two",  "multi", "newline", "plus",    "and" };
    String[] values = { "true", "also", "1 2 3", "1\n2\n3", "1\n2\n3", "123" };
    
    assertTrue(Arrays.equals(r.getKeys().toArray(new String[keys.length]), keys));
    for (int i=0;i<keys.length;i++) 
      assertEquals(keys[i], values[i], r.getString(keys[i]));
    
  }

  
  public void testLoadLiteral() throws IOException {
    
    Resources r = new Resources();
    
    r.load(getClass().getResourceAsStream("Literal.java.properties"), true);

    String[] keys =   { "one",  "two",  "multi", "newline", "plus",    "and" };
    String[] values = { "true", "also", "1 2 3", "1\n2\n3", "1\n2\n3", "123" };
    
    assertTrue(Arrays.equals(r.getKeys().toArray(new String[keys.length]), keys));
    for (int i=0;i<keys.length;i++) 
      assertEquals(keys[i], values[i], r.getString(keys[i]));
    
    
  }

  
  
  public void testDE() throws IOException {
    testTranslated("de");
  }
  
  
  public void testFR() throws IOException {
    testTranslated("fr");
  }
  
  private void testTranslated(String lang) throws IOException {
    assertEquals("differences in translation between en and "+lang, 0, diff("en", lang).size());
  }
  
  
  public static void main(String[] args) {
    
    
    if (args.length==1 && args[0].equals("all")) {
      File[] translations = dir.listFiles();
      for (int i=0;i<translations.length;i++) {
        if (!translations[i].isDirectory() ||translations[i].getName().startsWith(".") || translations[i].getName().equals("en")) 
          continue;
        diff(translations[i].getName());
      }
      return;
    }
    
    String translation = Locale.getDefault().getLanguage();
    if (translation.equals("en")) {
      System.out.println("Set language to diff with -Duser.language=xx");
      return;
    }

    diff(translation);
  }
    
  private static void diff(String translation) {
    
    System.out.println("Diffing en against "+translation);
    try  {
      List<String> diffs = new ResourcesTest().diff("en", translation);
      if (diffs.isEmpty())
        System.out.println("No differences found - Good Job!");
      else {
        System.out.println(diffs.size()+" differences found:");
        for (String s : diffs)
          System.out.println(s);
      }      
    } catch (IOException e) {
      System.out.println("IOException during diff: "+e.getMessage());
    }
  }
  
  
  private List<String> diff(String original, String translation) throws IOException {
    return diffResources(loadResources(original), loadResources(translation));
  }
  
  
  private List<String> diffResources(Map<String,Resources> originals, Map<String,Resources> translations) {
    
    List<String> diffs = new ArrayList<String>();

    
    for (String pckg : originals.keySet()) {
      
      Resources original = (Resources)originals.get(pckg);
      Resources translation = (Resources)translations.get(pckg);
      if (translation==null)
        diffs.add(pckg+",*,not translated");
      else
        diffResource(pckg, original, translation, diffs);
    }
    
    
    return diffs;
  }
  
  private final static Pattern PATTERN_IGNORE = Pattern.compile(".*[A-Z]{2}.*");
  
  private void diffResource(String pckg, Resources original, Resources translation, List<String> diffs) {
    
    for (String key : original.getKeys()) {
      
      String val1 = (String)original.getString(key);
      String val2 = (String)translation.getString(key, false);
      
      if (PATTERN_IGNORE.matcher(key).matches())
        continue;
      
      if (val2==null) {
        diffs.add(pckg+","+key+",not translated");
      } else {
        try {
          int fs1 = Resources.getMessageFormat(val1).getFormats().length;
          int fs2 = Resources.getMessageFormat(val2).getFormats().length;
          if (fs1!=fs2) {
            diffs.add(pckg+","+key+",wrong # of {n}s");
          }
        } catch (IllegalArgumentException e) {
          
        }
      }
      
    }
    
    for (String key : translation.getKeys()) {
      
      if (!key.toLowerCase().equals(key))
        continue;
      
      if (!original.contains(key)) {
        diffs.add(pckg+","+key+",translated but not in original");
      }
    }
    
  }
  
  
  private Map<String,Resources> loadResources(String language)  throws IOException {
    return loadResources(new File(dir, language),  "", new TreeMap<String,Resources>());
  }
  
  private Map<String,Resources> loadResources(File dir, String pckg, Map<String,Resources> result) throws IOException {
    
    
    File[] resources = dir.listFiles();
    for (int i=0;i<resources.length;i++) {
      File resource = resources[i];
      if (resource.isDirectory())
        loadResources(resource, pckg+"/"+resource.getName(), result);
      else if (resource.getName().endsWith(".properties"))
        result.put(pckg, new Resources(new FileInputStream(resource)));
    }
    
    
    return result;
  }
  
}
