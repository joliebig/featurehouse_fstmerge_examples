
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTArguments;
import net.sourceforge.pmd.ast.ASTArrayDimsAndInits;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTEnumDeclaration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class AccessorClassGeneration extends AbstractRule {

    private List<ClassData> classDataList = new ArrayList<ClassData>();
    private int classID = -1;
    private String packageName;

    public Object visit(ASTEnumDeclaration node, Object data) {
        return data;  
    }

    public Object visit(ASTCompilationUnit node, Object data) {
        classDataList.clear();
        packageName = node.getScope().getEnclosingSourceFileScope().getPackageName();
        return super.visit(node, data);
    }

    private static class ClassData {
        private String m_ClassName;
        private List<ASTConstructorDeclaration> m_PrivateConstructors;
        private List<AllocData> m_Instantiations;
        
        private List<String> m_ClassQualifyingNames;

        public ClassData(String className) {
            m_ClassName = className;
            m_PrivateConstructors = new ArrayList<ASTConstructorDeclaration>();
            m_Instantiations = new ArrayList<AllocData>();
            m_ClassQualifyingNames = new ArrayList<String>();
        }

        public void addInstantiation(AllocData ad) {
            m_Instantiations.add(ad);
        }

        public Iterator<AllocData> getInstantiationIterator() {
            return m_Instantiations.iterator();
        }

        public void addConstructor(ASTConstructorDeclaration cd) {
            m_PrivateConstructors.add(cd);
        }

        public Iterator<ASTConstructorDeclaration> getPrivateConstructorIterator() {
            return m_PrivateConstructors.iterator();
        }

        public String getClassName() {
            return m_ClassName;
        }

        public void addClassQualifyingName(String name) {
            m_ClassQualifyingNames.add(name);
        }

        public List<String> getClassQualifyingNamesList() {
            return m_ClassQualifyingNames;
        }
    }

    private static class AllocData {
        private String m_Name;
        private int m_ArgumentCount;
        private ASTAllocationExpression m_ASTAllocationExpression;
        private boolean isArray;

        public AllocData(ASTAllocationExpression node, String aPackageName, List<String> classQualifyingNames) {
            if (node.jjtGetChild(1) instanceof ASTArguments) {
                ASTArguments aa = (ASTArguments) node.jjtGetChild(1);
                m_ArgumentCount = aa.getArgumentCount();
                
                
                if (!(node.jjtGetChild(0) instanceof ASTClassOrInterfaceType)) {
                    throw new RuntimeException("BUG: Expected a ASTClassOrInterfaceType, got a " + node.jjtGetChild(0).getClass());
                }
                ASTClassOrInterfaceType an = (ASTClassOrInterfaceType) node.jjtGetChild(0);
                m_Name = stripString(aPackageName + ".", an.getImage());

                
                
                String findName = "";
                for (ListIterator<String> li = classQualifyingNames.listIterator(classQualifyingNames.size()); li.hasPrevious();) {
                    String aName = li.previous();
                    findName = aName + "." + findName;
                    if (m_Name.startsWith(findName)) {
                        
                        m_Name = m_Name.substring(findName.length());
                        break;
                    }
                }
            } else if (node.jjtGetChild(1) instanceof ASTArrayDimsAndInits) {
                
                
                isArray = true;
            }
            m_ASTAllocationExpression = node;
        }

        public String getName() {
            return m_Name;
        }

        public int getArgumentCount() {
            return m_ArgumentCount;
        }

        public ASTAllocationExpression getASTAllocationExpression() {
            return m_ASTAllocationExpression;
        }

        public boolean isArray() {
            return isArray;
        }
    }

    
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            if (!(node.jjtGetParent().jjtGetParent() instanceof ASTCompilationUnit)) {
                
                String interfaceName = node.getImage();
                int formerID = getClassID();
                setClassID(classDataList.size());
                ClassData newClassData = new ClassData(interfaceName);
                
                ClassData formerClassData = classDataList.get(formerID);
                newClassData.addClassQualifyingName(formerClassData.getClassName());
                classDataList.add(getClassID(), newClassData);
                Object o = super.visit(node, data);
                setClassID(formerID);
                return o;
            } else {
                String interfaceName = node.getImage();
                classDataList.clear();
                setClassID(0);
                classDataList.add(getClassID(), new ClassData(interfaceName));
                Object o = super.visit(node, data);
                if (o != null) {
                    processRule(o);
                } else {
                    processRule(data);
                }
                setClassID(-1);
                return o;
            }
        } else if (!(node.jjtGetParent().jjtGetParent() instanceof ASTCompilationUnit)) {
            
            String className = node.getImage();
            int formerID = getClassID();
            setClassID(classDataList.size());
            ClassData newClassData = new ClassData(className);
            
            
            
            
            if (formerID == -1 || formerID >= classDataList.size()) {
                return null;
            }
            
            ClassData formerClassData = classDataList.get(formerID);
            newClassData.addClassQualifyingName(formerClassData.getClassName());
            classDataList.add(getClassID(), newClassData);
            Object o = super.visit(node, data);
            setClassID(formerID);
            return o;
        }
        
        if ( ! node.isStatic() ) {	
        String className = node.getImage();
        classDataList.clear();
        setClassID(0);
        classDataList.add(getClassID(), new ClassData(className));
        }
        Object o = super.visit(node, data);
        if (o != null && ! node.isStatic() ) { 
            processRule(o);
        } else {
            processRule(data);
        }
        setClassID(-1);
        return o;
    }

    
    public Object visit(ASTConstructorDeclaration node, Object data) {
        if (node.isPrivate()) {
            getCurrentClassData().addConstructor(node);
        }
        return super.visit(node, data);
    }

    public Object visit(ASTAllocationExpression node, Object data) {
        
        
        
        
        if (classID == -1 || getCurrentClassData() == null) {
            return data;
        }
        AllocData ad = new AllocData(node, packageName, getCurrentClassData().getClassQualifyingNamesList());
        if (!ad.isArray()) {
            getCurrentClassData().addInstantiation(ad);
        }
        return super.visit(node, data);
    }

    private void processRule(Object ctx) {
        
        for (ClassData outerDataSet : classDataList) {
            for (Iterator<ASTConstructorDeclaration> constructors = outerDataSet.getPrivateConstructorIterator(); constructors.hasNext();) {
                ASTConstructorDeclaration cd = constructors.next();

                for (ClassData innerDataSet : classDataList) {
                    if (outerDataSet == innerDataSet) {
                        continue;
                    }
                    for (Iterator<AllocData> allocations = innerDataSet.getInstantiationIterator(); allocations.hasNext();) {
                        AllocData ad = allocations.next();
                        
                        
                        if (outerDataSet.getClassName().equals(ad.getName()) && (cd.getParameterCount() == ad.getArgumentCount())) {
                            addViolation(ctx, ad.getASTAllocationExpression());
                        }
                    }
                }
            }
        }
    }

    private ClassData getCurrentClassData() {
        
        
        
        
        if (classID >= classDataList.size()) {
            return null;
        }
        return classDataList.get(classID);
    }

    private void setClassID(int ID) {
        classID = ID;
    }

    private int getClassID() {
        return classID;
    }

    
    
    
    
    
    
    
    
    private static String stripString(String remove, String value) {
        String returnValue;
        int index = value.indexOf(remove);
        if (index != -1) {	
            returnValue = value.substring(0, index) + value.substring(index + remove.length());
        } else {
            returnValue = value;
        }
        return returnValue;
    }

}
