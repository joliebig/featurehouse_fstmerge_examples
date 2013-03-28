package net.sourceforge.squirrel_sql.plugins.editextras;

import java.util.StringTokenizer;
class Utilities
{
	static String quoteText(String textToQuote, boolean sbAppend)
	{
		if (null == textToQuote)
		{
			throw new IllegalArgumentException("textToQuote can not be null");
		}

		String[] lines = textToQuote.split("\n");

		StringBuffer ret = new StringBuffer();

		if (sbAppend)
		{
			ret.append("sb.append(\"").append(
				trimRight(lines[0].replaceAll("\"", "\\\\\"")));
		}
		else
		{
			ret.append("\"").append(
				trimRight(lines[0].replaceAll("\"", "\\\\\"")));
		}

		for (int i = 1; i < lines.length; ++i)
		{
			if (sbAppend)
			{
				ret.append(" \"); \nsb.append(\"").append(
					trimRight(lines[i].replaceAll("\"", "\\\\\"")));
			}
			else
			{
				ret.append(" \" +\n\"").append(
					trimRight(lines[i].replaceAll("\"", "\\\\\"")));
			}
		}

		if (sbAppend)
		{
			ret.append(" \");");
		}
		else
		{
			ret.append(" \";");
		}

		return ret.toString();
	}

	
	static String unquoteText(String textToUnquote)
	{
		
		
		textToUnquote = "\n" + textToUnquote + "\n";

		StringTokenizer st = new StringTokenizer(textToUnquote, "\"");

		StringBuffer ret = new StringBuffer();
		while (st.hasMoreTokens())
		{
			String token = st.nextToken();
			String trimmedToken = token;
			if (0 != token.trim().length() && -1 == token.indexOf('\n'))
			{
				if (trimmedToken.endsWith("\\n"))
				{
					
					
					trimmedToken =
						trimmedToken.substring(0, trimmedToken.length() - 2);
				}

				if (trimmedToken.endsWith("\\"))
				{
					ret.append(
						trimmedToken.substring(
							0,
							trimmedToken.length() - 1)).append(
						"\"");
				}
				else
				{
					ret.append(trimmedToken).append("\n");
				}
			}
		}
		if (ret.toString().endsWith("\n"))
		{
			ret.setLength(ret.length() - 1);
		}
		return ret.toString();
	}

	static String trimRight(String toTrim)                   
	{                                                         
		if( 0 >= toTrim.length())                             
		{                                                     
			return toTrim;                                    
		}                                                     
                                                          
		int i;                                                
		for(i=toTrim.length(); i > 0; --i)                    
		{                                                     
			if( !Character.isWhitespace(toTrim.charAt(i-1)) ) 
			{                                                 
				break;                                        
			}                                                 
		}                                                     
                                                          
		return toTrim.substring(0, i);                        
	}                                                         

}
