
package net.sourceforge.pmd.build;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


public class RuleSetToDocs implements PmdBuildTools {

	private String rulesetToDocsXsl = Config.getString("pmd.build.config.xsl.rulesetToDocs"); 
	private String mergeRulesetXsl = Config.getString("pmd.build.config.xsl.mergeRuleset"); 
	private String generateIndexXsl = Config.getString("pmd.build.config.xsl.rulesIndex"); 

	private String indexRuleSetFilename = Config.getString("pmd.build.config.index.filename"); 
	private String mergedRuleSetFilename = Config.getString("pmd.build.config.mergedRuleset.filename");

	private String rulesDirectory;
	private String targetDirectory;

	private Transformer transformer;
	private File target;


	
	public RuleSetToDocs() {
	}

	
	public String getRulesDirectory() {
		return rulesDirectory;
	}

	
	public void setRulesDirectory(String rulesDirectory) {
		this.rulesDirectory = rulesDirectory;
	}



	
	public String getRulesetToDocsXsl() {
		return rulesetToDocsXsl;
	}

	
	public void setRulesetToDocsXsl(String rulesetToDocsXsl) {
		this.rulesetToDocsXsl = rulesetToDocsXsl;
	}

	
	public String getMergeRulesetXsl() {
		return mergeRulesetXsl;
	}

	
	public void setMergeRulesetXsl(String mergeRulesetXsl) {
		this.mergeRulesetXsl = mergeRulesetXsl;
	}

	
	public String getGenerateIndexXsl() {
		return generateIndexXsl;
	}

	
	public void setGenerateIndexXsl(String generateIndexXsl) {
		this.generateIndexXsl = generateIndexXsl;
	}

	
	public String getIndexRuleSetFilename() {
		return indexRuleSetFilename;
	}

	
	public void setIndexRuleSetFilename(String indexRuleSetFilename) {
		this.indexRuleSetFilename = indexRuleSetFilename;
	}

	
	public String getMergedRuleSetFilename() {
		return mergedRuleSetFilename;
	}

	
	public void setMergedRuleSetFilename(String mergedRuleSetFilename) {
		this.mergedRuleSetFilename = mergedRuleSetFilename;
	}

	
	public void convertRulesets() throws PmdBuildException {
		init();
		File rulesDir = new File(rulesDirectory);
		if ( rulesDir.exists() && rulesDir.isDirectory() ) {
			File[] rulesets = rulesDir.listFiles(new RulesetFilenameFilter());
			for (int fileIterator = 0; fileIterator < rulesets.length; fileIterator++ )
			{
				File ruleset = rulesets[fileIterator];
				String targetName = this.targetDirectory + File.separator + ruleset.getName();
				System.out.println("Processing file " + ruleset + " into " + targetName); 
				try {
					convertRuleSet(ruleset,new File(targetName));
				} catch (ParserConfigurationException e) {
					throw new PmdBuildException(e);
				} catch (SAXException e) {
					throw new PmdBuildException(e);
				} catch (IOException e) {
					throw new PmdBuildException(e);
				} catch (TransformerException e) {
					throw new PmdBuildException(e);
				}
			}
		}
		else if ( ! rulesDir.exists() ) {
			throw new PmdBuildException("The rulesets directory specified '" + rulesDirectory + "' does not exist"); 
		}
		else if ( ! rulesDir.isDirectory() ) {
			throw new PmdBuildException("The rulesets directory '" + rulesDirectory + "' provided is not a directory !"); 
		}
	}

	
	private void init() throws PmdBuildException {
		
		System.out.println("Merge xsl:" + rulesetToDocsXsl);
		transformer = this.createTransformer(rulesetToDocsXsl);
		target = new File(targetDirectory);
		if ( (! target.exists() && ! target.mkdir()) ) {
			throw new PmdBuildException("Target directory '" +  target.getAbsolutePath() + "' does not exist and can't be created"); 
		}
		else if ( target.exists() && target.isFile() ) {
			throw new PmdBuildException("Target directory '" + target.getAbsolutePath() + "' already exist and is a file."); 
		}
	}

	
	private void convertRuleSet(File ruleset,File target) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		
		DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document =  parser.parse(ruleset);
		DOMSource xml =  new DOMSource(document);
		
		StreamResult fileResult= new StreamResult(target);
		
		this.transformer.transform(xml,fileResult);
	}

	
	public void generateRulesIndex() throws PmdBuildException, TransformerException {
		
		System.out.println("Merging all rules into " + this.mergedRuleSetFilename); 
		File mergedFile = new File(this.targetDirectory + File.separator + ".." + File.separator + mergedRuleSetFilename); 
		this.transformer = createTransformer(mergeRulesetXsl);
		StreamResult fileResult= new StreamResult(mergedFile);
		DOMSource xml = createXmlBackbone();
		this.transformer.transform(xml,fileResult);
		
		correctXmlMergeFile(mergedFile);
		System.out.println("Creating index file:" + this.indexRuleSetFilename); 
		this.transformer = createTransformer(generateIndexXsl);
		
		StreamSource src = new StreamSource(mergedFile);
		fileResult = new StreamResult(new File(this.targetDirectory + File.separator + indexRuleSetFilename));
		this.transformer.transform(src,fileResult);

	}

	private DOMSource createXmlBackbone() throws PmdBuildException {
		Document doc = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser = factory.newDocumentBuilder();
			doc = parser.newDocument();
		} catch (ParserConfigurationException e) {
			throw new PmdBuildException(e);
		}
		Element root = doc.createElement("root"); 
		doc = addingEachRuleset(doc,root);
		doc.appendChild(root);
		return new DOMSource(doc);
	}

	private Document addingEachRuleset(Document doc,Element root) {
		File rulesDir = new File(rulesDirectory);
		if ( rulesDir.exists() && rulesDir.isDirectory() ) {
			File[] rulesets = rulesDir.listFiles(new RulesetFilenameFilter());
			for (int fileIterator = 0; fileIterator < rulesets.length; fileIterator++ ) {
				File ruleset = rulesets[fileIterator];
				
				Element rulesetElement = doc.createElement("ruleset"); 
				
				rulesetElement.setAttribute("file",ruleset.getAbsolutePath()); 
				root.appendChild(rulesetElement);
			}
		}
		return doc;
	}

	private Transformer createTransformer(String xsl) throws PmdBuildException {
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			StreamSource src = new StreamSource(xsl);
			return factory.newTransformer(src);
		} catch (TransformerConfigurationException e) {
			throw new PmdBuildException(e);
		}
	}


	
	public String getTargetDirectory() {
		return targetDirectory;
	}

	
	public void setTargetDirectory(String targetDirectory) {
		this.targetDirectory = targetDirectory;
	}

	public static void deleteFile(File file) {
		if ( ! file.isDirectory() ) {
			file.delete();
		}
		else {
			File[] files = file.listFiles();
			for (int nbFile = 0; nbFile < files.length; nbFile++ )
				RuleSetToDocs.deleteFile(files[nbFile]);
			file.delete();
		}
	}

	private void correctXmlMergeFile(File file) {

		File tmp = new File(file + ".tmp"); 
		try {
			String line;
			FileWriter fw = new FileWriter(tmp);
			FileReader fr = new FileReader(file);
			BufferedWriter bw = new BufferedWriter(fw);
			BufferedReader br = new BufferedReader(fr);
			while (br.ready()) {
				line = br.readLine();
				line = line.replaceAll("xmlns=\"http://pmd.sf.net/ruleset/1.0.0\"", ""); 
				bw.write(line);
			}
			fr.close();
			bw.flush();
			fw.close();
			
			copy(tmp, file);
			tmp.delete();
		}
		
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void copy(File src, File dst) throws IOException
	{
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0)
			out.write(buf, 0, len);
		in.close();
		out.close();
	}

	public void setMergedRulesetFilename(String mergedRulesetFilename) {
		this.mergedRuleSetFilename = mergedRulesetFilename;

	}
}
