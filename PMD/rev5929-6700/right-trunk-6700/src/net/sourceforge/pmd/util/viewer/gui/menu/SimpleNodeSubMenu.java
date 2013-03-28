package net.sourceforge.pmd.util.viewer.gui.menu;

import java.text.MessageFormat;

import javax.swing.JMenu;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.viewer.model.ViewerModel;
import net.sourceforge.pmd.util.viewer.util.NLS;



public class SimpleNodeSubMenu
        extends JMenu {
    private ViewerModel model;
    private Node node;

    
    public SimpleNodeSubMenu(ViewerModel model, Node node) {
        super(MessageFormat.format(NLS.nls("AST.MENU.NODE.TITLE"), node.toString()));
        this.model = model;
        this.node = node;
        init();
    }

    private void init() {
        StringBuffer buf = new StringBuffer(200);
        for (Node temp = node; temp != null; temp = temp.jjtGetParent()) {
            buf.insert(0, "/" + temp.toString());
        }
        add(new XPathFragmentAddingItem(NLS.nls("AST.MENU.NODE.ADD_ABSOLUTE_PATH"), model, buf.toString()));
        add(new XPathFragmentAddingItem(NLS.nls("AST.MENU.NODE.ADD_ALLDESCENDANTS"), model,
                "//" + node.toString()));
    }
}


