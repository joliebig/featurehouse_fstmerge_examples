package net.sf.jabref.imports;

import net.sf.jabref.BibtexEntry;


public interface ImportInspector {

    
    void setProgress(int current, int max);

    
    void addEntry(BibtexEntry entry);


    
    void toFront();
}