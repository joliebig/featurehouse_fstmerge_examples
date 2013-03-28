
package net.sourceforge.pmd.lang.java;

import java.io.Reader;
import java.util.Map;

import test.net.sourceforge.pmd.testframework.AbstractTokenizerTest;

import net.sourceforge.pmd.lang.AbstractParser;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.AbstractTokenManager;
import net.sourceforge.pmd.lang.ast.JavaCharStream;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.JavaParser;
import net.sourceforge.pmd.lang.java.ast.ParseException;


public abstract class AbstractJavaParser extends AbstractParser {
    private JavaParser parser;

    public TokenManager createTokenManager(Reader source) {
	return new JavaTokenManager(source);
    }

    
    protected JavaParser createJavaParser(Reader source) throws ParseException {
	parser = new JavaParser(new JavaCharStream(source));
	String excludeMarker = getExcludeMarker();
	if (excludeMarker != null) {
	    parser.setExcludeMarker(excludeMarker);
	}
	return parser;
    }

    public Node parse(String fileName, Reader source) throws ParseException {
	AbstractTokenManager.setFileName(fileName);
	return createJavaParser(source).CompilationUnit();
    }

    public Map<Integer, String> getExcludeMap() {
	return parser.getExcludeMap();
    }
}
