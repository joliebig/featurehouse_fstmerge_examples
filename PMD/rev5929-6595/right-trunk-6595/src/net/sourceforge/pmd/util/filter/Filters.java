package net.sourceforge.pmd.util.filter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class Filters {

	
	public static <T> List<T> filter(Filter<T> filter, Collection<T> collection) {
		List<T> list = new ArrayList<T>();
		for (T obj : collection) {
			if (filter.filter(obj)) {
				list.add(obj);
			}
		}
		return list;
	}

	
	public static Filter<File> getFileExtensionFilter(String... extensions) {
		return new FileExtensionFilter(extensions);
	}

	
	public static Filter<File> getDirectoryFilter() {
		return DirectoryFilter.INSTANCE;
	}

	
	public static Filter<File> getFileExtensionOrDirectoryFilter(String... extensions) {
		return new OrFilter<File>(getFileExtensionFilter(extensions), getDirectoryFilter());
	}

	
	public static Filter<File> toNormalizedFileFilter(final Filter<String> filter) {
		return new Filter<File>() {
			public boolean filter(File file) {
				String path = file.getPath();
				path = path.replace('\\', '/');
				return filter.filter(path);
			}

			public String toString() {
				return filter.toString();
			}
		};
	}

	
	public static <T> Filter<T> fromStringFilter(final Filter<String> filter) {
		return new Filter<T>() {
			public boolean filter(T obj) {
				return filter.filter(obj.toString());
			}

			public String toString() {
				return filter.toString();
			}
		};
	}

	
	public static FilenameFilter toFilenameFilter(final Filter<File> filter) {
		return new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return filter.filter(new File(dir, name));
			}

			public String toString() {
				return filter.toString();
			}
		};
	}

	
	public static Filter<File> toFileFilter(final FilenameFilter filter) {
		return new Filter<File>() {
			public boolean filter(File file) {
				return filter.accept(file.getParentFile(), file.getName());
			}

			public String toString() {
				return filter.toString();
			}
		};
	}

	
	public static Filter<String> buildRegexFilterExcludeOverInclude(List<String> includeRegexes,
			List<String> excludeRegexes) {
		OrFilter<String> includeFilter = new OrFilter<String>();
		if (includeRegexes == null || includeRegexes.isEmpty()) {
			includeFilter.addFilter(new RegexStringFilter(".*"));
		} else {
			for (String includeRegex : includeRegexes) {
				includeFilter.addFilter(new RegexStringFilter(includeRegex));
			}
		}

		OrFilter<String> excludeFilter = new OrFilter<String>();
		if (excludeRegexes != null) {
			for (String excludeRegex : excludeRegexes) {
				excludeFilter.addFilter(new RegexStringFilter(excludeRegex));
			}
		}

		return new AndFilter<String>(includeFilter, new NotFilter<String>(excludeFilter));
	}

	
	public static Filter<String> buildRegexFilterIncludeOverExclude(List<String> includeRegexes,
			List<String> excludeRegexes) {
		OrFilter<String> includeFilter = new OrFilter<String>();
		if (includeRegexes != null) {
			for (String includeRegex : includeRegexes) {
				includeFilter.addFilter(new RegexStringFilter(includeRegex));
			}
		}

		OrFilter<String> excludeFilter = new OrFilter<String>();
		if (excludeRegexes != null) {
			for (String excludeRegex : excludeRegexes) {
				excludeFilter.addFilter(new RegexStringFilter(excludeRegex));
			}
		}

		return new OrFilter<String>(includeFilter, new NotFilter<String>(excludeFilter));
	}
}
