package geo.kml;

import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Indi;
import genj.gedcom.PropertyDate;
import genj.gedcom.time.PointInTime;
import genj.geo.GeoLocation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Names {
	Map<String, PropertyDate> names = new HashMap<String, PropertyDate>();

	Names(GeoLocation location) {
		for (int p = 0; p < location.getNumProperties(); p++) {
			Entity entity = location.getProperty(p).getEntity();
			PropertyDate date = location.getProperty(p).getWhen();
			if (entity instanceof Fam) {
				addName(((Fam) entity).getHusband(), date);
				addName(((Fam) entity).getWife(), date);
			}
			if (entity instanceof Indi) {
				addName(entity, date);
			}
		}
	}

	private void addName(Entity entity, PropertyDate date) {
		String name = getName(entity);
		PropertyDate period = names.get(name);
		if (period == null) {
			names.put(name, date);
		} else {
			if (date.getStart().getYear() < period.getStart().getYear()) {
				period.getStart().set(date.getStart());
			}
			PointInTime pointInTime = date.getEnd();
			if (pointInTime.getYear() < 0 || pointInTime.getYear() > 9999) {
				pointInTime = date.getStart();
			}
			int year = period.getEnd().getYear();
			if (year < 0 || year > 9999 || year < pointInTime.getYear()) {
				period.getEnd().set(pointInTime);
			}
		}
	}

	static public String getName(Entity entity) {
		String name;
		try {
			name = ((Indi) entity).getLastName().toString();
		} catch (NullPointerException e) {
			try {
				name = ((Indi) entity).getName().toString();
			} catch (NullPointerException e2) {
				name = "";
			}
		}
		return name;
	}

	public String getValue(String key) {
		try {
			PropertyDate period = names.get(key);
			int year = period.getEnd().getYear();
			return period.getStart().getYear()
					+ (year < 9999 ? "-" + year : "");
		} catch (NullPointerException e2) {
			return "";
		}
	}

	public Iterator<String> keys() {
		return names.keySet().iterator();
	}
}
