package net.sourceforge.squirrel_sql.fw.datasetviewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.*;


import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


public class CellDataPopup
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(CellDataPopup.class);


	
	public static void showDialog(JTable table,
		ColumnDisplayDefinition colDef,
		MouseEvent evt,
		boolean isModelEditable)
	{
		CellDataPopup popup = new CellDataPopup();
		popup.createAndShowDialog(table, evt, colDef, isModelEditable);
	}

	private void createAndShowDialog(JTable table, MouseEvent evt,
		ColumnDisplayDefinition colDef, boolean isModelEditable)
	{
      Point pt = evt.getPoint();
      int row = table.rowAtPoint(pt);
      int col = table.columnAtPoint(pt);

      Object obj = table.getValueAt(row, col);

      
      
      CellEditor editor = table.getCellEditor(row, col);
      if (editor != null)
         editor.cancelCellEditing();

      Component comp = SwingUtilities.getRoot(table);
      Component newComp = null;

      if (false == comp instanceof JFrame)
      {
         
         return;
      }

      
      
      
      
      TextAreaInternalFrame taif =
         new TextAreaInternalFrame((JFrame) comp, table.getColumnName(col), colDef, obj,
            row, col, isModelEditable, table);
      
      
      taif.pack();
      newComp = taif;

      Dimension dim = newComp.getSize();
		boolean dimChanged = false;
		if (dim.width < 300)
		{
			dim.width = 300;
			dimChanged = true;
		}
		if (dim.height < 300)
		{
			dim.height = 300;
			dimChanged = true;
		}
		if (dim.width > 600)
		{
			dim.width = 600;
			dimChanged = true;
		}
		if (dim.height > 500)
		{
			dim.height = 500;
			dimChanged = true;
		}
		if (dimChanged)
		{
			newComp.setSize(dim);
		}
		if (comp instanceof IMainFrame)
		{
			pt = SwingUtilities.convertPoint((Component) evt.getSource(), pt, comp);
			pt.y -= dim.height;
		}
		else
		{
			
			
			Component parent = SwingUtilities.windowForComponent(comp);
			while ((parent != null) &&
				!(parent instanceof IMainFrame) &&
				!(parent.equals(comp)))
			{
				comp = parent;
				parent = SwingUtilities.windowForComponent(comp);
			}
			comp = (parent != null) ? parent : comp;
			pt = SwingUtilities.convertPoint((Component) evt.getSource(), pt, comp);
		}

		if (pt.y < 0)
		{
			pt.y = 0;	
		}

		
		
		
		
		
		int fudgeFactor = 100;
		Rectangle parentBounds = comp.getBounds();
		if (parentBounds.width <= (dim.width + fudgeFactor))
		{
			dim.width = parentBounds.width - fudgeFactor;
			pt.x = fudgeFactor / 2;
			newComp.setSize(dim);
		}
		else
		{
			if ((pt.x + dim.width + fudgeFactor) > (parentBounds.width))
			{
				pt.x -= (pt.x + dim.width + fudgeFactor) - parentBounds.width;
			}
		}

		newComp.setLocation(pt);
		newComp.setVisible(true);
	}


	
	
	
	private static class ColumnDataPopupPanel extends JPanel {

      private static final long serialVersionUID = 1L;
      private final PopupEditableIOPanel ioPanel;
		private JDialog _parentFrame = null;
		private int _row;
		private int _col;
		private JTable _table;

		ColumnDataPopupPanel(Object cellContents,
			ColumnDisplayDefinition colDef,
			boolean tableIsEditable)
		{
			super(new BorderLayout());

			if (tableIsEditable &&
				CellComponentFactory.isEditableInPopup(colDef, cellContents)) {

				
				ioPanel = new PopupEditableIOPanel(colDef, cellContents, true);

				
				
				JPanel editingControls = createPopupEditingControls();
				add(editingControls, BorderLayout.SOUTH);
			}
			else {
				
				ioPanel = new PopupEditableIOPanel(colDef, cellContents, false);
			}

			add(ioPanel, BorderLayout.CENTER);

		}

		
		private JPanel createPopupEditingControls() {

			JPanel panel = new JPanel(new BorderLayout());

			
			JPanel updateControls = new JPanel();

			
			
			JButton updateButton = new JButton(s_stringMgr.getString("cellDataPopUp.updateData"));
			updateButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {

					
					
					StringBuffer messageBuffer = new StringBuffer();
					Object newValue = ColumnDataPopupPanel.this.ioPanel.getObject(messageBuffer);
					if (messageBuffer.length() > 0) {
						

						
						
						
						String msg = s_stringMgr.getString("cellDataPopUp.cannnotBGeConverted", messageBuffer);

						JOptionPane.showMessageDialog(
							ColumnDataPopupPanel.this,
							msg,
							
							s_stringMgr.getString("cellDataPopUp.conversionError"),
							JOptionPane.ERROR_MESSAGE);

						ColumnDataPopupPanel.this.ioPanel.requestFocus();

					}
					else {
						


_table.setValueAt(newValue, _row, _col);
						ColumnDataPopupPanel.this._parentFrame.setVisible(false);
						ColumnDataPopupPanel.this._parentFrame.dispose();
					}
				}
			});

			
			
			JButton cancelButton = new JButton(s_stringMgr.getString("cellDataPopup.cancel"));
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					ColumnDataPopupPanel.this._parentFrame.setVisible(false);
					ColumnDataPopupPanel.this._parentFrame.dispose();
				}
			});

			
			updateControls.add(updateButton);
			updateControls.add(cancelButton);

			
			panel.add(updateControls, BorderLayout.SOUTH);

			return panel;
		}

		
		 public void setUserActionInfo(JDialog parent, int row, int col,
		 	JTable table) {
		 	_parentFrame = parent;
		 	_row = row;
		 	_col = col;
		 	_table = table;
		 }


	}



	
	
	class TextAreaInternalFrame extends JDialog
	{
        private static final long serialVersionUID = 1L;

        public TextAreaInternalFrame(JFrame owner, String columnName, ColumnDisplayDefinition colDef,
			Object value, int row, int col,
			boolean isModelEditable, JTable table)
		{
			
			super(owner, s_stringMgr.getString("cellDataPopup.valueofColumn", columnName), false);
			ColumnDataPopupPanel popup =
				new ColumnDataPopupPanel(value, colDef, isModelEditable);
			popup.setUserActionInfo(this, row, col, table);
			setContentPane(popup);

         AbstractAction closeAction = new AbstractAction()
         {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent actionEvent)
            {
               setVisible(false);
               dispose();
            }
         };
         KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
         getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeStroke, "CloseAction");
         getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "CloseAction");
         getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(escapeStroke, "CloseAction");
         getRootPane().getActionMap().put("CloseAction", closeAction);
		}
	}

}
