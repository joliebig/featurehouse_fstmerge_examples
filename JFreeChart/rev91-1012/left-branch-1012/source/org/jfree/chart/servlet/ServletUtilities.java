

package org.jfree.chart.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;


public class ServletUtilities {

    
    private static String tempFilePrefix = "jfreechart-";
    
    
    private static String tempOneTimeFilePrefix = "jfreechart-onetime-";
    
    
    public static String getTempFilePrefix() {
        return ServletUtilities.tempFilePrefix;   
    }
    
    
    public static void setTempFilePrefix(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Null 'prefix' argument.");   
        }
        ServletUtilities.tempFilePrefix = prefix;
    }
    
    
    public static String getTempOneTimeFilePrefix() {
        return ServletUtilities.tempOneTimeFilePrefix;
    }
    
    
    public static void setTempOneTimeFilePrefix(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Null 'prefix' argument.");   
        }
        ServletUtilities.tempOneTimeFilePrefix = prefix;
    }
    
    
    public static String saveChartAsPNG(JFreeChart chart, int width, int height,
            HttpSession session) throws IOException {
        
        return ServletUtilities.saveChartAsPNG(chart, width, height, null, 
                session);
        
    }

    
    public static String saveChartAsPNG(JFreeChart chart, int width, int height,
            ChartRenderingInfo info, HttpSession session) throws IOException {

        if (chart == null) {
            throw new IllegalArgumentException("Null 'chart' argument.");   
        }
        ServletUtilities.createTempDir();
        String prefix = ServletUtilities.tempFilePrefix;
        if (session == null) {
            prefix = ServletUtilities.tempOneTimeFilePrefix;
        }
        File tempFile = File.createTempFile(prefix, ".png", 
                new File(System.getProperty("java.io.tmpdir")));
        ChartUtilities.saveChartAsPNG(tempFile, chart, width, height, info);
        if (session != null) {
            ServletUtilities.registerChartForDeletion(tempFile, session);
        }
        return tempFile.getName();

    }

    
    public static String saveChartAsJPEG(JFreeChart chart, int width, 
                                         int height, HttpSession session) 
            throws IOException {

        return ServletUtilities.saveChartAsJPEG(chart, width, height, null, 
                session);
        
    }

    
    public static String saveChartAsJPEG(JFreeChart chart, int width, 
            int height, ChartRenderingInfo info, HttpSession session)
            throws IOException {

        if (chart == null) {
            throw new IllegalArgumentException("Null 'chart' argument.");   
        }
        
        ServletUtilities.createTempDir();
        String prefix = ServletUtilities.tempFilePrefix;
        if (session == null) {
            prefix = ServletUtilities.tempOneTimeFilePrefix;   
        }
        File tempFile = File.createTempFile(prefix, ".jpeg", 
                new File(System.getProperty("java.io.tmpdir")));
        ChartUtilities.saveChartAsJPEG(tempFile, chart, width, height, info);
        if (session != null) {
            ServletUtilities.registerChartForDeletion(tempFile, session);
        }
        return tempFile.getName();

    }

    
    protected static void createTempDir() {
        String tempDirName = System.getProperty("java.io.tmpdir");
        if (tempDirName == null) {
            throw new RuntimeException("Temporary directory system property " 
                    + "(java.io.tmpdir) is null.");
        }

        
        File tempDir = new File(tempDirName);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
    }

    
    protected static void registerChartForDeletion(File tempFile, 
            HttpSession session) {

        
        if (session != null) {
            ChartDeleter chartDeleter 
                = (ChartDeleter) session.getAttribute("JFreeChart_Deleter");
            if (chartDeleter == null) {
                chartDeleter = new ChartDeleter();
                session.setAttribute("JFreeChart_Deleter", chartDeleter);
            }
            chartDeleter.addChart(tempFile.getName());
        }
        else {
            System.out.println("Session is null - chart will not be deleted");
        }
    }

    
    public static void sendTempFile(String filename, 
            HttpServletResponse response) throws IOException {

        File file = new File(System.getProperty("java.io.tmpdir"), filename);
        ServletUtilities.sendTempFile(file, response);
    }

    
    public static void sendTempFile(File file, HttpServletResponse response)
            throws IOException {

        String mimeType = null;
        String filename = file.getName();
        if (filename.length() > 5) {
            if (filename.substring(filename.length() - 5, 
                    filename.length()).equals(".jpeg")) {
                mimeType = "image/jpeg";
            } 
            else if (filename.substring(filename.length() - 4, 
                    filename.length()).equals(".png")) {
                mimeType = "image/png";
            }
        }
        ServletUtilities.sendTempFile(file, response, mimeType);
    }

    
    public static void sendTempFile(File file, HttpServletResponse response,
                                    String mimeType) throws IOException {

        if (file.exists()) {
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(file));

            
            if (mimeType != null) {
                response.setHeader("Content-Type", mimeType);
            }
            response.setHeader("Content-Length", String.valueOf(file.length()));
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            response.setHeader("Last-Modified", 
                    sdf.format(new Date(file.lastModified())));

            BufferedOutputStream bos = new BufferedOutputStream(
                    response.getOutputStream());
            byte[] input = new byte[1024];
            boolean eof = false;
            while (!eof) {
                int length = bis.read(input);
                if (length == -1) {
                    eof = true;
                } 
                else {
                    bos.write(input, 0, length);
                }
            }
            bos.flush();
            bis.close();
            bos.close();
        }
        else {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        return;
    }

    
    public static String searchReplace(String inputString,
                                       String searchString,
                                       String replaceString) {

        int i = inputString.indexOf(searchString);
        if (i == -1) {
            return inputString;
        }

        String r = "";
        r += inputString.substring(0, i) + replaceString;
        if (i + searchString.length() < inputString.length()) {
            r += searchReplace(inputString.substring(i + searchString.length()),
                    searchString, replaceString);
        }

        return r;
    }

}
