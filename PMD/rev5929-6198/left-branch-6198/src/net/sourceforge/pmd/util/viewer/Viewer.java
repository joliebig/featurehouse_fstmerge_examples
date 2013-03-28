package net.sourceforge.pmd.util.viewer;

import net.sourceforge.pmd.jaxen.MatchesFunction;
import net.sourceforge.pmd.jaxen.TypeOfFunction;
import net.sourceforge.pmd.util.viewer.gui.MainFrame;


public class Viewer {
    public static void main(String[] args) {
        MatchesFunction.registerSelfInSimpleContext();
        TypeOfFunction.registerSelfInSimpleContext();
        new MainFrame();
    }
}
