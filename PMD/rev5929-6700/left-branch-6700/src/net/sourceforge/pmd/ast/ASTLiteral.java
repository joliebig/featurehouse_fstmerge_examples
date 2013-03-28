

package net.sourceforge.pmd.ast;

import java.util.regex.Pattern;

public class ASTLiteral extends SimpleJavaTypeNode {
	
	private boolean isInt;
	private boolean isFloat;
	private boolean isChar;
	private boolean isString;
	
    public ASTLiteral(int id) {
        super(id);
    }

    public ASTLiteral(JavaParser p, int id) {
        super(p, id);
    }

    
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
    
    public void setIntLiteral() {
    	this.isInt = true;
    }
    public boolean isIntLiteral() {
    	return isInt;
    }

    public void setFloatLiteral() {
    	this.isFloat = true;
    }
    public boolean isFloatLiteral() {
    	return isFloat;
    }

    public void setCharLiteral() {
    	this.isChar = true;
    }
    public boolean isCharLiteral() {
    	return isChar;
    }

    public void setStringLiteral() {
    	this.isString = true;
    }
    public boolean isStringLiteral() {
    	return isString;
    }

    
    public boolean isSingleCharacterStringLiteral() {
        if (isString) {
            String image = getImage();
            int length = image.length();
            if (length == 3) {
                return true;
            } else if (image.charAt(1) == '\\') {
                return SINGLE_CHAR_ESCAPE_PATTERN.matcher(image).matches();
            }
        }
        return false;
    }

    
    private static final Pattern SINGLE_CHAR_ESCAPE_PATTERN = Pattern
            .compile("^\"\\\\(([ntbrf\\\\'\\\"])|([0-7][0-7]?)|([0-3][0-7][0-7]))\"");

}
