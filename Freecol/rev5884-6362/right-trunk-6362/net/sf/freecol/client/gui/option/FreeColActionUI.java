


package net.sf.freecol.client.gui.option;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import net.sf.freecol.client.gui.action.FreeColAction;
import net.sf.freecol.common.option.Option;



public final class FreeColActionUI extends JPanel implements OptionUpdater, ActionListener {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(FreeColActionUI.class.getName());


    private final FreeColAction option;
    private final OptionGroupUI optionGroupUI;
    private KeyStroke keyStroke;
    private JButton recordButton;
    private JButton removeButton;
    private BlinkingLabel bl;


    
    public FreeColActionUI(FreeColAction option, OptionGroupUI optionGroupUI) {
        super(new BorderLayout());

        this.option = option;
        this.optionGroupUI = optionGroupUI;
        keyStroke = option.getAccelerator();

        String name = option.getName();
        String description = option.getShortDescription();
        JLabel label = new JLabel(name, JLabel.LEFT);
        label.setToolTipText((description != null) ? description : name);
        add(label, BorderLayout.CENTER);

        JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bl = new BlinkingLabel();
        p1.add(bl);

        recordButton = new JButton(getRecordImage());
        recordButton.addActionListener(this);
        p1.add(recordButton);
        
        removeButton = new JButton(getRemoveImage());
        removeButton.addActionListener(this);
        p1.add(removeButton);
        
        add(p1, BorderLayout.EAST);

        setOpaque(false);
    }

    
    
    public void rollback() {
        
    }
    
    
    public void unregister() {
        
    }
    
    
    public static ImageIcon getRecordImage() {
        BufferedImage bi = new BufferedImage(9, 9, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setColor(Color.RED);
        g.fillOval(0, 0, 9, 9);
        g.setColor(Color.BLACK);
        g.drawOval(0, 0, 9, 9);

        return new ImageIcon(bi);
    }
    
    
    
    public static ImageIcon getRemoveImage() {
        BufferedImage bi = new BufferedImage(9, 9, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        
        g.setColor(Color.BLACK);
        g.drawLine(1, 0, 8, 7);
        g.drawLine(0, 1, 7, 8);
        g.drawLine(7, 0, 0, 7);
        g.drawLine(9, 0, 0, 9);
        g.setColor(Color.RED);
        g.drawLine(0, 0, 8, 8);
        g.drawLine(8, 0, 0, 8);

        return new ImageIcon(bi);
    }
    
    
    public static String getHumanKeyStrokeText(KeyStroke keyStroke) {
        if (keyStroke == null) {
            return " ";
        }

        String s = KeyEvent.getKeyModifiersText(keyStroke.getModifiers());
        if (!s.equals("")) {
            s += "+";
        }
        return s + KeyEvent.getKeyText(keyStroke.getKeyCode());
    }

    
    
    public void removeKeyStroke(KeyStroke k) {
        if (k != null && keyStroke != null && k.getKeyCode() == keyStroke.getKeyCode() && k.getModifiers() == keyStroke.getModifiers()) {
            keyStroke = null;
            bl.setText(" ");
        }
    }


    
    public void updateOption() {
        option.setAccelerator(keyStroke);
    }
    
    
    public void reset() {
        keyStroke = option.getAccelerator();
        bl.setText(getHumanKeyStrokeText(keyStroke));
    }

    
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == recordButton) {
            bl.startBlinking();
            bl.requestFocus();
        } else if (evt.getSource() == removeButton) {
            bl.stopBlinking();
            bl.setText(" ");
            keyStroke = null;
        }
    }



    
    class BlinkingLabel extends JLabel implements ActionListener, KeyListener, MouseListener {

        private Timer blinkingTimer = new Timer(500, this);
        private boolean blinkOn = false;

        BlinkingLabel() {
            super(getHumanKeyStrokeText(keyStroke), JLabel.CENTER);

            setOpaque(false);
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            addKeyListener(this);
            addMouseListener(this);
        }

        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() > 1) {
                startBlinking();
                requestFocus();
            }
        }


        public void mouseEntered(MouseEvent e) {  }
        public void mouseExited(MouseEvent e) {  }
        public void mousePressed(MouseEvent e) {  }
        public void mouseReleased(MouseEvent e) {  }


        public Dimension getMinimumSize() {
            return new Dimension(80, super.getMinimumSize().height);
        }

        public Dimension getPreferredSize() {
            return getMinimumSize();
        }


        public void startBlinking() {
            blinkingTimer.start();
        }


        public void stopBlinking() {
            blinkingTimer.stop();
            setOpaque(false);
            repaint();
        }

        public void actionPerformed(ActionEvent evt) {
            if (!hasFocus()) {
                stopBlinking();
            }

            if (blinkOn) {
                setOpaque(false);
                blinkOn = false;
                repaint();
            } else {
                setOpaque(true);
                setBackground(Color.RED);
                blinkOn = true;
                repaint();
            }
        }


        public void keyPressed(KeyEvent e) {  }

        public void keyTyped(KeyEvent e) {  }

        public void keyReleased(KeyEvent e) {
            KeyStroke ks = KeyStroke.getKeyStroke(e.getKeyCode(), e.getModifiers());
            optionGroupUI.removeKeyStroke(ks);
            keyStroke = ks;
            
            stopBlinking();
            setText(getHumanKeyStrokeText(keyStroke));
            recordButton.requestFocus();
        }
    }
}
