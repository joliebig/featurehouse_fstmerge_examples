package phonetics;



public class DoubleMetaphone implements Phonetics {

    private int current;
    private int encodeLimit = 4;
    private StringBuffer primary = new StringBuffer();
    private StringBuffer alternate = new StringBuffer();
    private String input;

    private final static char[] vowels = { 'A', 'E', 'I', 'O', 'U', 'Y' };
    private final static char[] AEOU = { 'A', 'E', 'O', 'U' };
    private final static char[] AO = "AO".toCharArray();
    private final static char[] BDH = { 'B', 'D', 'H' };
    private final static char[] BFHLMNRVW_ = "BFHLMNRVW ".toCharArray();
    private final static char[] BH = { 'B', 'H' };
    private final static char[] BKLMNSTZ = "LTKSNMBZ".toCharArray();
    private final static char[] BP = "BP".toCharArray();
    private final static char[] CGQ = { 'C', 'G', 'Q' };
    private final static char[] CGLRT = { 'C', 'G', 'L', 'R', 'T' };
    private final static char[] CKQ = { 'C', 'K', 'Q' };
    private final static char[] CX = "CX".toCharArray();
    private final static char[] DT = "DT".toCharArray();
    private final static char[] EI = { 'E', 'I' };
    private final static char[] EIY = { 'E', 'I', 'Y' };
    private final static char[] EHI = { 'I', 'E', 'H' };
    private final static char[] KLS = "KLS".toCharArray();
    private final static char[] LMNW = "LMNW".toCharArray();
    private final static char[] ST = { 'S', 'T' };
    private final static char[] SZ = "SZ".toCharArray();
    private final static String[] AggiOggi = { "AGGI", "OGGI" };
    private final static String[] AiOi = { "AI", "OI" };
    private final static String[] AlleIllaIllo = { "ILLO", "ILLA", "ALLE" };
    private final static String[] AmOm = { "OM", "AM" };
    private final static String[] AsOs = { "AS", "OS" };
    private final static String[] ArchitOrchesOrchid = { "ARCHIT", "ORCHES", "ORCHID" };
    private final static String[] AuOu = { "AU", "OU" };
    private final static String[] BacherMacher = { "BACHER", "MACHER" };
    private final static String[] CeCiCy = { "CI", "CE", "CY" };
    private final static String[] CeCi = { "CE", "CI" };
    private final static String[] CiaCieCio = { "CIO", "CIE", "CIA" };
    private final static String[] CkCgCq = { "CK", "CG", "CQ" };
    private final static String[] DangerMangerRanger = { "DANGER", "RANGER", "MANGER" };
    private final static String[] DdDt = { "DD", "DT" };
    private final static String[] EauIau = { "IAU", "EAU" };
    private final static String[] EbEiElEpErEsEyIbIlInIe = { "ES", "EP", "EB", "EL", "EY", "IB", "IL", "IN", "IE", "EI", "ER" };
    private final static String[] EdEmEnErOoUy = { "OO", "ER", "EN", "UY", "ED", "EM" };
    private final static String[] EnEr = { "ER", "EN" };
    private final static String[] EwskiEwskyOwskiOwsky = { "EWSKI", "EWSKY", "OWSKI", "OWSKY" };
    private final static String[] GnKnPnPsWr = { "GN", "KN", "PN", "WR", "PS" };
    private final static String[] HaracHaris = { "HARAC", "HARIS" };
    private final static String[] HeimHoekHolmHolz = { "HEIM", "HOEK", "HOLM", "HOLZ" };
    private final static String[] HemHiaHorHym = { "HOR", "HYM", "HIA", "HEM" };
    private final static String[] IslYsl = { "ISL", "YSL" };
    private final static String[] MaMe = { "ME", "MA" };
    private final static String[] OgyRgy = { "RGY", "OGY" };
    private final static String[] SiaSio = { "SIO", "SIA" };
    private final static String[] TiaTch = { "TIA", "TCH" };
    private final static String[] UcceeUcces = { "UCCEE", "UCCES" };
    private final static String[] Van_Von_ = { "VAN ", "VON " };
    private final static String[] WiczWitz = { "WICZ", "WITZ" };
    private final static String[] ZaZiZo = { "ZO", "ZI", "ZA" };

    
    public DoubleMetaphone() {
    }

    public String getPrimary() {
        return primary.toString();
    }

    public StringBuffer getPrimaryBuffer() {
        return primary;
    }

    public String getAlternate() {
        return alternate.toString();
    }

    public StringBuffer getAlternateBuffer() {
        return alternate;
    }

    public int getEncodeLimit() {
        return encodeLimit;
    }

    public boolean setEncodeLimit(int newLimit) {
        if (newLimit < 1)
            return false;
        encodeLimit = newLimit;
        return true;
    }

    void setInput(String in) {
        if (in != null) {
            input = in.toUpperCase() + "     ";
        } else {
            input = "";
        }
    }

    void add(char ch) {
        add(ch, ch);
    }

    void add(char primaryChar, char alternateChar) {
        primary.append(primaryChar);
        alternate.append(alternateChar);
    }

    boolean charAt(int index, char[] list) {
        if (index < 0 || index >= input.length())
            return false;
        char value = input.charAt(index);
        for (int i = 0; i < list.length; i++) {
            if (value == list[i])
                return true;
        }
        return false;
    }

    boolean stringAt(int start, int length, String str) {
        String[] list = new String[1];
        list[0] = str;
        return stringAt(start, length, list);
    }

    boolean stringAt(int start, int length, String[] list) {
        if (length <= 0)
            return false;
        for (int i = 0; i < list.length; i++) {
            if (input.regionMatches(start, list[i], 0, length))
                return true;
        }
        return false;
    }

    boolean isVowel(int index) {
        return charAt(index, vowels);
    }

    boolean isSlavoGermanic() {
        if ((input.indexOf('W') > -1) || (input.indexOf('K') > -1) || (input.indexOf("CZ") > -1) || (input.indexOf("WITZ") > -1)) {
            return true;
        }
        return false;
    }

    void addCode(char ch, char code) {
        add(code);
        current++;
        if (input.charAt(current) == ch)
            current++;
    }

    public String encode(String in) {
        if (in == null || in.length() == 0)
            return null;

        primary.delete(0, primary.length());
        alternate.delete(0, alternate.length());
        int length = in.length();
        if (length < 1)
            return "";
        int last = length - 1; 
        setInput(in);
        current = 0;

        
        if (stringAt(0, 2, GnKnPnPsWr))
            current++;

        
        if (input.startsWith("X")) {
            add('S'); 
            current++;
        }

        while (primary.length() < encodeLimit || alternate.length() < encodeLimit) {
            if (current >= length)
                break;

            switch (input.charAt(current)) {
                case 'A' :
                case 'E' :
                case 'I' :
                case 'O' :
                case 'U' :
                case 'Y' :
                    if (current == 0)
                        add('A'); 
                    current++;
                    break;

                case 'B' :
                    
                    addCode('B', 'P');
                    break;

                case '\u' : 
                    add('S');
                    current++;
                    
                    break;

                case 'C' :
                    
                    if ((current > 1) && !isVowel(current - 2) && input.regionMatches(current - 1, "ACH", 0, 3) && (input.charAt(current + 2) != 'I' && input.charAt(current + 2) != 'E' || stringAt(current - 2, 6, BacherMacher))) {
                        add('K');
                        current += 2;
                        break;
                    }

                    
                    if (current == 0 && input.regionMatches(current, "CAESAR", 0, 6)) {
                        add('S');
                        current += 2;
                        break;
                    }

                    
                    if (input.regionMatches(current, "CHIA", 0, 4)) {
                        add('K');
                        current += 2;
                        break;
                    }

                    if (input.regionMatches(current, "CH", 0, 2)) {
                        
                        if (current > 0 && input.regionMatches(current, "CHAE", 0, 4)) {
                            add('K', 'X');
                            current += 2;
                            break;
                        }

                        
                        if (current == 0 && (stringAt(current + 1, 5, HaracHaris) || stringAt((current + 1), 3, HemHiaHorHym)) && !input.regionMatches(0, "CHORE", 0, 5)) {
                            add('K');
                            current += 2;
                            break;
                        }

                        
                        if ((stringAt(0, 4, Van_Von_) || input.regionMatches(0, "SCH ", 0, 3)) 
                        || stringAt(0, 6, ArchitOrchesOrchid)
                        || charAt(current + 2, ST)
                        || ((charAt(current - 1, AEOU) || current == 0) 
                        && charAt(current + 2, BFHLMNRVW_))) {
                            add('K');
                        } else {
                            if (current > 0) {
                                if (input.regionMatches(0, "MC", 0, 2)) {
                                    
                                    add('K');
                                } else {
                                    add('X', 'K');
                                }
                            } else {
                                add('X');
                            }
                        }
                        current += 2;
                        break;
                    }

                    
                    if (input.regionMatches(current, "CZ", 0, 2) && !input.regionMatches(current - 2, "WICZ", 0, 4)) {
                        add('S', 'X');
                        current += 2;
                        break;
                    }

                    
                    if (input.regionMatches(current + 1, "CIA", 0, 3)) {
                        add('X');
                        current += 3;
                        break;
                    }

                    
                    if (input.regionMatches(current, "CC", 0, 2) && !((current == 1) && (input.charAt(0) == 'M'))) {
                        
                        if (charAt(current + 2, EHI) && !input.regionMatches(current + 2, "HU", 0, 2)) {
                            
                            if (((current == 1) && (input.charAt(current - 1) == 'A')) || stringAt(current - 1, 5, UcceeUcces)) {
                                add('K');
                                add('S');
                            } else { 
                                add('X');
                            }
                            current += 3;
                            break;
                        } else { 
                            add('K');
                            current += 2;
                            break;
                        }
                    }

                    if (stringAt(0, 2, CkCgCq)) {
                        add('K');
                        current += 2;
                        break;
                    }

                    if (stringAt(0, 2, CeCiCy)) {
                        
                        if (stringAt(0, 3, CiaCieCio)) {
                            add('S', 'X');
                        } else {
                            add('S');
                        }
                        current += 2;
                        break;
                    }

                    
                    add('K');

                    
                    if (charAt(current + 1, CGQ)) {
                        current += 3;
                    } else {
                        if (charAt(current + 1, CKQ) && !stringAt(current + 1, 2, CeCi)) {
                            current += 2;
                        } else {
                            current++;
                        }
                    }
                    break;

                case 'D' :
                    if (input.regionMatches(current, "DG", 0, 2)) {
                        if (charAt(current + 2, EIY)) {
                            
                            add('J');
                            current += 3;
                            break;
                        } else {
                            
                            add('T');
                            add('K');
                            current += 2;
                            break;
                        }
                    }

                    if (stringAt(current, 2, DdDt)) {
                        add('T');
                        current += 2;
                        break;
                    }

                    
                    add('T');
                    current++;
                    break;

                case 'F' : 
                    addCode('F', 'F');
                    break;

                case 'G' :
                    if (input.charAt(current + 1) == 'H') {
                        if (current > 0 && !isVowel(current - 1)) {
                            add('K');
                            current += 2;
                            break;
                        }

                        if (current < 3) {
                            
                            if (current == 0) {
                                if (input.charAt(current + 2) == 'I') {
                                    add('J');
                                } else {
                                    add('K');
                                }
                                current += 2;
                                break;
                            }
                        }
                        
                        if ((current > 1 && charAt(current - 2, BDH)) 
                        || (current > 2 && charAt(current - 3, BDH)) 
                        || (current > 3 && charAt(current - 4, BH))) {
                            current += 2;
                            break;
                        } else {
                            
                            if (current > 2 && input.charAt(current - 1) == 'U' && charAt(current - 3, CGLRT)) {
                                add('F');
                            } else {
                                if (current > 0 && input.charAt(current - 1) != 'I') {
                                    add('K');
                                }
                            }
                            current += 2;
                            break;
                        }
                    }

                    boolean slavoGermanic = isSlavoGermanic();
                    if (input.charAt(current + 1) == 'N') {
                        if (current == 1 && isVowel(0) && !slavoGermanic) {
                            primary.append('K');
                            add('N');
                        } else {
                            
                            if (!input.regionMatches(current + 2, "EY", 0, 2) && (input.charAt(current + 1) != 'Y') && !slavoGermanic) {
                                alternate.append('K');
                                add('N');
                            } else {
                                add('K');
                                add('N');
                            }
                            current += 2;
                            break;
                        }
                    }

                    
                    if (input.regionMatches(current + 1, "LI", 0, 2) && !slavoGermanic) {
                        primary.append('K');
                        add('L');
                        current += 2;
                        break;
                    }

                    
                    if ((current == 0) && (input.charAt(current + 1) == 'Y' || stringAt(current + 1, 2, EbEiElEpErEsEyIbIlInIe))) {
                        add('K', 'J');
                        current += 2;
                        break;
                    }

                    
                    if ((input.regionMatches(current + 1, "ER", 0, 2) || input.charAt(current + 1) == 'Y') && !stringAt(0, 6, DangerMangerRanger) && !charAt(current - 1, EI) && !stringAt(current - 1, 3, OgyRgy)) {
                        add('K', 'J');
                        current += 2;
                        break;
                    }

                    
                    if (charAt(current + 1, EIY) || stringAt(current - 1, 4, AggiOggi)) {
                        
                        if ((stringAt(0, 4, Van_Von_) || input.regionMatches(0, "SCH", 0, 3)) || input.regionMatches(current + 1, "ET", 0, 2)) {
                            add('K');
                        } else {
                            
                            if (input.regionMatches(current + 1, "IER ", 0, 4)) {
                                add('J');
                            } else {
                                add('J', 'K');
                            }
                            current += 2;
                            break;
                        }
                    }

                    if (input.charAt(current + 1) == 'G') {
                        current += 2;
                    } else {
                        current++;
                    }
                    add('K');
                    break;

                case 'H' :
                    
                    if ((current == 0 || isVowel(current - 1)) && isVowel(current + 1)) {
                        add('H');
                        current += 2;
                    } else { 
                        current++;
                    }
                    break;

                case 'J' :
                    
                    if (stringAt(current, 4, "JOSE") || stringAt(0, 4, "SAN ")) {
                        if ((current == 0 && (input.charAt(current + 4) == ' ')) || stringAt(0, 4, "SAN ")) {
                            add('H');
                        } else {
                            add('J', 'H');
                        }
                        current += 1;
                        break;
                    }

                    if (current == 0 && !stringAt(current, 4, "JOSE")) {
                        add('J', 'A'); 
                    } else {
                        
                        if (isVowel(current - 1) && !isSlavoGermanic() && ((input.charAt(current + 1) == 'A') || (input.charAt(current + 1) == 'O'))) {
                            add('J', 'H');
                        } else {
                            if (current == last) {
                                add('J', ' ');
                            } else {
                                if (!charAt(current + 1, BKLMNSTZ) && !charAt(current - 1, KLS)) {
                                    add('J');
                                }
                            }
                        }
                    }

                    current++;
                    if (input.charAt(current) == 'J')
                        current++; 
                    break;

                case 'K' : 
                    addCode('K', 'K');
                    break;

                case 'L' :
                    if (input.charAt(current + 1) == 'L') {
                        
                        if (((current == (length - 3)) && stringAt(current - 1, 4, AlleIllaIllo)) || ((stringAt((last - 1), 2, AsOs) || charAt(last, AO)) && stringAt(current - 1, 4, "ALLE"))) {
                            primary.append('L');
                            current += 2;
                            break;
                        }
                        current += 2;
                    } else {
                        current++;
                    }
                    add('L');
                    break;

                case 'M' :
                    if ((stringAt(current - 1, 3, "UMB") && (((current + 1) == last) || stringAt(current + 2, 2, "ER"))) 
                    || (input.charAt(current + 1) == 'M')) {
                        current += 2;
                    } else {
                        current++;
                    }
                    add('M');
                    break;

                case 'N' : 
                    addCode('N', 'N');
                    break;

                case '\u': 
                    current++;
                    add('N');
                    break;

                case 'P' :
                    if (input.charAt(current + 1) == 'H') {
                        add('F');
                        current += 2;
                        break;
                    }

                    
                    if (charAt(current + 1, BP))
                        current += 2;
                    else
                        current++;
                    add('P');
                    break;

                case 'Q' : 
                    addCode('Q', 'K');
                    break;

                case 'R' :
                    
                    if ((current == last) && !isSlavoGermanic() && stringAt(current - 2, 2, "IE") && !stringAt(current - 4, 2, MaMe)) {
                        alternate.append('R');
                    } else {
                        add('R');
                    }

                    current++;
                    if (input.charAt(current) == 'R')
                        current++; 
                    break;

                case 'S' :
                    
                    if (stringAt(current - 1, 3, IslYsl)) {
                        current++;
                        break;
                    }

                    
                    if ((current == 0) && stringAt(current, 5, "SUGAR")) {
                        add('X', 'S');
                        current++;
                        break;
                    }

                    if (stringAt(current, 2, "SH")) {
                        
                        if (stringAt(current + 1, 4, HeimHoekHolmHolz)) {
                            add('S');
                        } else {
                            add('X');
                        }
                        current += 2;
                        break;
                    }

                    
                    if (stringAt(current, 3, SiaSio) || stringAt(current, 4, "SIAN")) {
                        if (!isSlavoGermanic()) {
                            add('S', 'X');
                        } else {
                            add('S');
                        }
                        current += 3;
                        break;
                    }

                    
                    
                    if ((current == 0 && charAt(current + 1, LMNW)) || input.charAt(current + 1) == 'Z') {
                        add('S', 'X');
                        if (input.charAt(current + 1) == 'Z') {
                            current += 2;
                        } else {
                            current++;
                        }
                        break;
                    }

                    if (stringAt(current, 2, "SC")) {
                        
                        if (input.charAt(current + 2) == 'H') {
                            
                            if (stringAt(current + 3, 2, EdEmEnErOoUy)) {
                                
                                if (stringAt((current + 3), 2, EnEr)) {
                                    add('X', 'S');
                                    alternate.append('K');
                                } else {
                                    add('S');
                                    add('K');
                                }
                                current += 3;
                                break;
                            } else {
                                if (current == 0 && !isVowel(3) && input.charAt(3) != 'W') {
                                    add('X', 'S');
                                } else {
                                    add('X');
                                }
                                current += 3;
                                break;
                            }
                        }

                        if (charAt(current + 2, EIY)) {
                            add('S');
                            current += 3;
                            break;
                        }

                        
                        add('S');
                        add('K');
                        current += 3;
                        break;
                    }

                    
                    if (current == last && stringAt(current - 2, 2, AiOi)) {
                        alternate.append('S');
                    } else {
                        add('S');
                    }

                    if (charAt(current + 1, SZ)) {
                        current += 2;
                    } else {
                        current++;
                    }
                    break;

                case 'T' :
                    if (stringAt(current, 4, "TION")) {
                        add('X');
                        current += 3;
                        break;
                    }

                    if (stringAt(current, 3, TiaTch)) {
                        add('X');
                        current += 3;
                        break;
                    }

                    if (stringAt(current, 2, "TH") || stringAt(current, 3, "TTH")) {
                        
                        if (stringAt(current + 2, 2, AmOm) || stringAt(0, 4, Van_Von_) || stringAt(0, 3, "SCH")) {
                            add('T');
                        } else {
                            add('0', 'T');
                        }
                        current += 2;
                        break;
                    }

                    if (charAt(current + 1, DT))
                        current += 2;
                    else
                        current++;
                    add('T');
                    break;

                case 'V' : 
                    addCode('V', 'F');
                    break;

                case 'W' :
                    
                    if (stringAt(current, 2, "WR")) {
                        add('R');
                        current += 2;
                        break;
                    }

                    if (current == 0 && (isVowel(current + 1) || stringAt(current, 2, "WH"))) {
                        
                        if (isVowel(current + 1)) {
                            add('A', 'F');
                        } else {
                            
                            add('A');
                        }
                    }

                    
                    if ((current == last && isVowel(current - 1)) || stringAt(current - 1, 5, EwskiEwskyOwskiOwsky) || stringAt(0, 3, "SCH")) {
                        alternate.append('F');
                        current += 1;
                        break;
                    }

                    
                    if (stringAt(current, 4, WiczWitz)) {
                        add('T', 'F');
                        add('S', 'X');
                        current += 4;
                        break;
                    }

                    
                    current += 1;
                    break;

                case 'X' :
                    
                    if (!(current == last && (stringAt((current - 3), 3, EauIau) || stringAt((current - 2), 2, AuOu)))) {
                        add('K');
                        add('S');
                    }

                    if (charAt(current + 1, CX)) {
                        current += 2;
                    } else {
                        current++;
                    }
                    break;

                case 'Z' :
                    
                    if (input.charAt(current + 1) == 'H') {
                        add('J');
                        current += 2;
                        break;
                    } else {
                        if (stringAt(current + 1, 2, ZaZiZo) || (isSlavoGermanic() && (current > 0 && input.charAt(current - 1) != 'T'))) {
                            alternate.append('T');
                            add('S');
                        } else {
                            add('S');
                        }
                    }

                    if (input.charAt(current + 1) == 'Z') {
                        current += 2;
                    } else {
                        current++;
                    }
                    break;

                case '0' :
                case '1' :
                case '2' :
                case '3' :
                case '4' :
                case '5' :
                case '6' :
                case '7' :
                case '8' :
                case '9' :
                    add(input.charAt(current));
                    current++;
                    break;

                default :
                    current++;
            } 
        } 

        
        if (primary.length() > encodeLimit) {
            primary.delete(encodeLimit, primary.length());
        }
        if (alternate.length() > encodeLimit) {
            alternate.delete(encodeLimit, alternate.length());
        }

        return primary.toString();
    }
    
    public String toString() {
      return "Double Metaphone";
    }
}