
package net.sourceforge.pmd.lang;


public class ParserOptions {
    protected String suppressMarker;

    public String getSuppressMarker() {
	return suppressMarker;
    }

    public void setSuppressMarker(String suppressMarker) {
	this.suppressMarker = suppressMarker;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null || getClass() != obj.getClass()) {
	    return false;
	}
	final ParserOptions that = (ParserOptions) obj;
	return this.suppressMarker.equals(that.suppressMarker);
    }

    @Override
    public int hashCode() {
	return suppressMarker != null ? suppressMarker.hashCode() : 0;
    }
}
