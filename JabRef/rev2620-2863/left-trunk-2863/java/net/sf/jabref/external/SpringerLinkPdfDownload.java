package net.sf.jabref.external;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;


public class SpringerLinkPdfDownload implements FullTextFinder {

    private static final String BASE_URL = "http://www.springerlink.com";
    private static final String CONTENT_BASE_URL = "http://www.springerlink.com/content/";

    public SpringerLinkPdfDownload() {

    }

    public boolean supportsSite(URL url) {
        return url.getHost().toLowerCase().indexOf("www.springerlink.com") != -1;
    }



    public URL findFullTextURL(URL url) throws IOException {
        
        int idIndex = url.toString().indexOf("id=");
        if (idIndex > -1) {
            url = new URL(CONTENT_BASE_URL+url.toString().substring(idIndex+3));
        }
        
        String pageSource = FindFullText.loadPage(url);
        FindFullText.dumpToFile(pageSource, new File("page.html"));
        int index = pageSource.indexOf("PDF (");
        if (index > -1) {
            String leading = pageSource.substring(0, index);
            String marker = "href=";
            index = leading.toLowerCase().lastIndexOf(marker);
            if ((index > -1) && (index+marker.length()+1 < leading.length())) {
                int endIndex = leading.indexOf("\"", index+marker.length()+1);

                try {
                    URL pdfUrl = new URL(BASE_URL+leading.substring(index+marker.length()+1, endIndex));
                    System.out.println(pdfUrl.toString());
                    return pdfUrl;
                } catch (MalformedURLException e) {
                    return null;
                }
            }
            return null;
        } else
            return null;
    }
}