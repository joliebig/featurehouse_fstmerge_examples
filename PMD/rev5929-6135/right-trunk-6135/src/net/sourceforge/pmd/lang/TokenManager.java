
package net.sourceforge.pmd.lang;



public interface TokenManager {
    Object getNextToken();
    void setFileName(String fileName);
}
