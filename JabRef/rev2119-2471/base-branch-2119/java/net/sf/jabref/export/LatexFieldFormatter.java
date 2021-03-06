

package net.sf.jabref.export;

import java.util.* ;

import net.sf.jabref.* ;

public class LatexFieldFormatter implements FieldFormatter {

    StringBuffer sb;
    int col; 
    final int STARTCOL = 4;

    public String format(String text, String fieldName)
    throws IllegalArgumentException {

        if (Globals.prefs.putBracesAroundCapitals(fieldName) && !Globals.BIBTEX_STRING.equals(fieldName)) {
            text = Util.putBracesAroundCapitals(text);
        }

    
    
    boolean resolveStrings = true;
    if (Globals.prefs.getBoolean("resolveStringsAllFields")) {
        
        String[] exceptions = Globals.prefs.getStringArray("doNotResolveStringsFor");
        for (int i = 0; i < exceptions.length; i++) {
            if (exceptions[i].equals(fieldName)) {
                resolveStrings = false;
                break;
            }
        }
    }
    else {
        
        resolveStrings = BibtexFields.isStandardField(fieldName)
                || Globals.BIBTEX_STRING.equals(fieldName);
    }
    if (!resolveStrings) {
          int brc = 0;
          boolean ok = true;
          for (int i=0; i<text.length(); i++) {
            char c = text.charAt(i);
            
            if (c == '{') brc++;
            if (c == '}') brc--;
            if (brc < 0) {
              ok = false;
              break;
            }
          }
          if (brc > 0)
            ok = false;
          if (!ok)
            throw new IllegalArgumentException("Curly braces { and } must be balanced.");

          sb = new StringBuffer(Globals.getOpeningBrace());
          
          
          
          
          if (!Globals.prefs.isNonWrappableField(fieldName))
            sb.append(Util.wrap2(text, GUIGlobals.LINE_LENGTH));
          else
            sb.append(text);

          sb.append(Globals.getClosingBrace());

          return sb.toString();
    }

    sb = new StringBuffer();
    int pivot = 0, pos1, pos2;
    col = STARTCOL;
    
    
    
    
    checkBraces(text);


        while (pivot < text.length()) {
            int goFrom = pivot;
            pos1 = pivot;
            while (goFrom == pos1) {
                pos1 = text.indexOf('#', goFrom);
                if ((pos1 > 0) && (text.charAt(pos1 - 1) == '\\')) {
                    goFrom = pos1 + 1;
                    pos1++;
                } else
                    goFrom = pos1 - 1; 
            }

            if (pos1 == -1) {
                pos1 = text.length(); 
                pos2 = -1;
            } else {
                pos2 = text.indexOf('#', pos1 + 1);
                
                if (pos2 == -1) {
                    throw new IllegalArgumentException
                            (Globals.lang("The # character is not allowed in BibTeX fields") + ".\n" +
                                    Globals.lang("In JabRef, use pairs of # characters to indicate "
                                            + "a string.") + "\n" +
                                    Globals.lang("Note that the entry causing the problem has been selected."));
                }
            }

            if (pos1 > pivot)
                writeText(text, pivot, pos1);
            if ((pos1 < text.length()) && (pos2 - 1 > pos1))
                
                
                
                writeStringLabel(text, pos1 + 1, pos2, (pos1 == pivot),
                        (pos2 + 1 == text.length()));

            if (pos2 > -1) pivot = pos2 + 1;
            else pivot = pos1 + 1;
            
        }

        if (!Globals.prefs.isNonWrappableField(fieldName))
            return Util.wrap2(sb.toString(), GUIGlobals.LINE_LENGTH);
          else
            return sb.toString();


    }

    private void writeText(String text, int start_pos,
                           int end_pos) {
    
    sb.append(Globals.getOpeningBrace());
    boolean escape = false, inCommandName = false, inCommand = false,
        inCommandOption = false;
    int nestedEnvironments = 0;
    StringBuffer commandName = new StringBuffer();
    char c;
    for (int i=start_pos; i<end_pos; i++) {
        c = text.charAt(i);

        
        if (Character.isLetter(c) && (escape || inCommandName)) {
            inCommandName = true;
            if (!inCommandOption)
                commandName.append((char)c);
        }
        else if (Character.isWhitespace(c) && (inCommand || inCommandOption)) {
            
        }
        else if (inCommandName) {
            
            
            if (c == '[') {
                inCommandOption = true;
            }
            
            else if (inCommandOption && (c == ']'))
                inCommandOption = false;
            
            else if (!inCommandOption && (c == '{')) {
                
                inCommandName = false;
                inCommand = true;
            }
            
            else {
                

                commandName.delete(0, commandName.length());
                inCommandName = false;
            }
        }
        
        if (inCommand && (c == '}')) {
            
            
            if (commandName.toString().equals("begin")) {
                nestedEnvironments++;
            }
            if (nestedEnvironments > 0 && commandName.toString().equals("end")) {
                nestedEnvironments--;
            }
            
            
            commandName.delete(0, commandName.length());
            inCommand = false;
        }

        
        
        if ((c == '&') && !escape && 
            !(inCommand && commandName.toString().equals("url")) && 
            (nestedEnvironments == 0)) {
            sb.append("\\&");
        }
        else
            sb.append(c);
        escape = (c == '\\');
    }
    sb.append(Globals.getClosingBrace());
    }

    private void writeStringLabel(String text, int start_pos, int end_pos,
                                  boolean first, boolean last) {
    
    
    putIn((first ? "" : " # ") + text.substring(start_pos, end_pos)
          + (last ? "" : " # "));
    }

    private void putIn(String s) {
    sb.append(Util.wrap2(s, GUIGlobals.LINE_LENGTH));
    }


    private void checkBraces(String text) throws IllegalArgumentException {

    Vector
        left = new Vector(5, 3),
        right = new Vector(5, 3);
    int current = -1;

    
    while ((current = text.indexOf('{', current+1)) != -1)
        left.add(new Integer(current));
    while ((current = text.indexOf('}', current+1)) != -1)
        right.add(new Integer(current));

    
    if ((right.size() > 0) && (left.size() == 0))
        throw new IllegalArgumentException
        ("'}' character ends string prematurely.");
    if ((right.size() > 0) && (((Integer)right.elementAt(0)).intValue()
                   < ((Integer)left.elementAt(0)).intValue()))
        throw new IllegalArgumentException
        ("'}' character ends string prematurely.");
    if (left.size() != right.size())
        throw new IllegalArgumentException
        ("Braces don't match.");

    }

}
