package net.sf.jabref.util;



import net.sf.jabref.Util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashSet;


public class CaseChanger {

	
	public final static int LOWER = 0;

	
	public final static int UPPER = 1;

	
	public final static int UPPER_FIRST = 2;

	
	public final static int UPPER_EACH_FIRST = 3;

	
	private final static Pattern UF_PATTERN = Pattern.compile("\\b\\w");


	
	
	

	
	private final static int numModes = 4;

	private final static String[] modeNames = { "lower", "UPPER", "Upper first", "Upper Each First" };

    private final static HashSet<String> notToCapitalize = new HashSet<String>();

    static {
        notToCapitalize.add("of");
        notToCapitalize.add("and");
        notToCapitalize.add("the");
    }

	
	public static String getModeName(int mode) {
		return modeNames[mode];
	}

	
	public static String[] getModeNames() {
		return modeNames;
	}

	
	public static int getNumModes() {
		return numModes;
	}

	
	public static String[] changeCase(String[] input, int mode) {
		int n = input.length;
		String[] output = new String[n];
		for (int i = 0; i < n; i++) {
			output[i] = changeCase(input[i], mode);
		}
		return output;
	}

	
	public static String changeCase(String input, int mode) {
        return changeCase(input, mode, false);
    }

    
	public static String changeCase(String input, int mode, boolean skipSmallWords) {

		switch (mode) {
		case UPPER:
			return input.toUpperCase();
		case LOWER:
			return input.toLowerCase();
		case UPPER_FIRST: {
			String s = input.toLowerCase();

			Matcher matcher = UF_PATTERN.matcher(s);

			if (matcher.find()) {
				return matcher.replaceFirst(matcher.group(0).toUpperCase());
			} else {
				return input;
			}
		}
		case UPPER_EACH_FIRST: {
			String s = input.toLowerCase();
			boolean found = false;
            String[] words = s.split("\\s+");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                String word = words[i];
                if ((i == 0) || !skipSmallWords || !notToCapitalize.contains(word))
                    sb.append(Util.nCase(word));
                else
                    sb.append(word);
                if (i < words.length-1)
                    sb.append(" ");

            }
            return sb.toString();
			
		}
		default:
			return input;
		}
	}
}
