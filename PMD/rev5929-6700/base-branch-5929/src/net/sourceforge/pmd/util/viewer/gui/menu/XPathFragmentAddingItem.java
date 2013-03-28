package net.sourceforge.pmd.util.viewer.gui.menu;

import net.sourceforge.pmd.util.viewer.model.ViewerModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public class XPathFragmentAddingItem
        extends JMenuItem
        implements ActionListener {
    private ViewerModel model;
    private String fragment;

    
    public XPathFragmentAddingItem(String caption, ViewerModel model, String fragment) {
        super(caption);
        this.model = model;
        this.fragment = fragment;
        addActionListener(this);
    }

    
    public void actionPerformed(ActionEvent e) {
        model.appendToXPathExpression(fragment, this);
    }
}
