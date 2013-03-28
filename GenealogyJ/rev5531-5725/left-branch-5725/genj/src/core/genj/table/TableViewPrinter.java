
package genj.table;

import genj.gedcom.Property;
import genj.gedcom.PropertySimpleValue;
import genj.print.Printer;
import genj.renderer.PropertyRendererFactory;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.Dimension2D;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.table.TableModel;

public class TableViewPrinter implements Printer {
  
  private int pad = 2;
  
  
  private int[] 
    rowHeights,
    colWidths,
    colsOnPage,
    rowsOnPage;
  private int 
  	headerHeight,
    pageWidth,
    pageHeight;

  
  private TableView table;
  
  
  private Font font = new Font("SansSerif", Font.PLAIN, 8);
  private FontRenderContext context = new FontRenderContext(null, false, true);

  private Property header = new PropertySimpleValue();
  
  private int maxColumnWidth = 25;
  
  
  public int getMaxColumnWidth() {
    return maxColumnWidth;
  }
  
  
  public void setMaxColumnWidth(int set) {
    maxColumnWidth = Math.max(1, Math.min(100, set));
  }
  
    public void setView(JComponent view) {
    table = (TableView)view;
  }

  
  public Dimension calcSize(Dimension2D pageSizeInInches, Point dpi) {

    TableModel model = table.getTable().getTableModel();
    
    
    pageWidth = (int)Math.ceil(pageSizeInInches.getWidth()*dpi.x);
    pageHeight = (int)Math.ceil(pageSizeInInches.getHeight()*dpi.y);
    headerHeight = 0;
    
    
    rowHeights = new int[model.getRowCount()];
    colWidths = new int[model.getColumnCount()];
    
    
    for (int col=0;col<colWidths.length;col++) {
      header.setValue(model.getColumnName(col));
      calcSize(-1, col, header, dpi);
    }
    
    
    for (int row=0, height=0;row<rowHeights.length;row++) {
      
      for (int col=0;col<colWidths.length;col++) {
        
        calcSize(row, col, (Property)model.getValueAt(row,col), dpi);
      }
      
    }

    
    int pagesx = 1;
    int pagesy = 1;
    colsOnPage = new int[colWidths.length];
    rowsOnPage = new int[rowHeights.length];
    
    
    for (int col=0, width=0;col<colWidths.length;col++) {
      
      if (width+colWidths[col]>pageWidth) {
        width = 0;
        pagesx++;
      }
      
      colsOnPage[pagesx-1]++;
      
      width += colWidths[col] + pad;
    }
    
    
    for (int row=0, height=headerHeight+pad;row<rowHeights.length;row++) {
      
      if (height+rowHeights[row]>pageHeight) {
        height = headerHeight+pad;
        pagesy ++;
      }
      
      rowsOnPage[pagesy-1]++;
      
      height += rowHeights[row] + pad;
    }
    
    
    return new Dimension(pagesx,pagesy);
  }

  
  private void calcSize(int row, int col, Property prop, Point dpi) {
    
    if (prop==null)
      return;
    
    Dimension2D dim = PropertyRendererFactory.DEFAULT.getRenderer(prop).getSize(font, context, prop, new HashMap(), dpi);
    
    if (row<0)
      headerHeight    = max(dim.getHeight(), headerHeight, pageHeight - headerHeight - pad);
    else
      rowHeights[row] = max(dim.getHeight(), rowHeights[row], pageHeight - headerHeight - pad);
    
    colWidths[col] = max(dim.getWidth(), colWidths[col], pageWidth*maxColumnWidth/100);
    
  }
  
  
  private int max(double one, int two, int limit) {
    return Math.min(limit, (int)Math.max(Math.ceil(one), two));
  }
  
  
  public void renderPage(Graphics2D g, Point page, Dimension2D pageSizeInInches, Point dpi, boolean preview) {
    
    
    if (colsOnPage[page.x]==0)
      return;
    
    
    g.scale(dpi.x/72F, dpi.y/72F);

    
    g.setColor(Color.BLACK);
    g.setFont(font);

    
    TableModel model = table.getTable().getTableModel();
    
    
    int scol=0, cols=0;
    for (int c=0;c<page.x;c++)
      scol += colsOnPage[c];
    cols = colsOnPage[page.x];
    
    
    for (int col=0,x=0;col<cols;col++) {
      
      Rectangle r = new Rectangle(x, 0, colWidths[scol+col], headerHeight); 
      header.setValue(model.getColumnName(scol+col));
      render(g, r, header, dpi);
      
      x += r.getWidth() + pad;
      
      if (col<cols-1)
        g.drawLine(x - pad/2, 0, x - pad/2, pageHeight);
    }
    g.drawLine(0, headerHeight + pad/2, pageWidth, headerHeight + pad/2);
    
    
    if (rowsOnPage.length>0) {
      int rows = rowsOnPage[page.y];
      int srow=0;
      for (int r=0;r<page.y;r++)
        srow += rowsOnPage[r];
      
      for (int row=0,y=headerHeight+pad;row<rows;row++) {
        
        for (int col=0,x=0;col<cols;col++) {
          
          Rectangle r = new Rectangle(x, y, colWidths[scol+col], rowHeights[srow+row]);
          render(g, r, (Property)model.getValueAt(srow+row, scol+col), dpi);
          
          x += colWidths[scol+col] + pad;
        }
        
        y += rowHeights[srow+row] + pad;
        
        if (row<rows-1)
          g.drawLine(0, y - pad/2, pageWidth, y - pad/2);
        
      }
    }
    
    
  }

  
  private void render(Graphics2D g, Rectangle r, Property prop, Point dpi) {
    
    if (prop==null)
      return;
    
    Shape clip = g.getClip();
    g.clip(r);
    
    PropertyRendererFactory.DEFAULT.getRenderer(prop).render(g, r, prop, new HashMap(), dpi);
    
    g.setClip(clip);
    
  }
  
} 

