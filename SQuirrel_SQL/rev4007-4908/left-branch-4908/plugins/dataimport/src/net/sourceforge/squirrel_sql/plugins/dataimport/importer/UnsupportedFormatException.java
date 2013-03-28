package net.sourceforge.squirrel_sql.plugins.dataimport.importer;


public class UnsupportedFormatException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public UnsupportedFormatException() {}
	
	public UnsupportedFormatException(Exception e) {
		super(e);
	}
}
