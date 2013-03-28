
package net.sourceforge.pmd.lang.jsp.ast;


public class StartAndEndTagMismatchException extends SyntaxErrorException {

    public static final String START_END_TAG_MISMATCH_RULE_NAME
            = "Start and End Tags of an XML Element must match.";

    private int startLine, endLine, startColumn, endColumn;
    private String startTagName, endTagName;

    
    public StartAndEndTagMismatchException(int startLine, int startColumn, String startTagName,
                                           int endLine, int endColumn, String endTagName) {
        super(endLine, START_END_TAG_MISMATCH_RULE_NAME);
        this.startLine = startLine;
        this.startColumn = startColumn;
        this.startTagName = startTagName;

        this.endLine = endLine;
        this.endColumn = endColumn;
        this.endTagName = endTagName;
    }


    
    public int getEndColumn() {
        return endColumn;
    }

    
    public int getEndLine() {
        return endLine;
    }

    
    public int getStartColumn() {
        return startColumn;
    }

    
    public int getStartLine() {
        return startLine;
    }

    
    public String getMessage() {
        return "The start-tag of element \"" + startTagName + "\" (line "
                + startLine + ", column " + startColumn
                + ") does not correspond to the end-tag found: \""
                + endTagName + "\" (line " + endLine
                + ", column " + endColumn + ").";
    }
}
