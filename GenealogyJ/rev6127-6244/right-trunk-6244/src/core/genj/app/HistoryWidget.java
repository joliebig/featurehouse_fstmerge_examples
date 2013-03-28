
package genj.app;

import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomListener;
import genj.gedcom.Property;
import genj.util.swing.Action2;
import genj.util.swing.GraphicsHelper;
import genj.util.swing.ImageIcon;
import genj.util.swing.PopupWidget;
import genj.view.SelectionSink;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;


public class HistoryWidget extends JToolBar {
  
  private final static Icon POPUP =  GraphicsHelper.getIcon(Color.BLACK, 0, 0, 8, 0, 4, 4);
  
  private List<Entity> history = new ArrayList<Entity>();
  private int index = -1;
  
  private EventHandler events = new EventHandler();
  private Back back = new Back();
  private Forward forward = new Forward();
  private Popup pick = new Popup();
  
  
  public HistoryWidget(Workbench workbench) {
    workbench.addWorkbenchListener(events);
    setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
    add(back);
    add(forward);
    add(pick);
   
    setFloatable(false);
  }
  
  @Override
  public Dimension getMaximumSize() {
    return getPreferredSize();
  }
  
  @Override
  public JButton add(Action a) {
    JButton b = super.add(a);
    b.setFocusable(false);
    return b;
  }
  
  private void fireSelection(Entity e) {
    SelectionSink.Dispatcher.fireSelection(HistoryWidget.this, new Context(e), true);
  }
  
  private void update() {
    forward.setEnabled(index<history.size()-1);
    back.setEnabled(index>0);
    pick.setEnabled(history.size()>1);
  }
  
  
  private class Popup extends PopupWidget {
    Popup() {
      super(POPUP);
    }
    @Override
    public void showPopup() {
      removeItems();
      for (int i=0;i<history.size();i++) {
        JMenuItem item = new JMenuItem(new Jump(i));
        if (index==i)
          item.setFont(item.getFont().deriveFont(Font.BOLD));
        addItem(item);
      }
      super.showPopup();
    }
  }
  
  private class Jump extends Action2 {
    private int i;
    public Jump(int i) {
      this.i = i;
      Entity entity = history.get(i);
      setImage(entity.getImage());
      setText(entity.toString());
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      index = i;
      update();
      fireSelection(history.get(i));
    }
  }
  
  
  private class Back extends Action2 {
    public Back() {
      setImage(new ImageIcon(this,"images/Back.png"));
      install(HistoryWidget.this, "alt LEFT");
    }
    public void actionPerformed(ActionEvent evt) {
      if (index<1)
        return;
      index--;
      update();
      fireSelection(history.get(index));
    }
  }
  
  
  private class Forward extends Action2 {
    public Forward() {
      install(HistoryWidget.this, "alt RIGHT");
      setImage(new ImageIcon(this,"images/Forward.png"));
    }
    public void actionPerformed(ActionEvent evt) {
      if (index==history.size()-1)
        return;
      index++;
      update();
      fireSelection(history.get(index));
    }
  }
  
  private class EventHandler extends WorkbenchAdapter implements GedcomListener {

    @Override
    public void gedcomClosed(Workbench workbench, Gedcom gedcom) {
      history.clear();
      index = -1;
      update();
      gedcom.removeGedcomListener(this);
    }

    @Override
    public void gedcomOpened(Workbench workbench, Gedcom gedcom) {
      gedcom.addGedcomListener(this);
    }

    @Override
    public void selectionChanged(Workbench workbench, Context context, boolean isActionPerformed) {
      
      Entity e = context.getEntity();
      if (e==null)
        return;

      
      if (!history.isEmpty() && history.get(index) == e)
        return;

      
      while (history.size()>index+1)
        history.remove(history.size()-1);
      history.add(++index, e);
      
      
      while (history.size()>16) {
        index--;
        history.remove(0);
      }
      
      update();
      
      
    }

    @Override
    public void gedcomEntityAdded(Gedcom gedcom, Entity entity) {
    }

    @Override
    public void gedcomEntityDeleted(Gedcom gedcom, Entity entity) {
      int i = history.indexOf(entity);
      if (i<0)
        return;
      
      history.remove(i);
      if (index>=i)
        index--;
      
      i--;
      if (i<0&&history.size()>0)
        i++;
      if (i>=0)
        fireSelection(history.get(i));
      
      update();
    }

    @Override
    public void gedcomPropertyAdded(Gedcom gedcom, Property property, int pos, Property added) {
    }

    @Override
    public void gedcomPropertyChanged(Gedcom gedcom, Property property) {
    }

    @Override
    public void gedcomPropertyDeleted(Gedcom gedcom, Property property, int pos, Property deleted) {
    }
  }
}
