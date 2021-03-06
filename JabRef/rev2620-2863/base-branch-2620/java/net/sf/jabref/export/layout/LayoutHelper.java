
package net.sf.jabref.export.layout;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.Vector;

import wsi.ra.types.StringInt;



public class LayoutHelper {

    public static final int IS_LAYOUT_TEXT = 1;
    public static final int IS_SIMPLE_FIELD = 2;
    public static final int IS_FIELD_START = 3;
    public static final int IS_FIELD_END = 4;
    public static final int IS_OPTION_FIELD = 5;
    public static final int IS_GROUP_START = 6;
    public static final int IS_GROUP_END = 7;
    public static final int IS_ENCODING_NAME = 8;
    
    private static String currentGroup = null;
    
    private PushbackReader _in;
    private Vector<StringInt> parsedEntries = new Vector<StringInt>();

    private boolean _eof = false;
    private int line = 1;

    public LayoutHelper(Reader in)
    {
        if (in == null)
        {
            throw new NullPointerException();
        }

        _in = new PushbackReader(in);
    }

    public Layout getLayoutFromText(String classPrefix) throws Exception
    {
        parse();

        StringInt si;

        for (int i = 0; i < parsedEntries.size(); i++)
        {
            si = parsedEntries.get(i);

            if ((si.i == IS_SIMPLE_FIELD) || (si.i == IS_FIELD_START) ||
                    (si.i == IS_FIELD_END) || (si.i == IS_GROUP_START) ||
                    (si.i == IS_GROUP_END))
            {
                si.s = si.s.trim().toLowerCase();
            }
        }

        Layout layout = new Layout(parsedEntries, classPrefix);

        return layout;
    }

    
    public static String getCurrentGroup() {
        return currentGroup;
    }
    
    public static void setCurrentGroup(String newGroup) {
        currentGroup = newGroup;
    }
    
    private String getBracketedField(int _field) throws IOException
    {
        StringBuffer buffer = null;
        int c;
        boolean start = false;

        while (!_eof)
        {
            c = read();

            
            if (c == -1)
            {
                _eof = true;

                if (buffer != null)
                {
                    
                    parsedEntries.add(new StringInt(buffer.toString(), _field));

                    
                }

                
                
                return null;
            }

            if ((c == '{') || (c == '}'))
            {
                if (c == '}')
                {
                    if (buffer != null)
                    {
                        
                        parsedEntries.add(new StringInt(buffer.toString(),
                                _field));

                        
                        return null;
                    }
                }
                else
                {
                    start = true;
                }
            }
            else
            {
                if (buffer == null)
                {
                    buffer = new StringBuffer(100);
                }

                if (start)
                {
                    if (c == '}')
                    {
                    }
                    else
                    {
                        buffer.append((char) c);
                    }
                }
            }
        }

        return null;
    }

    
    private String getBracketedOptionField(int _field)
        throws IOException
    {
        StringBuffer buffer = null;
        int c;
        boolean start = false;
        String option = null;
        String tmp;

        while (!_eof)
        {
            c = read();

            
            if (c == -1)
            {
                _eof = true;

                if (buffer != null)
                {
                    
                    if (option != null)
                    {
                        tmp = buffer.toString() + "\n" + option;
                    }
                    else
                    {
                        tmp = buffer.toString();
                    }

                    parsedEntries.add(new StringInt(tmp, IS_OPTION_FIELD));

                    
                }

                return null;
            }

            if ((c == '{') || (c == '}') || (c == ']') || (c == '['))
            {
                if ((c == '}') || (c == ']'))
                {
                    
                    
                    
                    
                        if (c == ']' && buffer != null)
                        {
                    
                            option = buffer.toString();
                            buffer = null;
                            start = false;
                        }

                        
                        
                        
                        
                        
                        
                        
                        
                        else if (c == '}')
                        {
                           String parameter = buffer == null ? " " : buffer.toString();
                           if (option != null)
                            {
                                tmp = parameter + "\n" + option;
                            }
                            else
                            {
                                tmp = parameter;
                            }

                            
                            parsedEntries.add(new StringInt(tmp, IS_OPTION_FIELD));

                            return null;
                        }
                        
                     
                     
                     
                }
                else
                {
                    start = true;
                }
            }
            else
            {
                if (buffer == null)
                {
                    buffer = new StringBuffer(100);
                }

                if (start)
                {
                  
                    if ((c == '}') || (c == ']'))
                    {
                    }
                    else
                    {
                        
                        
                        
                        
                            buffer.append((char) c);
                        
                        
                    }
                }
            }
        }

        return null;
    }

    private Object parse() throws IOException {
		skipWhitespace();

		int c;

		StringBuffer buffer = null;
		boolean escaped = false;

		while (!_eof) {
			c = read();

			if (c == -1) {
				_eof = true;

				
				if (buffer != null)
					parsedEntries.add(new StringInt(buffer.toString(), IS_LAYOUT_TEXT));

				return null;
			}

			if ((c == '\\') && (peek() != '\\') && !escaped) {
				if (buffer != null) {
					parsedEntries.add(new StringInt(buffer.toString(), IS_LAYOUT_TEXT));

					buffer = null;
				}

				parseField();

				
				
				escaped = false;
			} else {
				if (buffer == null) {
					buffer = new StringBuffer(100);
				}

				if ((c != '\\') || escaped)
				{
					buffer.append((char) c);
				}

				escaped = (c == '\\') && !escaped;
			}
		}

		return null;
	}

    
    private void parseField() throws IOException
    {
        int c;
        StringBuffer buffer = null;
        String name;

        while (!_eof)
        {
            c = read();
            
            if (c == -1)
            {
                _eof = true;
            }

            if (!Character.isLetter((char) c))
            {
                unread(c);

                
                name = buffer != null ? buffer.toString() : "";

                
                buffer = null;

                if (name.charAt(0) == 'b')
                {
                    if (name.equalsIgnoreCase("begin"))
                    {
                        
                        getBracketedField(IS_FIELD_START);

                        return;
                    }
                    else if (name.equalsIgnoreCase("begingroup"))
                    {
                        
                        getBracketedField(IS_GROUP_START);
                        return;                    
                    }
                }
                else if (name.charAt(0) == 'f')
                {
                    if (name.equalsIgnoreCase("format"))
                    {
                        if (c == '[')
                        {
                            
                            
                            getBracketedOptionField(IS_OPTION_FIELD);

                            return;
                        }
                        else
                        {
                            
                            getBracketedField(IS_OPTION_FIELD);

                            return;
                        }
                    }
                }
                else if (name.charAt(0) == 'e')
                {
                    if (name.equalsIgnoreCase("end"))
                    {
                        
                        getBracketedField(IS_FIELD_END);
                        return;
                    }
                    else if (name.equalsIgnoreCase("endgroup"))
                    {
                        
                        getBracketedField(IS_GROUP_END);
                        return;
                    }
                    else if (name.equalsIgnoreCase("encoding"))
                    {
                        
                        
                        
                        parsedEntries.add(new StringInt(name, IS_ENCODING_NAME));
                        return;
                    }
                }
                
                
                parsedEntries.add(new StringInt(name, IS_SIMPLE_FIELD));

                
                return;
            }
            else
            {
                if (buffer == null)
                {
                    buffer = new StringBuffer(100);
                }

                buffer.append((char) c);
            }
        }
    }

    private int peek() throws IOException
    {
        int c = read();
        unread(c);

        return c;
    }

    private int read() throws IOException
    {
        int c = _in.read();

        if (c == '\n')
        {
            line++;
        }

        
        return c;
    }

    private void skipWhitespace() throws IOException
    {
        int c;

        while (true)
        {
            c = read();

            if ((c == -1) || (c == 65535))
            {
                _eof = true;

                return;
            }

            if (Character.isWhitespace((char) c))
            {
                continue;
            }
            else
            {
                unread(c);
            }

            break;
        }
    }

    private void unread(int c) throws IOException
    {
        if (c == '\n')
        {
            line--;
        }

        _in.unread(c);
    }
}
