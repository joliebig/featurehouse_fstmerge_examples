
package net.sourceforge.squirrel_sql.fw.completion;




import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class Completor
{
   public static final Color DEFAULT_POP_UP_BACK_GROUND = new Color(255,255,204);


	private Vector<CompletorListener> _listeners = new Vector<CompletorListener>();
	private ICompletorModel _model;
	private JPanel _completionPanel;
	private JList _completionList;

	private Rectangle _curCompletionPanelSize;
	private PopupManager _popupMan;
	private TextComponentProvider _txtComp;
	private CompletionFocusHandler _completionFocusHandler;
	private FocusListener _completionFocusListener;

	private MouseAdapter _listMouseAdapter;
	private KeyListener _filterKeyListener;
	private static final int MAX_ITEMS_IN_COMPLETION_LIST = 10;
	private JScrollPane _completionListScrollPane;

   private CompletionCandidates _currCandidates;

	private KeyStroke[] _keysToDisableWhenPopUpOpen = new KeyStroke[]
	{
		KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
		KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false),
		KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false),
		KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false),
		KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false),
		KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false),
		KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0, false),
		KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0, false),
		KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0, false),
		KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.CTRL_MASK, false)
	};

   private Action[] _originalActions = null;

   public Completor(JTextComponent txtComp, ICompletorModel model)
	{
		this(txtComp, model, DEFAULT_POP_UP_BACK_GROUND, false, txtComp); 
	}

   public Completor(JTextComponent txtComp, ICompletorModel model, Color popUpBackGround, boolean useOwnFilterTextField)
	{
      this(txtComp, model, popUpBackGround, useOwnFilterTextField, txtComp);
   }

   public Completor(JTextComponent txtComp, ICompletorModel model, Color popUpBackGround, boolean useOwnFilterTextField, JComponent popupParent)
	{
      _txtComp = new TextComponentProvider(txtComp, useOwnFilterTextField);

		_model = model;

		_completionPanel =
			new JPanel(new BorderLayout())
			{
                private static final long serialVersionUID = 1L;

                public void setSize(int width, int height)
				{
					
					super.setSize(_curCompletionPanelSize.width, _curCompletionPanelSize.height);
				}
			};

		_completionList = new JList(new DefaultListModel());
		_completionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_completionList.setBackground(popUpBackGround);

		_completionFocusHandler = new CompletionFocusHandler(_txtComp, _completionList);
		_completionFocusListener = new FocusAdapter()
		{
			public void focusLost(FocusEvent e){closePopup();}
		};


		_listMouseAdapter =
			new MouseAdapter()
			{
				public void mouseClicked(MouseEvent e)	{onMousClicked(e);}
			};

		_filterKeyListener =
			new KeyAdapter()
			{
				public void keyPressed(KeyEvent e){onKeyPressedOnList(e);}
			};

		_completionListScrollPane = new JScrollPane(_completionList);

      if(_txtComp.editorEqualsFilter())
      {
		   _completionPanel.add(_completionListScrollPane, BorderLayout.CENTER);
      }
      else
      {
         _completionPanel.add(_txtComp.getFilter(), BorderLayout.NORTH);
         _completionPanel.add(_completionListScrollPane, BorderLayout.CENTER);
      }

		_completionPanel.setVisible(false);

      _popupMan = new PopupManager(popupParent);
   }

   private void onKeyPressedOnList(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_TAB)
		{
			completionSelected(e.getKeyCode(), e.getModifiers());
		}
      else if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			closePopup();
		}
		else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
		{
			if(_txtComp.editorEqualsFilter() && 1 >= _currCandidates.getStringToReplace().length())
			{
				closePopup();
			}
			else
			{
				reInitList();
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_TAB)
		{
			
		}
		else if(e.getKeyCode() == KeyEvent.VK_UP)
		{
			if(0 < _completionList.getSelectedIndex())
			{
				int newSelIx = _completionList.getSelectedIndex() - 1;
				_completionList.setSelectionInterval(newSelIx, newSelIx);
				_completionList.ensureIndexIsVisible(newSelIx);
			}
         else
         {
            int lastIx = _completionList.getModel().getSize() - 1;
            _completionList.setSelectionInterval(lastIx, lastIx);
            _completionList.ensureIndexIsVisible(lastIx);
         }
		}
		else if(e.getKeyCode() == KeyEvent.VK_DOWN)
		{
			if(_completionList.getSelectedIndex() + 1 < _completionList.getModel().getSize())
			{
				int newSelIx = _completionList.getSelectedIndex() + 1;
				_completionList.setSelectionInterval(newSelIx, newSelIx);
				_completionList.ensureIndexIsVisible(newSelIx);
			}
         else
         {
            _completionList.setSelectionInterval(0, 0);
            _completionList.ensureIndexIsVisible(0);
         }
      }
      else if(e.getKeyCode() == KeyEvent.VK_PAGE_UP)
      {
         if(0 < _completionList.getSelectedIndex() - MAX_ITEMS_IN_COMPLETION_LIST)
         {
            int newSelIx = _completionList.getSelectedIndex() - MAX_ITEMS_IN_COMPLETION_LIST;
            _completionList.setSelectionInterval(newSelIx, newSelIx);
            _completionList.ensureIndexIsVisible(newSelIx);
         }
         else
         {
            _completionList.setSelectionInterval(0, 0);
            _completionList.ensureIndexIsVisible(0);
         }
      }
      else if(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)
      {
         if(_completionList.getSelectedIndex() + MAX_ITEMS_IN_COMPLETION_LIST < _completionList.getModel().getSize())
         {
            int newSelIx = _completionList.getSelectedIndex() + MAX_ITEMS_IN_COMPLETION_LIST;
            _completionList.setSelectionInterval(newSelIx, newSelIx);
            _completionList.ensureIndexIsVisible(newSelIx);
         }
         else
         {
            int lastIndex = _completionList.getModel().getSize() - 1;
            _completionList.setSelectionInterval(lastIndex, lastIndex);
            _completionList.ensureIndexIsVisible(lastIndex);
         }

      }
		else
		{
			reInitList();

			DefaultListModel listModel = (DefaultListModel) _completionList.getModel();
			if(1 == listModel.size())
			{
				CompletionInfo info = (CompletionInfo) listModel.getElementAt(0);
				if(_txtComp.editorEqualsFilter() && _currCandidates.getStringToReplace().toUpperCase().startsWith(info.getCompareString().toUpperCase()))
				{
					closePopup();
				}
			}
		}
	}

	private void reInitList()
	{
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            
            
            
            reInitListLater();
         }
      });
	}

   private void reInitListLater()
   {
      _currCandidates = _model.getCompletionCandidates(getTextTillCarret());

      if(0 == _currCandidates.getCandidates().length && _txtComp.editorEqualsFilter())
      {
         closePopup();
      }
      else
      {
         fillAndShowCompletionList(_currCandidates.getCandidates());
      }
   }

   
   public String getTextTillCarret()
   {
      try
      {
         if(_txtComp.editorEqualsFilter())
      {
         return _txtComp.getEditor().getText(0, _txtComp.getFilter().getCaretPosition());
         }
         else
         {
            return _txtComp.getFilter().getText();
         }
      }
      catch (BadLocationException e)
      {
         throw new RuntimeException(e);
      }
   }

   private void onMousClicked(MouseEvent e)
	{
		if(2 == e.getClickCount())
		{
			completionSelected(KeyEvent.VK_ENTER, 0);
		}
	}

	private void completionSelected(int keyCode, int modifiers)
	{
      Object selected = null;
      if(0 < _completionList.getModel().getSize())
      {
         selected = _completionList.getSelectedValue();
      }
      closePopup();
		if(null != selected && selected instanceof CompletionInfo)
		{
			fireEvent((CompletionInfo)selected, keyCode, modifiers);
		}
	}

	private void closePopup()
	{
		_completionList.removeMouseListener(_listMouseAdapter);
		_txtComp.getFilter().removeKeyListener(_filterKeyListener);
		_completionFocusHandler.setFocusListener(null);

		_completionPanel.setVisible(false);

      if(_txtComp.editorEqualsFilter())
      {
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               Keymap km = _txtComp.getEditor().getKeymap();
               for (int i = 0; i < _keysToDisableWhenPopUpOpen.length; i++)
               {
                  km.removeKeyStrokeBinding(_keysToDisableWhenPopUpOpen[i]);

                  if(null != _originalActions[i])
                  {
                     km.addActionForKeyStroke(_keysToDisableWhenPopUpOpen[i], _originalActions[i]);
                  }
               }
            }
         });
      }
      else
      {
         _txtComp.getFilter().setText("");
         _txtComp.getEditor().requestFocusInWindow();
      }
	}


	public void show()
	{
		try
		{
			_currCandidates = _model.getCompletionCandidates(getTextTillCarret());

			if(0 == _currCandidates.getCandidates().length)
			{
				return;
			}
			if(1 == _currCandidates.getCandidates().length)
			{
				fireEvent(_currCandidates.getCandidates()[0], KeyEvent.VK_ENTER, 0);
				return;
			}

         _txtComp.getEditor().modelToView(_currCandidates.getReplacementStart());

			_completionList.setFont(_txtComp.getEditor().getFont());
			fillAndShowCompletionList(_currCandidates.getCandidates());
		}
		catch (BadLocationException e)
		{
			throw new RuntimeException(e);
		}
	}

   private void fillAndShowCompletionList(CompletionInfo[] candidates)
	{
      try
      {
         
         
         _curCompletionPanelSize = getCurCompletionPanelSize(candidates);

         DefaultListModel model = (DefaultListModel) _completionList.getModel();
         model.removeAllElements();

         for (int i = 0; i < candidates.length; i++)
         {
            model.addElement(candidates[i]);
         }

         Rectangle caretBounds;
         if(_txtComp.editorEqualsFilter())
         {
            caretBounds = _txtComp.getEditor().modelToView(_currCandidates.getReplacementStart());
         }
         else
         {
            caretBounds = _txtComp.getEditor().modelToView(_txtComp.getEditor().getCaretPosition());
         }

         _popupMan.install(_completionPanel, caretBounds, PopupManager.BelowPreferred);

         _completionList.setSelectedIndex(0);
         _completionList.ensureIndexIsVisible(0);
         _completionPanel.setVisible(true);

         _completionList.removeMouseListener(_listMouseAdapter);
         _completionList.addMouseListener(_listMouseAdapter);
         _txtComp.getFilter().removeKeyListener(_filterKeyListener);
         _txtComp.getFilter().addKeyListener(_filterKeyListener);

			_completionFocusHandler.setFocusListener(_completionFocusListener);


			if(_txtComp.editorEqualsFilter())
			{
			    Action doNothingAction = new AbstractAction("doNothingAction")
			    {
			        private static final long serialVersionUID = 1L;

			        public void actionPerformed(ActionEvent e)
			        {
			        }
			    };

            Keymap km = _txtComp.getEditor().getKeymap();

            if(null == _originalActions)
            {
               _originalActions = new Action[_keysToDisableWhenPopUpOpen.length];

               for (int i = 0; i < _keysToDisableWhenPopUpOpen.length; i++)
               {
                  _originalActions[i] = km.getAction(_keysToDisableWhenPopUpOpen[i]);
               }
            }

            for (int i = 0; i < _keysToDisableWhenPopUpOpen.length; i++)
            {
               km.addActionForKeyStroke(_keysToDisableWhenPopUpOpen[i], doNothingAction);
            }
         }
         else
         {
            _txtComp.getFilter().requestFocusInWindow();
         }
      }
      catch (BadLocationException e)
      {
         throw new RuntimeException(e);
      }
   }


	private Rectangle getCurCompletionPanelSize(CompletionInfo[] candidates)
	{
		FontMetrics fm = _txtComp.getEditor().getGraphics().getFontMetrics(_txtComp.getEditor().getFont());
		int width = getCurCompletionPanelWidth(candidates, fm) + 32;
		int height = (int)(Math.min(candidates.length,  MAX_ITEMS_IN_COMPLETION_LIST) * (fm.getHeight() + 2.3) + 3);


      if(false == _txtComp.editorEqualsFilter())
      {
         height += _txtComp.getFilter().getPreferredSize().getHeight();
      }

		return new Rectangle(width, height);
	}

	private int getCurCompletionPanelWidth(CompletionInfo[] infos, FontMetrics fontMetrics)
	{
      int maxSize = 0;
      if(false == _txtComp.editorEqualsFilter() && null != _txtComp.getFilter().getText())
      {
         maxSize = Math.max(fontMetrics.stringWidth(_txtComp.getFilter().getText() + "   "), maxSize);
      }

		for (int i = 0; i < infos.length; i++)
		{
         maxSize = Math.max(fontMetrics.stringWidth(infos[i].toString()), maxSize);
		}
		return maxSize;

	}


	private void fireEvent(CompletionInfo completion, int keyCode, int modifiers)
	{
		Vector<CompletorListener> clone = new Vector<CompletorListener>(_listeners);

		for (int i = 0; i < clone.size(); i++)
		{
         CompletorListener completorListener = clone.elementAt(i);
         if(_txtComp.editorEqualsFilter())
         {
            completorListener.completionSelected(completion, _currCandidates.getReplacementStart(), keyCode, modifiers);
         }
         else
         {
            completorListener.completionSelected(completion, -1, keyCode, modifiers);
         }
		}
	}

	public void addCodeCompletorListener(CompletorListener l)
	{
		_listeners.add(l);
	}

	public void removeCodeCompletorListener(CompletorListener l)
	{
		_listeners.remove(l);
	}

}
