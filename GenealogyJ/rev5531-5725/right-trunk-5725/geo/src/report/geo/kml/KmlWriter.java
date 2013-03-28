package geo.kml;

import java.io.Writer;

abstract class KmlWriter {
	Writer out;

	KmlWriter (Writer out)
	{
		this.out = out;
	}
}

