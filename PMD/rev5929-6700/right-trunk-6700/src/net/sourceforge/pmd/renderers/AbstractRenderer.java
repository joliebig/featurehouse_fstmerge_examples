
package net.sourceforge.pmd.renderers;

import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;


public abstract class AbstractRenderer implements Renderer {

    protected String name;
    protected String description;
    protected Map<String, String> propertyDefinitions = new LinkedHashMap<String, String>();
    protected Properties properties;
    protected boolean showSuppressedViolations = true;
    protected Writer writer;

    public AbstractRenderer(String name, String description, Properties properties) {
	this.name = name;
	this.description = description;
	this.properties = properties;
    }

    
    public String getName() {
	return name;
    }

    
    public void setName(String name) {
	this.name = name;
    }

    
    public String getDescription() {
	return description;
    }

    
    public void setDescription(String description) {
	this.description = description;
    }

    
    public Map<String, String> getPropertyDefinitions() {
	return propertyDefinitions;
    }

    
    protected void defineProperty(String name, String description) {
	propertyDefinitions.put(name, description);
    }

    
    public boolean isShowSuppressedViolations() {
	return showSuppressedViolations;
    }

    
    public void setShowSuppressedViolations(boolean showSuppressedViolations) {
	this.showSuppressedViolations = showSuppressedViolations;
    }

    
    public void setWriter(Writer writer) {
	this.writer = writer;
    }

    
    public Writer getWriter() {
	return writer;
    }
}
