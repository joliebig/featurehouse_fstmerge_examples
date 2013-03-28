
package net.sourceforge.pmd.rules.imports;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.rules.ImportWrapper;

public class DuplicateImportsRule extends AbstractRule {

    private Set<ImportWrapper> singleTypeImports;
    private Set<ImportWrapper> importOnDemandImports;

    public Object visit(ASTCompilationUnit node, Object data) {
        singleTypeImports = new HashSet<ImportWrapper>();
        importOnDemandImports = new HashSet<ImportWrapper>();
        super.visit(node, data);

        
        
        
        for (ImportWrapper thisImportOnDemand : importOnDemandImports) {
            for (ImportWrapper thisSingleTypeImport : singleTypeImports) {
                String singleTypeFullName = thisSingleTypeImport.getName();	
				
                int lastDot = singleTypeFullName.lastIndexOf('.');
				String singleTypePkg = singleTypeFullName.substring(0, lastDot);	
                String singleTypeName = singleTypeFullName.substring(lastDot + 1);	

                if (thisImportOnDemand.getName().equals(singleTypePkg) && 
                		!isDisambiguationImport(node, singleTypePkg, singleTypeName)) {
                    addViolation(data, thisSingleTypeImport.getNode(), singleTypeFullName);
                }
            }
        }
        singleTypeImports.clear();
        importOnDemandImports.clear();
        return data;
    }

    
    private boolean isDisambiguationImport(ASTCompilationUnit node, String singleTypePkg, String singleTypeName) {
    	for (ImportWrapper thisImportOnDemand : importOnDemandImports) {	
    		if (!thisImportOnDemand.getName().equals(singleTypePkg)) {		
    			String fullyQualifiedClassName = thisImportOnDemand.getName() + "." + singleTypeName;
    			if (node.getClassTypeResolver().classNameExists(fullyQualifiedClassName)) {
    				return true;	
    			}
    		}
    	}
    	
    	String fullyQualifiedClassName = "java.lang." + singleTypeName;
    	if (node.getClassTypeResolver().classNameExists(fullyQualifiedClassName)) {
			return true;	
		}
    	
    	return false;	
	}

	public Object visit(ASTImportDeclaration node, Object data) {
        ImportWrapper wrapper = new ImportWrapper(node.getImportedName(), node.getImportedName(), node.getImportedNameNode());

        
        if (node.isImportOnDemand()) {
            if (importOnDemandImports.contains(wrapper)) {
                addViolation(data, node.getImportedNameNode(), node.getImportedNameNode().getImage());
            } else {
                importOnDemandImports.add(wrapper);
            }
        } else {
            if (singleTypeImports.contains(wrapper)) {
                addViolation(data, node.getImportedNameNode(), node.getImportedNameNode().getImage());
            } else {
                singleTypeImports.add(wrapper);
            }
        }
        return data;
    }

}
