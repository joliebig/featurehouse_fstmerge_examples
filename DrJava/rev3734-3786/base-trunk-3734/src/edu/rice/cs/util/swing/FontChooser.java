

package edu.rice.cs.util.swing;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;



public class FontChooser extends JDialog {
  
  private static final String[] STYLES =
      new String[] { "Plain", "Bold", "Italic", "Bold Italic" };

  
  private static final String[] SIZES =
      new String[] { "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
                     "13", "14", "15", "16", "17", "18", "19", "20", "22",
                     "24", "27", "30", "34", "39", "45", "51", "60"};

  
  private NwList _styleList;
  private NwList _fontList;
  private NwList _sizeList;

  
  private JButton _okButton;
  private JButton _cancelButton;
  private JLabel _sampleText = new JLabel();

  private boolean _clickedOK = false;

  
  private FontChooser(Frame parent, Font font) {
    super(parent, true);
    initAll();
    if (font == null) font = _sampleText.getFont();
    _fontList.setSelectedItem(font.getName());
    _sizeList.setSelectedItem(font.getSize() + "");
    _styleList.setSelectedItem(STYLES[font.getStyle()]);
    
    resize();
  }

  
  public static Font showDialog(Frame parent, String title, Font font) {
    FontChooser fd = new FontChooser(parent, font);
    fd.setTitle(title);
    fd.setVisible(true);
    Font chosenFont = null;
    if (fd.clickedOK()) {
      chosenFont = fd.getFont();
    }
    fd.dispose();
    return (chosenFont);
  }

  
  public static Font showDialog(Frame parent, Font font) {
    return showDialog(parent, "Font Chooser", font);
  }

  private void initAll() {
    getContentPane().setLayout(null);
    setBounds(50, 50, 425, 400);
    _sampleText = new JLabel();
    addLists();
    addButtons();
    _sampleText.setForeground(Color.black);
    getContentPane().add(_sampleText);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent e) {
        setVisible(false);
      }
    });
    addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent evt) {
        resize();
      }
    });
  }

  private void resize() {
    int w = getWidth();
    int h = getHeight();
    double wf = (double) w / 425;
    int w2 = (int) (80 * wf);
    int w3 = (int) (50 * wf);
    if (w3 < 30) w3 = 30;
    int w1 = w - w2 - w3 - 25;
    _fontList.setBounds(5, 5, w1, h - 91);
    _styleList.setBounds(w1 + 10, 5, w2, h - 91);
    _sizeList.setBounds(w1 + w2 + 15, 5, w3, h - 91);
    _sampleText.setBounds(10, h - 78, w - 20, 45);
    _okButton.setBounds(w - 165, h - 55, 70, 20);
    _cancelButton.setBounds(w - 81, h - 55, 70, 20);
    validate();
  }

  private void addLists() {
    _fontList = new NwList(GraphicsEnvironment.getLocalGraphicsEnvironment()
                           .getAvailableFontFamilyNames());
    _styleList = new NwList(STYLES);
    _sizeList = new NwList(SIZES);
    getContentPane().add(_fontList);
    getContentPane().add(_styleList);
    getContentPane().add(_sizeList);
  }

  private void addButtons() {
    _okButton = new JButton("OK");
    _okButton.setMargin(new Insets(0, 0, 0, 0));
    _cancelButton = new JButton("Cancel");
    _cancelButton.setMargin(new Insets(0, 0, 0, 0));
    _okButton.setFont(new Font(" ", 1, 11));
    _cancelButton.setFont(new Font(" ", 1, 12));
    getContentPane().add(_okButton);
    getContentPane().add(_cancelButton);
    _okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
        _clickedOK = true;
      }
    });
    _cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
        _clickedOK = false;
      }
    });
  }

  private void showSample() {
    int g = 0;
    try {
      g = Integer.parseInt(_sizeList.getSelectedValue());
    }
    catch (NumberFormatException nfe) {
    }
    String st = _styleList.getSelectedValue();
    int s = Font.PLAIN;
    if (st.equalsIgnoreCase("Bold")) s = Font.BOLD;
    if (st.equalsIgnoreCase("Italic")) s = Font.ITALIC;
    if (st.equalsIgnoreCase("Bold Italic")) s = Font.BOLD | Font.ITALIC;
    _sampleText.setFont(new Font(_fontList.getSelectedValue(), s, g));
    _sampleText.setText("The quick brown fox jumped over the lazy dog.");
    _sampleText.setVerticalAlignment(SwingConstants.TOP);
  }

  
  public boolean clickedOK() {
    return _clickedOK;
  }

  
  public Font getFont() {
    return _sampleText.getFont();
  }

  
  public class NwList extends JPanel {
    JList jl;
    JScrollPane sp;
    JLabel jt;
    String si = " ";

    public NwList(String[] values) {
      setLayout(null);
      jl = new JList(values);
      sp = new JScrollPane(jl);
      jt = new JLabel();
      jt.setBackground(Color.white);
      jt.setForeground(Color.black);
      jt.setOpaque(true);
      jt.setBorder(new JTextField().getBorder());
      jt.setFont(getFont());
      jl.addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          jt.setText((String) jl.getSelectedValue());
          si = (String) jl.getSelectedValue();
          showSample();
        }
      });
      add(sp);
      add(jt);
    }

    public void setBounds(int x, int y, int w, int h) {
      super.setBounds(x, y, w, h);
      sp.setBounds(0, y + 16, w, h - 23);
      sp.revalidate();
      jt.setBounds(0, 0, w, 20);
    }

    public String getSelectedValue() {
      return (si);
    }

    public void setSelectedItem(String s) {
      jl.setSelectedValue(s, true);
    }

  }
}