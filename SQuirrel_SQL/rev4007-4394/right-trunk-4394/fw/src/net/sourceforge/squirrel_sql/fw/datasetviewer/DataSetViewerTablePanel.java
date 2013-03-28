package net.sourceforge.squirrel_sql.fw.datasetviewer;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.JOptionPane;




import net.sourceforge.squirrel_sql.fw.gui.ButtonTableHeader;
import net.sourceforge.squirrel_sql.fw.gui.SortableTableModel;
import net.sourceforge.squirrel_sql.fw.gui.TablePopupMenu;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.RestorableJTextField;


 
import java.awt.print.Printable;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.Graphics2D;
import java.awt.Color;
import javax.swing.table.JTableHeader;
import java.awt.Font;

public class DataSetViewerTablePanel extends BaseDataSetViewerDestination
				implements IDataSetTableControls, Printable
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DataSetViewerTablePanel.class);

	private ILogger s_log = LoggerController.createLogger(DataSetViewerTablePanel.class);

	private MyJTable _table = null;
	private MyTableModel _typedModel;
	private IDataSetUpdateableModel _updateableModel;

	public DataSetViewerTablePanel()
	{
		super();
	}

	public void init(IDataSetUpdateableModel updateableModel)
	{
		_table = new MyJTable(this, updateableModel);
		_updateableModel = updateableModel;
	}
	
	public IDataSetUpdateableModel getUpdateableModel()
	{
		return _updateableModel;
	}

	public void clear()
	{
		_typedModel.clear();
		_typedModel.fireTableDataChanged();
	}
	

	public void setColumnDefinitions(ColumnDisplayDefinition[] colDefs)
	{
		super.setColumnDefinitions(colDefs);
		_table.setColumnDefinitions(colDefs);
	}

	public void moveToTop()
	{
		if (_table.getRowCount() > 0)
		{
			_table.setRowSelectionInterval(0, 0);
		}
	}

	
	public Component getComponent()
	{
		return _table;
	}

	
	protected void addRow(Object[] row)
	{
		_typedModel.addRow(row);
	}
	
	
	protected Object[] getRow(int row)
	{
		Object values[] = new Object[_typedModel.getColumnCount()];
		for (int i=0; i < values.length; i++)
			values[i] = _typedModel.getValueAt(row, i);
		return values;
	}

	
	protected void allRowsAdded()
	{
		_typedModel.fireTableStructureChanged();
      _table.initColWidths();
   }

	
	public int getRowCount()
	{
		return _typedModel.getRowCount();
	}

	public void setShowRowNumbers(boolean showRowNumbers)
	{
		_table.setShowRowNumbers(showRowNumbers);
	}


	
	protected final class MyJTable extends JTable
	{
		private static final long serialVersionUID = 1L;
		private final int _multiplier;
		private static final String data = "THE QUICK BROWN FOX JUMPED OVER THE LAZY DOG";

		private TablePopupMenu _tablePopupMenu;
		private IDataSetTableControls _creator;
		private Point _dragBeginPoint = null;
		private Point _dragEndPoint = null;
		private RowNumberTableColumn _rntc;
		private ButtonTableHeader _tableHeader = new ButtonTableHeader();

		MyJTable(IDataSetTableControls creator,
					IDataSetUpdateableModel updateableObject)
		{
			super(new SortableTableModel(new MyTableModel(creator)));
			_creator = creator;
			_typedModel = (MyTableModel) ((SortableTableModel) getModel()).getActualModel();
			_multiplier =
				getFontMetrics(getFont()).stringWidth(data) / data.length();
			setRowHeight(getFontMetrics(getFont()).getHeight());
			boolean allowUpdate = false;
			
			
			
			
			if (updateableObject != null && ! creator.isTableEditable())
				allowUpdate = true;
			createGUI(allowUpdate, updateableObject);

			
			_creator.setCellEditors(this);

			

			addMouseListener(new MouseAdapter()
			{
				public void mousePressed(MouseEvent e)
				{
					_dragBeginPoint = e.getPoint();
				}

				public void mouseReleased(MouseEvent e)
				{
					_dragBeginPoint = null;
					_dragEndPoint = null;
					repaint();
				}
			});

			addMouseMotionListener(new MouseMotionAdapter()
			{
				public void mouseDragged(MouseEvent e)
				{
					onMouseDragged(e);
					repaint();
				}
			});

		}

		private void onMouseDragged(MouseEvent e)
		{
			if(null == _dragBeginPoint)
			{
				_dragBeginPoint = e.getPoint();
			}

			_dragEndPoint = e.getPoint();
		}

		public void paint(Graphics g)
		{
			super.paint(g);

			if(null != _dragBeginPoint && null != _dragEndPoint && false == _dragBeginPoint.equals(_dragEndPoint))
			{
				int x = Math.min(_dragBeginPoint.x,  _dragEndPoint.x);
				int y = Math.min(_dragBeginPoint.y,  _dragEndPoint.y);
				int width = Math.abs(_dragBeginPoint.x - _dragEndPoint.x);
				int heigh = Math.abs(_dragBeginPoint.y - _dragEndPoint.y);

				Color colBuf = g.getColor();
				g.setColor(getForeground());
				g.drawRect(x,y,width,heigh);
				g.setColor(colBuf);
			}
		}

		public IDataSetTableControls getCreator() {
			return _creator;
		}

		
		public TableCellEditor getCellEditor(int row, int col)
		{
			TableCellEditor cellEditor = super.getCellEditor(row, col);
			currentCellEditor = (DefaultCellEditor)cellEditor;
			return cellEditor;
		}


		
		public void processKeyEvent(KeyEvent e) {

				
				if (e.getKeyChar() == '\b' && getEditorComponent() != null &&
						((RestorableJTextField)getEditorComponent()).getText().equals("<null>") ) {
						
						return;
				}

				
				
				
				super.processKeyEvent(e);

				
				
				if (getEditorComponent() != null) {
						if (e.getID() == KeyEvent.KEY_TYPED && ((RestorableJTextField)getEditorComponent()).getText().length() == 7) {
								
								if (((RestorableJTextField)getEditorComponent()).getText().equals("<null>"+e.getKeyChar())) {
										
										((RestorableJTextField)getEditorComponent()).updateText(""+e.getKeyChar());
								}
						}
				}

		}


		
		public void setValueAt(Object newValueString, int row, int col)
		{
			if (! (newValueString instanceof java.lang.String))
			{
				
				super.setValueAt(newValueString, row, col);
				return;
			}

			
			StringBuffer messageBuffer = new StringBuffer();

			int modelIndex = getColumnModel().getColumn(col).getModelIndex();
			ColumnDisplayDefinition colDef = getColumnDefinitions()[modelIndex];
			Object newValueObject = CellComponentFactory.validateAndConvert(
				colDef, getValueAt(row, col), (String) newValueString, messageBuffer);

			if (messageBuffer.length() > 0)
			{

				
				String msg = s_stringMgr.getString("dataSetViewerTablePanel.textCantBeConverted", messageBuffer);

				if (s_log.isDebugEnabled()) {
					s_log.debug("setValueAt: msg from DataTypeComponent was: "+msg);
				}
				
				
				JOptionPane.showMessageDialog(this,
					msg,
					
					s_stringMgr.getString("dataSetViewerTablePanel.conversionError"),
					JOptionPane.ERROR_MESSAGE);
				
			}
			else
			{
				
				super.setValueAt(newValueObject, row, col);
			}
		}


		public void setColumnDefinitions(ColumnDisplayDefinition[] colDefs)
		{
			TableColumnModel tcm = createColumnModel(colDefs);
			setColumnModel(tcm);
			_typedModel.setHeadings(colDefs);

			
			_creator.setCellEditors(this);
			_tablePopupMenu.reset();
		}

		MyTableModel getTypedModel()
		{
			return _typedModel;
		}

		
		private void displayPopupMenu(MouseEvent evt)
		{
			_tablePopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
		}


		private TableColumnModel createColumnModel(ColumnDisplayDefinition[] colDefs)
		{
			
			TableColumnModel cm = new DefaultTableColumnModel();

			_rntc = new RowNumberTableColumn();

			for (int i = 0; i < colDefs.length; ++i)
			{
				ColumnDisplayDefinition colDef = colDefs[i];
				int colWidth = colDef.getDisplayWidth() * _multiplier;
				if (colWidth > MAX_COLUMN_WIDTH * _multiplier)
				{
					colWidth = MAX_COLUMN_WIDTH * _multiplier;
				}
				else if (colWidth < MIN_COLUMN_WIDTH * _multiplier)
				{
					  colWidth = MIN_COLUMN_WIDTH * _multiplier;
				}

				ExtTableColumn col = new ExtTableColumn(i, colWidth,
					CellComponentFactory.getTableCellRenderer(colDefs[i]), null);
				col.setHeaderValue(colDef.getLabel());
				col.setColumnDisplayDefinition(colDef);
				cm.addColumn(col);
			}

			return cm;
		}

		void setShowRowNumbers(boolean show)
		{
			try
			{
				int rowNumColIx = getColumnModel().getColumnIndex(RowNumberTableColumn.ROW_NUMBER_COL_IDENTIFIER);
				_tableHeader.columnIndexWillBeRemoved(rowNumColIx);
			}
			catch(IllegalArgumentException e)
			{
				
			}

			getColumnModel().removeColumn(_rntc);
			if(show)
			{
				_tableHeader.columnIndexWillBeAdded(0);
				getColumnModel().addColumn(_rntc);
				getColumnModel().moveColumn(getColumnModel().getColumnCount()-1, 0);
			}
		}

		private void createGUI(boolean allowUpdate,
									  IDataSetUpdateableModel updateableObject)
		{
			setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			setRowSelectionAllowed(false);
			setColumnSelectionAllowed(false);
			setCellSelectionEnabled(true);
			getTableHeader().setResizingAllowed(true);
			getTableHeader().setReorderingAllowed(true);
			setAutoCreateColumnsFromModel(false);
			setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			setTableHeader(_tableHeader);
			_tableHeader.setTable(this);

			_tablePopupMenu = new TablePopupMenu(allowUpdate, updateableObject,
				DataSetViewerTablePanel.this);
			_tablePopupMenu.setTable(this);

         addMouseListener(new MouseAdapter()
         {
            public void mousePressed(MouseEvent evt)
            {
               onMousePressed(evt, false);
            }

            public void mouseReleased(MouseEvent evt)
            {
               onMouseReleased(evt);
            }
         });

         getTableHeader().addMouseListener(new MouseAdapter()
         {
            public void mousePressed(MouseEvent evt)
            {
               onMousePressed(evt, true);
            }

            public void mouseReleased(MouseEvent evt)
            {
               onMouseReleased(evt);
            }
         });

      }

      private void onMouseReleased(MouseEvent evt)
      {
         if (evt.isPopupTrigger())
         {
            this.displayPopupMenu(evt);
         }
      }

      private void onMousePressed(MouseEvent evt, boolean clickedOnTableHeader)
      {
         if (evt.isPopupTrigger())
         {
            this.displayPopupMenu(evt);
         }
         else if (evt.getClickCount() == 2 && false == clickedOnTableHeader)
         {
            
            
            

            
            
            Point pt = evt.getPoint();
            TableColumnModel cm = this.getColumnModel();
            int columnIndexAtX = cm.getColumnIndexAtX(pt.x);

            int modelIndex = cm.getColumn(columnIndexAtX).getModelIndex();


            if (RowNumberTableColumn.ROW_NUMBER_MODEL_INDEX != modelIndex)
            {
               ColumnDisplayDefinition colDefs[] = getColumnDefinitions();
               CellDataPopup.showDialog(this, colDefs[modelIndex], evt, this._creator.isTableEditable());

            }
         }
      }

      public void initColWidths()
      {
         _tableHeader.initColWidths();
      }
   }


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public boolean isTableEditable() {
		return false;
	}
	
	
	public boolean isColumnEditable(int col, Object originalValue) {
		return false;
	}

	
	public boolean needToReRead(int col, Object originalValue) {
		
		return CellComponentFactory.needToReRead(_colDefs[col], originalValue);
	}
	
	
	public Object reReadDatum(Object[] values, int col, StringBuffer message) {
		
		return ((IDataSetUpdateableTableModel)_updateableModel).
			reReadDatum(values, _colDefs, col, message);
	}
	
	
	public void setCellEditors(JTable table) {}
	
	
	public int[] changeUnderlyingValueAt(
		int row,
		int col,
		Object newValue,
		Object oldValue)
	{
		return new int[0];	
	}
	
	
	public void deleteRows(int[] rows) {}	
	
	
	public void insertRow() {}	
	
	

	
	
	

                                                                                
	
	
	
	JTableHeader tableHeader;
	int [] subTableSplit = null;
	boolean pageinfoCalculated=false;
	int totalNumPages=0;
	int prevPageIndex = 0;
	int subPageIndex = 0;
	int subTableSplitSize = 0;
	double tableHeightOnFullPage, headerHeight;
	double pageWidth, pageHeight;
	int fontHeight, fontDesent;
	double tableHeight, rowHeight;
	double scale = 8.0/12.0;        


	
	public int print(Graphics g, PageFormat pageFormat, int pageIndex)
			throws PrinterException {

		Graphics2D g2=(Graphics2D)g;
		
		
		if (pageIndex==0)
			pageinfoCalculated = false;
		
		if(!pageinfoCalculated) {
			getPageInfo(g, pageFormat);
		}
 
		g2.setColor(Color.black);
		if(pageIndex>=totalNumPages) {
			return NO_SUCH_PAGE;
		}
		if (prevPageIndex != pageIndex) {
			subPageIndex++;
			if( subPageIndex == subTableSplitSize -1) {
					subPageIndex=0;
			}
		}
 
		g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
 
		int rowIndex = pageIndex/ (subTableSplitSize -1);
         
		printTablePart(g2, pageFormat, rowIndex, subPageIndex);
		prevPageIndex= pageIndex;
 
		return Printable.PAGE_EXISTS;
	}
 
 
	
	public void getPageInfo(Graphics g, PageFormat pageFormat) {
 
		subTableSplit = null;
		subTableSplitSize = 0;
		subPageIndex = 0;
		prevPageIndex = 0;
 
		fontHeight=(int)(g.getFontMetrics().getHeight() * scale);
		fontDesent=(int)(g.getFontMetrics().getDescent() * scale);
 
		tableHeader = _table.getTableHeader();

		headerHeight = tableHeader.getHeight() +_table.getRowMargin() * scale;
 
		pageHeight = pageFormat.getImageableHeight();
		pageWidth =  pageFormat.getImageableWidth();
 

		tableHeight = _table.getHeight() * scale;
		rowHeight = _table.getRowHeight() + _table.getRowMargin() * scale;
 
		tableHeightOnFullPage = (int)(pageHeight - headerHeight - fontHeight*2);
		tableHeightOnFullPage = tableHeightOnFullPage/rowHeight * rowHeight;
 
		TableColumnModel tableColumnModel = tableHeader.getColumnModel();
		int columns = tableColumnModel.getColumnCount();
		int columnMargin = (int)(tableColumnModel.getColumnMargin() * scale);
 
		int [] temp = new int[columns];
		int columnIndex = 0;
		temp[0] = 0;
		int columnWidth;
		int length = 0;
		subTableSplitSize = 0;
		while ( columnIndex < columns ) {
 
			columnWidth = (int)(tableColumnModel.getColumn(columnIndex).getWidth() * scale);
 
			if ( length + columnWidth + columnMargin > pageWidth ) {
				temp[subTableSplitSize+1] = temp[subTableSplitSize] + length;
				length = columnWidth;
				subTableSplitSize++;
			}
			else {
				length += columnWidth + columnMargin;
			}
			columnIndex++;
		} 
 
		if ( length > 0 )  {  
		   temp[subTableSplitSize+1] = temp[subTableSplitSize] + length;
		   subTableSplitSize++;
		}
 
		subTableSplitSize++;
		subTableSplit = new int[subTableSplitSize];
		for ( int i=0; i < subTableSplitSize; i++ ) {
			subTableSplit[i]= temp[i];
		}
		totalNumPages = (int)(tableHeight/tableHeightOnFullPage);
		if ( tableHeight%tableHeightOnFullPage >= rowHeight ) { 
			totalNumPages++;
		}
 
		totalNumPages *= (subTableSplitSize-1);
		pageinfoCalculated = true;
	}
 
	
	public void printTablePart(Graphics2D g2, PageFormat pageFormat, int rowIndex, int columnIndex) {
 
		String pageNumber = "Page: "+(rowIndex+1);
		if ( subTableSplitSize > 1 ) {
			pageNumber += "-" + (columnIndex+1);
		}
 
		int pageLeft = subTableSplit[columnIndex];
		int pageRight = subTableSplit[columnIndex + 1];
 
		int pageWidth =  pageRight-pageLeft;
 
 
		
		g2.setFont(new Font(g2.getFont().getName(), g2.getFont().getStyle(), 8));
		g2.drawString(pageNumber,  pageWidth/2-35, (int)(pageHeight - fontHeight));
 
		double clipHeight = Math.min(tableHeightOnFullPage, tableHeight - rowIndex*tableHeightOnFullPage);
 
		g2.translate(-subTableSplit[columnIndex], 0);
		g2.setClip(pageLeft ,0, pageWidth, (int)headerHeight);
 
		g2.scale(scale, scale);
		tableHeader.paint(g2);   
		g2.scale(1/scale, 1/scale);
		g2.translate(0, headerHeight);
		g2.translate(0,  -tableHeightOnFullPage*rowIndex);
 
		
 
		g2.setClip(pageLeft, (int)tableHeightOnFullPage*rowIndex, pageWidth, (int)clipHeight);
		g2.scale(scale, scale);
		_table.paint(g2);
		g2.scale(1/scale, 1/scale);
 
		double pageTop =  tableHeightOnFullPage*rowIndex - headerHeight;

		g2.drawRect(pageLeft, (int)pageTop, pageWidth, (int)(clipHeight+ headerHeight));
	}
	
	
	
	
	
}
