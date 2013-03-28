package net.sourceforge.squirrel_sql.fw.datasetviewer;

 
 import net.sourceforge.squirrel_sql.fw.util.StringManager;
 import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

 import javax.swing.JOptionPane;
 import java.util.ArrayList;


public class XmlRefomatter
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(XmlRefomatter.class);


	
	private static String DEFAULT_MESSAGE = s_stringMgr.getString("xmlRefomatter.unexpectedProblem");
	private static String _message = DEFAULT_MESSAGE;
   private static boolean _showWarningMessages = true;

   public static String reformatXml(String xml)
	{
		
		if (xml.indexOf("<") == -1 || xml.equals("<null>")) {
			
			JOptionPane.showMessageDialog(null,
				
				s_stringMgr.getString("xmlRefomatter.noXml"),
				
				s_stringMgr.getString("xmlRefomatter.xmlWarning"), JOptionPane.WARNING_MESSAGE);
			return xml;
		}

		try
		{
			StringBuffer ret = new StringBuffer();
			int depth = 0;
			ParseRes parseRes = getParseRes(xml, 0);

			if (parseRes == null)
         {
				
            showWarning(_message);
				return xml;
			}

			xml = xml.trim();

			
			ArrayList<String> tagList = new ArrayList<String>();	

			while(null != parseRes)
			{

				if(ParseRes.BEGIN_TAG == parseRes.type)
				{
					tagList.add(parseRes.item);	

					ret.append(getIndent(depth)).append(parseRes.item);
					ParseRes nextRes = getParseRes(xml, parseRes.pos);

					
					if (nextRes == null) {
						
					    showWarning(_message);
						return xml;
					}

					if(ParseRes.TEXT != nextRes.type)
					{
						ret.append("\n");
					}

					++depth;
				}
				else if(ParseRes.END_TAG == parseRes.type)
				{
					
					if (tagList.size()> 0 ) {
						String startTag = tagList.remove(tagList.size()-1);
						
						
						
						
						String testableStartTag = startTag.substring(1, startTag.length() -1).trim().toUpperCase();
						if (testableStartTag.indexOf(' ') > -1)
							testableStartTag = testableStartTag.substring(0, testableStartTag.indexOf(' '));
						String endTag = parseRes.item.substring(2, parseRes.item.length()-1).trim().toUpperCase();

						if ( ! testableStartTag.equals(endTag))
                  {
							Object[] args = new Object[]{startTag, parseRes.item};

							
							String msg = s_stringMgr.getString("xmlRefomatter.malformedXml", args);
							showWarning(msg);
						}
					}
					

					--depth;
					if(ret.toString().endsWith("\n"))
					{
						ret.append(getIndent(depth));
					}
					ret.append(parseRes.item).append("\n");
				}
				else if(ParseRes.CLOSED_TAG == parseRes.type)
				{
					ret.append(getIndent(depth)).append(parseRes.item).append("\n");
				}
				else if(ParseRes.TEXT == parseRes.type)
				{
					ret.append(parseRes.item);
				}
				parseRes = getParseRes(xml, parseRes.pos);
			}

			return ret.toString();
		}
		catch(Exception e)
		{
			
			JOptionPane.showMessageDialog(null,
				DEFAULT_MESSAGE,
				
				s_stringMgr.getString("xmlReformatter.xmlWarning2"), JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
		}
      finally
      {
         _message = DEFAULT_MESSAGE;
         _showWarningMessages = true;
      }
		return xml;

	}


   private static void showWarning(String message)
   {
      if(false == _showWarningMessages)
      {
         return;
      }

      Object[] options =
			{
				
				s_stringMgr.getString("xmlReformatter.yes"),
				
				s_stringMgr.getString("xmlReformatter.no")
			};


		int ret = JOptionPane.showOptionDialog(null,
		    		 
					s_stringMgr.getString("xmlReformatter.seeOtherErrs", message),
					 
                s_stringMgr.getString("xmlReformatter.xmlWarning5"),
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                null, options, options[0]);


      if(0 != ret)
      {
         _showWarningMessages = false;
      }

   }


	private static ParseRes getParseRes(String xml, int pos)
	{
		if(pos >= xml.length())
		{
			return null;
		}

		pos = moveOverWhiteSpaces(xml, pos);

		int ltIndex = xml.indexOf("<", pos);
		int gtIndex = xml.indexOf(">", pos);

		ParseRes ret = new ParseRes();

		if(pos == ltIndex)
		{
			ret.item = xml.substring(ltIndex, gtIndex+1);

			if(xml.length() > ltIndex+1 && xml.charAt(ltIndex+1) == '/')
			{
				ret.type = ParseRes.END_TAG;
			}
			else if(pos < gtIndex-1 && xml.charAt(gtIndex-1) == '/')
			{
				ret.type = ParseRes.CLOSED_TAG;
			}
			else
			{
				ret.type = ParseRes.BEGIN_TAG;
			}
			ret.pos = gtIndex+1;
		}
		else
		{
			
			
			if (ltIndex == -1) {
				int lengthToPrint = xml.length() - pos;
				if (lengthToPrint > 40)
					lengthToPrint = 40;

				
				_message = s_stringMgr.getString("xmlReformatter.malformedXmlAt", xml.substring(pos, pos + lengthToPrint));
				return null;
			}		

			ret.type = ParseRes.TEXT;
			ret.item = xml.substring(pos, ltIndex);
			ret.pos = ltIndex;
		}

		return ret;
	}

	private static int moveOverWhiteSpaces(String xml, int pos)
	{
		int ret = pos;

		while(Character.isWhitespace(xml.charAt(ret)))
		{
			++ret;
		}
		return ret;

	}

	private static String getIndent(int depth)
	{
		StringBuffer ret = new StringBuffer("");
		for(int i=0; i < depth; ++i)
		{
			ret.append("   ");
		}
		return ret.toString();
	}

	static class ParseRes
	{
		public static final int BEGIN_TAG = 0;
		public static final int END_TAG = 1;
		public static final int CLOSED_TAG = 2;
		public static final int TEXT = 3;

		String item;
		int type;
		int pos;
	}
}
