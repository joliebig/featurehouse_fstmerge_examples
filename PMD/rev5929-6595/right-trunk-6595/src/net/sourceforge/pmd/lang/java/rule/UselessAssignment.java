package net.sourceforge.pmd.lang.java.rule;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.lang.dfa.VariableAccess;
import net.sourceforge.pmd.lang.dfa.pathfinder.CurrentPath;
import net.sourceforge.pmd.lang.dfa.pathfinder.DAAPathFinder;
import net.sourceforge.pmd.lang.dfa.pathfinder.Executable;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;


public class UselessAssignment extends AbstractJavaRule implements Executable {

    private RuleContext rc;

    public Object visit(ASTMethodDeclaration node, Object data) {
        this.rc = (RuleContext) data;



        DAAPathFinder a = new DAAPathFinder(node.getDataFlowNode().getFlow().get(0), this);
        a.run();

        return data;
    }

    private static class Usage {
        public int accessType;
        public DataFlowNode node;

        public Usage(int accessType, DataFlowNode node) {
            this.accessType = accessType;
            this.node = node;
        }

        public String toString() {
            return "accessType = " + accessType + ", line = " + node.getLine();
        }
    }

    public void execute(CurrentPath path) {
        Map<String, Usage> hash = new HashMap<String, Usage>();
        
        for (Iterator<DataFlowNode> i = path.iterator(); i.hasNext();) {
            
            DataFlowNode inode = i.next();
            if (inode.getVariableAccess() == null) {
                continue;
            }
            for (int j = 0; j < inode.getVariableAccess().size(); j++) {
                VariableAccess va = inode.getVariableAccess().get(j);
                
                Usage u = hash.get(va.getVariableName());
                if (u != null) {
                    
                    

                    
                    
                    if (va.isDefinition() && va.accessTypeMatches(u.accessType)) {
                        
                        addViolation(rc, u.node.getNode(), va.getVariableName());
                    }

                }
                u = new Usage(va.getAccessType(), inode);
                hash.put(va.getVariableName(), u);
            }
        }
    }
}
