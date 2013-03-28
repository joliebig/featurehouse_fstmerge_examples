package net.sf.jabref.autocompleter;

import java.util.StringTokenizer;

import net.sf.jabref.BibtexEntry;


public class DefaultAutoCompleter extends AbstractAutoCompleter {

	public String _fieldName;

	
	protected DefaultAutoCompleter(String fieldName) {
		_fieldName = fieldName;
	}

	public boolean isSingleUnitField() {
		return false;
	}

	public String[] complete(String s) {
		return super.complete(s);
	}

	@Override
	public void addBibtexEntry(BibtexEntry entry) {
		if (entry != null) {
			String fieldValue = entry.getField(_fieldName);
			if (fieldValue == null) {
				return;
			} else {
				StringTokenizer tok = new StringTokenizer(fieldValue.toString(), " .,\n");
				while (tok.hasMoreTokens()) {
					String word = tok.nextToken();
					addWordToIndex(word);
				}
			}
		}
	}
}
