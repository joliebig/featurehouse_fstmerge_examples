
package net.sourceforge.pmd;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RuleContext {

	private Report report = new Report();
	private File sourceCodeFile;
	private String sourceCodeFilename;
	private SourceType sourceType;
	private final Map<String, Object> attributes;

	
	public RuleContext() {
		attributes = Collections.synchronizedMap(new HashMap<String, Object>());
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

	public void excludeLines(Map<Integer, String> lines) {
		report.exclude(lines);
	}

	public SourceType getSourceType() {
		return this.sourceType;
	}

	public void setSourceType(SourceType t) {
		this.sourceType = t;
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
