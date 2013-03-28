
package net.sourceforge.squirrel_sql.client.update.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;



public class UpdateSummaryTableModel extends AbstractTableModel
{

      private static final long serialVersionUID = 1L;

      private List<ArtifactStatus> _artifacts = new ArrayList<ArtifactStatus>();
      
      
      private static final StringManager s_stringMgr = 
      	StringManagerFactory.getStringManager(UpdateSummaryTableModel.class);   
      
      private interface i18n {
         
         String YES_VAL = s_stringMgr.getString("UpdateSummaryTable.yes");

         
         String NO_VAL = s_stringMgr.getString("UpdateSummaryTable.no");
      }
      
      private final static Class<?>[] s_dataTypes = 
         new Class[] { 
            String.class, 
            String.class, 
            String.class, 
            UpdateSummaryTableActionItem.class, 
      };
      
      private final String[] s_hdgs = new String[] {
         s_stringMgr.getString("UpdateSummaryTable.artifactNameLabel"),
         s_stringMgr.getString("UpdateSummaryTable.typeLabel"),
         s_stringMgr.getString("UpdateSummaryTable.installedLabel"),
         s_stringMgr.getString("UpdateSummaryTable.actionLabel"), };
      
      
      private final int[] s_columnWidths = new int[] { 150, 100, 100, 50 };      
            
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
         	if (as.isCoreArtifact()) {
         		return ArtifactAction.INSTALL;
         	}
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

		
		public int getColumnWidth(int col)
		{
			return s_columnWidths[col];
		}
   
	
}
