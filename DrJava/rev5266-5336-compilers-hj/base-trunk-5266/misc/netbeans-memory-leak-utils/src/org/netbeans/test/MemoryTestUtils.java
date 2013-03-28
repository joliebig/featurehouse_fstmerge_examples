
package org.netbeans.test;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.netbeans.insane.live.LiveReferences;
import org.netbeans.insane.live.Path;
import org.netbeans.insane.scanner.CountingVisitor;
import org.netbeans.insane.scanner.ScannerUtils;


public class MemoryTestUtils {
    
    public static void assertGC(String text, Reference<?> ref) {
        assertGC(text, ref, Collections.emptySet());
    }

    
    public static void assertGC(String text, Reference<?> ref, Set<?> rootsHint) {
        List<byte[]> alloc = new ArrayList<byte[]>();
        int size = 100000;
        for (int i = 0; i < 50; i++) {
            if (ref.get() == null) {
                return;
            }
            try {
                System.gc();
            } catch (OutOfMemoryError error) {
                
            }
            try {
                System.runFinalization();
            } catch (OutOfMemoryError error) {
                
            }
            try {
                alloc.add(new byte[size]);
                size = (int) (((double) size) * 1.3);
            } catch (OutOfMemoryError error) {
                size = size / 2;
            }
            try {
                if (i % 3 == 0) {
                    Thread.sleep(321);
                }
            } catch (InterruptedException t) {
                
            }
        }
        alloc = null;
        String str = null;
        try {
            str = findRefsFromRoot(ref.get(), rootsHint);
        } catch (Exception e) {
            throw new AssertionFailedErrorException(e);
        } catch (OutOfMemoryError err) {
            
        }
        TestCase.fail(text + ":\n" + str);
    }

    
    public static void assertSize(String message, int limit, Object root) {
        assertSize(message, Arrays.asList(new Object[] {root}), limit);
    }

    
    public static void assertSize(String message, Collection<?> roots, int limit) {
        assertSize(message, roots, limit, new Object[0]);
    }

    
    public static void assertSize(String message, Collection<?> roots, int limit, Object[] skip) {
        org.netbeans.insane.scanner.Filter f = ScannerUtils.skipObjectsFilter(Arrays.asList(skip), false);
        assertSize(message, roots, limit, f);
    }

    
    public static int assertSize(String message, Collection<?> roots, int limit, final MemoryFilter skip) {
        org.netbeans.insane.scanner.Filter f = new org.netbeans.insane.scanner.Filter() {
            public boolean accept(Object o, Object refFrom, Field ref) {
                return !skip.reject(o);
            }
        };
        return assertSize(message, roots, limit, f);
    }

    private static int assertSize(String message, Collection<?> roots, int limit,
            org.netbeans.insane.scanner.Filter f) {
        try {
            CountingVisitor counter = new CountingVisitor();
            ScannerUtils.scan(f, counter, roots, false);
            int sum = counter.getTotalSize();
            if (sum > limit) {
                StringBuilder sb = new StringBuilder(4096);
                sb.append(message);
                sb.append(": leak ").append(sum - limit).append(" bytes ");
                sb.append(" over limit of ");
                sb.append(limit + " bytes");
                sb.append('\n');
                for (Iterator it = counter.getClasses().iterator(); it.hasNext();) {
                    sb.append("  ");
                    Class cls = (Class) it.next();
                    if (counter.getCountForClass(cls) == 0) {
                        continue;
                    }
                    sb.append(cls.getName()).append(": ").
                            append(counter.getCountForClass(cls)).append(", ").
                            append(counter.getSizeForClass(cls)).append("B\n");
                }
                TestCase.fail(sb.toString());
            }
            return sum;
        } catch (Exception e) {
            throw new AssertionFailedErrorException("Could not traverse reference graph", e);
        }
    }

    private static String findRefsFromRoot(final Object target, final Set<?> rootsHint) throws Exception {
        int count = Integer.getInteger("assertgc.paths", 1);
        StringBuilder sb = new StringBuilder();
        final Map<Object, Boolean> skip = new IdentityHashMap<Object, Boolean>();

        org.netbeans.insane.scanner.Filter knownPath = new org.netbeans.insane.scanner.Filter() {
            public boolean accept(Object obj, Object referredFrom, Field reference) {
                return !skip.containsKey(obj);
            }
        };

        while (count-- > 0) {
            @SuppressWarnings("unchecked")
            Map m = LiveReferences.fromRoots(Collections.singleton(target), (Set<Object>) rootsHint, null, knownPath);
            Path p = (Path) m.get(target);
            if (p == null) {
                break;
            }
            if (sb.length() > 0) {
                sb.append("\n\n");
            }

            sb.append(p.toString());
            for (; p != null; p = p.nextNode()) {
                Object o = p.getObject();
                if (o != target) {
                    skip.put(o, Boolean.TRUE);
                }
            }
        }
        return sb.length() > 0 ? sb.toString() : "Not found!!!";
    }

    
    public static class AssertionFailedErrorException extends AssertionFailedError {
        
        protected Throwable nestedException;

        
        public AssertionFailedErrorException(Throwable nestedException) {
            this(null, nestedException);
        }

        
        public AssertionFailedErrorException(String message, Throwable nestedException) {
            super(message);
            this.nestedException = nestedException;
        }

        
        public void printStackTrace() {
            printStackTrace(System.err);
        }

        
        public void printStackTrace(PrintWriter err) {
            synchronized (err) {
                super.printStackTrace(err);
                err.println("\nNested Exception is:");
                nestedException.printStackTrace(err);
            }
        }

        
        public void printStackTrace(PrintStream err) {
            synchronized (err) {
                super.printStackTrace(err);
                err.println("\nNested Exception is:");
                nestedException.printStackTrace(err);
            }
        }
    }

    
    public interface MemoryFilter {
        
        boolean reject(Object obj);
    }

    private MemoryTestUtils() {
    }
}
