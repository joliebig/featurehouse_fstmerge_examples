package net.sourceforge.squirrel_sql.client.update.gui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.sourceforge.squirrel_sql.client.update.UpdateController;
import net.sourceforge.squirrel_sql.fw.gui.SortableTable;
import net.sourceforge.squirrel_sql.fw.gui.SortableTableModel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class UpdateSummaryTable extends SortableTable {
   private static final long serialVersionUID = 1L;

   
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(UpdateSummaryTable.class);

   private interface i18n {
      
      String YES_VAL = s_stringMgr.getString("UpdateSummaryTable.yes");

      
      String NO_VAL = s_stringMgr.getString("UpdateSummaryTable.no");
   }

   private final static String[] s_hdgs = new String[] {
         s_stringMgr.getString("UpdateSummaryTable.artifactNameLabel"),
         s_stringMgr.getString("UpdateSummaryTable.typeLabel"),
         s_stringMgr.getString("UpdateSummaryTable.installedLabel"),
         s_stringMgr.getString("UpdateSummaryTable.actionLabel"), };

   private final static Class<?>[] s_dataTypes = 
      new Class[] { 
         String.class, 
         String.class, 
         String.class, 
         UpdateSummaryTableActionItem.class, 
   };

   private final static int[] s_columnWidths = new int[] { 150, 100, 100, 50 };

   private JComboBox _actionComboBox = new JComboBox();

   private List<ArtifactStatus> _artifacts = null;
   
   private UpdateController _updateController = null;
   
   public UpdateSummaryTable(List<ArtifactStatus> artifactStatus, 
                             UpdateController updateController) {
      super(new UpdateSummaryTableModel(artifactStatus));
      _artifacts = artifactStatus;
      this._updateController = updateController;
      
      setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
      getTableHeader().setResizingAllowed(true);
      getTableHeader().setReorderingAllowed(true);
      setAutoCreateColumnsFromModel(false);
      setAutoResizeMode(AUTO_RESIZE_LAST_COLUMN);

      final TableColumnModel tcm = new DefaultTableColumnModel();
      for (int i = 0; i < s_columnWidths.length; ++i) {
         final TableColumn col = new TableColumn(i, s_columnWidths[i]);
         col.setHeaderValue(s_hdgs[i]);
         if (i == 3) {
            col.setCellEditor(new DefaultCellEditor(initCbo(_actionComboBox)));
         }
         tcm.addColumn(col);
      }
      setColumnModel(tcm);
      initPopup();
      
   }

   
   public List<ArtifactStatus> getUserRequestedChanges() {
      List<ArtifactStatus> changes = new ArrayList<ArtifactStatus>();
      for (ArtifactStatus artifactStatus : _artifacts) {
         if (artifactStatus.getArtifactAction() != ArtifactAction.NONE) {
            changes.add(artifactStatus);
         }
      }
      return changes;
   }
   
   private void initPopup() {
      
      final JPopupMenu popup = new JPopupMenu("Install Options");
      JMenuItem coreItem = new JMenuItem("All core");
      coreItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            for (ArtifactStatus status : _artifacts) {
               if (status.isCoreArtifact()) {
                  status.setArtifactAction(ArtifactAction.INSTALL);
               }
            }
         }
      });
      JMenuItem pluginItem = new JMenuItem("All plugins");
      pluginItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            for (ArtifactStatus status : _artifacts) {
               if (status.isPluginArtifact()) {
                  status.setArtifactAction(ArtifactAction.INSTALL);
               }
            }
         }
      });
      JMenuItem translationItem = new JMenuItem("All translations");
      translationItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            for (ArtifactStatus status : _artifacts) {
               if (status.isTranslationArtifact()) {
                  status.setArtifactAction(ArtifactAction.INSTALL);
               }
            }
         }
      });
      
      JMenuItem allUpdatesItem = new JMenuItem("All updates");
      allUpdatesItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            for (ArtifactStatus status : _artifacts) {
               status.setArtifactAction(ArtifactAction.INSTALL);
            }
         }
      });
      
      popup.add(coreItem);
      popup.add(pluginItem);
      popup.add(translationItem);
      popup.addSeparator();
      popup.add(allUpdatesItem);
      
      addMouseListener(new MouseAdapter() {
         public void mousePressed(MouseEvent event){
          if(popup.isPopupTrigger(event)){
           popup.show(event.getComponent(), event.getX(),event.getY());
          }
         }
         public void mouseReleased(MouseEvent event){
          if(popup.isPopupTrigger(event)){
           popup.show(event.getComponent(), event.getX(),event.getY());
          }
         }
        });            
   }
      
   private JComboBox initCbo(JComboBox cbo) {
      cbo.setEditable(false);

      cbo.addItem(ArtifactAction.NONE);
      cbo.addItem(ArtifactAction.INSTALL);
      cbo.addItem(ArtifactAction.REMOVE);

      cbo.setSelectedIndex(0);

      _actionComboBox.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            final JComboBox source = (JComboBox)e.getSource();
            final ArtifactAction action = 
               (ArtifactAction)(source).getSelectedItem();
            final int row = UpdateSummaryTable.this.getSelectedRow();
            final SortableTableModel model = 
               (SortableTableModel)UpdateSummaryTable.this.getModel();

            
            if (row == -1) {
               return;
            }
            final ArtifactStatus as = UpdateSummaryTable.this._artifacts.get(row);
            if (as.isPluginArtifact()) {
               
               
               System.out.println("Need to ensure that core components are up-to-date");
               return;
            }
            if (as.isCoreArtifact() && action == ArtifactAction.REMOVE) {
               
               _updateController.showErrorMessage("Illegal Action", 
                                                  "Core artifacts cannot be removed");
               source.setSelectedIndex(0);
               model.fireTableDataChanged();
               return;
            }
            
            if (as.isCoreArtifact()) {
               for (ArtifactStatus status : UpdateSummaryTable.this._artifacts) {
                  if (status.isInstalled() || status.isCoreArtifact()) {
                     status.setArtifactAction(action);
                  }
               }
            }
            as.setArtifactAction(action);
            model.fireTableDataChanged();  
         }
      });
      
      return cbo;
   }

   private static class UpdateSummaryTableModel extends AbstractTableModel {
      private static final long serialVersionUID = 1L;

      private List<ArtifactStatus> _artifacts = new ArrayList<ArtifactStatus>();

      UpdateSummaryTableModel(List<ArtifactStatus> artifacts) {
         _artifacts = artifacts;

      }

      public Object getValueAt(int row, int col) {
         final ArtifactStatus as = _artifacts.get(row);
         switch (col) {
         case 0:
            return as.getName();
         case 1:
            return as.getType();
         case 2:
            return as.isInstalled() ? i18n.YES_VAL : i18n.NO_VAL;
         case 3:
            return as.getArtifactAction();
         default:
            throw new IndexOutOfBoundsException("" + col);
         }
      }

      public int getRowCount() {
         return _artifacts.size();
      }

      public int getColumnCount() {
         return s_hdgs.length;
      }

      public String getColumnName(int col) {
         return s_hdgs[col];
      }

      public Class<?> getColumnClass(int col) {
         return s_dataTypes[col];
      }

      public boolean isCellEditable(int row, int col) {
         return col == 3;
      }

      public void setValueAt(Object value, int row, int col) {


         final ArtifactStatus as = _artifacts.get(row);
         ArtifactAction action = 
            ArtifactAction.valueOf(value.toString()); 
         as.setArtifactAction(action);
      }
   }
}
