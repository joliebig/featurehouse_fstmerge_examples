







package net.sf.jabref;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JOptionPane;

public class BibtexDatabase {
    
	Map<String, BibtexEntry> _entries = new Hashtable<String, BibtexEntry>();

	String _preamble = null;

	HashMap<String, BibtexString> _strings = new HashMap<String, BibtexString>();

	Vector<String> _strings_ = new Vector<String>();

	Set<DatabaseChangeListener> changeListeners = new HashSet<DatabaseChangeListener>();

	
	private HashMap<String, Integer> allKeys = new HashMap<String, Integer>();

    
    private final VetoableChangeListener listener =
        new VetoableChangeListener()
        {
            public void vetoableChange(PropertyChangeEvent pce)
                throws PropertyVetoException
            {
                if (pce.getPropertyName() == null)
                    fireDatabaseChanged (new DatabaseChangeEvent(BibtexDatabase.this, DatabaseChangeEvent.CHANGING_ENTRY, (BibtexEntry)pce.getSource()));
                else if ("id".equals(pce.getPropertyName()))
                {
                    
                    BibtexEntry oldEntry =
                        _entries.remove(pce.getOldValue());

                    if (oldEntry != pce.getSource())
                    {
                        
                        
                        
                        
                        _entries.put((String)pce.getOldValue(), oldEntry);
                        throw new PropertyVetoException("Wrong old ID", pce);
                    }

                    if (_entries.get(pce.getNewValue()) != null)
                    {
                        _entries.put((String)pce.getOldValue(), oldEntry);
                        throw new PropertyVetoException
                            ("New ID already in use, please choose another",
                            pce);
                    }

                    
                    _entries.put((String) pce.getNewValue(),
                        (BibtexEntry) pce.getSource());
                } else {
                    fireDatabaseChanged (new DatabaseChangeEvent(BibtexDatabase.this, DatabaseChangeEvent.CHANGED_ENTRY, (BibtexEntry)pce.getSource()));
                    
                    
                }
            }
        };

    
    public synchronized int getEntryCount()
    {
        return _entries.size();
    }

    
    public synchronized Set<String> getKeySet()
    {
        return _entries.keySet();
    }

    
    public synchronized EntrySorter getSorter(Comparator<BibtexEntry> comp) {
        EntrySorter sorter = new EntrySorter(_entries, comp);
        addDatabaseChangeListener(sorter);
        return sorter;
    }

    
    public Map<String, BibtexEntry> getEntryMap() { return _entries; }

    
    public synchronized BibtexEntry getEntryById(String id)
    {
        return _entries.get(id);
    }

    public synchronized Collection<BibtexEntry> getEntries() {
            return _entries.values();
    }

    
    public synchronized BibtexEntry getEntryByKey(String key)
    {
      BibtexEntry back = null ;

      int keyHash = key.hashCode() ; 

      Set<String> keySet = _entries.keySet();
      if (keySet != null)
      {
          Iterator<String> it = keySet.iterator();
          while(it.hasNext())
          {
            String entrieID = it.next() ;
            BibtexEntry entry = getEntryById(entrieID) ;
            if ((entry != null) && (entry.getCiteKey() != null))
            {
              String citeKey = entry.getCiteKey() ;
              if (citeKey != null)
              {
                if (keyHash == citeKey.hashCode() )
                {
                  back = entry ;
                }
              }
            }
          }
      }
      return back ;
    }

    public synchronized BibtexEntry[] getEntriesByKey(String key) {
        
    	ArrayList<BibtexEntry> entries = new ArrayList<BibtexEntry>();
        
        for (BibtexEntry entry : _entries.values()){
            if (key.equals(entry.getCiteKey()))
                entries.add(entry);
        }
        
        return entries.toArray(new BibtexEntry[entries.size()]);
    }

    
    public synchronized boolean insertEntry(BibtexEntry entry)
        throws KeyCollisionException
    {
        String id = entry.getId();
        if (getEntryById(id) != null)
        {
          throw new KeyCollisionException(
                "ID is already in use, please choose another");
        }

        entry.addPropertyChangeListener(listener);

        _entries.put(id, entry);

        fireDatabaseChanged(new DatabaseChangeEvent(this, DatabaseChangeEvent.ADDED_ENTRY, entry));

        return checkForDuplicateKeyAndAdd(null, entry.getCiteKey(), false);
    }

    
    public synchronized BibtexEntry removeEntry(String id)
    {
        BibtexEntry oldValue = _entries.remove(id);
        
        if (oldValue == null)
            return null;
        
        removeKeyFromSet(oldValue.getCiteKey());
        oldValue.removePropertyChangeListener(listener);
        fireDatabaseChanged(new DatabaseChangeEvent(this, DatabaseChangeEvent.REMOVED_ENTRY, oldValue));

        return oldValue;
    }

    public synchronized boolean setCiteKeyForEntry(String id, String key) {
        if (!_entries.containsKey(id)) return false; 
        BibtexEntry entry = getEntryById(id);
        String oldKey = entry.getCiteKey();
        if (key != null)
          entry.setField(BibtexFields.KEY_FIELD, key);
        else
          entry.clearField(BibtexFields.KEY_FIELD);
        return checkForDuplicateKeyAndAdd(oldKey, entry.getCiteKey(), false);
    }

    
    public synchronized void setPreamble(String preamble)
    {
        _preamble = preamble;
    }

    
    public synchronized String getPreamble()
    {
        return _preamble;
    }

    
    public synchronized void addString(BibtexString string)
        throws KeyCollisionException
    {
    	if (hasStringLabel(string.getName())){
    		throw new KeyCollisionException("A string with this label already exists,");
        }

        if (_strings.containsKey(string.getId()))
            throw new KeyCollisionException("Duplicate BibtexString id.");

        _strings.put(string.getId(), string);
    }

    
    public synchronized void removeString(String id) {
        _strings.remove(id);
    }

    
    public Set<String> getStringKeySet() {
        return _strings.keySet();
    }
    
    
    public Collection<BibtexString> getStringValues() {
        return _strings.values();
    }

    
    public synchronized BibtexString getString(String o) {
        return _strings.get(o);
    }

    
    public synchronized int getStringCount() {
        return _strings.size();
    }

    
    public synchronized boolean hasStringLabel(String label) {
    	for (BibtexString value : _strings.values()){
             if (value.getName().equals(label))
                return true;
        }
        return false;
    }

    
    public String resolveForStrings(String content) {
    	if (content == null){
    		throw new IllegalArgumentException("Content for resolveForStrings must not be null.");
    	}
        return resolveContent(content, new HashSet<String>());
    }
    
    
    public List<BibtexEntry> resolveForStrings(Collection<BibtexEntry> entries, boolean inPlace){
    	
    	if (entries == null)
    		throw new NullPointerException();
    	
    	List<BibtexEntry> results = new ArrayList<BibtexEntry>(entries.size());
    	
    	for (BibtexEntry entry : entries){
    		results.add(this.resolveForStrings(entry, inPlace));
    	}
    	return results;
    }
    
    
    public BibtexEntry resolveForStrings(BibtexEntry entry, boolean inPlace) {
		
    	if (!inPlace){
    		entry = (BibtexEntry)entry.clone();
    	}
    	
    	for (Object field : entry.getAllFields()){
    		entry.setField(field.toString(), this.resolveForStrings(entry.getField(field.toString()).toString()));
    	}
    	
    	return entry;
	}

	
    private String resolveString(String label, HashSet<String> usedIds) {
    	for (BibtexString string : _strings.values()){

                
            if (string.getName().toLowerCase().equals(label.toLowerCase())) {

                
                
                
                
                if (usedIds.contains(string.getId())) {
                    Util.pr("Stopped due to circular reference in strings: "+label);
                    return label;
                }
                
                usedIds.add(string.getId());

                
                
                String res = string.getContent();
                res = resolveContent(res, usedIds);

                
                
                usedIds.remove(string.getId());

                return res;
            }
        }

        
        
        Object o;
        if ((o = Globals.MONTH_STRINGS.get(label.toLowerCase())) != null) {
            return (String)o;
        }

        return null;
    }

    private String resolveContent(String res, HashSet<String> usedIds) {
        
    if (res.matches(".*#[^#]+#.*")) {
            StringBuffer newRes = new StringBuffer();
            int piv = 0, next = 0;
            while ((next=res.indexOf("#", piv)) >= 0) {

                
                
                if (next > 0)
                    newRes.append(res.substring(piv, next));
                int stringEnd = res.indexOf("#", next+1);
                if (stringEnd >= 0) {
                    
                    
                    String refLabel = res.substring(next+1, stringEnd);
                    String resolved = resolveString(refLabel, usedIds);
                    
                    if (resolved == null) {
                        
                        
                        newRes.append(res.substring(next, stringEnd+1));
                    } else
                        
                        
                        newRes.append(resolved);
                    piv = stringEnd+1;
                } else {
                    
                    
                    
                    newRes.append(res.substring(next));
                    piv = res.length();
                    break;
                }

            }
            if (piv < res.length()-1)
                newRes.append(res.substring(piv));
            res = newRes.toString();
        }
        return res;
    }

    
    
    
    
        
    
    public boolean checkForDuplicateKeyAndAdd(String oldKey, String newKey, boolean issueWarning){
                

        boolean duplicate=false;
        if(oldKey==null){
            duplicate= addKeyToSet( newKey);
        }else{
            if(oldKey.equals(newKey)){
                duplicate=false;
            }else{

                
                
                
                
                
                
                

                removeKeyFromSet( oldKey);
                duplicate = addKeyToSet( newKey );
            }
        }
        if(duplicate==true && issueWarning==true){
            JOptionPane.showMessageDialog(null,  Globals.lang("Warning there is a duplicate key")+":" + newKey ,
                                          Globals.lang("Duplicate Key Warning"),
                                          JOptionPane.WARNING_MESSAGE);

        }
        return duplicate;
    }

    
    public int getNumberOfKeyOccurences(String key) {
        Object o = allKeys.get(key);
        if (o == null)
            return 0;
        else
            return ((Integer)o).intValue();

    }

    
    
    
    private boolean addKeyToSet(String key){
                boolean exists=false;
                if((key == null) || key.equals(""))
                        return false;
                if(allKeys.containsKey(key)){
                        
                        exists=true;
                        allKeys.put( key, new Integer( allKeys.get(key).intValue() + 1));
                }else
                        allKeys.put( key, new Integer(1));
                return exists;
    }
    
    
    
    
    
    private void removeKeyFromSet(String key){
                if((key == null) || key.equals("")) return;
                if(allKeys.containsKey(key)){
                        Integer tI = allKeys.get(key); 
                        if(tI.intValue()==1)
                                allKeys.remove( key);
                        else
                                allKeys.put( key, new Integer( (tI).intValue() - 1));
                }
    }



    public void fireDatabaseChanged(DatabaseChangeEvent e) {
    	for (DatabaseChangeListener listener : changeListeners){
    		listener.databaseChanged(e);
        }
    }

    public void addDatabaseChangeListener(DatabaseChangeListener l) {
        changeListeners.add(l);
    }

    public void removeDatabaseChangeListener(DatabaseChangeListener l) {
        changeListeners.remove(l);
    }

	
	public static String getResolvedField(String field, BibtexEntry bibtex,
			BibtexDatabase database) {
	
		if (field.equals("bibtextype"))
			return bibtex.getType().getName();

        Object o = bibtex.getField(field);

        
        
        if ((o == null) && (database != null)) {
            Object crossRef = bibtex.getField("crossref");
            if (crossRef != null) {
                BibtexEntry referred = database.getEntryByKey((String)crossRef);
                if (referred != null) {
                    
                    
                    o = referred.getField(field);
                }
            }
        }

        return getText((String)o, database);
	}

	
	public static String getText(String toResolve, BibtexDatabase database) {
		if (toResolve != null && database != null)
			return database.resolveForStrings(toResolve);
		
		return toResolve;
	}
}
