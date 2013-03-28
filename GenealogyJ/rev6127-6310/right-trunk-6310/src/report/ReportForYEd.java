

import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.PropertyEvent;
import genj.gedcom.PropertySex;
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
import java.util.Collection;

public class ReportForYEd extends Report {

	public int imageWidth = 100;
	public int imageHeight = 150;
	public String indiUrl = getString("indiUrlDefault");
	public String familyUrl = getString("familyUrlDefault");
	public boolean showDates = true;
	public boolean showPlaces = true;
	public boolean showOccupation = true;
	public String imageExtensions = "jpg jpeg gif png";

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

		generateReport(gedcom.getFamilies(), gedcom.getIndis());
	}

	private void generateReport(Collection<Fam> families, Collection<Indi> indis)
			throws FileNotFoundException, IOException {
		
		final Writer out = createWriter();
		if (out == null)
			return;
		println("creating: " + reportFile.getAbsoluteFile());

		out.write(XML_HEAD);
		for (final Fam fam : families) {
			out.write(createNode(fam));
		}
		for (final Indi indi : indis) {
			out.write(createNode( indi));
			out.write(createIndiToFam(indi,families));
			out.write(createFamToIndi(indi,families));
		}
		out.write(XML_TAIL);

		out.flush();
		out.close();
		println("ready with: " + reportFile.getAbsoluteFile());
	}

	private static String[] createIndiColors() {

		final String[] result = new String[3];
		result[PropertySex.MALE] = "#CCCCFF";
		result[PropertySex.FEMALE] = "#FF99CC";
		result[PropertySex.UNKNOWN] = "#CCCCCC";
		return result;
	}

	private String createIndiToFam(final Indi indi, Collection<Fam> families) {

		String s = "";
		for (final Fam fam : indi.getFamiliesWhereSpouse()) {
			if (families.contains(fam))
				s += MessageFormat.format(XML_EDGE, edgeCount++, indi.getId(),
						fam.getId());
		}
		return s;
	}

	private String createFamToIndi(final Indi indi, Collection<Fam> families) {

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
		return MessageFormat.format(XML_FAMILY, id, createLabel(family),
				createLink(id, familyUrl),
				createPopUpContainer(createPopUpContent(family)));
	}

	private String createNode(final Indi indi) {

		final String id = indi.getId();
		return MessageFormat.format(XML_INDI, id, createLabel(indi),
				createLink(id, indiUrl), INDI_COLORS[indi.getSex()],
				createPopUpContainer(createPopUpContent(indi)));
	}

	private String getImage(final Entity entity) {

		if (imageHeight == 0 || imageWidth == 0)
			return null;
		final Property property = entity.getPropertyByPath("INDI:OBJE:FILE");
		if (property == null)
			return null;
		final String value = property.getValue();
		final String extension = value.toLowerCase().replaceAll(".*\\.", "");
		if (imageExtensions.contains(extension)) {
			return value;
		}
		return null;
	}

	private String createLabel(final Fam family) {

		final String image = getImage(family);
		final String mariage = showEvent(OPTIONS.getMarriageSymbol(),
				(PropertyEvent) family.getProperty("MARR"));
		final String divorce = showEvent(OPTIONS.getDivorceSymbol(),
				(PropertyEvent) family.getProperty("DIV"));

		if (mariage == null && divorce == null && image == null)
			return "";
		final String format;
		if (image != null) {
			format = "<html><body><table><tr>"
					+ "<td>{0}<br>{1}</td>"
					+ "<td><img src='{3}' width='{4}' heigth='{5}'></td>"
					+ "</tr></table></body></html>";
		} else {
			format = "<html><body>{0}<br>{1}</body></html>";
		}
		return wrap(format, mariage, divorce, image, imageWidth, imageHeight);
	}

	private String createLabel(final Indi indi) {

		final String image = getImage(indi);
		final String name = indi.getPropertyDisplayValue("NAME");
		final String occu = indi.getPropertyDisplayValue("OCCU");

		final String birth = showEvent(OPTIONS.getBirthSymbol(),
				(PropertyEvent) indi.getProperty("BIRT"));
		final String death = showEvent(OPTIONS.getDeathSymbol(),
				(PropertyEvent) indi.getProperty("DEAT"));

		final String format;
		if (image != null) {
			format = "<html><body><table><tr>"
					+ "<td>{0}<br>{1}<br>{2}<br>{3}</td>"
					+ "<td><img src=\"{4}\" width=\"{5}\" heigth=\"{6}\"></td>"
					+ "</tr></table></body></html>";
		} else if (showOccupation) {
			format = "<html><body>{0}<br>{1}<br>{2}<br>{3}</body></html>";
		} else if (showDates || showPlaces) {
			format = "<html><body>{0}<br>{1}<br>{2}</body></html>";
		} else {
			format = "<html><body>{0}</body></html>";
		}
		return wrap(format, name, birth, death, occu, image, imageWidth,
				imageHeight);
	}

	private String wrap(final String format, final Object... args) {
		return MessageFormat.format(format, args).replaceAll(">", "&gt;")
				.replaceAll("<", "&lt;");
	}

	private String showEvent(final String symbol, final PropertyEvent event) {

		if (event == null || !(showDates || showPlaces))
			return "";
		final Property date = event.getDate(true);
		final Property place = event.getProperty("PLAC");
		if (date == null && place == null)
			return "";
		return symbol
				+ " "
				+ (date == null || !showDates ? "" : date.getDisplayValue())
				+ " "
				+ (place == null || !showPlaces ? "" : place.getDisplayValue()
						.replaceAll(",.*", ""));
	}

	private String createPopUpContent(final Fam family) {
		
		return null;
	}

	private String createPopUpContent(final Indi indi) {
		
		return null;
	}

	private String createLink(final String id, final String urlFormat) {

		if (urlFormat == null)
			return "";
		final String link = MessageFormat.format(urlFormat, id);
		return MessageFormat.format(XML_LINK_CONTAINER, link);
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
