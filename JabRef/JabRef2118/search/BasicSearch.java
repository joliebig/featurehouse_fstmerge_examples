package net.sf.jabref.search; 
import net.sf.jabref.SearchRule; 

import net.sf.jabref.BibtexEntry; 
import net.sf.jabref.export.layout.format.RemoveBrackets; 
import java.util.Map; 

import java.util.ArrayList; 
import java.util.Iterator; 
import java.util.HashMap; 
import java.util.regex.Matcher; 
import java.util.regex.Pattern; 


public  class  BasicSearch implements  SearchRule {
	
    private boolean caseSensitive;

	
    private boolean regExp;

	
    Pattern[] pattern;

	
    static RemoveBrackets removeBrackets = new RemoveBrackets();

	


    public BasicSearch(boolean caseSensitive, boolean regExp) {

        this.caseSensitive = caseSensitive;
        this.regExp = regExp;
    }


	

    


	

    public int applyRule(String query, BibtexEntry bibtexEntry) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("1", query);
        return applyRule(map, bibtexEntry);
    }


	

    


	

    private ArrayList<String> parseQuery(String query) {
        StringBuffer sb = new StringBuffer();
        ArrayList<String> result = new ArrayList<String>();
        int c;
        boolean escaped = false, quoted = false;
        for (int i=0; i<query.length(); i++) {
            c = query.charAt(i);
            
            if (!escaped && (c == '\\'))
                escaped = true;
            else {
                
                if (!escaped && !quoted && Character.isWhitespace((char)c)) {
                    if (sb.length() > 0) {
                        result.add(sb.toString());
                        sb = new StringBuffer();
                    }
                }
                else if (c == '"') {
                    
                    
                    if (sb.length() > 0) {
                        result.add(sb.toString());
                        sb = new StringBuffer();
                    }
                    quoted = !quoted;
                }
                else {
                    
                    
                    sb.append((char)c);
                }
                escaped = false;
            }
        }
        
        if (sb.length() > 0) {
            result.add(sb.toString());
        }

        return result; 
    }


	

    public int applyRule(Map<String, String> searchStrings, BibtexEntry bibtexEntry) {

        int flags = 0;
        String searchString = searchStrings.values().iterator().next();
        if (!caseSensitive) {
            searchString = searchString.toLowerCase();
            flags = Pattern.CASE_INSENSITIVE;
        }

        ArrayList<String> words = parseQuery(searchString);

        if (regExp) {
            pattern = new Pattern[words.size()];
            for (int i = 0; i < pattern.length; i++) {
                pattern[i] = Pattern.compile(words.get(i), flags);
            }
        }

        
        
        boolean[] matchFound = new boolean[words.size()];

        Object fieldContentAsObject;
        String fieldContent;
        
        for (String field : bibtexEntry.getAllFields()){
            fieldContentAsObject = bibtexEntry.getField(field);
            if (fieldContentAsObject != null) {
                fieldContent = removeBrackets.format(fieldContentAsObject.toString());
                if (!caseSensitive)
                    fieldContent = fieldContent.toLowerCase();
                int index = 0;
                
                
                for (int j=0; j<words.size(); j++) {
                    if (!regExp) {
                        String s = words.get(j);
                        matchFound[index] = matchFound[index]
                            || (fieldContent.indexOf(s) >= 0);
                    } else {
                        if (fieldContent != null) {
                            Matcher m = pattern[j].matcher
                                    (removeBrackets.format(fieldContent));
                            matchFound[index] = matchFound[index]
                                || m.find();
                        }
                    }

                    index++;
                }
            }

        }
        for (int i = 0; i < matchFound.length; i++) {
            if (!matchFound[i])
                return 0; 
        }
        return 1; 
    }


}
