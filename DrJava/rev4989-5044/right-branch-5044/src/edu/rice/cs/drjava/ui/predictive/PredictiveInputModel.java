

package edu.rice.cs.drjava.ui.predictive;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;


public class PredictiveInputModel<T extends Comparable<? super T>> {
  
  
  public static interface MatchingStrategy<X extends Comparable<? super X>> {
    
    
    public boolean isMatch(X item, PredictiveInputModel<X> pim);
    
    
    public boolean isPerfectMatch(X item, PredictiveInputModel<X> pim);
    
    
    public boolean equivalent(X item1, X item2, PredictiveInputModel<X> pim);
    
    
    public int compare(X item1, X item2, PredictiveInputModel<X> pim);

    
    public X getLongestMatch(X item, List<X> items, PredictiveInputModel<X> pim);
    
    
    public String getSharedMaskExtension(List<X> items, PredictiveInputModel<X> pim);
       
    
    public String getExtendedSharedMask(List<X> items, PredictiveInputModel<X> pim);
  
    
    public String force(X item, String mask);
  }
  
  
  public static class PrefixStrategy<X extends Comparable<? super X>> implements MatchingStrategy<X> {
    public String toString() { return "Prefix"; }
    public boolean isMatch(X item, PredictiveInputModel<X> pim) {
      String a = (pim._ignoreCase) ? (item.toString().toLowerCase()) : (item.toString());
      String b = (pim._ignoreCase) ? (pim._mask.toLowerCase()) : (pim._mask);
      return a.startsWith(b);
    }
    public boolean isPerfectMatch(X item, PredictiveInputModel<X> pim) {
      String a = (pim._ignoreCase) ? (item.toString().toLowerCase()) : (item.toString());
      String b = (pim._ignoreCase) ? (pim._mask.toLowerCase()) : (pim._mask);
      return a.equals(b);
    }
    public boolean equivalent(X item1, X item2, PredictiveInputModel<X> pim) {
      String a = (pim._ignoreCase) ? (item1.toString().toLowerCase()) : (item1.toString());
      String b = (pim._ignoreCase) ? (item2.toString().toLowerCase()) : (item2.toString());
      return a.equals(b);
    }
    public int compare(X item1, X item2, PredictiveInputModel<X> pim) {
      String a = (pim._ignoreCase) ? (item1.toString().toLowerCase()) : (item1.toString());
      String b = (pim._ignoreCase) ? (item2.toString().toLowerCase()) : (item2.toString());
      return a.compareTo(b);
    }
    public X getLongestMatch(X item, List<X> items, PredictiveInputModel<X> pim) {
      X longestMatch = null;
      int matchLength = -1;
      for(X i: items) {
        String s = (pim._ignoreCase) ? (i.toString().toLowerCase()) : (i.toString());
        String t = (pim._ignoreCase) ? (item.toString().toLowerCase()) : (item.toString());
        int ml = 0;
        while((s.length() > ml) && (t.length() > ml) && (s.charAt(ml) == t.charAt(ml))) {
          ++ml;
        }
        if (ml>matchLength) {
          matchLength = ml;
          longestMatch = i;
        }
      }
      return longestMatch;
    }
    public String getSharedMaskExtension(List<X> items, PredictiveInputModel<X> pim) {
      StringBuilder res = new StringBuilder();
      String ext = "";
      if (items.size() == 0) {
        return ext;
      }
      boolean allMatching = true;
      int len = pim._mask.length();
      while((allMatching) && (pim._mask.length() + ext.length() < items.get(0).toString().length())) {
        char origCh = items.get(0).toString().charAt(pim._mask.length() + ext.length());
        char ch = (pim._ignoreCase) ? (Character.toLowerCase(origCh)) : (origCh);
        allMatching = true;
        for (X i: items) {
          String a = (pim._ignoreCase) ? (i.toString().toLowerCase()) : (i.toString());
          if (a.charAt(len) != ch) {
            allMatching = false;
            break;
          }
        }
        if (allMatching) {
          ext = ext + ch;
          res.append(origCh);
          ++len;
        }
      }
      return res.toString();
    }
    public String getExtendedSharedMask(List<X> items, PredictiveInputModel<X> pim) {
      return pim._mask + getSharedMaskExtension(items, pim);
    }
    public String force(X item, String mask) { return item.toString(); }
  };
  
  
  public static class FragmentStrategy<X extends Comparable<? super X>> implements MatchingStrategy<X> {
    public String toString() { return "Fragments"; }
    public boolean isMatch(X item, PredictiveInputModel<X> pim) {
      String a = (pim._ignoreCase) ? (item.toString().toLowerCase()) : (item.toString());
      String b = (pim._ignoreCase) ? (pim._mask.toLowerCase()) : (pim._mask);

      java.util.StringTokenizer tok = new java.util.StringTokenizer(b);
      while(tok.hasMoreTokens()) {
        if (a.indexOf(tok.nextToken()) < 0) return false;
      }
      return true;
    }
    public boolean isPerfectMatch(X item, PredictiveInputModel<X> pim) {
      String a = (pim._ignoreCase) ? (item.toString().toLowerCase()) : (item.toString());
      String b = (pim._ignoreCase) ? (pim._mask.toLowerCase()) : (pim._mask);
      return a.equals(b);
    }
    public boolean equivalent(X item1, X item2, PredictiveInputModel<X> pim) {
      String a = (pim._ignoreCase)?(item1.toString().toLowerCase()):(item1.toString());
      String b = (pim._ignoreCase)?(item2.toString().toLowerCase()):(item2.toString());
      return a.equals(b);
    }
    public int compare(X item1, X item2, PredictiveInputModel<X> pim) {
      String a = (pim._ignoreCase)?(item1.toString().toLowerCase()):(item1.toString());
      String b = (pim._ignoreCase)?(item2.toString().toLowerCase()):(item2.toString());
      return a.compareTo(b);
    }
    public X getLongestMatch(X item, List<X> items, PredictiveInputModel<X> pim) {
      if (items.size() > 0)  return items.get(0);
      else return null;
    }
    public String getSharedMaskExtension(List<X> items, PredictiveInputModel<X> pim) {
      return ""; 
    }
    public String getExtendedSharedMask(List<X> items, PredictiveInputModel<X> pim) {
      return pim._mask;
    }
    public String force(X item, String mask) { return item.toString(); }
  };
  
  
  public static class RegExStrategy<X extends Comparable<? super X>> implements MatchingStrategy<X> {
    public String toString() { return "RegEx"; }
    public boolean isMatch(X item, PredictiveInputModel<X> pim) {
      String a = item.toString();

      try {
        Pattern p = Pattern.compile(pim._mask,
                                    (pim._ignoreCase)?(Pattern.CASE_INSENSITIVE):(0));
        Matcher m = p.matcher(a);
        return m.matches();
      }
      catch (PatternSyntaxException e) {
        return false;
      }
    }
    public boolean isPerfectMatch(X item, PredictiveInputModel<X> pim) {
      String a = (pim._ignoreCase)?(item.toString().toLowerCase()):(item.toString());
      String b = (pim._ignoreCase)?(pim._mask.toLowerCase()):(pim._mask);
      return a.equals(b);
    }
    public boolean equivalent(X item1, X item2, PredictiveInputModel<X> pim) {
      String a = (pim._ignoreCase)?(item1.toString().toLowerCase()):(item1.toString());
      String b = (pim._ignoreCase)?(item2.toString().toLowerCase()):(item2.toString());
      return a.equals(b);
    }
    public int compare(X item1, X item2, PredictiveInputModel<X> pim) {
      String a = (pim._ignoreCase)?(item1.toString().toLowerCase()):(item1.toString());
      String b = (pim._ignoreCase)?(item2.toString().toLowerCase()):(item2.toString());
      return a.compareTo(b);
    }
    public X getLongestMatch(X item, List<X> items, PredictiveInputModel<X> pim) {
      if (items.size() > 0)  return items.get(0); 
      else return null;
    }
    public String getSharedMaskExtension(List<X> items, PredictiveInputModel<X> pim) {
      return ""; 
    }
    public String getExtendedSharedMask(List<X> items, PredictiveInputModel<X> pim) {
      return pim._mask;
    }
    public String force(X item, String mask) { return item.toString(); }
  };
  
  
  public static class PrefixLineNumStrategy<X extends Comparable<? super X>> implements MatchingStrategy<X> {
    public String toString() { return "Prefix"; }
    public boolean isMatch(X item, PredictiveInputModel<X> pim) {
      int posB = pim._mask.lastIndexOf(':');
      if (posB < 0) { posB = pim._mask.length(); }
      String mask = pim._mask.substring(0,posB);
      
      String a = (pim._ignoreCase)?(item.toString().toLowerCase()):(item.toString());
      String b = (pim._ignoreCase)?(mask.toLowerCase()):(mask);
      return a.startsWith(b);
    }
    public boolean isPerfectMatch(X item, PredictiveInputModel<X> pim) {
      int posB = pim._mask.lastIndexOf(':');
      if (posB < 0) { posB = pim._mask.length(); }
      String mask = pim._mask.substring(0,posB);
      
      String a = (pim._ignoreCase)?(item.toString().toLowerCase()):(item.toString());
      String b = (pim._ignoreCase)?(mask.toLowerCase()):(mask);
      return a.equals(b);
    }
    public boolean equivalent(X item1, X item2, PredictiveInputModel<X> pim) {
      int posA = item1.toString().lastIndexOf(':');
      if (posA < 0) { posA = item1.toString().length(); }
      String i1 = item1.toString().substring(0,posA);

      int posB = item2.toString().lastIndexOf(':');
      if (posB < 0) { posB = item2.toString().length(); }
      String i2 = item2.toString().substring(0,posB);
      
      String a = (pim._ignoreCase)?(i1.toLowerCase()):(i1);
      String b = (pim._ignoreCase)?(i2.toLowerCase()):(i2);
      return a.equals(b);
    }
    public int compare(X item1, X item2, PredictiveInputModel<X> pim) {
      int posA = item1.toString().lastIndexOf(':');
      if (posA < 0) { posA = item1.toString().length(); }
      String i1 = item1.toString().substring(0,posA);

      int posB = item2.toString().lastIndexOf(':');
      if (posB < 0) { posB = item2.toString().length(); }
      String i2 = item2.toString().substring(0,posB);
      
      String a = (pim._ignoreCase)?(i1.toLowerCase()):(i1);
      String b = (pim._ignoreCase)?(i2.toLowerCase()):(i2);
      return a.compareTo(b);
    }
    public X getLongestMatch(X item, List<X> items, PredictiveInputModel<X> pim) {
      X longestMatch = null;
      int matchLength = -1;
      for(X i: items) {
        int posA = i.toString().lastIndexOf(':');
        if (posA < 0) { posA = i.toString().length(); }
        String i1 = i.toString().substring(0,posA);
        
        int posB = item.toString().lastIndexOf(':');
        if (posB < 0) { posB = item.toString().length(); }
        String i2 = item.toString().substring(0,posB);
        
        String s = (pim._ignoreCase)?(i1.toLowerCase()):(i1);
        String t = (pim._ignoreCase)?(i2.toLowerCase()):(i2);
        int ml = 0;
        while((s.length() > ml) && (t.length() > ml) && (s.charAt(ml) == t.charAt(ml))) {
          ++ml;
        }
        if (ml>matchLength) {
          matchLength = ml;
          longestMatch = i;
        }
      }
      return longestMatch;
    }
    public String getSharedMaskExtension(List<X> items, PredictiveInputModel<X> pim) {
      StringBuilder res = new StringBuilder();
      String ext = "";
      if (items.size() == 0) {
        return ext;
      }
      
      int posB = pim._mask.lastIndexOf(':');
      if (posB < 0) { posB = pim._mask.length(); }
      String mask = pim._mask.substring(0,posB);
      
      boolean allMatching = true;
      int len = mask.length();
      while((allMatching) && (mask.length() + ext.length() < items.get(0).toString().length())) {
        char origCh = items.get(0).toString().charAt(mask.length()+ext.length());
        char ch = (pim._ignoreCase)?(Character.toLowerCase(origCh)):(origCh);
        allMatching = true;
        for (X i: items) {
          String a = (pim._ignoreCase)?(i.toString().toLowerCase()):(i.toString());
          if (a.charAt(len) != ch) {
            allMatching = false;
            break;
          }
        }
        if (allMatching) {
          ext = ext + ch;
          res.append(origCh);
          ++len;
        }
      }
      return res.toString();
    }
    public String getExtendedSharedMask(List<X> items, PredictiveInputModel<X> pim) {
      int pos = pim._mask.lastIndexOf(':');
      if (pos < 0) { 
        return pim._mask + getSharedMaskExtension(items, pim);
      }
      else {
        return pim._mask.substring(0,pos) + getSharedMaskExtension(items, pim) + pim._mask.substring(pos);
      }
    }
    public String force(X item, String mask) {
      int pos = mask.lastIndexOf(':');
      if (pos < 0) { 
        return item.toString();
      }
      else {
        return item.toString()+mask.substring(pos);
      }
    }
  };
  
  
  public static class FragmentLineNumStrategy<X extends Comparable<? super X>> implements MatchingStrategy<X> {
    public String toString() { return "Fragments"; }
    public boolean isMatch(X item, PredictiveInputModel<X> pim) {
      int posB = pim._mask.lastIndexOf(':');
      if (posB < 0) { posB = pim._mask.length(); }
      String mask = pim._mask.substring(0,posB);
      
      String a = (pim._ignoreCase)?(item.toString().toLowerCase()):(item.toString());
      String b = (pim._ignoreCase)?(mask.toLowerCase()):(mask);

      java.util.StringTokenizer tok = new java.util.StringTokenizer(b);
      while(tok.hasMoreTokens()) {
        if (a.indexOf(tok.nextToken()) < 0) return false;
      }
      return true;
    }
    public boolean isPerfectMatch(X item, PredictiveInputModel<X> pim) {
      int posB = pim._mask.lastIndexOf(':');
      if (posB < 0) { posB = pim._mask.length(); }
      String mask = pim._mask.substring(0,posB);
      
      String a = (pim._ignoreCase)?(item.toString().toLowerCase()):(item.toString());
      String b = (pim._ignoreCase)?(mask.toLowerCase()):(mask);
      return a.equals(b);
    }
    public boolean equivalent(X item1, X item2, PredictiveInputModel<X> pim) {
      int posA = item1.toString().lastIndexOf(':');
      if (posA < 0) { posA = item1.toString().length(); }
      String i1 = item1.toString().substring(0,posA);

      int posB = item2.toString().lastIndexOf(':');
      if (posB < 0) { posB = item2.toString().length(); }
      String i2 = item2.toString().substring(0,posB);
      
      String a = (pim._ignoreCase)?(i1.toLowerCase()):(i1);
      String b = (pim._ignoreCase)?(i2.toLowerCase()):(i2);
      return a.equals(b);
    }
    public int compare(X item1, X item2, PredictiveInputModel<X> pim) {
      int posA = item1.toString().lastIndexOf(':');
      if (posA < 0) { posA = item1.toString().length(); }
      String i1 = item1.toString().substring(0,posA);

      int posB = item2.toString().lastIndexOf(':');
      if (posB < 0) { posB = item2.toString().length(); }
      String i2 = item2.toString().substring(0,posB);
      
      String a = (pim._ignoreCase)?(i1.toLowerCase()):(i1);
      String b = (pim._ignoreCase)?(i2.toLowerCase()):(i2);
      return a.compareTo(b);
    }
    public X getLongestMatch(X item, List<X> items, PredictiveInputModel<X> pim) {
      if (items.size() > 0)  return items.get(0);
      else return null;
    }
    public String getSharedMaskExtension(List<X> items, PredictiveInputModel<X> pim) {
      return ""; 
    }
    public String getExtendedSharedMask(List<X> items, PredictiveInputModel<X> pim) {
      return pim._mask;
    }
    public String force(X item, String mask) {
      int pos = mask.lastIndexOf(':');
      if (pos < 0) { 
        return item.toString();
      }
      else {
        return item.toString()+mask.substring(pos);
      }
    }
  };
  
  
  public static class RegExLineNumStrategy<X extends Comparable<? super X>> implements MatchingStrategy<X> {
    public String toString() { return "RegEx"; }
    public boolean isMatch(X item, PredictiveInputModel<X> pim) {
      int posB = pim._mask.lastIndexOf(':');
      if (posB < 0) { posB = pim._mask.length(); }
      String mask = pim._mask.substring(0,posB);
      
      String a = item.toString();

      try {
        Pattern p = Pattern.compile(mask,
                                    (pim._ignoreCase)?(Pattern.CASE_INSENSITIVE):(0));
        Matcher m = p.matcher(a);
        return m.matches();
      }
      catch (PatternSyntaxException e) {
        return false;
      }
    }
    public boolean isPerfectMatch(X item, PredictiveInputModel<X> pim) {
      int posB = pim._mask.lastIndexOf(':');
      if (posB < 0) { posB = pim._mask.length(); }
      String mask = pim._mask.substring(0,posB);
      
      String a = (pim._ignoreCase)?item.toString().toLowerCase():item.toString();
      return a.equals((pim._ignoreCase)?mask.toLowerCase():mask);
    }
    public boolean equivalent(X item1, X item2, PredictiveInputModel<X> pim) {
      int posA = item1.toString().lastIndexOf(':');
      if (posA < 0) { posA = item1.toString().length(); }
      String i1 = item1.toString().substring(0,posA);

      int posB = item2.toString().lastIndexOf(':');
      if (posB < 0) { posB = item2.toString().length(); }
      String i2 = item2.toString().substring(0,posB);
      
      String a = (pim._ignoreCase)?(i1.toLowerCase()):(i1);
      String b = (pim._ignoreCase)?(i2.toLowerCase()):(i2);
      return a.equals(b);
    }
    public int compare(X item1, X item2, PredictiveInputModel<X> pim) {
      int posA = item1.toString().lastIndexOf(':');
      if (posA < 0) { posA = item1.toString().length(); }
      String i1 = item1.toString().substring(0,posA);

      int posB = item2.toString().lastIndexOf(':');
      if (posB < 0) { posB = item2.toString().length(); }
      String i2 = item2.toString().substring(0,posB);
      
      String a = (pim._ignoreCase)?(i1.toLowerCase()):(i1);
      String b = (pim._ignoreCase)?(i2.toLowerCase()):(i2);
      return a.compareTo(b);
    }
    public X getLongestMatch(X item, List<X> items, PredictiveInputModel<X> pim) {
      if (items.size() > 0)  return items.get(0); 
      else return null;
    }
    public String getSharedMaskExtension(List<X> items, PredictiveInputModel<X> pim) {
      return ""; 
    }
    public String getExtendedSharedMask(List<X> items, PredictiveInputModel<X> pim) {
      return pim._mask;
    }
    public String force(X item, String mask) {
      int pos = mask.lastIndexOf(':');
      if (pos < 0) { 
        return item.toString();
      }
      else {
        return item.toString()+mask.substring(pos);
      }
    }
  };
  
  
  private volatile ArrayList<T> _items = new ArrayList<T>();

  
  private volatile int _index = 0;

  
  private final ArrayList<T> _matchingItems = new ArrayList<T>();

  
  private volatile String _mask = "";
  
  
  private volatile boolean _ignoreCase = false;
  
  
  private volatile MatchingStrategy<T> _strategy;

  
  public PredictiveInputModel(boolean ignoreCase, PredictiveInputModel<T> pim) {
    this(ignoreCase, pim._strategy, pim._items);
    setMask(pim.getMask());
  }

  
  public PredictiveInputModel(boolean ignoreCase, MatchingStrategy<T> strategy, Collection<T> items) {
    _ignoreCase = ignoreCase;
    _strategy = strategy;
    setItems(items);
  }

  
  public PredictiveInputModel(boolean ignoreCase, MatchingStrategy<T> strategy, T... items) {
    _ignoreCase = ignoreCase;
    _strategy = strategy;
    setItems(items);
  }

  
  public void setStrategy(MatchingStrategy<T> strategy) {
    _strategy = strategy;
    updateMatchingStrings(_items);
  }

  
  public List<T> getItems() {
    return new ArrayList<T>(_items);
  }
  
  
  public void setItems(Collection<T> items) {
    _items = new ArrayList<T>(items);
    Collections.sort(_items);
    updateMatchingStrings(_items);
  }

  
  public void setItems(T... items) {
    _items = new ArrayList<T>(items.length);
    for(T s: items) _items.add(s);
    Collections.sort(_items);
    updateMatchingStrings(_items);
  }

  
  public void setItems(PredictiveInputModel<T> pim) { setItems(pim._items); }  

  
  public String getMask() { return _mask; }

  
  public void setMask(String mask) {
    _mask = mask;
    updateMatchingStrings(_items);
  }

  
  private int indexOf(ArrayList<T> l, T item) {
    int index = 0;
    for (T i: l) {
      if (_strategy.equivalent(item, i, this)) return index;
      ++index;
    }
    return -1;
  }
  
  
  private void updateMatchingStrings(ArrayList<T> items) {
    items = new ArrayList<T>(items); 
    _matchingItems.clear();
    for(T s: items) {
      if (_strategy.isMatch(s, this)) _matchingItems.add(s);
    }
    if (_items.size() > 0) {
      for(int i = 0; i < _items.size(); ++i) {
        if (_strategy.isPerfectMatch(_items.get(i), this)) {
          _index = i;
          break;
        }
      }
      setCurrentItem(_items.get(_index));
    }
    else _index = 0;
  }

  
  public T getCurrentItem() {
    if (_items.size() > 0) return _items.get(_index);
    else return null;
  }

  
  public void setCurrentItem(T item) {
    if (_items.size() == 0) {
      _index = 0;
      return;
    }
    boolean found = false;
    int index = indexOf(_items, item);
    if (index < 0) {
      
      pickClosestMatch(item);
    }
    else {
      for (int i=index; i < _items.size(); ++i) {
        if (0 <= indexOf(_matchingItems, _items.get(i))) {
          _index = i;
          found = true;
          break;
        }
      }
      if (!found) {
        pickClosestMatch(item);
      }
    }
  }

  
  private void pickClosestMatch(T item) {
    if (_matchingItems.size() > 0) {
      
      T follows = _matchingItems.get(0);
      for (T i: _matchingItems) {
        if (_strategy.compare(item, i, this) < 0) {
          break;
        }
        follows = i;
      }
      _index = indexOf(_items, follows);
    }
    else {
      _index = indexOf(_items, _strategy.getLongestMatch(item, _items, this));
    }
  }

  
  public List<T> getMatchingItems() {
    return new ArrayList<T>(_matchingItems);
  }

  
  public String getSharedMaskExtension() {
    return _strategy.getSharedMaskExtension(_matchingItems, this);
  }

  
  public void extendMask(String extension) {
    _mask = _mask + extension;
    updateMatchingStrings(_matchingItems);
  }
  

  
  public void extendSharedMask() {
    _mask = _strategy.getExtendedSharedMask(_matchingItems, this);
    updateMatchingStrings(_matchingItems);
  }
}
