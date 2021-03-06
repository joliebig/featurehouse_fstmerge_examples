package net.sf.jabref.gui;

import java.util.StringTokenizer;
import java.util.TreeSet;

import net.sf.jabref.AuthorList;


public class AutoCompleter {
    final int SHORTEST_WORD = 4,
            SHORTEST_TO_COMPLETE = 2;

    private TreeSet<String> words = new TreeSet<String>();
    private boolean nameField = false, 
	entireField = false; 
		             

    public AutoCompleter(String fieldName) {
        if (fieldName.equals("author") || fieldName.equals("editor"))
            nameField = true;
	else if (fieldName.equals("journal") || fieldName.equals("publisher"))
	    entireField = true;
    }

    public void addWord(String word) {
        if (word.length() >= SHORTEST_WORD)
            words.add(word);
    }

    public void addAll(Object s) {
        if (s == null)
            return;
        if (nameField) {
            AuthorList list = AuthorList.getAuthorList(s.toString());
            String processed = list.getAuthorsLastFirstAnds(false);
            String[] names = processed.split(" and ");
            for (int i = 0; i < names.length; i++) {
                String name = names[i];
                addWord(name);
            }
	} else if (entireField) {
	    addWord(s.toString().trim());
        } else {
            StringTokenizer tok = new StringTokenizer(s.toString(), " .,\n");
            while (tok.hasMoreTokens()) {
                String word = tok.nextToken();
                
                addWord(word);
            }
        }
    }

    public Object[] complete(String s) {

        if (s.length() < SHORTEST_TO_COMPLETE)
            return null;

        char lastChar = s.charAt(s.length() - 1);
        String ender = s.substring(0, s.length() - 1)
                + Character.toString((char) (lastChar + 1));
        return words.subSet(s, ender).toArray();
    }
}
