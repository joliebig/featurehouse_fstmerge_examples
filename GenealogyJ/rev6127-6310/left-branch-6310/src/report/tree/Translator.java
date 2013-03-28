

package tree;

import genj.report.Report;


public class Translator
{
    private Report report;

    public Translator(Report report)
    {
        this.report = report;
    }

    
    public final String translate(String key)
    {
        return report.translate(key);
    }

    
    public final String translate(String key, int value)
    {
        return report.translate(key, value);
    }

    
    public final String translate(String key, Object value)
    {
        return report.translate(key, value);
    }

    
    public String translate(String key, Object[] values)
    {
        return report.translate(key, values);
    }
}
