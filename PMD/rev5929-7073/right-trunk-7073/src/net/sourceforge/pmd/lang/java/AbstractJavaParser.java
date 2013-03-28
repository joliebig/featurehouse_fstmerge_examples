
package net.sourceforge.pmd.lang.java;

import java.io.Reader;
import java.util.Map;

import net.sourceforge.pmd.lang.AbstractParser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.AbstractTokenManager;
import net.sourceforge.pmd.lang.ast.JavaCharStream;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.JavaParser;
import net.sourceforge.pmd.lang.java.ast.ParseException;


public abstract class AbstractJavaParser extends AbstractParser {
    private JavaParser parser;

    public AbstractJavaParser(ParserOptions parserOptions) {
	super(parserOptions);
    }

    @Override
    public TokenManager createTokenManager(Reader source) {
	return new JavaTokenManager(source);
    }

    
    protected JavaParser createJavaParser(Reader source) throws ParseException {
	parser = new JavaParser(new JavaCharStream(source));
	String suppressMarker = getParserOptions().getSuppressMarker();
	if (suppressMarker != null) {
	    parser.setSuppressMarker(suppressMarker);
	}
	return parser;
    }

    public boolean canParse() {
	return true;
    }

    public Node parse(String fileName, Reader source) throws ParseException {
	AbstractTokenManager.setFileName(fileName);
	return createJavaParser(source).CompilationUnit();
    }

    public Map<Integer, String> getSuppressMap() {
	return parser.getSuppressMap();
    }
}
