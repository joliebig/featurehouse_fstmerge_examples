

package net.sf.freecol.client.gui.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.SwingUtilities;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.control.InGameController;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.ImageLibrary;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.resources.ResourceManager;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Modifier;
import net.sf.freecol.common.model.Player;


public class FreeColPanel extends JPanel implements ActionListener {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(FreeColPanel.class.getName());

    protected static final String OK = "OK";

    public static final Insets emptyMargin = new Insets(0,0,0,0);

    private static final int cancelKeyCode = KeyEvent.VK_ESCAPE;

    
    private static final DecimalFormat modifierFormat = new DecimalFormat("0.00");

    
    private Canvas canvas = null;

    
    protected static final Font defaultFont = new Font("Dialog", Font.BOLD, 12);

    
    protected static final Font headerFont = ((Font) UIManager.get("HeaderFont")).deriveFont(0, 12);

    protected static final Font smallHeaderFont = ((Font) UIManager.get("HeaderFont")).deriveFont(0, 24);

    protected static final Font mediumHeaderFont = ((Font) UIManager.get("HeaderFont")).deriveFont(0, 36);

    protected static final Font bigHeaderFont = ((Font) UIManager.get("HeaderFont")).deriveFont(0, 48);

    
    protected static final int COLUMNS = 20;

    
    protected static final int margin = 3;

    
    protected static final Color LINK_COLOR = new Color(122, 109, 82);

    
    protected static final Color LIST_SELECT_COLOR = new Color(216, 194, 145);

    
    public static final Border TOPCELLBORDER =
        BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, LINK_COLOR),
                                           BorderFactory.createEmptyBorder(2, 2, 2, 2));
    public static final Border CELLBORDER = 
        BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, LINK_COLOR),
                                           BorderFactory.createEmptyBorder(2, 2, 2, 2));
    public static final Border LEFTCELLBORDER = 
        BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, LINK_COLOR),
                                           BorderFactory.createEmptyBorder(2, 2, 2, 2));
    public static final Border TOPLEFTCELLBORDER = 
        BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, LINK_COLOR),
                                           BorderFactory.createEmptyBorder(2, 2, 2, 2));

    protected boolean editable = true;

    protected JButton okButton = new JButton(Messages.message("ok"));


    
    public FreeColPanel(Canvas parent) {
        this(parent, new FlowLayout());
    }

    
    public FreeColPanel(Canvas parent, LayoutManager layout) {
        super(layout);

        this.canvas = parent;
        setFocusCycleRoot(true);

        Image menuborderN = ResourceManager.getImage("menuborder.n.image");
        Image menuborderNW = ResourceManager.getImage("menuborder.nw.image");
        Image menuborderNE = ResourceManager.getImage("menuborder.ne.image");
        Image menuborderW = ResourceManager.getImage("menuborder.w.image");
        Image menuborderE = ResourceManager.getImage("menuborder.e.image");
        Image menuborderS = ResourceManager.getImage("menuborder.s.image");
        Image menuborderSW = ResourceManager.getImage("menuborder.sw.image");
        Image menuborderSE = ResourceManager.getImage("menuborder.se.image");
        final FreeColImageBorder imageBorder = new FreeColImageBorder(menuborderN, menuborderW, menuborderS,
                menuborderE, menuborderNW, menuborderNE, menuborderSW, menuborderSE);
        setBorder(BorderFactory.createCompoundBorder(imageBorder,
                BorderFactory.createEmptyBorder(margin, margin,margin,margin)));

        
        
        addMouseListener(new MouseAdapter() {
        });

        okButton.setActionCommand(String.valueOf(OK));
        okButton.addActionListener(this);
        enterPressesWhenFocused(okButton);
        setCancelComponent(okButton);
    }

    
    public final Canvas getCanvas() {
        return canvas;
    }

    
    public ImageLibrary getLibrary() {
        return canvas.getGUI().getImageLibrary();
    }

    
    public FreeColClient getClient() {
        return canvas.getClient();
    }

    
    public Game getGame() {
        return canvas.getClient().getGame();
    }

    
    public InGameController getController() {
        return canvas.getClient().getInGameController();
    }

    
    public Player getMyPlayer() {
        return canvas.getClient().getMyPlayer();
    }

    
    public boolean isEditable() {
        return editable;
    }

    public void requestFocus() {
        okButton.requestFocus();
    }

    public static JTextPane getDefaultTextPane() {
        JTextPane textPane = new JTextPane();
        textPane.setOpaque(false);
        textPane.setEditable(false);

        StyledDocument doc = textPane.getStyledDocument();
        
        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        
        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "Dialog");
        StyleConstants.setBold(def, true);
        StyleConstants.setFontSize(def, 12);

        Style buttonStyle = doc.addStyle("button", regular);
        StyleConstants.setForeground(buttonStyle, LINK_COLOR);

        Style right = doc.addStyle("right", regular);
        StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);

        return textPane;
    }


    
    public static JTextArea getDefaultTextArea(String text) {
        return getDefaultTextArea(text, COLUMNS);
    }
    
    public static JTextArea getDefaultTextArea(String text, int columns) {
        JTextArea textArea = new JTextArea(text);
        textArea.setColumns(columns);
        textArea.setOpaque(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFocusable(false);
        textArea.setFont(defaultFont);
        
        textArea.setSize(textArea.getPreferredSize());
        return textArea;
    }

    public static JButton getLinkButton(String text, Icon icon, String action) {
        JButton button = new JButton(text, icon);
        button.setMargin(emptyMargin);
        button.setOpaque(false);
        button.setForeground(LINK_COLOR);
        button.setAlignmentY(0.8f);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setActionCommand(action);
        return button;
    }


    
    public static JLabel getDefaultHeader(String text) {
        JLabel header = new JLabel(text, JLabel.CENTER);
        header.setFont(bigHeaderFont);
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        return header;
    }

    public void setCancelComponent(AbstractButton cancelButton) {
        if (cancelButton == null) {
            throw new NullPointerException();
        }
        
        InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke(cancelKeyCode, 0, true), "release");
                
        Action cancelAction = cancelButton.getAction();
        getActionMap().put("release", cancelAction);
    }

    
    public static void enterPressesWhenFocused(JButton button) {
        button.registerKeyboardAction(
                button.getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false)), KeyStroke
                        .getKeyStroke(KeyEvent.VK_ENTER, 0, false), JComponent.WHEN_FOCUSED);

        button.registerKeyboardAction(button.getActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true)),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), JComponent.WHEN_FOCUSED);
    }

    public static final DecimalFormat getModifierFormat() {
        return modifierFormat;
    }

    public Set<Modifier> sortModifiers(Set<Modifier> result) {
        EnumMap<Modifier.Type, List<Modifier>> modifierMap =
            new EnumMap<Modifier.Type, List<Modifier>>(Modifier.Type.class);
        for (Modifier.Type type : Modifier.Type.values()) {
            modifierMap.put(type, new ArrayList<Modifier>());
        }
        for (Modifier modifier : result) {
            modifierMap.get(modifier.getType()).add(modifier);
        }
        Set<Modifier> sortedResult = new LinkedHashSet<Modifier>();
        for (Modifier.Type type : Modifier.Type.values()) {
            sortedResult.addAll(modifierMap.get(type));
        }
        return sortedResult;
    }

    
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (OK.equals(command)) {
            getCanvas().remove(this);
        }
    }

    
    public static MouseListener createEventForwardingMouseListener(final Component c) {
        final MouseListener ml = new MouseListener() {
            private void forward(MouseEvent e) {
                c.dispatchEvent(javax.swing.SwingUtilities.convertMouseEvent(e.getComponent(), e, c));
            }

            public void mouseClicked(MouseEvent e) {
                forward(e);
            }
            public void mouseEntered(MouseEvent e) {
                forward(e);
            }
            public void mouseExited(MouseEvent e) {
                forward(e);
            }
            public void mousePressed(MouseEvent e) {
                forward(e);
            }
            public void mouseReleased(MouseEvent e) {
                forward(e);
            }
        };
        return ml;
    }

    
    public static MouseMotionListener createEventForwardingMouseMotionListener(final Component c) {
        final MouseMotionListener ml = new MouseMotionListener() {
            private void forward(MouseEvent e) {
                c.dispatchEvent(javax.swing.SwingUtilities.convertMouseEvent(e.getComponent(), e, c));
            }

            public void mouseDragged(MouseEvent e) {
                forward(e);
            }
            public void mouseMoved(MouseEvent e) {
                forward(e);
            }
        };
        return ml;
    }
}
