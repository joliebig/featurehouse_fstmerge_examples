package net.sourceforge.pmd.lang.java.rule.unnecessary;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.NameOccurrence;
import net.sourceforge.pmd.util.CollectionUtil;


public class UselessOperationOnImmutableRule extends AbstractJavaRule {

    
    private static final Set<String> BIG_DECIMAL_METHODS = CollectionUtil.asSet(new String[] { ".abs", ".add", ".divide", ".divideToIntegralValue", ".max", ".min", ".movePointLeft", ".movePointRight", ".multiply", ".negate", ".plus", ".pow", ".remainder", ".round", ".scaleByPowerOfTen", ".setScale", ".stripTrailingZeros", ".subtract", ".ulp" });

    
    private static final Set<String> BIG_INTEGER_METHODS = CollectionUtil.asSet(new String[] { ".abs", ".add", ".and", ".andNot", ".clearBit", ".divide", ".flipBit", ".gcd", ".max", ".min", ".mod", ".modInverse", ".modPow", ".multiply", ".negate", ".nextProbablePrine", ".not", ".or", ".pow", ".remainder", ".setBit", ".shiftLeft", ".shiftRight", ".subtract", ".xor" });

    
    private static final Set<String> STRING_METHODS = CollectionUtil.asSet(new String[] { ".concat", ".intern", ".replace", ".replaceAll", ".replaceFirst", ".substring", ".toLowerCase", ".toString", ".toUpperCase", ".trim" });

    
    private static final Map<String, Set<String>> MAP_CLASSES = new HashMap<String, Set<String>>();
    static {
        MAP_CLASSES.put("java.math.BigDecimal", BIG_DECIMAL_METHODS);
        MAP_CLASSES.put("BigDecimal", BIG_DECIMAL_METHODS);
        MAP_CLASSES.put("java.math.BigInteger", BIG_INTEGER_METHODS);
        MAP_CLASSES.put("BigInteger", BIG_INTEGER_METHODS);
        MAP_CLASSES.put("java.lang.String", STRING_METHODS);
        MAP_CLASSES.put("String", STRING_METHODS);
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {

        ASTVariableDeclaratorId var = getDeclaration(node);
        if (var == null) {
            return super.visit(node, data);
        }
        String variableName = var.getImage();
        for (NameOccurrence no: var.getUsages()) {
            
            
            Node sn = no.getLocation();
            Node primaryExpression = sn.jjtGetParent().jjtGetParent();
            Class<? extends Node> parentClass = primaryExpression.jjtGetParent().getClass();
            if (parentClass.equals(ASTStatementExpression.class)) {
                String methodCall = sn.getImage().substring(variableName.length());
                ASTType nodeType = node.getTypeNode();
                if ( nodeType != null ) {
                    if ( MAP_CLASSES.get(nodeType.getTypeImage()).contains(methodCall)) {
                        addViolation(data, sn);
                    }
                }
            }
        }
        return super.visit(node, data);
    }

    
    private ASTVariableDeclaratorId getDeclaration(ASTLocalVariableDeclaration node) {
        ASTType type = node.getTypeNode();
        if (MAP_CLASSES.keySet().contains(type.getTypeImage())) {
            return (ASTVariableDeclaratorId) node.jjtGetChild(1).jjtGetChild(0);
        }
        return null;
    }
}
