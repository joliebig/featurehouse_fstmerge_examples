
package genj.geo;

import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.NestedBlockLayout;
import genj.util.swing.TextFieldWidget;
import genj.window.WindowManager;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;


public class QueryWidget extends JPanel {
  
  private final static Resources RESOURCES = Resources.get(QueryWidget.class);

  private static final String
    TXT_LOCATION = RESOURCES.getString("location"),
    TXT_LATLON = RESOURCES.getString("location.latlon"),
    TXT_QUERYING = RESOURCES.getString("query.querying");
  
  private final static NestedBlockLayout LAYOUT = new NestedBlockLayout(
      "<col>" +
      "<row><label/></row>" +
      "<row><label/><city wx=\"1\"/></row>" +
      "<row><label/><lat wx=\"1\"/><lon wx=\"1\"/></row>" +
      "<row><label/></row>" +
      "<row><hits wx=\"1\" wy=\"1\"/></row>" +
      "</col>"
      );
  
  
  private Model model;
  
  
  private GeoView view;
  
  
  private TextFieldWidget city, lat, lon;
  private JTable hits;
  private JLabel status;
  
  
  private boolean isChanging = false;
  
  
  public QueryWidget(GeoLocation setLocation, GeoView setView) {
    super(LAYOUT.copy());
    
    
    view = setView;
    model = new Model();
    
    
    city = new TextFieldWidget(setLocation.getCity());
    lat = new TextFieldWidget(setLocation.isValid() ? ""+setLocation.getCoordinate().y : "");
    lon = new TextFieldWidget(setLocation.isValid() ? ""+setLocation.getCoordinate().x : "");
    
    city.setToolTipText(RESOURCES.getString("query.city.tip"));
    lat.setToolTipText(RESOURCES.getString("query.lat.tip"));
    lon.setToolTipText(RESOURCES.getString("query.lon.tip"));
    
    hits = new JTable(model);
    hits.setPreferredScrollableViewportSize(new Dimension(64,64));
    
    status = new JLabel();
    
    add(new JLabel(RESOURCES.getString("query.instruction"))); 
    add(new JLabel(RESOURCES.getString("query.city"))); add(city);
    add(new JLabel(RESOURCES.getString("query.latlon"))); add(lat); add(lon);
    add(status);
    add(new JScrollPane(hits));
    
    
    final Timer timer = new Timer(500, new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        String sCity = city.getText().trim();
        int len =  sCity.length();
        if (sCity.endsWith("*")) len--;
        if (len<3) return;
        model.setLocation(new GeoLocation(sCity, null, null));
      }
    });
    timer.setRepeats(false);
    timer.start();
    
    city.addChangeListener( new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        
        if (!isChanging) timer.restart();
      }
    });

    ChangeListener cl = new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        if (!isChanging) 
          view.setSelection(getGeoLocation());
      }
    };
    lat.addChangeListener(cl);
    lon.addChangeListener(cl);
    
    hits.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        
        int row = hits.getSelectedRow();
        if (row<0)
          return;
        GeoLocation loc = model.getLocation(row);
        
        view.setSelection(loc);
        
        isChanging = true;
        city.setText(loc.getCity());
        lat.setText(""+loc.getCoordinate().y);
        lon.setText(""+loc.getCoordinate().x);
        isChanging = false;
        
      }
    });
    
    
  }
  
  
  public void addNotify() {
    
    super.addNotify();
    
    model.start();
  }
  
  
  public void removeNotify() {
    model.stop();
    
    view.setSelection(Collections.EMPTY_LIST);
    
    super.removeNotify();
  }
  
  
  public GeoLocation getGeoLocation() {
    try {
      GeoLocation loc = new GeoLocation(city.getText(), null, null);
      loc.setCoordinate(Double.parseDouble(lat.getText()), Double.parseDouble(lon.getText()));
      return loc;
    } catch (Throwable t) {
      return null;
    }
  }
  
  
  private class Model extends AbstractTableModel implements Runnable {
    
    private Thread thread = null;
    private boolean running = false;
    private GeoLocation query = null;
    private List locations = new ArrayList();
    
    
    public String getColumnName(int col) {
      switch (col) {
      default: case 0: return TXT_LOCATION;
      case 1: return TXT_LATLON;
    }
    }
    
    
    public int getColumnCount() {
      return 2;
    }
    
    
    public int getRowCount() {
      return locations.size();
    }
    
    
    public Object getValueAt(int row, int col) {
      GeoLocation loc = (GeoLocation)locations.get(row);
      switch (col) {
        default: case 0: return loc.toString();
        case 1: return loc.getCoordinateAsString();
      }
    }
    
    
    public GeoLocation getLocation(int row) {
      return (GeoLocation)locations.get(row);
    }
    
    
    public void setLocation(GeoLocation set) {
      synchronized (this) {
        query = set;
        notify();
      }
    }

    
    public void run() {
      
      while (running) {
        
        GeoLocation todo;
        synchronized (this) { 
          try { this.wait(250); } catch (InterruptedException ie) {} 
          todo = query;
          query = null;
        }
        
        if (running&&todo!=null) {
          synchronized (this) {
            locations = Collections.EMPTY_LIST;
            fireTableDataChanged();
            status.setText(TXT_QUERYING);
          }
          try {
            List found  = GeoService.getInstance().query(todo);
            synchronized (this) {
              locations = found;
              fireTableDataChanged();
              status.setText(RESOURCES.getString("query.matches", String.valueOf(found.size())));
            }
          } catch (final GeoServiceException e) {
            GeoView.LOG.log(Level.WARNING, "exception while querying", e);
            SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                if (QueryWidget.this.isVisible())
                  WindowManager.getInstance(QueryWidget.this).openDialog(null, TXT_QUERYING, WindowManager.INFORMATION_MESSAGE, e.getMessage(), Action2.okOnly(), QueryWidget.this);
              }
            });
          }
        }
      }
      
    }
    
    
    private synchronized void start() {
      stop();
      running = true;
      thread = new Thread(this);
      thread.start();
    }
    
    
    private void stop() {
      running = false;
      synchronized (this) {
        notify();
        if (thread!=null) thread.interrupt();
      }
      
      
    }
    
  } 
  
}
