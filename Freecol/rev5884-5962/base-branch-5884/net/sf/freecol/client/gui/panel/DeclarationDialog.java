

package net.sf.freecol.client.gui.panel;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.FAFile;
import net.sf.freecol.common.resources.ResourceManager;
import net.sf.freecol.common.util.Utils;


public final class DeclarationDialog extends FreeColDialog<Boolean> {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(DeclarationDialog.class.getName());

    private final SignaturePanel signaturePanel;

    final DeclarationDialog theDialog = this;


    
    public DeclarationDialog(final Canvas parent) {
        super(parent);
        this.signaturePanel = new SignaturePanel();

        setLayout(null);

        Image image = ResourceManager.getImage("Declaration.image");
        setSize(image.getWidth(null), image.getHeight(null));
        setOpaque(false);
        setBorder(null);

        signaturePanel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ActionListener al = new ActionListener() {
                    public void actionPerformed(ActionEvent e2) {
                        theDialog.setResponse(Boolean.TRUE);
                    }
                };
                Timer t = new Timer(10000, al);
                t.setRepeats(false);
                t.start();
            }
        });
        add(signaturePanel);

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                theDialog.setResponse(Boolean.TRUE);
            }
        });
    }

    
    public void paintComponent(Graphics g) {
        Image image = ResourceManager.getImage("Declaration.image");
        g.drawImage(image, 0, 0, null);
    }

    
    public void initialize() {
        final int SIGNATURE_Y = 450;
        resetResponse();

        signaturePanel.initialize(getMyPlayer().getName());
        signaturePanel.setLocation((getWidth() - signaturePanel.getWidth()) / 2,
                (getHeight() + SIGNATURE_Y - signaturePanel.getHeight()) / 2 - 15);

        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                signaturePanel.startAnimation();
            }
        };
        Timer t = new Timer(3000, al);
        t.setRepeats(false);
        t.start();
    }


    
    private class SignaturePanel extends JPanel {

        private FAFile faFile;

        private ArrayList<ActionListener> actionListeners = new ArrayList<ActionListener>();

        private Point[] points = null;

        private int counter = 0;


        SignaturePanel() {
            faFile = (FAFile) UIManager.get("Declaration.signature.font");
            setOpaque(false);
        }

        
        private String getAbbreviatedName(String name) {
            if (!isTooLarge(name)) {
                return name;
            }

            String[] partNames = name.split(" ");

            
            for (int i = 1; i < partNames.length - 1 && isTooLarge(Utils.join(" ", partNames)); i++) {
                partNames[i] = partNames[i].charAt(0) + ".";
            }

            
            while (partNames.length > 2 && isTooLarge(Utils.join(" ", partNames))) {
                String[] newPartNames = new String[partNames.length - 1];
                newPartNames[0] = partNames[0];
                for (int i = 1; i < newPartNames.length; i++) {
                    newPartNames[i] = partNames[i + 1];
                }
                partNames = newPartNames;
            }

            if (!isTooLarge(Utils.join(" ", partNames))) {
                return Utils.join(" ", partNames);
            } else if (!isTooLarge(partNames[0].charAt(0) + ". " + partNames[1])) {
                return partNames[0].charAt(0) + ". " + partNames[1];
            } else if (!isTooLarge(partNames[0] + " " + partNames[1].charAt(0) + ".")) {
                return partNames[0] + " " + partNames[1].charAt(0) + ".";
            } else {
                return partNames[0].charAt(0) + ". " + partNames[1].charAt(0) + ".";
            }
        }

        
        private boolean isTooLarge(String name) {
            Dimension d = faFile.getDimension(name);
            return (d.width > theDialog.getWidth() - 10);
        }

        
        public void initialize(String name) {
            name = getAbbreviatedName(name);

            points = faFile.getPoints(name);
            counter = 0;
            setSize(faFile.getDimension(name));
        }

        
        public void addActionListener(ActionListener al) {
            if (!actionListeners.contains(al)) {
                actionListeners.add(al);
            }
        }

        
        public void removeActionListener(ActionListener al) {
            actionListeners.remove(al);
        }

        private void notifyStopped() {
            for (int i = 0; i < actionListeners.size(); i++) {
                actionListeners.get(i).actionPerformed(
                        new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "AnimationStopped"));
            }
        }

        
        public void startAnimation() {
            int delay = 50; 
            ActionListener taskPerformer = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    if (counter < points.length - 1) {
                        counter += 20;
                        if (counter > points.length) {
                            counter = points.length - 1;
                            ((Timer) evt.getSource()).stop();
                            notifyStopped();
                        }
                        repaint();
                    } else {
                        ((Timer) evt.getSource()).stop();
                        notifyStopped();
                    }
                }
            };
            new Timer(delay, taskPerformer).start();
        }

        
        public void paintComponent(Graphics g) {
            if (points == null || points.length == 0) {
                return;
            }
            if (isOpaque()) {
                super.paintComponent(g);
            }

            g.setColor(Color.BLACK);
            ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));

            for (int i = 0; i < counter; i++) {
                Point p = points[i];
                g.drawLine((int) p.getX(), (int) p.getY(), (int) p.getX(), (int) p.getY());
            }
        }
    }
}
