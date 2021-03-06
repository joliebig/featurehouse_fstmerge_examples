
package net.sf.jabref.label;

import java.util.StringTokenizer;

import net.sf.jabref.BibtexEntry;

public class BookLabelRule extends DefaultLabelRule {

    
    
    public String applyRule(BibtexEntry oldEntry) {
        String newLabel = "";

        StringTokenizer authorTokens = null;
        
        try {
            if (oldEntry.getField("author") != null) {
                authorTokens = new StringTokenizer(oldEntry.getField("author"),
                    ",");
            } else if (oldEntry.getField("editor") != null) {
                authorTokens = new StringTokenizer(oldEntry.getField("editor"),
                    ",");
            }
            if (authorTokens != null)
                newLabel += authorTokens.nextToken().toLowerCase();
        } catch (Throwable t) {
            System.out.println("error getting author/editor: " + t);
        }

        
        try {
            if (oldEntry.getField("year") != null) {
                newLabel += String.valueOf(oldEntry.getField("year"));
            }
        } catch (Throwable t) {
            System.out.println("error getting author: " + t);
        }

        newLabel += "book";

        return newLabel;
    }

}
