package net.sourceforge.squirrel_sql.fw.sql;


import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.io.File;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Iterator;


public class MetaDataDecoratorDataSet extends MetaDataDataSet {

    
    private static final StringManager s_stringMgr =
       StringManagerFactory.getStringManager(MetaDataDecoratorDataSet.class);
    
    boolean finishedLocalRows = false;
    
    Iterator<Object[]> iter = null;
    ArrayList<Object[]> data = new ArrayList<Object[]>();
    
    Object[] currentRow = null;
    
    private static interface i18n {
        
        String CLASS_NAME_LABEL = 
            s_stringMgr.getString("MetaDataDecoratorDataSet.classNameLabel");
        
        String CLASS_PATH_LABEL = 
            s_stringMgr.getString("MetaDataDecoratorDataSet.classPathLabel");
        
        String NO_JAR_FILES = 
            s_stringMgr.getString("MetaDataDecoratorDataSet.noJarFiles");
    }
    
    
    public MetaDataDecoratorDataSet(DatabaseMetaData md, String driverClassName, String[] jarFileNames) 
    {
        super(md, null);
        Object[] className =
            new Object[] {i18n.CLASS_NAME_LABEL, driverClassName};
        
        data.add(className);
        String[] classPathFiles = jarFileNames;
        StringBuffer classPathBuffer = new StringBuffer();
        if (classPathFiles.length == 0) {
            classPathBuffer.append(i18n.NO_JAR_FILES);
        } else {
            for (int i = 0; i < classPathFiles.length; i++) {
                classPathBuffer.append(classPathFiles[i]);
                if (i+1 < classPathFiles.length) {
                    classPathBuffer.append(File.pathSeparator);
                }
            }            
        }
        Object[] classPath = 
            new Object[] {i18n.CLASS_PATH_LABEL,
                          classPathBuffer.toString()};
        data.add(classPath);
        iter = data.iterator();
    }

    
    public synchronized Object get(int columnIndex) {
        if (finishedLocalRows) {
            return super.get(columnIndex);
        } else {
            return currentRow[columnIndex];
        }
    }

    
    public synchronized boolean next(IMessageHandler msgHandler) {
        if (finishedLocalRows) {
            return super.next(msgHandler);
        } else {
            if (iter.hasNext()) {
                currentRow = iter.next();
                return true;
            } else {
                finishedLocalRows = true;
                return super.next(msgHandler);
            }
        }
    }
    
    
    
    
}
