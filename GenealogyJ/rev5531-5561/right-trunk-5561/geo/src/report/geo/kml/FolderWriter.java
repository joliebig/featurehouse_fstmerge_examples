package geo.kml;

import java.io.IOException;
import java.io.Writer;

public abstract class FolderWriter extends KmlWriter {
	String properties;

	public FolderWriter(Writer out, boolean radio, int folded) {
		super(out);
		properties = "<visibility>" + folded + "</visibility><open>" + folded + "</open>"
				+ "<styleUrl>#" + (radio ? "radio" : "check")
				+ "Folder</styleUrl>" + "\n";
	}

	public void write(String indent, String name, String description)
			throws IOException {
		out.write(indent + "<Folder>" + "<name>" + name + "</name>"
				+ description + properties + "\n");
		writeContent(indent + "\t");
		out.write("\n" + indent + "</Folder>\n");
	}

	public abstract void writeContent(String indent) throws IOException;
}

