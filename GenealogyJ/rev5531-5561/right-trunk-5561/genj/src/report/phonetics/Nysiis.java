package phonetics;



public class Nysiis implements Phonetics {

    boolean debug = false;
    StringBuffer word = null;

    
    public String encode(String originalWord) {
        if (originalWord == null || originalWord.length() == 0)
            return null;

        word = new StringBuffer(originalWord.toUpperCase());
        char first;

        
        if (word.length() == 0)
            return null;

        
        while (word.toString().endsWith("S") || word.toString().endsWith("Z")) {
            word.deleteCharAt(word.length() - 1);
        }

        
        int current = 0;
        while (current < word.length()) {
            if (!Character.isLetter(word.charAt(current)))
                word.deleteCharAt(current);
            else
                current++;
        }

        
        if (word.length() == 0)
            return null;

        replaceFront("MAC", "MC");
        replaceFront("PF", "F");
        replaceEnd("IX", "IC");
        replaceEnd("EX", "EC");

        replaceEnd("YE", "Y");
        replaceEnd("EE", "Y");
        replaceEnd("IE", "Y");

        replaceEnd("DT", "D");
        replaceEnd("RT", "D");
        replaceEnd("RD", "D");

        replaceEnd("NT", "N");
        replaceEnd("ND", "N");

        
        replaceAll("EV", "EF", 1);

        first = word.charAt(0);

        
        
        replaceAll("E", "A");
        replaceAll("I", "A");
        replaceAll("O", "A");
        replaceAll("U", "A");

        
        replaceAll("AW", "A");

        replaceAll("GHT", "GT");
        replaceAll("DG", "G");
        replaceAll("PH", "F");

        replaceAll("AH", "A", 1);
        replaceAll("HA", "A", 1);

        replaceAll("KN", "N");
        replaceAll("K", "C");

        replaceAll("M", "N", 1);
        replaceAll("Q", "G", 1);

        replaceAll("SH", "S");
        replaceAll("SCH", "S");

        replaceAll("YW", "Y");

        replaceAll("Y", "A", 1, word.length() - 2);

        replaceAll("WR", "R");

        replaceAll("Z", "S", 1);

        replaceEnd("AY", "Y");

        while (word.toString().endsWith("A")) {
            word.deleteCharAt(word.length() - 1);
        }

        
        if (word.length() == 0)
            return null;

        reduceDuplicates();

        if ('A' == first || 'E' == first || 'I' == first || 'O' == first || 'U' == first) {
            word.deleteCharAt(0);
            word.insert(0, first);
        }

        return word.toString();
    }

    private void reduceDuplicates() {
        char lastChar;
        StringBuffer newWord = new StringBuffer();

        if (0 == word.length()) {
            return;
        }

        lastChar = word.charAt(0);
        newWord.append(lastChar);
        for (int i = 1; i < word.length(); ++i) {
            if (lastChar != word.charAt(i)) {
                newWord.append(word.charAt(i));
            }
            lastChar = word.charAt(i);
        }

        log("reduceDuplicates: " + word);

        word = newWord;
    }

    private void replaceAll(String find, String repl) {
        replaceAll(find, repl, 0, -1);
    }

    private void replaceAll(String find, String repl, int startPos) {
        replaceAll(find, repl, startPos, -1);
    }

    private void replaceAll(String find, String repl, int startPos, int endPos) {
        int pos = word.toString().indexOf(find, startPos);

  

        if (-1 == endPos) {
            endPos = word.length() - 1;
        }

        while (-1 != pos) {
            if (-1 != endPos && pos > endPos) {
                log("stopping pos > endPos: " + pos + ":" + endPos);
                break;
            }
            
            

            word.delete(pos, pos + find.length());
            

            word.insert(pos, repl);
            

            pos = word.toString().indexOf(find);
            
            log("replaceAll[" + find + "," + repl + "]: " + word);
        }

    }

    private void replaceFront(String find, String repl) {
        if (word.toString().startsWith(find)) {
            word.delete(0, find.length());
            word.insert(0, repl);
            log("replaceFront[" + find + "]: " + word);
        }
    }

    private void replaceEnd(String find, String repl) {
        if (word.toString().endsWith(find)) {
            word.delete(word.length() - find.length(), word.length());
            word.append(repl);
            log("replaceEnd[" + find + "]: " + word);
        }
    }

    private void log(String msg) {
        if (!debug) {
            return;
        }
        System.out.println(msg);
        System.out.flush();
    }
    
    public String toString() {
      return "Nysiis";
    }
}