package net.sourceforge.pmd;

import java.io.File;


public class SourceFileSelector {

    private boolean selectJavaFiles = true;

    
    private boolean selectJspFiles = false;

    
    public boolean isWantedFile(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex < 0) {
            return false;
        }

        String extensionUppercase = fileName.substring(1 + lastDotIndex)
                .toUpperCase();

        if (selectJavaFiles
                && extensionUppercase
                .equals(SourceFileConstants.JAVA_EXTENSION_UPPERCASE)) {
            return true;
        }

        if (selectJspFiles
                && (extensionUppercase
                .equals(SourceFileConstants.JSP_EXTENSION_UPPERCASE) || extensionUppercase
                .equals(SourceFileConstants.JSPX_EXTENSION_UPPERCASE))) {
            return true;
        }

        return false;
    }

    
    public boolean isWantedFile(File file) {
        return isWantedFile(file.getAbsolutePath());
    }

    
    public boolean isSelectJavaFiles() {
        return selectJavaFiles;
    }

    
    public void setSelectJavaFiles(boolean selectJavaFiles) {
        this.selectJavaFiles = selectJavaFiles;
    }

    
    public boolean isSelectJspFiles() {
        return selectJspFiles;
    }

    
    public void setSelectJspFiles(boolean selectJspFiles) {
        this.selectJspFiles = selectJspFiles;
    }
}
