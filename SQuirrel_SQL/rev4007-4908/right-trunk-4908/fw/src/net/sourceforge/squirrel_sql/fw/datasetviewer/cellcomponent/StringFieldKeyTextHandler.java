
package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import java.awt.event.KeyEvent;

import javax.swing.text.JTextComponent;


public class StringFieldKeyTextHandler extends BaseKeyTextHandler
{
	
	private IRestorableTextComponent _textComponent;
	
	
	private int _columnSize;
	
	
	private boolean _isNullable;
		
	public StringFieldKeyTextHandler(IRestorableTextComponent component, int columnSize, boolean isNullable) {
		_textComponent = component;
		_columnSize = columnSize;
		_isNullable = isNullable;
	}
	
	
	public void keyTyped(KeyEvent e)
	{
		char c = e.getKeyChar();

		
		
		
		JTextComponent _theComponent = (JTextComponent) _textComponent;
		String text = _theComponent.getText();

		
		
		

		
		if (_columnSize > 0 && text.length() >= _columnSize 
				&& c != KeyEvent.VK_BACK_SPACE
				&& c != KeyEvent.VK_DELETE)
		{
			
			e.consume();
			_theComponent.getToolkit().beep();

			
		}

		
		
		

		if (_isNullable)
		{

			
			if (text.equals(BaseDataTypeComponent.NULL_VALUE_PATTERN))
			{
				if ((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))
				{
					
					_textComponent.restoreText();
					e.consume();
				}
				else
				{
					
					_textComponent.updateText("");
					
				}
			}
			else
			{
				
				
				
				if ((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))
				{
					if (text.length() == 0)
					{
						
						_textComponent.updateText(BaseDataTypeComponent.NULL_VALUE_PATTERN);
						e.consume();
					}
				}
			}
		}
		else
		{
			
			
			handleNotNullableField(text, c, e, _textComponent);
		}
	}
}
