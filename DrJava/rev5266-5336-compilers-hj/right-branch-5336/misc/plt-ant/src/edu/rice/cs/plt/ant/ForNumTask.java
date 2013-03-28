

package edu.rice.cs.plt.ant;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.MacroDef;
import org.apache.tools.ant.taskdefs.MacroInstance;
import org.apache.tools.ant.taskdefs.Parallel;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;


public class ForNumTask extends Task {

    private String     list;
    private Integer    count;
    private String     param;
    private String     delimiter = ",";
    private Path       currPath;
    private boolean    trim;
    private boolean    keepgoing = false;
    private MacroDef   macroDef;
    private List       hasIterators = new ArrayList();
    private boolean    parallel = false;
    private Integer    threadCount;
    private Parallel   parallelTasks;

    
    public ForNumTask() {
    }

    
    public void setParallel(boolean parallel) {
        this.parallel = parallel;
    }

    
    public void setThreadCount(int threadCount) {
        if (threadCount < 1) {
            throw new BuildException("Illegal value for threadCount " + threadCount
                                     + " it should be > 0");
        }
        this.threadCount = new Integer(threadCount);
    }

    
    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    
    public void setKeepgoing(boolean keepgoing) {
        this.keepgoing = keepgoing;
    }

    
    public void setList(String list) {
        this.list = list;
    }

    
    public void setCount(Integer count) {
        this.count = count;
    }

    
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    
    public void setParam(String param) {
        this.param = param;
    }

    private Path getOrCreatePath() {
        if (currPath == null) {
            currPath = new Path(getProject());
        }
        return currPath;
    }

    
    public void addConfigured(Path path) {
        getOrCreatePath().append(path);
    }

    
    public void addConfiguredPath(Path path) {
        addConfigured(path);
    }

    
    public Object createSequential() {
        macroDef = new MacroDef();
        macroDef.setProject(getProject());
        return macroDef.createSequential();
    }

    
    public void execute() {
        if (parallel) {
            parallelTasks = (Parallel) getProject().createTask("parallel");
            if (threadCount != null) {
                parallelTasks.setThreadCount(threadCount.intValue());
            }
        }
        if (list == null && count == null && currPath == null && hasIterators.size() == 0) {
            throw new BuildException(
                "You must have a list, count or path to iterate through");
        }
        if (param == null) {
            throw new BuildException(
                "You must supply a property name to set on"
                + " each iteration in param");
        }
        if (macroDef == null) {
            throw new BuildException(
                "You must supply an embedded sequential "
                + "to perform");
        }
        doTheTasks();
        if (parallel) {
            parallelTasks.perform();
        }
    }


    private void doSequentialIteration(String val) {
        MacroInstance instance = new MacroInstance();
        instance.setProject(getProject());
        instance.setOwningTarget(getOwningTarget());
        instance.setMacroDef(macroDef);
        instance.setDynamicAttribute(param.toLowerCase(),
                                     val);
        if (!parallel) {
            instance.execute();
        } else {
            parallelTasks.addTask(instance);
        }
    }

    private void doTheTasks() {
        int errorCount = 0;
        int taskCount = 0;

        
        if (macroDef.getAttributes().isEmpty()) {
         MacroDef.Attribute attribute = new MacroDef.Attribute();
         attribute.setName(param);
         macroDef.addConfiguredAttribute(attribute);
        }
        
        
        if (list != null) {
            StringTokenizer st = new StringTokenizer(list, delimiter);

            while (st.hasMoreTokens()) {
                String tok = st.nextToken();
                if (trim) {
                    tok = tok.trim();
                }
                try {
                    taskCount++;
                    doSequentialIteration(tok);
                } catch (BuildException bx) {
                    if (keepgoing) {
                        log(tok + ": " + bx.getMessage(), Project.MSG_ERR);
                        errorCount++;
                    } else {
                        throw bx;
                    }
                }
            }
        }
        if (keepgoing && (errorCount != 0)) {
            throw new BuildException(
                "Keepgoing execution: " + errorCount
                + " of " + taskCount + " iterations failed.");
        }
        
        
        if (count != null) {
   for (int i=0; i<count.intValue(); ++i) {
            try {
              taskCount++;
              doSequentialIteration(String.valueOf(i));
            } catch (BuildException bx) {
              if (keepgoing) {
                log(i + ": " + bx.getMessage(), Project.MSG_ERR);
                errorCount++;
              } else {
                throw bx;
              }
            }
          }
        }
        if (keepgoing && (errorCount != 0)) {
          throw new BuildException(
                                   "Keepgoing execution: " + errorCount
                                     + " of " + taskCount + " iterations failed.");
        }
        
        
        String[] pathElements = new String[0];
        if (currPath != null) {
            pathElements = currPath.list();
        }
        for (int i = 0; i < pathElements.length; i++) {
            File nextFile = new File(pathElements[i]);
            try {
                taskCount++;
                doSequentialIteration(nextFile.getAbsolutePath());
            } catch (BuildException bx) {
                if (keepgoing) {
                 log(nextFile + ": " + bx.getMessage(), Project.MSG_ERR);
                    errorCount++;
                } else {
                    throw bx;
                }
            }
        }
        if (keepgoing && (errorCount != 0)) {
            throw new BuildException(
                "Keepgoing execution: " + errorCount
                + " of " + taskCount + " iterations failed.");
        }

        
        for (Iterator i = hasIterators.iterator(); i.hasNext();) {
            Iterator it = ((HasIterator) i.next()).iterator();
            while (it.hasNext()) {
             String s = it.next().toString();
                try {
                    taskCount++;
                    doSequentialIteration(s);
                } catch (BuildException bx) {
                    if (keepgoing) {
                     log(s + ": " + bx.getMessage(), Project.MSG_ERR);
                        errorCount++;
                    } else {
                        throw bx;
                    }
                }
            }
        }
        if (keepgoing && (errorCount != 0)) {
            throw new BuildException(
                "Keepgoing execution: " + errorCount
                + " of " + taskCount + " iterations failed.");
        }
    }

    
    public void add(Map map) {
        hasIterators.add(new MapIterator(map));
    }

    
    public void add(FileSet fileset) {
        getOrCreatePath().addFileset(fileset);
    }

    
    public void addFileSet(FileSet fileset) {
        add(fileset);
    }

    
    public void add(DirSet dirset) {
        getOrCreatePath().addDirset(dirset);
    }

    
    public void addDirSet(DirSet dirset) {
        add(dirset);
    }

    
    public void add(Collection collection) {
        hasIterators.add(new ReflectIterator(collection));
    }

    
    public void add(Iterator iterator) {
        hasIterators.add(new IteratorIterator(iterator));
    }

    
    public void add(Object obj) {
        hasIterators.add(new ReflectIterator(obj));
    }

    
    private interface HasIterator {
        Iterator iterator();
    }

    private static class IteratorIterator implements HasIterator {
        private Iterator iterator;
        public IteratorIterator(Iterator iterator) {
            this.iterator = iterator;
        }
        public Iterator iterator() {
            return this.iterator;
        }
    }

    private static class MapIterator implements HasIterator {
        private Map map;
        public MapIterator(Map map) {
            this.map = map;
        }
        public Iterator iterator() {
            return map.values().iterator();
        }
    }

    private static class ReflectIterator implements HasIterator {
        private Object  obj;
        private Method  method;
        public ReflectIterator(Object obj) {
            this.obj = obj;
            try {
                method = obj.getClass().getMethod(
                    "iterator", new Class[] {});
            } catch (Throwable t) {
                throw new BuildException(
                    "Invalid type " + obj.getClass() + " used in ForNum task, it does"
                    + " not have a public iterator method");
            }
        }

        public Iterator iterator() {
            try {
                return (Iterator) method.invoke(obj, new Object[] {});
            } catch (Throwable t) {
                throw new BuildException(t);
            }
        }
    }
}
