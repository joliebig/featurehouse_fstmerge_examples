package phonetics;

import java.util.StringTokenizer;
import java.util.Vector;


public class Soundex implements Phonetics {
  
    
    private final static String ACCENTS = "ŠS ŽZ šs žz ŸY ÀA ÁA ÂA ÃA ÄA ÅA ÇC ÈE ÉE ÊE ËE ÌI ÍI ÎI ÏI ÑN ÒO ÓO ÔO ÕO ÖO ØO ÙU ÚU ÛU ÜU ÝY àa áa âa ãa äa åa çc èe ée êe ëe ìi íi îi ïi ñn òo óo ôo õo öo øo ùu úu ûu üu ýy µu ÞTH þth ÐDH ðdh ßss ŒOE œoe ÆAE æae";
    static public final char[] US_ENGLISH_SOUNDEX_MAPPING = "01230120022455012623010202".toCharArray();

    private String[] accents;

    private char[] soundexMapping;

    
    public Soundex() {
        this(US_ENGLISH_SOUNDEX_MAPPING);
    }

    
    public Soundex(char[] mapping) {
      
      this.soundexMapping = mapping;
      
    }

    
    public String substituteAccents(String str) {

      
      if (accents==null) {
        Vector buffer = new Vector(256);
        try {
          
          StringTokenizer tokens = new StringTokenizer(ACCENTS);
            while (tokens.hasMoreTokens()) {
              String token = tokens.nextToken();
              int unicode = token.charAt(0);
              String substitute = token.substring(1);
              if (buffer.size()<unicode+1)
                buffer.setSize(unicode+1);
              buffer.set(unicode, substitute);
            }
        } catch (Throwable t) {
        }
        
        accents = (String[])buffer.toArray(new String[buffer.size()]);
      }

      
      StringBuffer result = new StringBuffer(str.length() * 2);
      for (int i = 0; i < str.length(); i++) {
          char c = str.charAt(i);
          if (c<accents.length&&accents[c]!=null)
            result.append(accents[c]);
          else
            result.append(c);
      }

      
      return result.toString();
    }

    
    public String encode(String s) {

      
      if (s == null || s.length() == 0)
          return null;

        
        String str = substituteAccents(s);

        
        if (!Character.isLetter(str.charAt(0)))
            return encode(str.substring(1));

        char out[] = { '0', '0', '0', '0' };
        char last, mapped;
        int incount = 1, count = 1;
        out[0] = Character.toUpperCase(str.charAt(0));
        last = getMappingCode(str.charAt(0));
        while ((incount < str.length()) && (mapped = getMappingCode(str.charAt(incount++))) != 0 && (count < 4)) {
            if ((mapped != '0') && (mapped != last)) {
                out[count++] = mapped;
            }
            last = mapped;
        }
        return new String(out);
    }

    
    private char getMappingCode(char c) {
        if (!Character.isLetter(c)) {
            return '0';
        } else {
            int loc = Character.toUpperCase(c) - 'A';
            if (loc < 0 || loc > (soundexMapping.length - 1))
                return '0';
            return soundexMapping[loc];
        }
    }

    public String toString() {
      return "Soundex";
    }
}