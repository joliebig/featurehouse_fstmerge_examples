package net.sourceforge.pmd.lang.java.rule.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegexHelper {

	
	private RegexHelper() {}

	
	public static List<Pattern> compilePatternsFromList(List<String> list) {
		List<Pattern> patterns;
		if (list != null && list.size() > 0) {
			patterns = new ArrayList<Pattern>(list.size());
			for (String stringPattern : list) {
				if ( stringPattern != null && ! "".equals(stringPattern) ) {
					patterns.add(Pattern.compile(stringPattern));
				}
			}
		}
		else
			patterns = new ArrayList<Pattern>(0);
		return patterns;
	}

	
 	public static boolean isMatch(Pattern pattern,String subject) {
 		if ( subject != null && ! "".equals(subject) ) {
	        Matcher matcher = pattern.matcher(subject);
	        if (matcher.find()) {
	            return true;
	        }
 		}
		return false;
	}


}
