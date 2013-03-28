package geo.kml;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.geo.GeoLocation;

import java.io.IOException;
import java.io.Writer;

public class DetailedPlacemarkWriter extends PlacemarkWriter {

	public DetailedPlacemarkWriter(Writer out, boolean showIds) {
		super(out, 0, showIds);
	}

	protected void writePlacemarkContent(String indent, GeoLocation location,
			boolean showIds) throws IOException {
		for (int p = 0; p < location.getNumProperties(); p++) {
			Property prop = location.getProperty(p);
			Entity entity = prop.getEntity();
			out.write(indent);
			addDate(prop);
			
			
			out.write(Gedcom.getName(prop.getTag()));

			out.write(" " + entity.toString(showIds, showIds));
			out.write("<br>\n");
		}
	}

	private void addDate(Property prop) throws IOException {
		Property date = prop.getProperty("DATE", true);
		if (date != null) {
			out.write(date.toString());
			out.write(" ");
		}
	}
}
