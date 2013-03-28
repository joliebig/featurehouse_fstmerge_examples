

package net.sf.freecol.client.gui.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.FoundingFather;
import net.sf.freecol.common.model.FoundingFather.FoundingFatherType;
import net.sf.freecol.common.resources.ResourceManager;


public final class ChooseFoundingFatherDialog extends FreeColDialog<FoundingFather> implements ActionListener {

    private static final Logger logger = Logger.getLogger(ChooseFoundingFatherDialog.class.getName());

    private final JTabbedPane tb;


    private final List<FoundingFather> possibleFathers;

    
    public ChooseFoundingFatherDialog(Canvas parent, List<FoundingFather> possibleFoundingFathers) {
        super(parent);
        possibleFathers = possibleFoundingFathers;
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setLayout(new BorderLayout());
        setOpaque(false);

        setFocusCycleRoot(false);

        tb = new JTabbedPane(JTabbedPane.TOP);

        boolean hasSelectedTab = false;
        for (int index = 0; index < possibleFoundingFathers.size(); index++) {
            FoundingFather father = possibleFoundingFathers.get(index);
            final FoundingFatherPanel panel = new FoundingFatherPanel(father.getType());
            panel.initialize(father);
            tb.addTab(Messages.message(father.getTypeKey()), null, panel, null);
            if (!hasSelectedTab && panel.isEnabled()) {
                tb.setSelectedIndex(index);
                hasSelectedTab = true;
            }
            panel.addMouseListener(FreeColPanel.createEventForwardingMouseListener(ChooseFoundingFatherDialog.this));
            panel.addMouseMotionListener(FreeColPanel.createEventForwardingMouseMotionListener(ChooseFoundingFatherDialog.this));
        }
        add(tb, BorderLayout.CENTER);
        setSize(tb.getPreferredSize());
    }

    public void requestFocus() {
        tb.requestFocus();
    }

    
    public void actionPerformed(ActionEvent event) {
        String id = event.getActionCommand();
        for (FoundingFather father : possibleFathers) {
            if (father.getId().equals(id)) {
                setResponse(father);
                return;
            }
        }
        setResponse(null);
    }


    
    protected class FoundingFatherPanel extends JPanel {

        private FoundingFather foundingFather = null;

        private JLabel header;

        private JTextArea description;

        private JTextArea text;

        private JButton ok;

        
        public FoundingFatherPanel(FoundingFatherType type) {

            setLayout(new BorderLayout());

            
            header = getDefaultHeader("");
            add(header, BorderLayout.NORTH);

            
            JPanel contentPanel = createContentPanel(type);
            add(contentPanel, BorderLayout.CENTER);

            
            JPanel p3 = new JPanel(new BorderLayout());
            p3.setOpaque(false);
            p3.setBorder(new EmptyBorder(0, 160, 20, 160));
            ok = new JButton(Messages.message("chooseThisFoundingFather"));
            ok.addActionListener(ChooseFoundingFatherDialog.this);
            ok.setSize(ok.getPreferredSize());
            enterPressesWhenFocused(ok);
            p3.add(ok, BorderLayout.CENTER);
            add(p3, BorderLayout.SOUTH);
        }
        
        private JPanel createContentPanel(FoundingFatherType type) {
            
            JPanel p1 = new JPanel();
            p1.setLayout(new BorderLayout(20, 20));
            p1.setOpaque(false);
            p1.setBorder(new EmptyBorder(20, 20, 20, 20));

            
            Image image = ResourceManager.getImage("FoundingFather." + type.toString().toLowerCase());
            JLabel imageLabel;
            if (image != null) {
                imageLabel = new JLabel(new ImageIcon(image));
            } else {
                imageLabel = new JLabel();
            }
            JPanel imagePanel = new JPanel(new BorderLayout());
            imagePanel.add(imageLabel, BorderLayout.NORTH);
            p1.add(imagePanel, BorderLayout.WEST);

            
            JPanel p2 = new JPanel(new BorderLayout());
            p2.setOpaque(false);
            
            JScrollPane scrollPane = new JScrollPane(p2,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.getViewport().setOpaque(false);
            scrollPane.getVerticalScrollBar().setUnitIncrement( 16 );
            scrollPane.setOpaque(false);
            scrollPane.setBorder(null);
            p1.add(scrollPane, BorderLayout.CENTER);
            
            this.setPreferredSize(new Dimension(570, 400));

            description = new JTextArea();
            description.setBorder(null);
            description.setOpaque(false);
            description.setLineWrap(true);
            description.setEditable(false);
            description.setWrapStyleWord(true);
            description.setFocusable(false);
            p2.add(description, BorderLayout.NORTH);

            text = new JTextArea();
            text.setBorder(null);
            text.setOpaque(false);
            text.setLineWrap(true);
            text.setEditable(false);
            text.setWrapStyleWord(true);
            text.setFocusable(false);
            p2.add(text, BorderLayout.CENTER);
            
            return p1;
        }

        public void requestFocus() {
            ok.requestFocus();
        }

        
        public void initialize(FoundingFather father) {
            this.foundingFather = father;

            if (father != null) {
                header.setText(Messages.message(father.getNameKey()));
                description.setText(Messages.message(father.getDescriptionKey()));
                text.setText("\n" + "[" + Messages.message(father.getId() + ".birthAndDeath")
                             + "] " + Messages.message(father.getId() + ".text"));
                ok.setActionCommand(father.getId());
            }
        }

        public boolean isEnabled() {
            return (foundingFather != null);
        }
    }
}
