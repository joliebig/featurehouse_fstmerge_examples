package net.sourceforge.pmd.util.viewer;

import net.sourceforge.pmd.jaxen.Functions;
import net.sourceforge.pmd.util.viewer.gui.MainFrame;


public class Viewer {
    public static void main(String[] args) {
	Functions.registerAll();
        new MainFrame();
    }
}
