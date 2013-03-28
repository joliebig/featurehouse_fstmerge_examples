
package net.sourceforge.pmd;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.sourceforge.pmd.ast.CompilationUnit;
import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.cpd.SourceFileOrDirectoryFilter;
import net.sourceforge.pmd.parsers.Parser;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.sourcetypehandlers.SourceTypeHandler;
import net.sourceforge.pmd.sourcetypehandlers.SourceTypeHandlerBroker;
import net.sourceforge.pmd.util.Benchmark;
import net.sourceforge.pmd.util.ClasspathClassLoader;
import net.sourceforge.pmd.util.ConsoleLogHandler;
import net.sourceforge.pmd.util.FileFinder;

public class PMD {
    public static final String EOL = System.getProperty("line.separator", "\n");
    public static final String VERSION = "4.2.5-SNAPSHOT";
    public static final String EXCLUDE_MARKER = "NOPMD";

    private static final Logger LOG = Logger.getLogger(PMD.class.getName());

    private String excludeMarker = EXCLUDE_MARKER;
    private SourceTypeDiscoverer sourceTypeDiscoverer = new SourceTypeDiscoverer();
    private ClassLoader classLoader = getClass().getClassLoader();

    public PMD() {}

    
    public void processFile(Reader reader, RuleSets ruleSets, RuleContext ctx)
            throws PMDException {
        SourceType sourceType = getSourceTypeOfFile(ctx.getSourceCodeFilename());

        processFile(reader, ruleSets, ctx, sourceType);
    }

    
    public void processFile(Reader reader, RuleSets ruleSets, RuleContext ctx,
                            SourceType sourceType) throws PMDException {
        try {
        	
        	if (ruleSets.applies(ctx.getSourceCodeFile())) {
	            SourceTypeHandler sourceTypeHandler = SourceTypeHandlerBroker.getVisitorsFactoryForSourceType(sourceType);
	            ctx.setSourceType(sourceType);
	            Parser parser = sourceTypeHandler.getParser();
	            parser.setExcludeMarker(excludeMarker);
	            long start = System.nanoTime();
	            CompilationUnit rootNode = (CompilationUnit) parser.parse(reader);
	            ctx.excludeLines(parser.getExcludeMap());
	            long end = System.nanoTime();
	            Benchmark.mark(Benchmark.TYPE_PARSER, end - start, 0);
	            start = System.nanoTime();
	            sourceTypeHandler.getSymbolFacade().start(rootNode);
	            end = System.nanoTime();
	            Benchmark.mark(Benchmark.TYPE_SYMBOL_TABLE, end - start, 0);
	
	            Language language = SourceTypeToRuleLanguageMapper.getMappedLanguage(sourceType);
	
	            if (ruleSets.usesDFA(language)) {
	                start = System.nanoTime();
	                sourceTypeHandler.getDataFlowFacade().start(rootNode);
	                end = System.nanoTime();
	                Benchmark.mark(Benchmark.TYPE_DFA, end - start, 0);
	            }
	
	            if (ruleSets.usesTypeResolution(language)) {
	                start = System.nanoTime();
	                sourceTypeHandler.getTypeResolutionFacade(classLoader).start(rootNode);
	                end = System.nanoTime();
	                Benchmark.mark(Benchmark.TYPE_TYPE_RESOLUTION, end - start, 0);
	            }
	
	            List<CompilationUnit> acus = new ArrayList<CompilationUnit>();
	            acus.add(rootNode);
	
	            ruleSets.apply(acus, ctx, language);
        	}
        } catch (ParseException pe) {
            throw new PMDException("Error while parsing "
                    + ctx.getSourceCodeFilename(), pe);
        } catch (Exception e) {
            throw new PMDException("Error while processing "
                    + ctx.getSourceCodeFilename(), e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {}
        }
    }

    
    private SourceType getSourceTypeOfFile(String fileName) {
        SourceType sourceType = sourceTypeDiscoverer.getSourceTypeOfFile(fileName);
        if (sourceType == null) {
            
            
            sourceType = sourceTypeDiscoverer.getSourceTypeOfJavaFiles();
        }
        return sourceType;
    }

    
    public void processFile(Reader reader, RuleSet ruleSet, RuleContext ctx)
            throws PMDException {
        processFile(reader, new RuleSets(ruleSet), ctx);
    }

    
    public void processFile(InputStream fileContents, String encoding,
                            RuleSet ruleSet, RuleContext ctx) throws PMDException {
        try {
            processFile(new InputStreamReader(fileContents, encoding), ruleSet, ctx);
        } catch (UnsupportedEncodingException uee) {
            throw new PMDException("Unsupported encoding exception: "
                    + uee.getMessage());
        }
    }

    
    public void processFile(InputStream fileContents, String encoding,
                            RuleSets ruleSets, RuleContext ctx) throws PMDException {
        try {
            processFile(new InputStreamReader(fileContents, encoding), ruleSets, ctx);
        } catch (UnsupportedEncodingException uee) {
            throw new PMDException("Unsupported encoding exception: "
                    + uee.getMessage());
        }
    }

    
    public void processFile(InputStream fileContents, RuleSet ruleSet,
                            RuleContext ctx) throws PMDException {
        processFile(fileContents, System.getProperty("file.encoding"), ruleSet, ctx);
    }

    public void setExcludeMarker(String marker) {
        this.excludeMarker = marker;
    }

    
    public void setJavaVersion(SourceType javaVersion) {
        sourceTypeDiscoverer.setSourceTypeOfJavaFiles(javaVersion);
    }
    
    
    public ClassLoader getClassLoader() {
		return classLoader;
	}

    
	public void setClassLoader(ClassLoader classLoader) {
		if (classLoader == null) {
			classLoader = getClass().getClassLoader();
		}
		this.classLoader = classLoader;
	}
	
	
	public static ClassLoader createClasspathClassLoader(String classpath) throws IOException {
		ClassLoader classLoader = PMD.class.getClassLoader();
		if (classpath != null) {
			classLoader = new ClasspathClassLoader(classpath, classLoader);
		}
		return classLoader;
	}

	private static void doPMD(CommandLineOptions opts){
        long startFiles = System.nanoTime();
        SourceFileSelector fileSelector = new SourceFileSelector();

        fileSelector.setSelectJavaFiles(opts.isCheckJavaFiles());
        fileSelector.setSelectJspFiles(opts.isCheckJspFiles());

        List<DataSource> files;
        if (opts.containsCommaSeparatedFileList()) {
            files = collectFromCommaDelimitedString(opts.getInputPath(),
                    fileSelector);
        } else {
            files = collectFilesFromOneName(opts.getInputPath(), fileSelector);
        }
        long endFiles = System.nanoTime();
        Benchmark.mark(Benchmark.TYPE_COLLECT_FILES, endFiles - startFiles, 0);

        SourceType sourceType;
        if (opts.getTargetJDK().equals("1.3")) {
            LOG.fine("In JDK 1.3 mode");
            sourceType = SourceType.JAVA_13;
        } else if (opts.getTargetJDK().equals("1.5")) {
            LOG.fine("In JDK 1.5 mode");
            sourceType = SourceType.JAVA_15;
        } else if (opts.getTargetJDK().equals("1.6")) {
            LOG.fine("In JDK 1.6 mode");
            sourceType = SourceType.JAVA_16;
        } else if (opts.getTargetJDK().equals("1.7")) {
            LOG.fine("In JDK 1.7 mode");
            sourceType = SourceType.JAVA_17;
        } else {
            LOG.fine("In JDK 1.4 mode");
            sourceType = SourceType.JAVA_14;
        }

        final ClassLoader classLoader;
        try {
        	classLoader = createClasspathClassLoader(opts.getAuxClasspath());
        } catch (IOException e) {
        	LOG.log(Level.SEVERE, "Bad -auxclasspath argument", e);
        	System.out.println(opts.usage());        	
        	return;
        }
        
        long reportStart, reportEnd;
        Renderer renderer;
        Writer w = null;

        reportStart = System.nanoTime();
        try {
            renderer = opts.createRenderer();
            List<Renderer> renderers = new LinkedList<Renderer>();
            renderers.add(renderer);
            if (opts.getReportFile() != null) {
                w = new BufferedWriter(new FileWriter(opts.getReportFile()));
            } else {
                w = new OutputStreamWriter(System.out);
            }
            renderer.setWriter(w);
            renderer.start();

            reportEnd = System.nanoTime();
            Benchmark.mark(Benchmark.TYPE_REPORTING, reportEnd - reportStart, 0);
    
            RuleContext ctx = new RuleContext();

            try {
                long startLoadRules = System.nanoTime();
                RuleSetFactory ruleSetFactory = new RuleSetFactory();
                ruleSetFactory.setMinimumPriority(opts.getMinPriority());
    
                RuleSets rulesets = ruleSetFactory.createRuleSets(opts.getRulesets());
                printRuleNamesInDebug(rulesets);
                long endLoadRules = System.nanoTime();
                Benchmark.mark(Benchmark.TYPE_LOAD_RULES, endLoadRules - startLoadRules, 0);
    
                processFiles(opts.getCpus(), ruleSetFactory, sourceType, files, ctx, renderers,
                        opts.stressTestEnabled(),
                        opts.getRulesets(), opts.shortNamesEnabled(),
                        opts.getInputPath(), opts.getEncoding(), opts.getExcludeMarker(), classLoader);
            } catch (RuleSetNotFoundException rsnfe) {
                LOG.log(Level.SEVERE, "Ruleset not found", rsnfe);
                System.out.println(opts.usage());
            }

            reportStart = System.nanoTime();
            renderer.end();
            w.write(EOL);
            w.flush();
            if (opts.getReportFile() != null) {
                w.close();
                w = null;
            }
        } catch (Exception e) {
        	String message = e.getMessage();
        	if (message != null) {
        		LOG.severe(message);
        	} else {
                LOG.log(Level.SEVERE, "Exception during processing", e);
        	}
            
            LOG.log(Level.FINE, "Exception during processing", e);	
 
            LOG.info(opts.usage());
        } finally {
            if (opts.getReportFile() != null && w != null) {
                try {
                    w.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            reportEnd = System.nanoTime();
            Benchmark.mark(Benchmark.TYPE_REPORTING, reportEnd - reportStart, 0);
        }
    }

    public static void main(String[] args) {
        long start = System.nanoTime();
        final CommandLineOptions opts = new CommandLineOptions(args);

        final Level logLevel = opts.debugEnabled() ? Level.FINER : Level.INFO;
        final Handler logHandler = new ConsoleLogHandler();
        final ScopedLogHandlersManager logHandlerManager = new ScopedLogHandlersManager(logLevel, logHandler);
        final Level oldLogLevel = LOG.getLevel();
        LOG.setLevel(logLevel); 
        try{
            doPMD(opts);
        }finally{
            logHandlerManager.close();
            LOG.setLevel(oldLogLevel);
            if (opts.benchmark()) {
                long end = System.nanoTime();
                Benchmark.mark(Benchmark.TYPE_TOTAL_PMD, end - start, 0);
                System.err.println(Benchmark.report());
            }
        }
    }

    private static class PmdRunnable extends PMD implements Callable<Report> {
        private final ExecutorService executor;
        private final DataSource dataSource;
        private final String fileName;
        private final String encoding;
        private final String rulesets;
        private final List<Renderer> renderers;

        public PmdRunnable(ExecutorService executor, DataSource dataSource, String fileName, SourceType sourceType,
                List<Renderer> renderers, String encoding, String rulesets, String excludeMarker, ClassLoader classLoader) {
            this.executor = executor;
            this.dataSource = dataSource;
            this.fileName = fileName;
            this.encoding = encoding;
            this.rulesets = rulesets;
            this.renderers = renderers;

            setJavaVersion(sourceType);
            setExcludeMarker(excludeMarker);
            setClassLoader(classLoader);
        }

        public Report call() {
            PmdThread thread = (PmdThread) Thread.currentThread();

            RuleContext ctx = thread.getRuleContext();
            RuleSets rs = thread.getRuleSets(rulesets);

            Report report = new Report();
            ctx.setReport(report);

            ctx.setSourceCodeFilename(fileName);
            ctx.setSourceCodeFile(new File(fileName));
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Processing " + ctx.getSourceCodeFilename());
            }
            for(Renderer r: renderers) {
                r.startFileAnalysis(dataSource);
            }

            try {
                InputStream stream = new BufferedInputStream(dataSource.getInputStream());
                processFile(stream, encoding, rs, ctx);
            } catch (PMDException pmde) {
                LOG.log(Level.FINE, "Error while processing file", pmde.getCause());

                report.addError(
                        new Report.ProcessingError(pmde.getMessage(),
                        fileName));
            } catch (IOException ioe) {
                
                LOG.log(Level.FINE, "IOException during processing", ioe);

                report.addError(
                        new Report.ProcessingError(ioe.getMessage(),
                        fileName));

                executor.shutdownNow();
            } catch (RuntimeException re) {
                
                LOG.log(Level.FINE, "RuntimeException during processing", re);

                report.addError(
                        new Report.ProcessingError(re.getMessage(),
                        fileName));

                executor.shutdownNow();
            }
            return report;
        }

    }

    private static class PmdThreadFactory implements ThreadFactory {

        private final RuleSetFactory ruleSetFactory;
        private final RuleContext ctx;
        private final AtomicInteger counter = new AtomicInteger();

        public PmdThreadFactory(RuleSetFactory ruleSetFactory, RuleContext ctx) {
            this.ruleSetFactory = ruleSetFactory;
            this.ctx = ctx;
        }

        public Thread newThread(Runnable r) {
            PmdThread t = new PmdThread(counter.incrementAndGet(), r, ruleSetFactory, ctx);
            threadList.add(t);
            return t;
        }

        public List<PmdThread> threadList = Collections.synchronizedList(new LinkedList<PmdThread>());

    }

    private static class PmdThread extends Thread {

        public PmdThread(int id, Runnable r, RuleSetFactory ruleSetFactory, RuleContext ctx) {
            super(r, "PmdThread " + id);
            this.id = id;
            context = new RuleContext(ctx);
            this.ruleSetFactory = ruleSetFactory;
        }
        
        private int id;
        private RuleContext context;
        private RuleSets rulesets;
        private RuleSetFactory ruleSetFactory;
        
        public RuleContext getRuleContext() {
            return context;
        }

        public RuleSets getRuleSets(String rsList) {
            if (rulesets == null) {
                try {
                    rulesets = ruleSetFactory.createRuleSets(rsList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return rulesets;
        }

        public String toString() {
            return "PmdThread " + id;
        }

    }

    
    private static final boolean mtSupported;
    
    static {
        boolean error = false;
        try {
            
            ExecutorService executor = Executors.newFixedThreadPool(1);
            executor.shutdown();
        } catch (RuntimeException e) {
            error = true;
        }
        mtSupported = !error;
    }

    
    public static void processFiles(int threadCount, RuleSetFactory ruleSetFactory, SourceType sourceType, List<DataSource> files, RuleContext ctx,
            List<Renderer> renderers, String rulesets, final boolean shortNamesEnabled, final String inputPath,
            String encoding, String excludeMarker, ClassLoader classLoader) {
        processFiles(threadCount, ruleSetFactory, sourceType, files, ctx,
                renderers, false, rulesets, shortNamesEnabled, inputPath,
                encoding, excludeMarker, classLoader);
    }
    
    public static void processFiles(int threadCount, RuleSetFactory ruleSetFactory, SourceType sourceType, List<DataSource> files, RuleContext ctx,
            List<Renderer> renderers, boolean stressTestEnabled, String rulesets, final boolean shortNamesEnabled, final String inputPath,
            String encoding, String excludeMarker, ClassLoader classLoader) {

        
        boolean useMT = mtSupported && (threadCount > 0);

        if (stressTestEnabled) {
            
            Collections.shuffle(files);
        } else {
            Collections.sort(files, new Comparator<DataSource>() {
                public int compare(DataSource d1,DataSource d2) {
                    String s1 = d1.getNiceFileName(shortNamesEnabled, inputPath);
                    String s2 = d2.getNiceFileName(shortNamesEnabled, inputPath);
                    return s1.compareTo(s2);
                }
            });
        }

        if (useMT) {
            RuleSets rs = null;
            try {
                rs = ruleSetFactory.createRuleSets(rulesets);
            } catch (RuleSetNotFoundException rsnfe) {
                
            }
            rs.start(ctx);

            PmdThreadFactory factory = new PmdThreadFactory(ruleSetFactory, ctx);
            ExecutorService executor = Executors.newFixedThreadPool(threadCount, factory);
            List<Future<Report>> tasks = new LinkedList<Future<Report>>();

            for (DataSource dataSource:files) {
                String niceFileName = dataSource.getNiceFileName(shortNamesEnabled,
                        inputPath);
    
                PmdRunnable r = new PmdRunnable(executor, dataSource, niceFileName, sourceType, renderers,
                                                encoding, rulesets, excludeMarker, classLoader);
    
                Future<Report> future = executor.submit(r);
                tasks.add(future);
            }
            executor.shutdown();
    
            while (!tasks.isEmpty()) {
                Future<Report> future = tasks.remove(0);
                Report report = null;
                try {
                    report = future.get();
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    future.cancel(true);
                } catch (ExecutionException ee) {
                    Throwable t = ee.getCause();
                    if (t instanceof RuntimeException) {
                        throw (RuntimeException) t;
                    } else if (t instanceof Error) {
                        throw (Error) t;
                    } else {
                        throw new IllegalStateException("PmdRunnable exception", t);
                    }
                }
    
                try {
                    long start = System.nanoTime();
                    for (Renderer r: renderers) {
                        r.renderFileReport(report);
                    }
                    long end = System.nanoTime();
                    Benchmark.mark(Benchmark.TYPE_REPORTING, end - start, 1);
                } catch (IOException ioe) {
                }
            }

            try {
                rs.end(ctx);
                long start = System.nanoTime();
                for (Renderer r: renderers) {
                    r.renderFileReport(ctx.getReport());
                }
                long end = System.nanoTime();
                Benchmark.mark(Benchmark.TYPE_REPORTING, end - start, 1);
            } catch (IOException ioe) {
            }


        } else {
            

            PMD pmd = new PMD();
            pmd.setJavaVersion(sourceType);
            pmd.setExcludeMarker(excludeMarker);

            RuleSets rs = null;
            try {
                rs = ruleSetFactory.createRuleSets(rulesets);
            } catch (RuleSetNotFoundException rsnfe) {
                
            }
            for (DataSource dataSource: files) {
                String niceFileName = dataSource.getNiceFileName(shortNamesEnabled,
                        inputPath);

                Report report = new Report();
                ctx.setReport(report);

                ctx.setSourceCodeFilename(niceFileName);
                ctx.setSourceCodeFile(new File(niceFileName));
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Processing " + ctx.getSourceCodeFilename());
                }
                rs.start(ctx);

                for(Renderer r: renderers) {
                    r.startFileAnalysis(dataSource);
                }

                try {
                    InputStream stream = new BufferedInputStream(dataSource.getInputStream());
                    pmd.processFile(stream, encoding, rs, ctx);
                } catch (PMDException pmde) {
                    LOG.log(Level.FINE, "Error while processing file", pmde.getCause());

                    report.addError(
                            new Report.ProcessingError(pmde.getMessage(),
                                    niceFileName));
                } catch (IOException ioe) {
                    
                    LOG.log(Level.FINE, "Unable to read source file", ioe);

                    report.addError(
                            new Report.ProcessingError(ioe.getMessage(),
                                    niceFileName));
                } catch (RuntimeException re) {
                    
                    LOG.log(Level.FINE, "RuntimeException while processing file", re);
                    
                    report.addError(
                            new Report.ProcessingError(re.getMessage(),
                                    niceFileName));
                }

                rs.end(ctx);

                try {
                    long start = System.nanoTime();
                    for (Renderer r: renderers) {
                        r.renderFileReport(report);
                    }
                    long end = System.nanoTime();
                    Benchmark.mark(Benchmark.TYPE_REPORTING, end - start, 1);
                } catch (IOException ioe) {
                }
            }
        }
    }

    
    public void processFiles(List<DataSource> files, RuleContext ctx, RuleSets rulesets,
                             boolean debugEnabled, boolean shortNamesEnabled, String inputPath,
                             String encoding) throws IOException {
        for (DataSource dataSource: files) {
            String niceFileName = dataSource.getNiceFileName(shortNamesEnabled,
                    inputPath);
            ctx.setSourceCodeFilename(niceFileName);
            ctx.setSourceCodeFile(new File(niceFileName));
            LOG.fine("Processing " + ctx.getSourceCodeFilename());
            
            try {
                InputStream stream = new BufferedInputStream(dataSource
                        .getInputStream());
                processFile(stream, encoding, rulesets, ctx);
            } catch (PMDException pmde) {
                LOG.log(Level.FINE, "Error while processing files", pmde.getCause());

                ctx.getReport().addError(new Report.ProcessingError(pmde.getMessage(), niceFileName));
            }
        }
    }

    
    private static void printRuleNamesInDebug(RuleSets rulesets) {
        if (LOG.isLoggable(Level.FINER)) {
            for (Rule r: rulesets.getAllRules()) {
                LOG.finer("Loaded rule " + r.getName());
            }
        }
    }

    
    private static List<DataSource> collectFilesFromOneName(String inputFileName,
                                                SourceFileSelector fileSelector) {
        return collect(inputFileName, fileSelector);
    }

    
    private static List<DataSource> collectFromCommaDelimitedString(String fileList,
                                                        SourceFileSelector fileSelector) {
        List<DataSource> files = new ArrayList<DataSource>();
        for (StringTokenizer st = new StringTokenizer(fileList, ","); st
                .hasMoreTokens();) {
            files.addAll(collect(st.nextToken(), fileSelector));
        }
        return files;
    }

    
    private static List<DataSource> collect(String filename, SourceFileSelector fileSelector) {
        File inputFile = new File(filename);
        if (!inputFile.exists()) {
            throw new RuntimeException("File " + inputFile.getName()
                    + " doesn't exist");
        }
        List<DataSource> dataSources = new ArrayList<DataSource>();
        if (!inputFile.isDirectory()) {
            if (filename.endsWith(".zip") || filename.endsWith(".jar")) {
                ZipFile zipFile;
                try {
                    zipFile = new ZipFile(inputFile);
                    Enumeration e = zipFile.entries();
                    while (e.hasMoreElements()) {
                        ZipEntry zipEntry = (ZipEntry) e.nextElement();
                        if (fileSelector.isWantedFile(zipEntry.getName())) {
                            dataSources.add(new ZipDataSource(zipFile, zipEntry));
                        }
                    }
                } catch (IOException ze) {
                    throw new RuntimeException("Zip file " + inputFile.getName()
                            + " can't be opened");
                }
            } else {
                dataSources.add(new FileDataSource(inputFile));
            }
        } else {
            FileFinder finder = new FileFinder();
            List<File> files = finder.findFilesFrom(inputFile.getAbsolutePath(),
                    new SourceFileOrDirectoryFilter(fileSelector), true);
            for (File f: files) {
                dataSources.add(new FileDataSource(f));
            }
        }
        return dataSources;
    }

}
