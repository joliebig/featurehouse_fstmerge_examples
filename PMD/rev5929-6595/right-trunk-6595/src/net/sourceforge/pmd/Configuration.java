package net.sourceforge.pmd;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.util.ClasspathClassLoader;


public class Configuration {

    
    private String suppressMarker = PMD.SUPPRESS_MARKER;
    private int threads = Runtime.getRuntime().availableProcessors();
    private ClassLoader classLoader = getClass().getClassLoader();
    private String auxClasspath;
    private LanguageVersionDiscoverer languageVersionDiscoverer = new LanguageVersionDiscoverer();

    
    private String ruleSets;
    private RulePriority minPriority = RulePriority.LOW;
    private String sourceEncoding = new InputStreamReader(System.in).getEncoding();
    private String inputPaths;

    
    private String reportFormat;
    private String reportEncoding;
    private String reportFile;
    private boolean shortNames;
    private String linePrefix;
    private String linkPrefix;
    private String xsltFilename;

    
    private boolean debug;
    private boolean stressTest;
    private boolean benchmark;

    
    private String targetJDK;

    
    private boolean checkJavaFiles = true;

    
    private boolean checkJspFiles;

    
    public String getSuppressMarker() {
	return suppressMarker;
    }

    
    public void setSuppressMarker(String suppressMarker) {
	this.suppressMarker = suppressMarker;
    }

    
    public int getThreads() {
	return threads;
    }

    
    public void setThreads(int threads) {
	this.threads = threads;
    }

    
    public ClassLoader getClassLoader() {
	return classLoader;
    }

    
    public void setClassLoader(ClassLoader classLoader) {
	if (classLoader == null) {
	    classLoader = getClass().getClassLoader();
	}
	this.classLoader = classLoader;
    }

    
    public String getAuxClasspath() {
	return auxClasspath;
    }

    
    public void setAuxClasspath(String auxClasspath) throws IOException {
	setClassLoader(createClasspathClassLoader(auxClasspath));
	this.auxClasspath = auxClasspath;
    }

    
    public LanguageVersionDiscoverer getLanguageVersionDiscoverer() {
	return languageVersionDiscoverer;
    }

    
    public void setDefaultLanguageVersion(LanguageVersion languageVersion) {
	setDefaultLanguageVersions(Arrays.asList(languageVersion));
    }

    
    public void setDefaultLanguageVersions(List<LanguageVersion> languageVersions) {
	for (LanguageVersion languageVersion : languageVersions) {
	    languageVersionDiscoverer.setDefaultLanguageVersion(languageVersion);
	}
    }

    
    
    public LanguageVersion getLanguageVersionOfFile(String fileName) {
	LanguageVersion languageVersion = languageVersionDiscoverer.getDefaultLanguageVersionForFile(fileName);
	if (languageVersion == null) {
	    
	    
	    languageVersion = languageVersionDiscoverer.getDefaultLanguageVersion(Language.JAVA);
	}
	return languageVersion;
    }

    
    public static ClassLoader createClasspathClassLoader(String classpath) throws IOException {
	ClassLoader classLoader = Configuration.class.getClassLoader();
	if (classpath != null) {
	    classLoader = new ClasspathClassLoader(classpath, classLoader);
	}
	return classLoader;
    }

    
    public String getRuleSets() {
	return ruleSets;
    }

    
    public void setRuleSets(String ruleSets) {
	this.ruleSets = ruleSets;
    }

    
    public RulePriority getMinPriority() {
	return minPriority;
    }

    
    public void setMinPriority(RulePriority minPriority) {
	this.minPriority = minPriority;
    }

    
    public String getSourceEncoding() {
	return sourceEncoding;
    }

    
    public void setSourceEncoding(String sourceEncoding) {
	this.sourceEncoding = sourceEncoding;
    }

    
    public String getInputPaths() {
	return inputPaths;
    }

    
    public void setInputPaths(String inputPaths) {
	this.inputPaths = inputPaths;
    }

    
    public boolean isShortNames() {
	return shortNames;
    }

    
    public void setShortNames(boolean shortNames) {
	this.shortNames = shortNames;
    }
}
