package net.sourceforge.squirrel_sql.fw.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtilities
{
	
	public static boolean isEmpty(String str)
	{
		return str == null || str.length() == 0;
	}

	
	public static boolean areStringsEqual(String str1, String str2)
	{
		if (str1 == null && str2 == null)
		{
			return true;
		}
		if (str1 != null)
		{
			return str1.equals(str2);
		}
		return str2.equals(str1);
	}

	
	public static String cleanString(String str)
	{
		final StringBuffer buf = new StringBuffer(str.length());
		char prevCh = ' ';

		for (int i = 0, limit = str.length(); i < limit; ++i)
		{
			char ch = str.charAt(i);
			if (Character.isWhitespace(ch))
			{
				if (!Character.isWhitespace(prevCh))
				{
					buf.append(' ');
				}
			}
			else
			{
				buf.append(ch);
			}
			prevCh = ch;
		}

		return buf.toString();
	}

	
	public static int countOccurences(String str, int ch)
	{
		if (isEmpty(str))
		{
			return 0;
		}

		int count = 0;
		int idx = -1;
		do
		{
			idx = str.indexOf(ch, ++idx);
			if (idx != -1)
			{
				++count;
			}
		}
		while (idx != -1);
		return count;
	}

	
	public static String[] split(String str, char delimiter)
	{
		return split(str, delimiter, false);
	}

	
	public static String[] split(String str, char delimiter,
										boolean removeEmpty)
	{
		
		final int len = (str == null) ? 0 : str.length();
		if (len == 0)
		{
			return new String[0];
		}

		final List<String> result = new ArrayList<String>();
		String elem = null;
		int i = 0, j = 0;
		while (j != -1 && j < len)
		{
			j = str.indexOf(delimiter,i);
			elem = (j != -1) ? str.substring(i, j) : str.substring(i);
			i = j + 1;
			if (!removeEmpty || !(elem == null || elem.length() == 0))
			{
				result.add(elem);
			}
		}
		return result.toArray(new String[result.size()]);
	}
    
    
    public static String join(String[] parts, String delim) {
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            result.append(part);
            if (delim != null && i < parts.length-1) {
                result.append(delim);
            }        
        }
        return result.toString();
    }
    
    public static String[] segment(String source, int maxSegmentSize) {
        ArrayList<String> tmp = new ArrayList<String>();
        if (source.length() <= maxSegmentSize) {
            return new String[] { source };
        }
        boolean done = false;
        int currBeginIdx = 0;
        int currEndIdx = maxSegmentSize;
        while (!done) {
            String segment = source.substring(currBeginIdx, currEndIdx);
            tmp.add(segment);
            if (currEndIdx >= source.length()) {
                done = true;
                continue;
            }
            currBeginIdx = currEndIdx;
            currEndIdx += maxSegmentSize;
            if (currEndIdx > source.length()) {
                currEndIdx = source.length();
            }
        }
        return tmp.toArray(new String[tmp.size()]);
    }
    
    public static int getTokenBeginIndex(String selectSQL, String token)
    {
       String lowerSel = selectSQL.toLowerCase();
       String lowerToken = token.toLowerCase().trim();

       int curPos = 0;
       int count = 0;
       while(-1 != curPos)
       {
          curPos = lowerSel.indexOf(lowerToken, curPos + lowerToken.length());

          if(-1 < curPos
                  && (0 == curPos || Character.isWhitespace(lowerSel.charAt(curPos-1)))
                  && (lowerSel.length() == curPos + lowerToken.length() || Character.isWhitespace(lowerSel.charAt(curPos + lowerToken.length())))
            )
          {
             return curPos;
          }
          
          
          if (count++ > selectSQL.length()) {
              break;
          }
       }

       return curPos;
    }
    
    public static Byte[] getByteArray(byte[] bytes) {
        if (bytes == null || bytes.length == 0 ) {
            return new Byte[0];
        }
        Byte[] result = new Byte[bytes.length]; 
        for (int i = 0; i < bytes.length; i++) {
            result[i] = Byte.valueOf(bytes[i]);
        }

        return result;
    }
    
    
    public static String chop(String aString) {
        if (aString == null) {
            return null;
        }
        if (aString.length() == 0) {
            return "";
        }
        if (aString.length() == 1) {
            return "";
        }
        return aString.substring(0, aString.length()-1);
    }
    
    
    public static String getEolStr() {
   	 return System.getProperty("line.separator", "\n");
    }
}
