package net.sourceforge.pmd.util.viewer;

import net.sourceforge.pmd.lang.xpath.Initializer;
import net.sourceforge.pmd.util.viewer.gui.MainFrame;


public class Viewer {
    public static void main(String[] args) {
	Initializer.initialize();
        new MainFrame();
    }
}
