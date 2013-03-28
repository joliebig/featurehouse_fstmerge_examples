package net.sourceforge.squirrel_sql.client.session;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.Action;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import net.sourceforge.squirrel_sql.fw.gui.TextPopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.fw.util.DefaultExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class MessagePanel extends JTextPane implements IMessageHandler
{
    static final long serialVersionUID = 5859398063643519072L;
    
	
	private static final ILogger s_log =
		LoggerController.createLogger(MessagePanel.class);

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(MessagePanel.class);

	
	private final TextPopupMenu _popupMenu = new MessagePanelPopupMenu();

	
	private SimpleAttributeSet _saSetMessage;

	private SimpleAttributeSet _saSetError;
	private SimpleAttributeSet _saSetWarning;

	
	private int _lastLength;
	private String _lastMessage;
	private SimpleAttributeSet _lastSASet;

   private HashMap<SimpleAttributeSet, SimpleAttributeSet> _saSetHistoryBySaSet 
       = new HashMap<SimpleAttributeSet, SimpleAttributeSet>();

   private static interface I18N {
       
       String CLEAR_LABEL = s_stringMgr.getString("MessagePanel.clearLabel");
   }
   
   private DefaultExceptionFormatter defaultExceptionFormatter = 
       new DefaultExceptionFormatter();
   
   
   public MessagePanel()
   {
      super();

      _popupMenu.setTextComponent(this);

      
      addMouseListener(new MouseAdapter()
      {
         public void mousePressed(MouseEvent evt)
         {
            if (evt.isPopupTrigger())
            {
               _popupMenu.show(evt);
            }
         }
         public void mouseReleased(MouseEvent evt)
         {
            if (evt.isPopupTrigger())
            {
               _popupMenu.show(evt);
            }
         }
      });

      
      
      _saSetMessage = new SimpleAttributeSet();
      StyleConstants.setBackground(_saSetMessage, Color.green);

      SimpleAttributeSet saSetMessageHistory = new SimpleAttributeSet();
      StyleConstants.setBackground(saSetMessageHistory, getBackground());
      _saSetHistoryBySaSet.put(_saSetMessage, saSetMessageHistory);
      
      


      
      
      _saSetWarning = new SimpleAttributeSet();
      StyleConstants.setBackground(_saSetWarning, Color.yellow);

      SimpleAttributeSet saSetWarningHistory = new SimpleAttributeSet();
      StyleConstants.setBackground(saSetWarningHistory, new Color(255,255,210)); 
      _saSetHistoryBySaSet.put(_saSetWarning, saSetWarningHistory);
      
      


      
      
      _saSetError = new SimpleAttributeSet();
      StyleConstants.setBackground(_saSetError, Color.red);

      SimpleAttributeSet saSetErrorHistory = new SimpleAttributeSet();
      StyleConstants.setBackground(saSetErrorHistory, Color.pink);
      _saSetHistoryBySaSet.put(_saSetError, saSetErrorHistory);
      
      


   }


   public void addToMessagePanelPopup(Action action)
   {
       if (action == null) {
           throw new IllegalArgumentException("action cannot be null");
       }
      _popupMenu.add(action);   
   }


   
   public synchronized void showMessage(final Throwable th, 
                                        final ExceptionFormatter formatter)
   {
      if (th == null)
      {
          throw new IllegalArgumentException("th cannot be null");
      }
      privateShowMessage(th, formatter, _saSetMessage);
   }
   

   
	public synchronized void showErrorMessage(final Throwable th, 
                                              ExceptionFormatter formatter)
	{
		if (th == null)
		{
            throw new IllegalArgumentException("th cannot be null");
		}
        privateShowMessage(th, formatter, _saSetError);
	}


   
   public synchronized void showMessage(final String msg)
   {
      if (msg == null)
      {
          throw new IllegalArgumentException("msg cannot be null");
      }
      privateShowMessage(msg, _saSetMessage);
   }

   public void showWarningMessage(String msg)
   {
      if (msg == null)
      {
          throw new IllegalArgumentException("msg cannot be null");
         
      }
      privateShowMessage(msg, _saSetWarning);
   }
   
   
	public synchronized void showErrorMessage(final String msg)
	{
		if (msg == null)
		{
			throw new IllegalArgumentException("msg cannot be null");
		}
        privateShowMessage(msg, _saSetError);
	}


   
   private void privateShowMessage(final Throwable th, 
                                   final ExceptionFormatter formatter, 
                                   final SimpleAttributeSet saSet)
   {
      if (th != null) {
          
          String message = "";
          if (formatter == null) {
              message = defaultExceptionFormatter.format(th);
          } else {
              try {
                  message = formatter.format(th);
              } catch (Exception e) {
                  s_log.error("Unable to format message: "+e.getMessage(), e);
              }
          }
          privateShowMessage(message, saSet);
          if (s_log.isDebugEnabled()) {
              s_log.debug("Exception message shown in MessagePanel: "+message);
          }
      }
   }
   
	
	private void privateShowMessage(final String msg, final SimpleAttributeSet saSet)
	{
		if (msg == null)
		{
			throw new IllegalArgumentException("null Message");
		}

		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				addLine(msg, saSet);
			}
		});
	}

	
	private void append(String string, SimpleAttributeSet saSet)
	{
		Document document = getStyledDocument();
		try
		{
         
         
         if (document.getLength() >= _lastLength && null != _lastMessage)
			{
            SimpleAttributeSet historySaSet = _saSetHistoryBySaSet.get(_lastSASet);
            document.remove(_lastLength, _lastMessage.length());
            document.insertString(document.getLength(), _lastMessage, historySaSet);
			}
         
         

         _lastLength = document.getLength();
			_lastMessage = string;
			_lastSASet = saSet;

			document.insertString(document.getLength(), string, saSet);
		}
		catch (BadLocationException ble)
		{
			s_log.error("Error appending text to MessagePanel document.", ble);
		}
	}

	
	private void addLine(String line, SimpleAttributeSet saSet)
	{
		if (getDocument().getLength() > 0)
		{
			append("\n", saSet);
		}
		append(line, saSet);
		final int len = getDocument().getLength();
		select(len, len);
	}

	
	private class MessagePanelPopupMenu extends TextPopupMenu
	{
        static final long serialVersionUID = -425002646648750251L;
        
		public MessagePanelPopupMenu()
		{
			super();
			add(new ClearAction());
		}

		
		private class ClearAction extends BaseAction
		{
            static final long serialVersionUID = 2124058843445088350L;
			protected ClearAction()
			{
				super(I18N.CLEAR_LABEL);
			}

			public void actionPerformed(ActionEvent evt)
			{
				try
				{
				    Document doc = MessagePanel.this.getDocument();
				    doc.remove(0, doc.getLength());
				    _lastMessage = null;
				}
				catch (BadLocationException ex)
				{
					s_log.error("Error clearing document", ex);
				}
			}
		}
	}
}
