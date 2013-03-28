
package genj.print;

import genj.option.Option;
import genj.option.OptionListener;
import genj.option.OptionsWidget;
import genj.renderer.DPI;
import genj.renderer.Options;
import genj.renderer.RenderPreviewHintKey;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.ChoiceWidget;
import genj.util.swing.GraphicsHelper;
import genj.util.swing.NestedBlockLayout;
import genj.util.swing.ScrollPaneWidget;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.logging.Logger;

import javax.print.PrintService;
import javax.print.ServiceUI;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PrintWidget extends JTabbedPane {
  
   final static Resources RESOURCES = Resources.get(PrintWidget.class);
   final static Logger LOG = Logger.getLogger("genj.print");
  
  private PrintTask task;
  private ChoiceWidget services;
  private ScalingWidget scaling;
  private Preview preview;
  private Apply apply = new Apply();
  private JCheckBox fit, empties;
  
    public PrintWidget(PrintTask task) {
    
    
    this.task = task;

    add(RESOURCES.getString("printer" ), createFirstPage());
    add(RESOURCES.getString("settings"), createSecondPage());
    
    
  }
  
  public void commit() {
    
    if (services.getSelectedItem()!=null)
      task.setService((PrintService)services.getSelectedItem());

    
  }
  
  private JPanel createFirstPage() {
    
    String LAYOUT_TEMPLATE = 
      "<col>"+
      "<row><lprinter/><printers wx=\"1\"/><settings/></row>"+
      "<row><zoom/><fit/></row>"+
      "<row><lpreview/></row>"+
      "<row><preview wx=\"1\" wy=\"1\"/></row>"+
      "</col>";
    
    
    JPanel page = new JPanel(new NestedBlockLayout(LAYOUT_TEMPLATE));
    
    
    page.add(new JLabel(RESOURCES.getString("printer")));
    
    
    services = new ChoiceWidget(task.getServices(), task.getService());
    services.setEditable(false);
    services.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        
        if (e.getStateChange()!=ItemEvent.SELECTED) 
          
          task.setService((PrintService)services.getSelectedItem());
      }
    });
    page.add(services);

    
    page.add(new JButton(new Settings()));
    
    
    scaling = new ScalingWidget();
    scaling.addChangeListener(apply);
    page.add(scaling);

    fit = new JCheckBox(RESOURCES.getString("fit"), false);
    fit.setEnabled(false);
    fit.addChangeListener(apply);
    page.add(fit);

    
    page.add(new JLabel(RESOURCES.getString("preview")));
    preview = new Preview();
    
    page.add(new ScrollPaneWidget(preview));
    
    
    return page;    
  }
  
  private JComponent createSecondPage() {
    List<? extends Option> options = task.getOptions();
    for (Option option : options) 
      option.addOptionListener(apply);
    return new OptionsWidget(task.getTitle(), options);
  }
  
  
  private class Preview extends JComponent implements Scrollable {
    
    private double zoom = 0.2D;
    private int gap = 5; 
    private DPI dpi = new DPI(
        (int)(Options.getInstance().getDPI().horizontal() * zoom),
        (int)(Options.getInstance().getDPI().vertical  () * zoom)
      );
    
    
    public Dimension getPreferredSize() {

      Dimension pages = task.getPages(); 
      
      Dimension2D page = dpi.toPixel(task.getPageSize());

      return new Dimension(
          (int)Math.ceil(pages.width*page.getWidth()   + pages.width *gap + gap), 
          (int)Math.ceil(pages.height*page.getHeight() + pages.height*gap + gap)
      );
    }

    
    protected void paintComponent(Graphics g) {
      
      
      g.setColor(Color.gray);
      g.fillRect(0,0,getWidth(),getHeight());
      g.setColor(Color.white);
      
      
      Graphics2D g2d = (Graphics2D)g;
      g2d.setRenderingHint(RenderPreviewHintKey.KEY, true);
      g2d.setRenderingHint(DPI.KEY, dpi);
      Dimension pages = task.getPages(); 
      Dimension2D pageSize = dpi.toPixel(task.getPageSize());
      Rectangle clip = g2d.getClipBounds();
      AffineTransform at = g2d.getTransform();
      for (int y=0;y<pages.height;y++) {
        for (int x=0;x<pages.width;x++) {
          
          
          Rectangle2D page = new Rectangle2D.Double(
             gap + x*(pageSize.getWidth ()+gap), 
             gap + y*(pageSize.getHeight()+gap), 
             pageSize.getWidth (), 
             pageSize.getHeight()
          );
          
          
          if (!clip.intersects(page))
            continue;
          
          
          g2d.setColor(Color.white);
          g2d.fill(page);
          
          
          g.setColor(Color.gray);
          g.setFont(new Font("Arial", Font.BOLD, 48));
          GraphicsHelper.render(g2d, String.valueOf(x+y*pages.width+1), page.getCenterX(), page.getCenterY(), 0.5, 0.5);
          
          
          g2d.translate( gap + x*(page.getWidth()+gap), gap + y*(page.getHeight()+gap));
          task.print(g2d, y, x);
          g2d.setTransform(at);
          g2d.setClip(clip);
          
          
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
      super.setText(RESOURCES.getString("settings"));
    }

    
    public void actionPerformed(ActionEvent event) {
      
      
      Point pos = PrintWidget.this.getLocationOnScreen();
      PrintService choice = ServiceUI.printDialog(null, pos.x, pos.y, task.getServices(), task.getService(), null, task.getAttributes());
      if (choice!=null) 
        services.setSelectedItem(choice);

      
      preview.revalidate();
      preview.repaint();
      
    }
    
  } 

  
  private class Apply implements ChangeListener, OptionListener{
    
    public void stateChanged(ChangeEvent e) {
      apply();
    }
    public void optionChanged(Option option) {
      apply();
    }
    private void apply() {
      Object scale = scaling.getValue();
      if (scale instanceof Dimension) {
        task.setPages((Dimension)scale, fit.isSelected());
        fit.setEnabled(true);
      }
      if (scale instanceof Double) {
        task.setZoom((Double)scale);
        fit.setEnabled(false);
      }
      preview.revalidate();
      preview.repaint();
    }
  }
  
} 
