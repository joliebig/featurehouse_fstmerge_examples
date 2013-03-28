
package net.sourceforge.pmd.lang;

import java.io.Reader;


public abstract class AbstractParser implements Parser {
    protected final ParserOptions parserOptions;
    
    public AbstractParser(ParserOptions parserOptions) {
	this.parserOptions = parserOptions;
    }

    public ParserOptions getParserOptions() {
	return parserOptions;
    }

    public TokenManager getTokenManager(String fileName, Reader source) {
	TokenManager tokenManager = createTokenManager(source);
	tokenManager.setFileName(fileName);
	return tokenManager;
    }

    protected abstract TokenManager createTokenManager(Reader source);
}
