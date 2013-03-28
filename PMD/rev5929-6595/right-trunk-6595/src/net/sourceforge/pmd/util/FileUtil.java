package net.sourceforge.pmd.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.FileDataSource;
import net.sourceforge.pmd.util.datasource.ZipDataSource;
import net.sourceforge.pmd.util.filter.AndFilter;
import net.sourceforge.pmd.util.filter.Filter;
import net.sourceforge.pmd.util.filter.Filters;
import net.sourceforge.pmd.util.filter.OrFilter;


public class FileUtil {

    
    public static List<DataSource> collectFiles(String fileLocations, FilenameFilter filenameFilter) {
	List<DataSource> dataSources = new ArrayList<DataSource>();
	for (String fileLocation : fileLocations.split(",")) {
	    collect(dataSources, fileLocation, filenameFilter);
	}
	return dataSources;
    }

    private static List<DataSource> collect(List<DataSource> dataSources, String fileLocation,
	    FilenameFilter filenameFilter) {
	File file = new File(fileLocation);
	if (!file.exists()) {
	    throw new RuntimeException("File " + file.getName() + " doesn't exist");
	}
	if (!file.isDirectory()) {
	    if (fileLocation.endsWith(".zip") || fileLocation.endsWith(".jar")) {
		ZipFile zipFile;
		try {
		    zipFile = new ZipFile(fileLocation);
		    Enumeration<? extends ZipEntry> e = zipFile.entries();
		    while (e.hasMoreElements()) {
			ZipEntry zipEntry = e.nextElement();
			if (filenameFilter.accept(null, zipEntry.getName())) {
			    dataSources.add(new ZipDataSource(zipFile, zipEntry));
			}
		    }
		} catch (IOException ze) {
		    throw new RuntimeException("Archive file " + file.getName() + " can't be opened");
		}
	    } else {
		dataSources.add(new FileDataSource(file));
	    }
	} else {
	    
	    
	    Filter<File> filter = new OrFilter<File>(Filters.toFileFilter(filenameFilter), new AndFilter<File>(Filters
		    .getDirectoryFilter(), Filters.toNormalizedFileFilter(Filters.buildRegexFilterExcludeOverInclude(
		    null, Collections.singletonList("SCCS")))));
	    FileFinder finder = new FileFinder();
	    List<File> files = finder.findFilesFrom(file.getAbsolutePath(), Filters.toFilenameFilter(filter), true);
	    for (File f : files) {
		dataSources.add(new FileDataSource(f));
	    }
	}
	return dataSources;
    }
}
