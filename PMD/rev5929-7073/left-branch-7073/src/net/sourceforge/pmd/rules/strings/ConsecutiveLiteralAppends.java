
package net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.ast.ASTArgumentList;
import net.sourceforge.pmd.ast.ASTDoStatement;
import net.sourceforge.pmd.ast.ASTForStatement;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.ast.ASTSwitchLabel;
import net.sourceforge.pmd.ast.ASTSwitchStatement;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.ASTWhileStatement;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.TypeNode;
import net.sourceforge.pmd.properties.IntegerProperty;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.typeresolution.TypeHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ConsecutiveLiteralAppends extends AbstractRule {

    private final static Set<Class> blockParents;

    static {
        blockParents = new HashSet<Class>();
        blockParents.add(ASTForStatement.class);
        blockParents.add(ASTWhileStatement.class);
        blockParents.add(ASTDoStatement.class);
        blockParents.add(ASTIfStatement.class);
        blockParents.add(ASTSwitchStatement.class);
        blockParents.add(ASTMethodDeclaration.class);
    }
    
    private static final PropertyDescriptor thresholdDescriptor = new IntegerProperty(
    		"threshold", 
    		"?",
    		1,
    		1.0f
    		);
    
    private static final Map<String, PropertyDescriptor> propertyDescriptorsByName = asFixedMap(thresholdDescriptor);
 

    private int threshold = 1;

    public Object visit(ASTVariableDeclaratorId node, Object data) {

        if (!isStringBuffer(node)) {
            return data;
        }
        threshold = getIntProperty(thresholdDescriptor);

        int concurrentCount = checkConstructor(node, data);
        Node lastBlock = getFirstParentBlock(node);
        Node currentBlock = lastBlock;
        Map<VariableNameDeclaration, List<NameOccurrence>> decls = node.getScope().getVariableDeclarations();
        SimpleNode rootNode = null;
        
        if (concurrentCount >= 1) {
            rootNode = node;
        }
        for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry: decls.entrySet()) {
            List<NameOccurrence> decl = entry.getValue();
            for (NameOccurrence no: decl) {
                SimpleNode n = no.getLocation();

                currentBlock = getFirstParentBlock(n);

                if (!InefficientStringBuffering.isInStringBufferOperation(n, 3,"append")) {
                    if (!no.isPartOfQualifiedName()) {
                        checkForViolation(rootNode, data, concurrentCount);
                        concurrentCount = 0;
                    }
                    continue;
                }
                ASTPrimaryExpression s = n.getFirstParentOfType(ASTPrimaryExpression.class);
                int numChildren = s.jjtGetNumChildren();
                for (int jx = 0; jx < numChildren; jx++) {
                    SimpleNode sn = (SimpleNode) s.jjtGetChild(jx);
                    if (!(sn instanceof ASTPrimarySuffix)
                            || sn.getImage() != null) {
                        continue;
                    }

                    
                    if ((currentBlock != null && lastBlock != null && !currentBlock
                            .equals(lastBlock))
                            || (currentBlock == null ^ lastBlock == null)) {
                        checkForViolation(rootNode, data, concurrentCount);
                        concurrentCount = 0;
                    }

                    
                    
                    if (concurrentCount == 0) {
                        rootNode = sn;
                    }
                    if (isAdditive(sn)) {
                        concurrentCount = processAdditive(data,
                                concurrentCount, sn, rootNode);
                        if (concurrentCount != 0) {
                            rootNode = sn;
                        }
                    } else if (!isAppendingStringLiteral(sn)) {
                        checkForViolation(rootNode, data, concurrentCount);
                        concurrentCount = 0;
                    } else {
                        concurrentCount++;
                    }
                    lastBlock = currentBlock;
                }
            }
        }
        checkForViolation(rootNode, data, concurrentCount);
        return data;
    }

    
    private int checkConstructor(ASTVariableDeclaratorId node, Object data) {
        Node parent = node.jjtGetParent();
        if (parent.jjtGetNumChildren() >= 2) {
            ASTArgumentList list = ((SimpleNode) parent
                    .jjtGetChild(1)).getFirstChildOfType(ASTArgumentList.class);
            if (list != null) {
                ASTLiteral literal = list.getFirstChildOfType(ASTLiteral.class);
                if (!isAdditive(list) && literal != null
                        && literal.isStringLiteral()) {
                    return 1;
                } 
                return processAdditive(data, 0, list, node);
            }
        }
        return 0;
    }

    private int processAdditive(Object data, int concurrentCount,
                                SimpleNode sn, SimpleNode rootNode) {
        ASTAdditiveExpression additive = sn.getFirstChildOfType(ASTAdditiveExpression.class);
        
        if (additive == null || (additive.getType() != null && !TypeHelper.isA(additive, String.class))) {
            return 0;
        }
        int count = concurrentCount;
        boolean found = false;
        for (int ix = 0; ix < additive.jjtGetNumChildren(); ix++) {
            SimpleNode childNode = (SimpleNode) additive.jjtGetChild(ix);
            if (childNode.jjtGetNumChildren() != 1
                    || childNode.findChildrenOfType(ASTName.class).size() != 0) {
                if (!found) {
                    checkForViolation(rootNode, data, count);
                    found = true;
                }
                count = 0;
            } else {
                count++;
            }
        }

        
        
        if (!found) {
            count = 1;
        }

        return count;
    }

    
    private boolean isAdditive(SimpleNode n) {
        List lstAdditive = n.findChildrenOfType(ASTAdditiveExpression.class);
        if (lstAdditive.isEmpty()) {
            return false;
        }
        
        
        
        for (int ix = 0; ix < lstAdditive.size(); ix++) {
            ASTAdditiveExpression expr = (ASTAdditiveExpression) lstAdditive.get(ix);
            if (expr.getParentsOfType(ASTArgumentList.class).size() != 1) {
                return false;
            }
        }
        return true;
    }

    
    private Node getFirstParentBlock(Node node) {
        Node parentNode = node.jjtGetParent();

        Node lastNode = node;
        while (parentNode != null
                && !blockParents.contains(parentNode.getClass())) {
            lastNode = parentNode;
            parentNode = parentNode.jjtGetParent();
        }
        if (parentNode != null
                && parentNode.getClass().equals(ASTIfStatement.class)) {
            parentNode = lastNode;
        } else if (parentNode != null
                && parentNode.getClass().equals(ASTSwitchStatement.class)) {
            parentNode = getSwitchParent(parentNode, lastNode);
        }
        return parentNode;
    }

    
    private Node getSwitchParent(Node parentNode, Node lastNode) {
        int allChildren = parentNode.jjtGetNumChildren();
        ASTSwitchLabel label = null;
        for (int ix = 0; ix < allChildren; ix++) {
            Node n = parentNode.jjtGetChild(ix);
            if (n.getClass().equals(ASTSwitchLabel.class)) {
                label = (ASTSwitchLabel) n;
            } else if (n.equals(lastNode)) {
                parentNode = label;
                break;
            }
        }
        return parentNode;
    }

    
    private void checkForViolation(SimpleNode node, Object data,
                                   int concurrentCount) {
        if (concurrentCount > threshold) {
            String[] param = {String.valueOf(concurrentCount)};
            addViolation(data, node, param);
        }
    }

    private boolean isAppendingStringLiteral(SimpleNode node) {
        SimpleNode n = node;
        while (n.jjtGetNumChildren() != 0
                && !n.getClass().equals(ASTLiteral.class)) {
            n = (SimpleNode) n.jjtGetChild(0);
        }
        return n.getClass().equals(ASTLiteral.class);
    }

    private static boolean isStringBuffer(ASTVariableDeclaratorId node) {

        if (node.getType() != null) {
            return node.getType().equals(StringBuffer.class);
        }
        SimpleNode nn = node.getTypeNameNode();
        if (nn.jjtGetNumChildren() == 0) {
            return false;
        }
        return TypeHelper.isA((TypeNode)nn.jjtGetChild(0), StringBuffer.class);
    }

    protected Map<String, PropertyDescriptor> propertiesByName() {
    	return propertyDescriptorsByName;
    }
}