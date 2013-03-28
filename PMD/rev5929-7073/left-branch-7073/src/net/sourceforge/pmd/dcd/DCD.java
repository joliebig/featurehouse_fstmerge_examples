
package net.sourceforge.pmd.dcd;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.dcd.graph.UsageGraph;
import net.sourceforge.pmd.dcd.graph.UsageGraphBuilder;
import net.sourceforge.pmd.util.FileFinder;
import net.sourceforge.pmd.util.filter.Filter;
import net.sourceforge.pmd.util.filter.Filters;


public class DCD {
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public static void dump(UsageGraph usageGraph, boolean verbose) {
		usageGraph.accept(new DumpNodeVisitor(), Boolean.valueOf(verbose));
	}

	public static void report(UsageGraph usageGraph, boolean verbose) {
		usageGraph.accept(new UsageNodeVisitor(), Boolean.valueOf(verbose));
	}

	public static void main(String[] args) throws Exception {
		
		List<File> directories = new ArrayList<File>();
		directories.add(new File("C:/pmd/workspace/pmd-trunk/src"));

		
		FilenameFilter javaFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				
				if (new File(dir, name).isDirectory()) {
					return true;
				} else {
					
					if (name.startsWith("EJS") || name.startsWith("_") || dir.getPath().indexOf("com\\ibm\\") >= 0
							|| dir.getPath().indexOf("org\\omg\\") >= 0) {
						return false;
					}
					return name.endsWith(".java");
				}
			}
		};

		
		List<FilenameFilter> filters = new ArrayList<FilenameFilter>();
		filters.add(javaFilter);

		assert (directories.size() == filters.size());

		
		List<String> classes = new ArrayList<String>();
		for (int i = 0; i < directories.size(); i++) {
			File directory = directories.get(i);
			FilenameFilter filter = filters.get(i);
			List<File> files = new FileFinder().findFilesFrom(directory.getPath(), filter, true);
			for (File file : files) {
				String name = file.getPath();

				
				name = name.substring(directory.getPath().length() + 1);

				
				name = name.replaceAll("\\.java$", "");

				
				name = name.replace('\\', '.');
				name = name.replace('/', '.');

				classes.add(name);
			}
		}

		long start = System.currentTimeMillis();

		
		
		List<String> includeRegexes = Arrays.asList(new String[] { "net\\.sourceforge\\.pmd\\.dcd.*", "us\\..*" });
		List<String> excludeRegexes = Arrays.asList(new String[] { "java\\..*", "javax\\..*", ".*\\.twa\\..*" });
		Filter<String> classFilter = Filters.buildRegexFilterExcludeOverInclude(includeRegexes, excludeRegexes);
		System.out.println("Class filter: " + classFilter);

		
		UsageGraphBuilder builder = new UsageGraphBuilder(classFilter);
		int total = 0;
		for (String clazz : classes) {
			System.out.println("indexing class: " + clazz);
			builder.index(clazz);
			total++;
			if (total % 20 == 0) {
				System.out.println(total + " : " + total / ((System.currentTimeMillis() - start) / 1000.0));
			}
		}

		
		boolean dump = true;
		boolean deadCode = true;
		UsageGraph usageGraph = builder.getUsageGraph();
		if (dump) {
			System.out.println("--- Dump ---");
			dump(usageGraph, true);
		}
		if (deadCode) {
			System.out.println("--- Dead Code ---");
			report(usageGraph, true);
		}
		long end = System.currentTimeMillis();
		System.out.println("Time: " + (end - start) / 1000.0);
	}
}