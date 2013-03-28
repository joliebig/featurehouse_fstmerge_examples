package genj.reportrunner;

import genj.fo.Document;
import genj.fo.Format;
import genj.option.PropertyOption;
import genj.report.Report;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ReportProxy
{
	
    private Report proxiedReport;

    
    private Report originalReport;

    
    private Map<Class<?>, Method> contexts = new HashMap<Class<?>, Method>();

    
    private Map<String, ReportOption> options = new HashMap<String, ReportOption>();

    
    private String outputFileName = null;

    
    private String outputFormat = null;

    
	PrintWriter out = null;

    
    private static final Map<String, Format> FORMATS = new HashMap<String, Format>();
    static
    {
        for (Format format : Format.getFormats())
            FORMATS.put(format.getFormat().toLowerCase(), format);
    }

    
    public ReportProxy(Report originalReport, Report proxiedReport)
    {
        this.proxiedReport = proxiedReport;
        this.originalReport = originalReport;

        @SuppressWarnings("unchecked")
        List<PropertyOption> properties = proxiedReport.getOptions();
        for (PropertyOption property : properties)
        {
            String propertyName = property.getProperty();
            String description = originalReport.translate(propertyName);
            options.put(propertyName, new ReportOption(property, description));
        }

        
        Method[] methods = proxiedReport.getClass().getSuperclass().getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            
            if (!methods[i].getName().equals("start"))
            	continue;
            Class<?>[] params = methods[i].getParameterTypes();
            if (params.length!=1)
            	continue;
            Class<?> param = params[0];
            contexts.put(param, methods[i]);
          }
    }

    
    public void showDocumentToUser(Document doc)
    {
    	closeOut();
        Format formatter = FORMATS.get(outputFormat);

        File file = new File(outputFileName);
        try
        {
            formatter.format(doc, file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    
    public File getFileFromUser()
    {
    	closeOut();
        return new File(outputFileName);
    }

    private void closeOut()
    {
    	if (out != null)
    	{
    		out.close();
    		setOutput(null);
    	}
    }

    
    public String translate(String key, Object[] values)
    {
        return originalReport.translate(key, values);
    }

    
    public void start(Object context) throws ReportProxyException
    {
    	if (outputFileName != null)
    	{
    		out = new PrintWriter(new OnDemandFileWriter(outputFileName));
			setOutput(out);
    	}
        try
        {
        	Method method = contexts.get(context.getClass());
            method.invoke(proxiedReport, new Object[] { context });
        }
        catch (Throwable t)
        {
            throw new ReportProxyException(t);
        }
        finally
        {
        	if (out != null)
        	{
        		out.close();
        		setOutput(null);
        	}
        }
    }

    private void setOutput(PrintWriter printWriter)
    {
    	try
    	{
    		out = printWriter;
			Method method = proxiedReport.getClass().getDeclaredMethod("setOutput", PrintWriter.class);
			method.invoke(proxiedReport, printWriter);
		}
		
    	catch (NoSuchMethodException e)
    	{
			e.printStackTrace();
		}
    	catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
    	catch (IllegalAccessException e)
    	{
			e.printStackTrace();
		}
    	catch (InvocationTargetException e)
    	{
			e.printStackTrace();
		}
    }

    
    public Collection<ReportOption> getOptions()
    {
        return options.values();
    }

    
    public void setOption(String key, String value) throws ReportProxyException
    {
        ReportOption option = options.get(key);
        if (option == null)
            throw new ReportProxyException("Unknown option '" + key + "' in report " + proxiedReport.getClass().getCanonicalName());
        option.setValue(value);
    }

    
    public void resetOptions()
    {
        for (ReportOption option : options.values())
            option.reset();
        outputFormat = null;
        outputFileName = null;
    }

    
    public void setOutputFileName(String outputFileName)
    {
        this.outputFileName = outputFileName;
    }

    
    public String getOutputFileName()
    {
        return outputFileName;
    }

    
    public void setOutputFormat(String outputFormat)
    {
        this.outputFormat = outputFormat;
    }

    
    public Set<Class<?>> getContexts()
    {
    	return contexts.keySet();
    }

    
    public static Collection<String> getFormats()
    {
        return FORMATS.keySet();
    }
}
