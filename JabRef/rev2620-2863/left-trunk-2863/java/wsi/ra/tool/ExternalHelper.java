




















package wsi.ra.tool;



public class ExternalHelper
{
    

    public static final String OS_WINDOWS = "windows";
    public static final String OS_LINUX = "linux";
    public static final String OS_SOLARIS = "solaris";

    

    

    
    private ExternalHelper()
    {
    }

    

    

    
    public static String getOperationSystemName()
    {
        String osName = System.getProperty("os.name");

        
        if (osName.indexOf("Windows") != -1)
        {
            osName = OS_WINDOWS;
        }
        else if (osName.indexOf("Linux") != -1)
        {
            osName = OS_LINUX;
        }
        else if (osName.indexOf("Solaris") != -1)
        {
            osName = OS_SOLARIS;
        }

        return osName;
    }
}



