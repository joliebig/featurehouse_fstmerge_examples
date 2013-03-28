package net.sourceforge.pmd.lang.java.rule.basic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.NameOccurrence;
import net.sourceforge.pmd.util.CollectionUtil;


public class UselessOperationOnImmutableRule extends AbstractJavaRule {
	
    
    private static final Set<String> decMethods = CollectionUtil.asSet(new String[] { ".abs", ".add", ".divide", ".divideToIntegralValue", ".max", ".min", ".movePointLeft", ".movePointRight", ".multiply", ".negate", ".plus", ".pow", ".remainder", ".round", ".scaleByPowerOfTen", ".setScale", ".stripTrailingZeros", ".subtract", ".ulp" });
	
    
    private static final Set<String> intMethods = CollectionUtil.asSet(new String[] { ".abs", ".add", ".and", ".andNot", ".clearBit", ".divide", ".flipBit", ".gcd", ".max", ".min", ".mod", ".modInverse", ".modPow", ".multiply", ".negate", ".nextProbablePrine", ".not", ".or", ".pow", ".remainder", ".setBit", ".shiftLeft", ".shiftRight", ".subtract", ".xor" });

    
    private static final Set<String> strMethods = CollectionUtil.asSet(new String[] { ".concat", ".intern", ".replace", ".replaceAll", ".replaceFirst", ".substring", ".toLowerCase", ".toString", ".toUpperCase", ".trim" });

    
    private static final Map<String, Set<String>> mapClasses = new HashMap<String, Set<String>>();
    static {
        mapClasses.put("java.math.BigDecimal", decMethods);
        mapClasses.put("BigDecimal", decMethods);
        mapClasses.put("java.math.BigInteger", intMethods);
        mapClasses.put("BigInteger", intMethods);
        mapClasses.put("java.lang.String", strMethods);
        mapClasses.put("String", strMethods);
    }

    
    
    private static final Set<String> targetMethods = CollectionUtil.asSet(new String[] { ".add", ".multiply", ".divide", ".subtract", ".setScale", ".negate", ".movePointLeft", ".movePointRight", ".pow", ".shiftLeft", ".shiftRight" });

    
    private static final Set<String> targetClasses = CollectionUtil.asSet(new String[] { "java.math.BigDecimal", "BigDecimal", "java.math.BigInteger", "BigInteger" });

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
            if (!(parentClass.equals(ASTExpression.class) || parentClass.equals(ASTConditionalExpression.class) || 
            		hasComparisons(primaryExpression))) {
                String methodCall = sn.getImage().substring(variableName.length());
                ASTType nodeType = node.getTypeNode();
                if ( nodeType != null ) {
                    if ( mapClasses.get(nodeType.getTypeImage()).contains(methodCall)) {
                        addViolation(data, sn);
                    }
                }
            }
        }
        return super.visit(node, data);
    }

    
	private boolean hasComparisons(Node primaryExpression) {
		if (primaryExpression.getClass().equals(ASTPrimaryExpression.class)) {
			List<ASTPrimarySuffix> suffixes = ((ASTPrimaryExpression)primaryExpression).findChildrenOfType(ASTPrimarySuffix.class);
			for (Iterator<ASTPrimarySuffix> iterator = suffixes.iterator(); iterator.hasNext();) {
				ASTPrimarySuffix suffix = iterator.next();
				if ("compareTo".equals(suffix.getImage()))
					return true;
			}
		} else {
			
		}
		return false;	
	}

    
    private ASTVariableDeclaratorId getDeclaration(ASTLocalVariableDeclaration node) {
        ASTType type = node.getTypeNode();
        if (mapClasses.keySet().contains(type.getTypeImage())) {
            return (ASTVariableDeclaratorId) node.jjtGetChild(1).jjtGetChild(0);
        }
        return null;
    }
}
