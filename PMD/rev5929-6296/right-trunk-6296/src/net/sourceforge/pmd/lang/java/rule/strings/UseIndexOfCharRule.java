package net.sourceforge.pmd.lang.java.rule.strings;

import net.sourceforge.pmd.lang.java.rule.AbstractPoorMethodCall;


public class UseIndexOfCharRule extends AbstractPoorMethodCall {

    private static final String targetTypeName = "String";
    private static final String[] methodNames = new String[] { "indexOf", "lastIndexOf" };

    
    protected String targetTypename() { 
        return targetTypeName;
    }

    
    protected String[] methodNames() {
        return methodNames;
    }

    
    protected boolean isViolationArgument(int argIndex, String arg) {
        
        return isSingleCharAsString(arg);
    }

}
