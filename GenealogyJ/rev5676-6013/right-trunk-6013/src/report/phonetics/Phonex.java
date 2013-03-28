package phonetics;



public class Phonex implements Phonetics {

    static public final char[] CHAR_MAPPING = "01230120022455012623010202".toCharArray();

    private int maxCodeLen = 4;

    
    public String encode(String txt) {

        if (txt == null || txt.length() == 0)
            return null;

        
        txt = txt.toUpperCase();

        
        int current = 0;
        StringBuffer word = new StringBuffer(txt);
        while (current < word.length()) {
            if (!Character.isLetter(word.charAt(current)))
                word = word.deleteCharAt(current);
            else
                current++;
        }

        

        
        while (word.toString().endsWith("S"))
            word.deleteCharAt(word.length() - 1);

        
        while (word.toString().startsWith("H"))
            word.deleteCharAt(0);

        
        if (word.length() == 0)
            return null;

        char[] input = word.toString().toCharArray();
        StringBuffer processed = new StringBuffer();

        
        switch (input[0]) {
            case 'K' :
                if (input.length > 1 && input[1] == 'N')
                    processed.append(input, 1, input.length - 1);
                else {
                    input[0] = 'C';
                    processed.append(input);
                }
                break;
            case 'P' :
                if (input.length > 1 && input[1] == 'H') {
                    input[1] = 'F';
                    processed.append(input, 1, input.length - 1);
                } else {
                    input[0] = 'B';
                    processed.append(input);
                }
                break;
            case 'W' :
                if (input.length > 1 && input[1] == 'R')
                    processed.append(input, 1, input.length - 1);
                else {
                    processed.append(input);
                }
                break;
            case 'E' :
            case 'I' :
            case 'O' :
            case 'U' :
            case 'Y' :
                input[0] = 'A';
                processed.append(input);
                break;
            case 'V' :
                input[0] = 'F';
                processed.append(input);
                break;
            case 'Q' :
                input[0] = 'C';
                processed.append(input);
                break;
            case 'J' :
                input[0] = 'G';
                processed.append(input);
                break;
            case 'Z' :
                input[0] = 'S';
                processed.append(input);
                break;
            default :
                processed.append(input);
        }

        

        String processedString = processed.toString();

        StringBuffer code = new StringBuffer(maxCodeLen);
        char last, mapped;
        int incount = 1, count = 1;
        code.append(processedString.charAt(0));
        last = getCode(processedString, 0);
        while ((incount < processedString.length()) && (mapped = getCode(processedString, incount++)) != 0 && (count < maxCodeLen)) {
            if ((mapped != '0') && (mapped != last)) {
                code.append(mapped);
            }
            last = mapped;
        }

        
        while (code.length() < maxCodeLen)
            code.append('0');

        return code.toString().substring(0, maxCodeLen);
    }

    
    private char getCode(String s, int location) {
        Character a = null, b = null, c = null;

        if (location - 1 >= 0 && location - 1 < s.length())
            a = new Character(s.charAt(location - 1));

        if (location >= 0 && location < s.length())
            b = new Character(s.charAt(location));

        if (location + 1 >= 0 && location + 1 < s.length())
            c = new Character(s.charAt(location + 1));

        return getCode(a, b, c);
    }

    
    private char getCode(Character prev, Character c, Character next) {
        if (c == null || !Character.isLetter(c.charValue())) {
            return '0';
        } else {
            
            
            if ((c.charValue() == 'D' || c.charValue() == 'T') && (next != null && next.charValue() == 'C'))
                return '0';
            
            else if ((c.charValue() == 'L' || c.charValue() == 'R') && (next == null || next.charValue() == 'A' || next.charValue() == 'E' || next.charValue() == 'I' || next.charValue() == 'O' || next.charValue() == 'U'))
                return '0';
            
            else if ((c.charValue() == 'D' || c.charValue() == 'G') && (prev != null && (prev.charValue() == 'M' || prev.charValue() == 'N')))
                return '0';
            else {
                int loc = Character.toUpperCase(c.charValue()) - 'A';
                if (loc < 0 || loc > (CHAR_MAPPING.length - 1))
                    return '0';
                return CHAR_MAPPING[loc];
            }
        }
    }

    
    public int getMaxCodeLen() {
        return maxCodeLen;
    }

    
    public void setMaxCodeLen(int maxCodeLen) {
        this.maxCodeLen = maxCodeLen;
    }
    
    public String toString() {
      return "Phonex";
    }
}