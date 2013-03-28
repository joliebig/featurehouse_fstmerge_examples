




















package wsi.ra.tool;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;







public class ResourceLoader
{
    

    


    private static ResourceLoader resourceLoader;

    

    

    
    private ResourceLoader()
    {
    }

    

    

    
    public static synchronized ResourceLoader instance()
    {
        if (resourceLoader == null)
        {
            resourceLoader = new ResourceLoader();
        }

        return resourceLoader;
    }

    
    public byte[] getBytesFromResourceLocation(String resourceLocation)
    {
        if (resourceLocation == null)
        {
            return null;
        }
		
	    
	    
	    resourceLocation = resourceLocation.trim();

        
        
        if (resourceLocation.startsWith("..") ||
                resourceLocation.startsWith("/") ||
                resourceLocation.startsWith("\\") ||
                ((resourceLocation.length() > 1) &&
                (resourceLocation.charAt(1) == ':')))
        {
            return getBytesFromFile(resourceLocation);
        }

        InputStream in = this.getClass().getClassLoader()
                             .getSystemResourceAsStream(resourceLocation);

        if (in == null)
        {
            
            in = this.getClass().getClassLoader().getResourceAsStream(resourceLocation);
        }

        if (in == null)
        {
            return null;
        }

        byte bytes[]=getBytesFromStream(in);








        return bytes;

        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
    }

    
    public static Vector readLines(String resourceFile)
    {
        return readLines(resourceFile, false);
    }

    
    public static Vector readLines(String resourceFile,
        boolean ignoreCommentedLines)
    {
        if (resourceFile == null)
        {
            return null;
        }

        byte[] bytes = ResourceLoader.instance().getBytesFromResourceLocation(resourceFile);

        if (bytes == null)
        {
            return null;
        }

        ByteArrayInputStream sReader = new ByteArrayInputStream(bytes);
        LineNumberReader lnr = new LineNumberReader(new InputStreamReader(
                    sReader));

        String line;
        Vector vector = new Vector(100);

        try
        {
            while ((line = lnr.readLine()) != null)
            {
                if (!ignoreCommentedLines)
                {
                    if (!(line.charAt(0) == '#'))
                    {
                        vector.add(line);

                        
                    }
                }
                else
                {
                    vector.add(line);
                }
            }
        }
         catch (IOException ex)
        {
            ex.printStackTrace();
        }

        return vector;
    }

    

    
    private byte[] getBytesFromArchive(String urlToZipArchive,
        String internalArchivePath)
    {
        URL url = null;
        int size = -1;
        byte[] b = null;

        try
        {
            url = new URL(urlToZipArchive);

            
            ZipFile zf = new ZipFile(url.getFile());
            Enumeration e = zf.entries();

            while (e.hasMoreElements())
            {
                ZipEntry ze = (ZipEntry) e.nextElement();

                if (ze.getName().equals(internalArchivePath))
                {
                    if (ze.isDirectory())
                    {
                        return null;
                    }

                    
                    if (ze.getSize() > 65536)
                    {
                        System.out.println(
                            "Resource files should be smaller than 65536 bytes...");
                    }

                    size = (int) ze.getSize();
                }
            }

            zf.close();

            FileInputStream fis = new FileInputStream(url.getFile());
            BufferedInputStream bis = new BufferedInputStream(fis);
            ZipInputStream zis = new ZipInputStream(bis);
            ZipEntry ze = null;

            while ((ze = zis.getNextEntry()) != null)
            {
                if (ze.getName().equals(internalArchivePath))
                {
                    b = new byte[(int) size];

                    int rb = 0;
                    int chunk = 0;

                    while (((int) size - rb) > 0)
                    {
                        chunk = zis.read(b, rb, (int) size - rb);

                        if (chunk == -1)
                        {
                            break;
                        }

                        rb += chunk;
                    }
                }
            }
        }
         catch (Exception e)
        {
            
            System.err.println("error while loading");

            return null;
        }

        return b;
    }

    
    private byte[] getBytesFromFile(String fileName)
    {
        if (fileName.startsWith("/cygdrive/"))
        {
            int length = "/cygdrive/".length();
            fileName = fileName.substring(length, length + 1) + ":" +
                fileName.substring(length + 1);
        }

        
        
        
        

        File file = new File(fileName);
        FileInputStream fis = null;

        try
        {
            fis = new FileInputStream(file);
        }
         catch (Exception e)
        {
            return null;
        }

        BufferedInputStream bis = new BufferedInputStream(fis);

        
        
        int size = (int) file.length();
        byte[] b = new byte[size];
        int rb = 0;
        int chunk = 0;

        try
        {
            while (((int) size - rb) > 0)
            {
                chunk = bis.read(b, rb, (int) size - rb);

                if (chunk == -1)
                {
                    break;
                }

                rb += chunk;
            }
        }
         catch (Exception e)
        {
            return null;
        }

        return b;
    }

    
    private byte[] getBytesFromStream(InputStream stream)
    {
        
        
        
        

        BufferedInputStream bis = new BufferedInputStream(stream);

        try
        {
            int size = (int) bis.available();
            byte[] b = new byte[size];
            int rb = 0;
            int chunk = 0;

            while (((int) size - rb) > 0)
            {
                chunk = bis.read(b, rb, (int) size - rb);

                if (chunk == -1)
                {
                    break;
                }

                rb += chunk;
            }

            return b;
        }
         catch (Exception e)
        {
            return null;
        }
    }
}






