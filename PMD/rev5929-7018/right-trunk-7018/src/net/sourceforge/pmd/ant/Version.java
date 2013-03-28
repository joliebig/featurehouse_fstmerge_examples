
package net.sourceforge.pmd.ant;


public class Version {
    private String terseName;

    public void addText(String text) {
	this.terseName = text;
    }

    public String getTerseName() {
	return terseName;
    }
}
