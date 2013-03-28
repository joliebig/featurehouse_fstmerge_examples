package net.sourceforge.pmd.lang.ast.xpath.saxon;


public class IdGenerator {
    private int id;

    public int getNextId() {
	return id++;
    }
}
