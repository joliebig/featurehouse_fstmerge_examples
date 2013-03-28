

package edu.rice.cs.drjava.ui.predictive;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;

import edu.rice.cs.util.Lambda;


public class PredictiveInputFrame<T extends Comparable<? super T>> extends JFrame {
  
  
  public static interface InfoSupplier<X> extends Lambda<String, X> {
    public String apply(X param);
  }

  
  public static final InfoSupplier<Object> TO_STRING_SUPPLIER = new InfoSupplier<Object>() {
    public String apply(Object param) {
      return param.toString();
    }
  };
  
  
  public static interface CloseAction<X extends Comparable<? super X>> extends Lambda<Object, PredictiveInputFrame<X>> {
    public Object apply(PredictiveInputFrame<X> param);
  }
  
  
  public static class FrameState {
    private Dimension _dim;
    private Point _loc;
    private int _currentStrategyIndex;
    public FrameState(Dimension d, Point l, int currentStrategyIndex) {
      _dim = d;
      _loc = l;
      _currentStrategyIndex = currentStrategyIndex;
    }
    public FrameState(String s) {
      StringTokenizer tok = new StringTokenizer(s);
      try {
        int x = Integer.valueOf(tok.nextToken());
        int y = Integer.valueOf(tok.nextToken());
        _dim = new Dimension(x, y);
        x = Integer.valueOf(tok.nextToken());
        y = Integer.valueOf(tok.nextToken());
        _loc = new Point(x, y);
        _currentStrategyIndex = Integer.valueOf(tok.nextToken());
      }
      catch(NoSuchElementException nsee) {
        throw new IllegalArgumentException("Wrong FrameState string: " + nsee);
      }
      catch(NumberFormatException nfe) {
        throw new IllegalArgumentException("Wrong FrameState string: " + nfe);
      }
    }
    public FrameState(PredictiveInputFrame comp) {
      _dim = comp.getSize();
      _loc = comp.getLocation();
      _currentStrategyIndex = comp._strategies.indexOf(comp._currentStrategy);
    }
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append((int)_dim.getWidth());
      sb.append(' ');
      sb.append((int)_dim.getHeight());
      sb.append(' ');
      sb.append(_loc.x);
      sb.append(' ');
      sb.append(_loc.y);
      sb.append(' ');
      sb.append(_currentStrategyIndex);
      return sb.toString();
    }
    public Dimension getDimension() { return _dim; }
    public Point getLocation() { return _loc; }
    public int getCurrentStrategyIndex() { return _currentStrategyIndex; }
  }

  
  private PredictiveInputModel<T> _pim;

  
  private int _buttonPressed;

  
  private JButton _okButton;
  
  
  private JTextField _textField;

  
  private JList _matchList;

  
  private boolean _force;

  
  private JLabel _tabCompletesLabel;
  
  
  private JLabel _sharedExtLabel;

  
  private PredictiveInputListener _listener;

  
  InfoSupplier<? super T> _info = TO_STRING_SUPPLIER;


  
  private JLabel _infoLabel;
  
  
  private FrameState _lastState = null;
  
  
  private Frame _owner;
  
  
  private CloseAction<T> _okAction;
  
  
  private CloseAction<T> _cancelAction;
  
  
  private java.util.List<PredictiveInputModel.MatchingStrategy<T>> _strategies;
  
  
  private PredictiveInputModel.MatchingStrategy<T> _currentStrategy;
  
  
  private JComboBox _strategyBox;

  

  public PredictiveInputFrame(Frame owner, String title, boolean force, boolean ignoreCase, InfoSupplier<? super T> info, 
                              java.util.List<PredictiveInputModel.MatchingStrategy<T>> strategies,
                              CloseAction<T> okAction, CloseAction<T> cancelAction, java.util.List<T> items) {
    super(title);
    _strategies = strategies;
    _currentStrategy = _strategies.get(0);
    _pim = new PredictiveInputModel<T>(ignoreCase, _currentStrategy, items);
    _force = force;
    _info = info;
    _owner = owner;
    _okAction = okAction;
    _cancelAction = cancelAction;
    init(_info!=null);
  }

  
  public PredictiveInputFrame(Frame owner, String title, boolean force, boolean ignoreCase, InfoSupplier<? super T> info, 
                              java.util.List<PredictiveInputModel.MatchingStrategy<T>> strategies,
                              CloseAction<T> okAction, CloseAction<T> cancelAction, T... items) {
    super(title);
    _strategies = strategies;
    _currentStrategy = _strategies.get(0);
    _pim = new PredictiveInputModel<T>(ignoreCase, _currentStrategy, items);
    _force = force;
    _info = info;
    _owner = owner;
    _okAction = okAction;
    _cancelAction = cancelAction;
    init(_info!=null);
  }
  
  
  public FrameState getFrameState() { return _lastState; }
  
  
  public void setFrameState(FrameState ds) {
    _lastState = ds;
    if (_lastState!=null) {
      setSize(_lastState.getDimension());
      setLocation(_lastState.getLocation());
      int index = _lastState.getCurrentStrategyIndex();
      if ((index>=0) && (index<_strategies.size())) {
        _currentStrategy = _strategies.get(index);
        _strategyBox.setSelectedIndex(index);
      }
      selectStrategy();
      validate();
    }
  }  
  
  
  public void setFrameState(String s) {
    try { _lastState = new FrameState(s); }
    catch(IllegalArgumentException e) { _lastState = null; }
    if (_lastState!=null) {
      setSize(_lastState.getDimension());
      setLocation(_lastState.getLocation());
      int index = _lastState.getCurrentStrategyIndex();
      if ((index>=0) && (index<_strategies.size())) {
        _currentStrategy = _strategies.get(index);
        _strategyBox.setSelectedIndex(index);
      }
      selectStrategy();
      validate();
    }
    else {
      Dimension parentDim = (_owner!=null)?(_owner.getSize()):getToolkit().getScreenSize();
      int xs = (int)parentDim.getWidth()/3;
      int ys = (int)parentDim.getHeight()/4;
      setSize(Math.max(xs,400), Math.max(ys, 300));
      setLocationRelativeTo(_owner);
      _currentStrategy = _strategies.get(0);
      _strategyBox.setSelectedIndex(0);
      selectStrategy();
    }
  }

  
  public void setModel(boolean ignoreCase, PredictiveInputModel<T> pim) {
    _pim = new PredictiveInputModel<T>(ignoreCase, pim);
    removeListener();
    updateTextField();
    updateExtensionLabel();
    updateList();
    addListener();
  }

  
  public void setItems(boolean ignoreCase, java.util.List<T> items) {
    _pim = new PredictiveInputModel<T>(ignoreCase, _currentStrategy, items);
    removeListener();
    updateTextField();
    updateExtensionLabel();
    updateList();
    addListener();
  }
  
  
  public void setCurrentItem(T item) {
    _pim.setCurrentItem(item);
    removeListener();
    updateTextField();
    updateExtensionLabel();
    updateList();
    addListener();
  }

  
  public void setItems(boolean ignoreCase, T... items) {
    _pim = new PredictiveInputModel<T>(ignoreCase, _currentStrategy, items);
    removeListener();
    updateTextField();
    updateExtensionLabel();
    updateList();
    addListener();
  }

  
  public int getButtonPressed() {
    return _buttonPressed;
  }

  
  public String getText() {
    if (_force) return _pim.getCurrentItem().toString();
    return _textField.getText();
  }

  
  public T getItem() {
    if (!_force && _pim.getMatchingItems().size() == 0) return null;
    return _pim.getCurrentItem();
  }

  
  private void init(boolean info) {
    _buttonPressed = JOptionPane.CANCEL_OPTION;
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(WindowEvent winEvt) {
        cancelButtonPressed();
      }
    });
    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        validate();
        _matchList.ensureIndexIsVisible(_matchList.getSelectedIndex());
      }
    });

    
    _okButton = new JButton("OK");
    _okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButtonPressed();
      }
    });
    getRootPane().setDefaultButton(_okButton);

    final JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelButtonPressed();
      }
    });
    
    _strategyBox = new JComboBox(_strategies.toArray());
    _strategyBox.setEditable(false);
    _strategyBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        
        selectStrategy();
      }
    });
    _strategyBox.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
      }

      public void focusLost(FocusEvent e) {
        if ((e.getOppositeComponent()!=_textField) && 
            (e.getOppositeComponent()!=_okButton) && 
            (e.getOppositeComponent()!=cancelButton)) {
          _textField.requestFocus();
        }
      }
    });

    
    _textField = new JTextField();
    _textField.setDragEnabled(false);
    _textField.setFocusTraversalKeysEnabled(false);

    _listener = new PredictiveInputListener();
    addListener();

    Keymap ourMap = JTextComponent.addKeymap("PredictiveInputFrame._textField", _textField.getKeymap());
    ourMap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), new AbstractAction() {
      public void actionPerformed(ActionEvent e) {

        cancelButtonPressed();
      }
    });
    ourMap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), new AbstractAction() {
      public void actionPerformed(ActionEvent e) {

        okButtonPressed();
      }
    });
    ourMap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), new AbstractAction() {
      public void actionPerformed(ActionEvent e) {

        removeListener();
        _pim.extendMask(_pim.getSharedMaskExtension());
        updateTextField();
        updateExtensionLabel();
        updateList();
        addListener();
      }
    });
    ourMap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), new AbstractAction() {
      public void actionPerformed(ActionEvent e) {

        if (_matchList.getModel().getSize()>0) {
          removeListener();
          int i = _matchList.getSelectedIndex();
          if (i>0) {
            _matchList.setSelectedIndex(i-1);
            _matchList.ensureIndexIsVisible(i-1);
            _pim.setCurrentItem(_pim.getMatchingItems().get(i-1));
            updateInfo();
          }
          addListener();
        }
      }
    });
    ourMap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), new AbstractAction() {
      public void actionPerformed(ActionEvent e) {

        if (_matchList.getModel().getSize()>0) {
          removeListener();
          int i = _matchList.getSelectedIndex();
          if (i<_matchList.getModel().getSize()-1) {
            _matchList.setSelectedIndex(i+1);
            _matchList.ensureIndexIsVisible(i+1);
            _pim.setCurrentItem(_pim.getMatchingItems().get(i+1));
            updateInfo();
          }
          addListener();
        }
      }
    });
    ourMap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), new AbstractAction() {
      public void actionPerformed(ActionEvent e) {

        if (_matchList.getModel().getSize()>0) {
          removeListener();
          int page = _matchList.getLastVisibleIndex() - _matchList.getFirstVisibleIndex() + 1;
          int i = _matchList.getSelectedIndex() - page;
          if (i<0) {
            i = 0;
          }
          _matchList.setSelectedIndex(i);
          _matchList.ensureIndexIsVisible(i);
          _pim.setCurrentItem(_pim.getMatchingItems().get(i));
          updateInfo();
          addListener();
        }
      }
    });
    ourMap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), new AbstractAction() {
      public void actionPerformed(ActionEvent e) {

        if (_matchList.getModel().getSize()>0) {
          removeListener();
          int page = _matchList.getLastVisibleIndex() - _matchList.getFirstVisibleIndex() + 1;
          int i = _matchList.getSelectedIndex() + page;
          if (i>=_matchList.getModel().getSize()) {
            i = _matchList.getModel().getSize()-1;
          }
          _matchList.setSelectedIndex(i);
          _matchList.ensureIndexIsVisible(i);
          _pim.setCurrentItem(_pim.getMatchingItems().get(i));
          updateInfo();
          addListener();
        }
      }
    });
    _textField.setKeymap(ourMap);
    
















    _textField.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
      }

      public void focusLost(FocusEvent e) {
        if ((e.getOppositeComponent()!=_strategyBox) && 
            (e.getOppositeComponent()!=_okButton) && 
            (e.getOppositeComponent()!=cancelButton)) {
          _textField.requestFocus();
        }
      }
    });

    _matchList = new JList(_pim.getMatchingItems().toArray());
    _matchList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    _matchList.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {

        removeListener();
        int i = _matchList.getSelectedIndex();
        if (i >= 0) {
          _pim.setCurrentItem(_pim.getMatchingItems().get(i));
          _matchList.ensureIndexIsVisible(i);
          updateInfo();
        }
        addListener();
      }
    });
    
    
    Container contentPane = getContentPane();
    
    GridBagLayout layout = new GridBagLayout();
    contentPane.setLayout(layout);
    
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.NORTHWEST;
    c.weightx = 1.0;
    c.weighty = 0.0;
    c.gridwidth = GridBagConstraints.REMAINDER; 
    c.insets.top = 2;
    c.insets.left = 2;
    c.insets.bottom = 2;
    c.insets.right = 2;
    
    _infoLabel = new JLabel("");
    if (info) {
      c.fill = GridBagConstraints.NONE;
      contentPane.add(_infoLabel, c);
    }

    c.fill = GridBagConstraints.BOTH;
    c.weighty = 1.0;
    contentPane.add(new JScrollPane(_matchList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), c);
    
    c.anchor = GridBagConstraints.SOUTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0.0;
    c.weighty = 0.0;
    c.gridwidth = 1;
    _tabCompletesLabel = new JLabel("Tab completes: ");
    contentPane.add(_tabCompletesLabel, c);
    
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1.0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    _sharedExtLabel = new JLabel("");
    contentPane.add(_sharedExtLabel, c);
    
    contentPane.add(_textField, c);
    
    c.anchor = GridBagConstraints.SOUTH;
    
    JPanel buttonPanel = new JPanel(new GridBagLayout());
    GridBagConstraints bc = new GridBagConstraints();
    bc.insets.left = 2;
    bc.insets.right = 2;
    buttonPanel.add(new JLabel("Matching strategy:"), bc);
    buttonPanel.add(_strategyBox, bc);
    buttonPanel.add(_okButton, bc);
    buttonPanel.add(cancelButton, bc);
    
    contentPane.add(buttonPanel, c);

    Dimension parentDim = (_owner!=null)?(_owner.getSize()):getToolkit().getScreenSize();
    int xs = (int)parentDim.getWidth()/3;
    int ys = (int)parentDim.getHeight()/4;
    setSize(Math.max(xs,400), Math.max(ys, 300));
    setLocationRelativeTo(_owner);

    removeListener();
    updateTextField();
    addListener();
    updateList();
  }
  
  
  public void setVisible(boolean b) {
    validate();
    _owner.setEnabled(!b);
    super.setVisible(b);
    if (b) {
      _textField.requestFocus();
    }
    else {
      _owner.toFront();
    }
  }

  
  private void addListener() {
    _textField.getDocument().addDocumentListener(_listener);
    _textField.addCaretListener(_listener);
  }

  
  private void removeListener() {
    _textField.getDocument().removeDocumentListener(_listener);
    _textField.removeCaretListener(_listener);
  }

  
  private void updateTextField() {
    _textField.setText(_pim.getMask());
    _textField.setCaretPosition(_pim.getMask().length());
  }

  
  private void updateExtensionLabel() {
    _sharedExtLabel.setText(_pim.getSharedMaskExtension()+" ");
    _tabCompletesLabel.setVisible(_pim.getSharedMaskExtension().length()>0);
  }

  
  private void updateList() {
    _matchList.setListData(_pim.getMatchingItems().toArray());
    _matchList.setSelectedValue(_pim.getCurrentItem(), true);
    updateExtensionLabel();
    updateInfo();
    _okButton.setEnabled(_matchList.getModel().getSize()>0);
  }

  
  private void updateInfo() {
    if (_info==null) return;
    if (_matchList.getModel().getSize()>0)  _infoLabel.setText("Path:   " + _info.apply(_pim.getCurrentItem()));
    else _infoLabel.setText("No file selected");
  }
  
  
  private void okButtonPressed() {
    if (_matchList.getModel().getSize()>0) {
      _buttonPressed = JOptionPane.OK_OPTION;
      _lastState = new FrameState(PredictiveInputFrame.this);
      setVisible(false);
      _okAction.apply(this);
    }
    else Toolkit.getDefaultToolkit().beep();
  }
  
  
  private void cancelButtonPressed() {
    _buttonPressed = JOptionPane.CANCEL_OPTION;
    _lastState = new FrameState(PredictiveInputFrame.this);
    setVisible(false);
    _cancelAction.apply(this);
  }
  
  
  private void selectStrategy() {
    _currentStrategy = _strategies.get(_strategyBox.getSelectedIndex());
    removeListener();
    _pim.setStrategy(_currentStrategy);
    updateTextField();
    updateExtensionLabel();
    updateList();
    addListener();
    _textField.requestFocus();
  }

  
  private class PredictiveInputListener implements CaretListener, DocumentListener {
    public void insertUpdate(DocumentEvent e) {

      removeListener();
      _pim.setMask(_textField.getText());
      updateExtensionLabel();
      updateList();
      addListener();
    }

    public void removeUpdate(DocumentEvent e) {

      removeListener();
      _pim.setMask(_textField.getText());
      updateExtensionLabel();
      updateList();
      addListener();
    }

    public void changedUpdate(DocumentEvent e) {

      removeListener();
      _pim.setMask(_textField.getText());
      updateExtensionLabel();
      updateList();
      addListener();
    }

    public void caretUpdate(CaretEvent e) { }
  }

  public static void main(String[] args) {
    Frame frame = JOptionPane.getFrameForComponent(null);
    InfoSupplier<String> info = new InfoSupplier<String>() {
      public String apply(String t) {
        StringBuilder sb = new StringBuilder();
        sb.append(t);
        sb.append("\nLength = ");
        sb.append(t.length());
        sb.append("\nHashcode = ");
        sb.append(t.hashCode());
        for(int i=0;i<5;++i) {
          sb.append("\n"+t);
        }
        return sb.toString();
      }
    };
    CloseAction<String> okAction = new CloseAction<String>() {
      public Object apply(PredictiveInputFrame<String> p) {
        System.out.println("User pressed Ok");
        System.out.println("Text = "+p.getText());
        System.out.println("Item = "+p.getItem());
        return null;
      }
    };
    CloseAction<String> cancelAction = new CloseAction<String>() {
      public Object apply(PredictiveInputFrame<String> p) {
        System.out.println("User pressed Cancel");
        return null;
      }
    };
    java.util.ArrayList<PredictiveInputModel.MatchingStrategy<String>> strategies =
      new java.util.ArrayList<PredictiveInputModel.MatchingStrategy<String>>();
    strategies.add(new PredictiveInputModel.PrefixStrategy<String>());
    strategies.add(new PredictiveInputModel.FragmentStrategy<String>());
    strategies.add(new PredictiveInputModel.RegExStrategy<String>());
    PredictiveInputFrame<String> pif = 
      new PredictiveInputFrame<String>(frame, "Go to file", true, true, info, strategies, okAction, cancelAction,
                                       "AboutDialog.java", "FileOps.java", "FileOpsTest.java", "Utilities.java");
    pif.setVisible(true);
  }
}
