
package genj.print;

import genj.option.Option;
import genj.option.OptionListener;
import genj.option.OptionsWidget;
import genj.option.PropertyOption;
import genj.renderer.Options;
import genj.util.Dimension2d;
import genj.util.swing.Action2;
import genj.util.swing.ChoiceWidget;
import genj.util.swing.NestedBlockLayout;
import genj.util.swing.UnitGraphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.print.PrintService;
import javax.print.ServiceUI;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

public class PrintWidget extends JTabbedPane implements OptionListener {
  
  
  private PrintTask task;
  
  
  private ChoiceWidget services;

  
  private Preview preview;

    public PrintWidget(PrintTask task) {
    
    
    this.task = task;

    add(PrintTask.RESOURCES.getString("printer" ), createFirstPage());
    add(PrintTask.RESOURCES.getString("settings"), createSecondPage());
    
    
  }
  
  private JPanel createFirstPage() {
    
    String LAYOUT_TEMPLATE = 
      "<col>"+
      "<row><lprinter/><printers wx=\"1\"/><settings/></row>"+
      "<row><lpreview/></row>"+
      "<row><preview wx=\"1\" wy=\"1\"/></row>"+
      "</col>";
    
    
    JPanel page = new JPanel(new NestedBlockLayout(LAYOUT_TEMPLATE));
    
    
    page.add("lprinter", new JLabel(PrintTask.RESOURCES.getString("printer")));
    
    
    services = new ChoiceWidget(task.getServices(), task.getService());
    services.setEditable(false);
    services.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        
        if (e.getStateChange()!=ItemEvent.SELECTED) 
          
          task.setService((PrintService)services.getSelectedItem());
      }
    });
    page.add("printers", services);

    
    page.add("settings", new JButton(new Settings()));
    
    
    page.add("lpreview", new JLabel(PrintTask.RESOURCES.getString("preview")));
    
    
    preview = new Preview();
    
    page.add("preview", new JScrollPane(preview));
    
    
    return page;    
  }
  
  private JComponent createSecondPage() {
    List options = PropertyOption.introspect(task.getRenderer());
    for (int i = 0; i < options.size(); i++) 
      ((Option)options.get(i)).addOptionListener(this);
    return new OptionsWidget(PrintTask.RESOURCES.getString("printer"), options);
  }
  
  
  public void optionChanged(Option option) {
    task.invalidate();
  }
  
  
  private class Preview extends JComponent implements Scrollable {
    
    private float 
      padd = 0.1F, 
      zoom = 0.25F; 

    private Point dpiScreen = Options.getInstance().getDPI();
    
    
    public Dimension getPreferredSize() {
      
      Dimension pages = task.getPages(); 
      Rectangle2D page = task.getPage(pages.width-1,pages.height-1, padd);
      return new Dimension(
        (int)((page.getMaxX())*dpiScreen.x*zoom),
        (int)((page.getMaxY())*dpiScreen.y*zoom)
      );
    }

    
    protected void paintComponent(Graphics g) {
      
      
      g.setColor(Color.gray);
      g.fillRect(0,0,getWidth(),getHeight());
      g.setColor(Color.white);
      
      
      Printer renderer = task.getRenderer();
      Dimension pages = task.getPages(); 
      UnitGraphics ug = new UnitGraphics(g, dpiScreen.x*zoom, dpiScreen.y*zoom);
      Rectangle2D clip = ug.getClip();
      for (int y=0;y<pages.height;y++) {
        for (int x=0;x<pages.width;x++) {
          
          Rectangle2D 
            page = task.getPage(x,y, padd), 
            imageable = task.getPrintable(page);
          
          if (!clip.intersects(page))
            continue;
          
          ug.setColor(Color.white);
          ug.draw(page, 0, 0, true);
          
          ug.setColor(Color.gray);
          ug.draw(String.valueOf(x+y*pages.width+1),page.getCenterX(),page.getCenterY(),0.5D,0.5D);
          ug.pushTransformation();
          ug.pushClip(imageable);
          ug.translate(imageable.getMinX(), imageable.getMinY());
          ug.getGraphics().scale(zoom,zoom);
          renderer.renderPage(ug.getGraphics(), new Point(x,y), new Dimension2d(imageable), dpiScreen, true);
          ug.popTransformation();
          ug.popClip();
          
        }
      }
      
    }

    public boolean getScrollableTracksViewportHeight() {
      return false;
    }

    public boolean getScrollableTracksViewportWidth() {
      return false;
    }

    public Dimension getPreferredScrollableViewportSize() {
      return new Dimension(0,0);
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
      return orientation==SwingConstants.VERTICAL ? visibleRect.height : visibleRect.width;
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
      return 1;
    }

  } 

  
  private class Settings extends Action2 {

    
    private Settings() {
      super.setText(PrintTask.RESOURCES.getString("settings"));
      super.setTarget(PrintWidget.this);
    }

    
    protected void execute() {
      
      Point pos = task.getOwner().getLocationOnScreen();
      PrintService choice = ServiceUI.printDialog(null, pos.x, pos.y, task.getServices(), task.getService(), null, task.getAttributes());
      if (choice!=null) {
        services.setSelectedItem(choice);
        task.invalidate();
      }

      
      preview.revalidate();
      preview.repaint();
      
    }
    
  } 
  
} 
