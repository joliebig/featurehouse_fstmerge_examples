

package net.sf.jabref; 

import java.util.Comparator; 


public  class  EntryComparator implements  Comparator ,  Comparator<BibtexEntry> {
	

    String sortField;

	
    boolean descending, binary=false;

	
    

	

    public EntryComparator(boolean binary, boolean desc, String field, Comparator<BibtexEntry> next) {
        this.binary = binary;
        this.sortField = field;
        this.descending = desc;
        this.next = next;
    }


	

    <<<<<<< C:\Dokumente und Einstellungen\Sven Apel\Eigene Dateien\Uni\fstmerge\fstmerge_tmp\fstmerge_var1_64834
public EntryComparator(boolean binary, boolean desc, String field, Comparator<BibtexEntry> next) {
=======
public EntryComparator(boolean binary, boolean desc, String field) {
>>>>>>> C:\Dokumente und Einstellungen\Sven Apel\Eigene Dateien\Uni\fstmerge\fstmerge_tmp\fstmerge_var2_64836
        this.binary = binary;
        this.sortField = field;
        this.descending = desc;
        this.next = null;
    }


	


    


	

    private int idCompare(BibtexEntry b1, BibtexEntry b2) {
    return ((String)(b1.getId())).compareTo((String)(b2.getId()));
    }


	
    Comparator<BibtexEntry> next;

	


    public int compare(BibtexEntry e1, BibtexEntry e2) throws ClassCastException {

    if (e1 == e2)
        return 0;

    
    Object f1 = e1.getField(sortField),
        f2 = e2.getField(sortField);

    if (binary) {
        
        if (f1 != null)
            return (f2 == null) ? -1 :
                    (next != null ? next.compare(e1, e2) : idCompare(e1, e2));
        else
            return (f2 == null) ? (next != null ? next.compare(e1, e2) : idCompare(e1, e2))
                    : 1;
    }

    
    
    if (sortField.equals("author") || sortField.equals("editor")) {
        if (f1 != null)
        f1 = AuthorList.fixAuthorForAlphabetization((String)f1).toLowerCase();
                
        if (f2 != null)
        f2 = AuthorList.fixAuthorForAlphabetization((String)f2).toLowerCase();
                

    } else if (sortField.equals(GUIGlobals.TYPE_HEADER)) {
          
          f1 = e1.getType().getName();
          f2 = e2.getType().getName();
        }


    if ((f1 == null) && (f2 == null)) return (next != null ? next.compare(e1, e2) : idCompare(e1, e2));
	if ((f1 != null) && (f2 == null)) return -1;
	if ((f1 == null) && (f2 != null)) return 1;

	int result = 0;

	
	
	if ((f1 instanceof Integer) && (f2 instanceof Integer)) {
		result = -(((Integer) f1).compareTo((Integer) f2));
	} else if (f2 instanceof Integer) {
		Integer f1AsInteger = new Integer(f1.toString());
		result = -((f1AsInteger).compareTo((Integer) f2));
	} else if (f1 instanceof Integer) {
		Integer f2AsInteger = new Integer(f2.toString());
		result = -(((Integer) f1).compareTo(f2AsInteger));
	} else {
        String ours = ((String) f1).toLowerCase(),
	    	theirs = ((String) f2).toLowerCase();
        int comp = ours.compareTo(theirs);
		result = -comp;
	}
	if (result != 0)
	    return (descending ? result : -result); 
	if (next != null)
	    return next.compare(e1, e2); 
	else {

        return idCompare(e1, e2); 
    }
    }


}
