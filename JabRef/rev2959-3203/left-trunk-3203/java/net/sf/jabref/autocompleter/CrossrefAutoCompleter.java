package net.sf.jabref.autocompleter;

import net.sf.jabref.BibtexEntry;


public class CrossrefAutoCompleter extends AbstractAutoCompleter {

	public String _fieldName;

	
	protected CrossrefAutoCompleter(String fieldName) {
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
			if (entry != null) {
				String key = entry.getCiteKey();
				if (key != null)
					addWordToIndex(key.trim());
			}
		}
	}
}
