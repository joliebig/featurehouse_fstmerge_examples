

package net.sf.jabref.imports;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.BibtexEntryType;
import net.sf.jabref.BibtexFields;
import net.sf.jabref.BibtexString;
import net.sf.jabref.CustomEntryType;
import net.sf.jabref.GUIGlobals;
import net.sf.jabref.Globals;
import net.sf.jabref.JabRefPreferences;
import net.sf.jabref.KeyCollisionException;
import net.sf.jabref.UnknownEntryType;
import net.sf.jabref.Util;


public class BibtexParser {
	
	private PushbackReader _in;

	private BibtexDatabase _db;

	private HashMap _meta, entryTypes;

	private boolean _eof = false;

	private int line = 1;

	private FieldContentParser fieldContentParser = new FieldContentParser();

	private ParserResult _pr;

	public BibtexParser(Reader in) {

		if (in == null) {
			throw new NullPointerException();
		}
		if (Globals.prefs == null) {
			Globals.prefs = JabRefPreferences.getInstance();
		}
		_in = new PushbackReader(in);
	}

	
	public static ParserResult parse(Reader in) throws IOException {
		BibtexParser parser = new BibtexParser(in);
		return parser.parse();
	}
	
	
	
	public static Collection fromString(String bibtexString){
		StringReader reader = new StringReader(bibtexString);
		BibtexParser parser = new BibtexParser(reader); 
		try {
			return parser.parse().getDatabase().getEntries();
		} catch (Exception e){
			return null;
		}
	}
	
	
	public static BibtexEntry singleFromString(String bibtexString) {
		Collection c = fromString(bibtexString);
		if (c == null){
			return null;
		}
		return (BibtexEntry)c.iterator().next();
	}	
	
	
	public static boolean isRecognizedFormat(Reader inOrig) throws IOException {
		
		BufferedReader in = new BufferedReader(inOrig);

		Pattern pat1 = Pattern.compile("@[a-zA-Z]*\\s*\\{");

		String str;

		while ((str = in.readLine()) != null) {

			if (pat1.matcher(str).find())
				return true;
			else if (str.startsWith(GUIGlobals.SIGNATURE))
				return true;
		}

		return false;
	}

	private void skipWhitespace() throws IOException {
		int c;

		while (true) {
			c = read();
			if ((c == -1) || (c == 65535)) {
				_eof = true;
				return;
			}

			if (Character.isWhitespace((char) c)) {
				continue;
			} else
				
				
				unread(c);
			
			break;
		}
	}

	private String skipAndRecordWhitespace(int j) throws IOException {
		int c;
		StringBuffer sb = new StringBuffer();
		if (j != ' ')
			sb.append((char) j);
		while (true) {
			c = read();
			if ((c == -1) || (c == 65535)) {
				_eof = true;
				return sb.toString();
			}

			if (Character.isWhitespace((char) c)) {
				if (c != ' ')
					sb.append((char) c);
				continue;
			} else
				
				
				unread(c);
			
			break;
		}
		return sb.toString();
	}

	
	public ParserResult parse() throws IOException {

		
		if (_pr != null)
			return _pr;

        _db = new BibtexDatabase(); 
		_meta = new HashMap(); 
		entryTypes = new HashMap(); 
		_pr = new ParserResult(_db, _meta, entryTypes);

        
        
        String versionNum = readJabRefVersionNumber();
        if (versionNum != null) {
            _pr.setJabrefVersion(versionNum);
            setMajorMinorVersions();
        }
        else {
            
        }

        skipWhitespace();

		try {
			while (!_eof) {
				boolean found = consumeUncritically('@');
				if (!found)
					break;
				skipWhitespace();
				String entryType = parseTextToken();
				BibtexEntryType tp = BibtexEntryType.getType(entryType);
				boolean isEntry = (tp != null);
				
				if (!isEntry) {
					
					
					
					
					if (entryType.toLowerCase().equals("preamble")) {
						_db.setPreamble(parsePreamble());
					} else if (entryType.toLowerCase().equals("string")) {
						BibtexString bs = parseString();
						try {
							_db.addString(bs);
						} catch (KeyCollisionException ex) {
							_pr.addWarning(Globals.lang("Duplicate string name") + ": "
								+ bs.getName());
							
						}
					} else if (entryType.toLowerCase().equals("comment")) {
						StringBuffer commentBuf = parseBracketedTextExactly();
						
						String comment = commentBuf.toString().replaceAll("[\\x0d\\x0a]", "");
						if (comment.substring(0,
							Math.min(comment.length(), GUIGlobals.META_FLAG.length())).equals(
							GUIGlobals.META_FLAG)
							|| comment.substring(0,
								Math.min(comment.length(), GUIGlobals.META_FLAG_OLD.length()))
								.equals(GUIGlobals.META_FLAG_OLD)) {

							String rest;
							if (comment.substring(0, GUIGlobals.META_FLAG.length()).equals(
								GUIGlobals.META_FLAG))
								rest = comment.substring(GUIGlobals.META_FLAG.length());
							else
								rest = comment.substring(GUIGlobals.META_FLAG_OLD.length());

							int pos = rest.indexOf(':');

							if (pos > 0)
								_meta.put(rest.substring(0, pos), rest.substring(pos + 1));
							
							
							
							
						}

						
						if (comment.substring(0,
							Math.min(comment.length(), GUIGlobals.ENTRYTYPE_FLAG.length())).equals(
							GUIGlobals.ENTRYTYPE_FLAG)) {

							CustomEntryType typ = CustomEntryType.parseEntryType(comment);
							entryTypes.put(typ.getName().toLowerCase(), typ);

						}
					} else {
						
						
						
						
						
						
						tp = new UnknownEntryType(entryType.toLowerCase());
						
						isEntry = true;
					}
				}

				if (isEntry) 
				{
					
					try {
						BibtexEntry be = parseEntry(tp);

						boolean duplicateKey = _db.insertEntry(be);
						if (duplicateKey) 
							_pr.addWarning(Globals.lang("duplicate BibTeX key") + ": "
								+ be.getCiteKey() + " ("
								+ Globals.lang("grouping may not work for this entry") + ")");
						else if (be.getCiteKey() == null || be.getCiteKey().equals("")) {
							_pr.addWarning(Globals.lang("empty BibTeX key") + ": "
								+ be.getAuthorTitleYear(40) + " ("
								+ Globals.lang("grouping may not work for this entry") + ")");
						}
					} catch (IOException ex) {
						ex.printStackTrace();
						_pr.addWarning(Globals.lang("Error occured when parsing entry") + ": '"
							+ ex.getMessage() + "'. " + Globals.lang("Skipped entry."));

					}
				}

				skipWhitespace();
			}

			
			
			checkEntryTypes(_pr);

			return _pr;
		} catch (KeyCollisionException kce) {
			
			throw new IOException("Duplicate ID in bibtex file: " + kce.toString());
		}
	}

	private int peek() throws IOException {
		int c = read();
		unread(c);

		return c;
	}

	private int read() throws IOException {
		int c = _in.read();
		if (c == '\n')
			line++;
		return c;
	}

	private void unread(int c) throws IOException {
		if (c == '\n')
			line--;
		_in.unread(c);
	}

	public BibtexString parseString() throws IOException {
		
		skipWhitespace();
		consume('{', '(');
		
		skipWhitespace();
		
		String name = parseTextToken();
		
		skipWhitespace();
		
		consume('=');
		String content = parseFieldContent();
		
		consume('}', ')');
		
		String id = Util.createNeutralId();
		return new BibtexString(id, name, content);
	}

	public String parsePreamble() throws IOException {
		return parseBracketedText().toString();
	}

	public BibtexEntry parseEntry(BibtexEntryType tp) throws IOException {
		String id = Util.createNeutralId();
		BibtexEntry result = new BibtexEntry(id, tp);
		skipWhitespace();
		consume('{', '(');
		skipWhitespace();
		String key = null;
		boolean doAgain = true;
		while (doAgain) {
			doAgain = false;
			try {
				if (key != null)
					key = key + parseKey();
				else
					key = parseKey();
			} catch (NoLabelException ex) {
				
				
				
				char c = (char) peek();
				if (Character.isWhitespace(c) || (c == '{') || (c == '\"')) {
					String fieldName = ex.getMessage().trim().toLowerCase();
					String cont = parseFieldContent();
					result.setField(fieldName, cont);
				} else {
					if (key != null)
						key = key + ex.getMessage() + "=";
					else
						key = ex.getMessage() + "=";
					doAgain = true;
				}
			}
		}

		if ((key != null) && key.equals(""))
			key = null;
		
		if (result != null)
			result.setField(BibtexFields.KEY_FIELD, key);
		skipWhitespace();

		while (true) {
			int c = peek();
			if ((c == '}') || (c == ')')) {
				break;
			}

			if (c == ',')
				consume(',');

			skipWhitespace();

			c = peek();
			if ((c == '}') || (c == ')')) {
				break;
			}
			parseField(result);
		}

		consume('}', ')');
		return result;
	}

	private void parseField(BibtexEntry entry) throws IOException {
		String key = parseTextToken().toLowerCase();
		
		skipWhitespace();
		consume('=');
		String content = parseFieldContent();
		
		
		
		if (Globals.prefs.putBracesAroundCapitals(key)) {
			content = Util.removeBracesAroundCapitals(content);
		}
		if (content.length() > 0) {
			if (entry.getField(key) == null)
				entry.setField(key, content);
			else {
				
				
				
				
				
				
				
				
				
				if (key.equals("author") || key.equals("editor"))
					entry.setField(key, entry.getField(key) + " and " + content);
			}
		}
	}

	private String parseFieldContent() throws IOException {
		skipWhitespace();
		StringBuffer value = new StringBuffer();
		int c = '.';

		while (((c = peek()) != ',') && (c != '}') && (c != ')')) {

			if (_eof) {
				throw new RuntimeException("Error in line " + line + ": EOF in mid-string");
			}
			if (c == '"') {
				StringBuffer text = parseQuotedFieldExactly();
				value.append(fieldContentParser.format(text));
				
			} else if (c == '{') {
				
				
				
				StringBuffer text = parseBracketedTextExactly();
				value.append(fieldContentParser.format(text));

			} else if (Character.isDigit((char) c)) { 

				String numString = parseTextToken();
				try {
					
					value.append(String.valueOf(Integer.parseInt(numString)));
				} catch (NumberFormatException e) {
					
					
					value.append(numString);
				}
			} else if (c == '#') {
				consume('#');
			} else {
				String textToken = parseTextToken();
				if (textToken.length() == 0)
					throw new IOException("Error in line " + line + " or above: "
						+ "Empty text token.\nThis could be caused "
						+ "by a missing comma between two fields.");
				value.append("#").append(textToken).append("#");
				
				
			}
			skipWhitespace();
		}
		

		
		if (Globals.prefs.getBoolean("autoDoubleBraces")) {
			
			while ((value.length() > 1) && (value.charAt(0) == '{')
				&& (value.charAt(value.length() - 1) == '}')) {
				value.deleteCharAt(value.length() - 1);
				value.deleteCharAt(0);
			}
			
			
			
			while (hasNegativeBraceCount(value.toString())) {
				value.insert(0, '{');
				value.append('}');
			}

		}
		return value.toString();

	}

	
	private boolean hasNegativeBraceCount(String s) {
		
		int i = 0, count = 0;
		while (i < s.length()) {
			if (s.charAt(i) == '{')
				count++;
			else if (s.charAt(i) == '}')
				count--;
			if (count < 0)
				return true;
			i++;
		}
		return false;
	}

	
	private String parseTextToken() throws IOException {
		StringBuffer token = new StringBuffer(20);

		while (true) {
			int c = read();
			
			if (c == -1) {
				_eof = true;

				return token.toString();
			}

			if (Character.isLetterOrDigit((char) c) || (c == ':') || (c == '-') || (c == '_')
				|| (c == '*') || (c == '+') || (c == '.') || (c == '/') || (c == '\'')) {
				token.append((char) c);
			} else {
				unread(c);
				
				return token.toString();
			}
		}
	}

	
	private String parseKey() throws IOException, NoLabelException {
		StringBuffer token = new StringBuffer(20);

		while (true) {
			int c = read();
			
			if (c == -1) {
				_eof = true;

				return token.toString();
			}

			
			
			
			if (!Character.isWhitespace((char) c)
				&& (Character.isLetterOrDigit((char) c) || ((c != '#') && (c != '{') && (c != '}')
					&& (c != '\u') && (c != '~') && (c != '\u') && (c != ',') && (c != '=')))) {
				token.append((char) c);
			} else {

				if (Character.isWhitespace((char) c)) {
					
					
					
					
					
					return token.toString();
				} else if (c == ',') {
					unread(c);
					return token.toString();
					
					
				} else if (c == '=') {
					
					

					return token.toString();
					

				} else
					throw new IOException("Error in line " + line + ":" + "Character '" + (char) c
						+ "' is not " + "allowed in bibtex keys.");

			}
		}

	}

	private class NoLabelException extends Exception {
		public NoLabelException(String hasRead) {
			super(hasRead);
		}
	}

	private StringBuffer parseBracketedText() throws IOException {
		
		StringBuffer value = new StringBuffer();

		consume('{');

		int brackets = 0;

		while (!((peek() == '}') && (brackets == 0))) {

			int j = read();
			if ((j == -1) || (j == 65535)) {
				throw new RuntimeException("Error in line " + line + ": EOF in mid-string");
			} else if (j == '{')
				brackets++;
			else if (j == '}')
				brackets--;

			
			
			
			if (Character.isWhitespace((char) j)) {
				String whs = skipAndRecordWhitespace(j);

				

				if (!whs.equals("") && !whs.equals("\n\t")) { 
																

					whs = whs.replaceAll("\t", ""); 

					
					

					value.append(whs);

				} else {
					value.append(' ');
				}

			} else
				value.append((char) j);

		}

		consume('}');

		return value;
	}

	private StringBuffer parseBracketedTextExactly() throws IOException {

		StringBuffer value = new StringBuffer();

		consume('{');

		int brackets = 0;

		while (!((peek() == '}') && (brackets == 0))) {

			int j = read();
			if ((j == -1) || (j == 65535)) {
				throw new RuntimeException("Error in line " + line + ": EOF in mid-string");
			} else if (j == '{')
				brackets++;
			else if (j == '}')
				brackets--;

			value.append((char) j);

		}

		consume('}');

		return value;
	}

	private StringBuffer parseQuotedFieldExactly() throws IOException {

		StringBuffer value = new StringBuffer();

		consume('"');

		int brackets = 0;

		while (!((peek() == '"') && (brackets == 0))) {

			int j = read();
			if ((j == -1) || (j == 65535)) {
				throw new RuntimeException("Error in line " + line + ": EOF in mid-string");
			} else if (j == '{')
				brackets++;
			else if (j == '}')
				brackets--;

			value.append((char) j);

		}

		consume('"');

		return value;
	}

	private void consume(char expected) throws IOException {
		int c = read();

		if (c != expected) {
			throw new RuntimeException("Error in line " + line + ": Expected " + expected
				+ " but received " + (char) c);
		}

	}

	private boolean consumeUncritically(char expected) throws IOException {
		int c;
		while (((c = read()) != expected) && (c != -1) && (c != 65535))
			;
		if ((c == -1) || (c == 65535))
			_eof = true;

		
		return c == expected;
	}

	private void consume(char expected1, char expected2) throws IOException {
		

		int c = read();

		if ((c != expected1) && (c != expected2)) {
			throw new RuntimeException("Error in line " + line + ": Expected " + expected1 + " or "
				+ expected2 + " but received " + (int) c);

		}

	}

	public void checkEntryTypes(ParserResult _pr) {
		for (Iterator i = _db.getKeySet().iterator(); i.hasNext();) {
			Object key = i.next();
			BibtexEntry be = (BibtexEntry) _db.getEntryById((String) key);
			if (be.getType() instanceof UnknownEntryType) {
				

				Object o = entryTypes.get(be.getType().getName().toLowerCase());
				if (o != null) {
					BibtexEntryType type = (BibtexEntryType) o;
					be.setType(type);
				} else {
					
					
					_pr
						.addWarning(Globals.lang("unknown entry type") + ": "
							+ be.getType().getName() + ". " + Globals.lang("Type set to 'other'")
							+ ".");
					be.setType(BibtexEntryType.OTHER);
				}
			}
		}
	}

    
    private String readJabRefVersionNumber() throws IOException {
        StringBuffer headerText = new StringBuffer();
        
        boolean keepon = true;
        int piv = 0;
        int c;

        
        
        
        while (keepon) {
            c = peek();
            headerText.append((char) c);
            if (((piv == 0) && Character.isWhitespace((char) c))
                    || (c == GUIGlobals.SIGNATURE.charAt(piv))) {
                piv++;
                read();
            }
            else {
                keepon = false;
                return null;
            }

            
            if (piv == GUIGlobals.SIGNATURE.length()) {
                keepon = false;

                
                StringBuilder sb = new StringBuilder();
                while (((c=read()) != '\n') && (c != -1))
                    sb.append((char)c);
                String versionNum = sb.toString().trim();
                
                if (Pattern.compile("[1-9]+\\.[1-9A-Za-z ]+\\.").matcher(versionNum).matches()) {
                    
                    return versionNum.substring(0, versionNum.length()-1);
                }

            }
        }

        return null;
    }

    
    private void setMajorMinorVersions() {
        String v = _pr.getJabrefVersion();
        Pattern p = Pattern.compile("([0-9]+)\\.([0-9]+).*");
        Matcher m = p.matcher(v);
        if (!m.matches())
            return;
        if (m.groupCount() >= 2) {
            _pr.setJabrefMajorVersion(Integer.parseInt(m.group(1)));
            _pr.setJabrefMinorVersion(Integer.parseInt(m.group(2)));
        }
    }
}
