package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;

import java.util.List;

public class OccurrenceFinder extends JavaParserVisitorAdapter {

    public Object visit(ASTPrimaryExpression node, Object data) {
        NameFinder nameFinder = new NameFinder(node);

        
        
        NameDeclaration decl = null;

        List<NameOccurrence> names = nameFinder.getNames();
        for (NameOccurrence occ: names) {
            Search search = new Search(occ);
            if (decl == null) {
                
                search.execute();
                decl = search.getResult();
                if (decl == null) {
                    
                    
                    
                    break;
                }
            } else {
                
                search.execute(decl.getScope());
                decl = search.getResult();
            }
        }
        return super.visit(node, data);
    }

}
