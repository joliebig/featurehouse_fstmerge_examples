package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTLiteral;

public class SuspiciousOctalEscape extends AbstractRule {

    public Object visit(ASTLiteral node, Object data) {
        String image = node.getImage();
        if (image != null && image.startsWith("\"")) 
        {
            
            String s = image.substring(1, image.length() - 1);

            
            int offset = 0;
            for (int slash = s.indexOf('\\', offset);
                 slash != -1 && slash < s.length() - 1;
                 slash = s.indexOf('\\', offset)) {
                String escapeSequence = s.substring(slash + 1);
                char first = escapeSequence.charAt(0);
                if (isOctal(first)) {
                    if (escapeSequence.length() > 1) {
                        char second = escapeSequence.charAt(1);
                        if (isOctal(second)) {
                            if (escapeSequence.length() > 2) {
                                char third = escapeSequence.charAt(2);
                                if (isOctal(third)) {
                                    
                                    
                                    
                                    
                                    if (first != '0' && first != '1' && first != '2' && first != '3') {
                                        
                                        
                                        addViolation(data, node);
                                    } else {
                                        
                                        
                                        if (escapeSequence.length() > 3) {
                                            char fourth = escapeSequence.charAt(3);
                                            if (isDecimal(fourth)) {
                                                addViolation(data, node);
                                            }
                                        }
                                    }

                                } else if (isDecimal(third)) {
                                    
                                    
                                    addViolation(data, node);
                                }
                            }
                        } else if (isDecimal(second)) {
                            
                            
                            addViolation(data, node);
                        }
                    }
                }

                offset = slash + 1;
            }
        }

        return super.visit(node, data);
    }

    private boolean isOctal(char c) {
        switch (c) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
                return true;
            default:
                return false;
        }
    }

    private boolean isDecimal(char c) {
        switch (c) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                return true;
            default:
                return false;
        }
    }
}
