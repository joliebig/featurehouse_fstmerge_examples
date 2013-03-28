
package net.sourceforge.squirrel_sql.plugins.oracle.sqlloader.model;

import static java.io.File.separator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class ControlFileGenerator {
	
	
	public static void writeControlFile(String table, String[] columns, boolean append, String fieldSeparator, String stringDelimitator, String directory) throws IOException {
		final String controlFileExtension = ".ctl";
		BufferedWriter controlFileWriter = null;
		try {
			controlFileWriter = new BufferedWriter(
					new FileWriter(normalizeDirectoryPath(directory) + table + controlFileExtension));
			
			controlFileWriter.write("load data\n\t" +
										(append?"append\n\t":"replace\n\t") +
										"into table " + table + "\n\t" +
										"fields terminated by '" + fieldSeparator + "' " + 
										(stringDelimitator.length()==0?"":"optionally enclosed by " + (stringDelimitator.equals("'")?"\"'\"":"'" + stringDelimitator + "'") + "\n\t("));
			
			int lastFieldIndex = columns.length - 1;
			for (int i = 0; i < lastFieldIndex; i++) {
				controlFileWriter.write(columns[i] + ", ");
			}
			
			controlFileWriter.write(columns[lastFieldIndex] + ")\n");
		} finally {
			if (controlFileWriter != null) {
				try {
					controlFileWriter.close();
				} catch (IOException e1) {
					
				}
			}
		}
	}

	
	private static String normalizeDirectoryPath(String directory) {
		
		if (directory!=null && directory.length()>0 && !directory.endsWith(separator)) {
			return directory + separator;
		}
		return directory;
	}
}
