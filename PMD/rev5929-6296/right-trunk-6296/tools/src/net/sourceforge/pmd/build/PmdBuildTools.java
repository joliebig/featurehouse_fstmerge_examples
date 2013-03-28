package net.sourceforge.pmd.build;

import javax.xml.transform.TransformerException;

public interface PmdBuildTools {

    
    public abstract String getRulesDirectory();

    
    public abstract void setRulesDirectory(String rulesDirectory);

    
    public abstract void convertRulesets() throws PmdBuildException;

    public abstract void generateRulesIndex() throws PmdBuildException, TransformerException;

    
    public abstract String getTargetDirectory();

    
    public abstract void setTargetDirectory(String targetDirectory);

    public abstract void createPomForJava4(String pom, String pom4java4) throws PmdBuildException;

}