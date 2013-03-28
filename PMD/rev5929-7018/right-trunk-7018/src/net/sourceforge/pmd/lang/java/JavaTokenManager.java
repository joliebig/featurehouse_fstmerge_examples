
package net.sourceforge.pmd.lang.java;

import java.io.Reader;

import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.JavaCharStream;
import net.sourceforge.pmd.lang.java.ast.JavaParserTokenManager;


public class JavaTokenManager implements TokenManager {
    private final JavaParserTokenManager tokenManager;

    public JavaTokenManager(Reader source) {
	tokenManager = new JavaParserTokenManager(new JavaCharStream(source));
    }

    public Object getNextToken() {
	return tokenManager.getNextToken();
    }

    public void setFileName(String fileName) {
	tokenManager.setFileName(fileName);
    }
}
