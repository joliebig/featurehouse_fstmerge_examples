

package edu.rice.cs.drjava.ui.predictive;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;


public class PredictiveInputModel<T extends Comparable<? super T>> {
  
  public static interface MatchingStrategy<X extends Comparable<? super X>> {
    
    public boolean isMatch(X item, PredictiveInputModel<X> pim);
    
    
    public boolean equivalent(X item1, X item2, PredictiveInputModel<X> pim);
    
    
    public int compare(X item1, X item2, PredictiveInputModel<X> pim);

    
    public X getLongestMatch(X item, List<X> items, PredictiveInputModel<X> pim);
    
    
    public String getSharedMaskExtension(List<X> items, PredictiveInputModel<X> pim);
  }
  
  
  public static class PrefixStrategy<X extends Comparable<? super X>> implements MatchingStrategy<X> {
    public String toString() { return "Prefix"; }
    public boolean isMatch(X item, PredictiveInputModel<X> pim) {
      String a = (pim._ignoreCase)?(item.toString().toLowerCase()):(item.toString());
      String b = (pim._ignoreCase)?(pim._mask.toLowerCase()):(pim._mask);
      return a.startsWith(b);
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
      X longestMatch = null;
      int matchLength = -1;
      for(X i: items) {
        String s = (pim._ignoreCase)?(i.toString().toLowerCase()):(i.toString());
        String t = (pim._ignoreCase)?(item.toString().toLowerCase()):(item.toString());
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
      String res = "";
      String ext = "";
      if (items.size() == 0) {
        return ext;
      }
      boolean allMatching = true;
      int len = pim._mask.length();
      while((allMatching) && (pim._mask.length() + ext.length() < items.get(0).toString().length())) {
        char origCh = items.get(0).toString().charAt(pim._mask.length()+ext.length());
        char ch = (pim._ignoreCase)?(Character.toLowerCase(origCh)):(origCh);
        allMatching = true;
        for (X i: items) {
          String a = (pim._ignoreCase)?(i.toString().toLowerCase()):(i.toString());
          if (a.charAt(len)!=ch) {
            allMatching = false;
            break;
          }
        }
        if (allMatching) {
          ext = ext + ch;
          res = res + origCh;
          ++len;
        }
      }
      return res;
    }
  };
  
  
  public static class FragmentStrategy<X extends Comparable<? super X>> implements MatchingStrategy<X> {
    public String toString() { return "Fragments"; }
    public boolean isMatch(X item, PredictiveInputModel<X> pim) {
      String a = (pim._ignoreCase)?(item.toString().toLowerCase()):(item.toString());
      String b = (pim._ignoreCase)?(pim._mask.toLowerCase()):(pim._mask);

      java.util.StringTokenizer tok = new java.util.StringTokenizer(b);
      while(tok.hasMoreTokens()) {
        if (a.indexOf(tok.nextToken())<0) {
          return false;
        }
      }
      return true;
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
      if (items.size()>0) {
        return items.get(0);
      }
      else {
        return null;
      }
    }
    public String getSharedMaskExtension(List<X> items, PredictiveInputModel<X> pim) {
      return ""; 
    }
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
      if (items.size()>0) {
        return items.get(0); 
      }
      else {
        return null;
      }
    }
    public String getSharedMaskExtension(List<X> items, PredictiveInputModel<X> pim) {
      return ""; 
    }
  };
  
  
  ArrayList<T> _items = new ArrayList<T>();

  
  int _index = 0;

  
  ArrayList<T> _matchingItems = new ArrayList<T>();

  
  String _mask = "";
  
  
  boolean _ignoreCase = false;
  
  
  MatchingStrategy<T> _strategy;

  
  public PredictiveInputModel(boolean ignoreCase, PredictiveInputModel<T> pim) {
    this(ignoreCase, pim._strategy, pim._items);
    setMask(pim.getMask());
  }

  
  public PredictiveInputModel(boolean ignoreCase, MatchingStrategy<T> strategy, List<T> items) {
    _ignoreCase = ignoreCase;
    _strategy = strategy;
    setList(items);
  }

  
  public PredictiveInputModel(boolean ignoreCase, MatchingStrategy<T> strategy, T... items) {
    _ignoreCase = ignoreCase;
    _strategy = strategy;
    setList(items);
  }

  
  public void setStrategy(MatchingStrategy<T> strategy) {
    _strategy = strategy;
    updateMatchingStrings(_items);
  }

  
  public void setList(List<T> items) {
    _items = new ArrayList<T>(items);
    Collections.sort(_items);
    updateMatchingStrings(_items);
  }

  
  public void setList(T... items) {
    _items = new ArrayList<T>(items.length);
    for(T s: items) {
      _items.add(s);
    }
    Collections.sort(_items);
    updateMatchingStrings(_items);
  }

  
  public void setList(PredictiveInputModel<T> pim) {
    setList(pim._items);
  }  

  
  public String getMask() {
    return _mask;
  }

  
  public void setMask(String mask) {
    _mask = mask;
    updateMatchingStrings(_items);
  }

  
  private int indexOf(ArrayList<T> l, T item) {
    int index = 0;
    for (T i: l) {
      if (_strategy.equivalent(item, i, this)) {
        return index;
      }
      ++index;
    }
    return -1;
  }
  
  
  private void updateMatchingStrings(ArrayList<T> items) {
    items = new ArrayList<T>(items); 
    _matchingItems.clear();
    for(T s: items) {
      if (_strategy.isMatch(s, this)) {
        _matchingItems.add(s);
      }
    }
    if (_items.size() > 0) {
      setCurrentItem(_items.get(_index));
    }
    else {
      _index = 0;
    }
  }

  
  public T getCurrentItem() {
    if (_items.size() > 0) {
      return _items.get(_index);
    }
    else {
      return null;
    }
  }

  
  public void setCurrentItem(T item) {
    if (_items.size() == 0) {
      _index = 0;
      return;
    }
    boolean found = false;
    int index = indexOf(_items, item);
    if (index<0) {
      
      pickClosestMatch(item);
    }
    else {
      for (int i=index; i<_items.size(); ++i) {
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
}
