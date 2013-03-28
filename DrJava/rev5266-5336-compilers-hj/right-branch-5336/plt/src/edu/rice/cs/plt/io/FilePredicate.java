

package edu.rice.cs.plt.io;

import java.io.FileFilter;
import java.io.File;
import edu.rice.cs.plt.lambda.Predicate;


public interface FilePredicate extends FileFilter, Predicate<File> {
}
