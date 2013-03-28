

package edu.rice.cs.drjava.ui;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.MatteBorder;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;
import java.awt.*;
import java.awt.print.*;
import java.awt.image.*;
import java.net.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import edu.rice.cs.drjava.model.*;
import edu.rice.cs.util.swing.SwingFrame;


public abstract class PreviewFrame extends SwingFrame {
  
  protected final SingleDisplayModel _model;
  protected final MainFrame _mainFrame;
  protected final Pageable _print;
  protected volatile int _pageNumber;
  





  
  private final PageChangerUpdater _pageChanger;
  
  private static abstract class PageChangerUpdater {
    abstract void update(int pageNumber) throws Exception;
    abstract JComponent getComponent();
  }
  
  private class JTextFieldChanger extends PageChangerUpdater {
    private final JTextField textfield;
    private JTextFieldChanger(JTextField tf) { textfield = tf; }
    void update(int pageNumber) throws Exception { textfield.setText(String.valueOf(pageNumber)); }
    JComponent getComponent() { return textfield; }
  }
  
  private class JSpinnerChanger extends PageChangerUpdater {
    private volatile JComponent spinner;
    private volatile Method setValueMethod;
    private final Object[] args = new Object[1];
    private JSpinnerChanger(Class<?> spinnerClass, JComponent spinnerObj) throws Exception {
      spinner = spinnerObj;
      setValueMethod = spinnerClass.getMethod("setValue", Object.class);
    }
    void update(int pageNumber) throws Exception {
      args[0] = Integer.valueOf(pageNumber);
      setValueMethod.invoke(spinner, args);
    }
    JComponent getComponent() { return spinner; }
  }
  
  
  private final int PREVIEW_WIDTH;
  private final int PREVIEW_HEIGHT;
  private final int PREVIEW_PAGE_WIDTH;
  private final int PREVIEW_PAGE_HEIGHT;
  
  private static final double PAGE_ZOOM = 0.7;
  private static final int PAGE_BORDER = 20;
  private static final int TOOLBAR_HEIGHT = 35;
  private static final String ICON_PATH = "/edu/rice/cs/drjava/ui/icons/";
  
  
  private JToolBar _toolBar;
  private PagePreview _pagePreview;
  
  
  
  private final ActionListener _printListener = new ActionListener() {
    public void actionPerformed(ActionEvent ae) {
      _print();
      _close();
    }
  };
  
  
  private final Action _closeAction = new AbstractAction("Close") {
    public void actionPerformed(ActionEvent ae) { _close(); }
  };
  
  
  private final Action _nextPageAction = new AbstractAction("Next Page") {
    public void actionPerformed(ActionEvent ae) { _nextPage(); }
  };
  
  
  private final Action _prevPageAction = new AbstractAction("Previous Page") {
    public void actionPerformed(ActionEvent ae) { _previousPage(); }
  };
  
  
  private final WindowListener _windowCloseListener = new WindowAdapter() {
    public void windowClosing(WindowEvent ev) { _close(); }
  };
  
  
  public PreviewFrame(SingleDisplayModel model, MainFrame mainFrame, boolean interactions) 
    throws IllegalStateException {
    super("Print Preview");
    mainFrame.hourglassOn();
    _model = model;
    _mainFrame = mainFrame;
    _toolBar = new JToolBar();
    _print = setUpDocument(model, interactions);
    _pageChanger = createPageChanger();
    
    
    PageFormat first = _print.getPageFormat(0);
    
    PREVIEW_PAGE_WIDTH = (int) (PAGE_ZOOM * first.getWidth());
    PREVIEW_PAGE_HEIGHT = (int) (PAGE_ZOOM * first.getHeight());
    
    PREVIEW_WIDTH = PREVIEW_PAGE_WIDTH + (2 * PAGE_BORDER);
    PREVIEW_HEIGHT = PREVIEW_PAGE_HEIGHT + (2 * PAGE_BORDER) + TOOLBAR_HEIGHT;
    
    _setUpActions();
    _setUpToolBar();
    
    _pagePreview = new PagePreview(PREVIEW_PAGE_WIDTH, PREVIEW_PAGE_HEIGHT);
    _pageNumber = 0;
    
    
    PagePreviewContainer ppc = new PagePreviewContainer();
    ppc.add(_pagePreview);
    JPanel tbCont = new JPanel(new BorderLayout());
    JPanel cp = new JPanel(new BorderLayout());
    tbCont.add(_toolBar,BorderLayout.NORTH);
    tbCont.add(Box.createVerticalStrut(10),BorderLayout.SOUTH);
    tbCont.setBorder(new EmptyBorder(0,0,5,0));
    setContentPane(cp);
    cp.setBorder(new EmptyBorder(5,5,5,5));
    cp.add(tbCont, BorderLayout.NORTH);
    cp.add(ppc, BorderLayout.SOUTH);
    
    addWindowListener(_windowCloseListener);
    
    showPage();
    _updateActions();
    
    setSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    
    initDone(); 
    
    setVisible(true);
  }
  
  
  abstract protected void _print();
  
  
  abstract protected Pageable setUpDocument(SingleDisplayModel model, boolean interactions);
  
  private void _close() {
    dispose();
    _mainFrame.hourglassOff();
  }
  
  private void _nextPage() {
    _pageNumber++;
    _goToPage(_pageNumber);
  }
  
  private void _previousPage() {
    _pageNumber--;
    _goToPage(_pageNumber);
  }
  
  private void _goToPage(int pi) {
    _pageNumber = pi;
    showPage();
    _updateActions();
  }
  
  protected void _showError(Exception e, String title, String message) {
    JOptionPane.showMessageDialog(this, message + "\n" + e, title, JOptionPane.ERROR_MESSAGE);
  }
  
  
  private void _updateActions() {
    _nextPageAction.setEnabled(_print.getNumberOfPages() > (_pageNumber + 1));
    _prevPageAction.setEnabled(_pageNumber > 0);
    try { _pageChanger.update(_pageNumber + 1); }
    catch(Exception e) {  }
  }
  
  
  private void _setUpActions() {
    
    _closeAction.putValue(Action.SHORT_DESCRIPTION, "Close");
    
    _nextPageAction.putValue(Action.SMALL_ICON, _getIcon("Forward16.gif"));
    _nextPageAction.putValue(Action.SHORT_DESCRIPTION, "Next Page");
    _prevPageAction.putValue(Action.SMALL_ICON, _getIcon("Back16.gif"));
    _prevPageAction.putValue(Action.SHORT_DESCRIPTION, "Previous Page");
  }
  
  private PageChangerUpdater createPageChanger() {
    
    
    try {
      Class<?> spinnerClass = Class.forName("javax.swing.JSpinner");
      final JComponent spinner = (JComponent) spinnerClass.newInstance();
      final Method getter = spinnerClass.getMethod("getValue",new Class[0]);
      Object model = callMethod(spinner, spinnerClass, "getModel",null,null);
      Class<?> modelClass = model.getClass();
      Class<?>[] ca = new Class<?>[] {Comparable.class};
      Object[] aa = new Object[] {Integer.valueOf(1)};
      callMethod(model,modelClass,"setMinimum",ca,aa);
      aa[0] = Integer.valueOf(_print.getNumberOfPages());
      callMethod(model,modelClass,"setMaximum",ca,aa);
      ca[0] = ChangeListener.class;
      aa[0] = new ChangeListener() {
        public void stateChanged(ChangeEvent ev) {
          int num = _pageNumber;
          try {
            num = ((Number) getter.invoke(spinner,new Object[0])).intValue()-1;
            if ((num >= 0) && (num < _print.getNumberOfPages())) _goToPage(num);
            else _updateActions();
          }
          catch(IllegalAccessException ex) { _updateActions(); }
          catch(InvocationTargetException ex) { _updateActions(); }
        }
      };
      callMethod(spinner, spinnerClass,"addChangeListener",ca,aa);
      return new JSpinnerChanger(spinnerClass, spinner);
    } catch(Exception e) {
      
      final JTextField tf = new JTextField();
      tf.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          try {
            int pageToGoTo = Integer.parseInt(tf.getText()) - 1;
            if ((pageToGoTo < 0) || (pageToGoTo >= _print.getNumberOfPages())) { _updateActions(); } 
            else _goToPage(pageToGoTo); 
          } 
          catch (NumberFormatException e) { _updateActions(); }
        }
      });
      return new JTextFieldChanger(tf);
    }
  }
  
  private static Object callMethod(Object rec, Class<?> c, String name, Class<?>[] ca, Object[] args) throws Exception {
    Method m = c.getMethod(name,ca);
    return m.invoke(rec,args);
  }
  
  
  private ImageIcon _getIcon(String name) {
    URL url = PreviewFrame.class.getResource(ICON_PATH + name);
    if (url != null) return new ImageIcon(url);
    return null;
  }
  
  
  private void _setUpToolBar() {
    _toolBar.setFloatable(false);
    
    
    JButton printButton = new JButton("Print...",_getIcon("Print16.gif"));
    printButton.setToolTipText("Print this document");
    printButton.addActionListener(_printListener);
    _toolBar.add(printButton);
    _toolBar.addSeparator();
    _toolBar.add(_closeAction);
    
    
    _toolBar.add(Box.createHorizontalGlue());
    
    
    _toolBar.add(_prevPageAction);
    _toolBar.add(_nextPageAction);
    _toolBar.addSeparator();
    
    JLabel gotop = new JLabel("Page");
    
    JLabel of = new JLabel(" of " + _print.getNumberOfPages());
    
    _toolBar.add(gotop);
    _toolBar.addSeparator();
    JComponent c = _pageChanger.getComponent();
    Dimension d = c.getPreferredSize();
    d = new Dimension(100,d.height);
    c.setMaximumSize(d);
    c.setPreferredSize(d);
    c.setMinimumSize(d);
    c.setToolTipText("Goto Page");
    _toolBar.add(c);
    _toolBar.add(of);
  }
  
  
  private void showPage() {
    BufferedImage img = new BufferedImage((int) _model.getPageFormat().getWidth(),
                                          (int) _model.getPageFormat().getHeight(),
                                          BufferedImage.TYPE_INT_RGB);
    Graphics g = img.getGraphics();
    g.setColor(Color.white);
    g.fillRect(0, 0, (int) _model.getPageFormat().getWidth(), (int) _model.getPageFormat().getHeight());
    
    try {
      _print.getPrintable(_pageNumber).print(g, _model.getPageFormat(), _pageNumber);
      _pagePreview.setImage(img);
    } 
    catch (PrinterException e) {  }
  }
  
  
  class PagePreviewContainer extends JPanel {
    public Dimension getPreferredSize() { return getParent().getSize(); }
    
    
    public void doLayout() {
      Component cp = getComponent(0);
      
      Dimension dm = cp.getPreferredSize();
      int Hindent = (int) (getPreferredSize().getWidth() - dm.getWidth()) / 2;
      int Vindent = TOOLBAR_HEIGHT + (int) ((getPreferredSize().getHeight() - dm.getHeight() - TOOLBAR_HEIGHT) / 2);
      _pagePreview.setBounds(Hindent, Vindent, (int) dm.getWidth(), (int) dm.getHeight());
    }
  }
  
  
  static class PagePreview extends JPanel {
    protected final int _width;
    protected final int _height;
    protected volatile Image _source;
    protected volatile Image _image;
    
    
    public PagePreview(int width, int height) {
      super();
      _width = width;
      _height = height;
      setBorder(new MatteBorder(1, 1, 2, 2, Color.black));
      setBackground(Color.white);
    }
    
    
    protected void updateScaled() {
      _image = _source.getScaledInstance(_width, _height, Image.SCALE_SMOOTH);
      _image.flush();
    }
    
    
    public void setImage(Image i) {
      _source = i;
      updateScaled();
      repaint();
    }
    
    public Dimension getPreferredSize() { return new Dimension(_width, _height); }
    
    public Dimension getMaximumSize() { return getPreferredSize(); }
    
    public Dimension getMinimumSize() { return getPreferredSize(); }
    
    public void paint(Graphics g) {
      g.setColor(getBackground());
      g.fillRect(0, 0, _width, _height);
      g.drawImage(_image, 0, 0, this);
      paintBorder(g);
    }
  }
}