
package genj.gedcom;

import java.util.ArrayList;
import java.util.List;


public class GedcomDirectory {
  
  private static GedcomDirectory instance = new GedcomDirectory();
  
  private List<Gedcom> gedcoms = new ArrayList<Gedcom>();
  private List<Listener> listeners = new ArrayList<Listener>();
  
  
  private GedcomDirectory() {
  }

  
  public static GedcomDirectory getInstance() {
    return instance;
  }
  
  
  public void registerGedcom(Gedcom gedcom) {
    gedcoms.add(gedcom);
    List<Listener> ls = new ArrayList<Listener>(listeners);
    for (Listener listener : ls) 
      listener.gedcomRegistered(gedcoms.size()-1, gedcom);
  }
  
  
  public void unregisterGedcom(Gedcom gedcom) {
    int i = gedcoms.indexOf(gedcom);
    gedcoms.remove(gedcom);
    List<Listener> ls = new ArrayList<Listener>(listeners);
    for (Listener listener : ls) 
      listener.gedcomUnregistered(i, gedcom);
  }

  
  public List<Gedcom> getGedcoms() {
    return new ArrayList<Gedcom>(gedcoms);
  }

  
  public Gedcom getGedcom(String name) {
    for (Gedcom g : getGedcoms()) {
      if (g.getName().equals(name))
        return g;
    }
    return null;
  }

  
  
  public void addListener(Listener listener) {
    listeners.add(listener);
  }
  public void removeListener(Listener listener) {
    listeners.remove(listener);
  }
  public interface Listener {
    public void gedcomRegistered(int num, Gedcom gedcom);
    public void gedcomUnregistered(int num, Gedcom gedcom);
  }
}
