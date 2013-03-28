

package edu.rice.cs.drjava.ui;

import edu.rice.cs.drjava.*;
import edu.rice.cs.drjava.config.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;


public class KeyBindingManager {
  
  public static final KeyBindingManager ONLY = new KeyBindingManager();

  private KeyBindingManager() {   }
  
  
  private HashMap<KeyStroke, KeyStrokeData> _keyToDataMap = new HashMap<KeyStroke, KeyStrokeData>();
  private HashMap<Action, KeyStrokeData> _actionToDataMap = new HashMap<Action, KeyStrokeData>();

  private MainFrame _mainFrame = null;

  
  private ActionMap _actionMap;

  
  private boolean _shouldCheckConflict = true;

  public void setMainFrame (MainFrame mainFrame) { _mainFrame = mainFrame; }

  
  public void setActionMap (ActionMap actionMap) { _actionMap = actionMap; }

  public void setShouldCheckConflict (boolean bool) { _shouldCheckConflict = bool;  }
  
  public Collection<KeyStrokeData> getKeyStrokeData() { return _actionToDataMap.values(); }

  public void put(VectorOption<KeyStroke> vkso, Action a, JMenuItem jmi, String name)  {
    Vector<KeyStroke> keys = DrJava.getConfig().getSetting(vkso);
    Vector<KeyStroke> retained = new Vector<KeyStroke>();
    KeyStrokeData ksd = new KeyStrokeData(keys, a, jmi, name, vkso);
    _actionToDataMap.put(a, ksd);
    for(KeyStroke ks: keys) {
      if (shouldUpdate(ks, a)) {
        retained.add(ks);
        _keyToDataMap.put(ks, ksd);
      }
    }
    DrJava.getConfig().addOptionListener(vkso, new VectorKeyStrokeOptionListener(jmi, a, retained));
    if (retained.size() != keys.size()) {
      
      DrJava.getConfig().setSetting(vkso,retained);
    }
  }

  
  public Action get(KeyStroke ks) {
    KeyStrokeData ksd = _keyToDataMap.get(ks);
    if (ksd == null) return null;
    return ksd.getAction();
  }

  public String getName(KeyStroke ks) {
    KeyStrokeData ksd = _keyToDataMap.get(ks);
    if (ksd == null) return null;
    return ksd.getName();
  }

  public String getName(Action a) {
    KeyStrokeData ksd = _actionToDataMap.get(a);
    if (ksd == null) return null;
    return ksd.getName();
  }
  
  
  private boolean shouldUpdate(KeyStroke ks, Action a) {
    if (ks == KeyStrokeOption.NULL_KEYSTROKE) {
      
      return false;
    }

    if (!_keyToDataMap.containsKey(ks) ) {
      
      return true;
    }
    else if (_keyToDataMap.get(ks).getAction().equals(a)) {
      
      return false;
    }
    else { 
      if (_shouldCheckConflict) {
        KeyStrokeOption opt = new KeyStrokeOption(null,null);
        KeyStrokeData conflictKSD = _keyToDataMap.get(ks);
        String key = opt.format(ks);
        KeyStrokeData newKSD = _actionToDataMap.get(a);
        String text = "\""+ key +"\"" + " is already assigned to \"" + conflictKSD.getName() +
          "\".\nWould you like to assign \"" + key + "\" to \"" + newKSD.getName() + "\"?";
        int rc = JOptionPane.showConfirmDialog(_mainFrame, text, "DrJava", JOptionPane.YES_NO_OPTION);

        switch (rc) {
          case JOptionPane.YES_OPTION:
            removeExistingKeyStroke(ks);
            return true;
          case JOptionPane.NO_OPTION:
          case JOptionPane.CLOSED_OPTION:
          case JOptionPane.CANCEL_OPTION:
            return false;
          default:
            throw new RuntimeException("Invalid rc: " + rc);
        }
      }
      else return true;
    }
  }
  
  private void removeExistingKeyStroke(KeyStroke ks) {
    
    if (_keyToDataMap.containsKey(ks) && _shouldCheckConflict) {
      
      KeyStrokeData conflictKSD = _keyToDataMap.get(ks);
      
      Set<KeyStroke> conflictKeys = new LinkedHashSet<KeyStroke>(conflictKSD.getKeyStrokes());
      conflictKeys.remove(ks);
      conflictKSD.setKeyStrokes(new Vector<KeyStroke>(conflictKeys));
      updateMenuItem(conflictKSD);
      _keyToDataMap.remove(ks);
      DrJava.getConfig().setSetting(conflictKSD.getOption(), conflictKSD.getKeyStrokes());
    }
  }
  
  private void updateMenuItem(KeyStrokeData data) {
    JMenuItem jmi = data.getJMenuItem();
    
    
    if (jmi != null) {
      Vector<KeyStroke> keys = data.getKeyStrokes();
      if (keys.size() > 0) {
        
        jmi.setAccelerator(keys.get(0));
      }
      else {
        
        jmi.setAccelerator(null);
      }
    }
  }

  
  public class VectorKeyStrokeOptionListener implements OptionListener<Vector<KeyStroke>> {
    protected JMenuItem _jmi; 
    protected Action _a; 
    protected Set<KeyStroke> _oldKeys; 

    public VectorKeyStrokeOptionListener(JMenuItem jmi, Action a, Vector<KeyStroke> keys) {
      _jmi = jmi;
      _a = a;
      _oldKeys = new LinkedHashSet<KeyStroke>(keys);
    }

    public VectorKeyStrokeOptionListener(Action a, Vector<KeyStroke> keys) {
      this(null, a, keys);
    }

    public void optionChanged(OptionEvent<Vector<KeyStroke>> oce) {
      Set<KeyStroke> newKeys = new LinkedHashSet<KeyStroke>(oce.value);
      Set<KeyStroke> removed = new LinkedHashSet<KeyStroke>(_oldKeys);
      removed.removeAll(newKeys); 
      Set<KeyStroke> added = new LinkedHashSet<KeyStroke>(newKeys);
      added.removeAll(_oldKeys); 
      Set<KeyStroke> retained = new LinkedHashSet<KeyStroke>(_oldKeys);
      retained.retainAll(newKeys); 
      boolean update = false;
      KeyStrokeData data = _actionToDataMap.get(_a);
      if (data == null) {
        
        return;
      }
      
      
      for(KeyStroke ks: removed) {
        
        
        if (data.equals(_keyToDataMap.get(ks))) {
          _keyToDataMap.remove(ks);
          update = true;
        }
      }
      
      
      for(KeyStroke ks: added) {
        if (shouldUpdate(ks, _a)) {          
          _keyToDataMap.put(ks,data);
          retained.add(ks);
          update = true;
        }
      }
      
      if (update) {        
        Vector<KeyStroke> v = new Vector<KeyStroke>(retained);
        data.setKeyStrokes(v);
        updateMenuItem(data);
        _oldKeys = retained;
      }
    }
  }

  public static class KeyStrokeData {
    private Vector<KeyStroke> _ks;
    private Action _a;
    private JMenuItem _jmi;
    private String _name;
    private VectorOption<KeyStroke> _vkso;

    public KeyStrokeData(Vector<KeyStroke> ks, Action a, JMenuItem jmi, String name, VectorOption<KeyStroke> vkso) {
      _ks = new Vector<KeyStroke>(ks);
      _a = a;
      _jmi = jmi;
      _name = name;
      _vkso = vkso;
    }

    public Vector<KeyStroke> getKeyStrokes() { return _ks; }
    public Action getAction() { return _a; }
    public JMenuItem getJMenuItem() { return _jmi; }
    public String getName() { return _name; }
    public VectorOption<KeyStroke> getOption() { return _vkso; }
    
    public void setKeyStrokes(Vector<KeyStroke> ks) { _ks = new Vector<KeyStroke>(ks); }
    public void setAction(Action a) { _a = a; }
    public void setJMenuItem(JMenuItem jmi) { _jmi = jmi; }
    public void setName(String name) { _name = name; }
    public void setOption(VectorOption<KeyStroke> vkso) { _vkso = vkso; }
  }
}
