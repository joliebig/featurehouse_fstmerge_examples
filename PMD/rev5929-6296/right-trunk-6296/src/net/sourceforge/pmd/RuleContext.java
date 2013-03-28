
package net.sourceforge.pmd;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sourceforge.pmd.lang.LanguageVersion;


public class RuleContext {

    private Report report = new Report();
    private File sourceCodeFile;
    private String sourceCodeFilename;
    private LanguageVersion languageVersion;
    private final Map<String, Object> attributes;

    
    public RuleContext() {
	attributes = new ConcurrentHashMap<String, Object>();
    }

    
    public RuleContext(RuleContext ruleContext) {
	this.attributes = ruleContext.attributes;
    }

    
    public Report getReport() {
	return report;
    }

    
    public void setReport(Report report) {
	this.report = report;
    }

    
    public File getSourceCodeFile() {
	return sourceCodeFile;
    }

    
    public void setSourceCodeFile(File sourceCodeFile) {
	this.sourceCodeFile = sourceCodeFile;
    }

    
    public String getSourceCodeFilename() {
	return sourceCodeFilename;
    }

    
    public void setSourceCodeFilename(String filename) {
	this.sourceCodeFilename = filename;
    }

    
    public LanguageVersion getLanguageVersion() {
	return this.languageVersion;
    }

    
    public void setLanguageVersion(LanguageVersion languageVersion) {
	this.languageVersion = languageVersion;
    }

    
    public boolean setAttribute(String name, Object value) {
	if (name == null) {
	    throw new IllegalArgumentException("Parameter 'name' cannot be null.");
	}
	if (value == null) {
	    throw new IllegalArgumentException("Parameter 'value' cannot be null.");
	}
	synchronized (this.attributes) {
	    if (!this.attributes.containsKey(name)) {
		this.attributes.put(name, value);
		return true;
	    } else {
		return false;
	    }
	}
    }

    
    public Object getAttribute(String name) {
	return this.attributes.get(name);
    }

    
    public Object removeAttribute(String name) {
	return this.attributes.remove(name);
    }
}
