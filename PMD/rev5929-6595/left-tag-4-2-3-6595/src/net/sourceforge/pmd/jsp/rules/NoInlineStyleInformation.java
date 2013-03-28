package net.sourceforge.pmd.jsp.rules;

import java.util.Set;

import net.sourceforge.pmd.jsp.ast.ASTAttribute;
import net.sourceforge.pmd.jsp.ast.ASTElement;
import net.sourceforge.pmd.util.CollectionUtil;


public class NoInlineStyleInformation extends AbstractJspRule {

    
	
    
    private static final Set<String> STYLE_ELEMENT_NAMES = CollectionUtil.asSet(
    		new String[]{"B", "I", "FONT", "BASEFONT", "U", "CENTER"}
    		);

    
    private static final Set<String> ELEMENT_NAMES_THAT_CAN_HAVE_STYLE_ATTRIBUTES = CollectionUtil.asSet(
    		new String[]{"P", "TABLE", "THEAD", "TBODY", "TFOOT", "TR", "TD", "COL", "COLGROUP"}
    		);

    
    private static final Set<String> STYLE_ATTRIBUTES = CollectionUtil.asSet(
    		new String[]{"STYLE", "FONT", "SIZE", "COLOR", "FACE", "ALIGN", "VALIGN", "BGCOLOR"}
    		);
    
    public Object visit(ASTAttribute node, Object data) {
        if (isStyleAttribute(node)) {
            addViolation(data, node);
        }

        return super.visit(node, data);
    }

    public Object visit(ASTElement node, Object data) {
        if (isStyleElement(node)) {
            addViolation(data, node);
        }

        return super.visit(node, data);
    }

    
    private boolean isStyleElement(ASTElement elementNode) {
        return STYLE_ELEMENT_NAMES.contains(elementNode.getName().toUpperCase());
    }

    
    private boolean isStyleAttribute(ASTAttribute attributeNode) {
        if (STYLE_ATTRIBUTES.contains(attributeNode.getName().toUpperCase())) {
            if (attributeNode.jjtGetParent() instanceof ASTElement) {
                ASTElement parent = (ASTElement) attributeNode.jjtGetParent();
                if (ELEMENT_NAMES_THAT_CAN_HAVE_STYLE_ATTRIBUTES.contains(parent
                        .getName().toUpperCase())) {
                    return true;
                }
            }
        }

        return false;
    }
}
