
package net.sourceforge.pmd.lang.java.ast;



public class Token {

    
    public int kind;

    
    public int beginLine, beginColumn, endLine, endColumn;

    
    public String image;

    
    public Token next;

    
    public Token specialToken;

    
    public String toString() {
        return image;
    }

    
    public static final Token newToken(int ofKind) {
        switch (ofKind) {
            default :
                return new Token();
            case JavaParserConstants.RUNSIGNEDSHIFT:
            case JavaParserConstants.RSIGNEDSHIFT:
            case JavaParserConstants.GT:
                return new GTToken();
        }
    }

    public static class GTToken extends Token {
        int realKind = JavaParserConstants.GT;
    }


}
