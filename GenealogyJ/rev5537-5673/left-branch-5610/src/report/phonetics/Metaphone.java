package phonetics;



public class Metaphone implements Phonetics {

    private static String vowels = "AEIOU";
    private static String frontv = "EIY";
    private static String varson = "CSPTG";
    private static final int maxCodeLen = 4;

    
    public String encode(String txt) {
        int mtsz = 0;
        boolean hard = false;
        if ((txt == null) || (txt.length() == 0))
            return null;

        
        if (!Character.isLetter(txt.charAt(0)))
            return encode(txt.substring(1));

        
        if (txt.length() == 1)
            return txt.toUpperCase();
        
        char[] inwd = txt.toUpperCase().toCharArray();
        
        String tmpS;
        StringBuffer local = new StringBuffer(40); 
        StringBuffer code = new StringBuffer(10); 
        
        switch (inwd[0]) {
            case 'K' :
            case 'G' :
            case 'P' : 
                if (inwd[1] == 'N')
                    local.append(inwd, 1, inwd.length - 1);
                else
                    local.append(inwd);
                break;
            case 'A' : 
                if (inwd[1] == 'E')
                    local.append(inwd, 1, inwd.length - 1);
                else
                    local.append(inwd);
                break;
            case 'W' : 
                if (inwd[1] == 'R') { 
                    local.append(inwd, 1, inwd.length - 1);
                    break;
                }
                if (inwd[1] == 'H') {
                    local.append(inwd, 1, inwd.length - 1);
                    local.setCharAt(0, 'W'); 
                } else
                    local.append(inwd);
                break;
            case 'X' : 
                inwd[0] = 'S';
                local.append(inwd);
                break;
            default :
                local.append(inwd);
        } 
        int wdsz = local.length();
        int n = 0;
        while ((mtsz < maxCodeLen) && 
        (n < wdsz)) {
            char symb = local.charAt(n);
            
            if ((symb != 'C') && (n > 0) && (local.charAt(n - 1) == symb))
                n++;
            else { 
                switch (symb) {
                    case 'A' :
                    case 'E' :
                    case 'I' :
                    case 'O' :
                    case 'U' :
                        if (n == 0) {
                            code.append(symb);
                            mtsz++;
                        }
                        break; 
                    case 'B' :
                        if ((n > 0) && !(n + 1 == wdsz) && 
                        (local.charAt(n - 1) == 'M')) {
                            code.append(symb);
                        } else
                            code.append(symb);
                        mtsz++;
                        break;
                    case 'C' : 
                        
                        if ((n > 0) && (local.charAt(n - 1) == 'S') && (n + 1 < wdsz) && (frontv.indexOf(local.charAt(n + 1)) >= 0)) {
                            break;
                        }
                        tmpS = local.toString();
                        if (tmpS.indexOf("CIA", n) == n) { 
                            code.append('X');
                            mtsz++;
                            break;
                        }
                        if ((n + 1 < wdsz) && (frontv.indexOf(local.charAt(n + 1)) >= 0)) {
                            code.append('S');
                            mtsz++;
                            break; 
                        }
                        if ((n > 0) && (tmpS.indexOf("SCH", n - 1) == n - 1)) { 
                            code.append('K');
                            mtsz++;
                            break;
                        }
                        if (tmpS.indexOf("CH", n) == n) { 
                            if ((n == 0) && (wdsz >= 3) && 
                            (vowels.indexOf(local.charAt(2)) < 0)) {
                                code.append('K');
                            } else {
                                code.append('X'); 
                            }
                            mtsz++;
                        } else {
                            code.append('K');
                            mtsz++;
                        }
                        break;
                    case 'D' :
                        if ((n + 2 < wdsz) && 
                        (local.charAt(n + 1) == 'G') && (frontv.indexOf(local.charAt(n + 2)) >= 0)) {
                            code.append('J');
                            n += 2;
                        } else {
                            code.append('T');
                        }
                        mtsz++;
                        break;
                    case 'G' : 
                        if ((n + 2 == wdsz) && (local.charAt(n + 1) == 'H'))
                            break;
                        if ((n + 2 < wdsz) && (local.charAt(n + 1) == 'H') && (vowels.indexOf(local.charAt(n + 2)) < 0))
                            break;
                        tmpS = local.toString();
                        if ((n > 0) && (tmpS.indexOf("GN", n) == n) || (tmpS.indexOf("GNED", n) == n))
                            break; 
                        if ((n > 0) && (local.charAt(n - 1) == 'G'))
                            hard = true;
                        else
                            hard = false;
                        if ((n + 1 < wdsz) && (frontv.indexOf(local.charAt(n + 1)) >= 0) && (!hard))
                            code.append('J');
                        else
                            code.append('K');
                        mtsz++;
                        break;
                    case 'H' :
                        if (n + 1 == wdsz)
                            break; 
                        if ((n > 0) && (varson.indexOf(local.charAt(n - 1)) >= 0))
                            break;
                        if (vowels.indexOf(local.charAt(n + 1)) >= 0) {
                            code.append('H');
                            mtsz++; 
                        }
                        break;
                    case 'F' :
                    case 'J' :
                    case 'L' :
                    case 'M' :
                    case 'N' :
                    case 'R' :
                        code.append(symb);
                        mtsz++;
                        break;
                    case 'K' :
                        if (n > 0) { 
                            if (local.charAt(n - 1) != 'C') {
                                code.append(symb);
                            }
                        } else
                            code.append(symb); 
                        mtsz++;
                        break;
                    case 'P' :
                        if ((n + 1 < wdsz) && 
                        (local.charAt(n + 1) == 'H'))
                            code.append('F');
                        else
                            code.append(symb);
                        mtsz++;
                        break;
                    case 'Q' :
                        code.append('K');
                        mtsz++;
                        break;
                    case 'S' :
                        tmpS = local.toString();
                        if ((tmpS.indexOf("SH", n) == n) || (tmpS.indexOf("SIO", n) == n) || (tmpS.indexOf("SIA", n) == n))
                            code.append('X');
                        else
                            code.append('S');
                        mtsz++;
                        break;
                    case 'T' :
                        tmpS = local.toString(); 
                        if ((tmpS.indexOf("TIA", n) == n) || (tmpS.indexOf("TIO", n) == n)) {
                            code.append('X');
                            mtsz++;
                            break;
                        }
                        if (tmpS.indexOf("TCH", n) == n)
                            break;
                        
                        if (tmpS.indexOf("TH", n) == n)
                            code.append('0');
                        else
                            code.append('T');
                        mtsz++;
                        break;
                    case 'V' :
                        code.append('F');
                        mtsz++;
                        break;
                    case 'W' :
                    case 'Y' : 
                        if ((n + 1 < wdsz) && (vowels.indexOf(local.charAt(n + 1)) >= 0)) {
                            code.append(symb);
                            mtsz++;
                        }
                        break;
                    case 'X' :
                        code.append('K');
                        code.append('S');
                        mtsz += 2;
                        break;
                    case 'Z' :
                        code.append('S');
                        mtsz++;
                        break;
                } 
                n++;
            } 
            if (mtsz > 4)
                code.setLength(4);
        }
        return code.toString();
    }
    
    public String toString() {
      return "Metaphone";
    }
}