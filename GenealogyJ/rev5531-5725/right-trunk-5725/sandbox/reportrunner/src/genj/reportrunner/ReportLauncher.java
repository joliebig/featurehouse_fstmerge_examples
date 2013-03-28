package genj.reportrunner;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.io.GedcomReader;
import genj.report.Report;
import genj.report.ReportLoader;
import genj.util.Origin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;


public class ReportLauncher
{
    public static final String INDIVIDUAL_OPTION = "individual";
    public static final String FORMAT_OPTION = "format";
    public static final String OUTPUT_OPTION = "output";
    public static final String OUTPUT_DIR_OPTION = "output-dir";
    public static final String REPORT_OPTION = "report";
    public static final String GEDCOM_OPTION = "gedcom";

    
    private ReportProxyFactory proxyFactory = new ReportProxyFactory();

    
    private Map<String, Report> reportsByName = new HashMap<String, Report>();

    
    private Map<String, ReportProxy> proxiesByName = new HashMap<String, ReportProxy>();

    
    private Gedcom gedcom = null;


    
    private String gedcomFile = null;

    
    public ReportLauncher()
    {
        
        ReportLoader reportLoader = ReportLoader.getInstance();
        Report[] reports = reportLoader.getReports();
        for (int i = 0; i < reports.length; i++)
            reportsByName.put(reports[i].getClass().getCanonicalName(), reports[i]);
    }

    
    public void runReport(Map<String, String> options) throws ReportRunnerException, IOException
    {
        String reportName = options.get(REPORT_OPTION);
        ReportRunner.LOG.info("Running report: " + reportName);
        if (reportName == null)
        	throw new ReportRunnerException("Report name not supplied");

        
        ReportProxy proxy = getProxy(reportName);

        
        proxy.resetOptions();
        for (Map.Entry<String, String> entry : options.entrySet())
        {
            String key = entry.getKey();
            if (key.equals(OUTPUT_OPTION))
                proxy.setOutputFileName(entry.getValue());
            else if (key.equals(FORMAT_OPTION))
                proxy.setOutputFormat(entry.getValue());
            else if (!key.equals(REPORT_OPTION) && !key.equals(GEDCOM_OPTION) && !key.equals(INDIVIDUAL_OPTION) && !key.equals(OUTPUT_DIR_OPTION))
                proxy.setOption(key, entry.getValue());
        }

        
        String input = options.get(GEDCOM_OPTION);
        if (input != null && !input.equals(gedcomFile))
        {
            Origin origin = Origin.create(new File(input).toURI().toURL());
            GedcomReader reader = new GedcomReader(origin);
            gedcom = reader.read();
            gedcomFile = input;
        }

        Set<Class<?>> contexts = proxy.getContexts();
        Object context = gedcom;
        List<Object> contextList = null;

        String indiId = options.get(INDIVIDUAL_OPTION);
        if (indiId != null && contexts.contains(Indi.class))
        {
            if (indiId.matches("\\w*") && !indiId.equals("all"))
            {
                context = gedcom.getEntity(Gedcom.INDI, indiId);
                if (context == null)
                {
                    ReportRunner.LOG.warning("Individual '" + indiId + "' not found");
                    return;
                }
            }
            else
            {
                contextList = new ArrayList<Object>();

                
                if (indiId.equals("all"))
                    indiId = ".*";

                
                Pattern pattern = Pattern.compile(indiId);
                @SuppressWarnings("unchecked")
                Collection<Indi> indis = (Collection<Indi>)gedcom.getEntities(Gedcom.INDI);
                for (Indi indi : indis)
                {
                    if (pattern.matcher(indi.getId()).matches())
                        contextList.add(indi);
                }
                if (contextList.isEmpty())
                {
                    ReportRunner.LOG.warning("Regular expression '" + indiId + "' did not match any entities");
                    return;
                }
            }
        }
        else if (!contexts.contains(Gedcom.class))
        	throw new ReportRunnerException("Report context could not be established for report " + reportName);

        if (contextList != null)
        {
            int size = contextList.size();
            ReportRunner.LOG.info("Running report " + size + " times: " + reportName);
            int count = 0;
            for (Object o : contextList)
            {
                count++;
                if (o instanceof Entity)
                    ReportRunner.LOG.info("[" + count + "/" + size + "] " + ((Entity)o).getId());
                else
                    ReportRunner.LOG.info("[" + count + "/" + size + "]");
                startReport(proxy, o);
            }
        }
        else
            startReport(proxy, context);
    }

    
    private void startReport(ReportProxy proxy, Object context) throws ReportProxyException
    {
        
        String output = proxy.getOutputFileName();
        if (output != null)
        {
            String newOutput = output;
            if (context instanceof Indi)
            {
                Indi indi = (Indi)context;
                newOutput = newOutput.replaceAll("\\$i", indi.getId());
                newOutput = newOutput.replaceAll("\\$n", indi.getName());
                newOutput = newOutput.replaceAll("\\$f", indi.getFirstName());
                newOutput = newOutput.replaceAll("\\$l", indi.getLastName());
            }
            proxy.setOutputFileName(newOutput);

            
            File parentDir = new File(newOutput).getParentFile();
            if (parentDir != null)
                parentDir.mkdirs();
        }

        proxy.start(context);

        
        if (output != null)
            proxy.setOutputFileName(output);
    }

    
    private ReportProxy getProxy(String reportName) throws ReportRunnerException
    {
        ReportProxy proxy = proxiesByName.get(reportName);
        if (proxy == null)
        {
            Report report = reportsByName.get(reportName);
            if (report == null)
            	throw new ReportRunnerException("Report " + reportName + " not found");
			proxy = proxyFactory.create(report);
            proxiesByName.put(reportName, proxy);
        }
        return proxy;
    }

    
    public void printReports()
    {
        for (Report report : reportsByName.values())
            System.out.println(report.getClass().getCanonicalName() + "\t\t" + report.getName());
    }

    
    public String getFormats()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (String format : ReportProxy.getFormats())
            sb.append(format).append("|");
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        return sb.toString();
    }

    
    public void printOptions(String reportName) throws ReportRunnerException
    {
        ReportProxy proxy = getProxy(reportName);
        Collection<ReportOption> options = proxy.getOptions();

        System.out.println("These options are supposed to be used in configuration files");
        System.out.println();

        
        System.out.println("Common options:");
        System.out.println("   " + REPORT_OPTION + ":" + reportName + "\t\tspecifies this report");
        System.out.println("   " + GEDCOM_OPTION + "\t\tgedcom file to use");
        System.out.println("   " + INDIVIDUAL_OPTION + "\t\tstarting individual");
        System.out.println("   " + OUTPUT_OPTION + "\t\toutput file");
        System.out.println("   " + FORMAT_OPTION + "\t\toutput file format (only FO-based reports)" + getFormats());
        System.out.println();

        
        System.out.println("Report specific options:");
        for (ReportOption option : options)
        {
            System.out.println("   " + option.getName() + "\t\t" + option.getDescription() + "\t\t" + option.getType() + "  [" + option.getDefaultValue() + "]");
        }
    }
}
