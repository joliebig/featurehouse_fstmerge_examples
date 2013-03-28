

package edu.rice.cs.drjava.ui;

import edu.rice.cs.drjava.*;
import edu.rice.cs.drjava.config.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;


public class KeyBindingManager {
  
  public static final KeyBindingManager Singleton = new KeyBindingManager();

  private KeyBindingManager() {   }
  
  
  
  private Hashtable<KeyStroke, KeyStrokeData> _keyToDataMap =
    new Hashtable<KeyStroke, KeyStrokeData>();
  private Hashtable<Action, KeyStrokeData> _actionToDataMap =
    new Hashtable<Action, KeyStrokeData>();

  private MainFrame _mainFrame = null;

  
  private ActionMap _actionMap;

  
  private boolean _shouldCheckConflict = true;

  public void setMainFrame (MainFrame mainFrame) {
    _mainFrame = mainFrame;
  }

  
  public void setActionMap (ActionMap actionMap) {
    _actionMap = actionMap;
  }

  public void setShouldCheckConflict (boolean bool) {
    _shouldCheckConflict = bool;
  }
  public Enumeration getKeyStrokeData() { 
      return _actionToDataMap.elements();
  }

  public void put(Option<KeyStroke> kso, Action a, JMenuItem jmi, String name)  {
    KeyStroke ks = DrJava.getConfig().getSetting(kso);
    KeyStrokeData ksd = new KeyStrokeData(ks, a, jmi, name, kso);
    _keyToDataMap.put(ks, ksd);
    _actionToDataMap.put(a, ksd);

    
    if (kso != null) {
      DrJava.getConfig().addOptionListener(kso, new KeyStrokeOptionListener(jmi, a, ks));
    }
  }

  
  public Action get(KeyStroke ks) {
    KeyStrokeData ksd = _keyToDataMap.get(ks);
    if (ksd == null) {
      return null;
    }
    return ksd.getAction();
  }

  public String getName(KeyStroke ks) {
    KeyStrokeData ksd = _keyToDataMap.get(ks);
    if (ksd == null) {
      return null;
    }
    return ksd.getName();
  }

  public String getName(Action a) {
    KeyStrokeData ksd = _actionToDataMap.get(a);
    if (ksd == null) {
      return null;
    }
    return ksd.getName();
  }

  
  public void addShiftAction(Option<KeyStroke> opt, String shiftS) {
    Action shiftA = _actionMap.get(shiftS);
    addShiftAction(opt, shiftA);
  }

  
  public void addShiftAction(Option<KeyStroke> opt, Action shiftA) {
    KeyStroke ks = DrJava.getConfig().getSetting(opt);

    KeyStrokeData normal = _keyToDataMap.get(ks);
    normal.setShiftAction(shiftA);

    KeyStrokeData ksd = new KeyStrokeData(addShiftModifier(ks), shiftA, null,
                                          "Selection " + normal.getName(), null);

    _keyToDataMap.put(addShiftModifier(ks), ksd);
    _actionToDataMap.put(shiftA, ksd);
  }

  
  public KeyStroke addShiftModifier(KeyStroke k) {
    return KeyStroke.getKeyStroke(k.getKeyCode(),
                                  k.getModifiers() | InputEvent.SHIFT_MASK,
                                  k.isOnKeyRelease() );
  }

  
  
  private boolean shouldUpdate(KeyStroke ks, Action a) {
    if (ks == KeyStrokeOption.NULL_KEYSTROKE) {
      
      return true;
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
        int rc = JOptionPane.showConfirmDialog(_mainFrame,
                                               text,
                                               "DrJava",
                                               JOptionPane.YES_NO_CANCEL_OPTION);

        switch (rc) {
          case JOptionPane.YES_OPTION:
            return true;
          case JOptionPane.NO_OPTION:
            return false;
          case JOptionPane.CLOSED_OPTION:
            return false;
          case JOptionPane.CANCEL_OPTION:
            return false;
          default:
            throw new RuntimeException("Invalid rc: " + rc);
        }
      }
      else {
        return true;
      }
    }
  }

  
  public class KeyStrokeOptionListener implements OptionListener<KeyStroke> {
    protected JMenuItem _jmi; 
    protected Action _a; 
    protected KeyStroke _ks; 

    public KeyStrokeOptionListener(JMenuItem jmi, Action a, KeyStroke ks) {
      _jmi = jmi;
      _a = a;
      _ks = ks;
    }

    public KeyStrokeOptionListener(Action a, KeyStroke ks) {
      _jmi = null;
      _a = a;
      _ks = ks;
    }

    private void _updateMenuItem (KeyStrokeData data) {
      JMenuItem jmi = data.getJMenuItem();

      
      
      if (jmi != null) {
        KeyStroke ks = data.getKeyStroke();
        if (ks != KeyStrokeOption.NULL_KEYSTROKE) {
          
          
          jmi.setAccelerator(ks);
        }
        else {
          
          jmi.setAccelerator(null);
        }
      }
    }

    public void optionChanged(OptionEvent<KeyStroke> oce) {
      if (shouldUpdate(oce.value, _a))
      {
        KeyStrokeData data = _actionToDataMap.get(_a);
        if (data == null) {
          
          return;
        }

        
        
        
        
        if (data.equals(_keyToDataMap.get(_ks))) {
          _keyToDataMap.remove(_ks);
        }

        
        if (_keyToDataMap.containsKey(oce.value) && _shouldCheckConflict) {
          
          KeyStrokeData conflictKSD = _keyToDataMap.get(oce.value);
          conflictKSD.setKeyStroke(KeyStrokeOption.NULL_KEYSTROKE);
          _updateMenuItem(conflictKSD);
          _keyToDataMap.remove(oce.value);
          DrJava.getConfig().setSetting(conflictKSD.getOption(), KeyStrokeOption.NULL_KEYSTROKE);
        }

        if (oce.value != KeyStrokeOption.NULL_KEYSTROKE) {
          _keyToDataMap.put(oce.value,data);
        }
        data.setKeyStroke(oce.value);
        _updateMenuItem(data);

        
        Action shiftAction = data.getShiftAction();
        if (shiftAction != null) {
          
          KeyStrokeData shiftKSD = _actionToDataMap.get(shiftAction);
          _keyToDataMap.remove(shiftKSD.getKeyStroke());
          shiftKSD.setKeyStroke(addShiftModifier(oce.value));
          _keyToDataMap.put(shiftKSD.getKeyStroke(), shiftKSD);
          
        }

        _ks = oce.value;
      }
      else if (_ks != oce.value) {
        DrJava.getConfig().setSetting(oce.option, _ks);
      }
    }
  }

  public static class KeyStrokeData {
    private KeyStroke _ks;
    private Action _a;
    private JMenuItem _jmi;
    private String _name;
    private Option<KeyStroke> _kso;
    private Action _shiftA;

    public KeyStrokeData(KeyStroke ks, Action a, JMenuItem jmi, String name,
                         Option<KeyStroke> kso) {
      _ks = ks;
      _a = a;
      _jmi = jmi;
      _name = name;
      _kso = kso;
      _shiftA = null;
    }

    public KeyStroke getKeyStroke() {
      return _ks;
    }

    public Action getAction() {
      return _a;
    }

    public JMenuItem getJMenuItem() {
      return _jmi;
    }

    public String getName() {
      return _name;
    }

    public Option<KeyStroke> getOption() {
      return _kso;
    }

    public Action getShiftAction() {
      return _shiftA;
    }

    public void setKeyStroke(KeyStroke ks) {
      _ks = ks;
    }

    public void setAction(Action a) {
      _a = a;
    }

    public void setJMenuItem(JMenuItem jmi) {
      _jmi = jmi;
    }

    public void setName(String name) {
      _name = name;
    }

    public void setOption(Option<KeyStroke> kso) {
      _kso = kso;
    }

    public void setShiftAction(Action shiftA) {
      _shiftA = shiftA;
    }
  }
}
