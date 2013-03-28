package net.sourceforge.squirrel_sql.plugins.dataimport.importer;


import java.io.File;
import java.io.IOException;

import net.sourceforge.squirrel_sql.plugins.dataimport.ImportFileType;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.csv.CSVFileImporter;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.excel.ExcelFileImporter;


public class FileImporterFactory {
	
	public static IFileImporter createImporter(ImportFileType type, File importFile) throws IOException {
		IFileImporter importer = null;
		
		switch (type) {
		case CSV:
			importer = new CSVFileImporter(importFile);
			break;
		case XLS:
			importer = new ExcelFileImporter(importFile);
			break;
			default:
				throw new IllegalArgumentException("No such type: " + type.toString());
		}
		return importer;
	}

}
