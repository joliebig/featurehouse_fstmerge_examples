

package edu.rice.cs.drjava.ui.predictive;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import java.awt.*;
import java.awt.event.*;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;

import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.lambda.Runnable1;
import edu.rice.cs.plt.lambda.LambdaUtil;
import edu.rice.cs.util.swing.SwingFrame;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.drjava.DrJavaRoot;


public class PredictiveInputFrame<T extends Comparable<? super T>> extends SwingFrame {
  
  
  public static interface InfoSupplier<X> extends Lambda<X,String> { }





  
  
  public static interface CloseAction<X extends Comparable<? super X>> extends Lambda<PredictiveInputFrame<X>,Object> {
    public Object value(PredictiveInputFrame<X> param);
    public String getName();
    public KeyStroke getKeyStroke(); 
    public String getToolTipText(); 
  }
  
  
  public static class FrameState {
    private volatile Dimension _dim;
    private volatile Point _loc;
    private volatile int _currentStrategyIndex;
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
      catch(NullPointerException npe) {
        throw new IllegalArgumentException("Wrong FrameState string: " + npe);
      }
      catch(NoSuchElementException nsee) {
        throw new IllegalArgumentException("Wrong FrameState string: " + nsee);
      }
      catch(NumberFormatException nfe) {
        throw new IllegalArgumentException("Wrong FrameState string: " + nfe);
      }
    }
    public FrameState(PredictiveInputFrame<?> comp) {
      _dim = comp.getSize();
      _loc = comp.getLocation();
      _currentStrategyIndex = comp._strategies.indexOf(comp._currentStrategy);
    }
    public String toString() {
      final StringBuilder sb = new StringBuilder();
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

  
  private volatile PredictiveInputModel<T> _pim;

  
  private volatile String _buttonPressed;

  
  private final JButton[] _buttons;
  
  
  private final JTextField _textField = new JTextField();
  
  
  protected JPanel _optionsPanel;
  
  
  protected JComponent[] _optionalComponents;
  
  
  private final JLabel _tabCompletesLabel = new JLabel("Tab completes: ");

  
  private final JList _matchList;

  
  private final boolean _force;
  
  
  private final JLabel _sharedExtLabel = new JLabel("");

  
  private final PredictiveInputListener _listener = new PredictiveInputListener();

  
  private final InfoSupplier<? super T> _info;

  
  private final JLabel _infoLabel = new JLabel("");
  
  
  private final SwingFrame _owner;
  
  
  private final ArrayList<CloseAction<T>> _actions;
  
  
  private final int _cancelIndex;
  
  
  private final java.util.List<PredictiveInputModel.MatchingStrategy<T>> _strategies;
  
  
  private final JComboBox _strategyBox;
  
  
  private volatile FrameState _lastState;
  
  
  private volatile PredictiveInputModel.MatchingStrategy<T> _currentStrategy;

  

  public PredictiveInputFrame(SwingFrame owner, String title, boolean force, boolean ignoreCase, InfoSupplier<? super T> info, 
                              java.util.List<PredictiveInputModel.MatchingStrategy<T>> strategies,
                              java.util.List<CloseAction<T>> actions, int cancelIndex, Collection<T> items) {
    super(title);
    _strategies = strategies;
    _strategyBox = new JComboBox(_strategies.toArray());
    _currentStrategy = _strategies.get(0);
    _pim = new PredictiveInputModel<T>(ignoreCase, _currentStrategy, items);
    _matchList = new JList(_pim.getMatchingItems().toArray());
    _force = force;
    _info = info;
    _lastState = null;
    _owner = owner;
    _actions = new ArrayList<CloseAction<T>>(actions);
    _buttons = new JButton[actions.size()];
    _cancelIndex = cancelIndex;
    init(_info != null);
    initDone(); 
  }
  
  
  public PredictiveInputFrame(SwingFrame owner, String title, boolean force, boolean ignoreCase, InfoSupplier<? super T> info, 
                              List<PredictiveInputModel.MatchingStrategy<T>> strategies,
                              java.util.List<CloseAction<T>> actions, int cancelIndex, T... items) {
    this(owner, title, force, ignoreCase, info, strategies, actions, cancelIndex, Arrays.asList(items));
  }
  
  
  public FrameState getFrameState() { return _lastState; }
  
  
  public void setFrameState(FrameState ds) {
    _lastState = ds;
    if (_lastState != null) {
      setSize(_lastState.getDimension());
      setLocation(_lastState.getLocation());
      int index = _lastState.getCurrentStrategyIndex();
      if ((index >= 0) && (index < _strategies.size())) {
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
    if (_lastState != null) {
      setSize(_lastState.getDimension());
      setLocation(_lastState.getLocation());
      int index = _lastState.getCurrentStrategyIndex();
      if ((index >= 0) && (index < _strategies.size())) {
        _currentStrategy = _strategies.get(index);
        _strategyBox.setSelectedIndex(index);
      }
      selectStrategy();
      validate();
    }
    else {
      Dimension parentDim = (_owner != null) ? _owner.getSize() : getToolkit().getScreenSize();
      
      int ys = (int) parentDim.getHeight()/4;
      
      setSize(new Dimension((int)getSize().getWidth(), (int) Math.min(parentDim.getHeight(), Math.max(ys, 300))));
      if (_owner!=null) { setLocationRelativeTo(_owner); }
      _currentStrategy = _strategies.get(0);
      _strategyBox.setSelectedIndex(0);
      selectStrategy();
    }
  }

  
  public List<T> getItems() { return _pim.getItems(); }

  
  public void setModel(boolean ignoreCase, PredictiveInputModel<T> pim) {
    _pim = new PredictiveInputModel<T>(ignoreCase, pim);
    removeListener();
    updateTextField();
    updateExtensionLabel();
    updateList();
    addListener();
  }

  
  public void setItems(boolean ignoreCase, Collection<T> items) {
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

  
  public String getButtonPressed() {
    return _buttonPressed;
  }
  
  
  public String getMask() {
    return _textField.getText();
  }

  
  public void setMask(String mask) {
    _pim.setMask(mask);
    removeListener();
    updateTextField();
    updateExtensionLabel();
    updateList();
    addListener();
  }

  
  public String getText() {
    if (_force) {
      @SuppressWarnings("unchecked") 
      T item = (T)_matchList.getSelectedValue();
      return (item == null) ? "" : _currentStrategy.force(item,_textField.getText());
    }
    return _textField.getText();
  }

  
  public T getItem() {
    if (!_force && _pim.getMatchingItems().size() == 0) return null;
    @SuppressWarnings("unchecked") 
    T item = (T)_matchList.getSelectedValue();
    return item;
  }

  
  private void init(boolean info) {
    _buttonPressed = null;
    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        validate();
        _matchList.ensureIndexIsVisible(_matchList.getSelectedIndex());
      }
    });

    
    int i = 0;
    for (final CloseAction<T> a: _actions) {
      _buttons[i] = new JButton(a.getName());
      final String tooltip = a.getToolTipText();
      if (tooltip != null) { _buttons[i].setToolTipText(tooltip); }
      _buttons[i].addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) { buttonPressed(a); }
      });
      ++i;
    }

    getRootPane().setDefaultButton(_buttons[0]);

    _strategyBox.setEditable(false);
    _strategyBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        
        selectStrategy();
      }
    });
    _strategyBox.addFocusListener(new FocusAdapter() {

      public void focusLost(FocusEvent e) {
        boolean bf = false;
        for (JButton b: _buttons) { if (e.getOppositeComponent() == b) { bf = true; break; } }
        if ((e.getOppositeComponent() != _textField) && (!bf)) {
          for(JComponent c: _optionalComponents) {
            if (e.getOppositeComponent() == c) { return; }
          }
          _textField.requestFocus();
        }
      }
    });

    
    _textField.setDragEnabled(false);
    _textField.setFocusTraversalKeysEnabled(false);

    addListener();

    Keymap ourMap = JTextComponent.addKeymap("PredictiveInputFrame._textField", _textField.getKeymap());
    for (final CloseAction<T> a: _actions) {
      KeyStroke ks = a.getKeyStroke();
      if (ks != null) {
        ourMap.addActionForKeyStroke(ks, new AbstractAction() {
          public void actionPerformed(ActionEvent e) {
            buttonPressed(a);
          }
        });
      }
    }
    ourMap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), new AbstractAction() {
      public void actionPerformed(ActionEvent e) {

        removeListener();
        _pim.extendSharedMask();
        updateTextField();
        updateExtensionLabel();
        updateList();
        addListener();
      }
    });
    ourMap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), new AbstractAction() {
      public void actionPerformed(ActionEvent e) {

        if (_matchList.getModel().getSize() > 0) {
          removeListener();
          int i = _matchList.getSelectedIndex();
          if (i > 0) {
            _matchList.setSelectedIndex(i - 1);
            _matchList.ensureIndexIsVisible(i - 1);
            _pim.setCurrentItem(_pim.getMatchingItems().get(i - 1));
            updateInfo();
          }
          addListener();
        }
      }
    });
    ourMap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), new AbstractAction() {
      public void actionPerformed(ActionEvent e) {

        if (_matchList.getModel().getSize() > 0) {
          removeListener();
          int i = _matchList.getSelectedIndex();
          if (i < _matchList.getModel().getSize() - 1) {
            _matchList.setSelectedIndex(i + 1);
            _matchList.ensureIndexIsVisible(i + 1);
            _pim.setCurrentItem(_pim.getMatchingItems().get(i + 1));
            updateInfo();
          }
          addListener();
        }
      }
    });
    ourMap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), new AbstractAction() {
      public void actionPerformed(ActionEvent e) {

        if (_matchList.getModel().getSize() > 0) {
          removeListener();
          int page = _matchList.getLastVisibleIndex() - _matchList.getFirstVisibleIndex() + 1;
          int i = _matchList.getSelectedIndex() - page;
          if (i < 0)  i = 0;
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

        if (_matchList.getModel().getSize() > 0) {
          removeListener();
          int page = _matchList.getLastVisibleIndex() - _matchList.getFirstVisibleIndex() + 1;
          int i = _matchList.getSelectedIndex() + page;
          if (i >= _matchList.getModel().getSize()) {
            i = _matchList.getModel().getSize() - 1;
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

    _textField.addFocusListener(new FocusAdapter() {
      public void focusLost(FocusEvent e) {
        boolean bf = false;
        for (JButton b: _buttons) { if (e.getOppositeComponent() == b) { bf = true; break; } }
        if ((e.getOppositeComponent() != _strategyBox) && (!bf)) {
          for(JComponent c: _optionalComponents) {
            if (e.getOppositeComponent() == c) { return; }
          }
          _textField.requestFocus();
        }
      }
    });

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
    
    if (info) {
      c.fill = GridBagConstraints.NONE;
      contentPane.add(_infoLabel, c);
    }

    c.fill = GridBagConstraints.BOTH;
    c.weighty = 1.0;
    contentPane.add(new JScrollPane(_matchList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), 
                    c);
    
    c.anchor = GridBagConstraints.SOUTHWEST;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0.0;
    c.weighty = 0.0;
    c.gridwidth = 1;
    contentPane.add(_tabCompletesLabel, c);
    
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1.0;
    c.gridwidth = GridBagConstraints.REMAINDER;
    contentPane.add(_sharedExtLabel, c);
    
    contentPane.add(_textField, c);
    
    _optionalComponents = makeOptions();
    if (_optionalComponents.length > 0) {
      _optionsPanel = new JPanel(new BorderLayout());
      _setupOptionsPanel(_optionalComponents);
      contentPane.add(_optionsPanel, c);
    }
    
    c.anchor = GridBagConstraints.SOUTHWEST;
    c.weightx = 1.0;
    c.weighty = 0.0;
    c.gridwidth = GridBagConstraints.REMAINDER; 
    c.insets.top = 2;
    c.insets.left = 2;
    c.insets.bottom = 2;
    c.insets.right = 2;
    
    JPanel buttonPanel = new JPanel(new GridBagLayout());
    GridBagConstraints bc = new GridBagConstraints();
    bc.insets.left = 2;
    bc.insets.right = 2;
    buttonPanel.add(new JLabel("Matching strategy:"), bc);
    buttonPanel.add(_strategyBox, bc);
    for(JButton b: _buttons) { buttonPanel.add(b, bc); }
    
    contentPane.add(buttonPanel, c);

    pack();





    if (_owner!=null) { setLocationRelativeTo(_owner); }

    removeListener();
    updateTextField();
    addListener();
    updateList();
  }
  
  
  protected JComponent[] makeOptions() {        
    return new JComponent[0];    
  }
  
  
  private void _setupOptionsPanel(JComponent[] components) {
    JPanel mainButtons = new JPanel();
    JPanel emptyPanel = new JPanel();
    GridBagLayout gbLayout = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    mainButtons.setLayout(gbLayout);
    
    for (JComponent b: components) { mainButtons.add(b); }
    mainButtons.add(emptyPanel);
    
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.NORTH;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.weightx = 1.0;

    for (JComponent b: components) { gbLayout.setConstraints(b, c); }
    
    c.fill = GridBagConstraints.BOTH;
    c.anchor = GridBagConstraints.SOUTH;
    c.gridheight = GridBagConstraints.REMAINDER;
    c.weighty = 1.0;
    
    gbLayout.setConstraints(emptyPanel, c);
    
    _optionsPanel.add(mainButtons, BorderLayout.CENTER);
  }
  
  
  public void setOwnerEnabled(boolean b) {
    
  }
  
  
  public void setVisible(boolean vis) {
    assert EventQueue.isDispatchThread();
    validate();
    if (vis) {
      DrJavaRoot.installModalWindowAdapter(this, LambdaUtil.NO_OP, CANCEL);
      setOwnerEnabled(false);
      selectStrategy();
      _textField.requestFocus();
      toFront();
    }
    else {
      DrJavaRoot.removeModalWindowAdapter(this);
      setOwnerEnabled(true);
      if (_owner!=null) { _owner.toFront(); }
    }
    super.setVisible(vis);
  }
  
  
  protected final Runnable1<WindowEvent> CANCEL = new Runnable1<WindowEvent>() {
    public void run(WindowEvent e) { cancel(); }
  };

  
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
  
  
  public void resetFocus() {
    _textField.requestFocus();
  }

  
  private void updateExtensionLabel() {
    _sharedExtLabel.setText(_pim.getSharedMaskExtension() + " ");
    _tabCompletesLabel.setVisible(_pim.getSharedMaskExtension().length() > 0);
  }

  
  private void updateList() {
    _matchList.setListData(_pim.getMatchingItems().toArray());
    _matchList.setSelectedValue(_pim.getCurrentItem(), true);
    updateExtensionLabel();
    updateInfo();
    if (_force) {
      for(int i = 0; i < _buttons.length-1; ++i) {
        _buttons[i].setEnabled(_matchList.getModel().getSize() > 0);
      }
    }
  }

  
  private void updateInfo() {
    if (_info == null) return;
    if (_matchList.getModel().getSize() > 0) {
      @SuppressWarnings("unchecked") 
      T item = (T)_matchList.getSelectedValue();
      _infoLabel.setText("Path:   " + _info.value(item));
    }
    else _infoLabel.setText("No file selected");
  }
  
  
  private void cancel() {
    buttonPressed(_actions.get(_cancelIndex));
  }
  
  
  private void buttonPressed(CloseAction<T> a) {
    _buttonPressed = a.getName();
    _lastState = new FrameState(PredictiveInputFrame.this);
    setVisible(false);
    a.value(this);
  }
  
  
  public void selectStrategy() {
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
      assert EventQueue.isDispatchThread();



          removeListener();
          _pim.setMask(_textField.getText());
          updateExtensionLabel();
          updateList();
          addListener();


    }

    public void removeUpdate(DocumentEvent e) {
      assert EventQueue.isDispatchThread();



          removeListener();
          _pim.setMask(_textField.getText());
          updateExtensionLabel();
          updateList();
          addListener();


    }

    public void changedUpdate(DocumentEvent e) {
      assert EventQueue.isDispatchThread();



          removeListener();
          _pim.setMask(_textField.getText());
          updateExtensionLabel();
          updateList();
          addListener();


    }

    public void caretUpdate(CaretEvent e) { }
  }
}
