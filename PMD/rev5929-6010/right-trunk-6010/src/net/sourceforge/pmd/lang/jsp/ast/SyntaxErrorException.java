
package net.sourceforge.pmd.lang.jsp.ast;


public abstract class SyntaxErrorException extends ParseException {
    private int line;
    private String ruleName;

    
    public SyntaxErrorException(int line, String ruleName) {
        super();
        this.line = line;
        this.ruleName = ruleName;
    }

    
    public int getLine() {
        return line;
    }

    
    public String getRuleName() {
        return ruleName;
    }
}
