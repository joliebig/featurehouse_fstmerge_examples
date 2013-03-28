package geo.kml;

import genj.geo.GeoLocation;

import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

abstract class PlacemarkWriter extends KmlWriter{

	private final static DecimalFormat FORMAT 
	= new DecimalFormat("##0.###", new DecimalFormatSymbols(Locale.US));

	private final int folded;

	private boolean showIds;
	
	PlacemarkWriter(Writer out, int folded, boolean showIds) {
		super(out);
		this.folded = folded;
		this.showIds = showIds;
	}

	public void write(
			final String indent, 
			final Iterator<GeoLocation> markers,
			final String name, 
			final String description, 
			final boolean radio, 
			final Iterator<GeoLocation> lines)
			throws IOException {

		if (markers==null && lines == null)
			return;

		new FolderWriter(out, radio, 0) {

			public void writeContent(final String indent) throws IOException {
				while (markers.hasNext()) {
					writePlacemark(indent, markers.next());
				}
				writeLine(lines, indent);
			}

		}.write(indent, name, description);
	}

	private void writePoint(final GeoLocation location) throws IOException{
		out.write( "<Point>" 
		+ "<coordinates>" 
		+ FORMAT.format(location.getX()) + "," 
		+ FORMAT.format(location.getY()) 
		+ "</coordinates></Point>");
	}
	
	public void writeLine(final Collection<GeoLocation> locations, String indent) throws IOException{
		if (locations==null) return;
		out.write( indent+"<Placemark><LineString><coordinates>");
		for (GeoLocation location:locations){
			String x = FORMAT.format(location.getX());
			String y = FORMAT.format(location.getY());
			out.write( x + "," + y +"\n"+indent);
		}
		out.write( "</coordinates></LineString></Placemark>\n");
	}
	
	public void writeLine(final Iterator<GeoLocation> locations, String indent) throws IOException{
		if (locations==null) return;
		out.write( indent+"<Placemark><LineString><coordinates>");
		while (locations.hasNext()) {
			GeoLocation location = locations.next();
			String x = FORMAT.format(location.getX());
			String y = FORMAT.format(location.getY());
			out.write( x + "," + y +"\n"+indent);
		}
		out.write( "</coordinates></LineString></Placemark>\n");
	}
	
	private void writePlacemark(final String indent,
			final GeoLocation location) throws IOException {
		if (!location.isValid())
			return;
		out.write(indent
				+ "<Placemark>" 
				+ "<name>" + location.toString() + "</name>"
				+ "<Snippet maxLines='1'/><description><![CDATA[\n");
		writePlacemarkContent(indent + "\t", location, showIds);
		out.write(indent + "]]></description>");
		writePoint(location);
		out.write( "</Placemark>\n");
	}

	protected abstract void writePlacemarkContent(String indent, GeoLocation location, boolean showIds)
			throws IOException;
}
