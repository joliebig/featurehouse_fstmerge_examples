
package net.sourceforge.pmd.lang.java.symboltable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.util.Applier;

public class ClassScope extends AbstractScope {

    protected Map<ClassNameDeclaration, List<NameOccurrence>> classNames = new HashMap<ClassNameDeclaration, List<NameOccurrence>>();
    protected Map<MethodNameDeclaration, List<NameOccurrence>> methodNames = new HashMap<MethodNameDeclaration, List<NameOccurrence>>();
    protected Map<VariableNameDeclaration, List<NameOccurrence>> variableNames = new HashMap<VariableNameDeclaration, List<NameOccurrence>>();

    
    private static ThreadLocal<Integer> anonymousInnerClassCounter = new ThreadLocal<Integer>() {
        protected Integer initialValue() { return Integer.valueOf(1); }
    };

    private String className;

    public ClassScope(String className) {
        this.className = className;
        anonymousInnerClassCounter.set(Integer.valueOf(1));
    }

    
    public ClassScope() {
        
        int v = anonymousInnerClassCounter.get().intValue();
        this.className = "Anonymous$" + v;
        anonymousInnerClassCounter.set(v + 1);
    }

    public void addDeclaration(VariableNameDeclaration variableDecl) {
        if (variableNames.containsKey(variableDecl)) {
            throw new RuntimeException(variableDecl + " is already in the symbol table");
        }
        variableNames.put(variableDecl, new ArrayList<NameOccurrence>());
    }

    public NameDeclaration addVariableNameOccurrence(NameOccurrence occurrence) {
        NameDeclaration decl = findVariableHere(occurrence);
        if (decl != null && occurrence.isMethodOrConstructorInvocation()) {
            List<NameOccurrence> nameOccurrences = methodNames.get(decl);
            if (nameOccurrences == null) {
                
            } else {
                nameOccurrences.add(occurrence);
                Node n = occurrence.getLocation();
                if (n instanceof ASTName) {
                    ((ASTName) n).setNameDeclaration(decl);
                } 
            }

        } else if (decl != null && !occurrence.isThisOrSuper()) {
            List<NameOccurrence> nameOccurrences = variableNames.get(decl);
            if (nameOccurrences == null) {
                
            } else {
                nameOccurrences.add(occurrence);
                Node n = occurrence.getLocation();
                if (n instanceof ASTName) {
                    ((ASTName) n).setNameDeclaration(decl);
                } 
            }
        }
        return decl;
    }

    public Map<VariableNameDeclaration, List<NameOccurrence>> getVariableDeclarations() {
        VariableUsageFinderFunction f = new VariableUsageFinderFunction(variableNames);
        Applier.apply(f, variableNames.keySet().iterator());
        return f.getUsed();
    }

    public Map<MethodNameDeclaration, List<NameOccurrence>> getMethodDeclarations() {
        return methodNames;
    }

    public Map<ClassNameDeclaration, List<NameOccurrence>> getClassDeclarations() {
        return classNames;
    }

    public ClassScope getEnclosingClassScope() {
        return this;
    }

    public String getClassName() {
        return this.className;
    }

    public void addDeclaration(MethodNameDeclaration decl) {
        methodNames.put(decl, new ArrayList<NameOccurrence>());
    }

    public void addDeclaration(ClassNameDeclaration decl) {
        classNames.put(decl, new ArrayList<NameOccurrence>());
    }

    protected NameDeclaration findVariableHere(NameOccurrence occurrence) {
        if (occurrence.isThisOrSuper() || occurrence.getImage().equals(className)) {
            if (variableNames.isEmpty() && methodNames.isEmpty()) {
                
                
                
                
                return null;
            }
            
            
            
            
            
            
            
            
            if (!variableNames.isEmpty()) {
                return variableNames.keySet().iterator().next();
            }
            return methodNames.keySet().iterator().next();
        }

        if (occurrence.isMethodOrConstructorInvocation()) {
            for (MethodNameDeclaration mnd: methodNames.keySet()) {
                if (mnd.getImage().equals(occurrence.getImage())) {
                    int args = occurrence.getArgumentCount();
                    if (args == mnd.getParameterCount() || (mnd.isVarargs() && args >= mnd.getParameterCount() - 1)) {
                        
                        
                        
                        
                        return mnd;
                    }
                }
            }
            return null;
        }

        List<String> images = new ArrayList<String>();
        images.add(occurrence.getImage());
        if (occurrence.getImage().startsWith(className)) {
            images.add(clipClassName(occurrence.getImage()));
        }
        ImageFinderFunction finder = new ImageFinderFunction(images);
        Applier.apply(finder, variableNames.keySet().iterator());
        return finder.getDecl();
    }

    public String toString() {
        String res = "ClassScope (" + className + "): ";
        if (!classNames.isEmpty()) res += "(" + glomNames(classNames.keySet()) + ")";
        if (!methodNames.isEmpty()) {
            for (MethodNameDeclaration mnd: methodNames.keySet()) {
                res += mnd.toString();
                int usages = methodNames.get(mnd).size();
                res += "(begins at line " + mnd.getNode().getBeginLine() + ", " + usages + " usages)";
                res += ",";
            }
        }
        if (!variableNames.isEmpty()) res += "(" + glomNames(variableNames.keySet()) + ")";
        return res;
    }

    private String clipClassName(String in) {
        return in.substring(in.indexOf('.') + 1);
    }
}
