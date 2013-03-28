package net.sourceforge.pmd;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.RendererFactory;
import net.sourceforge.pmd.util.ClasspathClassLoader;


public class Configuration {

    
    private String suppressMarker = PMD.SUPPRESS_MARKER;
    private int threads = Runtime.getRuntime().availableProcessors();
    private ClassLoader classLoader = getClass().getClassLoader();
    private LanguageVersionDiscoverer languageVersionDiscoverer = new LanguageVersionDiscoverer();

    
    private String ruleSets;
    private RulePriority minimumPriority = RulePriority.LOW;
    private String sourceEncoding = System.getProperty("file.encoding");
    private String inputPaths;

    
    private String reportFormat;
    private String reportFile;
    private boolean reportShortNames = false;
    private Properties reportProperties = new Properties();
    private boolean showSuppressedViolations = false;

    
    private boolean debug;
    private boolean stressTest;
    private boolean benchmark;

    
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

    
    public void prependClasspath(String classpath) throws IOException {
	if (classLoader == null) {
	    classLoader = Configuration.class.getClassLoader();
	}
	if (classpath != null) {
	    classLoader = new ClasspathClassLoader(classpath, classLoader);
	}
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

    
    public String getRuleSets() {
	return ruleSets;
    }

    
    public void setRuleSets(String ruleSets) {
	this.ruleSets = ruleSets;
    }

    
    public RulePriority getMinimumPriority() {
	return minimumPriority;
    }

    
    public void setMinimumPriority(RulePriority minimumPriority) {
	this.minimumPriority = minimumPriority;
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

    
    public boolean isReportShortNames() {
	return reportShortNames;
    }

    
    public void setReportShortNames(boolean reportShortNames) {
	this.reportShortNames = reportShortNames;
    }

    
    public Renderer createRenderer() {
	Renderer renderer = RendererFactory.createRenderer(reportFormat, this.reportProperties);
	renderer.setShowSuppressedViolations(this.showSuppressedViolations);
	return renderer;
    }

    
    public String getReportFormat() {
	return reportFormat;
    }

    
    public void setReportFormat(String reportFormat) {
	this.reportFormat = reportFormat;
    }

    
    public String getReportFile() {
	return reportFile;
    }

    
    public void setReportFile(String reportFile) {
	this.reportFile = reportFile;
    }

    
    public boolean isShowSuppressedViolations() {
	return showSuppressedViolations;
    }

    
    public void setShowSuppressedViolations(boolean showSuppressedViolations) {
	this.showSuppressedViolations = showSuppressedViolations;
    }

    
    public Properties getReportProperties() {
	return reportProperties;
    }

    
    public void setReportProperties(Properties reportProperties) {
	this.reportProperties = reportProperties;
    }

    
    public boolean isDebug() {
	return debug;
    }

    
    public void setDebug(boolean debug) {
	this.debug = debug;
    }

    
    public boolean isStressTest() {
	return stressTest;
    }

    
    public void setStressTest(boolean stressTest) {
	this.stressTest = stressTest;
    }

    
    public boolean isBenchmark() {
	return benchmark;
    }

    
    public void setBenchmark(boolean benchmark) {
	this.benchmark = benchmark;
    }
}