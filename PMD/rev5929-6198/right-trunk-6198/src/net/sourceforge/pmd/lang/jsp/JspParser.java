
package net.sourceforge.pmd.lang.jsp;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.AbstractParser;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.AbstractTokenManager;
import net.sourceforge.pmd.lang.ast.JavaCharStream;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;


public class JspParser extends AbstractParser {

    public TokenManager createTokenManager(Reader source) {
	return new JspTokenManager(source);
    }

    public boolean canParse() {
	return true;
    }

    public Node parse(String fileName, Reader source) throws ParseException {
	AbstractTokenManager.setFileName(fileName);
	return new net.sourceforge.pmd.lang.jsp.ast.JspParser(new JavaCharStream(source)).CompilationUnit();
    }

    public Map<Integer, String> getExcludeMap() {
	return new HashMap<Integer, String>(); 
    }
}
