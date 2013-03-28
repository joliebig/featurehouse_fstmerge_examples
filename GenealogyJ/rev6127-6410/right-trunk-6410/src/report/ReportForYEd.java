

import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.PropertyEvent;
import genj.gedcom.PropertySex;
import genj.gedcom.time.Delta;
import genj.gedcom.time.PointInTime;
import genj.report.Options;
import genj.report.Report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class ReportForYEd extends Report {

	public class Events {

		public boolean showDates = true;
		public boolean showPlaces = true;
		public String place_display_format = "";

		private String format(final String symbol, final PropertyEvent event) {

			if (event == null || !(showDates || showPlaces))
				return "";
			final Property date = event.getDate(true);
			final Property place = event.getProperty("PLAC");
			if (date == null && place == null)
				return "";
			final String string = (date == null || !showDates ? "" : date
					.getDisplayValue())
					+ " "
					+ (place == null || !showPlaces ? "" : place.format(
							place_display_format).replaceAll("^(,|(, ))*", "")
							.trim());
			if (string.trim().equals(""))
				return "";
			return symbol + " " + string;
		}
	}

	public class Gender {
		public String unknown = "";
		public String male = "\u";
		public String female = "\u";
		private final String[] snippets = new String[3];

		public Gender() {
			snippets[PropertySex.UNKNOWN] = unknown;
			snippets[PropertySex.MALE] = male;
			snippets[PropertySex.FEMALE] = female;
		}

		private String format(final Indi indi) {
			return snippets[indi.getSex()];
		}
	}

	public class Images {

		public String famImage = translate("imageSnippetDefault");
		public String indiImage = translate("imageSnippetDefault");
		public String imageExtensions = "jpg jpeg gif png";

		private String format(final Entity entity, final String htmlFormat) {

			if (htmlFormat == null || htmlFormat.equals(""))
				return null;
			final Property property = entity instanceof Indi ? entity
					.getPropertyByPath("INDI:OBJE:FILE") : entity
					.getPropertyByPath("FAM:OBJE:FILE");
			if (property == null)
				return null;
			final String value = property.getValue();
			if (value == null || value.equals(""))
				return null;
			final String extension = value.toLowerCase()
					.replaceAll(".*\\.", "");
			if (imageExtensions.contains(extension)) {
				return MessageFormat.format(htmlFormat, value);
			}
			return null;
		}
	}

	public class Filter {

		public String tag = "_YED";
		public String content = "";
		public boolean active = true;
		public boolean descendants = true;
		public boolean ancestors = true;
	}

	public class Links {

		public String indi = translate("indiUrlDefault");
		public String family = translate("familyUrlDefault");

		private String format(final String id, final String urlFormat) {

			if (urlFormat == null)
				return "";
			final String link = MessageFormat.format(urlFormat, id);
			return MessageFormat.format(XML_LINK_CONTAINER, link);
		}
	}

	public boolean showOccupation = true;
	public Events events = new Events();
	public Images images = new Images();
	public Links links = new Links();
	public Filter filter = new Filter();
	public Gender gender = new Gender();

	private final String XML_LINK_CONTAINER = getString("LinkContainer");
	private final String XML_POPUP_CONTAINER = getString("PopUpContainer");
	private final String XML_FAMILY = getString("FamilyNode");
	private final String XML_INDI = getString("IndiNode");
	private final String XML_EDGE = getString("Edge");
	private final String XML_HEAD = getString("XmlHead");
	private final String XML_TAIL = getString("XmlTail");

	private static final String INDI_COLORS[] = createIndiColors();
	private static final Options OPTIONS = Options.getInstance();

	private int edgeCount = 0;
	private File reportFile;

	
	public void start(final Gedcom gedcom) throws IOException {

		if (!filter.active) {
			generateReport(gedcom.getFamilies(), gedcom.getIndis());
			return;
		}
		final Collection<Indi> indis = new HashSet<Indi>();
		final Collection<Fam> fams = new HashSet<Fam>();
		
		for (final Fam fam : gedcom.getFamilies()) {
			final String value = fam.getPropertyValue(filter.tag);
			if (value != null && value.contains(filter.content)) {
				fams.add(fam);
				indis.add(fam.getHusband());
				indis.add(fam.getWife());
				if (filter.descendants) {
					collectDecendants(indis, fams, fam.getHusband());
					collectDecendants(indis, fams, fam.getWife());
				}
				if (filter.ancestors) {
					collectAncestors(indis, fams, fam.getHusband());
					collectAncestors(indis, fams, fam.getWife());
				}
			}
		}
		
		for (final Indi indi : gedcom.getIndis()) {
			final String value = indi.getPropertyValue(filter.tag);
			if (value != null && value.contains(filter.content)) {
				indis.add(indi);
				for (final Fam fam : indi.getFamiliesWhereSpouse()) {
					fams.add(fam);
				}
				if (filter.descendants) {
					collectDecendants(indis, fams, indi);
				}
				if (filter.descendants) {
					collectAncestors(indis, fams, indi);
				}
			}
		}
		indis.remove(null);
		generateReport(fams, indis);
	}

	
	public void start(final Indi indi) throws IOException {

		final Collection<Indi> indis = new HashSet<Indi>();
		final Collection<Fam> fams = new HashSet<Fam>();
		buildCollections(indi, indis, fams);
		generateReport(fams, indis);
	}

	
	public void start(final Fam fam) throws IOException {

		final Collection<Indi> indis = new HashSet<Indi>();
		final Collection<Fam> fams = new HashSet<Fam>();
		buildCollections(fam.getHusband(), indis, fams);
		buildCollections(fam.getWife(), indis, fams);
		generateReport(fams, indis);
	}

	private void buildCollections(final Indi indi,
			final Collection<Indi> indis, final Collection<Fam> fams)
			throws FileNotFoundException, IOException {
		collectAncestors(indis, fams, indi);
		collectDecendants(indis, fams, indi);
	}

	private void collectAncestors(final Collection<Indi> indis,
			final Collection<Fam> fams, final Indi indi) {

		if (indi == null)
			return;
		indis.add(indi);
		for (final Fam fam : indi.getFamiliesWhereChild()) {
			fams.add(fam);
			collectAncestors(indis, fams, fam.getHusband());
			collectAncestors(indis, fams, fam.getWife());
		}
	}

	
	private void collectDecendants(final Collection<Indi> indis,
			final Collection<Fam> fams, final Indi indi) {

		if (indi == null)
			return;
		indis.add(indi); 
		for (final Fam fam : indi.getFamiliesWhereSpouse()) {
			
			indis.add(fam.getHusband());
			indis.add(fam.getWife());
			fams.add(fam);
			for (final Indi child : fam.getChildren()) {
				collectDecendants(indis, fams, child);
			}
		}
	}

	private static final PointInTime pit = new PointInTime(1, 1, 2200);

	
	private void generateReport(final Collection<Fam> families,
			final Collection<Indi> indis) throws FileNotFoundException,
			IOException {
		println(MessageFormat.format("{0} persons {1} families",
				indis.size(), families.size()) );

		final List<Indi> sortedIndis = sortByAge(indis);

		final Writer out = createWriter();
		if (out == null)
			return;
		println("creating: " + reportFile.getAbsoluteFile());

		out.write(XML_HEAD + "\n");
		for (final Indi indi : sortedIndis) {
			out.write(createNode(indi) + "\n");
		}
		for (final Fam fam : families) {
			out.write(createNode(fam) + "\n");
		}
		for (final Indi indi : sortedIndis) {
			out.write(createIndiToFam(indi, families) + "\n");
			out.write(createFamToIndi(indi, families) + "\n");
		}
		out.write(XML_TAIL + "\n");

		out.flush();
		out.close();
		println("ready");
	}

	private List<Indi> sortByAge(final Collection<Indi> indis) {
		
		final List<Indi> sortedIndis = new ArrayList<Indi>(indis);
		Collections.sort(sortedIndis, new Comparator<Indi>() {

			@Override
			public int compare(final Indi i1, final Indi i2) {

				final Delta p1 = i1.getAge(pit);
				final Delta p2 = i2.getAge(pit);

				
				if (p1 == p2)
					return 0;
				if (p1 == null)
					return 1;
				if (p2 == null)
					return -1;

				
				return -p1.compareTo(p2);
			}
		});
		return sortedIndis;
	}

	private static String[] createIndiColors() {

		final String[] result = new String[3];
		result[PropertySex.MALE] = "#CCCCFF";
		result[PropertySex.FEMALE] = "#FF99CC";
		result[PropertySex.UNKNOWN] = "#CCCCCC";
		return result;
	}

	private String createIndiToFam(final Indi indi,
			final Collection<Fam> families) {

		String s = "";
		for (final Fam fam : indi.getFamiliesWhereSpouse()) {
			if (families.contains(fam))
				s += MessageFormat.format(XML_EDGE, edgeCount++, indi.getId(),
						fam.getId());
		}
		return s;
	}

	private String createFamToIndi(final Indi indi,
			final Collection<Fam> families) {

		String s = "";
		for (final Fam fam : indi.getFamiliesWhereChild()) {
			if (families.contains(fam))
				s += MessageFormat.format(XML_EDGE, edgeCount++, fam.getId(),
						indi.getId());
		}
		return s;
	}

	private String createNode(final Fam family) {

		final String id = family.getId();
		final String label = createLabel(family);
		final String height = label.contains("<html>") ? "42.0" : "27.0";
		return MessageFormat.format(XML_FAMILY, id, escape(label), links
				.format(id, links.family), createPopUpContainer(label), height);
	}

	private String createNode(final Indi indi) {

		final String id = indi.getId();
		final String label = createLabel(indi);
		return MessageFormat.format(XML_INDI, id, escape(label), links.format(
				id, links.indi), INDI_COLORS[indi.getSex()],
				createPopUpContainer(label));
	}

	private String createLabel(final Fam family) {

		final String image = images.format(family, images.famImage);
		final String mariage = events.format(OPTIONS.getMarriageSymbol(),
				(PropertyEvent) family.getProperty("MARR"));
		final String divorce = events.format(OPTIONS.getDivorceSymbol(),
				(PropertyEvent) family.getProperty("DIV"));

		if (mariage.equals("") && divorce.equals("") && image == null)
			return "";
		final String format;
		if (image != null) {
			format = "<html><table><tr><td><p>{0}<br>{1}</p></td><td>{2}</td></tr></table></body></html>";
		} else if (divorce.equals("") || mariage.equals("")) {
			format = "{0}{1}";
		} else {
			format = "<html><body>{0}<br>{1}</body></html>";
		}
		return wrap(format, mariage, divorce, image);
	}

	private String createLabel(final Indi indi) {

		final String image = images.format(indi, images.indiImage);
		final String sex = gender.format(indi);
		final String name = indi.getPropertyDisplayValue("NAME");
		final String occu = indi.getPropertyDisplayValue("OCCU");

		final String birth = events.format(OPTIONS.getBirthSymbol(),
				(PropertyEvent) indi.getProperty("BIRT"));
		final String death = events.format(OPTIONS.getDeathSymbol(),
				(PropertyEvent) indi.getProperty("DEAT"));

		final String format;
		if (image != null) {
			format = "<html><table><tr><td>{5}<p>{0}<br>{1}<br>{2}<br>{3}</p></td><td>{4}</td></tr></table></body></html>";
		} else if (showOccupation && occu != null && !occu.trim().equals("")) {
			format = "<html><body>{5}<p>{0}<br>{1}<br>{2}<br>{3}</p></body></html>";
		} else if (!birth.equals("") || !death.equals("")) {
			format = "<html><body>{5}<p>{0}<br>{1}<br>{2}</p></body></html>";
		} else if (!sex.equals("")) {
			format = "<html><body>{5}<p>{0}</p></body></html>";
		} else {
			format = "{0}";
		}
		return wrap(format, name, birth, death, occu, image, sex);
	}

	private String wrap(final String format, final Object... args) {

		return MessageFormat.format(format, args).replaceAll("'", "\"");
	}

	private String escape(final String content) {

		return content.replaceAll(">", "&gt;").replaceAll("<", "&lt;");
	}

	
	private String createPopUpContainer(final String content) {

		if (content == null)
			return "";
		return MessageFormat.format(XML_POPUP_CONTAINER, content);
	}

	private String getString(final String key) {

		return getResources().getString(key);
	}

	private Writer createWriter() throws FileNotFoundException {

		final String extension = "graphml";
		reportFile = getFileFromUser(translate("name"), translate("save"),
				true, extension);
		if (reportFile == null)
			return null;
		if (!reportFile.getName().toLowerCase().endsWith("." + extension)) {
			reportFile = new File(reportFile.getPath() + "." + extension);
		}
		final FileOutputStream fileOutputStream = new FileOutputStream(
				reportFile);
		final OutputStreamWriter streamWriter = new OutputStreamWriter(
				fileOutputStream, Charset.forName("UTF8"));
		return new BufferedWriter(streamWriter);
	}
}
