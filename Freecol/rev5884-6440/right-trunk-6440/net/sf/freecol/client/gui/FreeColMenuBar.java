

package net.sf.freecol.client.gui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.action.ActionManager;
import net.sf.freecol.client.gui.action.FreeColAction;
import net.sf.freecol.client.gui.action.SelectableAction;
import net.sf.freecol.client.gui.panel.FreeColImageBorder;
import net.sf.freecol.common.resources.ResourceManager;


public abstract class FreeColMenuBar extends JMenuBar {

    private static final Logger logger = Logger.getLogger(FreeColMenuBar.class.getName());

    public static final int UNIT_ORDER_WAIT = 0;

    public static final int UNIT_ORDER_FORTIFY = 1;

    public static final int UNIT_ORDER_SENTRY = 2;

    public static final int UNIT_ORDER_CLEAR_ORDERS = 3;

    public static final int UNIT_ORDER_BUILD_COL = 5;

    public static final int UNIT_ORDER_PLOW = 6;

    public static final int UNIT_ORDER_BUILD_ROAD = 7;

    public static final int UNIT_ORDER_SKIP = 9;

    public static final int UNIT_ORDER_DISBAND = 11;

    protected final FreeColClient freeColClient;

    JMenuItem reportsTradeMenuItem = null;

    protected ActionManager am;


    
    protected FreeColMenuBar(FreeColClient f) {

        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        

        super();

        setOpaque(false);

        this.freeColClient = f;

        this.am = f.getActionManager();

        setBorder(FreeColImageBorder.imageBorder);
    }

    
    
    public abstract void reset();
    
    
    protected JMenuItem getMenuItem(String actionID) {
        JMenuItem rtn = null;

        FreeColAction action = am.getFreeColAction(actionID);

        if (action != null) {
            rtn = new JMenuItem();
            rtn.setAction(action);
            rtn.setOpaque(false);

            if (action.getMnemonic() != FreeColAction.NO_MNEMONIC) {
                rtn.addMenuKeyListener(action.getMenuKeyListener());
            }
        } else {
            logger.finest("Could not create menu item. [" + actionID + "] not found.");
        }
        return rtn;
    }

    
    protected JMenuItem getMenuItem(String actionID, ActionListener actionListener) {
        JMenuItem rtn = getMenuItem(actionID);

        rtn.addActionListener(actionListener);

        return rtn;
    }

    
    protected JCheckBoxMenuItem getCheckBoxMenuItem(String actionID) {

        JCheckBoxMenuItem rtn = null;
        FreeColAction action = am.getFreeColAction(actionID);

        if (action != null) {
            rtn = new JCheckBoxMenuItem();
            rtn.setAction(action);
            rtn.setOpaque(false);

            rtn.setSelected(((SelectableAction) am.getFreeColAction(actionID)).isSelected());
        } else
            logger.finest("Could not create menu item. [" + actionID + "] not found.");

        return rtn;
    }

    
    protected JRadioButtonMenuItem getRadioButtonMenuItem(String actionID,
                                                          ButtonGroup group) {
        JRadioButtonMenuItem rtn = null;
        FreeColAction action = am.getFreeColAction(actionID);

        if (action != null) {
            rtn = new JRadioButtonMenuItem();
            rtn.setAction(action);
            rtn.setOpaque(false);

            rtn.setSelected(((SelectableAction) am.getFreeColAction(actionID)).isSelected());
            group.add(rtn);
        } else {
            logger.finest("Could not create menu item. [" + actionID + "] not found.");
        }
        return rtn;
    }


    
    public void update() {
        repaint();
    }

    
    public void setEnabled(boolean enabled) {
        

        update();
    }

    
    public void paintComponent(Graphics g) {
        if (isOpaque()) {
            super.paintComponent(g);
        } else {
            Insets insets = getInsets();
            int width = getWidth() - insets.left - insets.right;
            int height = getHeight() - insets.top - insets.bottom;

            Image tempImage = ResourceManager.getImage("BackgroundImage");

            if (tempImage != null) {
                for (int x = 0; x < width; x += tempImage.getWidth(null)) {
                    for (int y = 0; y < height; y += tempImage.getHeight(null)) {
                        g.drawImage(tempImage, insets.left + x, insets.top + y, null);
                    }
                }
            } else {
                g.setColor(getBackground());
                g.fillRect(insets.left, insets.top, width, height);
            }
        }
    }    
}
