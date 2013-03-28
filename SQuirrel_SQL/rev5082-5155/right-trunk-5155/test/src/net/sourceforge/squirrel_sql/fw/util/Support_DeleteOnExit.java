

package net.sourceforge.squirrel_sql.fw.util;


public class Support_DeleteOnExit {

	public static void main(java.lang.String[] args) {
        for (int i = 0; i < args.length; i++) {
            FileWrapperImpl f1 = new FileWrapperImpl(args[i]);
            f1.deleteOnExit();
        }
    }
}
