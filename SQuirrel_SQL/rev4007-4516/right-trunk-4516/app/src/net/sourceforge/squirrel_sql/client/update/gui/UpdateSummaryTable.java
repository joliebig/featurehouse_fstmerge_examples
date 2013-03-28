package net.sourceforge.squirrel_sql.client.update.gui;


import static net.sourceforge.squirrel_sql.client.update.gui.ArtifactAction.INSTALL;
import static net.sourceforge.squirrel_sql.client.update.gui.ArtifactAction.NONE;
import static net.sourceforge.squirrel_sql.client.update.gui.ArtifactAction.REMOVE;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.sourceforge.squirrel_sql.fw.gui.SortableTable;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class UpdateSummaryTable extends SortableTable {

	private static final long serialVersionUID = 1L;

   
   private static final StringManager s_stringMgr = 
   	StringManagerFactory.getStringManager(UpdateSummaryTable.class);
   
   private interface i18n {
   	String ALL_TRANSLATIONS_LABEL = "All translations";
   	String ALL_PLUGINS_LABEL = "All plugins";
   	String INSTALL_OPTIONS_LABEL = "Install Options";
   }

   private List<ArtifactStatus> _artifacts = null;
   private boolean _releaseVersionWillChange = false;
   private UpdateSummaryTableModel _model = null;
   
   public UpdateSummaryTable(List<ArtifactStatus> artifactStatus, 
                             UpdateSummaryTableModel model) {
      super(model);
      _model = model;
      model.setTable(this);
      _artifacts = artifactStatus;
      setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
      getTableHeader().setResizingAllowed(true);
      getTableHeader().setReorderingAllowed(true);
      setAutoCreateColumnsFromModel(false);
      setAutoResizeMode(AUTO_RESIZE_LAST_COLUMN);

      final TableColumnModel tcm = new DefaultTableColumnModel();
      int[] columnWidths = model.getColumnWidths();
      String[] headerNames = model.getColumnHeaderNames(); 
      JComboBox _actionComboBox = new JComboBox();
		for (int i = 0; i < columnWidths.length; ++i) {
         final TableColumn col = new TableColumn(i, columnWidths[i]);
         col.setHeaderValue(headerNames[i]);
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

	
	public void setReleaseVersionWillChange(boolean releaseVersionWillChange)
	{
		Iterator<ArtifactStatus> i = _artifacts.iterator();
		_releaseVersionWillChange = releaseVersionWillChange;
		if (releaseVersionWillChange) {
			
			while (i.hasNext()) {
				ArtifactStatus status = i.next();
				if (status.isInstalled()) {
					status.setArtifactAction(ArtifactAction.INSTALL);
				}
			}
			
		} else {
			
			while (i.hasNext()) {
				ArtifactStatus status = i.next();
				if (status.isCoreArtifact()) {
					i.remove();
				}
			}
			
		}
	}
	
	
	public boolean getReleaseVersionWillChange() {
		return _releaseVersionWillChange;
	}
   
   
   private void initPopup() {
      final JPopupMenu popup = new JPopupMenu(i18n.INSTALL_OPTIONS_LABEL);
      
      JMenuItem pluginItem = new JMenuItem(i18n.ALL_PLUGINS_LABEL);
      pluginItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            for (ArtifactStatus status : _artifacts) {
               if (status.isPluginArtifact()) {
                  status.setArtifactAction(ArtifactAction.INSTALL);
               }
            }
            _model.fireTableDataChanged();
         }
      });
      JMenuItem translationItem = new JMenuItem(i18n.ALL_TRANSLATIONS_LABEL);
      translationItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            for (ArtifactStatus status : _artifacts) {
               if (status.isTranslationArtifact()) {
                  status.setArtifactAction(ArtifactAction.INSTALL);
               }
            }
            _model.fireTableDataChanged();
         }
      });
                  
      popup.add(pluginItem);
      popup.add(translationItem);
      
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
      
   private JComboBox initCbo(final JComboBox cbo) {
      cbo.setEditable(false);
      setModel(cbo, NONE, INSTALL, REMOVE);
      
      cbo.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuCanceled(PopupMenuEvent e) {}
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e){}
			public void popupMenuWillBecomeVisible(PopupMenuEvent e)
			{
				JComboBox source =(JComboBox) e.getSource();
				updateDataModel(source);
			}
      });
      
      return cbo;
   }
   
   
   private void updateDataModel(JComboBox source) {
		final int row = UpdateSummaryTable.this.getEditingRow();
		if (row == -1) {
			return;
		}
		final ArtifactStatus as = UpdateSummaryTable.this._artifacts.get(row);
		
		
		boolean installed = as.isInstalled();

		
		if (as.isCoreArtifact()) {
			if (_releaseVersionWillChange) {
				source.setModel(getComboBoxModel(INSTALL));
			} else {
				
			}
		} else {
			if (_releaseVersionWillChange) {
				if (installed) {
					setModel(source, INSTALL, REMOVE);
				} else {
					setModel(source, NONE, INSTALL);
				}
			} else {
				if (installed) {
					setModel(source, NONE, REMOVE);
				} else {
					setModel(source, NONE, INSTALL);
				}
			}
		}   
   }
   
   private void setModel(JComboBox box, ArtifactAction... actions) {
   	ComboBoxModel oldModel = box.getModel();
   	box.setModel(getComboBoxModel(actions));
   	if (oldModel.getSize() != actions.length) {
   		box.firePropertyChange("itemCount", oldModel.getSize(), actions.length);
   	}
   }
   
   private ComboBoxModel getComboBoxModel(ArtifactAction... actions) {
   	ComboBoxModel result = new DefaultComboBoxModel(actions);
   	result.setSelectedItem(actions[0]);
   	return result;
   }

}