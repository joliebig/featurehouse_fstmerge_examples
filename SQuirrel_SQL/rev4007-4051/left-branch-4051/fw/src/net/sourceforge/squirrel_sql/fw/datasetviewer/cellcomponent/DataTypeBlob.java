package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import java.awt.event.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Blob;
import java.io.ByteArrayInputStream;

import net.sourceforge.squirrel_sql.fw.gui.RightLabel;
import net.sourceforge.squirrel_sql.fw.gui.ReadTypeCombo;
import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.gui.OkJPanel;

import net.sourceforge.squirrel_sql.fw.datasetviewer.CellDataPopup;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;



public class DataTypeBlob extends BaseDataTypeComponent
	implements IDataTypeComponent
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DataTypeBlob.class);

	
	private boolean _isNullable;

	
	private JTable _table;

	
	private IRestorableTextComponent _textComponent;

	
	
	
	
	
	private DefaultColumnRenderer _renderer = DefaultColumnRenderer.getInstance();

	
	private static final String thisClassName =
		"net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeBlob";


	
	private static int LARGE_COLUMN_DEFAULT_READ_LENGTH = 255;

	
	 
	 private static boolean propertiesAlreadyLoaded = false;


	
	private static boolean _readBlobs = false;

	
	private static boolean _readCompleteBlobs = false;

	
	private static int _readBlobsSize = LARGE_COLUMN_DEFAULT_READ_LENGTH;





	
	public DataTypeBlob(JTable table, ColumnDisplayDefinition colDef) {
		_table = table;
		_colDef = colDef;
		_isNullable = colDef.isNullable();

		loadProperties();
	}

	
	private static void loadProperties() {

		
		
		
		if (propertiesAlreadyLoaded == false) {
			
			_readBlobs = false;	
			String readBlobsString = DTProperties.get(
				thisClassName, "readBlobs");
			if (readBlobsString != null && readBlobsString.equals("true"))
				_readBlobs = true;

			_readCompleteBlobs = false;	
			String readCompleteBlobsString = DTProperties.get(
				thisClassName, "readCompleteBlobs");
			if (readCompleteBlobsString != null && readCompleteBlobsString.equals("true"))
				_readCompleteBlobs = true;

			_readBlobsSize = LARGE_COLUMN_DEFAULT_READ_LENGTH;	
			String readBlobsSizeString = DTProperties.get(
				thisClassName, "readBlobsSize");
			if (readBlobsSizeString != null)
				_readBlobsSize = Integer.parseInt(readBlobsSizeString);

			propertiesAlreadyLoaded = true;
		}
	}

	
	public String getClassName() {
		return "net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.BlobDescriptor";
	}

	
	public boolean areEqual(Object obj1, Object obj2) {
		if (obj1 == obj2)
			return true;

		
		
		
		
		
		if (obj1 != null)
			return ((BlobDescriptor)obj1).equals((BlobDescriptor)obj2);
		else
			return ((BlobDescriptor)obj2).equals((BlobDescriptor)obj1);
	}

	

	
	public String renderObject(Object value) {
		return (String)_renderer.renderObject(value);
	}

	
	public boolean isEditableInCell(Object originalValue) {
		return wholeBlobRead((BlobDescriptor)originalValue);
	}

	
	public boolean needToReRead(Object originalValue) {
		
		
		
		
		
		
		
		
		
		
		return false;
	}

	
	public JTextField getJTextField() {
		_textComponent = new RestorableJTextField();

		
		((RestorableJTextField)_textComponent).addKeyListener(new KeyTextHandler());

		
		
		
		
		
		
		((RestorableJTextField)_textComponent).addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent evt)
			{
				if (evt.getClickCount() == 2)
				{
					MouseEvent tableEvt = SwingUtilities.convertMouseEvent(
						(RestorableJTextField)DataTypeBlob.this._textComponent,
						evt, DataTypeBlob.this._table);
					CellDataPopup.showDialog(DataTypeBlob.this._table,
						DataTypeBlob.this._colDef, tableEvt, true);
				}
			}
		});	

		return (JTextField)_textComponent;
	}

	
	public Object validateAndConvert(String value, Object originalValue, StringBuffer messageBuffer) {
		
		if (value.equals("<null>"))
			return null;

		

		
		Byte[] byteClassData;
		try {
					byteClassData = BinaryDisplayConverter.convertToBytes(value,
						BinaryDisplayConverter.HEX, false);
		}
		catch (Exception e) {
			messageBuffer.append(e.toString()+"\n");
			
			
			return null;
		}

		byte[] byteData = new byte[byteClassData.length];
		for (int i=0; i<byteClassData.length; i++)
			byteData[i] = byteClassData[i].byteValue();

		
		
		
		
		BlobDescriptor bdesc;
		if (originalValue == null) {
			
			bdesc = new BlobDescriptor(null, byteData, true, true, 0);
		}
		else {
			
			bdesc = (BlobDescriptor)originalValue;

			
			
			bdesc = new BlobDescriptor(bdesc.getBlob(), byteData, true, true, 0);
		}
		return bdesc;

	}

	
	public boolean useBinaryEditingPanel() {
		return true;
	}


	

	
	public boolean isEditableInPopup(Object originalValue) {
		
		
		return isEditableInCell(originalValue);
	}

	
	 public JTextArea getJTextArea(Object value) {
		_textComponent = new RestorableJTextArea();


		
		
		((RestorableJTextArea)_textComponent).setText(renderObject(value));

		
		((RestorableJTextArea)_textComponent).addKeyListener(new KeyTextHandler());

		return (RestorableJTextArea)_textComponent;
	 }

	
	public Object validateAndConvertInPopup(String value, Object originalValue, StringBuffer messageBuffer) {
		return validateAndConvert(value, originalValue, messageBuffer);
	}

	

	
	 private class KeyTextHandler extends BaseKeyTextHandler {
		 public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();

				
				
				
				JTextComponent _theComponent = (JTextComponent)DataTypeBlob.this._textComponent;
				String text = _theComponent.getText();


				
				
				

				if ( DataTypeBlob.this._isNullable) {

					
					if (text.equals("<null>")) {
						if ((c==KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
							
							DataTypeBlob.this._textComponent.restoreText();
							e.consume();
						}
						else {
							
							DataTypeBlob.this._textComponent.updateText("");
							
						}
					}
					else {
						
						if ((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
							if (text.length() <= 1 ) {
								
								DataTypeBlob.this._textComponent.updateText("<null>");
								e.consume();
							}
						}
					}
				}
				else {
                    
                    
                    handleNotNullableField(text, c, e, _textComponent);
				}
			}
		}

	
	private boolean wholeBlobRead(BlobDescriptor bdesc) {
		if (bdesc == null)
			return true;	

		if (bdesc.getWholeBlobRead())
			return true;	

		
		try {
			byte[] data = bdesc.getBlob().getBytes(1, (int)bdesc.getBlob().length());

			
			bdesc.setBlobRead(true);
			bdesc.setData(data);
			bdesc.setWholeBlobRead(true);
			bdesc.setUserSetBlobLimit(0);

			
			 return true;
		}
		catch (Exception ex) {
			bdesc.setBlobRead(false);
			bdesc.setWholeBlobRead(false);
			bdesc.setData(null);
			
			
			return false;
		}
	}

	

	public Object readResultSet(ResultSet rs, int index, boolean limitDataRead)
		throws java.sql.SQLException {

		return staticReadResultSet(rs, index);
	}

	 
	public static Object staticReadResultSet(ResultSet rs, int index)
		throws java.sql.SQLException {

		
		
		
		
		Blob blob = rs.getBlob(index);

		if (rs.wasNull())
			return null;

		
		
		if (_readBlobs)
		{
			
			byte[] blobData = null;
			if (blob != null)
			{
				int len = (int)blob.length();
				if (len > 0)
				{
					int charsToRead = len;
					if (! _readCompleteBlobs)
					{
						charsToRead = _readBlobsSize;
					}
					if (charsToRead > len)
					{
						charsToRead = len;
					}
					blobData = blob.getBytes(1, charsToRead);
				}
			}

			
			boolean wholeBlobRead = false;
			if (_readCompleteBlobs ||
				blobData.length < _readBlobsSize)
				wholeBlobRead = true;

			return new BlobDescriptor(blob, blobData, true, wholeBlobRead,
				_readBlobsSize);
		}
		else
		{
			
			return new BlobDescriptor(blob, null, false, false, 0);
		}

	}

	
	public String getWhereClauseValue(Object value, ISQLDatabaseMetaData md) {
		if (value == null || ((BlobDescriptor)value).getData() == null)
			return _colDef.getLabel() + " IS NULL";
		else
			return "";	
	}


	
	public void setPreparedStatementValue(PreparedStatement pstmt, Object value, int position)
		throws java.sql.SQLException {
		if (value == null || ((BlobDescriptor)value).getData() == null) {
			pstmt.setNull(position, _colDef.getSqlType());
		}
		else {
			
			BlobDescriptor bdesc = (BlobDescriptor)value;

			
			
			
			
			
			pstmt.setBinaryStream(position, new ByteArrayInputStream(bdesc.getData()), bdesc.getData().length);
		}
	}

	
	public Object getDefaultValue(String dbDefaultValue) {
		if (dbDefaultValue != null) {
			
			StringBuffer mbuf = new StringBuffer();
			Object newObject = validateAndConvert(dbDefaultValue, null, mbuf);

			
			
			
			if (mbuf.length() == 0)
				return newObject;
		}

		
		if (_isNullable)
			return null;

		
		return null;
	}


	


	 
	 public boolean canDoFileIO() {
		 return true;
	 }

	 
	public String importObject(FileInputStream inStream)
		throws IOException {


		int fileSize = inStream.available();

		byte[] buf = new byte[fileSize];

		int count = inStream.read(buf);

		if (count != fileSize) {
			throw new IOException(
				"Could read only "+ count +
				" bytes from a total file size of " + fileSize +
				". Import failed.");
		}
		
		Byte[] bBytes = new Byte[count];
		for (int i=0; i<count; i++) {
			bBytes[i] = Byte.valueOf(buf[i]);
		}

		
		return BinaryDisplayConverter.convertToString(bBytes,
			BinaryDisplayConverter.HEX, false);
	}


	 
	public void exportObject(FileOutputStream outStream, String text)
		throws IOException {

		Byte[] bBytes = BinaryDisplayConverter.convertToBytes(text,
			BinaryDisplayConverter.HEX, false);

		
		StringBuffer messageBuffer = new StringBuffer();
		validateAndConvertInPopup(text, null, messageBuffer);
		if (messageBuffer.length() > 0) {
			
			throw new IOException(new String(messageBuffer));
		}

		
		byte[] bytes = new byte[bBytes.length];
		for (int i=0; i<bytes.length; i++)
			bytes[i] = bBytes[i].byteValue();

		
		outStream.write(bytes);
		outStream.flush();
		outStream.close();
	}


	

	 
	 public static OkJPanel getControlPanel() {

		

		 
		 
		 loadProperties();

		return new BlobOkJPanel();
	 }



	 
	 private static class BlobOkJPanel extends OkJPanel {

	    private static final long serialVersionUID = 2859310264477848330L;

        
		
	    private JCheckBox _showBlobChk = new JCheckBox(
		
		s_stringMgr.getString("dataTypeBlob.readOnFirstLoad"));

		
		
		private RightLabel _typeDropLabel = new RightLabel(s_stringMgr.getString("dataTypeBlob.read"));

		
		private ReadTypeCombo _blobTypeDrop = new ReadTypeCombo();

		
		private IntegerField _showBlobSizeField = new IntegerField(5);


		public BlobOkJPanel() {

			
			
			_showBlobChk.setSelected(_readBlobs);
			_showBlobChk.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent e) {
				_blobTypeDrop.setEnabled(_showBlobChk.isSelected());
				_typeDropLabel.setEnabled(_showBlobChk.isSelected());
				_showBlobSizeField.setEnabled(_showBlobChk.isSelected() &&
					(_blobTypeDrop.getSelectedIndex()== 0));
				}
			});

			
			_blobTypeDrop = new ReadTypeCombo();
			_blobTypeDrop.setSelectedIndex( (_readCompleteBlobs) ? 1 : 0 );
			_blobTypeDrop.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					_showBlobSizeField.setEnabled(_blobTypeDrop.getSelectedIndex()== 0);
				}
			});

			_showBlobSizeField = new IntegerField(5);
			_showBlobSizeField.setInt(_readBlobsSize);


			
			_blobTypeDrop.setEnabled(_readBlobs);
			_typeDropLabel.setEnabled(_readBlobs);
			_showBlobSizeField.setEnabled(_readBlobs &&  ! _readCompleteBlobs);

			

			setLayout(new GridBagLayout());


			
			setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("dataTypeBlob.blobType")));
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.anchor = GridBagConstraints.WEST;

			gbc.gridx = 0;
			gbc.gridy = 0;

			gbc.gridwidth = 1;
			add(_showBlobChk, gbc);

			++gbc.gridx;
			add(_typeDropLabel, gbc);

			++gbc.gridx;
			add(_blobTypeDrop, gbc);

			++gbc.gridx;
			add(_showBlobSizeField, gbc);

		} 


		
		public void ok() {
			
			_readBlobs = _showBlobChk.isSelected();
			DTProperties.put(
				thisClassName,
				"readBlobs", Boolean.valueOf(_readBlobs).toString());


			_readCompleteBlobs = (_blobTypeDrop.getSelectedIndex() == 0) ? false : true;
			DTProperties.put(
				thisClassName,
				"readCompleteBlobs", Boolean.valueOf(_readCompleteBlobs).toString());

			_readBlobsSize = _showBlobSizeField.getInt();
			DTProperties.put(
				thisClassName,
				"readBlobsSize", Integer.toString(_readBlobsSize));
		}

	 } 
}
