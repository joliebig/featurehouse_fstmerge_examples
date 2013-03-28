
package net.sourceforge.pmd.cpd;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;


public class AnyTokenizer implements Tokenizer {
    public static final String TOKENS = " \t!#$%^&*(){}-=+<>/\\`~;:";

    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) {
        StringBuffer sb = sourceCode.getCodeBuffer();
        BufferedReader reader = new BufferedReader(new CharArrayReader(sb.toString().toCharArray()));
        try {
            int lineNumber = 1;
            String line = reader.readLine();
            while (line != null) {
                StringTokenizer tokenizer = new StringTokenizer(line, TOKENS, true);
                try {
                    String token = tokenizer.nextToken();
                    while (token != null) {
                        if (!token.equals(" ") && !token.equals("\t")) {
                            tokenEntries.add(new TokenEntry(token, sourceCode.getFileName(), lineNumber));
                        }
                        token = tokenizer.nextToken();
                    }
                } catch (NoSuchElementException ex) {
                    
                }
                
                line = reader.readLine();
                lineNumber++;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
            }
            tokenEntries.add(TokenEntry.getEOF());
        }
    }
}
