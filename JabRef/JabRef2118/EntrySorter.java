

package net.sf.jabref; 

import java.util.*; 

public  class  EntrySorter implements  DatabaseChangeListener {
	

    
    

	
    

	
    String[] idArray;

	
    BibtexEntry[] entryArray;

	
    
    private boolean outdated = false;

	
    private boolean changed = false;

	

    public EntrySorter(Map<String, BibtexEntry> entries, Comparator<BibtexEntry> comp) {
	    
        set = new ArrayList<BibtexEntry>();
        this.comp = comp;
        Set<String> keySet = entries.keySet();
	    if (keySet != null) {
    	    Iterator<String> i = keySet.iterator();
    	    while (i.hasNext()) {
    		    set.add(entries.get(i.next()));
            }
            
            changed = true;
            index();
	    }
    }


	

    public void index() {

        


        synchronized(set) {

            
            if (changed) {
                Collections.sort(set, comp);
                changed = false;
            }

            
            

	        int count = set.size();
            idArray = new String[count];
            entryArray = new BibtexEntry[count];
	        int piv = 0;
	        for (Iterator<BibtexEntry> i=set.iterator(); i.hasNext();) {
	            
    	        BibtexEntry entry = i.next();
    	        idArray[piv] = entry.getId();
    	        entryArray[piv] = entry;
    	        piv++;
            }
        }
    }


	

    public boolean isOutdated() {
	return outdated;
    }


	

    public String getIdAt(int pos) {
        synchronized(set) {
            return idArray[pos];
        }
	
    }


	

    public BibtexEntry getEntryAt(int pos) {
        synchronized(set) {
            return entryArray[pos];
        }
    }


	

    public int getEntryCount() {
        synchronized(set) {
	        if (entryArray != null)
	            return entryArray.length;
	        else
	        return 0;
        }
    }


	

    public void databaseChanged(DatabaseChangeEvent e) {
        synchronized(set) {
	        if (e.getType() == DatabaseChangeEvent.ADDED_ENTRY) {
                int pos = -Collections.binarySearch(set, e.getEntry(), comp) - 1;
                set.add(pos, e.getEntry());
                
                
                
                
            }
	        else if (e.getType() == DatabaseChangeEvent.REMOVED_ENTRY) {
	            set.remove(e.getEntry());
                changed = true;
            }
	        else if (e.getType() == DatabaseChangeEvent.CHANGED_ENTRY) {
                
                
                int pos = Collections.binarySearch(set, e.getEntry(), comp);
                int posOld = set.indexOf(e.getEntry());
                if (pos < 0) {
                    set.remove(posOld);
                    set.add(-pos-1, e.getEntry());
                }
                
            }

    	}

    }


	

    
    


	

    
    final ArrayList<BibtexEntry> set;

	
    Comparator<BibtexEntry> comp;


}
