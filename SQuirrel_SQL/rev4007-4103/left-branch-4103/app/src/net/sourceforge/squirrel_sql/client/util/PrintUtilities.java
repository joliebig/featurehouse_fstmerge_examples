package net.sourceforge.squirrel_sql.client.util;




import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.RepaintManager;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
public class PrintUtilities implements Printable {
    
    private Component componentToBePrinted;
    
    
    private final static ILogger s_log =
        LoggerController.createLogger(PrintUtilities.class);  
    
    public static void printComponent(Component c) {
        new PrintUtilities(c).print();
    }
    public PrintUtilities(Component componentToBePrinted) {
        this.componentToBePrinted = componentToBePrinted;
    }
    public void print() {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(this);
        if (printJob.printDialog()) {
            try {
                printJob.print();
            } catch (PrinterException pe) {
                s_log.error("Error printing", pe);
            }
        }
    }
    public int print(Graphics g, PageFormat pf, int pageIndex) {
        int response = NO_SUCH_PAGE;
        Graphics2D g2 = (Graphics2D) g;
        Dimension d = componentToBePrinted.getSize(); 
        double panelWidth = d.width; 
        double panelHeight = d.height; 
        double pageHeight = pf.getImageableHeight(); 
        double pageWidth = pf.getImageableWidth(); 
        double scale = pageWidth / panelWidth;
        int totalNumPages = (int) Math.ceil(scale * panelHeight / pageHeight);
        
        if (pageIndex >= totalNumPages) {
            response = NO_SUCH_PAGE;
        }
        else {
            
            g2.translate(pf.getImageableX(), pf.getImageableY());
            
            g2.translate(0f, -pageIndex * pageHeight);
            
            g2.scale(scale, scale);
            
            disableDoubleBuffering(componentToBePrinted);
            componentToBePrinted.paint(g2); 
            enableDoubleBuffering(componentToBePrinted);
            response = Printable.PAGE_EXISTS;
        }
        return response;
    }
    public static void disableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(false);
    }
    public static void enableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(true);
    }
}