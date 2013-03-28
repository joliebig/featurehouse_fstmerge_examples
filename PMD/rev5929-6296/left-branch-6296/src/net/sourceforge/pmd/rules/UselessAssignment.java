package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.dfa.IDataFlowNode;
import net.sourceforge.pmd.dfa.pathfinder.CurrentPath;
import net.sourceforge.pmd.dfa.pathfinder.DAAPathFinder;
import net.sourceforge.pmd.dfa.pathfinder.Executable;
import net.sourceforge.pmd.dfa.variableaccess.VariableAccess;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UselessAssignment extends AbstractRule implements Executable {

    private RuleContext rc;

    public Object visit(ASTMethodDeclaration node, Object data) {
        this.rc = (RuleContext) data;



        DAAPathFinder a = new DAAPathFinder(node.getDataFlowNode().getFlow().get(0), this);
        a.run();

        return data;
    }

    private static class Usage {
        public int accessType;
        public IDataFlowNode node;

        public Usage(int accessType, IDataFlowNode node) {
            this.accessType = accessType;
            this.node = node;
        }

        public String toString() {
            return "accessType = " + accessType + ", line = " + node.getLine();
        }
    }

    public void execute(CurrentPath path) {
        Map<String, Usage> hash = new HashMap<String, Usage>();
        
        for (Iterator<IDataFlowNode> i = path.iterator(); i.hasNext();) {
            
            IDataFlowNode inode = i.next();
            if (inode.getVariableAccess() == null) {
                continue;
            }
            for (int j = 0; j < inode.getVariableAccess().size(); j++) {
                VariableAccess va = inode.getVariableAccess().get(j);
                
                Usage u = hash.get(va.getVariableName());
                if (u != null) {
                    
                    

                    
                    
                    if (va.isDefinition() && va.accessTypeMatches(u.accessType)) {
                        
                        addViolation(rc, u.node.getSimpleNode(), va.getVariableName());
                    }

                }
                u = new Usage(va.getAccessType(), inode);
                hash.put(va.getVariableName(), u);
            }
        }
    }
}
