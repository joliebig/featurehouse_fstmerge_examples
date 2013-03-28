
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
import java.util.LinkedList;
import java.util.List;
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

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageFilenameFilter;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ParseException;
import net.sourceforge.pmd.lang.xpath.Initializer;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.Benchmark;
import net.sourceforge.pmd.util.ClasspathClassLoader;
import net.sourceforge.pmd.util.FileUtil;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.log.ConsoleLogHandler;
import net.sourceforge.pmd.util.log.ScopedLogHandlersManager;

public class PMD {
    public static final String EOL = System.getProperty("line.separator", "\n");
    public static final String VERSION = "5.0-SNAPSHOT";
    public static final String SUPPRESS_MARKER = "NOPMD";

    private static final Logger LOG = Logger.getLogger(PMD.class.getName());

    private Configuration configuration = new Configuration();

    
    public Configuration getConfiguration() {
	return configuration;
    }

    
    public void setConfiguration(Configuration configuration) {
	this.configuration = configuration;
    }

    
    public void processFile(InputStream inputStream, String encoding, RuleSets ruleSets, RuleContext ctx)
	    throws PMDException {
	try {
	    if (encoding == null) {
		encoding = System.getProperty("file.encoding");
	    }
	    processFile(new InputStreamReader(inputStream, encoding), ruleSets, ctx);
	} catch (UnsupportedEncodingException uee) {
	    throw new PMDException("Unsupported encoding exception: " + uee.getMessage());
	}
    }

    
    public void processFile(Reader reader, RuleSets ruleSets, RuleContext ctx) throws PMDException {
	
	if (ctx.getLanguageVersion() == null) {
	    LanguageVersion languageVersion = configuration.getLanguageVersionOfFile(ctx.getSourceCodeFilename());
	    ctx.setLanguageVersion(languageVersion);
	}

        
        Initializer.initialize();

	try {
	    
	    if (ruleSets.applies(ctx.getSourceCodeFile())) {
		LanguageVersion languageVersion = ctx.getLanguageVersion();
		LanguageVersionHandler languageVersionHandler = languageVersion.getLanguageVersionHandler();
		Parser parser = languageVersionHandler.getParser();
		parser.setSuppressMarker(configuration.getSuppressMarker());
		long start = System.nanoTime();
		Node rootNode = parser.parse(ctx.getSourceCodeFilename(), reader);
		ctx.getReport().suppress(parser.getSuppressMap());
		long end = System.nanoTime();
		Benchmark.mark(Benchmark.TYPE_PARSER, end - start, 0);
		start = System.nanoTime();
		languageVersionHandler.getSymbolFacade().start(rootNode);
		end = System.nanoTime();
		Benchmark.mark(Benchmark.TYPE_SYMBOL_TABLE, end - start, 0);

		Language language = languageVersion.getLanguage();

		if (ruleSets.usesDFA(language)) {
		    start = System.nanoTime();
		    languageVersionHandler.getDataFlowFacade().start(rootNode);
		    end = System.nanoTime();
		    Benchmark.mark(Benchmark.TYPE_DFA, end - start, 0);
		}

		if (ruleSets.usesTypeResolution(language)) {
		    start = System.nanoTime();
		    languageVersionHandler.getTypeResolutionFacade(configuration.getClassLoader()).start(rootNode);
		    end = System.nanoTime();
		    Benchmark.mark(Benchmark.TYPE_TYPE_RESOLUTION, end - start, 0);
		}

		List<Node> acus = new ArrayList<Node>();
		acus.add(rootNode);

		ruleSets.apply(acus, ctx, language);
	    }
	} catch (ParseException pe) {
	    throw new PMDException("Error while parsing " + ctx.getSourceCodeFilename(), pe);
	} catch (Exception e) {
	    throw new PMDException("Error while processing " + ctx.getSourceCodeFilename(), e);
	} finally {
	    try {
		reader.close();
	    } catch (IOException e) {
	    }
	}
    }

    
    public static ClassLoader createClasspathClassLoader(String classpath) throws IOException {
	ClassLoader classLoader = PMD.class.getClassLoader();
	if (classpath != null) {
	    classLoader = new ClasspathClassLoader(classpath, classLoader);
	}
	return classLoader;
    }

    private static void doPMD(CommandLineOptions opts) {
	long startFiles = System.nanoTime();
	
	List<LanguageVersion> languageVersions = new ArrayList<LanguageVersion>();
	Language language = opts.getLanguage();
	LanguageVersion languageVersion = opts.getVersion();
	if ( language == null )
	{
	    language = Language.getDefaultLanguage();
	    languageVersion = language.getDefaultVersion();
	}
	languageVersions.add(languageVersion);
	LOG.fine("Using " + languageVersion.getShortName());

	
	LanguageFilenameFilter fileSelector = new LanguageFilenameFilter(opts.getLanguage());
	List<DataSource> files = FileUtil.collectFiles(opts.getInputPath(), fileSelector);
	long endFiles = System.nanoTime();
	Benchmark.mark(Benchmark.TYPE_COLLECT_FILES, endFiles - startFiles, 0);

	
	final ClassLoader classLoader;
	try {
	    classLoader = createClasspathClassLoader(opts.getAuxClasspath());
	} catch (IOException e) {
	    LOG.log(Level.SEVERE, "Bad -auxclasspath argument", e);
	    System.out.println(opts.usage());
	    return;
	}

	long reportStart;
	long reportEnd;
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

		ruleSetFactory.setWarnDeprecated(true);
		RuleSets rulesets = ruleSetFactory.createRuleSets(opts.getRulesets());
		ruleSetFactory.setWarnDeprecated(false);
		printRuleNamesInDebug(rulesets);
		long endLoadRules = System.nanoTime();
		Benchmark.mark(Benchmark.TYPE_LOAD_RULES, endLoadRules - startLoadRules, 0);

		processFiles(opts.getCpus(), ruleSetFactory, languageVersions, files, ctx, renderers, opts
			.stressTestEnabled(), opts.getRulesets(), opts.shortNamesEnabled(), opts.getInputPath(), opts
			.getEncoding(), opts.getSuppressMarker(), classLoader);
	    } catch (RuleSetNotFoundException rsnfe) {
		LOG.log(Level.SEVERE, "Ruleset not found", rsnfe);
		System.out.println(opts.usage());
	    }

	    reportStart = System.nanoTime();
	    renderer.end();
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
	try {
	    doPMD(opts);
	} finally {
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

	public PmdRunnable(ExecutorService executor, DataSource dataSource, String fileName,
		List<LanguageVersion> languageVersions, List<Renderer> renderers, String encoding, String rulesets,
		String suppressMarker, ClassLoader classLoader) {
	    this.executor = executor;
	    this.dataSource = dataSource;
	    this.fileName = fileName;
	    this.encoding = encoding;
	    this.rulesets = rulesets;
	    this.renderers = renderers;

	    getConfiguration().setDefaultLanguageVersions(languageVersions);
	    getConfiguration().setSuppressMarker(suppressMarker);
	    getConfiguration().setClassLoader(classLoader);
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
	    for (Renderer r : renderers) {
		r.startFileAnalysis(dataSource);
	    }

	    try {
		InputStream stream = new BufferedInputStream(dataSource.getInputStream());
		processFile(stream, encoding, rs, ctx);
	    } catch (PMDException pmde) {
		LOG.log(Level.FINE, "Error while processing file", pmde.getCause());

		report.addError(new Report.ProcessingError(pmde.getMessage(), fileName));
	    } catch (IOException ioe) {
		
		LOG.log(Level.FINE, "IOException during processing", ioe);

		report.addError(new Report.ProcessingError(ioe.getMessage(), fileName));

		executor.shutdownNow();
	    } catch (RuntimeException re) {
		
		LOG.log(Level.FINE, "RuntimeException during processing", re);

		report.addError(new Report.ProcessingError(re.getMessage(), fileName));

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

	@Override
	public String toString() {
	    return "PmdThread " + id;
	}

    }

    
    private static final boolean MT_SUPPORTED;

    static {
	boolean error = false;
	try {
	    
	    ExecutorService executor = Executors.newFixedThreadPool(1);
	    executor.shutdown();
	} catch (RuntimeException e) {
	    error = true;
	}
	MT_SUPPORTED = !error;
    }

    
    public static void processFiles(int threadCount, RuleSetFactory ruleSetFactory,
	    List<LanguageVersion> languageVersions, List<DataSource> files, RuleContext ctx, List<Renderer> renderers,
	    String rulesets, final boolean shortNamesEnabled, final String inputPath, String encoding,
	    String suppressMarker, ClassLoader classLoader) {
	processFiles(threadCount, ruleSetFactory, languageVersions, files, ctx, renderers, false, rulesets,
		shortNamesEnabled, inputPath, encoding, suppressMarker, classLoader);
    }

    
    public static void processFiles(int threadCount, RuleSetFactory ruleSetFactory,
	    List<LanguageVersion> languageVersions, List<DataSource> files, RuleContext ctx, List<Renderer> renderers,
	    boolean stressTestEnabled, String rulesets, final boolean shortNamesEnabled, final String inputPath,
	    String encoding, String suppressMarker, ClassLoader classLoader) {

	
	boolean useMT = MT_SUPPORTED && threadCount > 0;

	if (stressTestEnabled) {
	    
	    Collections.shuffle(files);
	} else {
	    Collections.sort(files, new Comparator<DataSource>() {
		public int compare(DataSource d1, DataSource d2) {
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

	    for (DataSource dataSource : files) {
		String niceFileName = dataSource.getNiceFileName(shortNamesEnabled, inputPath);

		PmdRunnable r = new PmdRunnable(executor, dataSource, niceFileName, languageVersions, renderers,
			encoding, rulesets, suppressMarker, classLoader);

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
		    for (Renderer r : renderers) {
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
		for (Renderer r : renderers) {
		    r.renderFileReport(ctx.getReport());
		}
		long end = System.nanoTime();
		Benchmark.mark(Benchmark.TYPE_REPORTING, end - start, 1);
	    } catch (IOException ioe) {
	    }

	} else {
	    

	    PMD pmd = new PMD();
	    pmd.getConfiguration().setDefaultLanguageVersions(languageVersions);
	    pmd.getConfiguration().setSuppressMarker(suppressMarker);

	    RuleSets rs = null;
	    try {
		rs = ruleSetFactory.createRuleSets(rulesets);
	    } catch (RuleSetNotFoundException rsnfe) {
		
	    }
	    for (DataSource dataSource : files) {
		String niceFileName = dataSource.getNiceFileName(shortNamesEnabled, inputPath);

		Report report = new Report();
		ctx.setReport(report);

		ctx.setSourceCodeFilename(niceFileName);
		ctx.setSourceCodeFile(new File(niceFileName));
		if (LOG.isLoggable(Level.FINE)) {
		    LOG.fine("Processing " + ctx.getSourceCodeFilename());
		}
		rs.start(ctx);

		for (Renderer r : renderers) {
		    r.startFileAnalysis(dataSource);
		}

		try {
		    InputStream stream = new BufferedInputStream(dataSource.getInputStream());
		    pmd.processFile(stream, encoding, rs, ctx);
		} catch (PMDException pmde) {
		    LOG.log(Level.FINE, "Error while processing file", pmde.getCause());

		    report.addError(new Report.ProcessingError(pmde.getMessage(), niceFileName));
		} catch (IOException ioe) {
		    
		    LOG.log(Level.FINE, "Unable to read source file", ioe);

		    report.addError(new Report.ProcessingError(ioe.getMessage(), niceFileName));
		} catch (RuntimeException re) {
		    
		    LOG.log(Level.FINE, "RuntimeException while processing file", re);

		    report.addError(new Report.ProcessingError(re.getMessage(), niceFileName));
		}

		rs.end(ctx);

		try {
		    long start = System.nanoTime();
		    for (Renderer r : renderers) {
			r.renderFileReport(report);
		    }
		    long end = System.nanoTime();
		    Benchmark.mark(Benchmark.TYPE_REPORTING, end - start, 1);
		} catch (IOException ioe) {
		}
	    }
	}
    }

    
    public void processFiles(List<DataSource> files, RuleContext ctx, RuleSets rulesets, boolean debugEnabled,
	    boolean shortNamesEnabled, String inputPath, String encoding) throws IOException {
	for (DataSource dataSource : files) {
	    String niceFileName = dataSource.getNiceFileName(shortNamesEnabled, inputPath);
	    ctx.setSourceCodeFilename(niceFileName);
	    ctx.setSourceCodeFile(new File(niceFileName));
	    LOG.fine("Processing " + ctx.getSourceCodeFilename());

	    try {
		InputStream stream = new BufferedInputStream(dataSource.getInputStream());
		processFile(stream, encoding, rulesets, ctx);
	    } catch (PMDException pmde) {
		LOG.log(Level.FINE, "Error while processing files", pmde.getCause());

		ctx.getReport().addError(new Report.ProcessingError(pmde.getMessage(), niceFileName));
	    }
	}
    }

    
    private static void printRuleNamesInDebug(RuleSets rulesets) {
	if (LOG.isLoggable(Level.FINER)) {
	    for (Rule r : rulesets.getAllRules()) {
		LOG.finer("Loaded rule " + r.getName());
	    }
	}
    }
}
