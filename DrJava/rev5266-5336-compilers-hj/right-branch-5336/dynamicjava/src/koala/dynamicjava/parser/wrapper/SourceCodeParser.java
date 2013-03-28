

package koala.dynamicjava.parser.wrapper;

import java.util.*;

import koala.dynamicjava.tree.CompilationUnit;
import koala.dynamicjava.tree.Node;



public interface SourceCodeParser {

    
    List<Node> parseStream();
    
    
    CompilationUnit parseCompilationUnit();
}
