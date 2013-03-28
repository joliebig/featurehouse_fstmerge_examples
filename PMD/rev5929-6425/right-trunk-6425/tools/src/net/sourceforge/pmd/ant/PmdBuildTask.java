
package net.sourceforge.pmd.ant;

import javax.xml.transform.TransformerException;

import net.sourceforge.pmd.build.PmdBuildException;
import net.sourceforge.pmd.build.PmdBuildTools;
import net.sourceforge.pmd.build.RuleSetToDocs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;


public class PmdBuildTask extends Task {

    private String rulesDirectory;
    private String target;

    private String rulesetToDocs;
    private String mergeRuleset;
    private String rulesIndex;
    private String indexFilename;
    private String mergedRulesetFilename;
    private boolean shouldGenerateJavaFourPom = true;
    
	
	public boolean isShouldGenerateJavaFourPom() {
		return shouldGenerateJavaFourPom;
	}
	
	public void setShouldGenerateJavaFourPom(boolean shouldGenerateJavaFourPom) {
		this.shouldGenerateJavaFourPom = shouldGenerateJavaFourPom;
	}
	
    public String getRulesDirectory() {
        return rulesDirectory;
    }
    
    public void setRulesDirectory(String rulesDirectory) {
        this.rulesDirectory = rulesDirectory;
    }
    
    public String getTarget() {
        return target;
    }
    
    public void setTarget(String targetDirectory) {
        this.target = targetDirectory;
    }

    public void execute() throws BuildException {
		PmdBuildTools tool = validate(new RuleSetToDocs());
		tool.setTargetDirectory(this.target);
		tool.setRulesDirectory(this.rulesDirectory);
	
		try {
	        	tool.convertRulesets();
	        	tool.generateRulesIndex();
	        	if ( this.shouldGenerateJavaFourPom ) {
	        		tool.createPomForJava4("pom.xml","pmd-jdk14-pom.xml");
	        	}
		}
		catch ( PmdBuildException e) {
		    throw new BuildException(e);
		} catch (TransformerException e) {
		    throw new BuildException(e);
		}
    }

    private PmdBuildTools validate(RuleSetToDocs tool) throws BuildException {
		
    	if ( this.target == null || "".equals(target) )
		    throw new BuildException("Attribute targetDirectory is not optional");
		if ( this.rulesDirectory == null || "".equals(this.rulesDirectory) )
		    throw new BuildException("Attribute rulesDirectory is not optional");
		
		if ( this.mergedRulesetFilename != null && ! "".equals(this.mergedRulesetFilename) )
			tool.setMergedRulesetFilename(this.mergedRulesetFilename);
		if ( this.rulesIndex != null && ! "".equals(this.rulesIndex) )
			tool.setGenerateIndexXsl(this.rulesIndex);
		if ( this.rulesetToDocs != null && ! "".equals(this.rulesetToDocs) )
			tool.setRulesetToDocsXsl(this.rulesetToDocs);
		if ( this.mergeRuleset != null && ! "".equals(this.mergeRuleset) )
			tool.setMergeRulesetXsl(this.mergeRuleset);
		return tool;
    }
    
    
	
	public String getRulesetToDocs() {
		return rulesetToDocs;
	}
	
	public void setRulesetToDocs(String rulesetToDocs) {
		this.rulesetToDocs = rulesetToDocs;
	}
	
	public String getMergeRuleset() {
		return mergeRuleset;
	}
	
	public void setMergeRuleset(String mergeRuleset) {
		this.mergeRuleset = mergeRuleset;
	}
	
	public String getRulesIndex() {
		return rulesIndex;
	}
	
	public void setRulesIndex(String rulesIndex) {
		this.rulesIndex = rulesIndex;
	}

	
	public String getIndexFilename() {
		return indexFilename;
	}
	
	public void setIndexFilename(String indexFilename) {
		this.indexFilename = indexFilename;
	}
	
	public String getMergedRulesetFilename() {
		return mergedRulesetFilename;
	}
	
	public void setMergedRulesetFilename(String mergedRulesetFilename) {
		this.mergedRulesetFilename = mergedRulesetFilename;
	}
}
