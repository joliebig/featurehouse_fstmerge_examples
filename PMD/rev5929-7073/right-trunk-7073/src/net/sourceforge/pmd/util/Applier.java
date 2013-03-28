
package net.sourceforge.pmd.util;

import java.util.Iterator;

public class Applier {

    public static <E> void apply(UnaryFunction<E> f, Iterator<? extends E> i) {
        while (i.hasNext()) {
            f.applyTo(i.next());
        }
    }
}
