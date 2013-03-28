
package genj.app;

import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomListener;
import genj.gedcom.GedcomMetaListener;
import genj.gedcom.Property;
import genj.util.Resources;
import genj.util.Trackable;
import genj.util.swing.HeapStatusWidget;
import genj.util.swing.ProgressWidget;
import genj.view.View;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import spin.Spin;


 class StatusBar extends JPanel implements GedcomMetaListener, WorkbenchListener {

  private final static Resources RES = Resources.get(StatusBar.class);

  private int commits;

  private JLabel[] label = new JLabel[Gedcom.ENTITIES.length];
  private JLabel changes = new JLabel("", SwingConstants.RIGHT);
  private HeapStatusWidget heap = new HeapStatusWidget();
  
   StatusBar(Workbench workbench) {

    super(new BorderLayout());

    JPanel panel = new JPanel();
    for (int i = 0; i < Gedcom.ENTITIES.length; i++) {
      label[i] = new JLabel("0", Gedcom.getEntityImage(Gedcom.ENTITIES[i]), SwingConstants.LEFT);
      panel.add(label[i]);
    }
    add(panel, BorderLayout.WEST);
    add(changes, BorderLayout.CENTER);
    add(heap, BorderLayout.EAST);

    workbench.addWorkbenchListener(this);
  }

  public void processStarted(Workbench workbench, Trackable process) {
    remove(2);
    add(new ProgressWidget(process),BorderLayout.EAST);
    revalidate();
    repaint();
  }

  public void processStopped(Workbench workbench, Trackable process) {
    remove(2);
    add(heap,BorderLayout.EAST);
    revalidate();
    repaint();
  }
  
  private void update(Gedcom gedcom) {
    
    for (int i=0;i<Gedcom.ENTITIES.length;i++)  {
      String tag = Gedcom.ENTITIES[i];
      int es = gedcom.getEntities(tag).size();
      int ps = gedcom.getPropertyCount(tag);
      if (ps==0) {
        label[i].setText(Integer.toString(es));
        label[i].setToolTipText(Gedcom.getName(tag, true));
      } else {
        label[i].setText(es + "/" + ps);
        label[i].setToolTipText(Gedcom.getName(tag, true)+" ("+RES.getString("cc.tip.record_inline")+")");
      }
    }
    
    changes.setText(commits>0?RES.getString("stat.commits", new Integer(commits)):"");
  }
  
  public void gedcomWriteLockReleased(Gedcom gedcom) {
    commits++;
    update(gedcom);
  }

  public void gedcomHeaderChanged(Gedcom gedcom) {
  }

  public void gedcomBeforeUnitOfWork(Gedcom gedcom) {
  }

  public void gedcomAfterUnitOfWork(Gedcom gedcom) {
  }

  public void gedcomWriteLockAcquired(Gedcom gedcom) {
  }

  public void gedcomEntityAdded(Gedcom gedcom, Entity entity) {
  }

  public void gedcomEntityDeleted(Gedcom gedcom, Entity entity) {
  }

  public void gedcomPropertyAdded(Gedcom gedcom, Property property, int pos, Property added) {
  }

  public void gedcomPropertyChanged(Gedcom gedcom, Property prop) {
  }

  public void gedcomPropertyDeleted(Gedcom gedcom, Property property, int pos, Property removed) {
  }

  public void commitRequested(Workbench workbench) {
  }

  public void gedcomClosed(Workbench workbench, Gedcom gedcom) {
    gedcom.removeGedcomListener((GedcomListener)Spin.over(this));
    commits = 0;
    for (int i=0;i<Gedcom.ENTITIES.length;i++) 
      label[i].setText("-");
    changes.setText("");
  }


  public void gedcomOpened(Workbench workbench, Gedcom gedcom) {
    gedcom.addGedcomListener((GedcomListener)Spin.over(this));
    update(gedcom);
  }

  public void selectionChanged(Workbench workbench, Context context, boolean isActionPerformed) {
  }

  public void viewClosed(Workbench workbench, View view) {
  }
  
  public void viewRestored(Workbench workbench, View view) {
  }

  public void viewOpened(Workbench workbench, View view) {
  }

  public void workbenchClosing(Workbench workbench) {
  }

} 