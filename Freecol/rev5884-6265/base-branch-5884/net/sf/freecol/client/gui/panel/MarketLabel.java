


package net.sf.freecol.client.gui.panel;


import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.Market;
import net.sf.freecol.common.model.Player;



public final class MarketLabel extends JLabel implements ActionListener {

    private static Logger logger = Logger.getLogger(MarketLabel.class.getName());

    private final GoodsType type;
    private int amount;
    private final Market market;
    private final Canvas parent;
    private boolean partialChosen;
    private boolean toEquip;

    
    public MarketLabel(GoodsType type, Market market, Canvas parent) {
        super(parent.getGUI().getImageLibrary().getGoodsImageIcon(type));
        
        this.type = type;
        
        if (market == null) {
            throw new NullPointerException();
        }

        this.market = market;
        this.parent = parent;
        partialChosen = false;
        amount = 100;
    }


    
    public MarketLabel(GoodsType type, Market market, Canvas parent, boolean isSmall) {
        this(type, market, parent);
        setSmall(true);
    }


    public boolean isPartialChosen() {
        return partialChosen;
    }
    
    
    public void setPartialChosen(boolean partialChosen) {
        this.partialChosen = partialChosen;
    }
    
    public boolean isToEquip() {
        return toEquip;
    }
    
    public void toEquip(boolean toEquip) {
        this.toEquip = toEquip;
    }


    
    public GoodsType getType() {
        return type;
    }

    
    public int getAmount() {
        return amount;
    }

    
    public void setAmount(int amount) {
        this.amount = amount;
    }
    

    
    public Market getMarket() {
        return market;
    }

    
    public void setSmall(boolean isSmall) {
        if (isSmall) {
            ImageIcon imageIcon = parent.getGUI().getImageLibrary().getGoodsImageIcon(type);
            setIcon(new ImageIcon(imageIcon.getImage().getScaledInstance(imageIcon.getIconWidth() / 2, imageIcon.getIconHeight() / 2, Image.SCALE_DEFAULT)));
        } else {
            setIcon(parent.getGUI().getImageLibrary().getGoodsImageIcon(type));
        }
    }


    
    public void paintComponent(Graphics g) {

        Player player = market.getGame().getViewOwner();
        String toolTipText = type.getName();
        if (player == null || player.canTrade(type)) {
            setEnabled(true);
        } else {
            toolTipText = type.getName(false);
            setEnabled(false);
        }
        if (FreeCol.isInDebugMode()) {
            toolTipText += " " + market.getMarketData(type).getAmountInMarket();
        }
        setToolTipText(toolTipText);

        super.setText(Integer.toString(market.paidForSale(type)) + "/" + Integer.toString(market.costToBuy(type)));
        super.paintComponent(g);
    }

    
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        try {
                switch (Integer.valueOf(command).intValue()) {
                    default:
                        logger.warning("Invalid action");
                }
                setIcon(parent.getGUI().getImageLibrary().getGoodsImageIcon(type));
                repaint(0, 0, getWidth(), getHeight());
                
                
                
        }
        catch (NumberFormatException e) {
            logger.warning("Invalid action number");
        }
    }
}

