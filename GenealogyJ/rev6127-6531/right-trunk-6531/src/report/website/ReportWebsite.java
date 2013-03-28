package website;

import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.Media;
import genj.gedcom.MultiLineProperty;
import genj.gedcom.Note;
import genj.gedcom.Property;
import genj.gedcom.PropertyChange;
import genj.gedcom.PropertyComparator;
import genj.gedcom.PropertyDate;
import genj.gedcom.PropertyEvent;
import genj.gedcom.PropertyFamilyChild;
import genj.gedcom.PropertyFamilySpouse;
import genj.gedcom.PropertyFile;
import genj.gedcom.PropertyMedia;
import genj.gedcom.PropertyNote;
import genj.gedcom.PropertyRepository;
import genj.gedcom.PropertySex;
import genj.gedcom.PropertySource;
import genj.gedcom.PropertyXRef;
import genj.gedcom.Repository;
import genj.gedcom.Source;
import genj.gedcom.Submitter;
import genj.gedcom.MultiLineProperty.Iterator;
import genj.option.CustomOption;
import genj.option.Option;
import genj.report.Report;
import genj.util.Resources;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.security.InvalidParameterException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ReportWebsite extends Report {
	
	public boolean reportNotesInFullOnEntity = false;
	public boolean reportLinksToMap = true;
	public boolean reportNowLiving = false;
	public String reportIndexFileName = "index.html";
	public String listPersonFileName = "listing.html";
	public String listSourceFileName = "sources.html";
	public String listRepositoryFileName = "repositories.html";
	public boolean reportDisplayIndividualMap = true;
	public boolean omitXmlDeclaration = false;
    public String reportTitle = "Relatives";
    protected String reportWelcomeText = "On these pages my ancestors are presented";
    public boolean displaySosaStradonitz = false;
	protected HashMap<String, String> sosaStradonitzNumber = null; 
    public boolean displayGenJFooter = true;
	public String placeDisplayFormat = "all";
	public String secondaryLanguage = "en";
    public boolean removeAllFiles = false;

	
	protected static final String cssBaseFile = "html/style.css";

	
    public int treeType = 0;
    public String[] treeTypes = {translateLocal("treeLTR"), translateLocal("treeRTL")}; 
	protected static final String[] cssTreeFile = {"html/treel2r.css", "html/treer2l.css"};

    
	public String cssTextColor = "000";
	public String cssBackgroundColor = "FFF";
	public String cssLinkColor = "009";
	public String cssVistedLinkColor = "609";
	public String cssBorderColor = "000";

	
    public int boxBackground = 0;
    public String[] boxBackgrounds = {translateLocal("green"), translateLocal("blue")};
    protected static final String[] boxBackgroundImages = {"html/bkgr_green.png", "html/bkgr_blue.png"};
	
    
    protected List<Indi> personsWithImage = null;
    
    
    protected Element sourceDiv = null;
    protected List<Property> addedSourceProperty = null;
    protected int sourceCounter = 0;
    protected Element noteDiv = null;
    protected List<Property> addedNoteProperty = null;
    protected int noteCounter = 0;

    
    protected StringBuffer mapEventLocations = null;

     
	protected Locale currentLocale = null;   
	protected String currentLang = null; 
	
	protected Locale secondaryLocale = null;  
	protected Resources resources = null;
	
    
    File destDir = null;
    
    
	public void start(Gedcom gedcom) throws Exception {
		currentLang = Locale.getDefault().getLanguage();
		
		secondaryLocale = null;
		if (secondaryLanguage != null && !secondaryLanguage.equals("") && 
				!secondaryLanguage.equals(currentLang)) { 
			secondaryLocale = new Locale(secondaryLanguage);
			if (!secondaryLanguage.matches("[a-z]{2}") || secondaryLocale == null) {
				getOptionFromUser(translateLocal("invalidLanguage", new Object[]{secondaryLanguage}), OPTION_OK);
				return;
			} 
		}
		
		HashMap<String,String> translator;
		try {
			translator = makeCssAndJSSettings();
		} catch (InvalidParameterException e) {
			getOptionFromUser(e.getMessage(), OPTION_OK);
			return;
		}
		
		
		sosaStradonitzNumber = new HashMap<String, String>();
		personsWithImage = new ArrayList<Indi>();

		
		
		destDir = getDirectoryFromUser(translateLocal("qOutputDir"), translateLocal("qOk"));
		if (destDir == null) 
		  return; 
		
		
		destDir.mkdirs();
		
		
		if (destDir.list().length > 0) {
			if (! getOptionFromUser(translateLocal("qOverwrite"), OPTION_OKCANCEL)) 
				return; 
		}

		Indi rootIndi = null;
		if (displaySosaStradonitz) {
			rootIndi = (Indi)getEntityFromUser(translateLocal("selectSosaStradonitzRoot"), gedcom, Gedcom.INDI);
			makeSosaStradonitzNumbering(rootIndi, 1);
		}
		
		
		if (removeAllFiles) deleteDirContent(destDir, false);
		
		
		makeCss(destDir, translator);
		makeJs(destDir, translator);
		
		
		copyImages(destDir);
		
		generateFiles(gedcom, rootIndi);

		if (secondaryLocale != null) {
			Locale defaultLocale = Locale.getDefault();
			
			personsWithImage = new ArrayList<Indi>();
			
			currentLocale = secondaryLocale;
			currentLang = secondaryLocale.getLanguage();
			resources = new Resources(getClass().getResourceAsStream("/genj/gedcom/resources_"+currentLang+".properties"));
			translator = makeCssAndJSSettings();
			makeJs(destDir, translator);
			generateFiles(gedcom, rootIndi);
			currentLocale = null;

		}
	}

	  
	  public String getPropertyName(String tag) {
		  return getPropertyName(tag, false);
	  }

	  
	  public String getPropertyName(String tag, boolean plural) {
		  if (currentLocale != null) { 
			  if (plural) {
				  String name = resources.getString(tag+".s.name", false);
				  if (name!=null)
					  return name;
			  }
			  String name = resources.getString(tag+".name", false);
			  if (name!=null) return name;
		  }
		  return Gedcom.getName(tag, plural);
	  }

	
	
	protected void generateFiles(Gedcom gedcom, Indi rootIndi) throws Exception {
	    
		Entity[] objects = gedcom.getEntities(Gedcom.OBJE, "");
		for(Entity object : objects) {
			println("Exporting object " + object.getId());
			File objeFile = makeDirFor(object.getId());
			createMultimediaDoc((Media)object).toFile(objeFile, omitXmlDeclaration);
		}

	    
		Entity[] indis = gedcom.getEntities(Gedcom.INDI, "");
		for(Entity indi : indis) {
			println("Exporting person " + indi.getId() + " " + getName((Indi)indi));
			File indiFile = makeDirFor(indi.getId());
			createIndiDoc((Indi)indi).toFile(indiFile, omitXmlDeclaration);
		}
		
	    
		Entity[] sources = gedcom.getEntities(Gedcom.SOUR, "");
		for(Entity source : sources) {
			println("Exporting source " + source.getId());
			File sourFile = makeDirFor(source.getId());
			createSourceDoc((Source)source).toFile(sourFile, omitXmlDeclaration);
		}

	    
		Entity[] repos = gedcom.getEntities(Gedcom.REPO, "");
		for(Entity repo : repos) {
			println("Exporting repository " + repo.getId());
			File repoFile = makeDirFor(repo.getId());
			createRepoDoc((Repository)repo).toFile(repoFile, omitXmlDeclaration);
		}

	    
		Entity[] notes = gedcom.getEntities(Gedcom.NOTE, "");
		for(Entity note : notes) {
			println("Exporting note " + note.getId());
			File noteFile = makeDirFor(note.getId());
			createNoteDoc((Note)note).toFile(noteFile, omitXmlDeclaration);
		}

	    
		Entity[] submitters = gedcom.getEntities(Gedcom.SUBM, "");
		for(Entity submitter : submitters) {
			println("Exporting submitter " + submitter.getId());
			File submFile = makeDirFor(submitter.getId());
			createSubmitterDoc((Submitter)submitter).toFile(submFile, omitXmlDeclaration);
		}

		
		Collator collator = gedcom.getCollator();
		Arrays.sort(indis, new PropertyComparator("INDI:NAME"));
		Arrays.sort(sources, new EntityComparator());
		Arrays.sort(repos, new EntityComparator());
		makeStartpage(gedcom, destDir, indis, sources, repos, rootIndi);
		makePersonIndex(destDir, indis, collator);
		if (sources.length > 0)
			makeEntityIndex(destDir, sources, "sourceIndex", listSourceFileName, collator);
		if (repos.length > 0)
			makeEntityIndex(destDir, repos, "repositoryIndex", listRepositoryFileName, collator);
		makeSearchDataPage(destDir, indis);
		println("Report done!");		
	}
	
	
	protected void deleteDirContent(File dir, boolean deleteThisDir) {
		for(String name : dir.list()) {
			File curr = new File(dir, name);
			if (curr.isDirectory()) deleteDirContent(curr, true);
			else curr.delete();
		}
	}
	
	protected void makeSosaStradonitzNumbering(Indi person, int number) {
		sosaStradonitzNumber.put(person.getId(), Integer.toString(number));
		Fam fam = person.getFamilyWhereBiologicalChild();
		if (fam != null) {
			Indi father = fam.getHusband();
			if (father != null) makeSosaStradonitzNumbering(father, number * 2);
			Indi mother = fam.getWife();
			if (mother != null) makeSosaStradonitzNumbering(mother, number * 2 + 1);
		}
	}
	
	
	protected void copyImages(File dir) throws IOException {
		
		File sourceFile = new File(getFile().getParentFile(), boxBackgroundImages[boxBackground]);
		File dstFile = new File(dir, "bkgr.png");
		copyFile(sourceFile, dstFile);
		
		try {
			copyFile(new File(getFile().getParentFile(), "html/Indi.png"),
					new File(dir, "Indi.png"));
			copyFile(new File(getFile().getParentFile(), "html/Source.png"),
					new File(dir, "Source.png"));
			copyFile(new File(getFile().getParentFile(), "html/Repository.png"),
					new File(dir, "Repository.png"));
		} catch (Exception e) {
			println(" Failed to copy icons. Error:" + e.getMessage());
		}
	}

	
	protected List<? extends Option> getCustomOptions() {
		return Collections.singletonList(new TextAreaOption());
	}
	
	private class TextAreaOption extends CustomOption {
		private JTextArea text = new JTextArea(reportWelcomeText);
		protected JComponent getEditor() {
			text.setLineWrap(true);
			return new JScrollPane(text);
		}
		protected void commit(JComponent editor) {
			reportWelcomeText = text.getText();
		}
		public String getName() {
			return translateLocal("reportWelcomeText");
		}
		public String getToolTip() {
			return "Enter your page description here. It will be enclosed in <p>-tags.";
		}
		public void persist() {
			getRegistry().put("reportWelcomeText", text.getText());
		}
		public void restore() {
			text.setText(getRegistry().get("reportWelcomeText", ""));
		}
	}

	protected void makeSearchDataPage(File dir, Entity[] indis) throws IOException {
		println("Making search data file");
		File file = new File(dir, "searchData.js");
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8")); 
			out.write("var searchValues = [");
			boolean first = true;
			for (Entity indi : indis) {
				if (!first) {
					out.write(",");
					out.newLine();
				}
				first = false;
				String displayName = ((Indi)indi).getName().replace('"', ' ');
				String simpleName = displayName.toLowerCase();
				String sosaId = sosaStradonitzNumber.get(indi.getId());
				if (sosaId == null) sosaId = "";
				out.write("[\"" + simpleName + "\",\"" + indi.getId().substring(1) + "\",\"" + displayName + "\",\"" + sosaId + "\"]");
			}
			out.write("];");
		} finally {
			if (out != null) out.close();
		}
	}
	
	protected void makeStartpage(Gedcom gedcom, File dir, Entity[] indis, Entity[] sources, Entity[] repos, Indi rootIndi) {
		println("Making start-page");
		Collator collator = gedcom.getCollator();
		File startFile = new File(dir.getAbsolutePath() + File.separator + getLocalizedFilename(reportIndexFileName, currentLocale));
		Html html = new Html(reportTitle, "", currentLang);
		Document doc = html.getDoc();
		Element bodyNode = html.getBody();
		bodyNode.appendChild(html.h1(reportTitle));
		bodyNode.appendChild(html.pNewlines(reportWelcomeText));
		Element div1 = html.div("left");
		bodyNode.appendChild(div1);
		
		div1.appendChild(html.h2("Indi.png", getPropertyName("INDI", true)));
		div1.appendChild(html.p(translateLocal("indexPersonText1", 
				new Object[]{indis.length, gedcom.getEntities(Gedcom.FAM, "").length})));
		if (displaySosaStradonitz) {
			Element p = html.p(translateLocal("indexSosaDescriptionText") + " ");
			p.appendChild(html.link(addressTo(rootIndi.getId()), getName(rootIndi)));
			div1.appendChild(p);
		}

		div1.appendChild(html.h2(translateLocal("personIndex")));
		Element indiP = html.p();
		div1.appendChild(indiP);
		String lastLetter = "";
		for (Entity indi : indis) {
			String lastname = ((Indi)indi).getLastName();  
			String letter = "?";
			if (lastname != null && !lastname.isEmpty()) letter = lastname.substring(0, 1); 
			if (! collator.equals(letter, lastLetter)) {
				indiP.appendChild(html.link(listPersonFileName + "#" + letter, letter));				
				indiP.appendChild(html.text(", "));				
				lastLetter = letter;
			}
		}
		
		
		html.addJSFile(getLocalizedFilename("search.js", currentLocale));
		html.addJSFile("searchData.js");
		
		Element searchForm = html.form(null, "javascript:displayResult();", "return displayResult();"); 
		Element searchP = html.p(getPropertyName("NAME") + " ");
		searchForm.appendChild(searchP);
		searchP.appendChild(html.input("searchName", "name")); 
		searchP.appendChild(html.button(translateLocal("searchButton"), "displayResult();")); 
		div1.appendChild(searchForm);
		if (displaySosaStradonitz) {
			Element searchSosaForm = html.form(null, "javascript:jumpToSosa();", "return jumpToSosa();"); 
			Element searchSosaP = html.p(translateLocal("sosaNumber") + " ");
			searchSosaForm.appendChild(searchSosaP);
			searchSosaP.appendChild(html.input("searchSosa", "sosa", 5)); 
			searchSosaP.appendChild(html.button(translateLocal("searchButton"), "jumpToSosa();")); 
			div1.appendChild(searchSosaForm);
		}
		Element divResult = html.divId("searchResult");
		divResult.appendChild(html.text(" "));
		div1.appendChild(divResult);
		
		div1.appendChild(html.h2(translateLocal("personGallery")));
		for (Indi indi : personsWithImage) { 
			div1.appendChild(html.link(addressTo(indi.getId()), html.img(addressToDir(indi.getId()) + "gallery.jpg", getName(indi))));				
		}

		Element div2 = html.div("left");
		bodyNode.appendChild(div2);
		
		if (sources.length > 0) {
			div2.appendChild(html.h2("Source.png", translateLocal("sourceIndex")));
			Element sourceP = html.p();
			div2.appendChild(sourceP);
			lastLetter = "";
			for (Entity source : sources) { 
				String letter = source.toString().substring(0, 1); 
				if (! collator.equals(letter, lastLetter)) {
					sourceP.appendChild(html.link(listSourceFileName + "#" + letter, letter));				
					sourceP.appendChild(html.text(", "));				
					lastLetter = letter;
				}
			}
			bodyNode.appendChild(div2);
		}

		if (repos.length > 0) {
			div2.appendChild(html.h2("Repository.png", translateLocal("repositoryIndex")));
			Element repoP = html.p();
			div2.appendChild(repoP);
			lastLetter = "";
			for (Entity repo : repos) { 
				String letter = repo.toString().substring(0, 1); 
				if (! collator.equals(letter, lastLetter)) {
					repoP.appendChild(html.link(listRepositoryFileName + "#" + letter, letter));				
					repoP.appendChild(html.text(", "));				
					lastLetter = letter;
				}
			}
			bodyNode.appendChild(div2);
			
		}

		
		Submitter subm = gedcom.getSubmitter();
		if (subm != null) {
			div2.appendChild(html.h2(translateLocal("dataGatheredBy")));
			Element p = html.p(subm.getName() + ", ");
			div2.appendChild(p);
			processAddresses(p, subm, html, new ArrayList<String>(), false);
		}
		div2.appendChild(html.p(translateLocal("pageCreated") + 
				" " + (new PropertyChange()).getDisplayValue())); 
		
		Element backlink = backlink(reportIndexFileName, null, "", html);
		if (backlink.hasChildNodes()) bodyNode.appendChild(backlink);
		makeFooter(bodyNode, html);
		html.toFile(startFile, omitXmlDeclaration);
	}

	protected void makeEntityIndex(File dir, Entity[] sources, String name, String fileName, Collator collator) {
		name = translateLocal(name);
		println("Making "+ name);
		File startFile = new File(dir.getAbsolutePath() + File.separator + getLocalizedFilename(fileName, currentLocale));
		Html html = new Html(name, "", currentLang);
		Document doc = html.getDoc();
		Element bodyNode = html.getBody();
		bodyNode.appendChild(backlink(fileName, null, "", html));
		bodyNode.appendChild(html.h1(name));
		Element div1 = html.div("left");
		bodyNode.appendChild(div1);
		String lastLetter = "";
		for (Entity source : sources) { 
			String text = source.toString();
			String letter = text.substring(0, 1); 
			if (! collator.equals(letter, lastLetter)) {
				div1.appendChild(html.anchor(letter));
				div1.appendChild(html.h2(letter));
				lastLetter = letter;
			}
			div1.appendChild(html.link(addressTo(source.getId()), text));
			div1.appendChild(html.br());
		}				
		makeFooter(bodyNode, html);
		html.toFile(startFile, omitXmlDeclaration);
	}

	protected void makePersonIndex(File dir, Entity[] indis, Collator collator) {
		println("Making person index");
		File startFile = new File(dir.getAbsolutePath() + File.separator + 
				getLocalizedFilename(listPersonFileName, currentLocale));
		Html html = new Html(translateLocal("personIndex"), "", currentLang);
		Document doc = html.getDoc();
		Element bodyNode = html.getBody();
		bodyNode.appendChild(backlink(listPersonFileName, null, "", html));
		bodyNode.appendChild(html.h1(translateLocal("personIndex")));
		Element div1 = html.div("left");
		bodyNode.appendChild(div1);
		String lastLetter = "";
		for (Entity indi : indis) { 
			String lastname = ((Indi)indi).getLastName();  
			String letter = "?";
			if (lastname != null && !lastname.isEmpty()) letter = lastname.substring(0, 1); 
			if (! collator.equals(letter, lastLetter)) {
				div1.appendChild(html.anchor(letter));
				div1.appendChild(html.h2(letter));
				lastLetter = letter;
			}
			String text = getName((Indi)indi) + " (";
			if (!isPrivate((Indi)indi)) {
				PropertyDate birth = ((Indi)indi).getBirthDate();
				if (birth != null && birth.getStart().isValid()) text += birth.getStart().getYear();
				text += " - ";
				PropertyDate death = ((Indi)indi).getDeathDate();
				if (death != null && death.getStart().isValid()) text += death.getStart().getYear();
			} else {
				text += translateLocal("notPublic");
			}
			text += ")";
			div1.appendChild(html.link(addressTo(indi.getId()), text));
			div1.appendChild(html.br());
		}				
		makeFooter(bodyNode, html);
		html.toFile(startFile, omitXmlDeclaration);
	}

	protected class EntityComparator implements Comparator<Entity> {
		@Override
		public int compare(Entity arg0, Entity arg1) {
			return arg0.toString().compareTo(arg1.toString());
		}
	}

	
	protected File makeDirFor(String id) throws Exception {
		String path = addressTo(id);
		
		String fileSep = File.separator;
		if (fileSep.equals("\\")) {
			fileSep = "\\\\"; 
		}
		path = path.replaceAll("/", fileSep);
		File indiFile = new File(destDir.getAbsolutePath() + File.separator + path);
		File indiDir = indiFile.getParentFile();
		indiDir.mkdirs();
		return indiFile;
	}

	protected String makeDescription(Indi indi, boolean isPrivate) {
		StringBuffer pageDescription = new StringBuffer(indi.getName());
		if (! isPrivate) {
			String birth = makeDescriptionEvent((PropertyEvent)indi.getProperty("BIRT"));
			if (birth != null) pageDescription.append(", ").append(birth);
			String death = makeDescriptionEvent((PropertyEvent)indi.getProperty("DEAT"));
			if (death != null) pageDescription.append(", ").append(death);
		}
		Fam fam = indi.getFamilyWhereBiologicalChild();
		if (fam != null) {
			pageDescription.append(", ").append(translateLocal("parents")).append(":");
			Indi father = fam.getHusband();
			if (father != null) pageDescription.append(' ').append(father.getName());
			Indi mother = fam.getWife();
			if (mother != null) pageDescription.append(' ').append(mother.getName());
		}
		Fam[] spouseFams = indi.getFamiliesWhereSpouse();
		if (spouseFams.length > 0) {
			pageDescription.append(", ").append(translateLocal("spouses"));
			for (Fam spouseFam : spouseFams) {
				Indi spouse = spouseFam.getOtherSpouse(indi);
				if (spouse != null) pageDescription.append(' ').append(spouse.getName());
			}
		}
		return pageDescription.toString();
	}
	
	protected String makeDescriptionEvent(PropertyEvent event) {
		if (event == null) return null;
		Property date = event.getProperty("DATE");
		Property place = event.getProperty("PLAC");
		if (date == null && place == null) return null;
		if (date == null) return getPropertyName(event.getTag()) + ": " + place.getDisplayValue();	
		if (place == null) return getPropertyName(event.getTag()) + ": " + date.getDisplayValue();
		else return getPropertyName(event.getTag()) + ": " + 
		  date.getDisplayValue() + " " + place.getDisplayValue();
	}
	
	
	
	protected Html createIndiDoc(Indi indi) {
		List<String> handledProperties = new ArrayList<String>();
		resetNoteAndSourceList();

		String linkPrefix = relativeLinkPrefix(indi.getId());

		
		boolean isPrivate = isPrivate(indi); 

		if (! isPrivate) mapEventLocations = new StringBuffer();

		Html html = new Html(getName(indi), linkPrefix, currentLang);
		Document doc = html.getDoc();
		Element bodyNode = html.getBody();
		html.setDescription(makeDescription(indi, isPrivate));
		
		addDecendantTree(bodyNode, indi, "", linkPrefix, html);

		Property[] names = indi.getProperties("NAME");
		for (Property name : names) {
			Element h1 = html.h1(getName(indi, name));
			bodyNode.appendChild(h1);
			if (! isPrivate) {
				processSourceRefs(h1, name, linkPrefix, indi.getId(), html);
				processNoteRefs(h1, name, linkPrefix, indi.getId(), html);
			}
			Property nick = name.getProperty("NICK");
			if (nick != null) {
				bodyNode.appendChild(html.p(getPropertyName("NICK") + ": " + nick.getDisplayValue()));
			}
			String constructedName = constructName(name); 
			if (constructedName != null) bodyNode.appendChild(html.p(constructedName));

			for (String subTag : new String[] {"FONE", "ROMN"}) {
				Property fone = name.getProperty(subTag);
				if (fone != null) {
					String type = "";
					Property typeProp = fone.getProperty("TYPE"); 
					if (typeProp != null) type = typeProp.getDisplayValue(); 
					Element p = html.p(getPropertyName(subTag) + " " + type + ": " + typeProp.getDisplayValue());
					bodyNode.appendChild(p);
					Property foneNick = name.getProperty("NICK");
					String constructedFoneName = constructName(fone); 
					if (constructedFoneName != null) p.appendChild(html.text(", " + constructedName));
					if (foneNick != null) p.appendChild(html.text(", " + 
							getPropertyName("NICK") + " " + foneNick.getDisplayValue()));
					if (! isPrivate) {
						processSourceRefs(p, fone, linkPrefix, indi.getId(), html);
						processNoteRefs(p, fone, linkPrefix, indi.getId(), html);
					}
					reportUnhandledProperties(foneNick, new String[] {"TYPE", "SOUR", "NOTE", "NICK", "NPFX", "GIVN", "SPFX", "SURN", "NSFX"});
				}
			}

			reportUnhandledProperties(name, new String[]{"SOUR", "NOTE", "NICK", "NPFX", "GIVN", "SPFX", "SURN", "NSFX"});
		}
		if (names == null) bodyNode.appendChild(html.h1("("+translateLocal("unknown")+")"));
		handledProperties.add("NAME");

		Element div1 = null;
		if (! isPrivate) {
			div1 = html.div("left");
			bodyNode.appendChild(div1);

			div1.appendChild(html.h2(translateLocal("facts")));
			
			Property sex = indi.getProperty("SEX");
			if (sex != null) {
				div1.appendChild(html.p(getPropertyName("SEX") + ": " + 
						PropertySex.getLabelForSex(indi.getSex())));
				reportUnhandledProperties(sex, null);
			}
			handledProperties.add("SEX");
			
			Element birth = processEventDetail((PropertyEvent)indi.getProperty("BIRT"), 
					linkPrefix, indi.getId(), html, true); 
			if (birth != null) div1.appendChild(birth);
			handledProperties.add("BIRT");
			Element death = processEventDetail((PropertyEvent)indi.getProperty("DEAT"), 
					linkPrefix, indi.getId(), html, true); 
			if (death != null) div1.appendChild(death);
			handledProperties.add("DEAT");  

			for (String tag : new String[]{"CAST", "DSCR", "EDUC", "IDNO", "NATI", "NCHI", "NMR", "OCCU", "PROP", "RELI", "RESI", "SSN", "TITL", "FACT",
					"ADOP", "CHR", "CREM", "BURI", "BAPM", "BARM", "BASM", "BLES", "CHRA", "CONF", "FCOM", "ORDN", "NATU", "EMIG", "IMMI", "CENS", "PROB", "WILL", "GRAD", "RETI", "EVEN"}) {
				processOtherEventTag(tag, indi, linkPrefix, indi.getId(), div1, html);
				handledProperties.add(tag);  
			}
			for (String tag : new String[]{"SUBM", "ALIA", "ANCI", "DESI"}) {
				Property[] refs = indi.getProperties(tag);
				if (refs.length > 0) {
					div1.appendChild(html.h2(getPropertyName(tag)));
					Element p = html.p();
					for (Property ref : refs) {
						if (ref instanceof PropertyXRef) {
							getReferenceLink((PropertyXRef)ref, p, linkPrefix, html, false);
							if (p.hasChildNodes()) div1.appendChild(p);
							reportUnhandledProperties(ref, null); 
						} else {
							println(tag + " is not reference:" + ref.toString());
						}
					}
				}
				handledProperties.add(tag);  
			}
			Property[] refs = indi.getProperties("ASSO");
			if (refs.length > 0) {
				div1.appendChild(html.h2(getPropertyName("ASSO")));
				for (Property ref : refs) {
					if (ref instanceof PropertyXRef) {
						Property relation = ref.getProperty("RELA"); 
						Element p = html.p(relation.getDisplayValue() + ": ");
						getReferenceLink((PropertyXRef)ref, p, linkPrefix, html, false);
						if (p.hasChildNodes()) div1.appendChild(p);
						processNoteRefs(p, ref, linkPrefix, indi.getId(), html);
						processSourceRefs(p, ref, linkPrefix, indi.getId(), html);
						reportUnhandledProperties(ref, new String[] {"RELA", "NOTE", "SOUR"});
					} else {
						println("ASSO is not reference:" + ref.toString());
					}
				}
				handledProperties.add("ASSO");  
			}
			
			
			Element p = processMultimediaLink(indi, linkPrefix, indi.getId(), html, false, true);
			if (p != null) div1.appendChild(p);
			handledProperties.add("OBJE");

			
			processSimpleTag(indi, "RESN", div1, html, handledProperties);
			
			
		}
		
		
		
		Element div2 = html.div("right");
		bodyNode.appendChild(div2);
		
		
		div2.appendChild(html.h2(translateLocal("parents")));
		List<PropertyFamilyChild> famRefs = indi.getProperties(PropertyFamilyChild.class);
		if (famRefs.isEmpty()) {
			div2.appendChild(html.p(translateLocal("unknown")));
		} else {
			for (PropertyFamilyChild famRef : famRefs) {
				Fam fam = famRef.getFamily();
				Element p = html.p();
				div2.appendChild(p);
				Boolean bio = famRef.isBiological();
				if (! (bio == null || bio.booleanValue())) {
				    Property pedi = famRef.getProperty("PEDI");
				    if (pedi!=null) {
				    	p.appendChild(html.text(pedi.getValue() + ": "));
				    	p.appendChild(html.br());
				    }
				}
				Property status = famRef.getProperty("STAT");
				if (status != null) {
			    	p.appendChild(html.text(getPropertyName("STAT") + ": " + status.getDisplayValue()));
			    	p.appendChild(html.br());
				}
				getReferenceLink(famRef, p, linkPrefix, html, true);
				processNoteRefs(p, famRef, linkPrefix, indi.getId(), html);
				reportUnhandledProperties(famRef, new String[]{"PEDI", "NOTE"});
			}
		}
		handledProperties.add("FAMC");

		if (! isPrivate) {
			
		}

		
		List<PropertyFamilySpouse> famss = indi.getProperties(PropertyFamilySpouse.class);
		if (!famss.isEmpty()) {
			for (PropertyFamilySpouse pfs : famss) {
				Element h2 = html.h2(getPropertyName("FAM") + " - ");
				div2.appendChild(h2);
				Fam fam = pfs.getFamily();
				if (fam == null) {
					println(" Reference to invalid family: " + pfs.getValue());
					continue;
				}

				Indi spouse = fam.getOtherSpouse(indi);
				if (spouse != null) {
					h2.appendChild(html.link(linkPrefix + addressTo(spouse.getId()),getName(spouse)));
				} else {
					h2.appendChild(html.text(translateLocal("unknown")));
				}
				
				processNoteRefs(h2, pfs, linkPrefix, fam.getId(), html);
				
				List<String>handledFamProperties = new ArrayList<String>();
				handledFamProperties.add("HUSB");
				handledFamProperties.add("WIFE");
				if (! isPrivate) {
					
					for (String tag : new String[] {"ENGA", "MARR", "MARB", "MARC", "MARL", "MARS", "EVEN", "ANUL", "CENS", "DIV", "DIVF"}) {
						for (Property event : fam.getProperties(tag)) {
							div2.appendChild(processEventDetail(event, linkPrefix, fam.getId(), html, true));
						}
						handledFamProperties.add(tag);
					}
					
					for (String tag : new String[] {"NCHI", "RESN"}) {
						Property singleTag = fam.getProperty(tag);
						if (singleTag != null) {
							div2.appendChild(html.text(getPropertyName(tag) + ": " + singleTag.getDisplayValue()));
						}
						handledFamProperties.add(tag);
					}
					Element images = processMultimediaLink(fam, linkPrefix, fam.getId(), html, true, false);
					if (images != null)	div2.appendChild(images);
					handledFamProperties.add("OBJE");
					handledFamProperties.add("CHIL"); 
					for (String tag : new String[]{"SUBM"}) {
						Property[] refs = indi.getProperties(tag);
						if (refs.length > 0) {
							div2.appendChild(html.h2(getPropertyName(tag)));
							Element p = html.p();
							for (Property ref : refs) {
								if (ref instanceof PropertyXRef) {
									getReferenceLink((PropertyXRef)ref, p, linkPrefix, html, false);
									if (p.hasChildNodes()) div2.appendChild(p);
									reportUnhandledProperties(ref, null); 
								} else {
									println(tag + " is not reference:" + ref.toString());
								}
							}
						}
						handledFamProperties.add(tag);  
					}

					
				}
				Indi[] children = fam.getChildren(true);
				if (children.length > 0) {
					div2.appendChild(html.p(getPropertyName("CHIL", true) + ":"));
					Element childrenList = doc.createElement("ul");
					for (Indi child : children) {
						Element childEl = doc.createElement("li");
						childEl.appendChild(html.link(linkPrefix + addressTo(child.getId()), getName(child)));
						childrenList.appendChild(childEl);
					}
					div2.appendChild(childrenList);
				}
				reportUnhandledProperties(pfs, null);
				if (!isPrivate) processNumberNoteSourceChangeRest(fam, linkPrefix, div2, fam.getId(), html, handledFamProperties, false);
			}
		}
		handledProperties.add("FAMS");

		if (!isPrivate) {
			processNumberNoteSourceChangeRest(indi, linkPrefix, div1, indi.getId(), html, handledProperties, true);

			
			if (reportDisplayIndividualMap) {
				if (mapEventLocations != null && mapEventLocations.length() > 0) {
					String url = "http://maps.google.com/maps/api/staticmap?size=200x200&maptype=roadmap&sensor=false&markers=";
					div1.appendChild(html.img(url + mapEventLocations.toString(), ""));
				}
			}
		}
		mapEventLocations = null;
		
		addNoteAndSourceList(bodyNode);
		
		
		bodyNode.appendChild(backlink(reportIndexFileName, listPersonFileName, linkPrefix, html));
		makeFooter(bodyNode, html);
		return html;
	}

	
	protected String constructName(Property nameProp) {
		StringBuffer sb = new StringBuffer();
		for (String tag : new String[] {"NPFX", "GIVN", "SPFX", "SURN", "NSFX"}) {
			for (Property subProp : nameProp.getProperties(tag)) {
				if (sb.length() > 0) sb.append(' ');
				sb.append(subProp.getDisplayValue());
			}
		}
		if (sb.length() > 0) return sb.toString();
		else return null;
	}
	
	
	
	protected Html createSourceDoc(Source source) {
		List<String> handledProperties = new ArrayList<String>();
		resetNoteAndSourceList();
		
		String linkPrefix = relativeLinkPrefix(source.getId());

		Html html = new Html(getPropertyName("SOUR") + " " + source.getId() + ": " + source.getTitle(), 
				linkPrefix, currentLang);
		Document doc = html.getDoc();
		Element bodyNode = html.getBody();
		
		bodyNode.appendChild(html.h1(source.getTitle()));
		Element div1 = html.div("left");
		bodyNode.appendChild(div1);
		handledProperties.add("TITL");

		processSimpleTags(source, new String[] {"TEXT", "AUTH", "ABBR", "PUBL"}, div1, html, handledProperties);

		
		for (PropertyRepository repo : source.getProperties(PropertyRepository.class)) {
			div1.appendChild(html.h2(getPropertyName("REPO")));
			Element p = html.p();
			div1.appendChild(p);
			
			Repository ent = (Repository)repo.getTargetEntity();
			
			p.appendChild(html.link(linkPrefix + addressTo(ent.getId()), ent.toString()));
			
			for (Property caln : repo.getProperties("CALN")) {
				p.appendChild(html.text(", " + caln.getDisplayValue()));
				Property medi = caln.getProperty("MEDI");
				if (medi != null) p.appendChild(html.text(medi.getDisplayValue()));
				reportUnhandledProperties(caln, new String[] {"MEDI"});
			}
			
			processNoteRefs(div1, repo, linkPrefix, source.getId(), html);
			
			reportUnhandledProperties(repo, new String[] {"NOTE", "CALN"});
		}
		handledProperties.add("REPO");
		
		
		Property data = source.getProperty("DATA");
		if (data !=null) {
			div1.appendChild(html.h2(getPropertyName("DATA")));
			for (Property event : data.getProperties("EVEN")) {
				Element p = html.p(getPropertyName("EVEN") + ": ");
				for (String eventType : event.getValue().split(",")) {
					p.appendChild(html.text(getPropertyName(eventType.trim()) + " "));
				}
				
				Property date = event.getProperty("DATE");
				if (date != null) 
					p.appendChild(html.text(date.getDisplayValue() + " "));
				
				Property placeProp = event.getProperty("PLAC");
				Element place = processPlace(placeProp, linkPrefix, source.getId(), html);
				if (place != null) p.appendChild(place);
				reportUnhandledProperties(event, new String[] {"DATE", "PLAC"});
			}
			Property agency = data.getProperty("AGNC");
			if (agency != null) {
				div1.appendChild(html.p(getPropertyName("AGNC") + ": " + agency.getDisplayValue()));
			}
			this.processNoteRefs(div1, data, linkPrefix, source.getId(), html);
			reportUnhandledProperties(data, new String[] {"EVEN", "AGNC", "NOTE"});
		}
		handledProperties.add("DATA");
		
		
		Element images = processMultimediaLink(source, linkPrefix, source.getId(), html, false, false);
		if (images != null) {			
			div1.appendChild(html.h2(translateLocal("images")));
			div1.appendChild(images);
		}
		handledProperties.add("OBJE");
		
		Element div2 = html.div("right");
		bodyNode.appendChild(div2);
		processReferences(source, linkPrefix, div2, html, handledProperties);
		
		processNumberNoteSourceChangeRest(source, linkPrefix, div1, source.getId(), html, handledProperties, true);
		addNoteAndSourceList(bodyNode);

		bodyNode.appendChild(backlink(reportIndexFileName, listSourceFileName, linkPrefix, html));
		makeFooter(bodyNode, html);
		return html;
	}

	protected Html createRepoDoc(Repository repo) {
		List<String> handledProperties = new ArrayList<String>();
		String linkPrefix = relativeLinkPrefix(repo.getId());
		resetNoteAndSourceList();

		Html html = new Html(getPropertyName("REPO") + " " + repo.getId() + ": " + repo.toString(), 
				linkPrefix, currentLang);
		Document doc = html.getDoc();
		Element bodyNode = html.getBody();
		
		bodyNode.appendChild(html.h1(repo.toString()));
		Element div1 = html.div("left");
		bodyNode.appendChild(div1);
		handledProperties.add("NAME");

		
		processAddresses(div1, repo, html, handledProperties, true);

		
		Element div2 = html.div("right");
		processReferences(repo, linkPrefix, div2, html, handledProperties);
		if (div2.hasChildNodes()) bodyNode.appendChild(div2);
		
		processNumberNoteSourceChangeRest(repo, linkPrefix, div1, repo.getId(), html, handledProperties, true);
		addNoteAndSourceList(bodyNode);

		bodyNode.appendChild(backlink(reportIndexFileName, listRepositoryFileName, linkPrefix, html));
		makeFooter(bodyNode, html);
		return html;
	}

	protected Html createMultimediaDoc(Media object) {
		List<String> handledProperties = new ArrayList<String>();
		String linkPrefix = relativeLinkPrefix(object.getId());
		resetNoteAndSourceList();

		Html html = new Html(getPropertyName("OBJE") + " " + object.getId() + ": " + object.toString(), 
				linkPrefix, currentLang);
		Document doc = html.getDoc();
		Element bodyNode = html.getBody();

		bodyNode.appendChild(html.h1(object.getTitle()));
		
		Element div1 = html.div("left");
		bodyNode.appendChild(div1);
		
		processSimpleTag(object, "TITL", div1, html, handledProperties);
		processSimpleTag(object, "FORM", div1, html, handledProperties);
		
		

		
		Element p = html.p();
		for (PropertyFile file : object.getProperties(PropertyFile.class)) {
			
			String title = null;
			Property titleProp = file.getProperty("TITL");
			if (titleProp != null) {
				title = titleProp.getDisplayValue();
				reportUnhandledProperties(titleProp, null);
			}

			boolean tryMakeThumb = true;
			boolean thumbMade = false;
			
			
			Property formProp = file.getProperty("FORM");
			if (formProp != null) {
				if (! formProp.getValue().matches("^jpe?g|gif|JPE?G|gif|PNG|png$")) {
					tryMakeThumb = false;
				}
				Property type = formProp.getProperty("TYPE");
				if (type != null) {
					
					reportUnhandledProperties(type, null);
				}
				reportUnhandledProperties(formProp, new String[] {"TYPE"});
			}
			
			int imgSize = 100;
			
			File srcFile = file.getFile();
			if (srcFile != null) {
				File objectDir = new File(destDir, addressToDir(object.getId()));
				File dstFile = new File(objectDir, srcFile.getName());
				File thumbFile = new File(dstFile.getParentFile(), "thumb_" + dstFile.getName());
				try {
					if (!dstFile.exists() || srcFile.lastModified() > dstFile.lastModified()) {
						copyFile(srcFile, dstFile);
					}
					
					if (tryMakeThumb) {
						if (!thumbFile.exists() || srcFile.lastModified() > thumbFile.lastModified()) {
							try { 
								makeThumb(dstFile, imgSize, imgSize, thumbFile);
								thumbMade = true;
							} catch (Exception e) {
								println("Failed maiking thumb of:" + dstFile.getPath() + " Error:" + e.getMessage());
							}
						} else {
							thumbMade = true;
						}
					}

					
					if (thumbMade) p.appendChild(html.link(dstFile.getName(), html.img(thumbFile.getName(), title)));
					else p.appendChild(html.link(dstFile.getName(), title));
				} catch (IOException e) {
					println(" Error in copying file or making thumb: " + 
							srcFile.getName() + e.getMessage());
				}
			} else {
				println(" FILE ref but no file was found");
			}
			reportUnhandledProperties(file, new String[] {"TITL", "FORM"});
		}
		if (p.hasChildNodes()) div1.appendChild(p);
		handledProperties.add("FILE");
		
		
		Element div2 = html.div("right");
		processReferences(object, linkPrefix, div2, html, handledProperties);
		if (div2.hasChildNodes()) bodyNode.appendChild(div2);

		processNumberNoteSourceChangeRest(object, linkPrefix, div1, object.getId(), html, handledProperties, true);
		addNoteAndSourceList(bodyNode);
		bodyNode.appendChild(backlink(reportIndexFileName, null, linkPrefix, html));
		makeFooter(bodyNode, html);
		return html;
	}
	
	protected Html createNoteDoc(Note note) {
		List<String> handledProperties = new ArrayList<String>();
		String linkPrefix = relativeLinkPrefix(note.getId());
		resetNoteAndSourceList();

		Html html = new Html(getPropertyName("NOTE") + " " + note.getId() + ": " + note.toString(), 
				linkPrefix, currentLang);
		Document doc = html.getDoc();
		Element bodyNode = html.getBody();

		bodyNode.appendChild(html.h1(getPropertyName("NOTE") + note.getId() + ": " + note.toString()));
		
		Element div1 = html.div("left");
		bodyNode.appendChild(div1);

		
		appendDisplayValue(div1, note, false, html);

		
		Element div2 = html.div("right");
		processReferences(note, linkPrefix, div2, html, handledProperties);
		if (div2.hasChildNodes()) bodyNode.appendChild(div2);
		
		processNumberNoteSourceChangeRest(note, linkPrefix, div1, note.getId(), html, handledProperties, true);
		addNoteAndSourceList(bodyNode);
		bodyNode.appendChild(backlink(reportIndexFileName, null, linkPrefix, html));
		makeFooter(bodyNode, html);
		return html;
	}

	protected Html createSubmitterDoc(Submitter submitter) {
		List<String> handledProperties = new ArrayList<String>();
		String linkPrefix = relativeLinkPrefix(submitter.getId());
		resetNoteAndSourceList();

		Html html = new Html(getPropertyName("SUBM") + " " + submitter.getId() + ": " + submitter.getName(), 
				linkPrefix, currentLang);
		Document doc = html.getDoc();
		Element bodyNode = html.getBody();

		bodyNode.appendChild(html.h1(submitter.getName()));
		handledProperties.add("NAME");
		
		Element div1 = html.div("left");
		bodyNode.appendChild(div1);

		
		processAddresses(div1, submitter, html, handledProperties, true);

		
		processSimpleTag(submitter, "LANG", div1, html, handledProperties);
		
		
		Element images = processMultimediaLink(submitter, linkPrefix, submitter.getId(), html, true, false);
		if (images != null) div1.appendChild(images);
		handledProperties.add("OBJE");
		
		
		Element div2 = html.div("right");
		processReferences(submitter, linkPrefix, div2, html, handledProperties);
		if (div2.hasChildNodes()) bodyNode.appendChild(div2);

		processNumberNoteSourceChangeRest(submitter, linkPrefix, div1, submitter.getId(), html, handledProperties, true);
		addNoteAndSourceList(bodyNode);
		bodyNode.appendChild(backlink(reportIndexFileName, null, linkPrefix, html));
		makeFooter(bodyNode, html);
		return html;
	}

	protected Element backlink(String currentFileName, String indexFileName, String linkPrefix, Html html) {
		Element divlink = html.div("backlink");
		if (!linkPrefix.equals("") || !currentFileName.equals(reportIndexFileName)) { 
			divlink.appendChild(html.link(linkPrefix + getLocalizedFilename(reportIndexFileName, currentLocale), 
					translateLocal("startPage")));
		}
		if (indexFileName != null) {
			divlink.appendChild(html.text(" "));
			divlink.appendChild(html.link(linkPrefix + getLocalizedFilename(indexFileName, currentLocale), 
					translateLocal("indexPage")));
		}
		
		if (secondaryLocale != null) {
			divlink.appendChild(html.br());
			Locale linkToLocale = null;
			String nameOfLang = Locale.getDefault().getDisplayLanguage(Locale.getDefault());
			if (currentLocale == null) {
				linkToLocale = secondaryLocale;
				nameOfLang = secondaryLocale.getDisplayLanguage(secondaryLocale);
			}
			divlink.appendChild(html.link(getLocalizedFilename(currentFileName, linkToLocale), nameOfLang)); 
		}
		return divlink;
	}

	protected void makeFooter(Element appendTo, Html html) {
		
		if (displayGenJFooter) {
			Element divFooter = html.div("footer");
			appendTo.appendChild(divFooter);
			Element p = html.p(translateLocal("footerText") + " ");
			p.appendChild(html.link("http://genj.sourceforge.net/", "GenealogyJ"));
			divFooter.appendChild(p);
		}
	}

	protected String getName(Indi indi) {
		String name = indi.getName();
		if (sosaStradonitzNumber.get(indi.getId()) != null) { 
			name += " (" + sosaStradonitzNumber.get(indi.getId()) + ")";
		}
		return name;
	}
	protected String getName(Indi indi, Property nameProp) {
		String name = nameProp.getDisplayValue();
		if (sosaStradonitzNumber.get(indi.getId()) != null) { 
			name += " (" + sosaStradonitzNumber.get(indi.getId()) + ")";
		}
		return name;
	}

	
	protected boolean isPrivate(Indi indi) {
		if (reportNowLiving) return false;
		if (indi.isDeceased()) return false;
		if (bornBeforeDate(indi)) return false;
		return true;
	}
	
	
	protected boolean bornBeforeDate(Indi indi) {
		if (indi.getBirthDate() != null && indi.getBirthDate().isComparable()) { 
			if (indi.getBirthDate().compareTo(new PropertyDate(1900)) < 0) return true;
			return false;
		}
		for (Indi child : indi.getChildren()) { 
			if (bornBeforeDate(child)) return true;
		}
		return false; 
	}
	
	protected void processReferences(Property ent, String linkPrefix,
			Element appendTo, Html html, List<String> handledProperties) {
		
		List<PropertyXRef> refs = ent.getProperties(PropertyXRef.class);
		if (refs.size() > 0) {
			appendTo.appendChild(html.h2(translateLocal("references")));
			Element p = html.p();
			appendTo.appendChild(p);
			for (PropertyXRef ref : refs) {
				getReferenceLink(ref, p, linkPrefix, html, true);
			}
		}
		handledProperties.add("XREF");
	}

	protected void getReferenceLink(PropertyXRef ref, Element appendTo,
			String linkPrefix, Html html, boolean addNewline) {
		if (ref.isValid()) {
			Entity refEnt = ref.getTargetEntity();
			if (refEnt instanceof Indi) {
				
				appendTo.appendChild(html.link(linkPrefix + addressTo(refEnt.getId()), getName((Indi)refEnt)));
				if (addNewline) appendTo.appendChild(html.br());
			} else if (refEnt instanceof Fam) {
				
				Indi husb = ((Fam)refEnt).getHusband();
				Indi wife = ((Fam)refEnt).getWife();
				if (husb != null) {
					appendTo.appendChild(html.link(linkPrefix + addressTo(husb.getId()), getName(husb)));
					if (addNewline || wife != null) appendTo.appendChild(html.br());
				}
				if (wife != null) {
					appendTo.appendChild(html.link(linkPrefix + addressTo(wife.getId()), getName(wife)));
					if (addNewline) appendTo.appendChild(html.br());
				}
			} else {
				appendTo.appendChild(html.link(linkPrefix + addressTo(refEnt.getId()), refEnt.toString()));
				if (addNewline) appendTo.appendChild(html.br());
			}
		}
	}

	
	
	
	protected Element processMultimediaLink(Property prop, String linkPrefix, String id,
			Html html, boolean smallThumbs, boolean makeGalleryImage) {
		File currentObjectDir = new File(destDir, addressToDir(id));
		if (!currentObjectDir.exists()) currentObjectDir.mkdirs(); 
		Property[] objects = prop.getProperties("OBJE");
		if (objects.length == 0) return null;
		Element p = html.p();
		int imgSize = 200;
		if (smallThumbs) imgSize = 100;
		for (int i = 0; i < objects.length; i++){
			if (objects[i] instanceof PropertyMedia) {
				Media media = (Media)((PropertyMedia)objects[i]).getTargetEntity();
				if (media != null) {
					if (media.getFile() != null) {
						Element mediaBox = html.span("imageBox");
						p.appendChild(mediaBox);
						
						File mediaDir = new File(destDir, addressToDir(media.getId()));
						File thumbFile = new File(mediaDir, "thumb_" + media.getFile().getName());
						
						
						if (thumbFile.exists()) {
							mediaBox.appendChild(html.link(linkPrefix + addressToDir(media.getId()) + media.getFile().getName(), 
									html.img(linkPrefix + addressToDir(media.getId()) + "thumb_" + media.getFile().getName(), media.getTitle())));
							
							if (makeGalleryImage) {
								File galleryImage = new File(currentObjectDir, "gallery.jpg");
								File dstFile = media.getFile();
								try {
									if (!galleryImage.exists() || dstFile.lastModified() > galleryImage.lastModified()) {
										makeThumb(dstFile, 50, 70, galleryImage);
									}
									makeGalleryImage = false;
									if (prop instanceof Indi) personsWithImage.add((Indi)prop); 
								} catch (Exception e) {
									println("Making gallery thumb of image failed: " + dstFile.getAbsolutePath() + 
											" Error: " + e.getMessage());
								}
							}
						} else {
							mediaBox.appendChild(html.link(linkPrefix + addressToDir(media.getId()) + media.getFile().getName(), 
									media.getTitle()));
						}
						processNoteRefs(mediaBox, media, linkPrefix, id, html);
						processSourceRefs(mediaBox, media, linkPrefix, id, html);
						mediaBox.appendChild(html.br());
						mediaBox.appendChild(html.link(linkPrefix + addressTo(media.getId()), translate("aboutMedia")));
					} else {
						println(" Media reference to media without file.");
					}
					reportUnhandledProperties(objects[i], null);
				} else {
					println(" Invalid media reference to non existing object:" + objects[i].getValue());
				}
			} else {
				
				Property titleProp = objects[i].getProperty("TITL");
				String title = null;
				if (titleProp != null) title = titleProp.getValue();
				
				boolean tryMakeThumb = true;
				boolean thumbExist = false;
				
				Property formProp = objects[i].getProperty("FORM"); 
				if (formProp != null) {
					if (! formProp.getValue().matches("^jpe?g|gif|JPE?G|gif|PNG|png$")) {
						tryMakeThumb = false;
					}
					reportUnhandledProperties(formProp, null);
				}
				
				
				PropertyFile file = (PropertyFile)objects[i].getProperty("FILE");
				if (file != null) {
					
					formProp = file.getProperty("FORM");
					if (formProp != null) {
						if (! formProp.getValue().matches("^jpe?g|gif|JPE?G|gif|PNG|png$")) {
							tryMakeThumb = false;
						}
						reportUnhandledProperties(formProp, null);
					}
					reportUnhandledProperties(file, new String[] {"FORM"});
					
					File srcFile = file.getFile();
					if (srcFile != null) {
						File dstFile = new File(currentObjectDir, srcFile.getName());
						File thumbFile = new File(dstFile.getParentFile(), "thumb_" + dstFile.getName());
						try {
							if (!dstFile.exists() || srcFile.lastModified() > dstFile.lastModified()) {
								copyFile(srcFile, dstFile);
							}
							
							if (tryMakeThumb) {
								if (!thumbFile.exists() || srcFile.lastModified() > thumbFile.lastModified()) {
									try {
										makeThumb(dstFile, imgSize, imgSize, thumbFile);
										thumbExist = true;
									} catch (Exception e) {
										println("Making thumb of image failed: " + dstFile.getAbsolutePath() + 
												" Error: " + e.getMessage());
									}
								} else {
									thumbExist = true;
								}
							}

							
							if (makeGalleryImage && tryMakeThumb) {
								File galleryImage = new File(dstFile.getParentFile(), "gallery.jpg");
								try {
									if (!galleryImage.exists() || srcFile.lastModified() > galleryImage.lastModified()) {
										makeThumb(dstFile, 50, 70, galleryImage);
									}
									makeGalleryImage = false;
									if (prop instanceof Indi) personsWithImage.add((Indi)prop); 
								} catch (Exception e) {
									println("Making gallery thumb of image failed: " + dstFile.getAbsolutePath() + 
											" Error: " + e.getMessage());
								}
							}

							
							if (thumbExist)	p.appendChild(html.link(linkPrefix + addressToDir(id) + dstFile.getName(), html.img(linkPrefix + addressToDir(id) + thumbFile.getName(), title)));
							else p.appendChild(html.link(linkPrefix + addressToDir(id) + dstFile.getName(), title));
						} catch (IOException e) {
							println(" Error while copying file: " +	srcFile.getName() + 
									" Error: " + e.getMessage());
						}
						processNoteRefs(p, objects[i], linkPrefix, id, html);
						reportUnhandledProperties(objects[i], new String[]{"FILE", "TITL", "FORM", "NOTE"});
					} else {
						println(" FILE ref but no file was found");
					}
				} else {
					println(" OBJE without FILE is currently not handled");
				}
			}
		}
		if (p.hasChildNodes()) return p;
		return null;
	}	
	
	
	protected void processNumberNoteSourceChangeRest(Property prop, String linkPrefix,
			Element appendTo, String id, Html html, List<String> handledProperties, boolean showPageCreated) {

		
		if (! prop.getTag().equals("SOUR")) {
			Element sourceP = html.p(); 
			processSourceRefs(sourceP, prop, linkPrefix, id, html);
			if (sourceP.hasChildNodes()) {
				appendTo.appendChild(html.h2(getPropertyName("SOUR", true)));
				appendTo.appendChild(sourceP);
			}
		}
		handledProperties.add("SOUR");

		if (! prop.getTag().equals("NOTE")) {
			Element noteP = html.p(); 
			processNoteRefs(noteP, prop, linkPrefix, id, html);
			if (noteP.hasChildNodes()) {
				appendTo.appendChild(html.h2(getPropertyName("NOTE", true)));
				appendTo.appendChild(noteP);
			}
		}
		handledProperties.add("NOTE");
		
		
		processSimpleTags(prop, new String[] {"RFN", "AFN", "RIN"}, appendTo, html, handledProperties);
		
		Property[] refns = prop.getProperties("REFN");
		if (refns.length > 0) {
			appendTo.appendChild(html.h2(getPropertyName("REFN")));
			for (Property refn : refns) {
				Element p = html.p(refn.getDisplayValue());
				Property type = refn.getProperty("TYPE");
				if (type != null) p.appendChild(html.text(" (" + type.getDisplayValue() + ")"));
				appendTo.appendChild(p);
				reportUnhandledProperties(refn, new String[] {"TYPE"});
			}
			handledProperties.add("REFN");
		}

		
		PropertyChange lastUpdate = (PropertyChange)prop.getProperty("CHAN");
		if (lastUpdate != null) {
			appendTo.appendChild(html.h2(translateLocal("other")));
			Element p = html.p(translateLocal("dataUpdated") + 
					" " + lastUpdate.getDisplayValue());
			appendTo.appendChild(p);
			handledProperties.add("CHAN");
			processNoteRefs(p, lastUpdate, linkPrefix, id, html);
			reportUnhandledProperties(lastUpdate, new String[] {"NOTE"});
			if (showPageCreated) {
				p.appendChild(html.br()); 
				p.appendChild(html.text(translateLocal("pageCreated") + 
						" " + (new PropertyChange()).getDisplayValue()));
			}
		} else {
			if (showPageCreated) {
				appendTo.appendChild(html.h2(translateLocal("other")));
				appendTo.appendChild(html.p(translateLocal("pageCreated") + 
						" " + (new PropertyChange()).getDisplayValue()));
			}
		}
		
		
		reportUnhandledProperties(prop, (String[])handledProperties.toArray(new String[0])); 
		Element otherProperties = getAllProperties(prop, html, handledProperties);
		if (otherProperties != null)
		appendTo.appendChild(otherProperties);

	}

	
	protected void processSimpleTags(Property prop, String[] tags, Element appendTo, Html html, List<String> handledProperties) {
		for (String tag : tags) {
			processSimpleTag(prop, tag, appendTo, html, handledProperties);
		}
	}

	
	protected void processSimpleTag(Property prop, String tag, Element appendTo, Html html, List<String> handledProperties) {
		Property[] subProps = prop.getProperties(tag);
		if (subProps.length > 0) {
			appendTo.appendChild(html.h2(getPropertyName(tag)));
			for (Property subProp : subProps) {
				Element p = html.p();
				this.appendDisplayValue(p, subProp, true, html);
				appendTo.appendChild(p);
				
				reportUnhandledProperties(subProp, null);
			}
		}
		handledProperties.add(tag);
	}
	
	protected void processOtherEventTag(String tag, Property prop, String linkPrefix,
			String id, Element appendTo, Html html) {
		Property[] subProp = prop.getProperties(tag);
		if (subProp.length == 0) return;
		appendTo.appendChild(html.h2(getPropertyName(tag)));
		for (int i = 0; i < subProp.length; i++){
			appendTo.appendChild(processEventDetail(subProp[i], linkPrefix, id, html, false));
		}
	}

	
	protected Element processEventDetail(Property event, String linkPrefix, 
			String id, Html html, boolean displayTagDescription) {
		if (event == null) return null;
		Element p = html.p();
		List<String> handledProperties = new ArrayList<String>();

		if (displayTagDescription) {
			String description = "";
			if (!event.getTag().equals("EVEN")) {
				p.appendChild(html.text(getPropertyName(event.getTag()) + ": "));
			}
		}
		Property type = event.getProperty("TYPE");
		if (type != null) {
			p.appendChild(html.text(type.getDisplayValue() + " "));
		}
		handledProperties.add("TYPE");

		p.appendChild(html.text(event.getDisplayValue() + " "));
		
		
		PropertyDate date = (PropertyDate)event.getProperty("DATE");
		if (date != null) 
			p.appendChild(html.text(date.getDisplayValue() + " "));
		handledProperties.add("DATE");
		
		Element place = processPlace(event.getProperty("PLAC"), linkPrefix, id, html);
		if (place != null) {
			p.appendChild(place);
			if (mapEventLocations != null) {
				if(mapEventLocations.length() > 0) mapEventLocations.append("|");
				mapEventLocations.append(getEventMapPosition(event));
			}
		}
		
		handledProperties.add("PLAC");
		
		processAddresses(p, event, html, handledProperties, false);
		
		processSourceRefs(p, event, linkPrefix, id, html);
		handledProperties.add("SOUR");
		
		processNoteRefs(p, event, linkPrefix, id, html);
		handledProperties.add("NOTE");
		
		for (String tag : new String[] {"AGE", "AGNC", "CAUS", "RELI", "RESN"}) {
			Property tagProp = event.getProperty(tag);
			if (tagProp != null) {
				p.appendChild(html.text(getPropertyName(tag) + " " + tagProp.getDisplayValue()));
				reportUnhandledProperties(tagProp, null);
			}
			handledProperties.add(tag);
		}
		
		for (String tag : new String[] {"HUSB", "WIFE"}) {
			Property tagProp = event.getProperty(tag);
			if (tagProp != null) {
				Property age = tagProp.getProperty("AGE");
				if (age != null) {
					p.appendChild(html.text(getPropertyName(tag) + " " + getPropertyName("AGE") + " " +
							age.getDisplayValue()));
					this.reportUnhandledProperties(age, null);
				}
				reportUnhandledProperties(tagProp, new String[]{"AGE"});
			}
			handledProperties.add(tag);
		}
		
		
		Property famRef = event.getProperty("FAMC");
		if (famRef != null) { 
			if (famRef instanceof PropertyXRef) {
				Fam fam = (Fam)((PropertyXRef)famRef).getTargetEntity();
				Property adoptedBy = famRef.getProperty("ADOP");
				if (adoptedBy != null) makeLinkToFamily(p, fam, adoptedBy.getValue(), linkPrefix, html);
				else makeLinkToFamily(p, fam, null, linkPrefix, html);
			} else {
				println(event.getTag() + ":FAMC is not a reference:" + event.getValue());
			}
		}
		handledProperties.add("FAMC");
		
		Element pObj = processMultimediaLink(event, linkPrefix, id, html, true, false);
		if (pObj != null && pObj.hasChildNodes()) {
			NodeList nl = pObj.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) p.appendChild(nl.item(i));
		}
		handledProperties.add("OBJE");
		reportUnhandledProperties(event, handledProperties.toArray(new String[0]));
		return p;
	}

	
	protected String getEventMapPosition(Property event) {
		if (event == null) return null;
		Property place = event.getProperty("PLAC"); 
		if (place == null) return null;
		
		Property map = place.getProperty("MAP");
		if (map != null) {
			String latitude = map.getProperty("LATI").getDisplayValue();
			String longitude = map.getProperty("LONG").getDisplayValue();
			if (latitude.startsWith("S") || latitude.startsWith("s")) latitude = "-" + latitude.substring(1);
			else latitude = latitude.substring(1);
			if (longitude.startsWith("W") || longitude.startsWith("w")) longitude = "-" + longitude.substring(1);
			else longitude = longitude.substring(1);
			return latitude + "," + longitude;
		}
		
		String value = place.getValue();
		
		
		
		
		value = value.replaceAll("[?&]", "");
		value = value.replaceAll(" ", "+");
		return value;
	}

	protected void makeLinkToFamily(Element appendTo, Fam fam, String memberOfFamily, String linkPrefix, Html html) {
		Indi husb = fam.getHusband();
		Indi wife = fam.getWife();
		if (memberOfFamily == null || memberOfFamily.equals("BOTH")) {
			if (husb != null) {
				appendTo.appendChild(html.link(linkPrefix + addressTo(husb.getId()), getName(husb)));
				if (wife != null) appendTo.appendChild(html.text(" " + translateLocal("and") + " "));
			}
			if (wife != null) appendTo.appendChild(html.link(linkPrefix + addressTo(wife.getId()), getName(wife)));
		} else {
			if (memberOfFamily.equals("WIFE")) {
				if (wife != null) appendTo.appendChild(html.link(linkPrefix + addressTo(wife.getId()), getName(wife)));
			} else if (memberOfFamily.equals("HUSB")) {
				if (husb != null) appendTo.appendChild(html.link(linkPrefix + addressTo(husb.getId()), getName(husb)));
			} else {
				println("Invalid value on member of family:" + memberOfFamily);
			}
		}
	}
	
	protected Element processPlace(Property place, String linkPrefix, String id, Html html) {
		if (place == null) return null;
		Element span = html.span("place", placeDisplayFormat.equals("all") ? place.getValue() : place.format(placeDisplayFormat).replaceAll("^(,|(, ))*", "").trim());
		
		processSourceRefs(span, place, linkPrefix, id, html);
		
		processNoteRefs(span, place, linkPrefix, id, html);
		
		for (String subTag : new String[] {"FONE", "ROMN"}) {
			Property fone = place.getProperty(subTag);
			if (fone != null) {
				String type = "";
				Property typeProp = fone.getProperty("TYPE"); 
				if (typeProp != null) type = typeProp.getDisplayValue(); 
				span.appendChild(html.text(getPropertyName(subTag) + " " + type + ": " + 
						(placeDisplayFormat.equals("all") ? fone.getValue() : fone.format(placeDisplayFormat).replaceAll("^(,|(, ))*", "").trim())));
				reportUnhandledProperties(fone, new String[] {"TYPE"});
			}
		}
		
		Property map = place.getProperty("MAP");
		if (map != null && reportLinksToMap) {
			String latitude = map.getProperty("LATI").getDisplayValue();
			String longitude = map.getProperty("LONG").getDisplayValue();
			if (latitude.startsWith("S") || latitude.startsWith("s")) latitude = "-" + latitude.substring(1);
			else latitude = latitude.substring(1);
			if (longitude.startsWith("W") || longitude.startsWith("w")) longitude = "-" + longitude.substring(1);
			else longitude = longitude.substring(1);
			span.appendChild(html.text(" "));
			span.appendChild(html.link(translateLocal("mapLink", new Object[] {latitude, longitude}),
					translateLocal("linkToMap")));
			reportUnhandledProperties(map, new String[]{"LATI", "LONG"});
		}
		reportUnhandledProperties(place, new String[]{"SOUR", "NOTE", "MAP"});
		return span;
	}

	final String[] addressOtherProperties = new String[] {"PHON", "EMAIL", "FAX", "WWW"}; 

	protected boolean processAddressesHasData(Property prop) {
		if (prop.getProperty("ADDR") != null) return true;
		for (String tag : addressOtherProperties) {
			if (prop.getProperty(tag) != null) return true;
		}
		return false;
	}
	
	protected void processAddresses(Element appendTo, Property prop, Html html, List<String> handledProperties, boolean bigDisplayWithHeading) {
		if (! processAddressesHasData(prop)) return;
		if (bigDisplayWithHeading) appendTo.appendChild(html.h2(getPropertyName("ADDR")));

		Element span = html.span("address");
		if (! bigDisplayWithHeading) appendTo.appendChild(span);
		
		Property address = prop.getProperty("ADDR");
		if (address != null) {
			
			appendDisplayValue(span, address, ! bigDisplayWithHeading, html);
			
			final String[] addressSubProperties = new String[]{"ADR1", "ADR2", "ADR3", "CITY", "STAE", "POST", "CTRY"};
			for (String subTag : addressSubProperties) {
				Property subProp = address.getProperty(subTag);
				if (subProp != null) {
					if (bigDisplayWithHeading) span.appendChild(html.br());
					else span.appendChild(html.text(", ")); 
					span.appendChild(html.text(subProp.getDisplayValue()));
					reportUnhandledProperties(subProp, null);
				}
			}
			reportUnhandledProperties(address, addressSubProperties);
		}
		if (bigDisplayWithHeading && span.hasChildNodes()) appendTo.appendChild(html.p(span));
		handledProperties.add("ADDR");

		for (String tag : addressOtherProperties) {
			for (Property subProp : prop.getProperties(tag)) {
				Node value = html.text(subProp.getDisplayValue());
				if (tag.equals("EMAIL")) value = html.link("mailto:" + subProp.getDisplayValue(), subProp.getDisplayValue());
				if (tag.equals("WWW")) value = html.link(subProp.getDisplayValue(), subProp.getDisplayValue());
				if (bigDisplayWithHeading) {
					Element p = html.p(getPropertyName(tag) + ": ");
					p.appendChild(value);
					appendTo.appendChild(p);
				} else {
					span.appendChild(html.text(", "));
					span.appendChild(value);
				}
			}
			handledProperties.add(tag);
		}
	}

	
	protected void processSourceRefs(Element appendTo, Property prop, String linkPrefix, String id, Html html) {
		Property[] sourceRefs = prop.getProperties("SOUR");
		if (sourceRefs.length > 0) {
	    	if (sourceDiv == null) {
	    		sourceDiv = html.div("left");
	    		addedSourceProperty = new ArrayList<Property>();
	    		sourceCounter = 1;
	    	}
			Element sup = html.sup("source");
			for (Property sourceRef : sourceRefs) {
				if (sup.hasChildNodes()) sup.appendChild(html.text(", "));
				sup.appendChild(addSourceRef(sourceRef, linkPrefix, id, html));
			}
			appendTo.appendChild(sup);
		}
	}

	
	protected Element addSourceRef(Property sourceRef, String linkPrefix, String id, Html html) {
		int i = 1;
		for (Property alreadyAdded : addedSourceProperty) {
			if (propertyStructEquals(sourceRef, alreadyAdded)) {
				return html.link("#S" + i, "S" + i);
			}
			i++;
		}
		addedSourceProperty.add(sourceRef);

		int number = sourceCounter++;
		Element p = html.p();
		sourceDiv.appendChild(p);
		Element anchor = html.anchor("S" + number);
		p.appendChild(anchor);
		anchor.appendChild(html.text("S" + number + ": "));
	
		if (sourceRef instanceof PropertySource) {
			
			
			Source source = (Source)((PropertySource)sourceRef).getTargetEntity();
			if (source != null)
				p.appendChild(html.link(linkPrefix + addressTo(source.getId()), source.toString()));
			else 
				p.appendChild(html.text("(" + translateLocal("unknown") + ")"));
			
			Property page = sourceRef.getProperty("PAGE");
			if (page != null) {
				p.appendChild(html.text(" " + getPropertyName("PAGE") + ": " + 
						page.getDisplayValue()));
	       		reportUnhandledProperties(page, null);
			}
			
			Property even = sourceRef.getProperty("EVEN");
			if (even != null) {
				p.appendChild(html.text(" " + getPropertyName("EVEN") + ": " +
						even.getDisplayValue()));
				Property role = even.getProperty("ROLE");
				if (role != null) {
					p.appendChild(html.text(" " + getPropertyName("ROLE") + ": " +
						role.getDisplayValue()));
	           		reportUnhandledProperties(role, null);
				}
	       		reportUnhandledProperties(even, new String[] {"ROLE"});
			}
			
			Property data = sourceRef.getProperty("DATA");
			if (data != null) {
				p.appendChild(html.text(" " + getPropertyName("DATA") + ": " +
						data.getDisplayValue()));
				Property date = data.getProperty("DATE");
				if (date != null) p.appendChild(html.text(" " + date.getDisplayValue()));
				Property text = data.getProperty("TEXT");
				if (text != null) {
					p.appendChild(html.text(" "));
					appendDisplayValue(p, text, false, html);
				}
	   			reportUnhandledProperties(data, new String[] {"DATE", "TEXT"});
			}
			
			Property quay = sourceRef.getProperty("QUAY");
			if (quay != null) {
				p.appendChild(html.text(" " + getPropertyName("QUAY") + ": " + 
						quay.getDisplayValue()));
	       		reportUnhandledProperties(quay, null);
			}
			
			Element pObj = processMultimediaLink(sourceRef, linkPrefix, id, html, true, false);
			if (pObj != null) sourceDiv.appendChild(pObj);
					
	   		reportUnhandledProperties(sourceRef, new String[] {"PAGE", "EVEN", "DATA", "QUAY", "OBJE", "NOTE"});
		} else {
			
			appendDisplayValue(p, sourceRef, false, html);
			for (Property text : sourceRef.getProperties("TEXT")) {
				Element sp = html.p();
				sourceDiv.appendChild(sp);
				sp.appendChild(html.text(getPropertyName("TEXT") + ": "));
				appendDisplayValue(sp, text, false, html);
			}
	   		reportUnhandledProperties(sourceRef, new String[] {"TEXT", "NOTE"});
		}
		
		processNoteRefs(p, sourceRef, linkPrefix, id, html);
		return html.link("#S" + number, "S" + number);
	}

	
	protected void processNoteRefs(Element appendTo, Property prop, String linkPrefix, String id, Html html) {
		Property[] noteRefs = prop.getProperties("NOTE");
		if (noteRefs.length > 0) {
	    	if (noteDiv == null) {
	    		noteDiv = html.div("left");
	    		addedNoteProperty = new ArrayList<Property>();
	    		noteCounter = 1;
	    	}
			Element sup = html.sup("note");
			for (Property noteRef : noteRefs) {
				if (sup.hasChildNodes()) sup.appendChild(html.text(", "));
				sup.appendChild(addNoteRef(noteRef, linkPrefix, id, html));
			}
			appendTo.appendChild(sup);
		}
	}

	
	protected Element addNoteRef(Property noteRef, String linkPrefix, String id,
			Html html) {
		int i = 1;
		for (Property alreadyAdded : addedNoteProperty) {
			if (propertyStructEquals(noteRef, alreadyAdded)) {
				return html.link("#N" + i, "N" + i);
			}
			i++;
		}
		addedNoteProperty.add(noteRef);

		int number = noteCounter++;
		Element p = html.p();
		noteDiv.appendChild(p);
		Element anchor = html.anchor("N" + number);
		p.appendChild(anchor);
		anchor.appendChild(html.text("N" + number + ": "));
		
		if (noteRef instanceof PropertyNote) {
			
			Note note = (Note)((PropertyNote)noteRef).getTargetEntity();
			if (reportNotesInFullOnEntity) {
				
				appendDisplayValue(p, note, false, html);
				
				processSourceRefs(p, note, linkPrefix, id, html);
			} else {
				p.appendChild(html.link(linkPrefix + addressTo(note.getId()), note.toString()));
			}
		} else {
			
			appendDisplayValue(p, noteRef, false, html);
		}
		
		processSourceRefs(p, noteRef, linkPrefix, id, html);
	
		reportUnhandledProperties(noteRef, new String[]{"SOUR"});
		return html.link("#N" + number, "N" + number);
	}

	
	protected boolean propertyStructEquals(Property a, Property b) {
		if (! a.getClass().equals(b.getClass())) return false;
		if (a.compareTo(b) != 0) return false; 
		Property[] aProps = a.getProperties();  
		Property[] bProps = b.getProperties();  
		if (aProps.length == 0 && bProps.length == 0) return true;
		if (aProps.length == 1 && bProps.length == 1) return propertyStructEquals(aProps[0], bProps[0]);
		
		return false;
	}
	
	
	protected void appendDisplayValue(Element appendTo, Property prop, boolean ignoreNewLine, Html html) {
		if (prop instanceof MultiLineProperty) {
			Iterator lineIter = ((MultiLineProperty)prop).getLineIterator();
			boolean firstLine = true;
			do {
				if (! firstLine && ! ignoreNewLine && lineIter.getTag().equals("CONT")) appendTo.appendChild(html.br());
				appendTo.appendChild(html.text(lineIter.getValue()));
				firstLine = false;
			} while (lineIter.next());
		} else {
			appendTo.appendChild(html.text(prop.getDisplayValue()));
		}
	}

	protected void resetNoteAndSourceList() {
		sourceDiv = null;
		noteDiv = null;
	}

	protected void addNoteAndSourceList(Element appendTo) {
		if (noteDiv != null) appendTo.appendChild(noteDiv);
		if (sourceDiv != null) appendTo.appendChild(sourceDiv);
	}

	


	protected void addDecendantTree(Element whereToAdd, Indi indi, String relation, String linkPrefix, Html html) {
		if (indi == null) return;
		
		String relationClass = relation;
		if (relation.length() == 0) relationClass = "ident";
		Element div = html.div("anc " + relationClass);
		Element link = html.link(linkPrefix + addressTo(indi.getId()), getName(indi));
		link.appendChild(html.br());
		if (!isPrivate(indi)) {
			
			PropertyDate birthDate = indi.getBirthDate();
			if (birthDate != null) {
				link.appendChild(html.text(birthDate.getDisplayValue()));	
			}
			PropertyDate deathDate = indi.getDeathDate();
			if (deathDate != null) {
				link.appendChild(html.text(" -- " + deathDate.getDisplayValue()));	
			}
		}
		div.appendChild(link);
		whereToAdd.appendChild(div);
	
		
		Indi f = indi.getBiologicalFather();
		Indi m = indi.getBiologicalMother();
		if (f != null || m != null) {
			div.appendChild(html.div("l1", " "));
			div.appendChild(html.div("l2", " "));
			if (relation.length() == 2) { 
				div.appendChild(html.div("l3", " "));
				div.appendChild(html.div("l4", " "));
			}
			if (relation.length() < 3) {
				addDecendantTree(whereToAdd, m, relation + "m", linkPrefix, html);
				addDecendantTree(whereToAdd, f, relation + "f", linkPrefix, html);
			}
		}
		
		if (relation.length() == 0) {
			Element p = html.p();
			p.setAttribute("class", "treeMargin");
			whereToAdd.appendChild(p);
		}
	
	}

	
	protected Element getBirthPlaceMap(Indi indi, Html html) {
		String lines = getBirthPlaceMapRec(indi, 0, html);
		if (lines.equals("")) return null;
		return html.img("http://maps.google.com/maps/api/staticmap?size=300x300&maptype=roadmap&sensor=false" + lines, 
				translate("mapAncestorBirthPlace"));

	}
	protected String getBirthPlaceMapRec(Indi indi, int depth, Html html) {
		
		if (indi == null) return "";
		Indi f = indi.getBiologicalFather();
		Indi m = indi.getBiologicalMother();
		if (f == null || m == null) return "";
		String ibp = getEventMapPosition(indi.getProperty("BIRT"));
		String fbp = null;
		if (f != null) fbp = getEventMapPosition(f.getProperty("BIRT"));
		String mbp = null;
		if (m != null) mbp = getEventMapPosition(m.getProperty("BIRT"));

		String path = "";
		if (ibp != null && (mbp != null || fbp != null)) {
			String color = "000000";
			if (depth > 0) color = Integer.toHexString(depth * 0x444444);
			path = "&path=color:0x"+color+"FF|weight:2";
			if (fbp != null) path += "|" + fbp;
			path += "|" + ibp;
			if (mbp != null) path += "|" + mbp;
		}

		if (depth > 1) return path;
		if (f != null) path += getBirthPlaceMapRec(f, depth + 1, html);
		if (m != null) path += getBirthPlaceMapRec(m, depth + 1, html);
		return path;
	}
	
	protected void reportUnhandledProperties(Property current, String[] handled) {
		for (Property property : current.getProperties()) {
			String tag = property.getTag();
			if (! isIn(tag, handled)) {
				println("  Unhandled tag:" + current.getTag() + ":" + tag);
			}
		}
	}

	protected boolean isIn (String value, String[] list) {
		if (list == null) return false;
		for (String element : list) if (value.equals(element)) return true;
		return false;
	}

	protected Element getAllProperties(Property current, Html html, List<String> ignore) {
		
		Property[] properties = current.getProperties();
		if (properties.length > 0) {
			Element propertiesList = html.ul();
			for (int i = 0; i < properties.length; i++) {
				if (ignore == null || ! ignore.contains(properties[i].getTag())) {
					Element li = html.li(properties[i].getTag() + " " +
							properties[i].getDisplayValue());
					Element subProperties = getAllProperties(properties[i], html, null);
					if (subProperties != null) li.appendChild(subProperties);
					propertiesList.appendChild(li);
				}
			}
			if (propertiesList.hasChildNodes())	return propertiesList;
		}
		return null;
	}

	
	protected HashMap<String, String> makeCssAndJSSettings() {
		HashMap<String, String> translator = new HashMap<String, String>();
		addColorToMap(translator, "cssTextColor", cssTextColor);
		addColorToMap(translator, "cssBackgroundColor", cssBackgroundColor);
		addColorToMap(translator, "cssLinkColor", cssLinkColor);
		addColorToMap(translator, "cssVistedLinkColor", cssVistedLinkColor);
		addColorToMap(translator, "cssBorderColor", cssBorderColor);
		translator.put("indexFile", getLocalizedFilename(reportIndexFileName, currentLocale));
		translator.put("noSearchResults", translateLocal("noSearchResults"));
		return translator;
	}

	protected void addColorToMap(HashMap<String, String> translator, String name, String value) {
		final Pattern colorPattern = Pattern.compile("[0-9a-fA-F]{3}|[0-9a-fA-F]{6}");
		if (! colorPattern.matcher(value).matches()) {
			throw new InvalidParameterException(name + " has incorrect value: " + value);
		}
		translator.put(name, value);
	}

	
	protected void makeCss(File dir, HashMap<String, String> translator) throws IOException {
		println("Making css-file");
		copyTextFileModify(getFile().getParentFile().getAbsolutePath() + File.separator + cssBaseFile,
				dir.getAbsolutePath() + File.separator + "style.css", translator, false);	
		copyTextFileModify(getFile().getParentFile().getAbsolutePath() + File.separator + cssTreeFile[treeType],
				dir.getAbsolutePath() + File.separator + "style.css", translator, true);	
	}

	protected void makeJs(File dir, HashMap<String, String> translator) throws IOException {
		println("Making js-file");
		copyTextFileModify(getFile().getParentFile().getAbsolutePath() + File.separator + "html/search.js",
				dir.getAbsolutePath() + File.separator + getLocalizedFilename("search.js", currentLocale), translator, false);	
	}

	protected String getLocalizedFilename(String filename, Locale locale) {
		if (locale != null) {
			return filename.replaceFirst("\\.", "-" + locale.getLanguage() + ".");
		}
		return filename;
	}
	
	
	protected String addressTo(String id) {
		return addressToDir(id) + getLocalizedFilename(reportIndexFileName, currentLocale);
	}

	
	protected String addressToDir(String id) {
		StringBuffer address = new StringBuffer();
		
		String type = id.substring(0, 1);
		String prefix = type.toLowerCase();
		if (type.equals("I")) {	prefix = "indi"; }
		if (type.equals("S")) {	prefix = "source"; }
		if (type.equals("R")) {	prefix = "repository"; }
		if (type.equals("O")) {	prefix = "object"; }
		address.append(prefix);
		
		String idString = id.substring(1); 
		int i = idString.length();
		if (i % 2 == 1) { 
			i += 1; 
			idString = "0" + idString;
		}
		address.append(i);
		
		while (idString.length() > 0) {
			address.append('/').append(idString.substring(0, 2));
			idString = idString.substring(2);
		}
		address.append('/');
		return address.toString();
	}

	
	protected int addressDepth(String id) {
		return id.length() / 2 + 1;
	}

	
	protected String relativeLinkPrefix(String fromId) {
		StringBuffer address = new StringBuffer();
		for (int i = 0; i < addressDepth(fromId); i++) {
			address.append("../");
		}
		return address.toString();
	}

	
	protected void makeThumb(File imgFile, int wmax, int hmax, File thumbFile) throws IOException {
		BufferedImage originalImage = ImageIO.read(imgFile);
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();
		float wscale = (float)wmax / width;
		float hscale = (float)hmax / height;
		if (wscale > hscale) wscale = hscale;
		width = (int)(width * wscale);
		height = (int)(height * wscale);
		BufferedImage thumbImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = thumbImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(originalImage, 0, 0, width, height, null);
		ImageIO.write(thumbImage, "jpg", thumbFile);
	}

	protected void copyFile(File src, File dst) throws IOException {
		FileChannel source = null;
		FileChannel destination = null;
		try {
			source = new FileInputStream(src).getChannel();
			destination = new FileOutputStream(dst).getChannel();
			destination.transferFrom(source, 0, source.size());
		} finally {
			if (source!=null) try { source.close(); } catch (Throwable t){};
			if (destination!=null) try { destination.close(); } catch (Throwable t){};
		}

	}

	protected void copyTextFileModify(String inFile, String outFile, HashMap<String,String> translator, boolean append) throws IOException {
		final Pattern replacePattern = Pattern.compile(".*\\{(\\w+)\\}.*");
		BufferedReader in = null;
		BufferedWriter out = null;
		try {
			in = new BufferedReader(new FileReader(inFile));

			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile, append), "UTF-8")); 
			String buffer = in.readLine();
			while (buffer != null) {
				Matcher m = replacePattern.matcher(buffer);
				while (m.matches()) {
					String key = m.group(1);
					buffer = buffer.replaceAll("\\{"+key+"\\}", translator.get(key));
					m = replacePattern.matcher(buffer);
				}
				out.write(buffer);
				out.newLine();
				buffer = in.readLine();
			}
		} finally {
			if (in != null) in.close();
			if (out != null) out.close();
		}
	}

	protected String translateLocal(String key, Object... values) {
		if (currentLocale == null) return translate(key, values);
    	return translate(key, currentLocale, values);
    }

	protected String translateLocal(String key) {
		return translateLocal(key, (Object[])null);
	}
}

	