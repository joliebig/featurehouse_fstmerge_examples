package net.sourceforge.pmd;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class SourceTypeDiscoverer {

    
    private Map<String, SourceType> mapExtensionOnSourceType = new HashMap<String, SourceType>();

    
    public SourceTypeDiscoverer() {
        initialize();
    }

    
    private void initialize() {
        mapExtensionOnSourceType.put(SourceFileConstants.JSP_EXTENSION_UPPERCASE, SourceType.JSP);
        mapExtensionOnSourceType.put(SourceFileConstants.JSPX_EXTENSION_UPPERCASE, SourceType.JSP);

        
        mapExtensionOnSourceType.put(SourceFileConstants.JAVA_EXTENSION_UPPERCASE, SourceType.JAVA_14);
    }

    
    public SourceType getSourceTypeOfFile(File sourceFile) {
        String fileName = sourceFile.getName();
        return getSourceTypeOfFile(fileName);
    }

    
    public SourceType getSourceTypeOfFile(String fileName) {
        SourceType sourceType = null;

        int extensionIndex = 1 + fileName.lastIndexOf('.');
        if (extensionIndex > 0) {
            String extensionUppercase = fileName.substring(extensionIndex).toUpperCase();

            sourceType = mapExtensionOnSourceType.get(extensionUppercase);
        }

        return sourceType;
    }

    
    public void setSourceTypeOfJavaFiles(SourceType sourceType) {
        mapExtensionOnSourceType.put(SourceFileConstants.JAVA_EXTENSION_UPPERCASE, sourceType);
    }

    public SourceType getSourceTypeOfJavaFiles() {
        return mapExtensionOnSourceType.get(SourceFileConstants.JAVA_EXTENSION_UPPERCASE);
    }
}
