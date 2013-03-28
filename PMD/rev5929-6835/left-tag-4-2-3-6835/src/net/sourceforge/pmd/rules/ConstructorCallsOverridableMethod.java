
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTArguments;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.ast.ASTExplicitConstructorInvocation;
import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.ast.AccessNode;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public final class ConstructorCallsOverridableMethod extends AbstractRule {
    
    private static class MethodInvocation {
        private String m_Name;
        private ASTPrimaryExpression m_Ape;
        private List<String> m_ReferenceNames;
        private List<String> m_QualifierNames;
        private int m_ArgumentSize;
        private boolean m_Super;

        private MethodInvocation(ASTPrimaryExpression ape, List<String> qualifierNames, List<String> referenceNames, String name, int argumentSize, boolean superCall) {
            m_Ape = ape;
            m_QualifierNames = qualifierNames;
            m_ReferenceNames = referenceNames;
            m_Name = name;
            m_ArgumentSize = argumentSize;
            m_Super = superCall;
        }

        public boolean isSuper() {
            return m_Super;
        }

        public String getName() {
            return m_Name;
        }

        public int getArgumentCount() {
            return m_ArgumentSize;
        }

        public List<String> getReferenceNames() {
            return m_ReferenceNames;
        }

        public List<String> getQualifierNames() {
            return m_QualifierNames;
        }

        public ASTPrimaryExpression getASTPrimaryExpression() {
            return m_Ape;
        }

        public static MethodInvocation getMethod(ASTPrimaryExpression node) {
            MethodInvocation meth = null;
            int i = node.jjtGetNumChildren();
            if (i > 1) {
                
                Node lastNode = node.jjtGetChild(i - 1);
                if ((lastNode.jjtGetNumChildren() == 1) && (lastNode.jjtGetChild(0) instanceof ASTArguments)) { 
                    
                    
                    List<String> varNames = new ArrayList<String>();
                    List<String> packagesAndClasses = new ArrayList<String>(); 
                    String methodName = null;
                    ASTArguments args = (ASTArguments) lastNode.jjtGetChild(0);
                    int numOfArguments = args.getArgumentCount();
                    boolean superFirst = false;
                    int thisIndex = -1;

                    FIND_SUPER_OR_THIS: {
                        
                        
                        
                        
                        
                        for (int x = 0; x < i - 1; x++) {
                            Node child = node.jjtGetChild(x);
                            if (child instanceof ASTPrimarySuffix) { 
                                ASTPrimarySuffix child2 = (ASTPrimarySuffix) child;
                                
                                
                                if (child2.getImage() == null && child2.jjtGetNumChildren() == 0) {
                                    thisIndex = x;
                                    break;
                                }
                                
                                
                                
                            } else if (child instanceof ASTPrimaryPrefix) { 
                                ASTPrimaryPrefix child2 = (ASTPrimaryPrefix) child;
                                if (getNameFromPrefix(child2) == null) {
                                    if (child2.getImage() == null) {
                                        thisIndex = x;
                                        break;
                                    } else {
                                        superFirst = true;
                                        thisIndex = x;
                                        
                                        
                                        break;
                                    }
                                }
                            }
                            
                            
                            
                        }
                    }

                    if (thisIndex != -1) {
                        
                        
                        if (superFirst) { 
                            
                            FIRSTNODE:{
                                ASTPrimaryPrefix child = (ASTPrimaryPrefix) node.jjtGetChild(0);
                                String name = child.getImage();
                                if (i == 2) { 
                                    methodName = name;
                                } else { 
                                    varNames.add(name);
                                }
                            }
                            OTHERNODES:{ 
                                for (int x = 1; x < i - 1; x++) {
                                    Node child = node.jjtGetChild(x);
                                    ASTPrimarySuffix ps = (ASTPrimarySuffix) child;
                                    if (!ps.isArguments()) {
                                        String name = ((ASTPrimarySuffix) child).getImage();
                                        if (x == i - 2) {
                                            methodName = name;
                                        } else {
                                            varNames.add(name);
                                        }
                                    }
                                }
                            }
                        } else {
                            FIRSTNODE:{
                                if (thisIndex == 1) {
                                    ASTPrimaryPrefix child = (ASTPrimaryPrefix) node.jjtGetChild(0);
                                    String toParse = getNameFromPrefix(child);
                                    
                                    java.util.StringTokenizer st = new java.util.StringTokenizer(toParse, ".");
                                    while (st.hasMoreTokens()) {
                                        packagesAndClasses.add(st.nextToken());
                                    }
                                }
                            }
                            OTHERNODES:{ 
                                
                                
                                for (int x = thisIndex + 1; x < i - 1; x++) {
                                    ASTPrimarySuffix child = (ASTPrimarySuffix) node.jjtGetChild(x);
                                    if (!child.isArguments()) { 
                                        String name = child.getImage();
                                        
                                        if (x == i - 2) {
                                            methodName = name;
                                        } else {
                                            varNames.add(name);
                                        }
                                    }
                                }
                            }
                        }
                    } else { 
                        
                        FIRSTNODE:{ 
                            ASTPrimaryPrefix child = (ASTPrimaryPrefix) node.jjtGetChild(0);
                            String toParse = getNameFromPrefix(child);
                            
                            java.util.StringTokenizer st = new java.util.StringTokenizer(toParse, ".");
                            while (st.hasMoreTokens()) {
                                String value = st.nextToken();
                                if (!st.hasMoreTokens()) {
                                    if (i == 2) {
                                        methodName = value;
                                    } else {
                                        varNames.add(value);
                                    }
                                } else { 
                                    varNames.add(value);
                                }
                            }
                        }
                        OTHERNODES:{ 
                            for (int x = 1; x < i - 1; x++) {
                                ASTPrimarySuffix child = (ASTPrimarySuffix) node.jjtGetChild(x);
                                if (!child.isArguments()) {
                                    String name = child.getImage();
                                    if (x == i - 2) {
                                        methodName = name;
                                    } else {
                                        varNames.add(name);
                                    }
                                }
                            }
                        }
                    }
                    meth = new MethodInvocation(node, packagesAndClasses, varNames, methodName, numOfArguments, superFirst);
                }
            }
            return meth;
        }

        public void show() {
            System.out.println("<MethodInvocation>");
            System.out.println("  <Qualifiers>");
            for (String name: getQualifierNames()) {
                System.out.println("    " + name);
            }
            System.out.println("  </Qualifiers>");
            System.out.println("  <Super>" + isSuper() + "</Super>");
            System.out.println("  <References>");
            for (String name: getReferenceNames()) {
                System.out.println("    " + name);
            }
            System.out.println("  </References>");
            System.out.println("  <Name>" + getName() + "</Name>");
            System.out.println("</MethodInvocation>");
        }
    }

    private static final class ConstructorInvocation {
        private ASTExplicitConstructorInvocation m_Eci;
        private String name;
        private int count = 0;

        public ConstructorInvocation(ASTExplicitConstructorInvocation eci) {
            m_Eci = eci;
            List<ASTArguments> l = new ArrayList<ASTArguments>();
            eci.findChildrenOfType(ASTArguments.class, l);
            if (!l.isEmpty()) {
                ASTArguments aa = l.get(0);
                count = aa.getArgumentCount();
            }
            name = eci.getImage();
        }

        public ASTExplicitConstructorInvocation getASTExplicitConstructorInvocation() {
            return m_Eci;
        }

        public int getArgumentCount() {
            return count;
        }

        public String getName() {
            return name;
        }
    }

    private static final class MethodHolder {
        private ASTMethodDeclarator amd;
        private boolean dangerous;
        private String called;

        public MethodHolder(ASTMethodDeclarator amd) {
            this.amd = amd;
        }

        public void setCalledMethod(String name) {
            this.called = name;
        }

        public String getCalled() {
            return this.called;
        }

        public ASTMethodDeclarator getASTMethodDeclarator() {
            return amd;
        }

        public boolean isDangerous() {
            return dangerous;
        }

        public void setDangerous() {
            dangerous = true;
        }
    }

    private final class ConstructorHolder {
        private ASTConstructorDeclaration m_Cd;
        private boolean m_Dangerous;
        private ConstructorInvocation m_Ci;
        private boolean m_CiInitialized;

        public ConstructorHolder(ASTConstructorDeclaration cd) {
            m_Cd = cd;
        }

        public ASTConstructorDeclaration getASTConstructorDeclaration() {
            return m_Cd;
        }

        public ConstructorInvocation getCalledConstructor() {
            if (!m_CiInitialized) {
                initCI();
            }
            return m_Ci;
        }

        public ASTExplicitConstructorInvocation getASTExplicitConstructorInvocation() {
            ASTExplicitConstructorInvocation eci = null;
            if (!m_CiInitialized) {
                initCI();
            }
            if (m_Ci != null) {
                eci = m_Ci.getASTExplicitConstructorInvocation();
            }
            return eci;
        }

        private void initCI() {
            List<ASTExplicitConstructorInvocation> expressions = new ArrayList<ASTExplicitConstructorInvocation>();
            m_Cd.findChildrenOfType(ASTExplicitConstructorInvocation.class, expressions); 
            if (!expressions.isEmpty()) {
                ASTExplicitConstructorInvocation eci = expressions.get(0);
                m_Ci = new ConstructorInvocation(eci);
                
            }
            m_CiInitialized = true;
        }

        public boolean isDangerous() {
            return m_Dangerous;
        }

        public void setDangerous(boolean dangerous) {
            m_Dangerous = dangerous;
        }
    }

    private static final int compareNodes(SimpleNode n1, SimpleNode n2) {
        int l1 = n1.getBeginLine();
        int l2 = n2.getBeginLine();
        if (l1 == l2) {
            return n1.getBeginColumn() - n2.getBeginColumn();
        }
        return l1 - l2;
    }

    private static class MethodHolderComparator implements Comparator<MethodHolder> {
        public int compare(MethodHolder o1, MethodHolder o2) {
            return compareNodes(o1.getASTMethodDeclarator(), o2.getASTMethodDeclarator());
        }
    }

    private static class ConstructorHolderComparator implements Comparator<ConstructorHolder> {
        public int compare(ConstructorHolder o1, ConstructorHolder o2) {
            return compareNodes(o1.getASTConstructorDeclaration(), o2.getASTConstructorDeclaration());
        }
    }

    
    private static class EvalPackage {
        public EvalPackage() {
        }

        public EvalPackage(String className) {
            m_ClassName = className;
            calledMethods = new ArrayList<MethodInvocation>();
            allMethodsOfClass = new TreeMap<MethodHolder, List<MethodInvocation>>(new MethodHolderComparator());
            calledConstructors = new ArrayList<ConstructorInvocation>();
            allPrivateConstructorsOfClass = new TreeMap<ConstructorHolder, List<MethodInvocation>>(new ConstructorHolderComparator());
        }

        public String m_ClassName;
        public List<MethodInvocation> calledMethods;
        public Map<MethodHolder, List<MethodInvocation>> allMethodsOfClass;

        public List<ConstructorInvocation> calledConstructors;
        public Map<ConstructorHolder, List<MethodInvocation>> allPrivateConstructorsOfClass;
    }

    private static final class NullEvalPackage extends EvalPackage {
        public NullEvalPackage() {
            m_ClassName = "";
            calledMethods = Collections.emptyList();
            allMethodsOfClass = Collections.emptyMap();
            calledConstructors = Collections.emptyList();
            allPrivateConstructorsOfClass = Collections.emptyMap();
        }
    }

    private static final NullEvalPackage nullEvalPackage = new NullEvalPackage();


    
    private final List<EvalPackage> evalPackages = new ArrayList<EvalPackage>();

    private EvalPackage getCurrentEvalPackage() {
        return evalPackages.get(evalPackages.size() - 1);
    }

    
    private void putEvalPackage(EvalPackage ep) {
        evalPackages.add(ep);
    }

    private void removeCurrentEvalPackage() {
        evalPackages.remove(evalPackages.size() - 1);
    }

    private void clearEvalPackages() {
        evalPackages.clear();
    }

    
    private Object visitClassDec(ASTClassOrInterfaceDeclaration node, Object data) {
        String className = node.getImage();
        if (!node.isFinal()) {
            putEvalPackage(new EvalPackage(className));
        } else {
            putEvalPackage(nullEvalPackage);
        }
        
        super.visit(node, data);

        
        if (!(getCurrentEvalPackage() instanceof NullEvalPackage)) {
            
            while (evaluateDangerOfMethods(getCurrentEvalPackage().allMethodsOfClass)) { } 
            
            evaluateDangerOfConstructors1(getCurrentEvalPackage().allPrivateConstructorsOfClass, getCurrentEvalPackage().allMethodsOfClass.keySet());
            while (evaluateDangerOfConstructors2(getCurrentEvalPackage().allPrivateConstructorsOfClass)) { } 

            
            for (MethodInvocation meth: getCurrentEvalPackage().calledMethods) {
                
                for (MethodHolder h: getCurrentEvalPackage().allMethodsOfClass.keySet()) {
                    if (h.isDangerous()) {
                        String methName = h.getASTMethodDeclarator().getImage();
                        int count = h.getASTMethodDeclarator().getParameterCount();
                        if (methName.equals(meth.getName()) && meth.getArgumentCount() == count) {
                            addViolation(data, meth.getASTPrimaryExpression(), "method '" + h.getCalled() + "'");
                        }
                    }
                }
            }
            
            for (ConstructorHolder ch: getCurrentEvalPackage().allPrivateConstructorsOfClass.keySet()) {
                if (ch.isDangerous()) { 
                    
                    int paramCount = ch.getASTConstructorDeclaration().getParameterCount();
                    for (ConstructorInvocation ci: getCurrentEvalPackage().calledConstructors) {
                        if (ci.getArgumentCount() == paramCount) {
                            
                            addViolation(data, ci.getASTExplicitConstructorInvocation(), "constructor");
                        }
                    }
                }
            }
        }
        
        removeCurrentEvalPackage();
        return data;
    }

    
    private boolean evaluateDangerOfMethods(Map<MethodHolder, List<MethodInvocation>> classMethodMap) {
        
        boolean found = false;
        for (Map.Entry<MethodHolder, List<MethodInvocation>> entry: classMethodMap.entrySet()) {
            MethodHolder h = entry.getKey();
            List<MethodInvocation> calledMeths = entry.getValue();
            for (Iterator<MethodInvocation> calledMethsIter = calledMeths.iterator(); calledMethsIter.hasNext() && !h.isDangerous();) {
                
                MethodInvocation meth = calledMethsIter.next();
                
                for (MethodHolder h3: classMethodMap.keySet()) { 
                    if (h3.isDangerous()) {
                        String matchMethodName = h3.getASTMethodDeclarator().getImage();
                        int matchMethodParamCount = h3.getASTMethodDeclarator().getParameterCount();
                        
                        if (matchMethodName.equals(meth.getName()) && matchMethodParamCount == meth.getArgumentCount()) {
                            h.setDangerous();
                            h.setCalledMethod(matchMethodName);
                            found = true;
                            break;
                        }
                    }
                }
            }
        }
        return found;
    }

    
    private void evaluateDangerOfConstructors1(Map<ConstructorHolder, List<MethodInvocation>> classConstructorMap, Set<MethodHolder> evaluatedMethods) {
        
        for (Map.Entry<ConstructorHolder, List<MethodInvocation>> entry: classConstructorMap.entrySet()) {
            ConstructorHolder ch = entry.getKey();
            if (!ch.isDangerous()) {
                
                List<MethodInvocation> calledMeths = entry.getValue();
                
                for (Iterator<MethodInvocation> calledMethsIter = calledMeths.iterator(); calledMethsIter.hasNext() && !ch.isDangerous();) {
                    MethodInvocation meth = calledMethsIter.next();
                    String methName = meth.getName();
                    int methArgCount = meth.getArgumentCount();
                    
                    for (MethodHolder h: evaluatedMethods) {
                        if (h.isDangerous()) {
                            String matchName = h.getASTMethodDeclarator().getImage();
                            int matchParamCount = h.getASTMethodDeclarator().getParameterCount();
                            if (methName.equals(matchName) && (methArgCount == matchParamCount)) {
                                ch.setDangerous(true);
                                
                                break;
                            }
                        }

                    }
                }
            }
        }
    }

    
    private boolean evaluateDangerOfConstructors2(Map<ConstructorHolder, List<MethodInvocation>> classConstructorMap) {
        boolean found = false;
        
        for (ConstructorHolder ch: classConstructorMap.keySet()) {
            ConstructorInvocation calledC = ch.getCalledConstructor();
            if (calledC == null || ch.isDangerous()) {
                continue;
            }
            
            
            int cCount = calledC.getArgumentCount();
            for (Iterator<ConstructorHolder> innerConstIter = classConstructorMap.keySet().iterator(); innerConstIter.hasNext() && !ch.isDangerous();) { 
                ConstructorHolder h2 = innerConstIter.next();
                if (h2.isDangerous()) {
                    int matchConstArgCount = h2.getASTConstructorDeclaration().getParameterCount();
                    if (matchConstArgCount == cCount) {
                        ch.setDangerous(true);
                        found = true;
                        
                    }
                }
            }
        }
        return found;
    }

    public Object visit(ASTCompilationUnit node, Object data) {
        clearEvalPackages();
        return super.visit(node, data);
    }

    public Object visit(ASTEnumDeclaration node, Object data) {
        
        return data;
    }

    
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (!node.isInterface()) {
            return visitClassDec(node, data);
        } else {
            putEvalPackage(nullEvalPackage);
            Object o = super.visit(node, data);
            removeCurrentEvalPackage();
            return o;
        }
    }


    
    public Object visit(ASTConstructorDeclaration node, Object data) {
        if (!(getCurrentEvalPackage() instanceof NullEvalPackage)) {
            List<MethodInvocation> calledMethodsOfConstructor = new ArrayList<MethodInvocation>();
            ConstructorHolder ch = new ConstructorHolder(node);
            addCalledMethodsOfNode(node, calledMethodsOfConstructor, getCurrentEvalPackage().m_ClassName);
            if (!node.isPrivate()) {
                
                getCurrentEvalPackage().calledMethods.addAll(calledMethodsOfConstructor);
                
                
                
                ASTExplicitConstructorInvocation eci = ch.getASTExplicitConstructorInvocation();
                if (eci != null && eci.isThis()) {
                    getCurrentEvalPackage().calledConstructors.add(ch.getCalledConstructor());
                }
            } else {
                
                
                getCurrentEvalPackage().allPrivateConstructorsOfClass.put(ch, calledMethodsOfConstructor);
            }
        }
        return super.visit(node, data);
    }

    
    public Object visit(ASTMethodDeclarator node, Object data) {
        if (!(getCurrentEvalPackage() instanceof NullEvalPackage)) {
            AccessNode parent = (AccessNode) node.jjtGetParent();
            MethodHolder h = new MethodHolder(node);
            if (!parent.isAbstract() && !parent.isPrivate() && !parent.isStatic() && !parent.isFinal()) { 
                h.setDangerous();
                ASTMethodDeclaration decl = node.getFirstParentOfType(ASTMethodDeclaration.class);
                h.setCalledMethod(decl.getMethodName());
            }
            List<MethodInvocation> l = new ArrayList<MethodInvocation>();
            addCalledMethodsOfNode((SimpleNode) parent, l, getCurrentEvalPackage().m_ClassName);
            getCurrentEvalPackage().allMethodsOfClass.put(h, l);
        }
        return super.visit(node, data);
    }


    private static void addCalledMethodsOfNode(AccessNode node, List<MethodInvocation> calledMethods, String className) {
        List<ASTPrimaryExpression> expressions = new ArrayList<ASTPrimaryExpression>();
        node.findChildrenOfType(ASTPrimaryExpression.class, expressions, false);
        addCalledMethodsOfNodeImpl(expressions, calledMethods, className);
    }

    
    private static void addCalledMethodsOfNode(SimpleNode node, List<MethodInvocation> calledMethods, String className) {
        List<ASTPrimaryExpression> expressions = new ArrayList<ASTPrimaryExpression>();
        node.findChildrenOfType(ASTPrimaryExpression.class, expressions);
        addCalledMethodsOfNodeImpl(expressions, calledMethods, className);
    }

    private static void addCalledMethodsOfNodeImpl(List<ASTPrimaryExpression> expressions, List<MethodInvocation> calledMethods, String className) {
        for (ASTPrimaryExpression ape: expressions) {
            MethodInvocation meth = findMethod(ape, className);
            if (meth != null) {
                
                calledMethods.add(meth);
            }
        }
    }

    
    private static MethodInvocation findMethod(ASTPrimaryExpression node, String className) {
        if (node.jjtGetNumChildren() > 0
                && node.jjtGetChild(0).jjtGetNumChildren() > 0
                && node.jjtGetChild(0).jjtGetChild(0) instanceof ASTLiteral) {
            return null;
        }
        MethodInvocation meth = MethodInvocation.getMethod(node);
        boolean found = false;
        
        
        
        if (meth != null) {
            
            if ((meth.getReferenceNames().size() == 0) && !meth.isSuper()) {
                
                
                List<String> packClass = meth.getQualifierNames();
                if (!packClass.isEmpty()) {
                    for (String name: packClass) {
                        if (name.equals(className)) {
                            found = true;
                            break;
                        }
                    }
                } else {
                    found = true;
                }
            }
        }

        return found ? meth : null;
    }

    
    private static String getNameFromPrefix(ASTPrimaryPrefix node) {
        String name = null;
        
        if (node.jjtGetNumChildren() == 1) { 
            Node nnode = node.jjtGetChild(0);
            if (nnode instanceof ASTName) { 
                name = ((ASTName) nnode).getImage();
            }
        }
        return name;
    }

}
