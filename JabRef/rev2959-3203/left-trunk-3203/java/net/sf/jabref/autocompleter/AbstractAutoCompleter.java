package net.sf.jabref.autocompleter;

import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.jabref.BibtexEntry;


public abstract class AbstractAutoCompleter {

	public static final int SHORTEST_TO_COMPLETE = 2;
	public static final int SHORTEST_WORD = 4;

	private TreeSet<String> _index = new TreeSet<String>();

	
	abstract public void addBibtexEntry(BibtexEntry entry);

	abstract public boolean isSingleUnitField();

	
	public String[] complete(String str) {
		if (stringMinLength(str))
			return null;
		String ender = incrementLastCharacter(str);
		SortedSet<String> subset = _index.subSet(str, ender);
		return subset.toArray(new String[0]);
	}

	
	private static String incrementLastCharacter(String str) {
		char lastChar = str.charAt(str.length() - 1);
		String ender = str.substring(0, str.length() - 1) + Character.toString((char) (lastChar + 1));
		return ender;
	}

	private static boolean stringMinLength(String str) {
		return str.length() < AbstractAutoCompleter.SHORTEST_TO_COMPLETE;
	}

	public void addWordToIndex(String word) {
		if (word.length() >= SHORTEST_WORD)
			_index.add(word);
	}

	public boolean indexContainsWord(String word) {
		return _index.contains(word);
	}

}