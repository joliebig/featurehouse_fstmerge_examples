
package net.sf.jabref.msbib;
import net.sf.jabref.*;
import net.sf.jabref.export.layout.format.*;
import net.sf.jabref.export.layout.*;
import net.sf.jabref.mods.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import java.util.*;
import java.io.*;
import java.util.regex.*;


public class MSBibEntry {
	protected String sourceType = "Misc";
	protected String bibTexEntry = null;

	protected String tag = null;
	protected String GUID = null;
	protected int LCID = -1;

	protected List authors = null;
	protected List bookAuthors = null;
	protected List editors = null;
	protected List translators = null;
	protected List producerNames = null;
	protected List composers = null;
	protected List conductors = null;
	protected List performers = null;
	protected List writers = null;
	protected List directors = null;
	protected List compilers = null;
	protected List interviewers = null;
	protected List interviewees = null;
	protected List inventors = null;
	protected List counsels = null;

	protected String title = null;
	protected String year = null;
	protected String month = null;
	protected String day = null;
	
	protected String shortTitle = null;
	protected String comments = null;
	
	protected PageNumbers pages = null;
	protected String volume = null;
	protected String numberOfVolumes = null;
	protected String edition = null;
	protected String standardNumber = null;	
	protected String publisher = null;
	
	protected String address = null;
	protected String bookTitle = null;
	protected String chapterNumber = null;
	protected String journalName = null;
	protected String issue = null;
	protected String periodicalTitle = null;
	protected String conferenceName = null;
	protected String department = null;
	protected String institution = null;
	protected String thesisType = null;
	protected String internetSiteTitle = null;
	protected String dateAccessed = null;
	protected String url = null;
	protected String productionCompany = null;
	protected String publicationTitle = null;
	protected String medium = null;
	protected String albumTitle = null;
	protected String recordingNumber = null;
	protected String theater = null;
	protected String distributor = null;
	protected String broadcastTitle = null;
	protected String broadcaster = null;
	protected String station = null;
	protected String type = null;
	protected String patentNumber = null;
	protected String court = null;
	protected String reporter = null;
	protected String caseNumber = null;
	protected String abbreviatedCaseNumber = null;
	protected String bibTex_Series = null;
	protected String bibTex_Abstract = null; 	 
	protected String bibTex_KeyWords = null; 	 
	protected String bibTex_CrossRef = null;
	protected String bibTex_HowPublished = null; 	 
	protected String bibTex_Affiliation = null;
	protected String bibTex_Contents = null;
	protected String bibTex_Copyright = null;	 
	protected String bibTex_Price = null; 	 
	protected String bibTex_Size = null;

	private final String BIBTEX = "BIBTEX_";
	private final String MSBIB = "msbib-";

	private final String bcol = "b:";
	
	private final boolean FORMATXML = false;
	
	public MSBibEntry() {
	}
	
	public MSBibEntry(BibtexEntry bibtex) {
		this();
		populateFromBibtex(bibtex);
	}

	public MSBibEntry(Element entry, String _bcol) {
		this();
		populateFromXml(entry,_bcol);
	}

	protected String getFromXml(String name, Element entry) {
		String value = null;
		NodeList nodeLst = entry.getElementsByTagName(name);
		if(nodeLst.getLength()>0)
			value = nodeLst.item(0).getTextContent();
		return value;
	}
	

	protected void populateFromXml(Element entry, String _bcol) {		
		String temp = null;

		sourceType = getFromXml(_bcol+"SourceType", entry);

		tag  = getFromXml(_bcol+"Tag", entry);

		temp = getFromXml(_bcol+"LCID", entry);
		if(temp!=null)
		{
			try {
			LCID = Integer.parseInt(temp); }
			catch (Exception e) {
				LCID = -1;
			}
		}

		title = getFromXml(_bcol+"Title", entry);
		year = getFromXml(_bcol+"Year", entry);
		month = getFromXml(_bcol+"Month", entry);
		day = getFromXml(_bcol+"Day", entry);

		shortTitle = getFromXml(_bcol+"ShortTitle", entry);
		comments = getFromXml(_bcol+"Comments", entry);

		temp = getFromXml(_bcol+"Pages", entry);
		if(temp != null)
			pages = new PageNumbers(temp);

		volume = getFromXml(_bcol+"Volume", entry);

		numberOfVolumes = getFromXml(_bcol+"NumberVolumes", entry);

		edition = getFromXml(_bcol+"Edition", entry);
		
		standardNumber = getFromXml(_bcol+"StandardNumber", entry);

		publisher = getFromXml(_bcol+"Publisher", entry);

		String city = getFromXml(_bcol+"City", entry);
		String state = getFromXml(_bcol+"StateProvince", entry);
		String country = getFromXml(_bcol+"CountryRegion", entry);
		address = "";
		if(city != null)
			address += city + ", "; 
		if(state != null)
			address += state + " "; 
		if(country != null)
			address += country;
		address = address.trim();
		if(address.equals("") || address.equals(","))
			address = null;

		bookTitle = getFromXml(_bcol+"BookTitle", entry);

		chapterNumber = getFromXml(_bcol+"ChapterNumber", entry);

		journalName = getFromXml(_bcol+"JournalName", entry);

		issue = getFromXml(_bcol+"Issue", entry);

		periodicalTitle = getFromXml(_bcol+"PeriodicalTitle", entry);
		
		conferenceName = getFromXml(_bcol+"ConferenceName", entry);
		department = getFromXml(_bcol+"Department", entry);
		institution = getFromXml(_bcol+"Institution", entry);

		thesisType = getFromXml(_bcol+"ThesisType", entry);
		internetSiteTitle = getFromXml(_bcol+"InternetSiteTitle", entry);
		String month = getFromXml(_bcol+"MonthAccessed", entry);
		String day = getFromXml(_bcol+"DayAccessed", entry);
		String year = getFromXml(_bcol+"YearAccessed", entry);
		dateAccessed = "";
		if(month != null)
			dateAccessed += month + " ";
		if(day != null)
			dateAccessed += day + ", ";
		if(year != null)
			dateAccessed += year;
		dateAccessed = dateAccessed.trim();
		if(dateAccessed.equals("") || dateAccessed.equals(","))
			dateAccessed = null;

		url = getFromXml(_bcol+"URL", entry);
		productionCompany = getFromXml(_bcol+"ProductionCompany", entry);
		
		publicationTitle = getFromXml(_bcol+"PublicationTitle", entry);
		medium = getFromXml(_bcol+"Medium", entry);
		albumTitle = getFromXml(_bcol+"AlbumTitle", entry);
		recordingNumber = getFromXml(_bcol+"RecordingNumber", entry);
		theater = getFromXml(_bcol+"Theater", entry);
		distributor = getFromXml(_bcol+"Distributor", entry);
		broadcastTitle = getFromXml(_bcol+"BroadcastTitle", entry);
		broadcaster = getFromXml(_bcol+"Broadcaster", entry);
		station = getFromXml(_bcol+"Station", entry);
		type = getFromXml(_bcol+"Type", entry);
		patentNumber = getFromXml(_bcol+"PatentNumber", entry);
		court = getFromXml(_bcol+"Court", entry);
		reporter = getFromXml(_bcol+"Reporter", entry);
		caseNumber = getFromXml(_bcol+"CaseNumber", entry);
		abbreviatedCaseNumber = getFromXml(_bcol+"AbbreviatedCaseNumber", entry);
		bibTex_Series = getFromXml(_bcol+BIBTEX+"Series", entry);
		bibTex_Abstract = getFromXml(_bcol+BIBTEX+"Abstract", entry);
		bibTex_KeyWords = getFromXml(_bcol+BIBTEX+"KeyWords", entry);
		bibTex_CrossRef = getFromXml(_bcol+BIBTEX+"CrossRef", entry);
		bibTex_HowPublished = getFromXml(_bcol+BIBTEX+"HowPublished", entry);
		bibTex_Affiliation = getFromXml(_bcol+BIBTEX+"Affiliation", entry);
		bibTex_Contents = getFromXml(_bcol+BIBTEX+"Contents", entry);
		bibTex_Copyright = getFromXml(_bcol+BIBTEX+"Copyright", entry);
		bibTex_Price = getFromXml(_bcol+BIBTEX+"Price", entry);
		bibTex_Size = getFromXml(_bcol+BIBTEX+"Size", entry);
	 
		NodeList nodeLst = entry.getElementsByTagName(_bcol+"Author");
		if(nodeLst.getLength()>0)
			getAuthors((Element)(nodeLst.item(0)),_bcol);
	}

	protected void populateFromBibtex(BibtexEntry bibtex) {
		
		sourceType = getMSBibSourceType(bibtex);

		if (bibtex.getField("bibtexkey") != null)
			tag = bibtex.getField("bibtexkey").toString();

		if (bibtex.getField("language") != null)
			LCID = getLCID(bibtex.getField("language").toString());

		if (bibtex.getField("title") != null)
			title = bibtex.getField("title").toString();
		if (bibtex.getField("year") != null)
			year = bibtex.getField("year").toString();
		if (bibtex.getField("month") != null)
			month = bibtex.getField("month").toString();
		if (bibtex.getField(MSBIB+"day") != null)
			day = bibtex.getField(MSBIB+"day").toString();

		if (bibtex.getField(MSBIB+"shorttitle") != null)
			shortTitle = bibtex.getField(MSBIB+"shorttitle").toString();
		if (bibtex.getField("note") != null)
			comments = bibtex.getField("note").toString();

		if (bibtex.getField("pages") != null)
			pages = new PageNumbers(bibtex.getField("pages").toString());

		if (bibtex.getField("volume") != null)
			volume = bibtex.getField("volume").toString();

		if (bibtex.getField(MSBIB+"numberofvolume") != null)
			numberOfVolumes = bibtex.getField(MSBIB+"numberofvolume").toString();

		if (bibtex.getField("edition") != null)
			edition = bibtex.getField("edition").toString();
		
		standardNumber = new String();
		if (bibtex.getField("ISBN") != null)
			standardNumber += ":ISBN:" + bibtex.getField("ISBN").toString();
		if (bibtex.getField("ISSN") != null)
			standardNumber += ":ISSN:"+ bibtex.getField("ISSN").toString();
		if (bibtex.getField("LCCN") != null)
			standardNumber += ":LCCN:"+ bibtex.getField("LCCN").toString();
		if (bibtex.getField("mrnumber") != null)
			standardNumber += ":MRN:"+ bibtex.getField("mrnumber").toString();
		if(standardNumber.equals(""))
			standardNumber = null;

		if (bibtex.getField("publisher") != null)
			publisher = bibtex.getField("publisher").toString();

		if (bibtex.getField("address") != null)
			address = bibtex.getField("address").toString();

		if (bibtex.getField("booktitle") != null)
			bookTitle = bibtex.getField("booktitle").toString();

		if (bibtex.getField("chapter") != null)
			chapterNumber = bibtex.getField("chapter").toString();

		if (bibtex.getField("journal") != null)
			journalName = bibtex.getField("journal").toString();

		if (bibtex.getField("issue") != null)
			issue = bibtex.getField("issue").toString();

		if (bibtex.getField(MSBIB+"periodical") != null)
			periodicalTitle = bibtex.getField(MSBIB+"periodical").toString();
		
		if (bibtex.getField("organization") != null)
			conferenceName = bibtex.getField("organization").toString();
		if (bibtex.getField("school") != null)
			department = bibtex.getField("school").toString();
		if (bibtex.getField("institution") != null)
			institution = bibtex.getField("institution").toString();

		if (bibtex.getField("type") != null)
			thesisType = bibtex.getField("type").toString();
		if ( (sourceType.equals("InternetSite")==true || sourceType.equals("DocumentFromInternetSite")==true)
				&& bibtex.getField("title") != null)
			internetSiteTitle = bibtex.getField("title").toString();
		if (bibtex.getField(MSBIB+"accessed") != null)
			dateAccessed = bibtex.getField(MSBIB+"accessed").toString();
		if (bibtex.getField("URL") != null)
			url = bibtex.getField("URL").toString();
		if (bibtex.getField(MSBIB+"productioncompany") != null)
			productionCompany = bibtex.getField(MSBIB+"productioncompany").toString();
		
		if ( (sourceType.equals("ElectronicSource")==true 
				|| sourceType.equals("Art")==true
				|| sourceType.equals("Misc")==true)
				&& bibtex.getField("title") != null)
			publicationTitle = bibtex.getField("title").toString();
		if (bibtex.getField(MSBIB+"medium") != null)
			medium = bibtex.getField(MSBIB+"medium").toString();
		if (sourceType.equals("SoundRecording")==true && bibtex.getField("title") != null)
			albumTitle = bibtex.getField("title").toString();
		if (bibtex.getField(MSBIB+"recordingnumber") != null)
			recordingNumber = bibtex.getField(MSBIB+"recordingnumber").toString();
		if (bibtex.getField(MSBIB+"theater") != null)
			theater = bibtex.getField(MSBIB+"theater").toString();
		if (bibtex.getField(MSBIB+"distributor") != null)
			distributor = bibtex.getField(MSBIB+"distributor").toString();
		if (sourceType.equals("Interview")==true && bibtex.getField("title") != null)
			broadcastTitle = bibtex.getField("title").toString();
		if (bibtex.getField(MSBIB+"broadcaster") != null)
			broadcaster = bibtex.getField(MSBIB+"broadcaster").toString();
		if (bibtex.getField(MSBIB+"station") != null)
			station = bibtex.getField(MSBIB+"station").toString();
		if (bibtex.getField(MSBIB+"type") != null)
			type = bibtex.getField(MSBIB+"type").toString();
		if (bibtex.getField(MSBIB+"patentnumber") != null)
			patentNumber = bibtex.getField(MSBIB+"patentnumber").toString();
		if (bibtex.getField(MSBIB+"court") != null)
			court = bibtex.getField(MSBIB+"court").toString();
		if (bibtex.getField(MSBIB+"reporter") != null)
			reporter = bibtex.getField(MSBIB+"reporter").toString();
		if (bibtex.getField(MSBIB+"casenumber") != null)
			caseNumber = bibtex.getField(MSBIB+"casenumber").toString();
		if (bibtex.getField(MSBIB+"abbreviatedcasenumber") != null)
			abbreviatedCaseNumber = bibtex.getField(MSBIB+"abbreviatedcasenumber").toString();
		if (bibtex.getField("series") != null)
			bibTex_Series = bibtex.getField("series").toString();
		if (bibtex.getField("abstract") != null)
			bibTex_Abstract = bibtex.getField("abstract").toString();
		if (bibtex.getField("keywords") != null)
			bibTex_KeyWords = bibtex.getField("keywords").toString();
		if (bibtex.getField("crossref") != null)
			bibTex_CrossRef = bibtex.getField("crossref").toString();
		if (bibtex.getField("howpublished") != null)
			bibTex_HowPublished = bibtex.getField("howpublished").toString();
		if (bibtex.getField("affiliation") != null)
			bibTex_Affiliation = bibtex.getField("affiliation").toString();
		if (bibtex.getField("contents") != null)
			bibTex_Contents = bibtex.getField("contents").toString();
		if (bibtex.getField("copyright") != null)
			bibTex_Copyright = bibtex.getField("copyright").toString();
		if (bibtex.getField("price") != null)
			bibTex_Price = bibtex.getField("price").toString();
		if (bibtex.getField("size") != null)
			bibTex_Size = bibtex.getField("size").toString();
	 

		if (bibtex.getField("author") != null)
			authors = getAuthors(bibtex.getField("author").toString());
		
		
		if(FORMATXML)
		{
			title = format(title);
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			bibTex_Abstract = format(bibTex_Abstract);
		}
	}

	private String format(String value)
	{
		if(value == null)
			return null;
		String result = null;
		LayoutFormatter chars = new XMLChars();
		result = chars.format(value);
		return result;
	}
	
	
	protected int getLCID(String language)
	{
		int iLCID = 0;
		
		
		return iLCID;
	}

	
	protected String getLanguage(int LCID)
	{
		String language = "english";
		
		
		return language;
	}
	
	protected List getSpecificAuthors(String type, Element authors, String _bcol) {
		List result = null;
		NodeList nodeLst = authors.getElementsByTagName(_bcol+type);
		if(nodeLst.getLength()<=0)
			return result;
		nodeLst = ((Element)(nodeLst.item(0))).getElementsByTagName(_bcol+"NameList");
		if(nodeLst.getLength()<=0)
			return result;
		NodeList person = ((Element)(nodeLst.item(0))).getElementsByTagName(_bcol+"Person");
		if(person.getLength()<=0)
			return result;

		result = new LinkedList();
		for(int i=0;i<person.getLength();i++)
		{
			NodeList firstName  = ((Element)(person.item(i))).getElementsByTagName(_bcol+"First");
			NodeList lastName   = ((Element)(person.item(i))).getElementsByTagName(_bcol+"Last");
			NodeList middleName = ((Element)(person.item(i))).getElementsByTagName(_bcol+"Middle");
			PersonName name = new PersonName();
			if(firstName.getLength()>0)
				name.setFirstname(firstName.item(0).getTextContent());
			if(middleName.getLength()>0)
				name.setMiddlename(middleName.item(0).getTextContent());
			if(lastName.getLength()>0)
				name.setSurname(lastName.item(0).getTextContent());
			result.add(name);
		}
		
		return result;
	}

	protected void getAuthors(Element authorsElem, String _bcol) {
		authors = getSpecificAuthors("Author",authorsElem,_bcol);		
		bookAuthors = getSpecificAuthors("BookAuthor",authorsElem,_bcol);
		editors = getSpecificAuthors("Editor",authorsElem,_bcol);
		translators = getSpecificAuthors("Translator",authorsElem,_bcol);
		producerNames = getSpecificAuthors("ProducerName",authorsElem,_bcol);
		composers = getSpecificAuthors("Composer",authorsElem,_bcol);
		conductors = getSpecificAuthors("Conductor",authorsElem,_bcol);
		performers = getSpecificAuthors("Performer",authorsElem,_bcol);
		writers = getSpecificAuthors("Writer",authorsElem,_bcol);
		directors = getSpecificAuthors("Director",authorsElem,_bcol);
		compilers = getSpecificAuthors("Compiler",authorsElem,_bcol);
		interviewers = getSpecificAuthors("Interviewer",authorsElem,_bcol);
		interviewees = getSpecificAuthors("Interviewee",authorsElem,_bcol);
		inventors = getSpecificAuthors("Inventor",authorsElem,_bcol);
		counsels = getSpecificAuthors("Counsel",authorsElem,_bcol);		
	}

	protected List getAuthors(String authors) {
		List result = new LinkedList();
		LayoutFormatter chars = new XMLChars();
		
		if (authors.indexOf(" and ") == -1)
		{



				result.add(new PersonName(authors));
		}
        else
        {
            String[] names = authors.split(" and ");
            for (int i=0; i<names.length; i++)
            {



            		result.add(new PersonName(names[i]));
            }
        }
		return result;
	}
	
	
	protected String getDate(BibtexEntry bibtex) {
		String result = "";
		if (bibtex.getField("year") != null)
			result += (bibtex.getField("year").toString());
		if (bibtex.getField("month") != null)
			result += "-" + bibtex.getField("month").toString();
		
		return result;
	}

	protected String getMSBibSourceType(BibtexEntry bibtex) {
		String bibtexType = bibtex.getType().getName();

		String result = "Misc";
		if (bibtexType.equalsIgnoreCase("book"))
			result = "Book";
		else if(bibtexType.equalsIgnoreCase("inbook"))
			result = "BookSection";
		else if(bibtexType.equalsIgnoreCase("booklet"))
			{ result = "BookSection"; bibTexEntry = "booklet"; } 
		else if(bibtexType.equalsIgnoreCase("incollection"))
			{ result = "BookSection"; bibTexEntry = "incollection"; } 

		else if(bibtexType.equalsIgnoreCase("article"))
			result = "JournalArticle"; 

		else if(bibtexType.equalsIgnoreCase("inproceedings"))
			result = "ConferenceProceedings"; 
		else if(bibtexType.equalsIgnoreCase("conference"))
			{ result = "ConferenceProceedings"; bibTexEntry = "conference"; } 
		else if(bibtexType.equalsIgnoreCase("proceedings"))
			{ result = "ConferenceProceedings"; bibTexEntry = "proceedings"; } 
		else if(bibtexType.equalsIgnoreCase("collection"))
			{ result = "ConferenceProceedings"; bibTexEntry = "collection"; } 

		else if(bibtexType.equalsIgnoreCase("techreport"))
			result = "Report"; 
		else if(bibtexType.equalsIgnoreCase("manual"))
			{ result = "Report"; bibTexEntry = "manual"; } 
		else if(bibtexType.equalsIgnoreCase("mastersthesis"))
			{ result = "Report"; bibTexEntry = "mastersthesis"; } 
		else if(bibtexType.equalsIgnoreCase("phdthesis"))
			{ result = "Report"; bibTexEntry = "phdthesis"; } 
		else if(bibtexType.equalsIgnoreCase("unpublished"))
			{ result = "Report"; bibTexEntry = "unpublished"; } 

		else if(bibtexType.equalsIgnoreCase("patent"))
			result = "Patent"; 

		else if(bibtexType.equalsIgnoreCase("misc"))
			result = "Misc"; 
		
		return result;
	}
	
	public Document getDOMrepresentation() {
		Document result = null;
		try {
			DocumentBuilder d = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
		
		}
		catch (Exception e) 
		{
			throw new Error(e);
		}
		return result;
	}
	























	public void addField(Document d,Element parent, String name, String value) {
		if(value == null)
			return;
		Element elem = d.createElement(bcol+name);
 		





		elem.appendChild(d.createTextNode(stripNonValidXMLCharacters(value)));		
		parent.appendChild(elem);
	}

	public void addAuthor(Document d, Element allAuthors, String entryName, List authorsLst) {
		if(authorsLst == null)
			return;
		Element authorTop = d.createElement(bcol+entryName);
		Element nameList = d.createElement(bcol+"NameList");
		for(Iterator iter = authorsLst.iterator(); iter.hasNext();) {
			PersonName name = (PersonName) iter.next();
			Element person = d.createElement(bcol+"Person");
			addField(d, person,"Last",name.getSurname());
			addField(d, person,"Middle",name.getMiddlename());
			addField(d, person,"First",name.getFirstname());
			nameList.appendChild(person);
		}
		authorTop.appendChild(nameList);
		
		allAuthors.appendChild(authorTop);
	}

	public void addAdrress(Document d,Element parent, String address) {
		if(address == null)
			return;

		
		
		
		
		
		
		
		
		
		Pattern p = Pattern.compile("\\b(\\w+)\\s*[,]?\\s*(\\w+)\\s*[,]?\\s*(\\w+)\\b");
		Matcher m = p.matcher(address);
		if (m.matches() && m.groupCount()>3)
		{
			addField(d, parent,"City",m.group(1));
			addField(d, parent,"StateProvince",m.group(2));
			addField(d, parent,"CountryRegion",m.group(3));
		}
	}

	public void addDate(Document d,Element parent, String date, String extra) {
		if(date == null)
			return;

		
		
		
		
		Pattern p = Pattern.compile("(\\d{1,2})\\s*[.,-/]\\s*(\\d{1,2})\\s*[.,-/]\\s*(\\d{2,4})");
		Matcher m = p.matcher(date);
		if (m.matches() && m.groupCount()>3)
		{
			addField(d, parent,"Month"+extra,m.group(1));
			addField(d, parent,"Day"+extra,m.group(2));
			addField(d, parent,"Year"+extra,m.group(3));
		}
	}

	public Element getDOMrepresentation(Document d) {
		Node result = null;		
	   	try {
	   		Element msbibEntry = d.createElement(bcol+"Source");

	   		addField(d,msbibEntry,"SourceType",sourceType);
   			addField(d,msbibEntry,BIBTEX+"Entry",bibTexEntry);

   			addField(d,msbibEntry,"Tag",tag);
   			addField(d,msbibEntry,"GUID",GUID);
   			if(LCID >= 0)
   				addField(d,msbibEntry,"LCID",Integer.toString(LCID));
   			addField(d,msbibEntry,"Title",title);
   			addField(d,msbibEntry,"Year",year);
   			addField(d,msbibEntry,"ShortTitle",shortTitle);
   			addField(d,msbibEntry,"Comments",comments);

   			Element allAuthors = d.createElement(bcol+"Author");

   			addAuthor(d,allAuthors,"Author",authors);
	   		addAuthor(d,allAuthors,"BookAuthor",bookAuthors);
	   		addAuthor(d,allAuthors,"Editor",editors);
	   		addAuthor(d,allAuthors,"Translator",translators);
	   		addAuthor(d,allAuthors,"ProducerName",producerNames);
	   		addAuthor(d,allAuthors,"Composer",composers);
	   		addAuthor(d,allAuthors,"Conductor",conductors);
	   		addAuthor(d,allAuthors,"Performer",performers);
	   		addAuthor(d,allAuthors,"Writer",writers);
	   		addAuthor(d,allAuthors,"Director",directors);
	   		addAuthor(d,allAuthors,"Compiler",compilers);
	   		addAuthor(d,allAuthors,"Interviewer",interviewers);
	   		addAuthor(d,allAuthors,"Interviewee",interviewees);
	   		addAuthor(d,allAuthors,"Inventor",inventors);
	   		addAuthor(d,allAuthors,"Counsel",counsels);

	   		msbibEntry.appendChild(allAuthors);
	   		
	   		if(pages !=null )
	   			addField(d,msbibEntry,"Pages",pages.toString("-"));
	   		addField(d,msbibEntry,"Volume",volume);
	   		addField(d,msbibEntry,"NumberVolumes",numberOfVolumes);
	   		addField(d,msbibEntry,"Edition",edition);
	   		addField(d,msbibEntry,"StandardNumber",standardNumber);
	   		addField(d,msbibEntry,"Publisher",publisher);
		
	   		addAdrress(d,msbibEntry,address);
	   		
	   		addField(d,msbibEntry,"BookTitle",bookTitle);
	   		addField(d,msbibEntry,"ChapterNumber",chapterNumber);

	   		addField(d,msbibEntry,"JournalName",journalName);
	   		addField(d,msbibEntry,"Issue",issue);
	   		addField(d,msbibEntry,"PeriodicalTitle",periodicalTitle);
	   		addField(d,msbibEntry,"ConferenceName",conferenceName);

	   		addField(d,msbibEntry,"Department",department);
	   		addField(d,msbibEntry,"Institution",institution);
	   		addField(d,msbibEntry,"ThesisType",thesisType);
	   		addField(d,msbibEntry,"InternetSiteTitle",internetSiteTitle);
	   		
	   		addDate(d,msbibEntry, dateAccessed, "Accessed");
	   		
	   		addField(d,msbibEntry,"URL",url);
	   		addField(d,msbibEntry,"ProductionCompany",productionCompany);
	   		addField(d,msbibEntry,"PublicationTitle",publicationTitle);
	   		addField(d,msbibEntry,"Medium",medium);
	   		addField(d,msbibEntry,"AlbumTitle",albumTitle);
	   		addField(d,msbibEntry,"RecordingNumber",recordingNumber);	   		
	   		addField(d,msbibEntry,"Theater",theater);
	   		addField(d,msbibEntry,"Distributor",distributor);
	   		addField(d,msbibEntry,"BroadcastTitle",broadcastTitle);
	   		addField(d,msbibEntry,"Broadcaster",broadcaster);
	   		addField(d,msbibEntry,"Station",station);
	   		addField(d,msbibEntry,"Type",type);
	   		addField(d,msbibEntry,"PatentNumber",patentNumber);
	   		addField(d,msbibEntry,"Court",court);
	   		addField(d,msbibEntry,"Reporter",reporter);
	   		addField(d,msbibEntry,"CaseNumber",caseNumber);
	   		addField(d,msbibEntry,"AbbreviatedCaseNumber",abbreviatedCaseNumber);

	   		addField(d,msbibEntry,BIBTEX+"Series",bibTex_Series);
	   		addField(d,msbibEntry,BIBTEX+"Abstract",bibTex_Abstract);
	   		addField(d,msbibEntry,BIBTEX+"KeyWords",bibTex_KeyWords);
	   		addField(d,msbibEntry,BIBTEX+"CrossRef",bibTex_CrossRef);
	   		addField(d,msbibEntry,BIBTEX+"HowPublished",bibTex_HowPublished);
	   		addField(d,msbibEntry,BIBTEX+"Affiliation",bibTex_Affiliation);
	   		addField(d,msbibEntry,BIBTEX+"Contents",bibTex_Contents);
	   		addField(d,msbibEntry,BIBTEX+"Copyright",bibTex_Copyright);
	   		addField(d,msbibEntry,BIBTEX+"Price",bibTex_Price);
	   		addField(d,msbibEntry,BIBTEX+"Size",bibTex_Size);

	   		return msbibEntry;
	   	}
	   	catch (Exception e)
		{
	   		System.out.println("Exception caught..." + e);
	   		e.printStackTrace();
	   		throw new Error(e);
		}
	   	
	   }
	
	protected void parseSingleStandardNumber(String type,String bibtype, String standardNum, HashMap hm) {
		
		Pattern p = Pattern.compile(":"+type+":(.[^:]+)");
		Matcher m = p.matcher(standardNum);
		if (m.matches())
			hm.put(bibtype,m.group(1));
	}

	protected void parseStandardNumber(String standardNum, HashMap hm) {
		if(standardNumber == null)
			return;
		parseSingleStandardNumber("ISBN","ISBN",standardNum,hm);
		parseSingleStandardNumber("ISSN","ISSN",standardNum,hm);
		parseSingleStandardNumber("LCCN","LCCN",standardNum,hm);
		parseSingleStandardNumber("MRN","mrnumber",standardNum,hm);
	}

	public void addAuthor(HashMap hm, String type, List authorsLst) {
		if(authorsLst == null)
			return;
		String allAuthors = "";
		boolean First = true;
		for(Iterator iter = authorsLst.iterator(); iter.hasNext();) {
			PersonName name = (PersonName) iter.next();
			if(First == false)
				allAuthors += " and ";
			allAuthors += name.getFullname();
			First = false;
		}
		hm.put(type,allAuthors);
	}












































	
	public BibtexEntryType mapMSBibToBibtexType(String msbib)
	{
		BibtexEntryType bibtex = BibtexEntryType.OTHER;
		if(msbib.equals("Book"))
			bibtex = BibtexEntryType.BOOK;
		else if(msbib.equals("BookSection"))
			bibtex = BibtexEntryType.INBOOK;
		else if(msbib.equals("JournalArticle"))
			bibtex = BibtexEntryType.ARTICLE;
		else if(msbib.equals("ArticleInAPeriodical"))
			bibtex = BibtexEntryType.ARTICLE;
		else if(msbib.equals("ConferenceProceedings"))
			bibtex = BibtexEntryType.CONFERENCE;
		else if(msbib.equals("Report"))
			bibtex = BibtexEntryType.TECHREPORT;
		else if(msbib.equals("InternetSite"))
			bibtex = BibtexEntryType.OTHER;
		else if(msbib.equals("DocumentFromInternetSite"))
			bibtex = BibtexEntryType.OTHER;
		else if(msbib.equals("DocumentFromInternetSite"))
			bibtex = BibtexEntryType.OTHER;
		else if(msbib.equals("ElectronicSource"))
			bibtex = BibtexEntryType.OTHER;
		else if(msbib.equals("Art"))
			bibtex = BibtexEntryType.OTHER;
		else if(msbib.equals("SoundRecording"))
			bibtex = BibtexEntryType.OTHER;
		else if(msbib.equals("Performance"))
			bibtex = BibtexEntryType.OTHER;
		else if(msbib.equals("Film"))
			bibtex = BibtexEntryType.OTHER;
		else if(msbib.equals("Interview"))
			bibtex = BibtexEntryType.OTHER;
		else if(msbib.equals("Patent"))
			bibtex = BibtexEntryType.OTHER;
		else if(msbib.equals("Case"))
			bibtex = BibtexEntryType.OTHER;
		else if(msbib.equals("Misc"))
			bibtex = BibtexEntryType.MISC;
		else
			bibtex = BibtexEntryType.MISC;

		return bibtex;
	}
	public BibtexEntry getBibtexRepresentation() {		






		BibtexEntry entry = null;
		if(tag == null)
			entry = new BibtexEntry(BibtexFields.DEFAULT_BIBTEXENTRY_ID, 
					mapMSBibToBibtexType(sourceType));
		else
			entry = new BibtexEntry(tag, 
					mapMSBibToBibtexType(sourceType)); 
		

		








































		HashMap hm = new HashMap();
		
		if(tag != null)
			hm.put("bibtexkey",tag);


		if(LCID >= 0)
			hm.put("language",getLanguage(LCID));
		if(title != null)
			hm.put("title",title);
		if(year != null)
			hm.put("year",year);
		if(shortTitle != null)
			hm.put(MSBIB+"shorttitle",shortTitle);
		if(comments != null)
			hm.put("note",comments);

		addAuthor(hm,"author",authors);
		addAuthor(hm,MSBIB+"bookauthor",bookAuthors);
		addAuthor(hm,"editor",editors);
		addAuthor(hm,MSBIB+"translator",translators);
		addAuthor(hm,MSBIB+"producername",producerNames);
		addAuthor(hm,MSBIB+"composer",composers);
		addAuthor(hm,MSBIB+"conductor",conductors);
		addAuthor(hm,MSBIB+"performer",performers);
		addAuthor(hm,MSBIB+"writer",writers);
		addAuthor(hm,MSBIB+"director",directors);
		addAuthor(hm,MSBIB+"compiler",compilers);
		addAuthor(hm,MSBIB+"interviewer",interviewers);
		addAuthor(hm,MSBIB+"interviewee",interviewees);
		addAuthor(hm,MSBIB+"inventor",inventors);
		addAuthor(hm,MSBIB+"counsel",counsels);
   		
		if(pages !=null )
			hm.put("pages",pages.toString("--"));
		if(volume !=null )
			hm.put("volume",volume);
		if(numberOfVolumes !=null )
			hm.put(MSBIB+"numberofvolume",numberOfVolumes);
		if(edition !=null )
			hm.put("edition",edition);
		if(edition !=null )
			hm.put("edition",edition);
		parseStandardNumber(standardNumber,hm);

		if(publisher !=null )
			hm.put("publisher",publisher);
		if(publisher !=null )
			hm.put("publisher",publisher);
		if(address !=null )
			hm.put("address",address);
		if(bookTitle !=null )
			hm.put("booktitle",bookTitle);
		if(chapterNumber !=null )
			hm.put("chapter",chapterNumber);
		if(journalName !=null )
			hm.put("journal",journalName);
		if(issue !=null )
			hm.put("number",issue);
		if(periodicalTitle !=null )
			hm.put("organization",periodicalTitle);
		if(conferenceName !=null )
			hm.put("organization",conferenceName);
		if(department !=null )
			hm.put("school",department);
		if(institution !=null )
			hm.put("institution",institution);




		if(dateAccessed !=null )
			hm.put(MSBIB+"accessed",dateAccessed);
		if(url !=null )
			hm.put("url",url);
		if(productionCompany !=null )
			hm.put(MSBIB+"productioncompany",productionCompany);


		if(medium !=null )
			hm.put(MSBIB+"medium",medium);


		if(recordingNumber !=null )
			hm.put(MSBIB+"recordingnumber",recordingNumber);
		if(theater !=null )
			hm.put(MSBIB+"theater",theater);
		if(distributor !=null )
			hm.put(MSBIB+"distributor",distributor);


		if(broadcaster !=null )
			hm.put(MSBIB+"broadcaster",broadcaster);
		if(station !=null )
			hm.put(MSBIB+"station",station);
		if(type !=null )
			hm.put(MSBIB+"type",type);
		if(patentNumber !=null )
			hm.put(MSBIB+"patentnumber",patentNumber);
		if(court !=null )
			hm.put(MSBIB+"court",court);
		if(reporter !=null )
			hm.put(MSBIB+"reporter",reporter);
		if(caseNumber !=null )
			hm.put(MSBIB+"casenumber",caseNumber);
		if(abbreviatedCaseNumber !=null )
			hm.put(MSBIB+"abbreviatedcasenumber",abbreviatedCaseNumber);

		if(bibTex_Series !=null )
			hm.put("series",bibTex_Series);
		if(bibTex_Abstract !=null )
			hm.put("abstract",bibTex_Abstract);
		if(bibTex_KeyWords !=null )
			hm.put("keywords",bibTex_KeyWords);
		if(bibTex_CrossRef !=null )
			hm.put("crossref",bibTex_CrossRef);
		if(bibTex_HowPublished !=null )
			hm.put("howpublished",bibTex_HowPublished);
		if(bibTex_Affiliation !=null )
			hm.put("affiliation",bibTex_Affiliation);
		if(bibTex_Contents !=null )
			hm.put("contents",bibTex_Contents);
		if(bibTex_Copyright !=null )
			hm.put("copyright",bibTex_Copyright);
		if(bibTex_Price !=null )
			hm.put("price",bibTex_Price);
		if(bibTex_Size !=null )
			hm.put("size",bibTex_Size);

		entry.setField(hm);
		return entry;
	}

	
    public String stripNonValidXMLCharacters(String in) {
        StringBuffer out = new StringBuffer(); 
        char current; 

        if (in == null || ("".equals(in))) return ""; 
        for (int i = 0; i < in.length(); i++) {
            current = in.charAt(i); 
            if ((current == 0x9) ||
                (current == 0xA) ||
                (current == 0xD) ||
                ((current >= 0x20) && (current <= 0xD7FF)) ||
                ((current >= 0xE000) && (current <= 0xFFFD)) ||
                ((current >= 0x10000) && (current <= 0x10FFFF)))
                out.append(current);
        }
        return out.toString();
    }

	
	public String toString() {
		StringWriter sresult = new StringWriter();
	   	try {
	      	 DOMSource source = new DOMSource(getDOMrepresentation());
	      	 StreamResult result = new StreamResult(sresult);
	      	 Transformer trans = TransformerFactory.newInstance().newTransformer();
	      	 trans.setOutputProperty(OutputKeys.INDENT, "yes");
	      	 trans.transform(source, result);
	      	}
	      	catch (Exception e) {
	      		throw new Error(e);
	      	}
	      return sresult.toString();
	}

}
